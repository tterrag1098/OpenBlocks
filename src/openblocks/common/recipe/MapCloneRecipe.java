package openblocks.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openmods.utils.ItemUtils;

public class MapCloneRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		int emptyMapScale = -1;
		int normalMapScale = -1;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) continue;

			Item item = stack.getItem();
			if (item instanceof ItemEmptyMap) {
				if (emptyMapScale >= 0) return false;
				NBTTagCompound tag = ItemUtils.getItemTag(stack);
				emptyMapScale = tag.getByte(ItemEmptyMap.TAG_SCALE);
			} else if (item instanceof ItemHeightMap) {
				if (normalMapScale >= 0) return false;
				int mapId = stack.getItemDamage();
				HeightMapData data = MapDataManager.getMapData(world, mapId);

				if (data.isValid()) normalMapScale = data.scale;
				else return false;
			} else return false;

			if (emptyMapScale >= 0 && normalMapScale >= 0) return emptyMapScale == normalMapScale;
		}

		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);
			if (item != null && (item.getItem() instanceof ItemHeightMap)) {
				ItemStack result = item.copy();
				result.stackSize = 2;
				return result;
			}
		}

		return null;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
