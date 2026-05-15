Feature: Visualización del horario estimado del viaje
 Como estudiante
 Quiero poder abandonar el grupo
 Para que el sistema borre mi parada y recalcule un camino más rápido para los demás
 
Scenario: Eliminación de waypoint y actualización de ruta tras salida de pasajero
 Given que el estudiante presiona "Salir del viaje" antes de arrancar
 When la petición de borrado de coordenada se ejecuta en el servicio de rutas
 Then su parada desaparece del mapa y el tiempo de viaje (ETA) disminuye para el resto.
 
Examples:
 |       Campo        |        Valor Mostrado al Usuario        | 
 | Duración del viaje | 45 minutos estimados (Tráfico moderado) | 
 | Hora de llegada    | Aprox. 23:15 hrs                        | 