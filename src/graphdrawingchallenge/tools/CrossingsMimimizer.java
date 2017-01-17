/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphdrawingchallenge.tools;

import com.yworks.yfiles.algorithms.AbortHandler;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;

/**
 *
 * @author Panagiotis
 */
public class CrossingsMimimizer {
    
    //FIXME !!!!!!!!!
    
    public static void minimizeCrossings(IGraph graph){
        LayoutUtilities.applyLayout(graph, new OrthogonalLayout());
        //ILayoutAlgorithm orthogonal = new OrthogonalLayout();
        //graph.applyLayout(new OrthogonalLayout());
        for (IEdge curr : graph.getEdges()){
            graph.clearBends(curr);
            //FIXME superugly
            //AbortHandler handler = new AbortHandler(); 
            /*graph.createEdge((INode) curr.getSourcePort().getOwner(),
                          (INode) curr.getTargetPort().getOwner());
            graph.remove(curr);*/
        }
            
    }
    
}
