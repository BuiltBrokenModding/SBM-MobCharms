package com.builtbroken.sbmmobcharms.content.charm;

import java.util.Random;

import com.builtbroken.sbmmobcharms.lib.CharmEffects;
import com.builtbroken.sbmmobcharms.lib.CharmType;
import com.builtbroken.sbmmobcharms.lib.CharmEffectContext;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TileEntityCharm extends TileEntity implements ITickable
{
    private final Random r = new Random();
    private CharmType charmType;
    private String potionId = null;
    private int power = 0;
    //for rendering
    private int rotation = r.nextInt(360);
    //for rendering
    private double xOffset = Math.abs(r.nextDouble() - 0.5D) - 0.25D; //1. double from 0-1 2. make it range from -0.5 to 0.5 3. take the absolute value 4. make that range from -0.25 to 0.25
    //for rendering
    private double zOffset = Math.abs(r.nextDouble() - 0.5D) - 0.25D;

    public TileEntityCharm() {}

    public TileEntityCharm(CharmType charmType)
    {
        this.charmType = charmType;
    }

    @Override
    public void update()
    {
        if(!world.isRemote && charmType != null)
            charmType.apply(new CharmEffectContext(world, pos, null, getPotion(), power));
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        //the charm type is not set early enough so update the client with the correct one
        //also synchronize the exact render placement of the charm
        if(world.isRemote)
            world.markBlockRangeForRenderUpdate(pos, pos);
        else
            CharmEffects.addCharmTile(getWorld(), getPos(), getCharmType());
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        if(!world.isRemote)
            CharmEffects.removeCharmTile(getWorld(), getPos());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        charmType = CharmType.values()[tag.getInteger("CharmType")];

        if(tag.hasKey("Potion"))
            potionId = tag.getString("Potion");

        power = tag.getInteger("Power");
        rotation = tag.getInteger("Rotation");
        xOffset = tag.getDouble("OffsetX");
        zOffset = tag.getDouble("OffsetZ");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("CharmType", charmType.ordinal());

        if(potionId != null)
            tag.setString("Potion", potionId);

        tag.setInteger("Power", power);
        tag.setInteger("Rotation", rotation);
        tag.setDouble("OffsetX", xOffset);
        tag.setDouble("OffsetZ", zOffset);
        return super.writeToNBT(tag);
    }

    /**
     * @return The charm type of this tile entity's block
     */
    public CharmType getCharmType()
    {
        return charmType;
    }

    /**
     * Sets this tile entity's potion id
     * @param potionId The id of the potion that this tile entity has
     */
    public void setPotionId(String potionId)
    {
        this.potionId = potionId;
        markDirty();
    }

    /**
     * @return The potion this tile entity has, null if the charm type of this tile entity is not potion
     */
    public Potion getPotion()
    {
        if(charmType == CharmType.POTION && potionId != null)
            return ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionId));
        else return null;
    }

    public String getPotionId()
    {
        return potionId;
    }

    /**
     * Sets this charm's power
     * @param power The power in blocks
     */
    public void setPower(int power)
    {
        this.power = power;
        markDirty();
    }

    /**
     * @return This charm's power in blocks
     */
    public int getPower()
    {
        return power;
    }

    /**
     * @return The random rotation of this tile entity for rendering
     */
    public int getRotation()
    {
        return rotation;
    }

    /**
     * @return The random offset in the x direction of this tile entity for rendering
     */
    public double getXOffset()
    {
        return xOffset;
    }

    /**
     * @return The random offset in the z direction of this tile entity for rendering
     */
    public double getZOffset()
    {
        return zOffset;
    }
}
