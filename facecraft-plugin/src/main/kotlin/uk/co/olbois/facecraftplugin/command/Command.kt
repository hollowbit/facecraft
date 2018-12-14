package uk.co.olbois.facecraftplugin.command

import org.bukkit.command.CommandSender

interface Command {
    fun invoke(args: Array<out String>?, sender: CommandSender?) : Boolean
}