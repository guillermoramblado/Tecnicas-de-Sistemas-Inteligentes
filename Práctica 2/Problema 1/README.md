# 🗺️ Problema del Coloreado de Mapas

## 📜 Descripción del Problema

En este ejercicio, se busca colorear el mapa de **América del Sur** utilizando 5 colores de forma que dos países limítrofes nunca tengan el mismo color. Además, el coloreado tiene un coste asociado, y el objetivo es minimizar el coste total de la coloración.

### Requisitos:
- **Países y vecinos**: Se debe tener en cuenta la lista de países vecinos y asegurarse de que no se asignen los mismos colores a los países que comparten frontera.
  
- **Colores y costes**: Los colores disponibles son los siguientes, con los respectivos costes:

  | Color   | Coste (€) |
  |---------|-----------|
  | Verde   | 100       |
  | Rojo    | 250       |
  | Naranja | 350       |
  | Azul    | 450       |

- **Objetivo**:
  - Encontrar la coloración de los países que minimice el coste total, respetando la condición de que países limítrofes no tengan el mismo color.
  - Una vez haya resuelto este problema de optimización de restricciones (COP), plantéelo como un problema de satisfacción de restricciones (CSP) 

### Países y sus vecinos:
| País            | Vecinos                                                       |
|-----------------|---------------------------------------------------------------|
| Argentina       | Bolivia, Brasil, Chile, Paraguay, Uruguay                      |
| Bolivia         | Argentina, Brasil, Chile, Paraguay, Perú                       |
| Brasil          | Argentina, Bolivia, Colombia, Guayana Francesa, Guyana, Paraguay, Perú, Surinam, Uruguay, Venezuela |
| Chile           | Argentina, Bolivia, Perú                                       |
| Colombia        | Brasil, Ecuador, Perú, Venezuela                               |
| Ecuador         | Colombia, Perú                                                 |
| Guayana Francesa| Brasil, Surinam                                                |
| Guyana          | Brasil, Surinam, Venezuela                                      |
| Paraguay        | Argentina, Bolivia, Brasil, Uruguay                             |
| Perú            | Bolivia, Brasil, Chile, Colombia, Ecuador                       |
| Surinam         | Brasil, Guayana Francesa, Guyana                               |
| Uruguay         | Argentina, Brasil, Paraguay                                     |
| Venezuela       | Brasil, Colombia, Guyana                                       |

### 

### Formato de la Solución:
La solución debe indicar el color asignado a cada país por orden alfabético y el coste total de la coloración.
