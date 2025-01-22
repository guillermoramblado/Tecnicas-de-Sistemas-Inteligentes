include "alldifferent.mzn";

%enumeramos las dos listas de simbolos a relacionar entre sí
enum Iniciales = {a,b,c,d};
enum Finales = {w,x,y,z};

%variable de decision: array indexado por los simbolos iniciales, el cual contiene los simbolos finales que se emparejan
array[Iniciales] of var Finales: parejas;

%asegurarnos de que cada simbolo final se asocie a un unico simbolo inicial
constraint alldifferent(parejas);
%restricciones indicadas en el enunciado
constraint parejas[a]!=x -> parejas[c]!=y;
constraint (parejas[b]==y \/ parejas[b]==z) -> parejas[a]==x;
constraint parejas[c]!=w -> parejas[b]==z;
constraint parejas[d]==y -> parejas[b]!=x;
constraint parejas[d]!=x -> parejas[b]==x;

solve satisfy;

output["Las parejas formadas son las siguientes:"]++["\n\t\(simbolo) -  \(parejas[simbolo])"| simbolo in Iniciales];

