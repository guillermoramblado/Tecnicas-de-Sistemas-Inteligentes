package tracks.singlePlayer.evaluacion.srcRAMBLADOCARRASCOGUILLERMO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class AgenteRTAStar extends AbstractPlayer{
	//componentes del agente
	Vector2dNew fescala;
	Integer nodos_expandidos;
	Vector2dNew destino;
	HashSet<Vector2dNew> muros_trampas;
	//guardamos tambien la dimension del tablero de juego
	Integer longitud_x;
	Integer longitud_y;
	//tabla hash que guarda la informacion heuristica de cada nodo...
	Map<Vector2dNew, Integer> tabla_heuristicas;
	//ultima accion devuelta por RTA, que habra que ejecutar dos veces en el caso de
	//no estar bien orientados
	ACTIONS ultima_accion;
	Boolean repetiraccion; //señal que nos indicará si hay que ejecutar dos veces la accion
	
	//Guardo el numero de muros y trampas visibles a la hora de actualizar la información que se
	//Así evito copiar todas las posiciones de muros y trampas de principio a fin, solo las nuevas
	Integer num_muros;
	Integer num_trampas;
	//variables a imprimir cuando se alcance el portal
	long tiempo_total;
	boolean destino_alcanzado;
	int tamanio_ruta;
		
	public AgenteRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2dNew(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length); 
        nodos_expandidos = 0;
        //Guardo la posicion del portal para poder calcular la dist.Manhattan de un determinado nodo
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        destino = new Vector2dNew(posiciones[0].get(0).position);
        destino.x = Math.floor(destino.x / fescala.x);
        destino.y = Math.floor(destino.y / fescala.y);
        //Guardamos posiciones de muros y trampas
        muros_trampas = new HashSet<>();
        ArrayList<Observation>[] posiciones_inmoviles=stateObs.getImmovablePositions();
        if(posiciones_inmoviles.length>=2) {
        	num_muros=posiciones_inmoviles[0].size();
        	//procedemos a tomar muros y trampas, haciendo la conversion de pixeles a grid
        	Vector2dNew nueva_pos;
        	for(int i=0;i<num_muros;i++){
        		//muros
        		nueva_pos=new Vector2dNew(posiciones_inmoviles[0].get(i).position);
        		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
        		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
        		muros_trampas.add(nueva_pos);
        	}
        	num_trampas=posiciones_inmoviles[1].size();
        	for(int i=0;i<num_trampas;i++){
        		//trampas
        		nueva_pos=new Vector2dNew(posiciones_inmoviles[1].get(i).position);
        		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
        		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
        		muros_trampas.add(nueva_pos);
        	}
        }
        else {
        	//en el peor de los casos, no habra trampas pero si muros
        	num_muros=posiciones_inmoviles[0].size();
        	Vector2dNew nueva_pos;
        	for(int i=0;i<num_muros;i++){
        		//muros
        		nueva_pos=new Vector2dNew(posiciones_inmoviles[0].get(i).position);
        		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
        		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
        		muros_trampas.add(nueva_pos);
        	}
        	num_trampas=0;
        }
        
        longitud_x=stateObs.getObservationGrid().length;
        longitud_y=stateObs.getObservationGrid()[0].length;
        //Tabla hash
        tabla_heuristicas=new HashMap<>();
        ultima_accion=ACTIONS.ACTION_NIL;
        repetiraccion=false;
        tiempo_total=(long) 0.0;
        destino_alcanzado=false;
        tamanio_ruta=0;
	}
	
	static class Nodo{
		private Vector2dNew posicion;
		private ACTIONS accion;
		
		public Nodo(Vector2dNew posicion, ACTIONS accion) {
			this.posicion = posicion;
			this.accion = accion;	}
		
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Nodo) {
	            Nodo nodo2= (Nodo) obj;
	            return this.posicion.equals(nodo2.posicion);
	        } else {
	            return false;
	        }
		}
		//para comparar primeramente dos objetos segun su codigo hash
		//si tienen dif codigo hash -> son diferentes (no se invoca a equals())
		@Override
	    public int hashCode() {
	        return Objects.hashCode(posicion);
	    }
	}
	
	//clase que modela los objetos que meteré en la priority queue
	static class Pareja implements Comparable<Pareja>{
		Nodo nodo;
		int coste; //Coste(padre,hijo)+heuristica(hijo)
			
		public Pareja(Nodo nodo,int coste) {
			this.nodo=nodo;
			this.coste=coste;
		}
			
		//si int<0: menor, >0: mayor, ==0 --> iguales
		@Override
		public int compareTo(Pareja otra) {
			//comparamos el coste asociado
			return this.coste-otra.coste;
		}
			
		//se recomienda mantener la consistencia del compareTo con el equals, pero como las unicas operaciones que voy a
		//realizar en la priority queue no supone usar equals() [simplemente uso add(),poll() y peek()], no es necesario
	}
	
	public boolean es_transitable(Vector2dNew casilla) {
		return !muros_trampas.contains(casilla);
	}
		
	//funcion heuristica: distancia de manhattan. Calculada para Nodo actual
	public int heuristica(Vector2dNew pos_actual) {
			return (int) (Math.abs(pos_actual.x-destino.x)+Math.abs(pos_actual.y-destino.y));
	}

	//funcion que genera ndos hijos
	private List<Nodo> generarHijos(Nodo actual) {
		List<Nodo> hijos = new ArrayList<>();
		nodos_expandidos++;
		Vector2dNew casilla;
			
		//UP
	    if (actual.posicion.y - 1 >= 0) { //no nos salimos del tablero y es transitable...
	    	casilla=new Vector2dNew(actual.posicion.x, actual.posicion.y-1);
	        if(es_transitable(casilla)) {
	        	hijos.add(new Nodo(casilla,ACTIONS.ACTION_UP));
	        }
	    }
	    //DOWN
	    if (actual.posicion.y + 1 <= longitud_y-1) {
	    	casilla=new Vector2dNew(actual.posicion.x, actual.posicion.y+1);
	       if(es_transitable(casilla)) {  
	        	hijos.add(new Nodo(casilla,ACTIONS.ACTION_DOWN));
	       }
	    }
	    //LEFT
	    if (actual.posicion.x - 1 >= 0) {
	    	casilla=new Vector2dNew(actual.posicion.x-1, actual.posicion.y);
	       if(es_transitable(casilla)) {
	        	hijos.add(new Nodo(casilla,ACTIONS.ACTION_LEFT));
	        }		
	    }
	    //RIGHT
	    if (actual.posicion.x + 1 <= longitud_x - 1) {
	    	casilla=new Vector2dNew(actual.posicion.x+1, actual.posicion.y);
	        if(es_transitable(casilla)) {
	        	hijos.add(new Nodo(casilla,ACTIONS.ACTION_RIGHT));
	        }
	    }
	        
	    return hijos;
	}
	
	//funcion encargada de actualizar los muros y trampas conocidos al pisar una casilla activadora
	private void actualizar_info_muros_trampas(ArrayList<Observation>[] posiciones_inmoviles) {
		//Actualizo primero los muros conocidos...
		Vector2dNew nueva_pos;
		if(num_muros<posiciones_inmoviles[0].size()) {
			//han aparecido nuevos muros, meto solo los nuevos
			for(int i=num_muros;i<posiciones_inmoviles[0].size();i++){
	    		//muros
	    		nueva_pos=new Vector2dNew(posiciones_inmoviles[0].get(i).position);
	    		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
	    		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
	    		muros_trampas.add(nueva_pos);
	    	}
			//actualizo el contador de muros conocidos
			num_muros=posiciones_inmoviles[0].size();
		}
		
		if(posiciones_inmoviles.length>=2) {
			//hay al menos alguna trampa en el mapa (comprobacion necesaria para acceder a ''[1] cuando se nos permita)
			if(num_trampas<posiciones_inmoviles[1].size()) {
				//han aparecido nuevas trampas, meto solo las nuevas
				for(int i=num_trampas;i<posiciones_inmoviles[1].size();i++){
		    		//muros
		    		nueva_pos=new Vector2dNew(posiciones_inmoviles[1].get(i).position);
		    		nueva_pos.x = Math.floor(nueva_pos.x / fescala.x);
		    		nueva_pos.y = Math.floor(nueva_pos.y / fescala.y);
		    		muros_trampas.add(nueva_pos);
		    	}
				//actualizo el contador de muros conocidos
				num_trampas=posiciones_inmoviles[1].size();
			}
		}
	}
	
	public ACTIONS RTAStar(StateObservation stateObs) {
		//tomamos la posicion del avatar
		Vector2dNew avatar =  new Vector2dNew(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
        Nodo actual = new Nodo(avatar,ACTIONS.fromVector(stateObs.getAvatarOrientation()));  
        
        Integer heuristica_actual = tabla_heuristicas.get(avatar);
    	if(heuristica_actual==null) {
    		//le asociamos un valor heuristico inicial al nodo donde se situa el avatar...
    		heuristica_actual=heuristica(avatar);
    		tabla_heuristicas.put(avatar,heuristica_actual);
    	}
    	
    	ArrayList<Observation>[] posiciones_inmoviles = stateObs.getImmovablePositions();
    	int total_muros_trampas;
    	if(posiciones_inmoviles.length>=2) {
    		total_muros_trampas=posiciones_inmoviles[0].size()+posiciones_inmoviles[1].size();
    	}
    	else {
    		//en el peor de los casos, no habra trampas pero si muros (es lo mas logico)
    		total_muros_trampas=posiciones_inmoviles[0].size();
    	}
    	
    	if(muros_trampas.size()!=total_muros_trampas) {
    		actualizar_info_muros_trampas(posiciones_inmoviles);
    	}
    	
    	//Genero hijos...
    	PriorityQueue<Pareja> cola_prioridad = new PriorityQueue<>();
    	
    	for(Nodo hijo: generarHijos(actual)) {
    		//calculo Coste(padre,hijo)
    		Integer coste = (hijo.accion == actual.accion) ? 1 : 2;
    		//Calculo el valor heuristico
    		Integer valor_h;
    		if(tabla_heuristicas.containsKey(hijo.posicion)) {
    			//tiene asignado ya un valor heuristico
    			valor_h=tabla_heuristicas.get(hijo.posicion);
    		}
    		else {
    			//le asignamos un valor heuristico inicial, guardandolo en la tabla hash
    			valor_h=heuristica(hijo.posicion);
    			tabla_heuristicas.put(hijo.posicion, valor_h);
    		}
    		//meto en la priority queue, para mantenerlo ordenado con respecto al resto de hijos
    		cola_prioridad.add(new Pareja(hijo,coste+valor_h));
    	}
    	
    	//una vez generados los hijos, procedo a desplazarme al mejor
    	Pareja mejor_hijo=cola_prioridad.poll();
    	//actualizo la heuristica del padre usando el 2º mejor hijo
    	if(cola_prioridad.size()>=1) {
        	Pareja segundo_mejor_hijo=cola_prioridad.poll();
        	tabla_heuristicas.put(avatar,Math.max(heuristica_actual, segundo_mejor_hijo.coste));
    	}
    	else {
    		//no hay mas hijos, usamos el coste del primer mejor hijo
    		tabla_heuristicas.put(avatar,Math.max(heuristica_actual, mejor_hijo.coste));
    	}
    	
    	//antes de salir, comprobamos si hemos alcanzado el nodo destino
    	if((mejor_hijo.nodo.posicion).equals(destino)) {
    		destino_alcanzado=true;
    	}
    	
    	return mejor_hijo.nodo.accion; 
	}
	

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		if(!repetiraccion) {
		   //calculo el nuevo nodo hijo al que desplazarme
		   long tInicio = System.nanoTime();		
		   ultima_accion=RTAStar(stateObs);
		   long tFin = System.nanoTime();
		   tiempo_total+=tFin-tInicio;
		   if(destino_alcanzado) {
			   System.out.println("Nº de nodos expandidos:"+nodos_expandidos+"\nTamanio ruta:"+tamanio_ruta+"\nTiempo empleado:"+(long)(tiempo_total/1000000));
			   destino_alcanzado=false;
		   }
		   //¿estoy bien orientado?
		   if(ultima_accion!=ACTIONS.fromVector(stateObs.getAvatarOrientation())){
				//mal orientado, ejecuto la accion dos veces
				repetiraccion=true;	
			}
			tamanio_ruta++;
			return ultima_accion;
		}
		//repetimos la ultima accion llevaba a cabo...
		repetiraccion=false;
		tamanio_ruta++;
		return ultima_accion;
	}
	
}
