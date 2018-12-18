package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.model.ConsoleOutput
import uk.co.olbois.facecraft.networking.packet.ConsoleOutputPacket
import uk.co.olbois.facecraft.networking.packet.PacketListener
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ConsoleOutputPacketListener(networkManager: NetworkManager) : PacketListener(networkManager) {

    init {
        register(PacketType.CONSOLE_OUTPUT) { packet: Packet, conn: WebSocket ->
            if (packet is ConsoleOutputPacket) {
                // make sure this socket is connected to a server
                val serverAddress = ConnectionManager.instance.getServerByWebSocket(conn)
                if (serverAddress != null) {

                    // get the server model, if possible
                    val serverOptional = Application.getInstance().serverRepository.findById(serverAddress)
                    if (serverOptional.isPresent) {
                        val server = serverOptional.get()

                        // create the output
                        val output = ConsoleOutput(packet.message, server)

                        // save the new output
                        Application.getInstance().consoleOutputRepository.save(output)

                        ResponsePacket(packet, 0, "")
                    } else
                        ResponsePacket(packet, 5, "The server you are connected to no longer exists")
                } else
                    ResponsePacket(packet, 2, "Not connected. Please connect first")
            } else
                ResponsePacket(packet, 1, "Internal server error...")
        }
    }

}
