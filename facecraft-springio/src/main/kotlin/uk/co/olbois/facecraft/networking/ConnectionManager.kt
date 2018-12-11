package uk.co.olbois.facecraft.networking

import org.java_websocket.WebSocket
import uk.co.olbois.facecraft.model.Server

internal class ConnectionManager private constructor() {

    companion object {
        val instance = ConnectionManager()
    }

    private val websocketConnections = mutableMapOf<WebSocket, Server>()
    private val serverWebsockets = mutableMapOf<String, WebSocket>()

    fun addConnection(websocket: WebSocket, server: Server) {
        websocketConnections[websocket] = server
        serverWebsockets[server.address] = websocket
    }

    fun removeConnection(websocket: WebSocket) {
        val server = websocketConnections[websocket]
        if (server != null)
            serverWebsockets.remove(server.address)

        websocketConnections.remove(websocket)
    }

    fun getServerByWebSocket(websocket : WebSocket) : Server? {
        return websocketConnections[websocket]
    }

    fun getWebSocketByServer(server : Server) : WebSocket? {
        return serverWebsockets[server.address]
    }

}