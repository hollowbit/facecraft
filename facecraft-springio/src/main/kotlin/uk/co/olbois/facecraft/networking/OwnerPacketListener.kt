package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.networking.packet.PacketListener
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.AddOwnerPacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import uk.co.olbois.facecraftplugin.networking.packet.RemoveOwnerPacket


class OwnerPacketListener(networkManager: NetworkManager) : PacketListener(networkManager) {

    init {
        register(PacketType.ADD_OWNER) { packet: Packet, conn: WebSocket ->
            if (packet is AddOwnerPacket) {
                // get server associated to packet to make sure they are connected
                val server = ConnectionManager.instance.getServerByWebSocket(conn)
                if (server != null) {

                    // get user given by packet and make sure it exists
                    val user = Application.getInstance().userRepository.findByUsername(packet.user)
                    if (user != null) {

                        // make sure the user isn't already an owner of the server
                        if (!user.serversOwned.contains(server)) {
                            // all is good, add the owner and send response
                            user.serversOwned.add(server)
                            server.owners.add(user)
                            Application.getInstance().serverRepository.save(server)
                            ResponsePacket(packet, 0, "")
                        } else
                            ResponsePacket(packet, 4, "User is already an owner of this server")
                    } else
                        ResponsePacket(packet, 3, "User doesn't exist")
                } else
                    ResponsePacket(packet, 2, "Not connected. Please connect first")
            } else
        ResponsePacket(packet,1, "Internal server error...")
        }

        register(PacketType.REMOVE_OWNER) {packet: Packet, conn: WebSocket ->
            if (packet is RemoveOwnerPacket) {
                // see if this connection is connected to a server
                val server = ConnectionManager.instance.getServerByWebSocket(conn)
                if (server != null) {
                    // make sure the given user exists
                    val user = Application.getInstance().userRepository.findByUsername(packet.user)
                    if (user != null) {
                        // check if they currently are an owner of the server
                        if (server.owners.contains(user)) {

                            // all is good, remove the owner and send response
                            user.serversOwned.remove(server)
                            server.owners.remove(user)
                            Application.getInstance().serverRepository.save(server)
                            ResponsePacket(packet, 0, "")
                        } else
                            ResponsePacket(packet, 4, "User is already not an owner")
                    } else
                        ResponsePacket(packet, 3, "User doesn't exist")
                } else
                    ResponsePacket(packet, 2, "Not connected. Please connect first")
            } else
                ResponsePacket(packet, 1, "Internal server error...")
        }
    }

}