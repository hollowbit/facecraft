package uk.co.olbois.facecraftplugin

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import uk.co.olbois.facecraftplugin.command.FacecraftCommand
import uk.co.olbois.facecraftplugin.events.FacecraftEvents
import uk.co.olbois.facecraftplugin.networking.NetworkManager

class FacecraftPlugin : JavaPlugin() {

    companion object {
        val networkManager = NetworkManager()
    }

    override fun onEnable() {
        super.onEnable()
        Bukkit.getServer().pluginManager.registerEvents(FacecraftEvents(), this)
        networkManager.load()
    }

    override fun onDisable() {
        super.onDisable()
        networkManager.unload()
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