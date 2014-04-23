package openblocks.enchantments;

import java.util.*;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import openblocks.Config;
import openblocks.OpenBlocks.Enchantments;
import openblocks.api.FlimFlamRegistry;
import openblocks.api.IFlimFlamEffect;
import openmods.Log;
import openmods.config.ConfigurationChange;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FlimFlamEnchantmentsHandler {

	public static final String LUCK_PROPERTY = "OpenBlocks-Luck";

	public static final int LUCK_MARGIN = 5;

	public static final int EFFECT_DELAY = 20 * 15; // 15s cooldown

	private static final Random random = new Random();

	private static Set<String> blacklist;

	private static Set<String> getBlacklist() {
		if (blacklist == null) {
			blacklist = Sets.newHashSet();
			Set<String> validNames = Sets.newHashSet(FlimFlamRegistry.getAllFlimFlamsNames());
			for (String s : Config.flimFlamBlacklist) {
				if (validNames.contains(s)) blacklist.add(s);
				else Log.warn("Trying to blacklist unknown flimflam name '%s'", s);
			}
		}

		return blacklist;
	}

	@ForgeSubscribe
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.check("tomfoolery", "flimFlamBlacklist")) blacklist = null;
	}

	private static class Luck implements IExtendedEntityProperties {

		public int luck;

		public int cooldown;

		public boolean forceNext;

		@Override
		public void saveNBTData(NBTTagCompound entityTag) {
			entityTag.setInteger(LUCK_PROPERTY, luck);
		}

		@Override
		public void loadNBTData(NBTTagCompound entityTag) {
			luck = entityTag.getInteger(LUCK_PROPERTY);
		}

		@Override
		public void init(Entity entity, World world) {}
	}

	private static Luck getProperty(Entity entity) {
		IExtendedEntityProperties prop = entity.getExtendedProperties(LUCK_PROPERTY);
		return (prop instanceof Luck)? (Luck)prop : null;
	}

	@ForgeSubscribe
	public void onEntityConstruct(EntityEvent.EntityConstructing evt) {
		if (evt.entity instanceof EntityPlayer) evt.entity.registerExtendedProperties(LUCK_PROPERTY, new Luck());
	}

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		if (!(e.entityLiving instanceof EntityPlayer)) return;
		if (e.entityLiving.worldObj.isRemote) return;

		final EntityPlayer targetPlayer = (EntityPlayer)e.entityLiving;

		if (e.source == null) return;
		final Entity damageSource = e.source.getEntity();

		if (!(damageSource instanceof EntityPlayer)) return;
		final EntityPlayer sourcePlayer = (EntityPlayer)damageSource;

		// flim flam yerself?
		if (sourcePlayer == targetPlayer) return;

		final int sourceFlimFlam = getFlimFlamToolLevel(sourcePlayer);
		final int targetFlimFlam = getFlimFlamArmorLevel(targetPlayer);

		Luck targetLuck = getProperty(e.entityLiving);
		if (targetLuck != null) {
			targetLuck.luck -= calculateWeaponLuckChange(sourceFlimFlam);
			if (targetLuck.luck < LUCK_MARGIN) targetLuck.forceNext = true;
		}

		Luck sourceLuck = getProperty(sourcePlayer);
		if (sourceLuck != null) {
			sourceLuck.luck -= calculateArmorLuckChange(targetFlimFlam);
			if (sourceLuck.luck < LUCK_MARGIN) sourceLuck.forceNext = true;
		}
	}

	private static int calculateWeaponLuckChange(int sourceFlimFlam) {
		return 10 * sourceFlimFlam;
	}

	private static int calculateArmorLuckChange(int sourceFlimFlam) {
		return 5 * sourceFlimFlam;
	}

	public static void deliverKarma(EntityPlayerMP player) {
		if (player.isDead) return;
		Luck property = getProperty(player);
		if (property == null || !canFlimFlam(property)) return;
		final int luck = property.luck;

		int totalWeight = 0;
		List<IFlimFlamEffect> selectedEffects = Lists.newArrayList();
		Set<String> blacklist = getBlacklist();
		for (IFlimFlamEffect effectMeta : FlimFlamRegistry.getFlimFlams())
			if (effectMeta.canApply(luck) && (!Config.safeFlimFlams || effectMeta.isSafe()) && !blacklist.contains(effectMeta.name())) {
				selectedEffects.add(effectMeta);
				totalWeight += effectMeta.weight();
			}

		if (selectedEffects.isEmpty()) return;

		Collections.shuffle(selectedEffects);

		while (!selectedEffects.isEmpty()) {
			final int selectedWeight = random.nextInt(totalWeight);
			int currentWeight = 0;
			Iterator<IFlimFlamEffect> it = selectedEffects.iterator();

			while (it.hasNext()) {
				final IFlimFlamEffect effectMeta = it.next();
				currentWeight += effectMeta.weight();
				if (selectedWeight <= currentWeight) {
					try {
						if (effectMeta.action().execute(player)) {
							property.luck -= effectMeta.cost();
							Log.fine("Player %s flim-flammed with %s, current luck: %s", player, effectMeta.name(), property.luck);
							if (!effectMeta.isSilent()) player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("openblocks.flim_flammed"));
							return;
						}
					} catch (Throwable t) {
						Log.warn(t, "Error during flimflam '%s' execution", effectMeta.name());
					}
					totalWeight -= effectMeta.weight();
					it.remove();
					break;
				}
			}
		}
	}

	public static int getLuck(EntityPlayer player) {
		Luck property = getProperty(player);
		return property != null? property.luck : 0;
	}

	public static int modifyLuck(EntityPlayer player, int amount) {
		Luck property = getProperty(player);
		if (property == null) return 0;
		property.luck += amount;
		return property.luck;
	}

	private static boolean canFlimFlam(Luck property) {
		if (property.forceNext) {
			property.forceNext = false;
			property.cooldown = EFFECT_DELAY;
			return true;
		}

		if (property.luck > -LUCK_MARGIN || property.cooldown-- > 0) return false;
		property.cooldown = EFFECT_DELAY;
		double probability = 0.75 * 2.0 * Math.abs(Math.atan(property.luck / 250.0) / Math.PI);
		double r = random.nextDouble();
		return r < probability;
	}

	private static int getFlimFlamToolLevel(EntityPlayer player) {
		return getFlimFlamLevel(player.getHeldItem());
	}

	private static int getFlimFlamArmorLevel(EntityPlayer player) {
		int sum = 0;
		for (ItemStack stack : player.inventory.armorInventory) {
			sum += getFlimFlamLevel(stack);
		}
		return sum;
	}

	@SuppressWarnings("unchecked")
	private static int getFlimFlamLevel(ItemStack stack) {
		if (stack == null) return 0;
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		Integer result = enchantments.get(Enchantments.flimFlam.effectId);
		return result != null? result : 0;
	}
}
