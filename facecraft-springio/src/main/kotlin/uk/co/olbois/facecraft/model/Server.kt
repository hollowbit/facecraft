package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "servers")
class Server() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "server_id")
    val id: Long = 0

    @Column(name = "name")
    val name = ""

    @Column(name = "address")
    val address = ""

    @Column(name = "password")
    val password = ""

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable
    val owners: List<User> = listOf()

}