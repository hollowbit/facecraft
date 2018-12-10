package uk.co.olbois.facecraftplugin.networking

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import uk.co.olbois.facecraftplugin.networking.packet.Packet
import uk.co.olbois.facecraftplugin.networking.packet.PacketType
import java.lang.Exception
import java.net.URI

class NetworkManager private constructor() {

    private object Holder {val INSTANCE = NetworkManager()}

    companion object {
        const val FACECRAFT_CENTRAL_ADDRESS = "localhost"
        const val FACECRAFT_CENTRAL_PORT = 22123

        const val CLEAN_CLOSE_CODE = 1000

        val instance: NetworkManager by lazy {Holder.INSTANCE}
    }

    private val socket = FacecraftWebsocketClient()

    private val responseListeners = mutableMapOf<Long, (Packet) -> Unit>()
    private val packetListeners = mutableMapOf<PacketType, MutableList<(Packet) -> Unit>>()
    private var connectListener : ((Boolean) -> Unit)? = null
    private var disconnectListener : ((Boolean) -> Unit)? = null

    private val gson = Gson()

    init {
        // create list of packet listeners for each packet typ
        for (p in PacketType.values())
            packetListeners[p] = mutableListOf()
    }

    fun connect(listener: (Boolean) -> Unit) {
        connectListener = listener
        socket.connect()
    }

    val status : Status get() {
        // return simplified status for outside
        return when(socket.isOpen) {
            true -> Status.OPEN
            false -> Status.CLOSED
        }
    }

    fun disconnect(listener: (Boolean) -> Unit) {
        disconnectListener = listener
        socket.close(CLEAN_CLOSE_CODE)
    }

    fun sendPacket(packet : Packet, responseListener : (Packet) -> Unit) : Boolean {
        // if not connected just return false
        if (status == Status.CLOSED)
            return false

        // add the packet listener to be used later when we get a response
        responseListeners[packet.id] = responseListener

        // send the packet
        socket.send(gson.toJson(packet))
        return true
    }

    fun registerListener(packetType : PacketType, listener : (Packet) -> Unit) {
        // add the listener
        packetListeners[packetType]?.add(listener)
    }

    fun deregisterListener(listener: (Packet) -> Unit) {
        // check each packet type and remove the listener
        for (list in packetListeners.values)
            list.remove(listener)
    }

    enum class Status {
        CLOSED, OPEN
    }

    private inner class FacecraftWebsocketClient : WebSocketClient(URI.create("ws://$FACECRAFT_CENTRAL_ADDRESS:$FACECRAFT_CENTRAL_PORT")) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            connectListener?.invoke(true)
            connectListener = null
            Bukkit.getServer().consoleSender.sendMessage("Connection to Facecraft Central Server established!")
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            disconnectListener?.invoke(true)
            disconnectListener = null
            Bukkit.getServer().consoleSender.sendMessage("Connection to Facecraft Central Server closed.")
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
            connectListener?.invoke(false)
            connectListener = null
            disconnectListener?.invoke(false)
            disconnectListener = null
            Bukkit.getServer().consoleSender.sendMessage("Facecraft Error: ${ex?.message}")
        }

    }

}