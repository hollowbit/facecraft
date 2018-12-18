package uk.co.olbois.facecraft.networking.packet

import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ConsoleOutputPacket(val message : String) : Packet(PacketType.CONSOLE_OUTPUT)