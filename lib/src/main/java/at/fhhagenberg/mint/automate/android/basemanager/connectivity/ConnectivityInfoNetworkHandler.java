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

package at.fhhagenberg.mint.automate.android.basemanager.connectivity;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.ClientNetworkHandler;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.packet.GenericPacket;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.network.thrift.ThriftHelper;
import at.fhhagenberg.mint.automate.android.basemanager.connectivity.event.NetworkInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;
import at.fhhagenberg.mint.automate.thrift.connectivityinfo.ConnectivityInfoThriftService;
import at.fhhagenberg.mint.automate.thrift.connectivityinfo.NetworkInfoThriftPacket;
import at.fhooe.automate.thrift.base.ThriftPacketHeader;

/**
 * Handler to send connectivity information to the server.
 */
public class ConnectivityInfoNetworkHandler implements ClientNetworkHandler {
    private ConnectivityInfoThriftService.Client mClient;

    @Override
    public List<Id> getTransmissionEvents() {
        return Arrays.asList(NetworkInfoTransmissionEvent.ID);
    }

    @Override
    public Serializable convertToSerializable(GenericPacket packet) {
        Serializable s = null;
        if (packet.getEvent() instanceof NetworkInfoTransmissionEvent) {
            s = convertNetworkInfoPacket(packet);
        }
        return s;
    }

    private static Serializable convertNetworkInfoPacket(GenericPacket packet) {
        ThriftPacketHeader header = ThriftHelper.convertHeader(packet);
        NetworkInfoTransmissionEvent temp = (NetworkInfoTransmissionEvent) packet.getEvent();
        NetworkInfoThriftPacket thriftPacket = new NetworkInfoThriftPacket(header, temp.getStartTime(),
                temp.getDuration(), temp.getType(), temp.getSubType(), temp.isRoaming());
        return thriftPacket;
    }

    @Override
    public List<Class<?>> getThriftEvents() {
        return Arrays.asList(new Class<?>[]{NetworkInfoThriftPacket.class});
    }

    @Override
    public void sendPacket(TProtocol transportProtocol, Serializable packet) throws TException {
        if (mClient == null) {
            TMultiplexedProtocol multiplexProtocol = new TMultiplexedProtocol(
                    transportProtocol, ConnectivityInfoThriftService.class.getName());
            mClient = new ConnectivityInfoThriftService.Client(multiplexProtocol);
        }

        if (packet instanceof NetworkInfoThriftPacket) {
            sendPacket((NetworkInfoThriftPacket) packet);
        }
    }

    private void sendPacket(NetworkInfoThriftPacket packet) throws TException {
        mClient.sendNetworkInfoPacket(packet);
    }
}
