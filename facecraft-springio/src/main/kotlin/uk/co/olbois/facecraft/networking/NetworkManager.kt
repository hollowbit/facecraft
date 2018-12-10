package uk.co.olbois.facecraft.networking

import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import uk.co.olbois.facecraft.model.Server
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

    private val websocketConnections = mutableMapOf<WebSocket, Server>()
    private val serverWebsockets = mutableMapOf<String, WebSocket>()

    private val responseListeners = mutableMapOf<Long, (Packet, Server) -> Unit>()
    private val packetListeners = mutableMapOf<PacketType, MutableList<(Packet, Server) -> Unit>>()

    var status = Status.CLOSED

    fun start() {
        server.start()
    }

    fun stop() {
        status = Status.CLOSED
        server.stop()
    }

    fun registerListener(packetType : PacketType, listener : (Packet, Server) -> Unit) {
        // add the listener
        packetListeners[packetType]?.add(listener)
    }

    fun deregisterListener(listener: (Packet, Server) -> Unit) {
        // check each packet type and remove the listener
        for (list in packetListeners.values)
            list.remove(listener)
    }

    fun sendPacket(packet: Packet, server: Server, responseListener: (Packet, Server) -> Unit) : Boolean {
        // if not connected just return false
        if (status == Status.CLOSED)
            return false

        // add the packet listener to be used later when we get a response
        responseListeners[packet.id] = responseListener

        // send the packet
        val socket = serverWebsockets[server.address]
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
                val connection = websocketConnections[conn]

                if (connection != null) {
                    // remove references to this closed connection
                    websocketConnections.remove(conn)
                    serverWebsockets.remove(connection.address)
                }
            }
        }

        override fun onMessage(conn: WebSocket?, message: String?) {
            if (conn != null) {
                // find server if it exists
                val connection = websocketConnections[conn]

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
                val connection = websocketConnections[conn]
                if (connection != null && ex != null) {
                    println("Error with server [${connection.address}]: ${ex.message}")
                }
            }
        }

    }
}