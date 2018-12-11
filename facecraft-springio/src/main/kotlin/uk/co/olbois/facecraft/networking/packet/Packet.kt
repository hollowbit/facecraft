package uk.co.olbois.facecraftplugin.networking.packet

import java.util.concurrent.atomic.AtomicLong

abstract class Packet(val type : PacketType) {

    companion object {
        val ID_GENERATOR = AtomicLong()
    }

    internal val id = ID_GENERATOR.incrementAndGet()
}