package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ServerCommandPacket(val command: String) : Packet(PacketType.SERVER_COMMAND)