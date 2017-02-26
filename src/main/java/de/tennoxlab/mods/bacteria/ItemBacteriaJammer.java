package de.tennoxlab.mods.bacteria;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

@MethodsReturnNonnullByDefault
public class ItemBacteriaJammer extends Item implements IItemWithName {

	public static final String name = "jammeritem";

	int tick;
	static long num;

	public ItemBacteriaJammer() {
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(Bacteria.MODID + "-" + name);
		
		maxStackSize = 64;
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int i, boolean flag) {
		if (tick > 0) {
			tick -= 1;
			if (tick == 0) {
				Bacteria.jam_all = false;
				((EntityPlayer) entity).addChatMessage(new TextComponentString("Jammed " + num + " bacteria!"));
				num = 0L;
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack item, World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote)
			return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
		Bacteria.jam_all = true;
		tick = 30;
		
		player.addStat(Bacteria.jamAchievement, 1);
		player.addChatMessage(new TextComponentString("Jamming bacteria..."));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public String getName() {
		return name;
	}
}