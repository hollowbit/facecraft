package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "servers")
class Message() {

    @Id
    @Column(name = "messageId")
    var address = ""

    @Column(name = "name")
    var name = ""

    @Column(name = "type")
    var gameType = ""

    @Column(name = "message")
    var message = ""

    @Column(name = "time")
    var time = ""
}