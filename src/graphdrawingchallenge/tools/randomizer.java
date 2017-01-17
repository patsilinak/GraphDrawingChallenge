/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphdrawingchallenge.tools;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Panagiotis
 */
public class randomizer {
    
    protected static Random random = new Random();
    private static double boundsMin = -500;
    private static double boundsMax = 500;
    
    public static void randomize(IGraph graph){
        ArrayList<PointD> blacklist = new ArrayList<>();
        PointD coordinates = new PointD(0,0);
        for (INode curr : graph.getNodes()){
            do{
                coordinates = new PointD(randomDouble(boundsMin, boundsMax),
                                        randomDouble(boundsMin, boundsMax));
            }while(isInList(coordinates, blacklist));
            
            blacklist.add(coordinates);
            graph.setNodeCenter(curr, coordinates);
        }
    }
    
    private static boolean isInList(PointD point, ArrayList<PointD> list){
        
        for (PointD curr : list){
            if(point.equals(curr))
                return true;
        }
        
        return false;
    }

    public static double randomDouble(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted;
    }
}

