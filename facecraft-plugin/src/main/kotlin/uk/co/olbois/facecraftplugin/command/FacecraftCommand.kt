package uk.co.olbois.facecraftplugin.command

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import uk.co.olbois.facecraftplugin.networking.NetworkManager

class FacecraftCommand : Command {

    override fun invoke(args: Array<out String>?, sender: CommandSender?): Boolean {
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
                val status = NetworkManager.instance.status
                if (status == NetworkManager.Status.OPEN) {
                    sender?.sendMessage("${ChatColor.GREEN}Facecraft: Already Connected!")
                    return true
                }

                // connect to server and handle result
                NetworkManager.instance.connect {established ->
                    when(established) {
                        true -> {
                            sender?.sendMessage("${ChatColor.GREEN}Facecraft: Connected!")
                        }
                        false -> {
                            sender?.sendMessage("${ChatColor.RED}Facecraft: Connection failed!")
                        }
                    }
                }

                true
            }
            "status" -> {
                // if the sender is not null, tell them the connection status
                if (sender != null) {
                    val status = NetworkManager.instance.status
                    when(status) {
                        NetworkManager.Status.OPEN -> {
                            sender.sendMessage("${ChatColor.GREEN}Facecraft: Connected!")
                        }
                        else -> {
                            sender.sendMessage("${ChatColor.RED}Facecraft: Disconnected!")
                        }
                    }
                }
                true
            }
            "disconnect" -> {
                // make sure you are not already disconnected
                val status = NetworkManager.instance.status
                if (status == NetworkManager.Status.CLOSED) {
                    sender?.sendMessage("${ChatColor.GREEN}Facecraft: Already Disconnected!")
                    return true
                }

                // disconnect and handle result
                NetworkManager.instance.disconnect {disconnected ->
                    when(disconnected) {
                        true -> {
                            sender?.sendMessage("${ChatColor.GREEN}Facecraft: Disconnected!")
                        }
                        false -> {
                            sender?.sendMessage("${ChatColor.RED}Facecraft: Disconnection failed!")
                        }
                    }
                }

                true
            }
            else -> false
        }
    }

}