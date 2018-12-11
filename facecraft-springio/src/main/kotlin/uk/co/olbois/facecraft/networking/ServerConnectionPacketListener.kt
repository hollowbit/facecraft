package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.packet.ConnectPacket
import uk.co.olbois.facecraft.networking.packet.PacketListener
import uk.co.olbois.facecraft.networking.packet.RegisterPacket
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType

class ServerConnectionPacketListener(networkManager: NetworkManager) : PacketListener(networkManager) {

    init {
        register(PacketType.REGISTER) {packet: Packet, conn: WebSocket ->
            // make sure this is actually a register packet
            if (packet is RegisterPacket) {

                val serverRepository = Application.getInstance().serverRepository

                // make sure doesn't already exist
                if (serverRepository.existsById(packet.address))
                    ResponsePacket(packet, 90, "Server with address [${packet.address}] already registered")


                // create the server
                val server = Server()
                server.address = packet.address
                server.name = packet.name
                server.password = packet.password
                serverRepository.save(server)

                ResponsePacket(packet,0, "")
            } else
                ResponsePacket(packet,1, "Internal server error...")
        }

        register(PacketType.CONNECT) {packet: Packet, conn: WebSocket ->
            // make sure this is actually a register packet
            if (packet is ConnectPacket) {
                val serverRepository = Application.getInstance().serverRepository

                // make sure doesn't already exist
                if (!serverRepository.existsById(packet.address))
                    ResponsePacket(packet,404, "Server with address [${packet.address}] not registered")

                // get server
                val server = serverRepository.findById(packet.address) as Server

                // authenticate password
                if (packet.password != server.password)
                    ResponsePacket(packet,91, "The given password is incorrect")

                // add to connections
                ConnectionManager.instance.addConnection(conn, server)

                // send valid response
                ResponsePacket(packet,0, "")
            } else
                ResponsePacket(packet,1, "Internal server error...")
        }

        register(PacketType.DISCONNECT) {packet: Packet, conn: WebSocket ->
            println("Disconnect!")
            // simply remove the connection
            ConnectionManager.instance.removeConnection(conn)
            ResponsePacket(packet,0, "")
        }
    }

}