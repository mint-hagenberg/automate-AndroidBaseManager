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

package at.fhhagenberg.mint.automate.android.basemanager.deviceinfo;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.ClientNetworkHandler;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.packet.GenericPacket;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.thrift.ThriftHelper;
import at.fhhagenberg.mint.automate.android.basemanager.deviceinfo.event.DeviceInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;
import at.fhhagenberg.mint.automate.thrift.deviceinfo.DeviceInfoThriftPacket;
import at.fhhagenberg.mint.automate.thrift.deviceinfo.DeviceInfoThriftService;
import at.fhooe.automate.thrift.base.ThriftPacketHeader;

/**
 * Network handler to transfer device info objects to the server.
 */
public class DeviceInfoNetworkHandler implements ClientNetworkHandler {
	private DeviceInfoThriftService.Client mClient;

	public DeviceInfoNetworkHandler() {
	}

	@Override
	public List<Id> getTransmissionEvents() {
		return Arrays.asList(DeviceInfoTransmissionEvent.ID);
	}

	@Override
	public Serializable convertToSerializable(GenericPacket packet) {
		Serializable s = null;
		if (packet.getEvent() instanceof DeviceInfoTransmissionEvent) {
			s = convertDeviceInfoPacket(packet);
		}
		return s;
	}

	private static Serializable convertDeviceInfoPacket(GenericPacket packet) {
		ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
		DeviceInfoTransmissionEvent temp = (DeviceInfoTransmissionEvent) packet.getEvent();
		DeviceInfoThriftPacket thrift = new DeviceInfoThriftPacket(header, temp.getDate(),
				temp.getDevice(), temp.getOperator(), temp.getOs(), temp.getLocation(),
				temp.getLocaleLanguage(), temp.getLocaleCountry());
		thrift.setTimeZoneOffset(temp.getTimeZoneOffset());
		thrift.setAppVersionName(temp.getVersionName());
		thrift.setResolutionWidth(temp.getResolutionWidth());
		thrift.setResolutionHeight(temp.getResolutionHeight());
		return thrift;
	}

	@Override
	public List<Class<?>> getThriftEvents() {
		return Arrays.asList(new Class<?>[]{DeviceInfoThriftPacket.class});
	}

	@Override
	public void sendPacket(TProtocol transportProtocol, Serializable packet) throws TException {
		if (mClient == null) {
			TMultiplexedProtocol multiplexProtocol = new TMultiplexedProtocol(
					transportProtocol, DeviceInfoThriftService.class.getName());
			mClient = new DeviceInfoThriftService.Client(multiplexProtocol);
		}

		if (packet.getClass().equals(DeviceInfoThriftPacket.class)) {
			mClient.sendDeviceInfoPacket((DeviceInfoThriftPacket) packet);
		}
	}
}
