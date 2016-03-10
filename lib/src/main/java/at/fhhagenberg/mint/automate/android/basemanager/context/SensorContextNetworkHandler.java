/*
 *     Copyright (C) 2016 Mobile Interactive Systems Research Group
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.fhhagenberg.mint.automate.android.basemanager.context;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.ClientNetworkHandler;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.packet.GenericPacket;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.thrift.ThriftHelper;
import at.fhhagenberg.mint.automate.android.basemanager.context.battery.event.BatteryInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.context.light.event.LightConditionTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.context.orientation.event.DeviceOrientationTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;
import at.fhhagenberg.mint.automate.thrift.sensorcontext.BatteryInfoThriftPacket;
import at.fhhagenberg.mint.automate.thrift.sensorcontext.DeviceOrientationThriftPacket;
import at.fhhagenberg.mint.automate.thrift.sensorcontext.LightSensorDataThriftPacket;
import at.fhhagenberg.mint.automate.thrift.sensorcontext.SensorContextThriftService;
import at.fhooe.automate.thrift.base.ThriftPacketHeader;

/**
 * Network handler for the sensor context managers.
 */
public class SensorContextNetworkHandler implements ClientNetworkHandler {
	private SensorContextThriftService.Client mClient;

	public SensorContextNetworkHandler() {
	}

	@Override
	public List<Id> getTransmissionEvents() {
		return Arrays.asList(LightConditionTransmissionEvent.ID,
				DeviceOrientationTransmissionEvent.ID,
				BatteryInfoTransmissionEvent.ID);
	}

	@Override
	public Serializable convertToSerializable(GenericPacket packet) {
		Serializable s = null;
		if (packet.getEvent() instanceof LightConditionTransmissionEvent) {
			s = convertLightSensorPacket(packet);
		} else if (packet.getEvent() instanceof DeviceOrientationTransmissionEvent) {
			s = convertDeviceOrientationPacket(packet);
		} else if (packet.getEvent() instanceof BatteryInfoTransmissionEvent) {
			s = convertBatteryInfoPacket(packet);
		}
		return s;
	}

	private static Serializable convertLightSensorPacket(GenericPacket packet) {
		ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
		LightConditionTransmissionEvent temp = (LightConditionTransmissionEvent) packet.getEvent();
		return new LightSensorDataThriftPacket(header, temp.getStartTime().getTime(),
				temp.getDuration(), temp.getClassification());
	}

	private static Serializable convertDeviceOrientationPacket(GenericPacket packet) {
		ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
		DeviceOrientationTransmissionEvent temp = (DeviceOrientationTransmissionEvent) packet.getEvent();
		return new DeviceOrientationThriftPacket(header, temp.getStartTime().getTime(), temp.getEndTime().getTime(), temp.getOrientation());
	}

	private static Serializable convertBatteryInfoPacket(GenericPacket packet) {
		ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
		BatteryInfoTransmissionEvent temp = (BatteryInfoTransmissionEvent) packet.getEvent();
		return new BatteryInfoThriftPacket(header, temp.getStartTime(),
				temp.getDuration(), temp.getHealth(), temp.getLevel(), temp.getPlugged(), temp.isPresent(),
				temp.getScale(), temp.getStatus(), temp.getTechnology(), temp.getTemperature(), temp.getVoltage());
	}

	@Override
	public List<Class<?>> getThriftEvents() {
		return Arrays.asList(new Class<?>[]{LightSensorDataThriftPacket.class, DeviceOrientationThriftPacket.class, BatteryInfoThriftPacket.class});
	}

	@Override
	public void sendPacket(TProtocol transportProtocol, Serializable packet) throws TException {
		if (mClient == null) {
			TMultiplexedProtocol multiplexProtocol = new TMultiplexedProtocol(
					transportProtocol, SensorContextThriftService.class.getName());
			mClient = new SensorContextThriftService.Client(multiplexProtocol);
		}

		if (packet.getClass().equals(LightSensorDataThriftPacket.class)) {
			mClient.sendLightSensorDataPacket((LightSensorDataThriftPacket) packet);
		} else if (packet.getClass().equals(DeviceOrientationThriftPacket.class)) {
			mClient.sendDeviceOrientationPacket((DeviceOrientationThriftPacket) packet);
		} else if (packet.getClass().equals(BatteryInfoThriftPacket.class)) {
			mClient.sendBatteryInfoPacket((BatteryInfoThriftPacket) packet);
		}
	}
}
