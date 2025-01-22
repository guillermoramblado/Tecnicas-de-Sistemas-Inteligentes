enum Color = {Verde,Rojo,Naranja,Azul};
array[Color] of int: Costes = [100,250,350,450];
%la unica diferencia radica en definir exactamente cual es la solucion optima a la que queremos llegar y que sabemos que existe (tras plantearlo primero como un COP). Esto implica definir el coste minimo que obtuvimos con la primera forma de tratar el problema, definiendo una nueva restriccion en este caso, tal que exigimos que la solucion obtenida por el solver tenga un coste total de 2900 (=coste_minimo).
int: coste_minimo = 2900;

enum Regiones={Argentina,Bolivia,Brasil,Chile,Colombia,Ecuador,Guayana_Francesa,Guyana,Paraguay,Peru,Surinam,Uruguay,Venezuela};

array[Regiones] of set of Regiones: vecinos = [
{Bolivia, Brasil, Chile, Paraguay, Uruguay},
{Argentina, Brasil, Chile, Paraguay, Peru},
{Argentina, Bolivia, Colombia, Guayana_Francesa, Guyana, Paraguay, Peru, Surinam, Uruguay, Venezuela},
{Argentina, Bolivia, Peru},
{Brasil, Ecuador, Peru, Venezuela},
{Colombia, Peru},
{Brasil, Surinam},
{Brasil, Surinam, Venezuela},
{Argentina, Bolivia, Brasil, Uruguay},
{Bolivia, Brasil, Chile, Colombia, Ecuador},
{Brasil, Guayana_Francesa, Guyana},
{Argentina, Brasil, Paraguay},
{Brasil, Colombia, Guyana} ];

array[Regiones] of var Color: coloreado;

%restriccion necesaria para evitar pintar vecinos con el mismo color
constraint forall(i in Regiones)(
              forall(j in vecinos[i] where j>i)(coloreado[i]!=coloreado[j])
           );
           

var int: coste_total = sum(r in Regiones)(Costes[coloreado[r]]);
constraint coste_total==coste_minimo;

solve satisfy;


output ["\ntotal: \(coste_total)\n"]++["\(r) = \(coloreado[r]) \(Costes[coloreado[r]])\n" | r in Regiones];


