package uk.co.olbois.facecraft.controller

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import uk.co.olbois.facecraft.model.Message
import uk.co.olbois.facecraft.model.User

@RepositoryRestResource(collectionResourceRel = "messages", path = "messages")
interface MessageRepository : CrudRepository<Message, Long>