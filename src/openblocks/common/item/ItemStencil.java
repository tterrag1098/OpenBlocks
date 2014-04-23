package openblocks.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.render.PaintUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStencil extends Item {

	public ItemStencil() {
		super(Config.itemStencilId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 0;
	}

	@Override
	public Icon getIconFromDamage(int dmg) {
		return Stencil.values()[dmg].getCoverBlockIcon();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(int id, CreativeTabs par2CreativeTabs, List list) {
		for (Stencil stencil : Stencil.values()) {
			list.add(new ItemStack(id, 1, stencil.ordinal()));
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (PaintUtils.instance.isAllowedToReplace(world, x, y, z)) {
			BlockCanvas.replaceBlock(world, x, y, z);
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			int stencilId = stack.getItemDamage();
			Stencil stencil;
			try {
				stencil = Stencil.VALUES[stencilId];
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}

			if (canvas.useStencil(side, stencil)) stack.stackSize--;
			return true;
		}

		return false;
	}

}
