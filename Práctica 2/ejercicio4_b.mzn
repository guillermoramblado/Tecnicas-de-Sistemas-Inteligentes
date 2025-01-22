

enum Tareas = {A,B,C,D,E,F,G,H,I,J};
set of int: Trabajadores = 1..3;
%guardamos la duracion de la tarea dependiendo del trabajador
array[Tareas,Trabajadores] of int: duracion = [| 3, 6, 9 | 7, 5, 3 | 3, 2, 4 | 5, 5, 8 | 4, 3, 6 | 3, 2, 1 | 2, 2, 3 | 2, 4, 2 | 2, 3, 4 | 3, 2, 1 |];
%determino el peor tiempo total necesario para ejecutar todas las tareas del proyecto: supondrá ejecutar secuencialmente cada tarea usando el empleado que más tarde en realizar dicha tarea
%esto me permite restringir el dominio de la variable que almacena el tiempo total consumido para ejecutar todas las tareas del proyecto
int: peor_tiempo_total = sum(task in Tareas)(max(worker in Trabajadores)(duracion[task,worker]));
%tiempo consumido en total
var 0..peor_tiempo_total: end;

%variables de decision: 
%dos arrays indexado por las tareas que contiene tanto el trabajador asignado como el instante de ejecucion
array[Tareas] of var Trabajadores: empleado_asignado;
array[Tareas] of var 0..peor_tiempo_total: inicio_tarea;
%el solver tendrá que decidir en qué tareas intervendrá ese trabajador de apoyo
array[Tareas] of var bool: tareas_apoyadas;
    
%funcion que recibe una tarea y devuelve su duracion actualizada en funcion de si interviene o no el trabajador de apoyo
function var int: duracion_efectiva(int: i,var int: j) = if tareas_apoyadas[to_enum(Tareas,i)] then duracion[to_enum(Tareas,i),j]-1 else duracion[to_enum(Tareas,i),j] endif;

%comprobar que cada trabajador no realice simultaneamente dos o mas tareas
constraint forall(trab in Trabajadores)(
                %defino una variable que guarda las tareas asignadas a dicho empleado
                let {
                  var set of Tareas: tareas_asignadas = {task | task in Tareas where empleado_asignado[task]==trab}
                } 
                in
                forall(t1,t2 in tareas_asignadas where t2>t1)(
                    %comprobar que no se solapen las tareas de un mismo trabajador
                    inicio_tarea[t1]+duracion_efectiva(t1,trab) <= inicio_tarea[t2] \/ inicio_tarea[t2]+duracion_efectiva(t2,trab) <= inicio_tarea[t1]
                )
);

%RESTRICCONES SOBRE LAS TAREAS

%la tarea A debe terminar antes de ejecutar B
constraint inicio_tarea[A]+duracion_efectiva(A,empleado_asignado[A])<=inicio_tarea[B];
%la tarea B debe terminar antes de ejecutar C
constraint inicio_tarea[B]+duracion_efectiva(B,empleado_asignado[B])<=inicio_tarea[C];
%la tarea D,E y F no pueden comenzar hasta que no termine C
var int: fin_tareaC = inicio_tarea[C]+duracion_efectiva(C,empleado_asignado[C]);
constraint (fin_tareaC<=inicio_tarea[D] /\ fin_tareaC<=inicio_tarea[E] /\ fin_tareaC<=inicio_tarea[F]);
%la tarea G no puede comenzar hasta que finalice la tarea D
constraint inicio_tarea[D]+duracion_efectiva(D,empleado_asignado[D])<=inicio_tarea[G];
%la tarea H no puede comenzar hasta que no finalice G
constraint inicio_tarea[G]+duracion_efectiva(G,empleado_asignado[G])<=inicio_tarea[H];
%la tarea I no puede comenzar hasta que no finalice H y F
constraint (inicio_tarea[H]+duracion_efectiva(H,empleado_asignado[H])<=inicio_tarea[I] /\ inicio_tarea[F]+duracion_efectiva(F,empleado_asignado[F])<=inicio_tarea[I]);
%la tarea J no puede comenzar hasta que no finalice I
constraint inicio_tarea[I]+duracion_efectiva(I,empleado_asignado[I])<=inicio_tarea[J];

%A partir del grafo que he realizado, deduzco que:
%La tarea J no puede comenzar hasta que no finalicen todas las tareas restantes menos la 'E', por lo que se puede dar la posibilidad de que la tarea J y E se ejecuten simultaneamente (siempre que la realicen trabajadores diferentes). Podriamos considerar entonces que la ultima tarea que se va a ejecutar siempre será la tarea J, pudiendo ser acompañado de la ejecución de E (que podria finalizar antes de que comience J).
%Concluimos que el tiempo total necesario vendrá determinado por el instante de fin de J y E
constraint inicio_tarea[J]+duracion_efectiva(J,empleado_asignado[J])<=end /\ inicio_tarea[E]+duracion_efectiva(E,empleado_asignado[E])<=end;

%el trabajador de apoyo puede intervenir como maximo en 5 tareas
constraint sum(tarea in Tareas)(bool2int(tareas_apoyadas[tarea]))<=5;
%solo puede intervenir en aquellas tareas cuyo trabajador asignado tarde mas de dos dias
%aqui tenemos en cuenta la duracion inicial (no usamos la funcion) para saber si puede intervenir o no el trabajador de apoyo
var set of Tareas: apoyo_interviene = {tarea | tarea in Tareas where tareas_apoyadas[tarea]};
constraint forall(tarea in apoyo_interviene)( duracion[tarea,empleado_asignado[tarea]]>=2);
%no puede intervenir en tareas simultaneas -> procedemos a tomar todas las tareas en las que interviene, y ver que no se solapan
constraint forall(i,j in apoyo_interviene where j>i)(
                  (inicio_tarea[i]+duracion_efectiva(i,empleado_asignado[i])) <= inicio_tarea[j] \/ (inicio_tarea[j]+duracion_efectiva(j,empleado_asignado[j]) <= inicio_tarea[i])
);


solve minimize end;