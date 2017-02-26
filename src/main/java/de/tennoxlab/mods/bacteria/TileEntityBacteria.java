package de.tennoxlab.mods.bacteria;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Random;

public class TileEntityBacteria extends TileEntity implements ITickable {

	IBlockState bacteriaBlock;
	ArrayList<Food> food;
	Random rand = new Random();
	int colony;
	boolean jammed;
	int tick = 0;
	boolean startInstantly;

	public TileEntityBacteria() {
		if (food == null)
			food = new ArrayList<Food>();
		bacteriaBlock = Bacteria.bacteria.getDefaultState();
		do
			colony = rand.nextInt();
		while (Bacteria.jamcolonies.contains(Integer.valueOf(colony)));
	}

	@Override
	public void tick() {
		if (worldObj.isRemote)
			return;
		if (Bacteria.jamcolonies.contains(Integer.valueOf(colony)) || Bacteria.jam_all) {
			jammed = true;
			die();
			return;
		}

		if (food.size() == 0) {
			if (worldObj.isBlockIndirectlyGettingPowered(this.getPos()) <= 0)
				return;
			selectFood();
			if (food.size() == 0)
				return;
			if (shouldStartInstantly())
				startInstantly = true;
		}
		if (!startInstantly) {
			if (Bacteria.randomize)
				tick = rand.nextInt(Bacteria.speed + 1);
			if (tick < Bacteria.speed) {
				tick += 1;
				return;
			}
			tick = 0;
		}

		eatEverything();
	}

	public boolean shouldStartInstantly() {
		return true;
	}

	public void selectFood() {
		IBlockState state;
		BlockPos pos = this.getPos().up();

		while ((state = worldObj.getBlockState(pos)).getBlock() != Blocks.AIR) {
			addFood(state);
			pos = pos.up();
		}
	}

	public void addFood(IBlockState state) {
		if (isValidFood(state))
			food.add(new Food(state));
	}

	public static boolean isValidFood(IBlockState state) {
		if (state.getBlock() == Blocks.BEDROCK || state.getBlock() == Bacteria.bacteria)
			return false;
		return true;
	}

	public void eatEverything() {
		maybeEat(getPos().north());
		maybeEat(getPos().south());
		maybeEat(getPos().east());
		maybeEat(getPos().west());
		maybeEat(getPos().up());
		maybeEat(getPos().down());

		die();
	}

	public void maybeEat(BlockPos pos) {
		if (isAtBorder(pos))
			return;
		if (isFood(worldObj.getBlockState(pos))) {
			worldObj.setBlockState(pos, bacteriaBlock);
			((TileEntityBacteria) worldObj.getTileEntity(pos)).food = food;
			((TileEntityBacteria) worldObj.getTileEntity(pos)).colony = colony;
		}
	}

	public boolean isAtBorder(BlockPos pos) { // Block
		while (worldObj.getBlockState(pos) != Block.getBlockFromName(Bacteria.isolation)) {
			if (pos.getY() >= worldObj.getActualHeight())
				return false;
			pos = pos.up(); // TODO: improve by reusing BlockPos instances (maybe MutableBlockPos?)
		}
		return true;
	}

	Food grassFood = new Food(Blocks.GRASS.getDefaultState());
	Food dirtFood = new Food(Blocks.DIRT.getDefaultState());
	Food waterFood = new Food(Blocks.WATER.getDefaultState());
	Food flowingWaterFood = new Food(Blocks.FLOWING_WATER.getDefaultState());

	public boolean isFood(IBlockState state) {
		if (Bacteria.jamcolonies.contains(Integer.valueOf(colony)))
			return false;
		if (state.getBlock() == Bacteria.jammer) {
			Bacteria.jamcolonies.add(Integer.valueOf(colony));

			jammed = true;
			return false;
		}

		for (Food f : Bacteria.blacklist) {
			if (isFood2(f, state))
				return false;
		}

		for (Food f : food) {
			if (isFood2(f, state))
				return true;
		}

		return false;
	}

	/**
	 * checks if the specified blockstate matches the specified food
	 **/
	private boolean isFood2(Food f, IBlockState state) { // TODO: check if BlockState comparison works correctly
		// if (!state.getBlock().equals(f.block))
		// return false;
		// Item item = Item.getItemFromBlock(state.getBlock());
		// if (item != null && !item.getHasSubtypes())
		// return true;

		if (state == f.state)
			return true;

		else if (state == Blocks.DIRT.getDefaultState()) // dirt == grass
			return f.state == Blocks.GRASS.getDefaultState();
		else if (state == Blocks.GRASS.getDefaultState())
			return f.state == Blocks.DIRT.getDefaultState();

		else if (state == Blocks.WATER.getDefaultState()) // water == flowing water
			return f.state == Blocks.FLOWING_WATER.getDefaultState();
		else if (state == Blocks.FLOWING_WATER.getDefaultState())
			return f.state == Blocks.WATER.getDefaultState();

		else if (state == Blocks.LAVA.getDefaultState()) // lava == flowing lava
			return f.state == Blocks.FLOWING_LAVA.getDefaultState();
		else if (state == Blocks.FLOWING_LAVA.getDefaultState())
			return f.state == Blocks.LAVA.getDefaultState();

		return false;
	}

	public void die() {
		worldObj.setBlockToAir(getPos()); // x,y,z
		if (jammed)
			ItemBacteriaJammer.num += 1L;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (food == null)
			food = new ArrayList<Food>();

		colony = nbt.getInteger("colony");
		int i = nbt.getInteger("numfood");

		for (int j = 0; j < i; j++) {
			int id = nbt.getInteger("food" + j);
			int meta = nbt.getInteger("food_meta" + j + "");
			Block b = Block.getBlockById(id);
			food.add(new Food(b.getStateFromMeta(meta)));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("colony", colony);
		nbt.setInteger("numfood", food.size());

		for (int j = 0; j < food.size(); j++) {
			IBlockState bs = food.get(j).state;
			int id = Block.getIdFromBlock(bs.getBlock());

			nbt.setInteger("food" + j, id);
			nbt.setInteger("food_meta" + j, bs.getBlock().getMetaFromState(bs));
		}
		return nbt;
	}
}

class Food {
	IBlockState state;

	public Food(IBlockState state) {
		this.state = state;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Food))
			return false;
		Food f = (Food) o;
		return this.state == f.state; // TODO: check if state comparison works
	}

	public String toString() {
		return String.format("Food[%s]", state);
	}
}