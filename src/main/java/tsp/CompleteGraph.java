package tsp;

import java.util.HashMap;
import model.Courier;

public class CompleteGraph implements Graph {
	private static final int MAX_COST = 40;
	private static final int MIN_COST = 10;
	int nbVertices;
	double[][] cost;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
         * @param c
         * @param idWarehouse
	 */
	public CompleteGraph(Courier c, Long idWarehouse) {
            HashMap<Long, HashMap<Long, Double>> completeMap = c.getShortestPathBetweenDPs();
            this.nbVertices = completeMap.size();
            this.cost = new double[this.nbVertices][this.nbVertices];
		/*int iseed = 1;
		cost = new int[nbVertices][nbVertices];
		for (int i=0; i<nbVertices; i++){
		    for (int j=0; j<nbVertices; j++){
		        if (i == j) cost[i][j] = -1;
		        else {
		            int it = 16807 * (iseed % 127773) - 2836 * (iseed / 127773);
		            if (it > 0)	iseed = it;
		            else iseed = 2147483647 + it;
		            cost[i][j] = MIN_COST + iseed % (MAX_COST-MIN_COST+1);
		        }
		    }
		}*/

            for (int i = 0 ; i < c.getPositionIntersection().size() ; i++) {
                Long keyOrigin = c.getPositionIntersection().get(i);
                for (int j = 0 ; j < c.getPositionIntersection().size() ; j++) {
                    Long keyDesti = c.getPositionIntersection().get(j);
                    if (keyDesti.equals(idWarehouse) || i==j) {
                        // warehouse is destination, we do not add 5 mins of shipping
                        cost[i][j] = completeMap.get(keyOrigin).get(keyDesti);
                    } else {
                        cost[i][j] = completeMap.get(keyOrigin).get(keyDesti) + 5;
                    }
                }
            }
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public double getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1;
		return cost[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}

        @Override
        public double[][] getCostTable() {
            return cost;
        }
        
        

}
