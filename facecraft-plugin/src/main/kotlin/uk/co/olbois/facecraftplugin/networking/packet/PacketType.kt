package uk.co.olbois.facecraftplugin.networking.packet

enum class PacketType(val clazz : Class<*>) {
    PING(PingPacket::class.java),
    MESSAGE(MessagePacket::class.java),
    REGISTER(RegisterPacket::class.java),
    CONNECT(ConnectPacket::class.java),
    RESPONSE(ResponsePacket::class.java),
    DISCONNECT(DisconnectPacket::class.java),
    ADD_OWNER(AddOwnerPacket::class.java),
    REMOVE_OWNER(RemoveOwnerPacket::class.java)
}