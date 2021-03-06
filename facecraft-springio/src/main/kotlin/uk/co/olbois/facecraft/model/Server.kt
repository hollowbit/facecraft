package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "servers")
class Server {

    @Id
    @Column(name = "address")
    var address = ""

    @Column(name = "name")
    var name = ""

    @Column(name = "password")
    var password = ""
    
    @ManyToMany(cascade = [CascadeType.ALL], fetch=FetchType.EAGER)
    @JoinTable
    val owners: MutableList<User> = mutableListOf()

    @ManyToMany(cascade = [CascadeType.ALL])
    val members: List<User> = listOf()

    @OneToMany(mappedBy = "server", targetEntity = ConsoleOutput::class)
    private val consoleOutputs: List<ConsoleOutput> = mutableListOf()
  
    override fun equals(other: Any?): Boolean {
        return when(other is Server) {
            true -> other.address == this.address
            false -> false
        }
    }
}