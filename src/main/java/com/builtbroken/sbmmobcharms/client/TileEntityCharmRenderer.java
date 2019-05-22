package com.builtbroken.sbmmobcharms.client;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.builtbroken.sbmmobcharms.content.charm.TileEntityCharm;
import com.builtbroken.sbmmobcharms.lib.CharmType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityCharmRenderer extends TileEntitySpecialRenderer<TileEntityCharm>
{
    private static final Map<CharmType,ItemStack> STACKS = new HashMap<>();

    static {
        for(CharmType type : CharmType.values())
        {
            STACKS.put(type, new ItemStack(type.toItem()));
        }
    }

    @Override
    public void render(TileEntityCharm te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        ItemStack stack = STACKS.get(te.getCharmType());
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D + te.getXOffset(), y + 0.02D, z + 0.5D + te.getZOffset()); //0.02D to make it level with the ground, 0.05D for middle, then offset by random predefined amount
        GlStateManager.rotate(te.getRotation(), 0, 1.0F, 0); //random rotation
        GlStateManager.rotate(90.0F, 1.0F, 0, 0); //make it lay down
        model = ForgeHooksClient.handleCameraTransforms(model, TransformType.GROUND, false);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
