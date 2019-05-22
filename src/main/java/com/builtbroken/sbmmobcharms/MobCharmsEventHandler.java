package com.builtbroken.sbmmobcharms;

import java.util.HashMap;
import java.util.Map;

import com.builtbroken.sbmmobcharms.content.charm.ItemCharm;
import com.builtbroken.sbmmobcharms.lib.CharmEffects;
import com.builtbroken.sbmmobcharms.lib.CharmType;
import com.builtbroken.sbmmobcharms.lib.CharmUtils;
import com.builtbroken.sbmmobcharms.lib.EntityAIAffectedByFollowCharm;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid=MobCharms.MODID)
public class MobCharmsEventHandler
{
    @SubscribeEvent
    public static void onLivingAttack(LivingDamageEvent event) //handling for buff charm
    {
        if(MobCharmsConfig.enableBuffCharm)
        {
            //yes, if multiple buff charms are there, the damage will not be different than with just one. this is intended
            //loop through all players who have an active charm in their inventory
            EntityLivingBase attacked = event.getEntityLiving();
            Entity attacker = event.getSource().getTrueSource();
            boolean buffedByCharmOnPlayer = CharmUtils.checkPlayersWithCharms(CharmType.BUFF, (player, affected) -> {
                //if both mobs are buffed, the attacked entity will receive 0.96 times the damage it would have received without the buff
                if(attacker instanceof EntityLivingBase && !(attacker instanceof EntityPlayer) && affected.contains(attacker))
                    event.setAmount(event.getAmount() * 1.2F); //more damage

                if(affected.contains(attacked) && !(attacker instanceof EntityPlayer))
                    event.setAmount(event.getAmount() * 0.8F); //reduce damage

                return true;
            }, false);

            if(buffedByCharmOnPlayer)
                return;

            boolean buffedByCharmTile = CharmUtils.checkCharmTileEntities(CharmType.BUFF, affected -> {
                //if both mobs are buffed, the attacked entity will receive 0.96 times the damage it would have received without the buff
                if(attacker instanceof EntityLivingBase && !(attacker instanceof EntityPlayer) && affected.contains(attacker))
                    event.setAmount(event.getAmount() * 1.2F); //more damage

                if(affected.contains(attacked) && !(attacker instanceof EntityPlayer))
                    event.setAmount(event.getAmount() * 0.8F); //reduce damage

                return true;
            }, false);

            if(buffedByCharmTile)
                return;

            boolean buffedByCharmEntityItem = CharmUtils.checkCharmTileEntities(CharmType.BUFF, affected -> {
                //if both mobs are buffed, the attacked entity will receive 0.96 times the damage it would have received without the buff
                if(attacker instanceof EntityLivingBase && !(attacker instanceof EntityPlayer) && affected.contains(attacker))
                    event.setAmount(event.getAmount() * 1.2F); //more damage

                if(affected.contains(attacked) && !(attacker instanceof EntityPlayer))
                    event.setAmount(event.getAmount() * 0.8F); //reduce damage

                return true;
            }, false);

            if(buffedByCharmEntityItem)
                return;
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if(event.getEntity() instanceof EntityLiving)
            ((EntityLiving)event.getEntity()).tasks.addTask(10, new EntityAIAffectedByFollowCharm((EntityLiving)event.getEntity()));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event)
    {
        if(event.side == Side.SERVER && event.phase == Phase.START)
        {
            EntityPlayer player = event.player;
            Map<Integer,ItemStack> activeCharms = CharmEffects.PLAYERS_WITH_CHARMS.get(player);

            if(activeCharms == null)
                activeCharms = new HashMap<>();

            for(int i = 0; i < 9; i++) //loop through hotbar, offhand slot is 0 as well
            {
                ItemStack stack = player.inventory.getStackInSlot(i);

                if(stack.getItem() instanceof ItemCharm)
                    activeCharms.put(i, stack);
                else
                    activeCharms.remove(i);
            }

            if(Loader.isModLoaded("baubles"))
            {
                ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.CHARM.getValidSlots()[0]); //only one charm slot

                if(stack.getItem() instanceof ItemCharm)
                    activeCharms.put(10, stack); //10 is defined as the baubles slot here since 0-9 are already from the hotbar
                else
                    activeCharms.remove(10);
            }

            CharmEffects.PLAYERS_WITH_CHARMS.put(player, activeCharms);
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event)
    {
        EntityItem ei = event.getEntityItem();

        if(ei.getItem().getItem() instanceof ItemCharm)
            CharmEffects.addEntityItemCharm(ei.world, ei, ((ItemCharm)ei.getItem().getItem()).getCharmType());
    }

    @SubscribeEvent
    public static void onItemPickup(ItemPickupEvent event)
    {
        EntityItem ei = event.getOriginalEntity();

        if(ei.getItem().getItem() instanceof ItemCharm)
            CharmEffects.removeEntityItemCharm(ei.world, ei);
    }

    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event)
    {
        EntityItem ei = event.getEntityItem();

        if(ei.getItem().getItem() instanceof ItemCharm)
            CharmEffects.removeEntityItemCharm(ei.world, ei);
    }
}
