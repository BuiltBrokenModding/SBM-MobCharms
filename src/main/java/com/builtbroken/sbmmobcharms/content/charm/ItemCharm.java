package com.builtbroken.sbmmobcharms.content.charm;

import java.util.List;

import com.builtbroken.sbmmobcharms.MobCharms;
import com.builtbroken.sbmmobcharms.lib.CharmType;
import com.builtbroken.sbmmobcharms.lib.EffectContext;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCharm extends Item implements IBauble
{
    private final CharmType type;

    public ItemCharm(CharmType type)
    {
        setCreativeTab(MobCharms.CREATIVE_TAB);
        this.type = type;
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return "item." + getRegistryName().toString();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if(itemSlot >= 0 && itemSlot < 9)
            tryApplyEffect(stack, world, entity.getPosition(), entity);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        tryApplyEffect(entityItem.getItem(), entityItem.getEntityWorld(), entityItem.getPosition(), null);
        return false;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player)
    {
        tryApplyEffect(stack, player.world, player.getPosition(), player);
    }

    /**
     * Tries to apply the potion effect that may be present on the given stack
     * @param stack The charm containing a potion effect
     * @param world The world to apply the effect in
     * @param player The player in whose inventory the stack is in, can be null if the item is dropped
     * @param pos The position to apply the effect at
     */
    protected void tryApplyEffect(ItemStack stack, World world, BlockPos pos, Entity player)
    {
        if(!world.isRemote)
        {
            //set a random power if the stack doesn't have one or it has no power
            if(!stack.hasTagCompound() || stack.getTagCompound().getInteger("Power") == 0)
                stack.setTagCompound(MobCharms.getRandomizedCharmTag(false, world.rand));

            if(player == null || player instanceof EntityPlayer) //null is allowed
                type.apply(new EffectContext(world, pos, (EntityPlayer)player, null, stack.getTagCompound().getInteger("Power")));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(world.isAirBlock(pos.offset(facing))) //only place down if there is space
        {
            world.setBlockState(pos.offset(facing), CharmType.toBlockState(type)); //set the block
            world.playSound(player, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            //te and item removal logic is in block
        }

        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        if(stack.hasTagCompound())
            tooltip.add(I18n.format("sbmmobcharms.tooltip.power", stack.getTagCompound().getInteger("Power")));
    }

    /**
     * @return The type of this charm
     */
    public CharmType getCharmType()
    {
        return type;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack)
    {
        return BaubleType.CHARM;
    }
}
