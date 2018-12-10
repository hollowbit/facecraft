package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.util.*

class MessagePacket (private var message : String, private var name : String, private var gameType : String, private var date : Date) : Packet(PacketType.MESSAGE) {

    constructor () : this("", "", "", Date())

}