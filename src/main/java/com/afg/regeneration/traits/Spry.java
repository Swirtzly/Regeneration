package com.afg.regeneration.traits;

import lucraft.mods.lucraftcore.abilities.AbilityAttributeModifier;
import lucraft.mods.lucraftcore.attributes.LCAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created by AFlyingGrayson on 8/10/17
 */
public class Spry extends AbilityAttributeModifier
{

	public Spry(EntityPlayer player, UUID uuid, float factor, int operation)
	{
		super(player, uuid, factor, operation);
	}

	@Override public IAttribute getAttribute()
	{
		return LCAttributes.JUMP_HEIGHT;
	}
}
