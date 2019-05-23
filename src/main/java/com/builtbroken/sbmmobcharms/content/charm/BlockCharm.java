package com.builtbroken.sbmmobcharms.content.charm;

import com.builtbroken.sbmmobcharms.MobCharms;
import com.builtbroken.sbmmobcharms.lib.CharmType;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid=MobCharms.MODID)
public class BlockCharm extends Block
{
    public static final PropertyEnum<CharmType> CHARM_TYPE = PropertyEnum.create("charm_type", CharmType.class);
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F / 16.0F, 1.0F);

    public BlockCharm()
    {
        super(Material.GLASS);

        setSoundType(SoundType.GLASS);
        setHardness(1.0F);
        setResistance(1.5F);
        setHarvestLevel("pickaxe", 2);
        setDefaultState(blockState.getBaseState().withProperty(CHARM_TYPE, CharmType.ATTACK));
    }

    @SubscribeEvent
    public static void onPlace(PlaceEvent event)
    {
        ItemStack stack = event.getItemInHand();

        if(stack.getItem() instanceof ItemCharm)
        {
            ItemCharm charm = (ItemCharm)stack.getItem();
            EntityPlayer player = event.getPlayer();
            World world = event.getWorld();
            BlockPos pos = event.getPos();

            if(charm.getCharmType() == CharmType.POTION && stack.hasTagCompound() && stack.getTagCompound().hasKey("Potion"))
                ((TileEntityCharm)world.getTileEntity(pos)).setPotionId(stack.getTagCompound().getString("Potion"));

            if(stack.getTagCompound().hasKey("Power"))
                ((TileEntityCharm)world.getTileEntity(pos)).setPower(stack.getTagCompound().getInteger("Power"));

            if(!player.isCreative()) //only remove item if player is not in creative
            {
                if(event.getHand() == EnumHand.OFF_HAND)
                    player.inventory.offHandInventory.set(0, ItemStack.EMPTY);
                else
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return null;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(state.getValue(CHARM_TYPE).toItem()));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(state.getValue(CHARM_TYPE).toItem());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(CHARM_TYPE).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(CHARM_TYPE, CharmType.values()[meta]);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CHARM_TYPE});
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityCharm(state.getValue(CHARM_TYPE));
    }
}
