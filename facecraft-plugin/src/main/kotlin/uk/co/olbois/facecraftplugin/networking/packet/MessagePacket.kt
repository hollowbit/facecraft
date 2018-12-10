package uk.co.olbois.facecraftplugin.networking.packet

import java.util.*

class MessagePacket () : Packet(PacketType.MESSAGE) {

    private var message : String = ""
    private var name : String = ""
    private var gameType : String = ""
    private var date : Date = Date()

    constructor(message : String, name : String, gameType : String, date : Date) : this() {
        this.message = message
        this.name = name
        this.gameType = gameType
        this.date = date
    }

}