package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.model.Server

internal class ConnectionManager private constructor() {

    companion object {
        val instance = ConnectionManager()
    }

    private val websocketConnections = mutableMapOf<WebSocket, String>()
    private val serverWebsockets = mutableMapOf<String, WebSocket>()

    fun addConnection(websocket: WebSocket, serverAddress: String) {
        websocketConnections[websocket] = serverAddress
        serverWebsockets[serverAddress] = websocket
    }

    fun removeConnection(websocket: WebSocket) {
        val serverAddress = websocketConnections[websocket]
        if (serverAddress != null)
            serverWebsockets.remove(serverAddress)

        websocketConnections.remove(websocket)
    }

    fun getServerByWebSocket(websocket : WebSocket) : String? {
        return websocketConnections[websocket]
    }

    fun getWebSocketByServer(server : Server) : WebSocket? {
        return serverWebsockets[server.address]
    }

    fun getWebSocketByServer(serverAddress : String) : WebSocket? {
        return serverWebsockets[serverAddress]
    }

}