package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "users")
class User() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long = 0

    @Column(name = "username")
    val username = ""

    @Column(name = "password")
    val password = ""

    @Column(name = "deviceToken")
    val deviceToken = ""

    @ManyToMany(mappedBy = "owners")
    val serversOwned: List<Server> = listOf()

    @ManyToMany(mappedBy = "members")
    val serversPartOf: List<Server> = listOf()
}