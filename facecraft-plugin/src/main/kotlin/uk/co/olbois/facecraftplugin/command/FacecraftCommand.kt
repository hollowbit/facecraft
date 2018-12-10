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

                // make sure there are 2 args
                if (args.size < 2) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft connect <address> <password>")
                }

                // connect to server and handle result
                NetworkManager.instance.connect(args[0], args[1]) {established, message ->
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
                    val status = NetworkManager.instance.status
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
                val status = NetworkManager.instance.status
                if (status == NetworkManager.Status.CLOSED) {
                    sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Already Disconnected!")
                    return true
                }

                // disconnect and handle result
                NetworkManager.instance.disconnect()

                true
            }
            "register" -> {
                // make sure you are not already connected
                val status = NetworkManager.instance.status
                if (status == NetworkManager.Status.OPEN) {
                    sender?.sendMessage("${ChatColor.GREEN}<Facecraft> Please disconnect first: /facecraft disconnect")
                    return true
                }

                // make sure there are 2 args
                if (args.size < 2) {
                    sender?.sendMessage("${ChatColor.RED}<Facecraft> Usage: /facecraft register <address> <password>")
                }

                // connect to server and handle result
                NetworkManager.instance.register(args[0], args[1]) {established, message ->
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