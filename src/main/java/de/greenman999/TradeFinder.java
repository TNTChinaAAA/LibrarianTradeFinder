package de.greenman999;

import de.greenman999.config.TradeFinderConfig;
import de.greenman999.gui.handler.InGameHudHandler;
import de.greenman999.gui.screens.ControlUi;
import de.greenman999.gui.handler.ResetLecternModeHandler;
import de.greenman999.gui.handler.SlowModeHandler;
import net.minecraft.block.Block;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;

import java.util.Random;


@SuppressWarnings("DuplicatedCode")
public class TradeFinder {

    public static TradeState state = TradeState.IDLE;
    public static VillagerEntity villager = null;
    public static BlockPos lecternPos = null;

    public static boolean searchAll = true;

    // When searching a single enchantment
    public static Enchantment enchantment = null;

    public static boolean needToBuy = false;
    public static int maxBookPrice = 0;
    public static int minLevel = 0;

    public static int tries = 0;

    private static Vec3d prevPos = null;

    public static int placeDelay = 2;
    public static int interactDelay = 2;

    public static int resetDelay = 80;

    public static int recipeIndex = 0;

    private static boolean startedBreakLook = false;
    private static boolean startedPlaceLook = false;
    private static boolean startedCheckLook = false;
    private static boolean finishedBreakLook = false;
    private static boolean finishedPlaceLook = false;
    private static boolean finishedCheckLook = false;

    private static boolean startedAimLook = false;

    private static boolean finishedAimLook = false;

    public static void stop() {
        state = TradeState.IDLE;

        enchantment = null;
        maxBookPrice = 0;
        minLevel = 0;
        tries = 0;
        resetAllParams();
        InGameHudHandler.removeLastTextFromInGameHud();
    }

    public static int searchList() {
        if(TradeFinderConfig.INSTANCE.enchantments.values().stream().noneMatch(e -> e.enabled)) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.search.no-enchantments").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            return 0;
        }
        searchAll = true;
        state = TradeState.CHECK;
        if(TradeFinder.villager == null || TradeFinder.lecternPos == null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.not-selected").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            stop();
            return 0;
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.success-list").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GREEN))));
        tries = 0;
        return 1;
    }

    public static int searchSingle(Enchantment enchantment, int minLevel, int maxBookPrice) {
        TradeFinder.enchantment = enchantment;
        TradeFinder.minLevel = Math.min(minLevel, enchantment.getMaxLevel());
        TradeFinder.maxBookPrice = maxBookPrice;

        searchAll = false;
        state = TradeState.CHECK;
        tries = 0;

		if(TradeFinder.villager == null || TradeFinder.lecternPos == null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.not-selected").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            stop();
            return 0;
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.success-single", enchantment.getName(minLevel), maxBookPrice).styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GREEN))));
        return 1;
    }

    public static boolean select() {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult hitResult = null;
        if (MinecraftClient.getInstance().player != null) {
            hitResult = MinecraftClient.getInstance().player.raycast(3.0, 0.0F, false);
        }
        if (hitResult != null && (!(hitResult.getType().equals(HitResult.Type.BLOCK)) || hitResult.getType().equals(HitResult.Type.ENTITY))) {
            mc.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.select.not-looking-at-lectern").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            return false;
        }
        BlockPos blockPos = null;
        if (hitResult != null) {
            blockPos = ((BlockHitResult) hitResult).getBlockPos();
        }
        Block block = null;
        if (MinecraftClient.getInstance().world != null) {
            block = MinecraftClient.getInstance().world.getBlockState(blockPos).getBlock();
        }
        if(!(block instanceof LecternBlock)) {
            mc.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.select.not-looking-at-lectern").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            return false;
        }

        double closestDistance = Double.POSITIVE_INFINITY;
        Entity closestEntity = null;

        for(Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            Vec3d entityPos = entity.getPos();
            if (blockPos != null && entity instanceof VillagerEntity && ((VillagerEntity) entity).getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN) && entityPos.distanceTo(blockPos.toCenterPos()) < closestDistance) {
                closestDistance = entityPos.distanceTo(blockPos.toCenterPos());
                closestEntity = entity;
            }
        }

        VillagerEntity foundVillager = (VillagerEntity) closestEntity;
        if(foundVillager == null) {
            mc.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.select.no-librarian-found").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            return false;
        }

        villager = foundVillager;
        lecternPos = blockPos;

        mc.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.select.success").formatted(Formatting.GREEN));

        return true;
    }

    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        //LibrarianTradeFinder.LOGGER.info("AD");

