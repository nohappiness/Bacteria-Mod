package de.tennoxlab.mods.bacteria;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockBacteriaReplace extends BlockContainer implements IBlockWithName {
	private static final String name = "replacer";

	protected BlockBacteriaReplace() {
		super(Material.ROCK);
		GameRegistry.registerBlock(this, name);
		setUnlocalizedName(Bacteria.MODID + "-" + name);

		setCreativeTab(CreativeTabs.MISC);
		setHardness(0.07F);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World w, int i) {
		return new TileEntityBacteriaReplacer();
	}

	@Override
	public String getName() {
		return name;
	}
}