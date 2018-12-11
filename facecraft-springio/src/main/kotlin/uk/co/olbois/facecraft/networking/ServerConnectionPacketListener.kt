package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.packet.PacketListener
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ServerConnectionPacketListener(networkManager: NetworkManager) : PacketListener(networkManager) {

    init {
        register(PacketType.REGISTER) {packet: Packet, conn: WebSocket ->
            //TODO add packet verification code
            println("Received register packet!")
            ResponsePacket(packet,0, "")
        }

        register(PacketType.CONNECT) {packet: Packet, conn: WebSocket ->
            //TODO add packet verification code
            println("Received connect packet!")
            ResponsePacket(packet,0, "")
        }

        register(PacketType.DISCONNECT) {packet: Packet, conn: WebSocket ->
            //TODO add packet verification code
            println("Received disconnect packet!")
            ResponsePacket(packet,0, "")
        }
    }

}