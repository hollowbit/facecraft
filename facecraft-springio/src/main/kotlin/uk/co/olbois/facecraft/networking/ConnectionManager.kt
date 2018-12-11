package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.model.Server

internal class ConnectionManager private constructor() {

    companion object {
        val instance = ConnectionManager()
    }

    val websocketConnections = mutableMapOf<WebSocket, Server>()
    val serverWebsockets = mutableMapOf<String, WebSocket>()

}