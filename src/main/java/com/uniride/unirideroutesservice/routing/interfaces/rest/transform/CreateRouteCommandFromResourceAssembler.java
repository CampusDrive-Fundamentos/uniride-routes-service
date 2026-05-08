package com.uniride.unirideroutesservice.routing.interfaces.rest.transform;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.CreateRouteResource;

public class CreateRouteCommandFromResourceAssembler {
    public static CreateRouteCommand toCommandFromResource(CreateRouteResource resource) {
        return new CreateRouteCommand(resource.leaderId(), resource.campus(), resource.destinationAddress());
    }
}