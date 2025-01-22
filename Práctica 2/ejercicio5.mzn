include "tsiutils.mzn";

% Set de valores
int: size;
int: capacidad_maxima = 5000; %5000 gramos == 5kg
% Pesos
array[1..size] of var int : Pesos;
constraint pesosN(Pesos);
% Prioridad
array[1..size] of var int : Prioridad;
constraint prioridadN(Prioridad); 

%resuelvo el problema
%los objetos que nos ofrecen se identifican con enteros de 1 a size
%variable de decision: conjunto de objetos seleccionados
%al usar un set, ya me aseguro de que no se almacene mas de una vez un mismo objeto
var set of 1..size: mochila;

%restriccion: no superar la capacidad maxima de la mochila
var int: peso_total = sum(objeto in mochila)(Pesos[objeto]);
constraint peso_total <= capacidad_maxima;
%buscamos maximizar la prioridad obtenida, por lo que calculamos la prioridad total de los objetos que decidimos llevar
var int: prioridad_total = sum(objeto in mochila)(Prioridad[objeto]);
solve maximize prioridad_total;

%output ["Objetos: \(mochila)","\nNumero de objetos = \(size)","\nPrioridades = \(prioridad_total)","\nPeso final = \(peso_total)"];
output ["Objetos: \(mochila)",
        "\nNumero de objetos = \(size)",
        "\nPrioridades = \(prioridad_total)",
        "\nPeso final = "++show(sum(objeto in mochila)(Pesos[objeto]))++" gramos"
];