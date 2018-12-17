package uk.co.olbois.facecraft.controller

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import uk.co.olbois.facecraft.model.ConsoleOutput

@RepositoryRestResource(collectionResourceRel = "consoleoutputs", path = "consoleoutputs")
interface ConsoleOutputRepository : CrudRepository<ConsoleOutput, Long> {

    @Query("SELECT c FROM ConsoleOutput c WHERE server.address = :serverAddress AND c.id > :lastId")
    fun findAllSinceLastByServer(@Param("lastId") lastId: Long, @Param("serverAddress") serverAddress: String) : List<ConsoleOutput>

}