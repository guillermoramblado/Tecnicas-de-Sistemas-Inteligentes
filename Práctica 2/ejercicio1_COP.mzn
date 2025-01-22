include "alldifferent.mzn";

%comenzamos enumerando los colores y regiones a utilizar, indexando los costes por los colores asociados
enum Color = {Verde,Rojo,Naranja,Azul};
array[Color] of int: Costes = [100,250,350,450];

enum Regiones={Argentina,Bolivia,Brasil,Chile,Colombia,Ecuador,Guayana_Francesa,Guyana,Paraguay,Peru,Surinam,Uruguay,Venezuela};

%he decidido definir un array que guarde, para cada regio, el conjunto de regiones vecinas
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

%variable de decision: color con el que se pinta cada region
array[Regiones] of var Color: coloreado;

%comprobamos que cada region se pinte con un color diferente a los colores que emplean sus vecinos
%hay que tener en cuenta que basta con realizar la comprobacion en una sola direccion. Con esto me refiero a que si A es vecino de B, basta con probar que para A, su color difiere del color de B, tal que cuando lleguemos a B, no hace falta comprobar de nuevo, que para B, su color difiere del color de A.
%por esta razon, para cada pais, simplemente tomo los paises vecinos que esten por delante de el (voy en una misma direccion siempre) y compruebo que difieren en color
constraint forall(i in Regiones)(
              forall(j in vecinos[i] where j>i)(coloreado[i]!=coloreado[j])
           );

%busco minimizar el coste
var int: coste = sum(r in Regiones)(Costes[coloreado[r]]);

solve minimize coste;

output ["total: \(coste)\n"]++["\(r) = \(coloreado[r])  \(Costes[coloreado[r]])\n" | r in Regiones]





