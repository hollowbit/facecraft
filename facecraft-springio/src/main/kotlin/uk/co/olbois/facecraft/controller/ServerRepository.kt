package uk.co.olbois.facecraft.controller

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import uk.co.olbois.facecraft.model.Server

@RepositoryRestResource(collectionResourceRel = "servers", path = "servers")
interface ServerRepository : CrudRepository<Server, String>