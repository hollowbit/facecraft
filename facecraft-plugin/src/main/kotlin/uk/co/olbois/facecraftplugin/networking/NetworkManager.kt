package uk.co.olbois.facecraftplugin.networking

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import uk.co.olbois.facecraft.networking.packet.ConnectPacket
import uk.co.olbois.facecraft.networking.packet.RegisterPacket
import uk.co.olbois.facecraft.networking.packet.ResponsePacket
import uk.co.olbois.facecraftplugin.networking.packet.DisconnectPacket
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.lang.Exception
import java.net.URI

class NetworkManager private constructor() {

    private object Holder {val INSTANCE = NetworkManager()}

    companion object {
        const val FACECRAFT_CENTRAL_ADDRESS = "localhost"
        const val FACECRAFT_CENTRAL_PORT = 22123

        val instance: NetworkManager by lazy {Holder.INSTANCE}
    }

    private val socket = FacecraftWebsocketClient()

    private val responseListeners = mutableMapOf<Long, (ResponsePacket) -> Unit>()
    private val packetListeners = mutableMapOf<PacketType, MutableList<(Packet) -> ResponsePacket>>()

    private val gson = Gson()

    init {
        // create list of packet listeners for each packet typ
        for (p in PacketType.values())
            packetListeners[p] = mutableListOf()

        // connect right off the bat
        socket.connect()
    }

    var status = Status.CLOSED

    fun register(address: String, password: String, listener : (Boolean, String) -> Unit) {
        this.sendPacket(RegisterPacket(address, password)) { responsePacket: ResponsePacket ->
            // send register response
            listener.invoke(responsePacket.errorCode == 0, responsePacket.errorMessage)
        }
    }

    fun connect(address: String, password: String, listener : (Boolean, String) -> Unit) {
        this.sendPacket(ConnectPacket(address, password)) {responsePacket: ResponsePacket ->
            // change connection status if successful
            if (responsePacket.errorCode == 0) {
                status = Status.OPEN
            }

            // send connect response
            listener.invoke(responsePacket.errorCode == 0, responsePacket.errorMessage)
        }
    }

    fun disconnect() {
        status = Status.CLOSED
        this.sendPacket(DisconnectPacket()) {} // don't handle response
    }

    fun sendPacket(packet : Packet, responseListener : (ResponsePacket) -> Unit) : Boolean {
        // if not connected just return false
        if (status == Status.CLOSED)
            return false

        // add the packet listener to be used later when we get a response
        responseListeners[packet.id] = responseListener

        // send the packet
        socket.send(gson.toJson(packet))
        return true
    }

    fun registerListener(packetType : PacketType, listener : (Packet) -> ResponsePacket) {
        // add the listener
        packetListeners[packetType]?.add(listener)
    }

    fun deregisterListener(listener: (Packet) -> ResponsePacket) {
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
                packet = gson.fromJson(message, Packet::class.java)
            } catch (e : Exception) {
                // ignore back packets
                return
            }

            val listeners = packetListeners[packet.type]
            if (listeners != null) {
                for (listener in listeners)
                    listener.invoke(packet)
            }
        }

        override fun onError(ex: Exception?) {
            Bukkit.getServer().consoleSender.sendMessage("Facecraft Error: ${ex?.message}")
        }

    }

}