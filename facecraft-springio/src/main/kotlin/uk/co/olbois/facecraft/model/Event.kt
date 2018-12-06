package uk.co.olbois.facecraft.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "event")
class Event() {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        val id: Long = 0

        @Temporal (value = TemporalType.TIMESTAMP)
        @Column(name = "date")
        val date : Date = Date()

        @Column(name = "title")
        val title : String = ""

        @Column(name = "description")
        val description : String = ""

        @Column(name = "serverId")
        val serverId : Long = 0

}
