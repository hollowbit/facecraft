package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "messages")
class Message() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val message_id = ""

    @Column(name = "username")
    val username = ""

    @Column(name = "message")
    val message = ""

    @Column(name = "date")
    val date = ""

    @Column(name = "sender_type")
    val senderType = ""

    @Column(name = "server")
    val serverAddr = ""
}