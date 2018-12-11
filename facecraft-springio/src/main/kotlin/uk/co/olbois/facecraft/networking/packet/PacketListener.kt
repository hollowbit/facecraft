package uk.co.olbois.facecraft.networking.packet

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.NetworkManager
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

abstract class PacketListener(val networkManager: NetworkManager) {

    private val listeners = mutableListOf<(Packet, WebSocket) -> ResponsePacket?>()

    protected fun register(packetType : PacketType, listener : (Packet, WebSocket) -> ResponsePacket?) {
        networkManager.registerListener(packetType, listener)
    }

    fun deregister() {
        for (l in listeners)
            networkManager.deregisterListener(l)
    }

}