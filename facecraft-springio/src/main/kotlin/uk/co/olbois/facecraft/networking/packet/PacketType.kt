package uk.co.olbois.facecraftplugin.networking.packet

import uk.co.olbois.facecraft.networking.packet.*

enum class PacketType(val clazz : Class<*>) {
    PING(PingPacket::class.java),
    MESSAGE(MessagePacket::class.java),
    REGISTER(RegisterPacket::class.java),
    CONNECT(ConnectPacket::class.java),
    RESPONSE(ResponsePacket::class.java),
    DISCONNECT(DisconnectPacket::class.java)
}