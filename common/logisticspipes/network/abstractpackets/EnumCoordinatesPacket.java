package logisticspipes.network.abstractpackets;

import lombok.Getter;
import lombok.Setter;

import network.rs485.logisticspipes.util.LPDataInput;
import network.rs485.logisticspipes.util.LPDataOutput;

public abstract class EnumCoordinatesPacket<E extends Enum<E>> extends CoordinatesPacket {

	@Getter
	@Setter
	public E enumValue;

	public EnumCoordinatesPacket(int id) {
		super(id);
	}

	@Override
	public void writeData(LPDataOutput output) {
		super.writeData(output);
		output.writeEnum(enumValue);
	}

	@Override
	public void readData(LPDataInput input) {
		super.readData(input);
		enumValue = input.readEnum(getEnumClass());
	}

	public abstract Class<E> getEnumClass();
}
