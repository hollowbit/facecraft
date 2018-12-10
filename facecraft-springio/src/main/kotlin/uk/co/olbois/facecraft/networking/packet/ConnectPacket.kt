package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ConnectPacket(private val address : String, private val password : String) : Packet(PacketType.CONNECT) {
    constructor() : this("", "")
}