        if(mc.currentScreen instanceof ControlUi) return;
        ClientPlayerEntity player = mc.player;
        if(player == null) return;

        if(TradeFinder.villager == null || TradeFinder.lecternPos == null) {
            if (state != TradeState.IDLE)
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.not-selected").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
            return;
        }

        if (state == TradeState.IDLE) {
            if (needToBuy && !(mc.currentScreen instanceof MerchantScreen)) {
                if (LibrarianTradeFinder.getConfig().autoTradeMode) {
                    TradeFinder.openTradeScreen();
                } else {
                    needToBuy = false;
                }

                return;
            }

            if (mc.currentScreen instanceof MerchantScreen tradeScreen) {
                if (needToBuy) {
                    if (LibrarianTradeFinder.getConfig().autoTradeMode) {
                        tradeScreen.getScreenHandler().setRecipeIndex(recipeIndex);
                        tradeScreen.getScreenHandler().switchTo(recipeIndex);
                        mc.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(recipeIndex));

                        // buy whatever the villager is selling
                        mc.interactionManager.clickSlot(
                                tradeScreen.getScreenHandler().syncId, 2, recipeIndex,
                                SlotActionType.PICKUP, mc.player);

                        recipeIndex = 0;
                    }

                    // close the trade screen
                    closeTradeScreen();
                    needToBuy = false;
                }
            }

