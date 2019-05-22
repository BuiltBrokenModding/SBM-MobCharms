package com.builtbroken.sbmmobcharms.lib;

import com.builtbroken.sbmmobcharms.MobCharmsConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Provides context when applying an effect
 */
public class EffectContext
{
    private final World world;
    private final BlockPos pos;
    private final EntityPlayer player;
    private final Potion potion;
    private final int power;

    /**
     * @param world The world
     * @param pos The position
     * @param player The player, may be null
     * @param potion The potion, may be null
     * @param power The power of the effect in blocks
     */
    public EffectContext(World world, BlockPos pos, EntityPlayer player, Potion potion, int power)
    {
        this.world = world;
        this.pos = pos;
        this.player = player;
        this.potion = potion;
        this.power = power;
    }

    /**
     * @return The world
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * @return The position
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * @return The player, can be null
     */
    public EntityPlayer getPlayer()
    {
        return player;
    }

    /**
     * @return The potion, can be null
     */
    public Potion getPotion()
    {
        return potion;
    }

    /**
     * @return The power in blocks
     */
    public int getPower()
    {
        return power > MobCharmsConfig.maxCharmPower ? MobCharmsConfig.maxCharmPower : power;
    }
}
