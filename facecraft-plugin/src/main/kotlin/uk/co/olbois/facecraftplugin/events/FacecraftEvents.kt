package uk.co.olbois.facecraftplugin.events

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import uk.co.olbois.facecraftplugin.FacecraftPlugin
import uk.co.olbois.facecraftplugin.networking.NetworkManager
import uk.co.olbois.facecraftplugin.networking.packet.ConsoleOutputPacket
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

        val net = FacecraftPlugin.networkManager
        // make sure we are connected to Facecraft first
        if (net.canConnect() && net.status == NetworkManager.Status.OPEN) {
            // send packet
            net.sendPacket(messagePacket) { }
        }
    }

}