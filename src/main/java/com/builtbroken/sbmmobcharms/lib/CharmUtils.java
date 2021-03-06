package com.builtbroken.sbmmobcharms.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    public static EntityPlayer getClosestPlayer(CharmEffectContext ctx)
    {
        List<EntityPlayer> players = ctx.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(ctx.getPos()).grow(MobCharmsConfig.maxCharmRange));

        return players.size() > 0 ? players.get(0) : null;
    }

    //the next three methods use iterators to prevent ConcurrentModificationExceptions

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
        for(Iterator<EntityPlayer> playerIterator = CharmEffects.PLAYERS_WITH_CHARMS.keySet().iterator(); playerIterator.hasNext();)
        {
            EntityPlayer player = playerIterator.next();

            if(CharmEffects.PLAYERS_WITH_CHARMS.get(player) != null)
            {
                for(Iterator<ItemStack> stackIterator = CharmEffects.PLAYERS_WITH_CHARMS.get(player).values().iterator(); stackIterator.hasNext();)
                {
                    ItemStack stack = stackIterator.next();

                    //check if prerequisites are met
                    if(stack.getItem() instanceof ItemCharm && ((ItemCharm)stack.getItem()).getCharmType() == type && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                        return function.apply(player, getAffectedEntities(new CharmEffectContext(player.world, player.getPosition(), player, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange));
                }
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
        for(Iterator<World> worldIterator = CharmEffects.CHARM_TILES.keySet().iterator(); worldIterator.hasNext();)
        {
            World world = worldIterator.next();

            if(CharmEffects.CHARM_TILES.get(world) != null)
            {
                for(Iterator<BlockPos> posIterator = CharmEffects.CHARM_TILES.get(world).keySet().iterator(); posIterator.hasNext();)
                {
                    BlockPos pos = posIterator.next();

                    //check if prerequisites are met
                    if(CharmEffects.CHARM_TILES.get(world).get(pos) == type)
                        return function.apply(getAffectedEntities(new CharmEffectContext(world, pos, null, null, ((TileEntityCharm)world.getTileEntity(pos)).getPower()), MobCharmsConfig.maxCharmRange));
                }
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
        for(Iterator<World> worldIterator = CharmEffects.CHARM_TILES.keySet().iterator(); worldIterator.hasNext();)
        {
            World world = worldIterator.next();

            if(CharmEffects.CHARM_ENTITIES.get(world) != null)
            {
                for(Iterator<EntityItem> eiIterator = CharmEffects.CHARM_ENTITIES.get(world).keySet().iterator(); eiIterator.hasNext();)
                {
                    EntityItem ei = eiIterator.next();
                    ItemStack stack = ei.getItem();

                    //check if prerequisites are met
                    if(CharmEffects.CHARM_ENTITIES.get(world).get(ei) == type && stack.hasTagCompound() && stack.getTagCompound().hasKey("Power"))
                        return function.apply(getAffectedEntities(new CharmEffectContext(world, ei.getPosition(), null, null, stack.getTagCompound().getInteger("Power")), MobCharmsConfig.maxCharmRange));
                }
            }
        }

        return defaultReturnValue;
    }

    /**
     * Returns a list of living entities that will affected when applying the effect. This method also weakens the affected range if the original range would affect too many entities
     * @param ctx The context of the effect to apply
     * @param range The range to affect. Starts with the maximum range, then descreases it by one if there are too many mobs in the area. Stops if the range is <= 0
     * @return The entities that will be affected when applying the effect
     */
    public static List<EntityLivingBase> getAffectedEntities(CharmEffectContext ctx, int range)
    {
        if(range <= 0 || ctx == null || ctx.getWorld() == null || ctx.getPos() == null)
            return new ArrayList<>();

        //find all entities in the given range
        List<EntityLivingBase> entitiesToAffect = ctx.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(ctx.getPos()).grow(range));

        if(entitiesToAffect.size() > ctx.getPower()) //if there are too many entities, reduce range and try again
            entitiesToAffect = getAffectedEntities(ctx, range - 1);

        return entitiesToAffect;
    }

    /**
     * Tries to apply a given effect to entities in a specific area
     * @param ctx The context in which the effect should be applied
     * @param effect The effect to apply
     */
    public static void tryApplyEffectToEntities(CharmEffectContext ctx, Consumer<EntityLivingBase> effect)
    {
        for(EntityLivingBase entity : getAffectedEntities(ctx, MobCharmsConfig.maxCharmRange))
        {
            effect.accept(entity);
        }
    }

    /**
     * Tries to apply a given effect to entities in a specific area
     * @param ctx The context in which the effect should be applied
     * @param effect The effect to apply
     */
    public static void tryApplyEffectToEntitiesWithClosestPlayer(CharmEffectContext ctx, BiConsumer<EntityLivingBase,EntityPlayer> effect)
    {
        EntityPlayer closestPlayer = ctx.getPlayer() == null ? getClosestPlayer(ctx) : ctx.getPlayer();

        if(closestPlayer != null)
        {
            for(EntityLivingBase entity : getAffectedEntities(ctx, MobCharmsConfig.maxCharmRange))
            {
                effect.accept(entity, closestPlayer);
            }
        }
    }
}
