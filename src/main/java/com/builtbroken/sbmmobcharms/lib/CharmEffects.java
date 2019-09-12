package com.builtbroken.sbmmobcharms.lib;

import java.util.HashMap;
import java.util.Map;

import com.builtbroken.sbmmobcharms.MobCharmsConfig;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CharmEffects
{
    public static final Map<EntityPlayer,Map<Integer,ItemStack>> PLAYERS_WITH_CHARMS = new HashMap<>();
    public static final Map<World,Map<BlockPos,CharmType>> CHARM_TILES = new HashMap<>();
    public static final Map<World,Map<EntityItem,CharmType>> CHARM_ENTITIES = new HashMap<>();

    public static void applyAttackEffect(CharmEffectContext ctx)
    {
        if(MobCharmsConfig.enableAttackCharm)
        {
            CharmUtils.tryApplyEffectToEntitiesWithClosestPlayer(ctx, (entity, closestPlayer) -> {
                if(entity instanceof EntityLiving)
                    ((EntityLiving)entity).setAttackTarget(closestPlayer);
            });
        }
    }

    public static void applyAttackTargetEffect(CharmEffectContext ctx)
    {
        if(MobCharmsConfig.enableAttackTargetCharm)
        {
            CharmUtils.tryApplyEffectToEntitiesWithClosestPlayer(ctx, (entity, closestPlayer) -> {
                if(entity instanceof EntityLiving)
                    ((EntityLiving)entity).setAttackTarget(closestPlayer.getLastAttackedEntity());
            });
        }
    }

    public static void applyHealFriendlyEffect(CharmEffectContext ctx)
    {
        if(MobCharmsConfig.enableHealFriendlyCharm)
        {
            CharmUtils.tryApplyEffectToEntities(ctx, entity -> {
                if(entity instanceof EntityAmbientCreature || entity instanceof EntityAgeable || entity instanceof EntityWaterMob || entity instanceof EntityPlayer)
                    entity.setHealth(entity.getMaxHealth());
            });
        }
    }

    public static void applyNoAttackEffect(CharmEffectContext ctx)
    {
        if(MobCharmsConfig.enableNoAttackCharm)
        {
            CharmUtils.tryApplyEffectToEntities(ctx, entity -> {
                if(entity instanceof EntityLiving)
                    ((EntityLiving)entity).setAttackTarget(null);
            });
        }
    }

    public static void applyPotionEffect(CharmEffectContext ctx)
    {
        if(MobCharmsConfig.enablePotionCharm && ctx.getPotion() != null)
        {
            CharmUtils.tryApplyEffectToEntities(ctx, entity -> {
                entity.addPotionEffect(new PotionEffect(ctx.getPotion(), 20 * 11, 0)); //11 seconds
            });
        }
    }

    public static void applyPushEffect(CharmEffectContext ctx)
    {
        if(MobCharmsConfig.enablePushCharm)
        {
            CharmUtils.tryApplyEffectToEntities(ctx, entity -> {
                BlockPos dir = (ctx.getPlayer() == null ? ctx.getPos() : ctx.getPlayer().getPosition()).subtract(entity.getPosition()); //get the direction the entity should be pushed towards
                //normalize
                double dist = dir.getDistance(0, 0, 0);

                //move entity towards that direction, respecting +/-
                //extra - to push away instead of attract
                //* 0.04F to reduce the strength
                entity.motionX = dist * -Math.signum(dir.getX()) * 0.04F;
                entity.motionY = dist * -Math.signum(dir.getY()) * 0.04F;
                entity.motionZ = dist * -Math.signum(dir.getZ()) * 0.04F;
            });
        }
    }

    /**
     * Adds a charm tile entity to a world
     * @param world The world to add to
     * @param pos The position at which the charm tile entity is
     * @param type The type of the tile entity's charm
     */
    public static void addCharmTile(World world, BlockPos pos, CharmType type)
    {
        Map<BlockPos,CharmType> positions = CHARM_TILES.get(world);

        if(positions == null)
            positions = new HashMap<>();

        positions.put(pos, type);
        CHARM_TILES.put(world, positions);
    }

    /**
     * Removes a charm tile entity from a world
     * @param world The world to remove from
     * @param pos The position at which the charm tile entity was
     */
    public static void removeCharmTile(World world, BlockPos pos)
    {
        Map<BlockPos,CharmType> positions = CHARM_TILES.get(world);

        if(positions != null)
            positions.remove(pos);

        CHARM_TILES.put(world, positions);
    }

    /**
     * Adds a charm entity item to a world
     * @param world The world to add to
     * @param ei The entity item
     * @param type The type of the entity item's charm
     */
    public static void addEntityItemCharm(World world, EntityItem ei, CharmType type)
    {
        Map<EntityItem,CharmType> eis = CHARM_ENTITIES.get(world);

        if(eis == null)
            eis = new HashMap<>();

        eis.put(ei, type);
        CHARM_ENTITIES.put(world, eis);
    }

    /**
     * Removes a charm entity item from a world
     * @param world The world to remove from
     * @param ei The entity item
     */
    public static void removeEntityItemCharm(World world, EntityItem ei)
    {
        Map<EntityItem,CharmType> eis = CHARM_ENTITIES.get(world);

        if(eis != null)
            eis.remove(ei);

        CHARM_ENTITIES.put(world, eis);
    }
}
