package uk.co.olbois.facecraftplugin

import org.bukkit.Bukkit
import uk.co.olbois.facecraftplugin.networking.packet.*

class CommandExecuter(private val plugin: FacecraftPlugin) : Runnable {

    private val scheduler = plugin.server.scheduler
    private val commandQueue = mutableListOf<String>()
    private val messageQueue = mutableListOf<String>()
    var taskId = 0

    private val commandHandler = {packet: Packet ->
        if (packet is ServerCommandPacket) {
            // add command to queue
            commandQueue.add(packet.command)
            ResponsePacket(packet, 0, "")
        } else
            ResponsePacket(packet, 1, "Internal server error...")
    }

    private val messageHandler = {packet: Packet->
        if (packet is MessagePacket) {
            Bukkit.broadcastMessage(packet.name + ": " + packet.message)
            ResponsePacket(packet, 0, "")
        } else
            ResponsePacket(packet, 1, "Internal server error...")
    }


    override fun run() {
        // make a list copy
        val commandQueueCopy = mutableListOf<String>()
        commandQueueCopy.addAll(commandQueue)

        val messageQueueCopy = mutableListOf<String>()
        commandQueueCopy.addAll(messageQueue)
        // clear current list
        messageQueue.clear()

        // handle all copied commands
        messageQueueCopy.forEach { c ->
            Bukkit.getServer().logger.info(c)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), c)
        }
    }

    fun load() {
        FacecraftPlugin.networkManager.registerListener(PacketType.SERVER_COMMAND, commandHandler)
        FacecraftPlugin.networkManager.registerListener(PacketType.MESSAGE, messageHandler)
        taskId = scheduler.scheduleSyncRepeatingTask(plugin, this, 0, 20)
    }

    fun unload() {
        FacecraftPlugin.networkManager.deregisterListener(commandHandler)
        FacecraftPlugin
        scheduler.cancelTask(taskId)
    }

}