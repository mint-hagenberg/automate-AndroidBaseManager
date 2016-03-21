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

package at.fhhagenberg.mint.automate.android.basemanager.appinteraction;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.ClientNetworkHandler;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.packet.GenericPacket;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.thrift.ThriftHelper;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppInteractionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppInteractionTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppScreenVisitEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppSequenceTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;
import at.fhhagenberg.mint.automate.thrift.appinteraction.AppInteractionThriftPacket;
import at.fhhagenberg.mint.automate.thrift.appinteraction.AppInteractionThriftService;
import at.fhhagenberg.mint.automate.thrift.appinteraction.AppSequenceThriftPacket;
import at.fhooe.automate.thrift.base.ThriftPacketHeader;

/**
 * Implementation of the client nwtwork handler for the app sequence manager data transfer.
 */
public class AppSequenceNetworkHandler implements ClientNetworkHandler {
	private AppInteractionThriftService.Client mClient;

	@Override
	public List<Id> getTransmissionEvents() {
		return Arrays.asList(AppSequenceTransmissionEvent.ID, AppInteractionTransmissionEvent.ID);
	}

	@Override
	public Serializable convertToSerializable(GenericPacket packet) {
		Serializable s = null;
		if (packet.getEvent() instanceof AppSequenceTransmissionEvent) {
			s = convertAppSequencePacket(packet);
		} else if (packet.getEvent() instanceof AppInteractionTransmissionEvent) {
			s = convertAppInteractionPacket(packet);
		}
		return s;
	}

	private static Serializable convertAppSequencePacket(GenericPacket packet) {
		ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
		AppSequenceTransmissionEvent temp = (AppSequenceTransmissionEvent) packet.getEvent();
		return new AppSequenceThriftPacket(header, temp.getXml(), temp.getTimestamp());
	}

	private static Serializable convertAppInteractionPacket(GenericPacket packet) {
		ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
		AppInteractionTransmissionEvent temp = (AppInteractionTransmissionEvent) packet.getEvent();
		AppScreenVisitEvent state = temp.getState();
		AppInteractionEvent interaction = temp.getInteraction();
		if (interaction.getScreenBounds() == null || interaction.getParentBounds() == null) {
			return new AppInteractionThriftPacket(header,
					state.getClassName(), state.getTitle(), state.getOrientation(),
					interaction.getEventTime(),
					interaction.getInteractionType().ordinal(),
					interaction.getClassName() == null ? "" : interaction.getClassName(),
					interaction.getText() == null ? "" : interaction.getText(),
					interaction.getContentDescription() == null ? "" : interaction.getContentDescription(),
					interaction.getViewIdResourceName() == null ? "" : interaction.getViewIdResourceName(),
					0, 0, 0, 0, 0, 0, 0, 0);
		} else {
			return new AppInteractionThriftPacket(header,
					state.getClassName(), state.getTitle(), state.getOrientation(),
					interaction.getEventTime(),
					interaction.getInteractionType().ordinal(), interaction.getClassName(), interaction.getText(), interaction.getContentDescription(), interaction.getViewIdResourceName(),
					interaction.getScreenBounds().left, interaction.getScreenBounds().top, interaction.getScreenBounds().right, interaction.getScreenBounds().bottom,
					interaction.getParentBounds().left, interaction.getParentBounds().top, interaction.getParentBounds().right, interaction.getParentBounds().bottom);
		}
	}

	@Override
	public List<Class<?>> getThriftEvents() {
		return Arrays.asList(new Class<?>[]{AppSequenceThriftPacket.class, AppInteractionThriftPacket.class});
	}

	@Override
	public void sendPacket(TProtocol transportProtocol, Serializable packet) throws TException {
		if (mClient == null) {
			TMultiplexedProtocol multiplexProtocol = new TMultiplexedProtocol(
					transportProtocol, AppInteractionThriftService.class.getName());
			mClient = new AppInteractionThriftService.Client(multiplexProtocol);
		}

		if (packet.getClass().equals(AppSequenceThriftPacket.class)) {
			mClient.sendAppSequencePacket((AppSequenceThriftPacket) packet);
		} else if (packet.getClass().equals(AppInteractionThriftPacket.class)) {
			mClient.sendAppInteractionPacket((AppInteractionThriftPacket) packet);
		}
	}
}
