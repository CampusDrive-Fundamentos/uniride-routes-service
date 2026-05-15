Feature: Búsqueda Basada en el Algoritmo de 500m
 Como estudiante seguidor
 Quiero ingresar la dirección de mi casa
 Para ver únicamente los vehículos cuya ruta pase a menos de 500 metros de mi destino
 
Scenario: Búsqueda y filtrado geoespacial por radio de proximidad (500m)
 Given que el estudiante escribe su destino en el buscador
 When ejecuta la búsqueda de anuncios disponibles
 Then la lista se filtra geoespacialmente para mostrar solo las rutas que cruzan cerca a él
 
Examples:
 | Rutas disponibles        | Proximidad a tu destino              | 
 | CampusDrive - Auto Gris  | El conductor pasa a 300 metros de ti | 
 | CampusDrive - SUV Negra  | El conductor pasa a 450 metros de ti | 