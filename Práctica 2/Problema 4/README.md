# 🛠️ Problema de Asignación de Tareas

## 📜 Descripción del Problema

Este problema trata sobre la asignación de tareas dentro de un proyecto, donde se busca optimizar el tiempo total de ejecución considerando tanto las duraciones específicas de cada tarea según el trabajador asignado, como las relaciones de precedencia entre las tareas.

### Condiciones del Problema:

1. **Tareas**: El proyecto está compuesto por varias tareas, cada una con una duración diferente dependiendo del trabajador que la realice. Cada tarea tiene un tiempo asignado para cada uno de los tres trabajadores disponibles.

2. **Duración por Trabajador**: La duración de cada tarea varía dependiendo del trabajador. Por ejemplo, si la tarea A tiene una duración de **3, 6, 9** días, eso significa que:
   - Si la realiza el **trabajador #1**, tardará 3 días.
   - Si la realiza el **trabajador #2**, tardará 6 días.
   - Si la realiza el **trabajador #3**, tardará 9 días.

3. **Precedencia de las Tareas**: Algunas tareas tienen restricciones sobre cuándo pueden comenzar, dependiendo de otras tareas que deben completarse previamente. Por ejemplo, si la tarea **F** depende de las tareas **C** y **D**, esas deben terminar antes de que **F** pueda comenzar.

4. **Restricción de Trabajadores**: Un trabajador puede estar trabajando en una sola tarea a la vez. Los tres trabajadores pueden estar trabajando simultáneamente en tareas diferentes.

### Requisitos:

- Encontrar una **asignación de tiempos de inicio** para las tareas que minimice el tiempo total de ejecución del proyecto.
- Considerar que un trabajador no puede realizar dos tareas al mismo tiempo.

---

### Tabla de Tareas:

| Tarea | Duración (Trabajador 1, Trabajador 2, Trabajador 3) | Predecesoras |
|-------|------------------------------------------------------|--------------|
| A     | 3, 6, 9                                              | Ninguna      |
| B     | 7, 5, 3                                              | A            |
| C     | 3, 2, 4                                              | B            |
| D     | 5, 5, 8                                              | A, C         |
| E     | 4, 3, 6                                              | C            |
| F     | 3, 2, 1                                              | C            |
| G     | 2, 2, 3                                              | C, D         |
| H     | 2, 4, 2                                              | G            |
| I     | 2, 3, 4                                              | F, H         |
| J     | 3, 2, 1                                              | G, I         |

### Objetivos del Problema:

1. **Parte a**: Encontrar la **duración mínima** del proyecto sin utilizar un cuarto trabajador.
   
2. **Parte b**: Considerar que se dispone de un **cuarto trabajador** que puede ser asignado a tareas cuya duración sea mayor o igual a 2 días. Este trabajador puede reducir la duración de una tarea en un día, pero solo puede apoyar un máximo de 5 tareas y no puede trabajar en tareas que se realicen a la vez.

   El objetivo aquí es **minimizar el tiempo de finalización** del proyecto utilizando este cuarto trabajador bajo las restricciones mencionadas.

### Formato de la Solución

La solución debe presentar las siguientes partes:

- **Parte a**: La **duración total** del proyecto sin el cuarto trabajador, es decir, el tiempo necesario para completar todas las tareas con los tres trabajadores disponibles.
  
- **Parte b**: La **nueva duración total** del proyecto considerando al cuarto trabajador y las restricciones del uso del mismo.

---

## 🧠 Enfoque de Resolución

Este problema se modelará como un **problema de optimización de programación** utilizando **restricciones de precedencia** y **restricciones de asignación de recursos** (trabajadores).

### Paso 1: Modelado en MiniZinc
Cada tarea tendrá variables asociadas a su tiempo de inicio y trabajador asignado, además de restricciones que aseguren que no haya solapamientos entre tareas y que se respeten las relaciones de precedencia.

### Paso 2: Solución del Problema
El modelo de MiniZinc se ejecutará para encontrar la **asignación de tiempos de inicio** que minimice la duración total del proyecto.

---
