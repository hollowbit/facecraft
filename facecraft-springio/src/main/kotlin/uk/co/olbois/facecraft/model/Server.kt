package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "servers")
class Server() {

    @Id
    @Column(name = "address")
    var address = ""

    @Column(name = "name")
    var name = ""

    @Column(name = "password")
    var password = ""

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable
    val owners: List<User> = listOf()

}