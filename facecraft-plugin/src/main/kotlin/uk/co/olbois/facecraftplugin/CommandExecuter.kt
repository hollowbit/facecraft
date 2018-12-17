package uk.co.olbois.facecraftplugin

import org.bukkit.Bukkit
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import uk.co.olbois.facecraftplugin.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.ServerCommandPacket

class CommandExecuter(private val plugin: FacecraftPlugin) : Runnable {

    private val scheduler = plugin.server.scheduler
    private val commandQueue = mutableListOf<String>()
    var taskId = 0

    private val commandHandler = {packet: Packet ->
        if (packet is ServerCommandPacket) {
            // add command to queue
            commandQueue.add(packet.command)
            ResponsePacket(packet, 0, "")
        } else
            ResponsePacket(packet, 1, "Internal server error...")
    }

    override fun run() {
        // make a list copy
        val commandQueueCopy = mutableListOf<String>()
        commandQueueCopy.addAll(commandQueue)

        // clear current list
        commandQueue.clear()

        // handle all copied commands
        commandQueueCopy.forEach { c ->
            Bukkit.getServer().logger.info(c)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), c)
        }
    }

    fun load() {
        FacecraftPlugin.networkManager.registerListener(PacketType.SERVER_COMMAND, commandHandler)
        taskId = scheduler.scheduleSyncRepeatingTask(plugin, this, 0, 20)
    }

    fun unload() {
        FacecraftPlugin.networkManager.deregisterListener(commandHandler)
        scheduler.cancelTask(taskId)
    }

}