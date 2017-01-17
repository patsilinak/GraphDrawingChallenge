/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embedders;

import com.yworks.yfiles.geometry.PointD;
import static com.yworks.yfiles.geometry.PointD.add;
import static com.yworks.yfiles.geometry.PointD.subtract;
import static com.yworks.yfiles.geometry.PointD.times;
import static com.yworks.yfiles.graph.AdjacencyTypes.ALL;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IListEnumerable;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import java.util.ArrayList;

/**
 *
 * @author Panagiotis
 */
public class SpringEmbedder {
    
    IGraph calc_graph;
    double const1, const2, const3, speed;
    
   // ArrayList<NodeInfo> Nodes = new ArrayList<NodeInfo>();
   
    public SpringEmbedder(IGraph graph){
        calc_graph = graph;
        
        //σταθερές spring-embedding. Έχουν τοποθετηθεί εμπειρικά.
        const1 = 2;
        const2 = 100;
        const3 = 100;
        speed = 0.1;
    }
    
    public void motion(int M){
        for (int i = 0; i<M; i++)
            move();
    }
    
    private void move(){
        
        IListEnumerable<INode> nodes = calc_graph.getNodes();

        for (INode curr : nodes){
            PointD force = new PointD(0,0);
            //Μπορώ αντί για ALL να βάλω INCOMMING και να έχω έλξη μόνο από γονείς
            IListEnumerable<IEdge> connectedEdges = calc_graph.edgesAt(curr, ALL);
            ArrayList<INode> children = new ArrayList<INode>();
            for (IEdge child: connectedEdges){
                //αντίστοιχα μπορώ να βάλω μόνο source ή target
                children.add(child.getSourceNode());
                children.add(child.getTargetNode());
            }
            
            //Παρακάτω, όποτε χρησιμοποιούμε τις add, subtract και times, 
            //είναι οι αντίστοιχες static συναρτήσεις της κλάσης PointD
            //(κάνουν πράξεις μεταξύ PointD αντικείμενα, σαν διανύσματα)
            
            for (INode otherNode : nodes){
                if (otherNode != curr){
                     force = add(force, coulomb(curr, otherNode));
                    if (children.contains(otherNode))
                        force = subtract(force, hook(curr, otherNode));
                }
            }
            
            PointD velocity = new PointD(0,0); 
            velocity = add(velocity, force);
            velocity = times(velocity, speed);
            calc_graph.setNodeCenter(curr, add(curr.getLayout().getCenter(), velocity));

        }
    }
    
    private PointD hook(INode parent, INode child){
        PointD direction = new PointD(0,0);
        direction = subtract(parent.getLayout().getCenter(), child.getLayout().getCenter());
        return times(direction, log(parent.getLayout().getCenter().distanceTo(child.getLayout().getCenter()) / const2 )* const1);
    }
    
    private PointD coulomb(INode parent, INode child){
        PointD direction = new PointD(0,0);
        direction = subtract(parent.getLayout().getCenter(), child.getLayout().getCenter());
        return times(direction,  const3 / pow(parent.getLayout().getCenter().distanceTo(child.getLayout().getCenter()), 2));
    }
    
}
