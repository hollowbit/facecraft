package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ResponsePacket(val errorCode : Int, val errorMessage : String) : Packet(PacketType.RESPONSE) {
    constructor() : this(0, "")
}