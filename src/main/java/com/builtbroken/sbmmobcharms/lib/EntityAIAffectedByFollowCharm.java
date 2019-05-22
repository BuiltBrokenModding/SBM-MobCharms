package com.builtbroken.sbmmobcharms.lib;

import com.builtbroken.sbmmobcharms.MobCharmsConfig;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;

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
            return CharmUtils.checkPlayersWithCharms(CharmType.FOLLOW, (player, affected) -> {
                boolean isAffected = affected.contains(entity);

                if(isAffected)
                    following = player;

                return isAffected;
            }, false)
                    || CharmUtils.checkCharmTileEntities(CharmType.FOLLOW, affected -> {
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
                    }, false)
                    || CharmUtils.checkCharmEntityItems(CharmType.FOLLOW, affected -> {
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
                    }, false);
        }
        else return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return CharmUtils.checkPlayersWithCharms(CharmType.FOLLOW, (player, affected) -> {
            boolean isAffected = affected.contains(entity);

            if(!isAffected)
                following = null;

            return !isAffected;
        }, true)
                || CharmUtils.checkCharmTileEntities(CharmType.FOLLOW, affected -> {
                    boolean isAffected = affected.contains(entity);

                    if(!isAffected)
                        following = null;

                    return !isAffected;
                }, true)
                || CharmUtils.checkCharmEntityItems(CharmType.FOLLOW, affected -> {
                    boolean isAffected = affected.contains(entity);

                    if(!isAffected)
                        following = null;

                    return !isAffected;
                }, true);
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
