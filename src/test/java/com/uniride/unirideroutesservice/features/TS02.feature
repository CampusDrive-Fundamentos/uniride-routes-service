Feature: Creación de Viaje Grupal (Trazado Inicial)
 Como estudiante líder
 Quiero establecer el campus de salida y mi destino final
 Para que el mapa trace la ruta base del viaje
 
Scenario: Trazado inicial de ruta desde origen a destino
 Given que el líder ingresa su punto de partida y la dirección a donde se dirige
 When presiona el botón "Crear ruta"
 Then el sistema renderiza el mapa, la polilínea inicial y calcula el tiempo y distancia base
 
Examples:
 |       Campo        |        Valor Mostrado al Usuario        | 
 | Mapa Visual        | [Línea trazada desde UPC hasta Destino] | 
 | Distancia y Tiempo | 12.5 km • Aprox. 25 min                 | 