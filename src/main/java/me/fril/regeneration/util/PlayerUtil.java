package me.fril.regeneration.util;

import me.fril.regeneration.client.gui.ToastRegeneration;
import me.fril.regeneration.network.MessageSetPerspective;
import me.fril.regeneration.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Sub
 * on 20/09/2018.
 */
public class PlayerUtil {
	
	public static void sendHotbarMessage(EntityPlayer player, String message, boolean hotBar) {
		if (!player.world.isRemote) {
			player.sendStatusMessage(new TextComponentTranslation(message), hotBar);
		}
	}
	
	public static void sendHotbarMessage(EntityPlayer player, TextComponentTranslation translation, boolean hotBar) {
		if (!player.world.isRemote) {
			player.sendStatusMessage(translation, hotBar);
		}
	}
	
	
	public static void setPerspective(EntityPlayerMP player, boolean thirdperson, boolean resetPitch) {
		NetworkHandler.INSTANCE.sendTo(new MessageSetPerspective(thirdperson, resetPitch), player);
	}
	
	
	public static boolean canEntityAttack(Entity entity) { //NOTE unused
		if (entity instanceof EntityLiving) {
			EntityLiving ent = (EntityLiving) entity;
			for (EntityAITasks.EntityAITaskEntry task : ent.tasks.taskEntries) {
				if (task.action instanceof EntityAIAttackMelee || task.action instanceof EntityAIAttackRanged || task.action instanceof EntityAIAttackRangedBow
						|| task.action instanceof EntityAINearestAttackableTarget || task.action instanceof EntityAIZombieAttack || task.action instanceof EntityAIOwnerHurtByTarget)
					return true;
			}
		}
		return false;
	}
	
	
	public static void damagePlayerArmor(EntityPlayerMP playerMP, int amount) {
		for (EntityEquipmentSlot type : EntityEquipmentSlot.values()) {
			if (!type.equals(EntityEquipmentSlot.MAINHAND) && !type.equals(EntityEquipmentSlot.OFFHAND) && playerMP.getItemStackFromSlot(type).getItem() instanceof ItemArmor) {
				ItemStack stack = playerMP.getItemStackFromSlot(type);
				if (stack.attemptDamageItem(amount, playerMP.world.rand, playerMP))
					stack.setCount(0); //item broke
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void createToast(TextComponentTranslation title, TextComponentTranslation subtitle, RegenState regenState) {
		Minecraft.getMinecraft().getToastGui().add(new ToastRegeneration(title, subtitle, regenState));
	}
	
	public static boolean applyPotionIfAbsent(EntityPlayer player, int potionId, int length, int amplifier, boolean ambient, boolean showParticles) {
		if (player.getActivePotionEffects().stream().noneMatch(pe->Potion.getIdFromPotion(pe.getPotion()) == potionId)) {
			player.addPotionEffect(new PotionEffect(Potion.getPotionById(potionId), length, amplifier, ambient, showParticles));
			return true;
		} else return false;
	}
	
}
