package uk.co.olbois.facecraftplugin.events

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.layout.PatternLayout
import org.bukkit.ChatColor
import uk.co.olbois.facecraftplugin.FacecraftPlugin
import uk.co.olbois.facecraftplugin.networking.NetworkManager
import uk.co.olbois.facecraftplugin.networking.packet.ConsoleOutputPacket

class ServerConsoleHandler : AbstractAppender("Log4JAppender", null,
        PatternLayout.createDefaultLayout(), false) {

    private val re = Regex("[^a-zA-Z0-9\\!\\@\\#\\\$\\%\\^\\&\\*\\(\\)\\_\\+\\-\\=\\{\\}\\[\\]\\:\\;\\\"\\'\\<\\>\\,\\.\\?\\\\\\/\\`\\~]+")

    override fun isStarted(): Boolean = true

    override fun append(logEvent: LogEvent?) {
        // make sure the message is not null or empty
        if (logEvent == null || logEvent.message == null)
            return

        val net = FacecraftPlugin.networkManager
        // make sure we are connected to Facecraft first
        if (net.canConnect() && net.status == NetworkManager.Status.OPEN) {
            // send packet
            net.sendPacket(ConsoleOutputPacket(ChatColor.stripColor(logEvent.message.formattedMessage).replace(re, " "))) { }//silently ignore the response
        }
    }

}