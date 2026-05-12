package com.uniride.unirideroutesservice.routing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data required to create a new route (Leader)")
public record CreateRouteResource(
        @Schema(description = "Campus ID string", example = "MONTERRICO")
        String campus,

        @Schema(description = "Text address of the destination", example = "Parque Kennedy, Miraflores")
        String destinationAddress,

        @Schema(description = "Optional explicit latitude", example = "-12.1210")
        Double destinationLat,

        @Schema(description = "Optional explicit longitude", example = "-77.0290")
        Double destinationLng
) {
}