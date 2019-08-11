package roito.afterthedrizzle.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import roito.afterthedrizzle.AfterTheDrizzle;
import roito.afterthedrizzle.api.block.IBlockStove;
import roito.afterthedrizzle.common.tileentity.TileEntityStove;
import roito.afterthedrizzle.registry.GuiElementsRegistry;

import java.util.Random;

public abstract class BlockStove extends BlockHorizontal implements IBlockStove
{
    protected int efficiency;
    protected static boolean keepInventory = false;

    public BlockStove(boolean light, int efficiency, Material material)
    {
        super(material);
        if (light)
        {
            this.setLightLevel(1.0F);
        }
        else
        {
            this.setCreativeTab(AfterTheDrizzle.TAB_CRAFT);
        }
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.efficiency = efficiency;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isBurning()
    {
        return this.lightValue != 0;
    }

    @Override
    public int getFuelPower()
    {
        return efficiency;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityStove();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!keepInventory)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null)
            {
                IItemHandler fuelInventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

                for (int i = fuelInventory.getSlots() - 1; i >= 0; --i)
                {
                    if (fuelInventory.getStackInSlot(i) != ItemStack.EMPTY)
                    {
                        Block.spawnAsEntity(worldIn, pos, fuelInventory.getStackInSlot(i));
                        ((IItemHandlerModifiable) fuelInventory).setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isSneaking())
        {
            int id = GuiElementsRegistry.GUI_STOVE;
            playerIn.openGui(AfterTheDrizzle.getInstance(), id, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        else
        {
            if (playerIn.getHeldItem(hand).getItem().equals(Items.FLINT_AND_STEEL))
            {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof TileEntityStove)
                {
                    ((TileEntityStove) te).setToLit();
                    playerIn.getHeldItem(hand).damageItem(1, playerIn);
                }
                return true;
            }
            else if (!playerIn.getHeldItem(hand).isEmpty())
            {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof TileEntityStove)
                {
                    IItemHandler fuel = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                    playerIn.setHeldItem(hand, fuel.insertItem(0, playerIn.getHeldItem(hand), false));
                    ((TileEntityStove) te).mark();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (isBurning())
        {
            double d0 = pos.getX() + 0.5D;
            double d1 = pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = pos.getZ() + 0.5D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1 + 1.0D, d2 + d4, 0.0D, 0.1D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d4, 0.0D, 0.06D, 0.0D);
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState origin = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        return origin.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta);
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(FACING).getHorizontalIndex();
        return facing;
    }

    public static void setState(boolean active, World worldIn, BlockPos pos, IBlockStove stove)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;

        if (active)
        {
            worldIn.setBlockState(pos, stove.getLit().getDefaultState().withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING)));
        }
        else
        {
            worldIn.setBlockState(pos, stove.getUnlit().getDefaultState().withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING)));
        }

        keepInventory = false;

        if (tileentity != null)
        {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(getUnlit());
    }
}