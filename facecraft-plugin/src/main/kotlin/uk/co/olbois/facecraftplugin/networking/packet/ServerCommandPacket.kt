package uk.co.olbois.facecraftplugin.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ServerCommandPacket(val command: String) : Packet(PacketType.SERVER_COMMAND)