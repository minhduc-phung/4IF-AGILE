package tsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {
	@Override
	protected double bound(Integer currentVertex, ArrayList<Integer> unvisited) {
            return g.getMinCost()*unvisited.size();
        /*
        double sum = Double.MAX_VALUE;
        double minToAnUnvisitedVertex = Double.MAX_VALUE;
        
        double[][] cost = g.getCostTable();
        for (Integer unvisitedVertex : unvisited) {
            if (sum > cost[currentVertex][unvisitedVertex]) {
                sum = cost[currentVertex][unvisitedVertex];
            }
        }

        for (Integer unvisitedVertex : unvisited) {
            for (Integer anotherUnvisitedVertex : unvisited) {
                if (!anotherUnvisitedVertex.equals(unvisited) && minToAnUnvisitedVertex > cost[unvisitedVertex][anotherUnvisitedVertex]) {
                    minToAnUnvisitedVertex = cost[unvisitedVertex][anotherUnvisitedVertex];
                }
            }
            if (minToAnUnvisitedVertex > cost[unvisitedVertex][0]) {
                minToAnUnvisitedVertex = cost[unvisitedVertex][0];
            }
            sum += minToAnUnvisitedVertex;
            minToAnUnvisitedVertex = Double.MAX_VALUE;
        }

            
            return sum;
*/
	}

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, ArrayList<Integer> unvisited, Graph g) {
		return new SeqIter(unvisited, currentVertex, g);
	}

}
