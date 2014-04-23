package openblocks.client.gui;

import openblocks.common.container.ContainerLuggage;
import openmods.gui.BaseGuiContainer;

public class GuiLuggage extends BaseGuiContainer<ContainerLuggage> {

	public GuiLuggage(ContainerLuggage container) {
		super(container, 176, container.getOwner().isSpecial()? 221 : 167, "openblocks.gui.luggage");
	}

}
