package openblocks.client.model;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import openblocks.api.IMutantDefinition;
import openblocks.api.IMutantRenderer;
import openblocks.common.entity.EntityMutant;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMutant extends ModelBase {

	public HashMap<IMutantDefinition, IMutantRenderer> rendererCache = Maps.newHashMap();

	public ModelMutant() {

	}

	private IMutantRenderer getRenderer(IMutantDefinition definition) {
		IMutantRenderer renderer = rendererCache.get(definition);
		if (renderer == null) {
			renderer = definition.createRenderer();
			renderer.initialize(this);
			rendererCache.put(definition, renderer);
		}
		return renderer;
	}

	@Override
	public void render(Entity entity, float legSwing, float prevLegSwing,
			float wingSwing, float yaw, float pitch, float scale) {

		EntityMutant mutant = (EntityMutant)entity;

		IMutantDefinition head = mutant.getHead();
		IMutantDefinition body = mutant.getBody();
		IMutantDefinition arms = mutant.getArms();
		IMutantDefinition legs = mutant.getLegs();
		IMutantDefinition wings = mutant.getWings();
		IMutantDefinition tail = mutant.getTail();

		GL11.glPushMatrix();
		if (mutant.isChild()) {
			GL11.glTranslated(0, 0.5, 0);
		}
		if (head != null) {
			getRenderer(head).renderHead(mutant, scale, yaw, pitch);
		}

		GL11.glPushMatrix();
		if (mutant.isChild()) {
			GL11.glScaled(0.6, 0.6, 0.6);
		}
		if (body != null) {
			getRenderer(body).renderBody(mutant, scale);
		}

		if (arms != null) {
			getRenderer(arms).renderArms(mutant, scale, legSwing, prevLegSwing);
		}

		if (legs != null) {
			getRenderer(legs).renderLegs(mutant, scale, legSwing, prevLegSwing);
		}

		if (wings != null) {
			getRenderer(wings).renderWings(mutant, scale, wingSwing);
		}

		if (tail != null) {
			getRenderer(tail).renderTail(mutant, scale, legSwing, prevLegSwing);
		}

		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

	public void _setTextureOffset(String par1Str, int par2, int par3) {
		setTextureOffset(par1Str, par2, par3);
	}

}