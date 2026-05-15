Feature:Visualización de monto individual
 Como estudiante pasajero
 Quiero ver exactamente cuánto me toca pagar según mi punto de bajada
 Para aportar un precio justo y proporcional a la distancia que voy a recorrer
 
Scenario: Cálculo de tarifa individualizada según distancia de bajada
 Given que el estudiante visualiza un anuncio y añade su dirección de destino
 When el sistema calcula en qué kilómetro exacto se bajará este pasajero
 Then se le muestra una tarifa personalizada y prorrateada antes de que confirme su unión al grupo

Examples:
 |       Campo           | Valor Mostrado al Usuario               | 
 | Tu parada (Destino)   | Av. Javier Prado Este (km 5 de la ruta) | 
 | Tu aporte individual  | S/ 5.50                                 | 