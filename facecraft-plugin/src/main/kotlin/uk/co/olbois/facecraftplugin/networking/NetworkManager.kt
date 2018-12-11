package uk.co.olbois.facecraftplugin.networking

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import uk.co.olbois.facecraftplugin.networking.packet.*
import java.lang.Exception
import java.net.URI

class NetworkManager {

    companion object {
        const val FACECRAFT_CENTRAL_ADDRESS = "localhost"
        const val FACECRAFT_CENTRAL_PORT = 22123
    }

    private val socket = FacecraftWebsocketClient()

    private val responseListeners = mutableMapOf<Long, (ResponsePacket) -> Unit>()
    private val packetListeners = mutableMapOf<PacketType, MutableList<(Packet) -> ResponsePacket?>>()

    private val gson = Gson()

    init {
        // create list of packet listeners for each packet typ
        for (p in PacketType.values())
            packetListeners[p] = mutableListOf()
    }

    var status = Status.CLOSED

    fun load() {
        // connect right off the bat
        socket.connect()
    }

    fun unload() {
        // close the socket if unloaded
        socket.close()
    }

    fun canConnect() : Boolean {
        return socket.isOpen
    }

    fun register(address: String, name : String, password: String, listener : (Boolean, String) -> Unit) {
        if (socket.isClosed)
            return

        this.sendPacket(RegisterPacket(address, name, password)) { responsePacket: ResponsePacket ->
            // send register response
            listener.invoke(responsePacket.errorCode == 0, responsePacket.errorMessage)
        }
    }

    fun connect(address: String, password: String, listener : (Boolean, String) -> Unit) {
        if (socket.isClosed)
            return

        this.sendPacket(ConnectPacket(address, password)) {responsePacket: ResponsePacket ->
            // change connection status if successful
            if (responsePacket.errorCode == 0) {
                status = Status.OPEN
                Bukkit.getServer().consoleSender.sendMessage("Connected to Facecraft Central Server!")
            }

            // send connect response
            listener.invoke(responsePacket.errorCode == 0, responsePacket.errorMessage)
        }
    }

    fun disconnect() {
        if (socket.isClosed)
            return

        status = Status.CLOSED
        Bukkit.getServer().consoleSender.sendMessage("Disconnected to Facecraft Central Server.")
        this.sendPacket(DisconnectPacket()) {} // don't handle response
    }

    fun sendPacket(packet : Packet, responseListener : (ResponsePacket) -> Unit) : Boolean {
        // add the packet listener to be used later when we get a response
        responseListeners[packet.id] = responseListener

        // send the packet
        val wrapper = PacketWrapper(packet.type, gson.toJson(packet))
        socket.send(gson.toJson(wrapper))
        return true
    }

    private fun sendResponsePacket(packet : ResponsePacket) {
        // send the packet
        val wrapper = PacketWrapper(packet.type, gson.toJson(packet))
        socket.send(gson.toJson(wrapper))
    }

    fun registerListener(packetType : PacketType, listener : (Packet) -> ResponsePacket?) {
        // add the listener
        packetListeners[packetType]?.add(listener)
    }

    fun deregisterListener(listener: (Packet) -> ResponsePacket?) {
        // check each packet type and remove the listener
        for (list in packetListeners.values)
            list.remove(listener)
    }

    enum class Status {
        CLOSED, OPEN
    }

    private inner class FacecraftWebsocketClient : WebSocketClient(URI.create("ws://$FACECRAFT_CENTRAL_ADDRESS:$FACECRAFT_CENTRAL_PORT")) {

        override fun onOpen(handshakedata: ServerHandshake?) {}

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            status = Status.CLOSED
        }

        override fun onMessage(message: String?) {
            val packet : Packet
            try {
                val wrapper = gson.fromJson(message, PacketWrapper::class.java)
                packet = gson.fromJson(wrapper.packet, wrapper.type.clazz) as Packet
            } catch (e : Exception) {
                // ignore back packets
                return
            }

            // handle response packet
            if (packet is ResponsePacket) {
                println("Test: $message")
                val responseListener = responseListeners[packet.originId]
                if (responseListener != null) {
                    responseListener.invoke(packet)
                    responseListeners.remove(packet.originId)
                }
            }

            // find listeners and handle packets
            val listeners = packetListeners[packet.type]
            if (listeners != null) {
                for (listener in listeners) {
                    val responsePacket = listener.invoke(packet)
                    if (responsePacket != null) {
                        sendResponsePacket(responsePacket)
                        break
                    }
                }
            }
        }

        override fun onError(ex: Exception?) {
            ex?.printStackTrace()
            Bukkit.getServer().consoleSender.sendMessage("Facecraft Error: ${ex?.message}")
        }

    }

}