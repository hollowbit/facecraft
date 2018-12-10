package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.NetworkManager
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

abstract class PacketListener {

    private val listeners = mutableListOf<(Packet, Server) -> ResponsePacket>()

    protected fun register(packetType : PacketType, listener : (Packet, Server) -> ResponsePacket) {
        NetworkManager.instance.registerListener(packetType, listener)
    }

    fun deregister() {
        for (l in listeners)
            NetworkManager.instance.deregisterListener(l)
    }

}