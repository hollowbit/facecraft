package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "servers")
class Server() {

    @Id
    @Column(name = "address")
    val address = ""

    @Column(name = "name")
    val name = ""

    @Column(name = "password")
    val password = ""

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable
    val owners: List<User> = listOf()

}