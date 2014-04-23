package openblocks.client.renderer.block;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockPath;
import openmods.renderer.IBlockRenderer;
import openmods.tileentity.renderer.OpenRenderHelper;

import org.lwjgl.opengl.GL11;

public class BlockPathRenderer implements IBlockRenderer<BlockPath> {

	private final Random rnd = new Random();

	@Override
	public void renderInventoryBlock(BlockPath block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderWorldBlock(null, 0, 0, 0, OpenBlocks.Blocks.path, -1, renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPath block, int modelId, RenderBlocks renderer) {
		Set<AxisAlignedBB> boundingBoxes = new HashSet<AxisAlignedBB>();
		for (int i = 0; i < 10; i++) {
			rnd.setSeed((x ^ 31) * (y ^ 11) * z ^ (i * 113));
			double width = rnd.nextDouble() * 0.3 + 0.1;
			double length = rnd.nextDouble() * 0.3 + 0.1;
			double pX = rnd.nextDouble() * (1.0 - width);
			double pZ = rnd.nextDouble() * (1.0 - length);
			AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(pX, 0, pZ, pX + width, 1, pZ + length);
			boolean hit = false;
			for (AxisAlignedBB box : boundingBoxes) {
				if (box.intersectsWith(bb)) {
					hit = true;
					break;
				}
			}
			if (!hit) {
				renderer.setRenderBounds(pX, 0, pZ, pX + width, 0.05 * rnd.nextDouble() + 0.02, pZ + length);
				if (modelId == -1) {
					OpenRenderHelper.renderCube(renderer.renderMinX, renderer.renderMinY, renderer.renderMinZ, renderer.renderMaxX, renderer.renderMaxY, renderer.renderMaxZ, block, null);
				} else {
					renderer.renderStandardBlock(block, x, y, z);
				}
				boundingBoxes.add(bb);
			}
		}
		return true;
	}

}
