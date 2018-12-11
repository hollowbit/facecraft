package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class RegisterPacket(private val address : String, private val password : String) : Packet(PacketType.REGISTER) {
    constructor() : this("", "")
}