package uk.co.olbois.facecraft.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.co.olbois.facecraft.Application
import uk.co.olbois.facecraft.model.Message
import uk.co.olbois.facecraft.networking.ConnectionManager
import uk.co.olbois.facecraft.networking.packet.MessagePacket
import uk.co.olbois.facecraft.networking.packet.ServerCommandPacket
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@RestController()
class MessageController {

    @PostMapping("messages/send")
    fun send(@RequestBody stringMsg: String): ResponseEntity<String> {

        var argss = stringMsg.split('Ω')
        // make sure the server is connected
        val conn = ConnectionManager.instance.getWebSocketByServer(argss[4])
        val sdl = SimpleDateFormat("MMM dd, YYYY h:mm:ss aa")

        if (conn != null) {
            Application.getNetworkManager().sendPacket(MessagePacket(argss[2], argss[0], argss[1], sdl.parse(argss[3])), conn) { responsePacket, webSocket ->
                // don't bother handling a response
            }

            return ResponseEntity.ok().build<String>()
        } else
            return ResponseEntity.notFound().build()
    }

}//var username : String, var senderType : String, var message : String, var date : Date, var serverAddr : String, var url : String?)