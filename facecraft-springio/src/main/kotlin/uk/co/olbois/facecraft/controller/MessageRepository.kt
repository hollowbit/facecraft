package uk.co.olbois.facecraft.controller

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import uk.co.olbois.facecraft.model.Server

@RepositoryRestResource(collectionResourceRel = "messages", path = "messages")
interface MessageRepository : CrudRepository<Server, Long>