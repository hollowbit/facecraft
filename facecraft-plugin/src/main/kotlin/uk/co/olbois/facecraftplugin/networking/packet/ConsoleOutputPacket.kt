package uk.co.olbois.facecraftplugin.networking.packet

class ConsoleOutputPacket(val message : String) : Packet(PacketType.CONSOLE_OUTPUT)