package uk.co.olbois.facecraft.controller

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import uk.co.olbois.facecraft.model.Invite

@RepositoryRestResource(collectionResourceRel = "invites", path = "invites")
interface InviteRepository : CrudRepository<Invite, Long>