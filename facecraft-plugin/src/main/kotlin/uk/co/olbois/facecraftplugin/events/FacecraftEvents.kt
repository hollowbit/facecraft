package uk.co.olbois.facecraftplugin.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import uk.co.olbois.facecraftplugin.networking.packet.MessagePacket
import java.util.*

class FacecraftEvents : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        var message = event.message
        var user = event.player.name
        var type = "game"
        var date = Date()

        var messagePacket : MessagePacket

        messagePacket = MessagePacket(message, user, type, date)

        // send packet "messagePacket"
    }

}