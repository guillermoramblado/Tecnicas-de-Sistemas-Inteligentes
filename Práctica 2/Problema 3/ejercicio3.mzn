include "alldifferent.mzn";
%comienzo enumerando tanto las asignaturas como los dias
enum Asignaturas= {A1,A2,A3,A4,A5,A6,A7,A8,A9,A10};
enum Dias = {Lunes,Martes,Miercoles,Jueves,Viernes};
set of int: horario = 8..14;
set of int: horario_ext = {0} union horario;
%el siguinte array sera usado en la cuarta restriccion...
array[1..7] of int: horas_lectivas = array1d(1..7,[8,9,10,11,12,13,14]);

enum Profesores = {Prof1, Prof2, Prof3, Prof4, Prof5};
%indicamos las horas semanales que se deben impartir para cada asignatura
array[Asignaturas] of int: horas_a_impartir = [4,2,4,4,4,2,2,2,1,5];
%indicamos las asignaturas que imparte cada profesor usando un array de sets
array[Profesores] of set of Asignaturas: asignaturas_imparte = [
{A1,A3},{A4,A5},{A6,A9},{A2,A7,A8},{A10}
];

%bloque minimo que se debe impartir cada asignatura
array[Asignaturas] of int: duracion_bloque = [2,1,2,2,2,1,1,2,1,1];
%puedo decir cuantos bloques he de impartir para cada asignatura
array[Asignaturas] of int: num_bloques = [horas_a_impartir[i] div duracion_bloque[i]| i in Asignaturas];

%VARIABLES DE DECISION:

%matriz que almacena la hora de inicio de cada asignatura, cada dia, tal que si una asignatura no se imparte un determinado dia, se almacena '0'
%con esta codificacion, asociamos una unica hora de inicio a cada asignatura cada dia, por lo que incluye la restriccion de maximo un bloque cada dia
array[Dias,Asignaturas] of var horario_ext: hora_inicio;

%RESTRICCIONES

%asegurar que el nº de dias que se imparta dicha asignatura coincida con el nº de bloqueas a realizar semanalmente de la asignatura
constraint forall(a in Asignaturas)(
                  sum(d in Dias)(bool2int(hora_inicio[d,a]!=0))==num_bloques[a]
);

%evitar que las asignaturas se solapen en un mismo dia
constraint forall(d in Dias)(
              forall(a in Asignaturas where hora_inicio[d,a]!=0)(
                  %no solapa con las siguientes que se impartan ese mismo dia
                  forall(b in Asignaturas where (b>a /\ hora_inicio[d,b]!=0))(
                        hora_inicio[d,a]+duracion_bloque[a]<=hora_inicio[d,b] \/ hora_inicio[d,b]+duracion_bloque[b]<=hora_inicio[d,a])
                
              )
              /\
              %para cada dia, un profesor no puede dar 2 o más asignaturas suyas, excluyendo el profesor 4
              forall(prof in Profesores where prof!=Prof4)(sum(materia in asignaturas_imparte[prof])(bool2int(hora_inicio[d,materia]!=0)) <= 1)
);

%la tercera franja horaria debe estar libre para tutorias
constraint forall(d in Dias)(
              %para todas las asignaturas que se imparten dicho dia...
              forall(a in Asignaturas where hora_inicio[d,a]!=0)(
              %tienen que terminar antes de las 15 - no empezar justo en tercera hora - mientras que la impartimos, no abarcar la tercera hora
                (hora_inicio[d,a]+duracion_bloque[a]<=15) /\ 
                (if hora_inicio[d,a]==10 then false 
                else 
                  %si dicha asignatura comienza antes de la tecera franja horaria, asegurarnos de que termina antes de dicha franja horaria
                  if hora_inicio[d,a]<10 then hora_inicio[d,a]+duracion_bloque[a]<=10 else true endif
                endif)
              )
);

%RESTRICCIONES DE PROFESORES...

%profesor 1 solo puede dar clase a quinta y sexta hora de la mañana
constraint forall(materia in asignaturas_imparte[Prof1])(
                  %seleccionar los dias en los que se imparte dicha materia
                 forall(dia in Dias where hora_inicio[dia,materia]!=0)(
                      %solo puede impartir clase a quinta y sexta hora
                      %como el profesor1 imparte asignaturas cuyos bloques duran 2 horas, bastara con comprobar que comienzan si o si a quinta hora, ya que si comenzasen a sexta hora, el profesor tendria que dar clase en septima hora para terminar de dar el bloque de dicha asignatura, incumpliendo la restriccion
                      hora_inicio[dia,materia]==horas_lectivas[5]
                 )
);

%profesor 2 solo puede dar clase en las dos primeras horas de la mañana
%de nuevo, imparte dos asignaturas cuyos bloques duran 2 horas cada uno
%bastara por tanto con comprobar que las asignaturas que imparten empiezan (el dia que se haya decidido impartir) a primera hora, si o si
constraint forall(materia in asignaturas_imparte[Prof2])(
                  %seleccionar los dias en los que se imparte dicha materia
                 forall(dia in Dias where hora_inicio[dia,materia]!=0)(
                      hora_inicio[dia,materia]==horas_lectivas[1] 
                 )
);

%profesor 5 solo puede dar clase a cuarta hora de la mañana
%dicho profesor solo imparte una asignatura, 'A10', que supone dar 5 bloques de 1 hora en dias diferentes)
%por ello, teniendo en cuenta las dos anotaciones previas, todo esto equivale a comprobar que A10 se imparte todos los dias a las 11 (de 11 a 12)
constraint forall(materia in asignaturas_imparte[Prof5])(
                 forall(dia in Dias)(
                      %comprobar que comience siempre a cuarta hora
                      hora_inicio[dia,materia]==horas_lectivas[4]
                 )
);

%RESTRICCIONES SOBRE ASIGNATURAS
constraint forall(dia in Dias)(
              %asignatura 1 y 4 solo pueden impartirse lunes o martes
              (hora_inicio[dia,A1]!=0 -> (dia==Lunes \/ dia==Martes))
              /\
              (hora_inicio[dia,A4]!=0 -> (dia==Lunes \/ dia==Martes))
              /\
              %A3 se debe impartir solo los miercoles o  jueves
              (hora_inicio[dia,A3]!=0 -> (dia==Miercoles \/ dia==Jueves))
              /\
              %A5 se debe impartir solo los jueves o viernes
              (hora_inicio[dia,A5]!=0 -> (dia==Jueves \/ dia==Viernes))
              /\
              %A6 se debe impartir solo los miercoles o viernes
              (hora_inicio[dia,A6]!=0 -> (dia==Miercoles \/ dia==Viernes))
              /\
              %A7 se debe impartir solo los martes o viernes
              (hora_inicio[dia,A7]!=0 -> (dia==Martes \/ dia==Viernes))
              /\
              %A8 se debe impartir solo los miercoles
              (hora_inicio[dia,A8]!=0 -> dia==Miercoles)
              /\
              %98 se debe impartir solo los lunes
              (hora_inicio[dia,A9]!=0 -> dia==Lunes)
              /\
              %si dicho dia se imparten las dos, la 7 debe comenzar justo despues de terminar la 6
              (hora_inicio[dia,A6]!=0 /\ hora_inicio[dia,A7]!=0 -> ((hora_inicio[dia,A6]+duracion_bloque[A6])==hora_inicio[dia,A7]))
);

solve satisfy;











