package de.tennoxlab.mods.bacteria;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public class BacteriaCommonProxy {
	public void registerRenderInformation() {
	}

	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(Bacteria.instance);
		MinecraftForge.EVENT_BUS.register(Bacteria.instance);

		// CONFIG //
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		// TODO: remove config option somehow
		config.get("General", "Enable achievements", true).getBoolean(true);

		Bacteria.isolation = config.get("General", "isolation block", "brick_block").getString();
		Bacteria.speed = config.get("General", "bacteria speed", 50).getInt();
		Bacteria.randomize = config.get("General", "randomize bacteria spread", true).getBoolean(true);
		String blacklist1 = config.get("General", "blacklist", "").getString();
		config.save();

		Bacteria.blacklist = new ArrayList<Food>();
		if (blacklist1.length() > 0) {
			for (String s : blacklist1.split(",")) {
				try {
					int meta = 0;
					if (s.contains(":")) {
						String[] s2 = s.split(":");
						s = s2[0];
						meta = Integer.parseInt(s2[1]);
					}

					int id = Integer.parseInt(s);
					Block block = Block.getBlockById(id);
					if (block == Blocks.AIR) {
						Bacteria.logger.error("Error while parsing blacklist: ID " + id + " is not a valid block!");
					} else {
						Bacteria.blacklist.add(new Food(block.getStateFromMeta(meta))); //TODO: blacklist independant of meta
					}
				} catch (NumberFormatException e) {
					Bacteria.logger.error("Error while parsing blacklist: '" + s + "' is not a valid number!");
				}
			}
		}

		// ITEMS //
		Bacteria.bacteriaBunch = new ItemBacteria();
		Bacteria.jammerItem = new ItemBacteriaJammer();
		Bacteria.bacteriaPotion = new ItemBacteriaPotion();

		// BLOCKS //
		Bacteria.bacteria = new BlockBacteria();
		Bacteria.replacer = new BlockBacteriaReplace();
		Bacteria.jammer = new BlockBacteriaJammer();
		Bacteria.must = new BlockMust();
	}

	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ItemStack(Bacteria.jammer, 1), "+#+", "#*#", "+-+",
				Character.valueOf('#'), Bacteria.bacteria, Character.valueOf('*'), Items.IRON_INGOT,
				Character.valueOf('-'), Blocks.REDSTONE_TORCH, Character.valueOf('+'), Blocks.COBBLESTONE);
		GameRegistry.addRecipe(new ItemStack(Bacteria.jammerItem, 1), " # ", "#*#", " - ",
				Character.valueOf('#'), Bacteria.bacteria, Character.valueOf('*'), Items.IRON_INGOT,
				Character.valueOf('-'), Blocks.REDSTONE_TORCH);
		GameRegistry.addRecipe(new ItemStack(Bacteria.bacteria, 1), " # ", "#*#", " # ",
				Character.valueOf('#'), Bacteria.bacteriaBunch, Character.valueOf('*'), Blocks.REDSTONE_TORCH);
		GameRegistry.addRecipe(new ItemStack(Bacteria.replacer, 1), " # ", "#*#", " # ",
				Character.valueOf('#'), Bacteria.bacteriaBunch, Character.valueOf('*'), Items.COAL);
		GameRegistry.addRecipe(new ItemStack(Bacteria.must, 1), "+*+", " # ",
				Character.valueOf('+'), Items.BREAD, Character.valueOf('#'), Items.WATER_BUCKET, Character.valueOf('*'), Blocks.SPONGE);
		GameRegistry.addRecipe(new ItemStack(Blocks.SPONGE, 2), "+*+", "*+*", "+#+",
				Character.valueOf('+'), Blocks.WOOL, Character.valueOf('#'), Items.WATER_BUCKET,
				Character.valueOf('*'), Blocks.YELLOW_FLOWER);

		GameRegistry.addRecipe(new ItemStack(Blocks.SPONGE, 2), "+*+", "*+*", "+#+",
				Character.valueOf('+'), Blocks.WOOL, Character.valueOf('#'), Items.WATER_BUCKET,
				Character.valueOf('*'), Blocks.YELLOW_FLOWER);

		GameRegistry.addShapelessRecipe(new ItemStack(Bacteria.bacteriaPotion, 1), Items.POTIONITEM, Items.NETHER_WART, Bacteria.bacteriaBunch);

		// Achievements //
		Bacteria.mustAchievement = new Achievement("bacteriamod.must", "tennox_bacteria-must", 5, -2, Bacteria.must, AchievementList.BUILD_WORK_BENCH).registerStat();
		Bacteria.bacteriaAchievement = new Achievement("bacteriamod.bacteria", "tennox_bacteria-bacteria", 5, -3, Bacteria.bacteriaBunch, Bacteria.mustAchievement).registerStat();
		Bacteria.bacteriumAchievement = new Achievement("bacteriamod.bacterium", "tennox_bacteria-bacterium", 5, -4, Bacteria.bacteria, Bacteria.bacteriaAchievement)
				.setSpecial().registerStat();
		Bacteria.jamAchievement = new Achievement("bacteriamod.jammer", "tennox_bacteria-jammer", 5, -5, Bacteria.jammerItem, Bacteria.bacteriumAchievement)
			.setSpecial().registerStat();

		GameRegistry.registerTileEntity(TileEntityBacteria.class, "bacteria_tileentity");
		GameRegistry.registerTileEntity(TileEntityBacteriaReplacer.class, "replacer_tileentity");
		GameRegistry.registerWorldGenerator(Bacteria.worldGen, 0);
	}
}