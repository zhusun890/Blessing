/*
 * Copyright (C) 2023-2024. CatMoe / Blessing Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.miaomoe.blessing.protocol.registry;

import net.miaomoe.blessing.protocol.mappings.PacketMapping;
import net.miaomoe.blessing.protocol.mappings.ProtocolMappings;
import net.miaomoe.blessing.protocol.packet.common.PacketDisconnect;
import net.miaomoe.blessing.protocol.packet.common.PacketKeepAlive;
import net.miaomoe.blessing.protocol.packet.common.PacketPluginMessage;
import net.miaomoe.blessing.protocol.packet.configuration.PacketClientConfiguration;
import net.miaomoe.blessing.protocol.packet.configuration.PacketRegistryData;
import net.miaomoe.blessing.protocol.packet.handshake.PacketHandshake;
import net.miaomoe.blessing.protocol.packet.login.PacketLoginAcknowledged;
import net.miaomoe.blessing.protocol.packet.login.PacketLoginPluginMessage;
import net.miaomoe.blessing.protocol.packet.login.PacketLoginRequest;
import net.miaomoe.blessing.protocol.packet.login.PacketLoginResponse;
import net.miaomoe.blessing.protocol.packet.status.PacketStatusPing;
import net.miaomoe.blessing.protocol.packet.status.PacketStatusRequest;
import net.miaomoe.blessing.protocol.packet.status.PacketStatusResponse;
import net.miaomoe.blessing.protocol.util.LazyInit;
import net.miaomoe.blessing.protocol.version.Version;
import net.miaomoe.blessing.protocol.version.VersionRange;

import static net.miaomoe.blessing.protocol.mappings.PacketMapping.*;
import static net.miaomoe.blessing.protocol.version.VersionRange.of;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public enum State {
    HANDSHAKE{
        {
            final ProtocolMappings serverbound = this.serverbound.getValue();
            serverbound.register(generate(PacketHandshake::new, withAll(0x00)));
        }
    },
    STATUS{
        {
            final ProtocolMappings clientbound = this.clientbound.getValue();
            final ProtocolMappings serverbound = this.serverbound.getValue();
            serverbound.register(generate(PacketStatusRequest::new, withAll(0x00)));
            clientbound.register(generate(PacketStatusResponse::new, withAll(0x00)));
            final PacketMapping ping = generate(PacketStatusPing::new, withAll(0x01));
            serverbound.register(ping);
            clientbound.register(ping);
        }
    },
    LOGIN{
        {
            final ProtocolMappings clientbound = this.clientbound.getValue();
            final ProtocolMappings serverbound = this.serverbound.getValue();
            serverbound.register(generate(PacketLoginRequest::new, withAll(0x00)));
            clientbound.register(generate(PacketLoginResponse::new, withAll(0x02)));
            serverbound.register(generate(PacketLoginPluginMessage::new, withAll(0x02)));
            clientbound.register(generate(PacketLoginPluginMessage::new, withAll(0x04)));
            clientbound.register(generate(PacketDisconnect::new, withAll(0x00)));
            serverbound.register(generate(PacketLoginAcknowledged::new, withSingle(of(Version.V1_20_3, Version.V1_20_4), 0x03)));
        }
    },
    CONFIGURATION{
        {
            final ProtocolMappings clientbound = this.clientbound.getValue();
            final ProtocolMappings serverbound = this.serverbound.getValue();
            final VersionRange range = VersionRange.of(Version.V1_20_3, Version.V1_20_4);
            serverbound.register(generate(PacketClientConfiguration::new, withSingle(range, 0x00)));
            clientbound.register(generate(PacketDisconnect::new, withSingle(range, 0x01)));
            serverbound.register(generate(PacketPluginMessage::new, withSingle(range, 0x01)));
            clientbound.register(generate(PacketPluginMessage::new, withSingle(range, 0x00)));
            final PacketMapping keepAlive = generate(PacketKeepAlive::new, withSingle(range, 0x03));
            serverbound.register(keepAlive);
            clientbound.register(keepAlive);
            final PacketMapping finishConfiguration = generate(PacketClientConfiguration::new, withSingle(range, 0x02));
            serverbound.register(finishConfiguration);
            clientbound.register(finishConfiguration);
            clientbound.register(generate(PacketRegistryData::new, withSingle(range, 0x05)));
        }
    },
    PLAY;

    public final LazyInit<ProtocolMappings> clientbound = new LazyInit<>(ProtocolMappings::new);
    public final LazyInit<ProtocolMappings> serverbound = new LazyInit<>(ProtocolMappings::new);
}
