package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ResponsePacket(originPacket : Packet, val errorCode : Int, val errorMessage : String) : Packet(PacketType.RESPONSE) {

    constructor() : this(PingPacket(), 0, "")

    val originId = originPacket.id
}