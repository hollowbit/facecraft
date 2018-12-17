package uk.co.olbois.facecraft.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "events")
class Event() {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "event_id")
        val id: Long = 0

        @Temporal (value = TemporalType.TIMESTAMP)
        @Column(name = "date")
        val date : Date = Date()

        @Column(name = "title")
        val title : String = ""

        @Column(name = "description")
        val description : String = ""

        @ManyToOne
        @JoinColumn(name = "server_id")
        @OnDelete(action = OnDeleteAction.CASCADE)
        val server : Server = Server()

}
