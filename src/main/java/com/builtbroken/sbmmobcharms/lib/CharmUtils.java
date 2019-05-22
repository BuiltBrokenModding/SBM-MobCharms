package com.builtbroken.sbmmobcharms.lib;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.builtbroken.sbmmobcharms.MobCharmsConfig;
import com.builtbroken.sbmmobcharms.content.charm.ItemCharm;
import com.builtbroken.sbmmobcharms.content.charm.TileEntityCharm;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CharmUtils
{
    /**
     * Creates an NBT that contains an integer (Power) for a charm's power and, if wanted, a potion id (Potion) for a potion charm.
     * This tag can be used as a stack tag
     * @param applyPotionType Whether or not to set a random potion id on the tag
     * @param rand The random number generator
     * @return An NBT that contains the applicable data
     */
    public static NBTTagCompound getRandomizedCharmTag(boolean applyPotionType, Random rand)
    {
        NBTTagCompound randomizedTag = new NBTTagCompound();

        randomizedTag.setInteger("Power", rand.nextInt(MobCharmsConfig.maxCharmPower) + 1); //+1 to get a result from 1 to maxCharmPower (both inclusive)

        if(applyPotionType)
        {
            int potionAmount = ForgeRegistries.POTIONS.getKeys().size();
            String potion = ForgeRegistries.POTIONS.getKeys().toArray(new ResourceLocation[potionAmount])[rand.nextInt(potionAmount)].toString();

            randomizedTag.setString("Potion", potion);
        }

        return randomizedTag;
    }

    /**
     * Returns the player that is closest to the position of the given effect context
     * @param ctx The effect context
     * @return The closest player, null if no player has been found
     */
    public static EntityPlayer getClosestPlayer(EffectContext ctx)
    {
        List<EntityPlayer> players = ctx.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(ctx.getPos()).grow(MobCharmsConfig.maxCharmRange));

        return players.size() > 0 ? players.get(0) : null;
    }

    /**
     * Loops through all known charms that are in players' inventories and applies the given function for the first one of the given type that was found
     * @param type The type to check for
     * @param function The function to execute. The argument is the list of affected entities
     * @param defaultReturnValue The return value of this function if function wasn't applied once
     * @return defaultReturnValue if the function was not applied once, otherwhise the return value of the function
     */
    public static <T> T checkPlayersWithCharms(CharmType type, BiFunction<EntityPlayer,List<EntityLivingBase>,T> function, T defaultReturnValue)
    {
        //loop through all players who have an active charm in their inventory
        for(EntityPlayer player : CharmEffects.PLAYERS_WITH_CHARMS.keySet())
        {
            for(ItemStack stack : CharmEffects.PLAYERS_WITH_CHARMS.get(player).values())
            {
                //check if prerequisites are met
                if(stack.getItem() instanceof ItemCharm && ((ItemCharm)stack.getItem()).getCharmType() == type && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                    return function.apply(player, CharmEffects.getAffectedEntities(new EffectContext(player.world, player.getPosition(), player, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange));
            }
        }

        return defaultReturnValue;
    }

    /**
     * Loops through all known charms that are placed down in the world and applies the given function for the first one of the given type that was found
     * @param type The type to check for
     * @param function The function to execute. The argument is the player who holds the charm and the list of affected entities
     * @param defaultReturnValue The return value of this function if function wasn't applied once
     * @return defaultReturnValue if the function was not applied once, otherwhise the return value of the function
     */
    public static <T> T checkCharmTileEntities(CharmType type, Function<List<EntityLivingBase>,T> function, T defaultReturnValue)
    {
        //loop through all charm tile entities
        for(World world : CharmEffects.CHARM_TILES.keySet())
        {
            for(BlockPos pos : CharmEffects.CHARM_TILES.get(world).keySet())
            {
                //check if prerequisites are met
                if(CharmEffects.CHARM_TILES.get(world).get(pos) == type)
                    return function.apply(CharmEffects.getAffectedEntities(new EffectContext(world, pos, null, null, ((TileEntityCharm)world.getTileEntity(pos)).getPower()), MobCharmsConfig.maxCharmRange));
            }
        }

        return defaultReturnValue;
    }

    /**
     * Loops through all known charms that are dropped in the world and applies the given function for the first one of the given type that was found
     * @param type The type to check for
     * @param function The function to execute. The argument is the list of affected entities
     * @param defaultReturnValue The return value of this function if function wasn't applied once
     * @return defaultReturnValue if the function was not applied once, otherwhise the return value of the function
     */
    public static <T> T checkCharmEntityItems(CharmType type, Function<List<EntityLivingBase>,T> function, T defaultReturnValue)
    {
        //loop through all charm entity items
        for(World world : CharmEffects.CHARM_ENTITIES.keySet())
        {
            for(EntityItem ei : CharmEffects.CHARM_ENTITIES.get(world).keySet())
            {
                ItemStack stack = ei.getItem();

                //check if prerequisites are met
                if(CharmEffects.CHARM_ENTITIES.get(world).get(ei) == type && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                    return function.apply(CharmEffects.getAffectedEntities(new EffectContext(world, ei.getPosition(), null, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange));
            }
        }

        return defaultReturnValue;
    }
}
