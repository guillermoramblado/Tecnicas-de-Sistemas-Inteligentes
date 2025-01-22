# 🗓️ Problema de los Horarios

## 📜 Descripción del Problema

En este ejercicio, el objetivo es encontrar una asignación de horarios para un conjunto de asignaturas en una semana, cumpliendo con ciertas restricciones tanto de tiempo como de disponibilidad de profesores y asignaturas. 

### Condiciones del Problema:
1. **Aula Disponible**: Existe un aula disponible de **8:00 a 15:00**, de lunes a viernes.
2. **Asignaturas**: Hay un total de **10 asignaturas (A1..A10)** con las siguientes horas semanales asignadas:

| Asignatura | Horas Semanales |
|------------|------------------|
| A1         | 4 hs             |
| A2         | 2 hs             |
| A3         | 4 hs             |
| A4         | 4 hs             |
| A5         | 4 hs             |
| A6         | 2 hs             |
| A7         | 2 hs             |
| A8         | 2 hs             |
| A9         | 1 hr             |
| A10        | 5 hs             |

3. **Bloques de Horario**: 
   - Las asignaturas {A1, A3, A4, A5, A8} deben impartirse en bloques de **2 horas consecutivas**.
   - Las asignaturas {A2, A6, A7, A9, A10} deben impartirse en bloques de **1 hora**.

4. **Restricciones de Día**:
   - En cada día solo se puede impartir, como máximo, un bloque de cada asignatura.
   
5. **Profesores y Asignaturas**:
   - **Prof1**: {A1, A3}
   - **Prof2**: {A4, A5}
   - **Prof3**: {A6, A9}
   - **Prof4**: {A2, A7, A8}
   - **Prof5**: {A10}

6. **Restricciones de Profesores**:
   - **Cada profesor** puede impartir un bloque de sus asignaturas por día, excepto **Prof4**, quien puede impartir más de uno.

7. **Restricción de Franja Horaria**:
   - La **tercera franja horaria** debe ser reservada exclusivamente para tutorías, por lo que no se puede asignar ninguna asignatura a este horario.

8. **Restricciones Horarias de los Profesores**:
   - **Prof1**: Solo puede dar clase en las **quinta** y **sexta** hora de la mañana.
   - **Prof2**: Solo puede dar clase en las **dos primeras horas** de la mañana.
   - **Prof5**: Solo puede dar clase de su asignatura en la **cuarta hora** de la mañana.

9. **Restricciones de Días y Orden de Asignaturas**:
   - **A1 y A4**: Deben impartirse **lunes o martes**.
   - **A3**: Debe impartirse **miércoles o jueves**.
   - **A5**: Debe impartirse **jueves o viernes**.
   - **A6**: Debe impartirse **miércoles o viernes**.
   - **A7**: Debe impartirse **martes o viernes**.
   - **A8**: Debe impartirse **miércoles**.
   - **A9**: Debe impartirse **lunes**.
   - **A6 y A7**: Si se imparten en el mismo día, **A6** debe ser impartida antes que **A7**.