            return;
        }

        if (!TradeFinder.villager.getWorld().equals(mc.player.getWorld())) {
            stop();
            //TODO: MinecraftClient.getInstance().inGameHud.getChatHud().addMessage("Not in the same world")
            return;
        } else if (player.squaredDistanceTo(villager) > 5) {
            stop();
            //TODO: MinecraftClient.getInstance().inGameHud.getChatHud().addMessage("Out of range")
            return;
        }

        if (LibrarianTradeFinder.getConfig().resetLecternMode) {
            if (state == TradeState.CHECK) {
                if (villager.getVillagerData().getProfession().equals(VillagerProfession.NONE)) {
                    if (resetDelay > 0) {
                        resetDelay--;
                    } else {
                        resetAllParams();
                        state = TradeState.AIM;
                    }
                } else if (villager.getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
                    resetResetDelay();
                }
            } else if (state == TradeState.WAITING_FOR_PACKET) {
                resetResetDelay();
            } else if (state == TradeState.AIM) {
                resetResetDelay();
            }
        }

        if (state == TradeState.AIM) {
            Vec3d villagerPosition = new Vec3d(villager.getX(), villager.getY() + (double) villager.getEyeHeight(EntityPose.STANDING), villager.getZ());

            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                if(RotationTools.isRotated && !finishedAimLook) {
                    finishedAimLook = true;
                    startedAimLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedAimLook && !finishedAimLook) {
                    RotationTools.smoothLookAt(villagerPosition, 3);
                    startedAimLook = true;
                    return;
                }else if(!finishedAimLook) {
                    return;
                }
            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, villagerPosition);
            }

            resetAllParams();
            state = TradeState.BREAK;
            tries++;
        }

        if((state == TradeState.CHECK || state == TradeState.WAITING_FOR_PACKET) && villager.getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
            Vec3d villagerPosition = new Vec3d(villager.getX(), villager.getY() + (double) villager.getEyeHeight(EntityPose.STANDING), villager.getZ());

            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                if(RotationTools.isRotated && !finishedCheckLook) {
                    finishedCheckLook = true;
                    startedCheckLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedCheckLook && !finishedCheckLook) {
                    RotationTools.smoothLookAt(villagerPosition, 3);
                    startedCheckLook = true;
                    return;
                }else if(!finishedCheckLook) {
                    return;
                }
            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, villagerPosition);
            }

            if(LibrarianTradeFinder.getConfig().slowMode) {
                if(interactDelay > 0) {
                    interactDelay--;
                    return;
                }

                interactDelay = Integer.parseInt(SlowModeHandler.interactDelay.getText());
            }

            ActionResult result = null;
            if (mc.interactionManager != null) {
                result = mc.interactionManager.interactEntity(mc.player, villager, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND, true);
                mc.player.networkHandler
                        .sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
            if(result == ActionResult.SUCCESS) {
                finishedBreakLook = false;
                state = TradeState.WAITING_FOR_PACKET;
            }else {
                mc.inGameHud.getChatHud().addMessage(Text.translatable("librarian-trade-finder.check.interact.failed").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
                stop();
            }

        } else if(state == TradeState.BREAK) {
            if (LibrarianTradeFinder.getConfig().resetLecternMode) {
                resetResetDelay();
            }

            BlockPos toPlace = lecternPos.down();
            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                if(RotationTools.isRotated && !finishedBreakLook) {
                    finishedBreakLook = true;
                    startedBreakLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedBreakLook && !finishedBreakLook) {
                    RotationTools.smoothLookAt(new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5), 3);
                    startedBreakLook = true;
                    return;
                }else if(!finishedBreakLook) {
                    //printLookParams();
                    return;
                }
            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
            }
            PlayerInventory inventory = player.getInventory();
            ItemStack mainHand = inventory.getMainHandStack();
            if(mainHand.getItem() instanceof AxeItem) {
                int remainingDurability = mainHand.getMaxDamage() - mainHand.getDamage();
                if(remainingDurability <= 5 && LibrarianTradeFinder.getConfig().preventAxeBreaking) {
                    stop();
                    mc.inGameHud.getChatHud().addMessage(Text.translatable("librarian-trade-finder.break.axe.breaking").formatted(Formatting.RED));
                    return;
                }
            }

            if (mc.world != null && mc.world.getBlockState(lecternPos).getBlock() instanceof LecternBlock && mc.interactionManager != null) {
                player.swingHand(Hand.MAIN_HAND, true);
                mc.interactionManager.updateBlockBreakingProgress(lecternPos, Direction.UP);
                player.networkHandler
                        .sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }else {
                //finishedPlaceLook = false;
                resetAllParams();
                state = TradeState.PLACE;
                if(LibrarianTradeFinder.getConfig().tpToVillager && mc.getNetworkHandler() != null) {
                    prevPos = mc.player.getPos();
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(villager.getX(), villager.getY(), villager.getZ(), true));
                }
            }

        } else if (state == TradeState.PLACE) {
            if (LibrarianTradeFinder.getConfig().resetLecternMode) {
                resetResetDelay();
            }

            if(LibrarianTradeFinder.getConfig().tpToVillager && mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(prevPos.x, prevPos.y, prevPos.z, true));
            }

            BlockPos toPlace = lecternPos.down();
            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                //mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
                if(RotationTools.isRotated && !finishedPlaceLook) {
                    finishedPlaceLook = true;
                    startedPlaceLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedPlaceLook && !finishedPlaceLook) {
                    RotationTools.smoothLookAt(new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5), 3);
                    startedPlaceLook = true;
                    return;
                }else if(!finishedPlaceLook) {
                    //LibrarianTradeFinder.LOGGER.info("Not finish place look");
                    //printLookParams();
                    return;
                }

            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
            }

            if(LibrarianTradeFinder.getConfig().slowMode) {
                if(placeDelay > 0) {
                    placeDelay--;
                    return;
                }

                placeDelay = Integer.parseInt(SlowModeHandler.placeDelay.getText());
            }


            if(!mc.player.getOffHandStack().getItem().equals(Items.LECTERN) && mc.interactionManager != null) {
                if (mc.player.playerScreenHandler == mc.player.currentScreenHandler) {
                    for (int i = 9; i < 45; i++) {
                        if (mc.player.getInventory().getStack(i >= 36 ? i - 36 : i).getItem() == Items.LECTERN) {
                            boolean itemInOffhand = !mc.player.getOffHandStack().isEmpty();
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);

                            if (itemInOffhand)
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);

                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.getInventory().getStack(i).getItem() == Items.LECTERN) {
                            if (i != mc.player.getInventory().selectedSlot) {
                                mc.player.getInventory().selectedSlot = i;
                                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
                            }

                            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                            break;
                        }
                    }
                }
            }

            //LibrarianTradeFinder.LOGGER.info("Success reached!");

            BlockHitResult hit = new BlockHitResult(new Vec3d(lecternPos.getX(), lecternPos.getY(),
                    lecternPos.getZ()), Direction.UP, lecternPos.down(), false);
            if (mc.interactionManager != null) {
                mc.interactionManager.interactBlock(mc.player, Hand.OFF_HAND, hit);
                player.swingHand(Hand.OFF_HAND, true);
                player.networkHandler
                        .sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));
            }

            //finishedCheckLook = false;
            resetAllParams();
            state = TradeState.CHECK;
        }
    }

    public static void openTradeScreen() {
        MinecraftClient MC = MinecraftClient.getInstance();

        if(MC.itemUseCooldown > 0)
            return;

        ClientPlayerInteractionManager im = MC.interactionManager;
        ClientPlayerEntity player = MC.player;
        // create realistic hit result
        Box box = villager.getBoundingBox();
        Vec3d start = RotationTools.getEyesPos();
        Vec3d end = box.getCenter();
        Vec3d hitVec = box.raycast(start, end).orElse(start);
        EntityHitResult hitResult = new EntityHitResult(villager, hitVec);

        // face end vector
        if (LibrarianTradeFinder.getConfig().legitMode) {
            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, end);
        }

        // click on villager
        Hand hand = Hand.MAIN_HAND;
        ActionResult actionResult =
                im.interactEntityAtLocation(player, villager, hitResult, hand);

        if(!actionResult.isAccepted())
            im.interactEntity(player, villager, hand);

        // swing hand
        if(actionResult.isAccepted() && actionResult.shouldSwingHand())
            player.swingHand(hand);

        // set cooldown
        MC.itemUseCooldown = 4;
    }

    public static void closeTradeScreen() {
        MinecraftClient MC = MinecraftClient.getInstance();
        MC.player.closeHandledScreen();
        MC.itemUseCooldown = 4;
    }

    public static void resetAllParams() {
        resetResetDelay();
        placeDelay = Integer.parseInt(SlowModeHandler.placeDelay.getText());
        interactDelay = Integer.parseInt(SlowModeHandler.interactDelay.getText());
        resetCheckLook();
        resetBreakLook();
        resetPlaceLook();
        resetAimLook();
    }

    public static void resetCheckLook() {
        if (RotationTools.isRotated) {
            RotationTools.isRotated = false;
        }

        /*if (RotationTools.speed != 0) {
            RotationTools.speed = 0;
        }*/

        startedCheckLook = false;
        finishedCheckLook = false;
    }

    public static void resetBreakLook() {
        if (RotationTools.isRotated) {
            RotationTools.isRotated = false;
        }

        /*if (RotationTools.speed != 0) {
            RotationTools.speed = 0;
        }*/

        startedBreakLook = false;
        finishedBreakLook = false;
    }

    public static void resetPlaceLook() {
        if (RotationTools.isRotated) {
            RotationTools.isRotated = false;
        }

        /*if (RotationTools.speed != 0) {
            RotationTools.speed = 0;
        }*/

        startedPlaceLook = false;
        finishedPlaceLook = false;
    }

    public static void resetAimLook() {
        if (RotationTools.isRotated) {
            RotationTools.isRotated = false;
        }

        startedAimLook = false;
        finishedAimLook = false;
    }

    public static void resetResetDelay() {
        boolean a = new Random().nextBoolean();
        int b = new Random().nextInt(3);
        resetDelay = Integer.parseInt(ResetLecternModeHandler.delay.getText()) + (a ? -b : b);
    }

    public static void printLookParams() {
        LibrarianTradeFinder.LOGGER.info("--------------------------------------------");
        LibrarianTradeFinder.LOGGER.info("Speed: " + RotationTools.speed);
        LibrarianTradeFinder.LOGGER.info("Rotated: " + RotationTools.isRotated);
        LibrarianTradeFinder.LOGGER.info("Finished Check Look: " + finishedCheckLook);
        LibrarianTradeFinder.LOGGER.info("Started Check Look: " + startedCheckLook);
        LibrarianTradeFinder.LOGGER.info("Finished Break Look: " + finishedBreakLook);
        LibrarianTradeFinder.LOGGER.info("Started Break Look: " + startedBreakLook);
        LibrarianTradeFinder.LOGGER.info("Finished Place Look: " + finishedPlaceLook);
        LibrarianTradeFinder.LOGGER.info("Started Place Look: " + startedPlaceLook);
        LibrarianTradeFinder.LOGGER.info("--------------------------------------------");
    }
}
