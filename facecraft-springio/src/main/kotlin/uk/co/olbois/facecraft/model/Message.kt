package uk.co.olbois.facecraft.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "messages")
class Message() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var message_id = ""

    @Column(name = "username")
    var username = ""

    @Column(name = "message")
    var message = ""

    @Column(name = "date")
    var date = ""

    @Column(name = "sender_type")
    var senderType = ""

    @Column(name = "server")
    var serverAddr = ""
}