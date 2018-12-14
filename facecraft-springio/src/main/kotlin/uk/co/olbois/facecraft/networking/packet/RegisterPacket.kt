package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class RegisterPacket(val address : String, val name : String, val password : String) : Packet(PacketType.REGISTER) {
    constructor() : this("", "", "")
}