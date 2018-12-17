package uk.co.olbois.facecraftplugin

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import uk.co.olbois.facecraftplugin.command.FacecraftCommand
import uk.co.olbois.facecraftplugin.events.FacecraftEvents
import uk.co.olbois.facecraftplugin.events.ServerConsoleHandler
import uk.co.olbois.facecraftplugin.networking.NetworkManager
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import uk.co.olbois.facecraftplugin.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.ServerCommandPacket

class FacecraftPlugin : JavaPlugin() {

    companion object {
        val networkManager = NetworkManager()
    }

    private val serverConsoleHandler = ServerConsoleHandler()
    private lateinit var logger : Logger

    private val commandExecuter = CommandExecuter(this)

    override fun onEnable() {
        super.onEnable()
        Bukkit.getServer().pluginManager.registerEvents(FacecraftEvents(), this)
        networkManager.load()
        logger = LogManager.getRootLogger() as Logger
        logger.addAppender(serverConsoleHandler)
        commandExecuter.load()
    }

    override fun onDisable() {
        super.onDisable()
        commandExecuter.unload()
        networkManager.unload()
        logger.removeAppender(serverConsoleHandler)
    }

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        return when(label) {
            "facecraft" -> {
               FacecraftCommand().invoke(args, sender)
            }
            else -> super.onCommand(sender, command, label, args)
        }
    }

}