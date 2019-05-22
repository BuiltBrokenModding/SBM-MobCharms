package com.builtbroken.sbmmobcharms.lib;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootFunctionSetCharmNBT extends LootFunction
{
    private final boolean applyPotionType;

    public LootFunctionSetCharmNBT(boolean applyPotionType)
    {
        super(new LootCondition[0]);

        this.applyPotionType = applyPotionType;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext ctx)
    {
        NBTTagCompound stackTag = stack.getTagCompound();
        NBTTagCompound randomizedTag = CharmUtils.getRandomizedCharmTag(applyPotionType, rand);

        if(stackTag == null)
            stackTag = randomizedTag.copy();
        else
            stackTag.merge(randomizedTag);

        stack.setTagCompound(stackTag);
        return stack;
    }
}
