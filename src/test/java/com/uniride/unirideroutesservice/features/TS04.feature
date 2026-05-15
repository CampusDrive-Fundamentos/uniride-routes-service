Feature: Visualización de Ruta Optimizada
 Como conductor
 Quiero ver el camino óptimo dibujado en el mapa de navegación
 Para seguir la secuencia correcta y no dar vueltas innecesarias.
 
Scenario: Generación de ruta optimizada para navegación del conductor
 Given que el grupo ya está completo con múltiples paradas
 When el conductor abre la pantalla de inicio de viaje
 Then el sistema muestra la ruta optimizada ordenando los puntos de bajada lógicamente
 
Examples:
 |       Campo          | Valor Mostrado al Usuario                          | 
 | Ruta de Navegación   | [Mapa interactivo activo]                          | 
 | Secuencia de Paradas | 1. Juan (km 3) → 2. María (km 5) → 3. Líder (km 8) | 