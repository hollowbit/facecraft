package uk.co.olbois.facecraft.networking

import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import uk.co.olbois.facecraft.model.Server
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.lang.Exception
import java.net.InetSocketAddress

class NetworkManager {

    private object Holder {val INSTANCE = NetworkManager()}

    companion object {
        const val FACECRAFT_CENTRAL_PORT = 22123

        val instance: NetworkManager by lazy {Holder.INSTANCE}
    }

    private val server = FacecraftWebsocketServer()

    private val gson = Gson()

    private val responseListeners = mutableMapOf<Long, (ResponsePacket, Server) -> Unit>()
    private val packetListeners = mutableMapOf<PacketType, MutableList<(Packet, Server) -> ResponsePacket>>()

    init {
        // init the server connection packet listener to accept connect and register requests
        ServerConnectionPacketListener()
    }

    var status = Status.CLOSED

    fun start() {
        server.start()
    }

    fun stop() {
        status = Status.CLOSED
        server.stop()
    }

    fun registerListener(packetType : PacketType, listener : (Packet, Server) -> ResponsePacket) {
        // add the listener
        packetListeners[packetType]?.add(listener)
    }

    fun deregisterListener(listener: (Packet, Server) -> ResponsePacket) {
        // check each packet type and remove the listener
        for (list in packetListeners.values)
            list.remove(listener)
    }

    fun sendPacket(packet: Packet, server: Server, responseListener: (ResponsePacket, Server) -> Unit) : Boolean {
        // if not connected just return false
        if (status == Status.CLOSED)
            return false

        // add the packet listener to be used later when we get a response
        responseListeners[packet.id] = responseListener

        // send the packet
        val socket = ConnectionManager.instance.serverWebsockets[server.address]
        if (socket != null) {
            socket.send(gson.toJson(packet))
            return true
        }

        return false
    }

    enum class Status {
        STARTED, CLOSED
    }

    inner class FacecraftWebsocketServer : WebSocketServer(InetSocketAddress(FACECRAFT_CENTRAL_PORT)) {

        override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {}

        override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
            if (conn != null) {
                // find server connection, if exists
                val connection = ConnectionManager.instance.websocketConnections[conn]

                if (connection != null) {
                    // remove references to this closed connection
                    ConnectionManager.instance.websocketConnections.remove(conn)
                    ConnectionManager.instance.serverWebsockets.remove(connection.address)
                }
            }
        }

        override fun onMessage(conn: WebSocket?, message: String?) {
            if (conn != null) {
                // find server if it exists
                val connection = ConnectionManager.instance.websocketConnections[conn]

                val packet : Packet
                try {
                    packet = gson.fromJson(message, Packet::class.java)
                } catch(e: Exception){
                    //ignore bad packets
                    return
                }

                if (connection != null) {
                    val listeners = packetListeners[packet.type]
                    if (listeners != null) {
                        for (listener in listeners)
                            listener.invoke(packet, connection)
                    }
                }
            }
        }

        override fun onStart() {
            status = Status.STARTED
        }

        override fun onError(conn: WebSocket?, ex: Exception?) {
            if (conn != null) {
                val connection = ConnectionManager.instance.websocketConnections[conn]
                if (connection != null && ex != null) {
                    println("Error with server [${connection.address}]: ${ex.message}")
                }
            }
        }

    }
}