package uk.co.olbois.facecraftplugin.networking.packet

import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.NetworkManager

abstract class PacketListener {

    private val listeners = mutableListOf<(Packet) -> ResponsePacket>()

    protected fun register(packetType : PacketType, listener : (Packet) -> ResponsePacket) {
        NetworkManager.instance.registerListener(packetType, listener)
    }

    fun deregister() {
        for (l in listeners)
            NetworkManager.instance.deregisterListener(l)
    }

}