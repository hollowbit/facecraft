package uk.co.olbois.facecraft.model

import javax.persistence.*

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long = 0

    @Column(name = "name")
    val name = ""

    @ManyToMany(mappedBy = "owners", fetch=FetchType.EAGER)
    val serversOwned: MutableList<Server> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        return when(other is User) {
            true -> other.id == this.id
            false -> false
        }
    }

}