Feature: Visualización del horario estimado del viaje
 Como estudiante
 Quiero que el mapa registre mi casa como punto de bajada
 Para que la ruta general del vehículo se desvíe lo necesario para dejarme
 
Scenario: Recálculo de ruta y polígono tras inyección de nueva parada
 Given que el estudiante es aceptado en el grupo
 When el sistema inyecta sus coordenadas al servicio de rutas
 Then el polígono del mapa se recalcula y se añade un nuevo "pin" (parada) en la ruta del conductor

Examples:
 |       Campo        | Valor Mostrado al Usuario | 
 | Estado de Ruta     | Recalculada exitosamente  | 
 | Paradas Totales    | 3 paradas asignadas       | 