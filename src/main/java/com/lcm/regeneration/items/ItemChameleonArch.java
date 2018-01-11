package com.lcm.regeneration.items;

import com.lcm.regeneration.RegenerationSounds;
import com.lcm.regeneration.superpower.TimelordSuperpower;
import com.lcm.regeneration.superpower.TimelordSuperpowerHandler;

import lucraft.mods.lucraftcore.superpowers.SuperpowerHandler;
import lucraft.mods.lucraftcore.superpowers.SuperpowerPlayerHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Created by AFlyingGrayson on 8/28/17
 */
public class ItemChameleonArch extends Item {
	public ItemChameleonArch() {
		setUnlocalizedName("chameleonArch");
		setRegistryName("chameleonarch");
		setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		SuperpowerPlayerHandler handler = SuperpowerHandler.getSuperpowerPlayerHandler(playerIn);
		
		if (handler == null) {
			SuperpowerHandler.setSuperpower(playerIn, TimelordSuperpower.instance);
			
			playerIn.world.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, RegenerationSounds.GET, SoundCategory.PLAYERS, 1.0F, 1.0F);
			playerIn.sendStatusMessage(new TextComponentString("You've become a timelord!"), true);
		} else if (handler instanceof TimelordSuperpowerHandler) {
			((TimelordSuperpowerHandler) handler).regenerationsLeft = 12;
			playerIn.world.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, RegenerationSounds.GET, SoundCategory.PLAYERS, 1.0F, 1.0F);
			playerIn.sendStatusMessage(new TextComponentString("You've reset your regeneration cycles!"), true);
		} else return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		
		itemstack.shrink(1);
		return new ActionResult<>(EnumActionResult.PASS, itemstack);
	}
}