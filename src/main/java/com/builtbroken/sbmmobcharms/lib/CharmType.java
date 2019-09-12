package com.builtbroken.sbmmobcharms.lib;

import java.util.function.Consumer;

import com.builtbroken.sbmmobcharms.MobCharms;
import com.builtbroken.sbmmobcharms.content.charm.BlockCharm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

public enum CharmType implements IStringSerializable
{
    ATTACK(0x000000, CharmEffects::applyAttackEffect),
    ATTACK_TARGET(0x0000FF, CharmEffects::applyAttackTargetEffect),
    BUFF(0x00FF00, ctx -> {}), //handled in MobCharmsEventHandler#onLivingAttack
    FOLLOW(0x00FFFF, ctx -> {}), //handled in EntityAIAffectedByFollowCharm
    HEAL_FRIENDLY(0xFF0000, CharmEffects::applyHealFriendlyEffect),
    NO_ATTACK(0xFF00FF, CharmEffects::applyNoAttackEffect),
    POTION(0xFFFF00, CharmEffects::applyPotionEffect),
    PUSH(0xFF6600, CharmEffects::applyPushEffect);

    public final int color;
    private final Consumer<CharmEffectContext> effectApplier;

    /**
     * @param color The color of the charm item
     * @param effectApplier The consumer which applies the effect of the charm in a world at a position
     */
    CharmType(int color, Consumer<CharmEffectContext> effectApplier)
    {
        this.color = color;
        this.effectApplier = effectApplier;
    }

    /**
     * Applies this charm type's effect with in the given world at the given position
     * @param ctx The context while applying the effect
     */
    public void apply(CharmEffectContext ctx)
    {
        effectApplier.accept(ctx);
    }

    /**
     * Returns the item corresponding to this CharmType
     * @return This type's item, null if item registration isn't done yet
     */
    public Item toItem()
    {
        switch(this)
        {
            case ATTACK: return MobCharms.attackCharm;
            case ATTACK_TARGET: return MobCharms.attackTargetCharm;
            case BUFF: return MobCharms.buffCharm;
            case FOLLOW: return MobCharms.followCharm;
            case HEAL_FRIENDLY: return MobCharms.healFriendlyCharm;
            case NO_ATTACK: return MobCharms.noAttackCharm;
            case POTION: return MobCharms.potionCharm;
            case PUSH: return MobCharms.pushCharm;
            default: return Items.AIR;
        }
    }

    /**
     * Returns the corresponding charm type to the given item
     * @param item The item to get the charm type of
     * @return The item's charm type, null if an invalid item was given
     */
    public static CharmType fromItem(Item item)
    {
        if(item == MobCharms.attackCharm)
            return ATTACK;
        else if(item == MobCharms.attackTargetCharm)
            return ATTACK_TARGET;
        else if(item == MobCharms.buffCharm)
            return BUFF;
        else if(item == MobCharms.followCharm)
            return FOLLOW;
        else if(item == MobCharms.healFriendlyCharm)
            return HEAL_FRIENDLY;
        else if(item == MobCharms.noAttackCharm)
            return NO_ATTACK;
        else if(item == MobCharms.potionCharm)
            return POTION;
        else if(item == MobCharms.pushCharm)
            return PUSH;
        else return null;
    }

    /**
     * Returns the corresponding block state to the given CharmType
     * @param type The type to get the block state of
     * @return The type's block state, null if block registration isn't done yet
     */
    public static IBlockState toBlockState(CharmType type)
    {
        if(MobCharms.charmBlock != null)
            return MobCharms.charmBlock.getDefaultState().withProperty(BlockCharm.CHARM_TYPE, type);
        else return null;
    }

    @Override
    public String getName()
    {
        return name().toLowerCase();
    }
}
