package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ConnectPacket(val address : String, val password : String) : Packet(PacketType.CONNECT) {
    constructor() : this("", "")
}