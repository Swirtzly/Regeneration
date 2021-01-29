package me.swirtzly.regeneration.handlers;

import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.Regeneration;
import me.swirtzly.regeneration.client.gui.GuiPreferences;
import me.swirtzly.regeneration.client.skinhandling.SkinManipulation;
import me.swirtzly.regeneration.common.capability.IRegen;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.dimension.biomes.GallifrayanWastelands;
import me.swirtzly.regeneration.common.types.RegenTypes;
import me.swirtzly.regeneration.util.client.ClientUtil;
import me.swirtzly.regeneration.util.client.RenderUtil;
import me.swirtzly.regeneration.util.common.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.HandSide;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.swirtzly.regeneration.compat.ArchHelper.getRegenerations;
import static me.swirtzly.regeneration.compat.ArchHelper.hasRegenerations;
import static me.swirtzly.regeneration.util.common.PlayerUtil.RegenState.*;

/**
 * Created by Sub on 16/09/2018.
 */
public class ClientHandler {

    public static String getColoredText(String msg) {
        return msg.replaceAll("&", String.valueOf('\u00a7'));
    }

    private static void createWorldAmbience(PlayerEntity player) {
        Random random = player.world.rand;
        double originX = player.posX;
        double originY = player.posY;
        double originZ = player.posZ;
        for (int i = 0; i < 55; i++) {
            double particleX = originX + (random.nextInt(24) - random.nextInt(24));
            double particleY = originY + (random.nextInt(24) - random.nextInt(24));
            double particleZ = originZ + (random.nextInt(24) - random.nextInt(24));
            double velocityX = (random.nextDouble() - 0.5) * 0.02;
            double velocityY = (random.nextDouble() - 0.5) * 0.02;
            double velocityZ = (random.nextDouble() - 0.5) * 0.02;

            BasicParticleType[] TYPES = new BasicParticleType[]{ParticleTypes.SMOKE, ParticleTypes.CRIT, ParticleTypes.BARRIER};
            IParticleData currentType = TYPES[player.world.rand.nextInt(TYPES.length - 1)];
            player.world.addParticle(currentType, particleX, particleY, particleZ, velocityX, velocityY, velocityZ);
            if (player.world.rand.nextInt(30) < 10 && player.world.rand.nextBoolean()) {
                BlockPos pos = new BlockPos(particleX + random.nextInt(500), particleY + random.nextInt(500), particleZ + random.nextInt(500));
                player.world.addEntity(new LightningBoltEntity(player.world, pos.getX(), pos.getY(), pos.getZ(), true));
            }
        }
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) return;
        if (Minecraft.getInstance().world == null) {
            if (SkinManipulation.PLAYER_SKINS.size() > 0) {
                SkinManipulation.PLAYER_SKINS.forEach(((uuid, skinInfo) -> Minecraft.getInstance().getTextureManager().deleteTexture(skinInfo.getTextureLocation())));
                SkinManipulation.PLAYER_SKINS.clear();
                Regeneration.LOG.warn("CLEARED CACHE OF PLAYER_SKINS");
            }
        }
    }

    @SubscribeEvent
    public void onClientUpdate(LivingEvent.LivingUpdateEvent e) {
        if (!(e.getEntity() instanceof PlayerEntity) || Minecraft.getInstance().player == null) return;

        PlayerEntity player = (PlayerEntity) e.getEntity();
        Minecraft.getInstance().runAsync(() -> {

            if (player.ticksExisted == 50) {

                UUID clientUUID = Minecraft.getInstance().player.getUniqueID();
                RegenCap.get(player).ifPresent((data) -> {
                    if (data.areHandsGlowing()) {
                        ClientUtil.playSound(data.getLivingEntity(), RegenObjects.Sounds.HAND_GLOW.get().getRegistryName(), SoundCategory.PLAYERS, true, () -> !data.areHandsGlowing(), 0.2F);
                    }

                    if (data.getState() == REGENERATING) {
                        ClientUtil.playSound(data.getLivingEntity(), RegenObjects.Sounds.REGENERATION_0.get().getRegistryName(), SoundCategory.PLAYERS, true, () -> !data.getState().equals(REGENERATING), 1.0F);
                    }

                    if (data.getState().isGraceful() && clientUUID == player.getUniqueID()) {
                        ClientUtil.playSound(data.getLivingEntity(), RegenObjects.Sounds.CRITICAL_STAGE.get().getRegistryName(), SoundCategory.PLAYERS, true, () -> !data.getState().equals(GRACE_CRIT), 1F);
                        ClientUtil.playSound(data.getLivingEntity(), RegenObjects.Sounds.HEART_BEAT.get().getRegistryName(), SoundCategory.PLAYERS, true, () -> !data.getState().isGraceful(), 0.2F);
                        ClientUtil.playSound(data.getLivingEntity(), RegenObjects.Sounds.GRACE_HUM.get().getRegistryName(), SoundCategory.AMBIENT, true, () -> data.getState() != GRACE, 1.5F);
                    }
                });
            }

        });

    }

    @SuppressWarnings("incomplete-switch")
    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;

        RegenCap.get(player).ifPresent((cap) -> {
            String warning = null;

            switch (cap.getState()) {
                case GRACE:
                    RenderUtil.renderVignette(cap.getPrimaryColor(), 0.3F, cap.getState());
                    warning = new TranslationTextComponent("regeneration.messages.warning.grace", new TranslationTextComponent(ClientUtil.keyBind)).getUnformattedComponentText();
                    break;

                case GRACE_CRIT:
                    RenderUtil.renderVignette(new Vec3d(1, 0, 0), 0.5F, cap.getState());
                    warning = new TranslationTextComponent("regeneration.messages.warning.grace_critical", ClientUtil.keyBind).getUnformattedComponentText();
                    break;

                case REGENERATING:
                    RenderUtil.renderVignette(cap.getSecondaryColor(), 0.5F, cap.getState());
                    break;

                case POST:
                    if (player.hurtTime > 0 || player.getActivePotionEffect(Effects.NAUSEA) != null) {
                        RenderUtil.renderVignette(cap.getSecondaryColor(), 0.5F, cap.getState());
                    }
                    break;
            }

            if (warning != null)
                Minecraft.getInstance().fontRenderer.drawString(warning, Minecraft.getInstance().mainWindow.getScaledWidth() / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(warning) / 2, 4, 0xffffffff);
        });

    }

    @SubscribeEvent
    public void onColorFog(EntityViewRenderEvent.RenderFogEvent.FogColors e) {
        RegenCap.get(Minecraft.getInstance().getRenderViewEntity()).ifPresent((data) -> {
            if (data.getRegenType() == RegenTypes.HARTNELL && data.getState() == REGENERATING) {
                e.setRed((float) data.getPrimaryColor().x);
                e.setGreen((float) data.getPrimaryColor().y);
                e.setBlue((float) data.getPrimaryColor().z);
            }
        });
    }

    @SubscribeEvent
    public void onSetupFogDensity(EntityViewRenderEvent.RenderFogEvent.FogDensity event) {
        Entity viewer = Minecraft.getInstance().getRenderViewEntity();
        if (viewer != null) {
            RegenCap.get(viewer).ifPresent((data) -> {
                if (data.getState() == GRACE_CRIT) {
                    event.setCanceled(true);
                    float amount = MathHelper.cos(data.getLivingEntity().ticksExisted * 0.06F) * -0.09F;
                    event.setDensity(amount);
                }

                if (data.getRegenType() == RegenTypes.HARTNELL && data.getAnimationTicks() > 0) {
                    event.setCanceled(true);
                    float opacity = MathHelper.clamp(MathHelper.sin((viewer.ticksExisted + Minecraft.getInstance().getRenderPartialTicks()) / 10F) * 0.1F + 0.1F, 0.11F, 1F);
                    event.setDensity(opacity);
                }

            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientChatRecieved(ClientChatReceivedEvent e) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (e.getType() != ChatType.CHAT || PlayerUtil.isZeroRoom(player)) return;

        IRegen data = RegenCap.get(player).orElse(null);
        if (data.getState() != POST) return;

        if (player.world.rand.nextBoolean()) {
            String message = e.getMessage().getUnformattedComponentText();
            StringTextComponent newMessage = new StringTextComponent("");
            String[] words = message.split(" ");
            for (String word : words) {
                if (word.equals(words[0])) {
                    StringTextComponent name = new StringTextComponent(word + " ");
                    newMessage.appendSibling(name);
                    continue;
                }
                if (player.world.rand.nextBoolean()) {
                    StringTextComponent txtComp = new StringTextComponent(getColoredText("&k" + word + "&r "));
                    txtComp.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(word)));
                    newMessage.appendSibling(txtComp);
                } else {
                    StringTextComponent txtComp = new StringTextComponent(word + " ");
                    newMessage.appendSibling(txtComp);
                }
            }
            e.setMessage(newMessage);
        }
    }

    @SubscribeEvent
    public void keyInput(InputUpdateEvent e) {
        if (Minecraft.getInstance().player == null) return;

        RegenCap.get(Minecraft.getInstance().player).ifPresent((data -> {
            if (data.getState() == REGENERATING || data.isSyncingToJar()) { // locking user
                MovementInput moveType = e.getMovementInput();
                moveType.rightKeyDown = false;
                moveType.leftKeyDown = false;
                moveType.backKeyDown = false;
                moveType.jump = false;
                moveType.moveForward = 0.0F;
                moveType.sneak = false;
                moveType.moveStrafe = 0.0F;
            }
        }));
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) e.getEntityLiving();
            SkinManipulation.PLAYER_SKINS.remove(player.getUniqueID());

            if (player.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) {
                ClientUtil.sendSkinResetPacket();
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent e) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = Minecraft.getInstance().player;

        float factor = 0.2F;
        if (player.getHeldItemMainhand().getItem() != Items.AIR || mc.gameSettings.thirdPersonView > 0) return;

        RegenCap.get(player).ifPresent((data) -> {

            boolean flag = data.getState() == REGENERATING;
            e.setCanceled(flag);

            if (!data.areHandsGlowing())// || !flag)
                return;

            GlStateManager.pushMatrix();

            float leftHandedFactor = mc.gameSettings.mainHand.equals(HandSide.RIGHT) ? 1 : -1;
            GlStateManager.translatef(0.33F * leftHandedFactor, -0.23F, -0.5F); // move in place
            GlStateManager.translatef(-.8F * player.swingProgress * leftHandedFactor, -.8F * player.swingProgress, -.4F * player.swingProgress); // compensate for 'punching' motion
            GlStateManager.translatef(-(player.renderArmYaw - player.prevRenderArmYaw) / 400F, (player.renderArmPitch - player.prevRenderArmPitch) / 500F, 0); // compensate for 'swinging' motion

            RenderUtil.setupRenderLightning();
            GlStateManager.rotatef((mc.player.ticksExisted + RenderUtil.renderTick) / 2F, 0, 1, 0);
            for (int i = 0; i < 15; i++) {
                GlStateManager.rotatef((mc.player.ticksExisted + RenderUtil.renderTick) * i / 70F, 1, 1, 0);
                Vec3d primaryColor = data.getPrimaryColor();

                Random rand = player.world.rand;
                RenderUtil.drawGlowingLine(new Vec3d((-factor / 2F) + rand.nextFloat() * factor, (-factor / 2F) + rand.nextFloat() * factor, (-factor / 2F) + rand.nextFloat() * factor), new Vec3d((-factor / 2F) + rand.nextFloat() * factor, (-factor / 2F) + rand.nextFloat() * factor, (-factor / 2F) + rand.nextFloat() * factor), 0.1F, primaryColor, 0);
            }
            RenderUtil.finishRenderLightning();

            GlStateManager.popMatrix();
        });
    }

    @SubscribeEvent
    public void onItemToolTip(ItemTooltipEvent event) {
        List< ITextComponent > tooltip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        if (hasRegenerations(stack)) {
            tooltip.add(new TranslationTextComponent("nbt.regeneration.item.stored_regens", getRegenerations(stack)));
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null) return;
        PlayerEntity player = Minecraft.getInstance().player;

        if (player.world.getBiome(player.getPosition()) instanceof GallifrayanWastelands) {
            createWorldAmbience(player);
        }
    }

}
