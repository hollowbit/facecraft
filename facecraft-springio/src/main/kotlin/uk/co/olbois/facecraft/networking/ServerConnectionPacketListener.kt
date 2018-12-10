package uk.co.olbois.facecraft.networking

import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.packet.PacketListener
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ServerConnectionPacketListener : PacketListener() {

    init {
        register(PacketType.REGISTER) {packet: Packet, server: Server ->
            //TODO add packet verification code
            ResponsePacket(0, "")
        }

        register(PacketType.CONNECT) {packet: Packet, server: Server ->
            //TODO add packet verification code
            ResponsePacket(0, "")
        }

        register(PacketType.DISCONNECT) {packet: Packet, server: Server ->
            //TODO add packet verification code
            ResponsePacket(0, "")
        }
    }

}