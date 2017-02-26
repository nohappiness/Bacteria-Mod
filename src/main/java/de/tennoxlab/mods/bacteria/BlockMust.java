package de.tennoxlab.mods.bacteria;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class BlockMust extends Block implements IBlockWithName {
	private final String name = "must";

	public static final int MAX_GROW_INDEX = 7;
	public static final PropertyInteger GROWN = PropertyInteger.create("grown", 0, MAX_GROW_INDEX);

	public int tick;
	public boolean drop = true;

	protected BlockMust() {
		super(Material.SPONGE);
		GameRegistry.registerBlock(this, name);
		setUnlocalizedName(Bacteria.MODID + "-" + name);

		setCreativeTab(CreativeTabs.MISC);
		setHardness(0.6F);
		setTickRandomly(true);
		setSoundType(SoundType.GROUND);
		setDefaultState(blockState.getBaseState().withProperty(GROWN, 0));
	}

	// BlockCrops
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote) {
			Block above = world.getBlockState(pos.up()).getBlock();
			int meta = (Integer) state.getValue(GROWN);

			if (meta >= MAX_GROW_INDEX)
				return; // already fully grown

			if (above == Blocks.WATER)
				meta++;
			else if (above == Blocks.FLOWING_WATER && rand.nextInt(3) >= 1) // it grows slower under flowing water (~2/3 of the speed)
				meta++;
			if (above != Blocks.FLOWING_WATER && above != Blocks.WATER)
				meta = 0;
			Bacteria.logger.info("Must at " + pos + " updates meta from " + state.getValue(GROWN) + " to " + meta);
			world.setBlockState(pos, blockState.getBaseState().withProperty(GROWN, meta));
		}
	}

	@Override
	public int quantityDropped(Random random) {
		boolean prevDrop = drop;
		drop = true;
		return prevDrop ? 1 : 0;
	}

	// Block
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if ((Integer) state.getValue(GROWN) >= MAX_GROW_INDEX)
			return Bacteria.bacteriaBunch;
		return Item.getItemFromBlock(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, GROWN);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (Integer) state.getValue(GROWN);
	}

	@Override //TODO:
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(GROWN, meta);
	}

	@Override
	public String getName() {
		return name;
	}
}