package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.utils.ByteUtils;

public class PlayerActionEvent extends EventPacket {

	public enum Type {
		BOO
	}

	public Type type;

	public PlayerActionEvent() {}

	public PlayerActionEvent(Type type) {
		this.type = type;
	}

	@Override
	public IEventPacketType getType() {
		return EventTypes.PLAYER_ACTION;
	}

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		int typeId = ByteUtils.readVLI(input);
		type = Type.values()[typeId];
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		ByteUtils.writeVLI(output, type.ordinal());
	}

}
