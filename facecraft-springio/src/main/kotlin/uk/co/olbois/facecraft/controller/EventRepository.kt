package uk.co.olbois.facecraft.controller

import uk.co.olbois.facecraft.model.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
interface EventRepository : CrudRepository<Event, Long>