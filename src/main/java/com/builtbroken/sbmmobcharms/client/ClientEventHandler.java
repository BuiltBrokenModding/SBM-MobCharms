package com.builtbroken.sbmmobcharms.client;

import com.builtbroken.sbmmobcharms.MobCharms;
import com.builtbroken.sbmmobcharms.content.charm.TileEntityCharm;
import com.builtbroken.sbmmobcharms.lib.CharmType;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid=MobCharms.MODID, value=Side.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        for(CharmType type : CharmType.values())
        {
            ModelLoader.setCustomModelResourceLocation(type.toItem(), 0, new ModelResourceLocation(new ResourceLocation(MobCharms.MODID, "charm"), "inventory"));
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCharm.class, new TileEntityCharmRenderer());
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event)
    {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 1 ? CharmType.fromItem(stack.getItem()).color : -1, MobCharms.attackCharm, MobCharms.attackTargetCharm, MobCharms.buffCharm, MobCharms.followCharm, MobCharms.healFriendlyCharm, MobCharms.noAttackCharm, MobCharms.potionCharm, MobCharms.pushCharm);
    }
}
