package uk.co.olbois.facecraftplugin.command

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import uk.co.olbois.facecraftplugin.FacecraftPlugin
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
                if (!FacecraftPlugin.networkManager.canConnect()) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Cannot connect to Facecraft Central Server")
                    return true
                }

                // make sure you are not already connected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.OPEN) {
                    sender?.sendMessage("${ChatColor.GREEN}Facecraft: Already Connected!")
                    return true
                }

                // make sure there are 2 args
                if (args.size < 3) {
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
                if (!FacecraftPlugin.networkManager.canConnect()) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Cannot connect to Facecraft Central Server")
                    return true
                }

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
                if (!FacecraftPlugin.networkManager.canConnect()) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Cannot connect to Facecraft Central Server")
                    return true
                }

                // make sure you are not already connected
                val status = FacecraftPlugin.networkManager.status
                if (status == NetworkManager.Status.OPEN) {
                    sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Please disconnect first: /facecraft disconnect")
                    return true
                }

                // make sure there are 2 args
                if (args.size < 3) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft register <address> <password>")
                    return true
                }

                // connect to server and handle result
                FacecraftPlugin.networkManager.register(args[1], args[2]) {established, message ->
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
            else -> false
        }
    }

}