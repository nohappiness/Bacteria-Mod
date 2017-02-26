package de.tennoxlab.mods.bacteria;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

@MethodsReturnNonnullByDefault
public class BlockBacteria extends BlockContainer implements IBlockWithName {

	public static final String name = "bacteria";

	ArrayList<Integer> food = new ArrayList<Integer>();

	protected BlockBacteria() {
		super(Material.ROCK);
		GameRegistry.registerBlock(this, name); //TODO: correct Block / ItemBlock registry
		setUnlocalizedName(Bacteria.MODID + "-" + name);

		setCreativeTab(CreativeTabs.MISC);
		setHardness(0.07F);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World w, int i) {
		return new TileEntityBacteria();
	}

	@Override
	public String getName() {
		return name;
	}
}