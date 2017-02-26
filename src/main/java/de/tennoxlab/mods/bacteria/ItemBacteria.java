package de.tennoxlab.mods.bacteria;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemBacteria extends Item implements IItemWithName {

	private final String name = "bunch";

	public ItemBacteria() {
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(Bacteria.MODID + "-" + name);

		maxStackSize = 64;
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public String getName() {
		return name;
	}
}