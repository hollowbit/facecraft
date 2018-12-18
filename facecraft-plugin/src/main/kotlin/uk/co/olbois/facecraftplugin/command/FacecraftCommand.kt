package uk.co.olbois.facecraftplugin.command

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import uk.co.olbois.facecraftplugin.FacecraftPlugin
import uk.co.olbois.facecraftplugin.networking.NetworkManager
import uk.co.olbois.facecraftplugin.networking.packet.AddOwnerPacket
import uk.co.olbois.facecraftplugin.networking.packet.MessagePacket
import uk.co.olbois.facecraftplugin.networking.packet.RemoveOwnerPacket
import java.util.*

class FacecraftCommand : Command {

    override fun invoke(args: Array<out String>?, sender: CommandSender?): Boolean {
        // make sure we can even connect to facecraft
        if (!FacecraftPlugin.networkManager.canConnect()) {
            sender?.sendMessage("${ChatColor.RED}<Facecraft> Cannot connect to Facecraft Central Server")
            return true
        }

        // there must be args
        if (args == null)
            return false

        // there must be at least 1 arg
        if (args.isEmpty())
            return false

        val label = args.first()
        return when(label) {
            "connect" -> {
                // make sure you are not already connected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.OPEN) {
                    sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Already Connected!")
                    return true
                }

                // make sure there are 2 args
                if (args.size != 3) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft connect <address> <password>")
                    return true
                }

                // connect to server and handle result
                FacecraftPlugin.networkManager.connect(args[1], args[2]) {established, message ->
                    when(established) {
                        true -> {
                            sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Connected!")
                        }
                        false -> {
                            sender?.sendMessage("${ChatColor.RED}<Facecraft> Connection failed! $message")
                        }
                    }
                }

                true
            }
            "status" -> {
                // if the sender is not null, tell them the connection status
                if (sender != null) {
                    val status = FacecraftPlugin.networkManager.status
                    when(status) {
                        NetworkManager.Status.OPEN -> {
                            sender.sendMessage("${ChatColor.GREEN}<Facecraft> Connected")
                        }
                        else -> {
                            sender.sendMessage("${ChatColor.RED}<Facecraft> Disconnected")
                        }
                    }
                }
                true
            }
            "disconnect" -> {
                // make sure you are not already disconnected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.CLOSED) {
                    sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Already Disconnected!")
                    return true
                }

                // disconnect and handle result
                FacecraftPlugin.networkManager.disconnect()
                sender?.sendMessage("${ChatColor.RED}<Facecraft> Disconnected.")

                true
            }
            "register" -> {
                // make sure you are not already connected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.OPEN) {
                    sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Please disconnect first: /facecraft disconnect")
                    return true
                }

                // make sure there are at least 4 args
                if (args.size < 4) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft register <address> <password> <name...>")
                    return true
                }

                // build name argument
                var name = ""
                for (i in 3 until args.size) {
                    name += args[i]
                    if (i < args.size - 1)
                        name += " "
                }

                // connect to server and handle result
                FacecraftPlugin.networkManager.register(args[1], name, args[2]) {established, message ->
                    when(established) {
                        true -> {
                            sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Registered! Please connect now: /facecraft connect <address> <password>")
                        }
                        false -> {
                            sender?.sendMessage("${ChatColor.RED}<Facecraft> Register failed! $message")
                        }
                    }
                }

                true
            }
            "addowner" -> {
                // make sure you are not already connected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.CLOSED) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Please connect first: /facecraft connect <address> <password>!")
                    return true
                }

                if (args.size < 2) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft addowner <facecraft-username>")
                    return true
                }

                val packet = AddOwnerPacket(args[1])

                FacecraftPlugin.networkManager.sendPacket(packet) {responsePacket ->
                    if (responsePacket.errorCode == 0)
                        sender?.sendMessage("${ChatColor.GREEN}<Facecraft> ${packet.user} has been added as an owner")
                    else
                        sender?.sendMessage("${ChatColor.RED}<Facecraft> Could not add ${packet.user} as an owner: " + responsePacket.errorMessage)
                }

                true
            }
            "removeowner" -> {
                // make sure you are not already connected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.CLOSED) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Please connect first: /facecraft connect <address> <password>!")
                    return true
                }

                if (args.size < 2) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft removeowner <facecraft-username>")
                    return true
                }

                val packet = RemoveOwnerPacket(args[1])

                FacecraftPlugin.networkManager.sendPacket(packet) {responsePacket ->
                    if (responsePacket.errorCode == 0)
                        sender?.sendMessage("${ChatColor.GREEN}<Facecraft> ${packet.user} has been removed as an owner")
                    else
                        sender?.sendMessage("${ChatColor.RED}<Facecraft> Could not remove ${packet.user} as an owner: " + responsePacket.errorMessage)
                }
                true
            }
            else -> false
        }
    }

}