package uk.co.olbois.facecraft.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.networking.ConnectionManager
import uk.co.olbois.facecraft.networking.packet.ServerCommandPacket

@RestController()
class ServerCommandController {

    @PostMapping("servercommand/send")
    fun send(@RequestParam(value = "serverAddress") serverAddress: String, @RequestBody command : String): ResponseEntity<String> {
        // make sure the server is connected
        val conn = ConnectionManager.instance.getWebSocketByServer(serverAddress)
        if (conn != null) {
            Application.getNetworkManager().sendPacket(ServerCommandPacket(command), conn) {responsePacket, webSocket ->
                // don't bother handling a response
            }
            return ResponseEntity.ok().build<String>()
        } else
            return ResponseEntity.notFound().build()
    }

}