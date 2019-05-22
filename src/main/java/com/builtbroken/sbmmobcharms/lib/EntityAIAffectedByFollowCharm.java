package com.builtbroken.sbmmobcharms.lib;

import java.util.List;

import com.builtbroken.sbmmobcharms.MobCharmsConfig;
import com.builtbroken.sbmmobcharms.content.charm.ItemCharm;
import com.builtbroken.sbmmobcharms.content.charm.TileEntityCharm;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIAffectedByFollowCharm extends EntityAIBase
{
    private EntityLiving entity;
    private EntityPlayer following;
    private final PathNavigate navigation;

    public EntityAIAffectedByFollowCharm(EntityLiving entity)
    {
        this.entity = entity;
        navigation = entity.getNavigator();
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        if(MobCharmsConfig.enableFollowCharm)
        {
            //loop through all players who have an active charm in their inventory
            for(EntityPlayer player : CharmEffects.PLAYERS_WITH_CHARMS.keySet())
            {
                for(ItemStack stack : CharmEffects.PLAYERS_WITH_CHARMS.get(player).values())
                {
                    if(stack.getItem() instanceof ItemCharm && ((ItemCharm)stack.getItem()).getCharmType() == CharmType.FOLLOW && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                    {
                        boolean isAffected = CharmEffects.getAffectedEntities(new EffectContext(player.world, player.getPosition(), player, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange).contains(entity);

                        if(isAffected)
                            following = player;

                        return isAffected;
                    }
                }
            }

            //loop through all charm tile entities
            for(World world : CharmEffects.CHARM_TILES.keySet())
            {
                for(BlockPos pos : CharmEffects.CHARM_TILES.get(world).keySet())
                {
                    if(CharmEffects.CHARM_TILES.get(world).get(pos) == CharmType.FOLLOW)
                    {
                        List<EntityLivingBase> affected = CharmEffects.getAffectedEntities(new EffectContext(world, pos, null, null, ((TileEntityCharm)world.getTileEntity(pos)).getPower()), MobCharmsConfig.maxCharmRange);
                        boolean isAffected = affected.contains(entity);

                        if(isAffected)
                        {
                            for(EntityLivingBase entity : affected)
                            {
                                if(entity instanceof EntityPlayer)
                                    following = (EntityPlayer)entity;
                            }
                        }

                        return isAffected;
                    }
                }
            }

            //loop through all charm entity items
            for(World world : CharmEffects.CHARM_ENTITIES.keySet())
            {
                for(EntityItem ei : CharmEffects.CHARM_ENTITIES.get(world).keySet())
                {
                    ItemStack stack = ei.getItem();

                    if(CharmEffects.CHARM_ENTITIES.get(world).get(ei) == CharmType.FOLLOW && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                    {
                        List<EntityLivingBase> affected = CharmEffects.getAffectedEntities(new EffectContext(world, ei.getPosition(), null, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange);
                        boolean isAffected = affected.contains(entity);

                        if(isAffected)
                        {
                            for(EntityLivingBase entity : affected)
                            {
                                if(entity instanceof EntityPlayer)
                                    following = (EntityPlayer)entity;
                            }
                        }

                        return isAffected;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        //loop through all players who have an active charm in their inventory
        for(EntityPlayer player : CharmEffects.PLAYERS_WITH_CHARMS.keySet())
        {
            for(ItemStack stack : CharmEffects.PLAYERS_WITH_CHARMS.get(player).values())
            {
                if(stack.getItem() instanceof ItemCharm && ((ItemCharm)stack.getItem()).getCharmType() == CharmType.FOLLOW && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                {
                    boolean isAffected = CharmEffects.getAffectedEntities(new EffectContext(player.world, player.getPosition(), player, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange).contains(entity);

                    if(!isAffected)
                        following = null;

                    return !isAffected;
                }
            }
        }

        //loop through all charm tile entities
        for(World world : CharmEffects.CHARM_TILES.keySet())
        {
            for(BlockPos pos : CharmEffects.CHARM_TILES.get(world).keySet())
            {
                if(CharmEffects.CHARM_TILES.get(world).get(pos) == CharmType.FOLLOW)
                {
                    boolean isAffected = CharmEffects.getAffectedEntities(new EffectContext(world, pos, null, null, ((TileEntityCharm)world.getTileEntity(pos)).getPower()), MobCharmsConfig.maxCharmRange).contains(entity);

                    if(!isAffected)
                        following = null;

                    return !isAffected;
                }
            }
        }

        //loop through all charm entity items
        for(World world : CharmEffects.CHARM_ENTITIES.keySet())
        {
            for(EntityItem ei : CharmEffects.CHARM_ENTITIES.get(world).keySet())
            {
                ItemStack stack = ei.getItem();

                if(CharmEffects.CHARM_ENTITIES.get(world).get(ei) == CharmType.FOLLOW && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                {
                    boolean isAffected = CharmEffects.getAffectedEntities(new EffectContext(world, ei.getPosition(), null, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange).contains(entity);

                    if(!isAffected)
                        following = null;

                    return !isAffected;
                }
            }
        }

        return true;
    }

    @Override
    public void updateTask()
    {
        if(following != null)
            navigation.setPath(navigation.getPathToXYZ(following.getPosition().getX(), following.getPosition().getY(), following.getPosition().getZ()), 1.0F);
    }

    @Override
    public void resetTask()
    {
        following = null;
    }
}
