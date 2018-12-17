package uk.co.olbois.facecraft.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "ConsoleOutput")
class ConsoleOutput() {

    constructor(message: String, server: Server) : this() {
        this.message = message
        this.server = server
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "message")
    var message: String = ""

    @ManyToOne(targetEntity = Server::class)
    @JoinColumn(name = "server")
    @OnDelete(action = OnDeleteAction.CASCADE)
    var server: Server = Server()

    override fun equals(other: Any?): Boolean {
        return when (other is ConsoleOutput) {
            true -> this.id == other.id
            false -> false
        }
    }

}