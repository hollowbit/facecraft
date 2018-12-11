package uk.co.olbois.facecraftplugin.networking.packet

import uk.co.olbois.facecraftplugin.networking.NetworkManager

abstract class PacketListener(val networkManager: NetworkManager) {

    private val listeners = mutableListOf<(Packet) -> ResponsePacket?>()

    protected fun register(packetType : PacketType, listener : (Packet) -> ResponsePacket?) {
        networkManager.registerListener(packetType, listener)
    }

    fun deregister() {
        for (l in listeners)
            networkManager.deregisterListener(l)
    }

}