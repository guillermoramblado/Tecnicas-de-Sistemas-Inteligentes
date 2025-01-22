package tracks.singlePlayer.evaluacion.srcRAMBLADOCARRASCOGUILLERMO;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class AgenteAStar extends AbstractPlayer{
	//componentes del agente
	boolean hay_plan;
	Deque<ACTIONS> camino;
	Vector2dNew fescala;
	Integer nodos_expandidos;
	HashSet<Vector2dNew> muros_trampas;
	//guardamos tambien la dimension del tablero de juego
	Integer longitud_x;
	Integer longitud_y;
	//guardo la posicion inicial del avatar y la posicion del portal
	Vector2dNew avatar;
	Vector2dNew portal;
	ACTIONS orientacion_inicial;
	
	//definicion de la clase Nodo utilizada para representar cada nodo del grafo
	static class Nodo implements Comparable<Nodo>{
		private Vector2dNew posicion;
		private int distancia;
		private int valor_heuristico;
		//importante para mantener siempre una referencia del nodo previo del mejor
		//camino encontrado hasta el momento
		private Nodo padre;
		private ACTIONS accion; //ultima accion que nos llevó hasta dicho nodo, y ademas nos permite calcular correctamente el coste a cada uno de sus hijos
			
		public Nodo(Vector2dNew posicion, int distancia,int valor_heuristico, Nodo padre,ACTIONS accion) {
			this.posicion = posicion;
			this.distancia = distancia;
			this.valor_heuristico = valor_heuristico;
			this.padre = padre;
			this.accion = accion;	}
			
			
		//comparacion de dos objetos de la clase Nodo (iguales si representan la misma posicion del mapa)
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Nodo) {
		       Nodo nodo2= (Nodo) obj;
		       return (this.posicion.equals(nodo2.posicion) && this.accion==nodo2.accion);
		    } else {
		       return false;
		    }
		}


		@Override
		public int compareTo(Nodo o) {
			// Comparación basada en f(n)
		    int comparacionFn = (this.distancia + this.valor_heuristico) - (o.distancia + o.valor_heuristico);
		    
		    // Si f(n) es igual, comparar distancia desde el origen
		    if (comparacionFn == 0) {
		        return this.distancia - o.distancia;
		    }
		    
		    return comparacionFn;
		}
		
		//para comparar primeramente dos objetos segun su codigo hash
		//si tienen dif codigo hash -> son diferentes (no se invoca a equals())
		@Override
		public int hashCode() {
			return Objects.hash(posicion.x,posicion.y,accion);
		}
			
			
	}

	//metodo para comprobar si una casilla es transitable o no: 'true' == transitable
	public boolean es_transitable(Vector2dNew casilla) {
		return !muros_trampas.contains(casilla);
	}
		
	//funcion heuristica: distancia de manhattan. Calculada para Nodo actual
	public int heuristica(Vector2dNew pos_actual) {
			return (int) (Math.abs(pos_actual.x-portal.x)+Math.abs(pos_actual.y-portal.y));
	}
		
	//importante tener a mano el nodo destino para poder calcular el valor heuristico de cada hijo generado
	private List<Nodo> generarHijos(Nodo actual) {
		List<Nodo> hijos = new ArrayList<>();
		int distancia;
		int valor_heuristico;
		Vector2dNew casilla;
		nodos_expandidos++;
		//System.out.println("NODOS EXPANDIDOS:"+nodos_expandidos);
			
		//Probamos las cuatro acciones y calculamos la distancia del nuevo estado al portal.
			
		//subir
	    if (actual.posicion.y - 1 >= 0) { //no nos salimos del tablero y es transitable...
	    	casilla=new Vector2dNew(actual.posicion.x, actual.posicion.y-1);
	        if(es_transitable(casilla)) {
	        	distancia = (actual.accion == ACTIONS.ACTION_UP) ? actual.distancia + 1 : actual.distancia + 2;
	        	valor_heuristico = heuristica(casilla);
	        	hijos.add(new Nodo(casilla,distancia,valor_heuristico,actual,ACTIONS.ACTION_UP));
	        }
	    }  
	    //bajar
	    if (actual.posicion.y + 1 <= longitud_y-1) {
	    	casilla=new Vector2dNew(actual.posicion.x, actual.posicion.y+1);
	      	if(es_transitable(casilla)) {  
	        	distancia = (actual.accion == ACTIONS.ACTION_DOWN) ? actual.distancia + 1 : actual.distancia + 2;
	        	valor_heuristico = heuristica(casilla);
	        	hijos.add(new Nodo(casilla,distancia,valor_heuristico,actual,ACTIONS.ACTION_DOWN));
	        }
	     }
	     //izquierdas
	     if (actual.posicion.x - 1 >= 0) {
	    	 casilla=new Vector2dNew(actual.posicion.x-1, actual.posicion.y);
	    	 if(es_transitable(casilla)) {
	        	distancia = (actual.accion == ACTIONS.ACTION_LEFT) ? actual.distancia + 1 : actual.distancia + 2;
	        	valor_heuristico = heuristica(casilla);
	        	hijos.add(new Nodo(casilla,distancia,valor_heuristico,actual,ACTIONS.ACTION_LEFT));
	        }		
	      }
	      //derecha
	      if (actual.posicion.x + 1 <= longitud_x - 1) {
	    	  casilla = new Vector2dNew(actual.posicion.x+1, actual.posicion.y);
	        if(es_transitable(casilla)) {
	        	distancia = (actual.accion == ACTIONS.ACTION_RIGHT) ? actual.distancia + 1 : actual.distancia + 2;
	        	valor_heuristico = heuristica(casilla);
	        	hijos.add(new Nodo(casilla,distancia,valor_heuristico,actual,ACTIONS.ACTION_RIGHT));
	        }
	      }
	        
	      return hijos;
	}

	public void reconstruir_camino(Nodo destino) {
		Nodo actual = destino;
		while(!actual.posicion.equals(avatar)) { //paramos cuando se alcance la casilla de partida...
			camino.addLast(actual.accion);
			if(actual.padre.accion!=actual.accion) {
				camino.addLast(actual.accion); //llegamos al padre con diferente orientacion, duplicamos
			}
			actual=actual.padre;
		}
	}
	//constructor
	public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		nodos_expandidos = 0;
		hay_plan = false;
		camino = new LinkedList<>();
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2dNew(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length); 
      //almacenamos las posiciones de los muros y trampas del mapa en el hashset...
        muros_trampas = new HashSet<>();
        ArrayList<Observation>[] posiciones_inmoviles=stateObs.getImmovablePositions();
      //guardamos las posiciones de muros y trampas, lo recordará el agente
        if(posiciones_inmoviles.length>=2) {
        	//procedemos a tomar muros y trampas, haciendo la conversion de pixeles a grid
        	Vector2dNew nueva_pos;
        	for(int i=0;i<posiciones_inmoviles[0].size();i++){
        		//muros
        		nueva_pos=new Vector2dNew(posiciones_inmoviles[0].get(i).position);
        		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
        		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
        		muros_trampas.add(nueva_pos);
        	}
        	for(int i=0;i<posiciones_inmoviles[1].size();i++){
        		//trampas
        		nueva_pos=new Vector2dNew(posiciones_inmoviles[1].get(i).position);
        		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
        		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
        		muros_trampas.add(nueva_pos);
        	}
        }
        else {
        	//en el peor de los casos, no habrá trampas, pero si muros
        	Vector2dNew nueva_pos;
        	for(int i=0;i<posiciones_inmoviles[0].size();i++){
        		//muros
        		nueva_pos=new Vector2dNew(posiciones_inmoviles[0].get(i).position);
        		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
        		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
        		muros_trampas.add(nueva_pos);
        	}
        }
        
        longitud_x=stateObs.getObservationGrid().length;
        longitud_y=stateObs.getObservationGrid()[0].length;
        //posicion avatar + portal
        avatar =  new Vector2dNew(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        portal = new Vector2dNew(posiciones[0].get(0).position);
        portal.x = Math.floor(portal.x / fescala.x);
        portal.y = Math.floor(portal.y / fescala.y);
        orientacion_inicial=ACTIONS.fromVector(stateObs.getAvatarOrientation());
	}
	
	public void AEstrella() {
        Nodo origen = new Nodo(avatar,0,heuristica(avatar),null,orientacion_inicial);
        
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        //estructura que me ofrece acceso directo a un elemento (facilita la busqueda de un nodo concreto)
        //el hashmap de abiertos lo uso unicamente para comprobar existencia y en ese caso, tomar su distancia al origen
        Map<Nodo, Integer> mapeo_abiertos = new HashMap<>();
        //cerrados...
        Map<Nodo,Integer> cerrados = new HashMap<>();
        
        //cada vez que metamos un nodo en abiertos, habra que meterlo tanto dentro de la cola como
        //dentro del mapa 'mapeo_abiertos'. Mismo proceso a la hora de sacarlo de abiertos
        abiertos.add(origen);
        mapeo_abiertos.put(origen,origen.distancia);
         
        while(true) {
        	Nodo actual = abiertos.poll();
        	mapeo_abiertos.remove(actual);
        	cerrados.put(actual,actual.distancia);
        	
        	//¿=me situo en el portal?
        	if(!(actual.posicion).equals(portal)) {
        		//expando y actualizo la distancia a cada uno de sus hijos
        		for(Nodo hijo: generarHijos(actual)) {
        			if(cerrados.containsKey(hijo) && hijo.distancia<cerrados.get(hijo)) {
        				//esta en cerrados, y el nuevo camino a él es más corto...
        				cerrados.remove(hijo);
        				abiertos.add(hijo);
        				mapeo_abiertos.put(hijo,hijo.distancia);
        			}
        			else if(!cerrados.containsKey(hijo) && !mapeo_abiertos.containsKey(hijo)){
        				//no esta en cerrados ni en abiertos, lo metemos en abiertos
        				abiertos.add(hijo);
        				mapeo_abiertos.put(hijo, hijo.distancia);
        			}
        			else if(mapeo_abiertos.containsKey(hijo) && hijo.distancia<mapeo_abiertos.get(hijo)){
        				//esta en abiertos y nuevo mejor camino -> ACTUALIZO
        				abiertos.remove(hijo);
        				abiertos.add(hijo);
        				mapeo_abiertos.put(hijo, hijo.distancia);
        			}
        			
        			
        		}
        	}
        	else {
        		//hemos alcanzado el portal de destino, tenemos ruta
        		hay_plan=true;
        		reconstruir_camino(actual);
        		break; //salimos del bucle
        	}
        	
        }
        
        
	}
	
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//comprobamos si ya ha calculado la ruta a seguir...
		if(!hay_plan) {
			long tInicio = System.nanoTime();		
			AEstrella();
			long tFin = System.nanoTime();
			long tiempoTotalms = (tFin-tInicio)/1000000;
			System.out.println("Nº de nodos expandidos:"+nodos_expandidos+"\nTamanio de ruta:"+camino.size()+"\nTiempo empleado:"+tiempoTotalms);	
			
		}
		return camino.pollLast();
	}

}
