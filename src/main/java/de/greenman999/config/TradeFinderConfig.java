package de.greenman999.config;

import com.google.gson.*;
import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import de.greenman999.gui.handler.ResetLecternModeHandler;
import de.greenman999.gui.handler.SlowModeHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeFinderConfig {
    public static final TradeFinderConfig INSTANCE = new TradeFinderConfig();

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("librarian-trade-finder.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public boolean preventAxeBreaking = true;
    public boolean tpToVillager = false;
    public boolean legitMode = true;
    public boolean slowMode = false;

    public boolean resetLecternMode = false;

    public boolean autoTradeMode = false;

    public HashMap<Enchantment, EnchantmentOption> enchantments = new HashMap<>();

    public void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("configVersion", 1);
            json.addProperty("preventAxeBreaking", preventAxeBreaking);
            json.addProperty("tpToVillager", tpToVillager);
            json.addProperty("legitMode", legitMode);
            JsonObject slowMode_elements = new JsonObject();
            slowMode_elements.addProperty("enabled", slowMode);
            slowMode_elements.addProperty("placeDelay", Integer.parseInt(SlowModeHandler.placeDelay.getText()));
            slowMode_elements.addProperty("interactDelay", Integer.parseInt(SlowModeHandler.interactDelay.getText()));
            json.add("slowMode", slowMode_elements);
            JsonObject resetLecternMode_elements = new JsonObject();
            resetLecternMode_elements.addProperty("enabled", resetLecternMode);
            resetLecternMode_elements.addProperty("resetDelay", Integer.parseInt(ResetLecternModeHandler.resetDelay.getText()));
            //TODO: add aim delay.
            json.add("resetLecternMode", resetLecternMode_elements);
            json.addProperty("autoTradeMode", autoTradeMode);
            JsonObject enchantmentsJson = new JsonObject();
            enchantments.forEach((enchantment, enchantmentOption) -> enchantmentsJson.add(Registries.ENCHANTMENT.getEntry(enchantment).getKey().orElseThrow().getValue().toString(), enchantmentOption.toJson()));
            json.add("enchantments", enchantmentsJson);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            LibrarianTradeFinder.LOGGER.error("Failed to save config file", e);
        }
    }

    public void load() {
        try {
            if(!Files.exists(configFile)) {
                Files.createFile(configFile);
                Files.writeString(configFile, "{}");
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            if(!(!json.has("configVersion") || json.get("configVersion").getAsInt() != 1)) {
                if (json.has("preventAxeBreaking"))
                    preventAxeBreaking = json.getAsJsonPrimitive("preventAxeBreaking").getAsBoolean();
                if (json.has("tpToVillager"))
                    tpToVillager = json.getAsJsonPrimitive("tpToVillager").getAsBoolean();
                if (json.has("legitMode"))
                    legitMode = json.getAsJsonPrimitive("legitMode").getAsBoolean();
                if (json.has("slowMode")) {
                    if (json.get("slowMode").isJsonObject()) {
                        JsonObject slowMode_elements = json.getAsJsonObject("slowMode");

                        if (slowMode_elements.has("enabled"))
                            slowMode = slowMode_elements.getAsJsonPrimitive("enabled").getAsBoolean();
                        if (slowMode_elements.has("placeDelay")) {
                            int placeDelay = slowMode_elements.getAsJsonPrimitive("placeDelay").getAsInt();

                            if (placeDelay >= SlowModeHandler.placeDelay.minValue && placeDelay <= SlowModeHandler.placeDelay.maxValue) {
                                TradeFinder.placeDelay = placeDelay;
                                SlowModeHandler.placeDelay.setText(String.valueOf(placeDelay));
                                SlowModeHandler.placeDelay.setLastText(String.valueOf(placeDelay));
                            }
                        }

                        if (slowMode_elements.has("interactDelay")) {
                            int interactDelay = slowMode_elements.getAsJsonPrimitive("interactDelay").getAsInt();

                            if (interactDelay >= SlowModeHandler.interactDelay.minValue && interactDelay <= SlowModeHandler.interactDelay.maxValue) {
                                TradeFinder.interactDelay = interactDelay;
                                SlowModeHandler.interactDelay.setText(String.valueOf(interactDelay));
                                SlowModeHandler.interactDelay.setLastText(String.valueOf(interactDelay));
                            }
                        }
                    }
                }

                if (json.has("resetLecternMode")) {
                    if (json.get("resetLecternMode").isJsonObject()) {
                        JsonObject resetLecternMode_elements = json.getAsJsonObject("resetLecternMode");

                        if (resetLecternMode_elements.has("enabled"))
                            resetLecternMode = resetLecternMode_elements.getAsJsonPrimitive("enabled").getAsBoolean();
                        if (resetLecternMode_elements.has("resetDelay")) {
                            int resetDelay = resetLecternMode_elements.getAsJsonPrimitive("resetDelay").getAsInt();

                            if (resetDelay >= ResetLecternModeHandler.resetDelay.minValue && resetDelay < ResetLecternModeHandler.resetDelay.maxValue) {
                                TradeFinder.resetDelay = resetDelay;
                                ResetLecternModeHandler.resetDelay.setText(String.valueOf(resetDelay));
                                ResetLecternModeHandler.resetDelay.setLastText(String.valueOf(resetDelay));
                            }
                        }

                        if (resetLecternMode_elements.has("aimDelay")) {
                            int aimDelay = resetLecternMode_elements.getAsJsonPrimitive("aimDelay").getAsInt();

                            //TODO: add aimDelay load.
                        }
                    }
                }

                if (json.has("autoTradeMode"))
                    autoTradeMode = json.getAsJsonPrimitive("autoTradeMode").getAsBoolean();
                if (json.has("enchantments")) {
                    JsonObject enchantmentsJson = json.getAsJsonObject("enchantments");
                    enchantmentsJson.entrySet().forEach(entry -> {
                        RegistryKey<Enchantment> enchantmentKey = RegistryKey.of(Registries.ENCHANTMENT.getKey(), Identifier.tryParse(entry.getKey()));
                        Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentKey);
                        if (enchantment != null) {
                            enchantments.put(enchantment, EnchantmentOption.fromJson(entry.getValue().getAsJsonObject()));
                        }
                    });
                }
            }

            for(Enchantment enchantment : Registries.ENCHANTMENT) {
                if(!enchantments.containsKey(enchantment)) {
                    if(!enchantment.isAvailableForEnchantedBookOffer()) continue;
                    enchantments.put(enchantment, new EnchantmentOption(enchantment, false));
                }
            }
            sortEnchantmentsMap();

            save();
        } catch (IOException e) {
            LibrarianTradeFinder.LOGGER.error("Failed to load config file", e);
        }
    }

    private void sortEnchantmentsMap() {
        enchantments = enchantments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(enchantment -> enchantment.getName(enchantment.getMaxLevel()).copy().formatted(Formatting.WHITE).getString())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static class EnchantmentOption {

        public Enchantment enchantment;
        public boolean enabled;
        public int level;
        public int maxPrice;

        EnchantmentOption(Enchantment enchantment, boolean enabled, int level, int maxPrice) {
            this.enchantment = enchantment;
            this.enabled = enabled;
            this.level = level;
            this.maxPrice = maxPrice;
        }

        public EnchantmentOption(Enchantment enchantment, boolean enabled) {
            this(enchantment, enabled, enchantment.getMaxLevel(), 64);
        }

        public static EnchantmentOption fromJson(JsonObject json) {
            RegistryKey<Enchantment> enchantmentKey = RegistryKey.of(Registries.ENCHANTMENT.getKey(), Identifier.tryParse(json.getAsJsonPrimitive("enchantment").getAsString()));
            Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentKey);
            if (enchantment == null) return null;
            return new EnchantmentOption(enchantment, json.getAsJsonPrimitive("enabled").getAsBoolean(), json.getAsJsonPrimitive("level").getAsInt(), json.getAsJsonPrimitive("maxPrice").getAsInt());
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", Registries.ENCHANTMENT.getEntry(enchantment).getKey().orElseThrow().getValue().toString());
            json.addProperty("enabled", enabled);
            json.addProperty("level", level);
            json.addProperty("maxPrice", maxPrice);
            return json;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setMaxPrice(int maxPrice) {
            this.maxPrice = maxPrice;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getLevel() {
            return level;
        }

        public int getMaxPrice() {
            return maxPrice;
        }

        /*
        public String getName() {
            return enchantment.getName(level).copy().formatted(Formatting.WHITE).getString();
        }
         */
    }

}
