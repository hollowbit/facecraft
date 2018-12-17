package uk.co.olbois.facecraft.networking

import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import uk.co.olbois.facecraft.networking.packet.PacketWrapper
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.lang.Exception
import java.net.InetSocketAddress

class NetworkManager {

    companion object {
        const val FACECRAFT_CENTRAL_PORT = 22123
    }

    private val server = FacecraftWebsocketServer()

    private val gson = Gson()

    private val responseListeners = mutableMapOf<Long, (ResponsePacket, WebSocket) -> Unit>()
    private val packetListeners = mutableMapOf<PacketType, MutableList<(Packet, WebSocket) -> ResponsePacket?>>()

    init {
        // create list of packet listeners for each packet typ
        for (p in PacketType.values())
            packetListeners[p] = mutableListOf()

        // init the server connection packet listener to accept connect and register requests
        ServerConnectionPacketListener(this)
    }

    var status = Status.CLOSED

    fun start() {
        server.start()
        println("WebSocket Server Started!")
    }

    fun stop() {
        status = Status.CLOSED
        server.stop()
        println("WebSocket Server Stopped.")
    }

    fun registerListener(packetType : PacketType, listener : (Packet, WebSocket) -> ResponsePacket?) {
        // add the listener
        packetListeners[packetType]?.add(listener)
    }

    fun deregisterListener(listener: (Packet, WebSocket) -> ResponsePacket?) {
        // check each packet type and remove the listener
        for (list in packetListeners.values)
            list.remove(listener)
    }

    fun sendPacket(packet: Packet, socket: WebSocket, responseListener: (ResponsePacket, WebSocket) -> Unit) : Boolean {
        // if not connected just return false
        if (status == Status.CLOSED)
            return false

        // add the packet listener to be used later when we get a response
        responseListeners[packet.id] = responseListener

        // send the packet
        val wrapper = PacketWrapper(packet.type, gson.toJson(packet))
        socket.send(gson.toJson(wrapper))
        return true
    }

    private fun sendResponsePacket(packet: ResponsePacket, socket: WebSocket) {
        // if not connected just return false
        if (status == Status.CLOSED)
            return

        // send the packet
        val wrapper = PacketWrapper(packet.type, gson.toJson(packet))
        socket.send(gson.toJson(wrapper))
    }

    enum class Status {
        STARTED, CLOSED
    }

    inner class FacecraftWebsocketServer : WebSocketServer(InetSocketAddress(FACECRAFT_CENTRAL_PORT)) {

        override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {}

        override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
            if (conn != null) {
                // remove references to this closed connection
                ConnectionManager.instance.removeConnection(conn)
            }
        }

        override fun onMessage(conn: WebSocket?, message: String?) {
            if (conn != null) {
                val packet : Packet
                try {
                    val wrapper = gson.fromJson(message, PacketWrapper::class.java)
                    packet = gson.fromJson(wrapper.packet, wrapper.type.clazz) as Packet
                } catch(e: Exception){
                    //ignore bad packets
                    return
                }

                // if this is a response, handle it
                if (packet is ResponsePacket) {
                    val responseListener = responseListeners[packet.originId]
                    if (responseListener != null) {
                        responseListener.invoke(packet, conn)
                        responseListeners.remove(packet.originId)
                    }
                }

                // find listeners and invoke them
                val listeners = packetListeners[packet.type]
                if (listeners != null) {
                   for (listener in listeners) {
                       val responsePacket = listener.invoke(packet, conn)
                       if (responsePacket != null) {
                           sendResponsePacket(responsePacket, conn)
                           break
                       }
                   }
                }

            }
        }

        override fun onStart() {
            status = Status.STARTED
        }

        override fun onError(conn: WebSocket?, ex: Exception?) {
            if (conn != null && ex != null) {
                val serverAddress = ConnectionManager.instance.getServerByWebSocket(conn)
                if (serverAddress != null)
                    println("Error with server [$serverAddress]: ${ex.message}")
                else
                    println("Error with disconnected server: ${ex.message}")
            }
        }

    }
}