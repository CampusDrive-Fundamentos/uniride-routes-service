Feature: Visualización de Total de la Carrera
 Como estudiante líder o conductor
 Quiero ver el costo total estimado de toda la ruta
 Para saber cuánto dinero en total cuesta el servicio de extremo a extremo.
 
Scenario: Cálculo y visualización de la tarifa total consolidada del viaje
 Given que el sistema ha terminado de trazar la ruta óptima con todas las paradas
 When el usuario visualiza la pantalla de resumen del viaje
 Then el sistema calcula la distancia total y muestra la tarifa sumada de la carrera completa

Examples:
 |       Campo           | Valor Mostrado al Usuario | 
 | Distancia Total       | 15.2 km                   | 
 | Costo Total del Viaje | S/ 18.50                  | 