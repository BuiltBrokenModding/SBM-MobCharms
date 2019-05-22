package com.builtbroken.sbmmobcharms;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;

@Config(modid=MobCharms.MODID)
public class MobCharmsConfig
{
    @Name("maximum_charm_power")
    @Comment("This defines how many mobs this charm can affect in a given range.")
    @RangeInt(min=1)
    public static int maxCharmPower = 20;
    @Name("maximum_char_range")
    @Comment("This defines how far a charm's effect can reach.")
    @RangeInt(min=1)
    public static int maxCharmRange = 10;

    @Name("enable_attack_charm")
    public static boolean enableAttackCharm = true;
    @Name("enable_attack_target_charm")
    public static boolean enableAttackTargetCharm = true;
    @Name("enable_buff_charm")
    public static boolean enableBuffCharm = true;
    @Name("enable_follow_charm")
    public static boolean enableFollowCharm = true;
    @Name("enable_heal_friendly_charm")
    public static boolean enableHealFriendlyCharm = true;
    @Name("enable_no_attack_charm")
    public static boolean enableNoAttackCharm = true;
    @Name("enable_potion_charm")
    public static boolean enablePotionCharm = true;
    @Name("enable_push_charm")
    public static boolean enablePushCharm = true;

    @Name("loot_tables")
    @Comment("Defines in which loot tables the charms will spawn")
    public static String[] lootTables = {
            "minecraft:chests/desert_pyramid",
            "chests/end_city_treasure",
            "minecraft:chests/stronghold_corridor",
            "minecraft:chests/simple_dungeon",
            "minecraft:chests/nether_bridge"
    };
}
