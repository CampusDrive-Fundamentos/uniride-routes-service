package com.uniride.unirideroutesservice.routing.application.internal.commandservices;

import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenRouteServiceIntegration;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenStreetMapGeocodingService;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.PolylineDecoder;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Visibility;
import com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteCommandServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private OpenStreetMapGeocodingService geocodingService;

    @Mock
    private OpenRouteServiceIntegration orsIntegration;

    @Mock
    private PolylineDecoder polylineDecoder;

    @InjectMocks
    private RouteCommandServiceImpl routeCommandService;

    private CreateRouteCommand createRouteCommand;

    @BeforeEach
    void setUp() {
        createRouteCommand = new CreateRouteCommand(
                1L,
                UniversityCampus.UPC_SAN_MIGUEL,
                "Av. Universitaria 1801",
                -12.0734,
                -77.0818);
    }

    @Test
    void handleCreateRouteCommand_ShouldReturnSavedRoute() {
        // Arrange
        Map<String, Object> orsResult = new HashMap<>();
        orsResult.put("geometry", "encoded_polyline");
        orsResult.put("distanceKm", 10.5);

        when(orsIntegration.calculateOptimalRoute(any(), any(), any())).thenReturn(orsResult);
        when(routeRepository.save(any(Route.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Route> result = routeCommandService.handle(createRouteCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("encoded_polyline", result.get().getEncodedPolyline());
        assertEquals(10.5, result.get().getTotalDistanceKm());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void updateVisibility_ShouldUpdateRouteVisibility_WhenRouteExists() {
        // Arrange
        Long routeId = 1L;
        Visibility newVisibility = Visibility.HIDDEN;
        Route route = new Route();
        route.setVisibility(Visibility.SEARCHABLE);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        // Act
        routeCommandService.updateVisibility(routeId, newVisibility);

        // Assert
        assertEquals(newVisibility, route.getVisibility());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void deleteRoute_ShouldCallDeleteById_WhenRouteExists() {
        // Arrange
        Long routeId = 1L;
        when(routeRepository.existsById(routeId)).thenReturn(true);

        // Act
        routeCommandService.deleteRoute(routeId);

        // Assert
        verify(routeRepository, times(1)).deleteById(routeId);
    }
}
