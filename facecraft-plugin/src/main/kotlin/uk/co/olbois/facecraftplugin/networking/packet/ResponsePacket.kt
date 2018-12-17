package uk.co.olbois.facecraftplugin.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ResponsePacket(val originPacket : Packet, val errorCode : Int, val errorMessage : String) : Packet(PacketType.RESPONSE) {
    constructor() : this(PingPacket(), 0, "")

    val originId = originPacket.id
}