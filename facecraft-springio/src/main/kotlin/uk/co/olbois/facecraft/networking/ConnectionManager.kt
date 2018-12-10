package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.model.Server

internal class ConnectionManager {

    private object Holder {val INSTANCE = ConnectionManager()}

    companion object {
        val instance: ConnectionManager by lazy { ConnectionManager.Holder.INSTANCE}
    }

    val websocketConnections = mutableMapOf<WebSocket, Server>()
    val serverWebsockets = mutableMapOf<String, WebSocket>()

}