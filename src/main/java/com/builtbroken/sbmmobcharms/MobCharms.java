package com.builtbroken.sbmmobcharms;

import java.util.Random;

import com.builtbroken.sbmmobcharms.content.charm.BlockCharm;
import com.builtbroken.sbmmobcharms.content.charm.ItemCharm;
import com.builtbroken.sbmmobcharms.content.charm.ItemPotionCharm;
import com.builtbroken.sbmmobcharms.content.charm.TileEntityCharm;
import com.builtbroken.sbmmobcharms.lib.CharmType;
import com.builtbroken.sbmmobcharms.lib.LootFunctionSetCharmNBT;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid=MobCharms.MODID, name=MobCharms.NAME, version=MobCharms.VERSION, acceptedMinecraftVersions=MobCharms.MC_VERSION)
@EventBusSubscriber
public class MobCharms
{
    public static final String MODID = "sbmmobcharms";
    public static final String NAME = "[SBM] Mob Charms";
    public static final String VERSION = ""; //TODO
    public static final String MC_VERSION = "1.12";
    public static final String PREFIX = MODID + ":";
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID + "_tab") {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(CharmType.BUFF.toItem());
        }
    };
    public static final String ATTACK_ID = "attack_charm";
    public static final String ATTACK_TARGET_ID = "attack_target_charm";
    public static final String BUFF_ID = "buff_charm";
    public static final String FOLLOW_ID = "follow_charm";
    public static final String HEAL_FRIENDLY_ID = "heal_friendly_charm";
    public static final String NO_ATTACK_ID = "no_attack_charm";
    public static final String POTION_ID = "potion_charm";
    public static final String PUSH_ID = "push_charm";
    @ObjectHolder(PREFIX + "charm_block")
    public static Block charmBlock;
    @ObjectHolder(PREFIX + ATTACK_ID)
    public static Item attackCharm;
    @ObjectHolder(PREFIX + ATTACK_TARGET_ID)
    public static Item attackTargetCharm;
    @ObjectHolder(PREFIX + BUFF_ID)
    public static Item buffCharm;
    @ObjectHolder(PREFIX + FOLLOW_ID)
    public static Item followCharm;
    @ObjectHolder(PREFIX + HEAL_FRIENDLY_ID)
    public static Item healFriendlyCharm;
    @ObjectHolder(PREFIX + NO_ATTACK_ID)
    public static Item noAttackCharm;
    @ObjectHolder(PREFIX + POTION_ID)
    public static Item potionCharm;
    @ObjectHolder(PREFIX + PUSH_ID)
    public static Item pushCharm;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockCharm().setRegistryName(new ResourceLocation(MODID, "charm_block")));
        GameRegistry.registerTileEntity(TileEntityCharm.class, new ResourceLocation(MODID, "charm_block"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemCharm(CharmType.ATTACK).setRegistryName(new ResourceLocation(MODID, ATTACK_ID)));
        event.getRegistry().register(new ItemCharm(CharmType.ATTACK_TARGET).setRegistryName(new ResourceLocation(MODID, ATTACK_TARGET_ID)));
        event.getRegistry().register(new ItemCharm(CharmType.BUFF).setRegistryName(new ResourceLocation(MODID, BUFF_ID)));
        event.getRegistry().register(new ItemCharm(CharmType.FOLLOW).setRegistryName(new ResourceLocation(MODID, FOLLOW_ID)));
        event.getRegistry().register(new ItemCharm(CharmType.HEAL_FRIENDLY).setRegistryName(new ResourceLocation(MODID, HEAL_FRIENDLY_ID)));
        event.getRegistry().register(new ItemCharm(CharmType.NO_ATTACK).setRegistryName(new ResourceLocation(MODID, NO_ATTACK_ID)));
        event.getRegistry().register(new ItemPotionCharm().setRegistryName(new ResourceLocation(MODID, POTION_ID)));
        event.getRegistry().register(new ItemCharm(CharmType.PUSH).setRegistryName(new ResourceLocation(MODID, PUSH_ID)));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        for(String s : MobCharmsConfig.lootTables)
        {
            if(event.getName().toString().equals(s))
            {
                LootPool pool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(0, 1), new RandomValueRange(0, 0), PREFIX + "charm_pool");

                for(CharmType type : CharmType.values())
                {
                    pool.addEntry(new LootEntryItem(type.toItem(), 1, 0, new LootFunction[]{new LootFunctionSetCharmNBT(type == CharmType.POTION)}, new LootCondition[0], type.getName()));
                }

                event.getTable().addPool(pool);
            }
        }
    }

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
}
