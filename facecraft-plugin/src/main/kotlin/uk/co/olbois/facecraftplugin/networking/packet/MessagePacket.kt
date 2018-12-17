package uk.co.olbois.facecraftplugin.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.util.*

class MessagePacket (var message : String, var name : String, var gameType : String, var date : Date) : Packet(PacketType.MESSAGE) {

    constructor () : this("", "", "", Date())

}