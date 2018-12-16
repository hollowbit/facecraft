package uk.co.olbois.facecraft.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "invites")
class Invite() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by_id", nullable = false)
    val invited_by: User = User()

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_user_id", nullable = false)
    val invited_user_id: User = User()

    @ManyToOne
    @JoinColumn(name = "server_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val server: Server = Server()
}