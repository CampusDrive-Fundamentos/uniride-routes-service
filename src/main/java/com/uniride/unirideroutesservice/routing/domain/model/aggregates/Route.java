// src/main/java/com/uniride/unirideroutesservice/routing/domain/model/aggregates/Route.java
package com.uniride.unirideroutesservice.routing.domain.model.aggregates;

import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import com.uniride.unirideroutesservice.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "routes")
public class Route extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long leaderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UniversityCampus startCampus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude", column=@Column(name="start_lat")),
            @AttributeOverride(name="longitude", column=@Column(name="start_lng")),
            @AttributeOverride(name="address", column=@Column(name="start_address"))
    })
    private Location startLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude", column=@Column(name="dest_lat")),
            @AttributeOverride(name="longitude", column=@Column(name="dest_lng")),
            @AttributeOverride(name="address", column=@Column(name="dest_address"))
    })
    private Location destination;

    // Aquí guardamos la ruta dibujada (texto codificado)
    @Column(columnDefinition = "TEXT")
    private String encodedPolyline;

    @Column(nullable = false)
    private Double totalDistanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status;

    // Paradas Intermedias (Seguidores)
    @ElementCollection
    @CollectionTable(name = "route_waypoints", joinColumns = @JoinColumn(name = "route_id"))
    @OrderColumn(name = "stop_order")
    private List<Location> waypoints = new ArrayList<>();

    public Route(CreateRouteCommand command, Location destinationLocation) {
        this.leaderId = command.leaderId();
        this.startCampus = command.campus();
        this.startLocation = new Location(command.campus().getLatitude(), command.campus().getLongitude(), command.campus().getDescription());
        this.destination = destinationLocation;
        this.status = RouteStatus.PENDING;
        this.totalDistanceKm = 0.0;
    }
}