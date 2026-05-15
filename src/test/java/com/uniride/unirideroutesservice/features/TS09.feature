Feature: Visualización del horario estimado del viaje
 Como estudiante
 Quiero ver una estimación de cuánto demorará el trayecto total
 Para avisar a mi familia a qué hora llegaré
 
Scenario: Cálculo y visualización del tiempo estimado de llegada (ETA)
 Given que el usuario está revisando la tarjeta de un viaje
 When la información de los segmentos de ruta se termina de procesar
 Then aparece el ETA (Estimated Time of Arrival) actualizado
 
Examples:
 |       Campo        |        Valor Mostrado al Usuario        | 
 | Duración del viaje | 45 minutos estimados (Tráfico moderado) | 
 | Hora de llegada    | Aprox. 23:15 hrs                        | 