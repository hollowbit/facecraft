package uk.co.olbois.facecraftplugin.networking.packet

class RemoveOwnerPacket(val user : String) : Packet(PacketType.REMOVE_OWNER)