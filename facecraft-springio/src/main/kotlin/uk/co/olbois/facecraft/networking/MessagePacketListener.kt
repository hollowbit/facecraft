package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.model.Message
import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.packet.*
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.text.SimpleDateFormat
import java.util.*

class MessagePacketListener(networkManager: NetworkManager) : PacketListener(networkManager) {

    init {
        register(PacketType.MESSAGE) { packet: Packet, conn: WebSocket ->
            // make sure this is actually a message packet
            if (packet is MessagePacket) {

                val messageRepository = Application.getInstance().messageRepository

                val message = Message()
                val sdl = SimpleDateFormat("MMM dd, YYYY h:mm:ss aa")

                var tmp = ConnectionManager.instance.getServerByWebSocket(conn)
                if (tmp != null) {
                    // create the message
                    message.message = packet.message
                    message.senderType = "game"
                    message.date = sdl.format(packet.date)
                    message.serverAddr = tmp
                    message.username = packet.name
                    // save to the database
                    messageRepository.save(message)
                    ResponsePacket(packet, 0, "")
                }

                else {
                    ResponsePacket(packet, 1, "Internal server error...")
                }

            } else
                ResponsePacket(packet, 1, "Internal server error...")
        }
    }
}