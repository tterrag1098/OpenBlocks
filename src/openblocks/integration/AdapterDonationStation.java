package openblocks.integration;

import openblocks.common.tileentity.TileEntityDonationStation;
import openperipheral.api.*;

public class AdapterDonationStation implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityDonationStation.class;
	}

	@LuaMethod(onTick = true, returnType = LuaType.STRING, description = "Find the mod name and mod authors")
	public IMultiReturn getItemAuthor(final TileEntityDonationStation station) {
		if (station.getInventory().getStackInSlot(0) == null) return null;
		return OpenPeripheralAPI.wrap(station.getModName().getValue(), station.getAuthors().getValue());
	}
}
