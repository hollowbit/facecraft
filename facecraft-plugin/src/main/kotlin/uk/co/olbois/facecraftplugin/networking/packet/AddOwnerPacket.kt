package uk.co.olbois.facecraftplugin.networking.packet

class AddOwnerPacket(val user : String) : Packet(PacketType.ADD_OWNER)