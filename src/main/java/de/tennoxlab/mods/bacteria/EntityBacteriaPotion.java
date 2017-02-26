package de.tennoxlab.mods.bacteria;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

class EntityBacteriaPotion extends EntityPotion {

	public EntityBacteriaPotion(World world) {
		super(world);
	}

	public EntityBacteriaPotion(World world, EntityPlayer player, ItemStack itemstack) {
		super(world, player, itemstack);
	}

	@Override
	protected void onImpact(RayTraceResult objpos) { //TODO: adapt
		BlockPos pos = objpos.getBlockPos();
		if (objpos.typeOfHit == RayTraceResult.Type.BLOCK) { // RenderPotion
			IBlockState state = worldObj.getBlockState(pos);

			if (TileEntityBacteria.isValidFood(state)) {
				worldObj.setBlockState(pos, Bacteria.bacteria.getDefaultState());
				TileEntity t = worldObj.getTileEntity(pos);

				if (t != null && t instanceof TileEntityBacteria) {
					TileEntityBacteria tile = (TileEntityBacteria) t;
					tile.addFood(state);
					if (tile.shouldStartInstantly())
						tile.startInstantly = true;
				}
			}

			this.worldObj.playEvent(2002, this.getPosition(), PotionType.getID(potiontype));
			this.setDead();
		}
	}
}