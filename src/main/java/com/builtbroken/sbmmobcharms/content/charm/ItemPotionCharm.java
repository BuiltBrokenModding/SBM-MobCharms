package com.builtbroken.sbmmobcharms.content.charm;

import java.util.List;

import com.builtbroken.sbmmobcharms.MobCharms;
import com.builtbroken.sbmmobcharms.lib.CharmType;
import com.builtbroken.sbmmobcharms.lib.EffectContext;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemPotionCharm extends ItemCharm
{
    public ItemPotionCharm()
    {
        super(CharmType.POTION);
    }

    @Override
    protected void tryApplyEffect(ItemStack stack, World world, BlockPos pos, Entity player)
    {
        if(!world.isRemote)
        {
            //set a random power if the stack doesn't have one or it has no power
            if(!stack.hasTagCompound() || stack.getTagCompound().getInteger("Power") == 0)
                stack.setTagCompound(MobCharms.getRandomizedCharmTag(true, world.rand));

            if(player == null || player instanceof EntityPlayer) //null is allowed
            {
                String potionId = stack.getTagCompound().getString("Potion");

                if(potionId != null)
                    getCharmType().apply(new EffectContext(world, pos, (EntityPlayer)player, ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionId)), stack.getTagCompound().getInteger("Power")));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);

        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Potion"))
            tooltip.add(I18n.format("sbmmobcharms.tooltip.potion", I18n.format(ForgeRegistries.POTIONS.getValue(new ResourceLocation(stack.getTagCompound().getString("Potion"))).getName())));
    }
}
