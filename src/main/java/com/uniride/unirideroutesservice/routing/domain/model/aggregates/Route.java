package com.uniride.unirideroutesservice.routing.domain.model.aggregates;

import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import com.uniride.unirideroutesservice.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status;

    public Route(CreateRouteCommand command, Location destinationLocation) {
        this.leaderId = command.leaderId();
        this.startCampus = command.campus();
        this.startLocation = new Location(command.campus().getLatitude(), command.campus().getLongitude(), command.campus().getDescription());
        this.destination = destinationLocation;
        this.status = RouteStatus.PENDING;
    }
}