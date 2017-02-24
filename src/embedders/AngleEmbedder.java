/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embedders;

import com.yworks.yfiles.geometry.PointD;
import static com.yworks.yfiles.geometry.PointD.add;
import static com.yworks.yfiles.geometry.PointD.negate;
import static com.yworks.yfiles.geometry.PointD.times;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IListEnumerable;
import crossings.finder.Crossing;
import static crossings.finder.CrossingsFinder.BFAllCrossings;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Panagiotis
 */
public class AngleEmbedder {
    
    IGraph calc_graph;
    double speed;
       
    ArrayList<Crossing> crossings;

    public AngleEmbedder(IGraph graph){
        calc_graph = graph;
        
        //σταθερές spring-embedding. Έχουν τοποθετηθεί εμπειρικά.
        speed = 1;
    }
    
    public void motion(int M, String mode, String priority){
        for (int i = 0; i<M; i++)
            move(mode, priority);
    }
    
    private void move(String mode, String priority){
        
        IListEnumerable<INode> nodes = calc_graph.getNodes();
        ArrayList<PointD> nodesVelocities = new ArrayList<>();

        for (INode currNode : nodes){
            
            crossings = BFAllCrossings(calc_graph);
            PointD velocity = new PointD(0,0); 
            PointD force = new PointD(0,0);
            for (Crossing currCross : crossings){
                // Κοιτάμε μόνο τα destinations γιατί αν μετακινήσουμε αυτά,
                //φτοιάχνουν ταυτόχρονα και οι γωνίες από την μεριά των sources
                if ((currCross.getIndexOfFirstSeg().getTargetNode() == currNode))
                    force = add(force, angleSpring(currCross, "leftTarget"));
                if ((currCross.getIndexOfSecondSeg().getTargetNode() == currNode))
                    force = add(force, angleSpring(currCross, "rightTarget"));
                if ((currCross.getIndexOfFirstSeg().getSourceNode() == currNode) && mode.equals("rotateAll"))
                    force = add(force, angleSpring(currCross, "leftSource"));
                if ((currCross.getIndexOfSecondSeg().getSourceNode() == currNode) && mode.equals("rotateAll"))
                    force = add(force, angleSpring(currCross, "rightSource"));
            }
            
            velocity = add(velocity, force);
            velocity = times(velocity, speed);
            nodesVelocities.add(velocity);
            /// TEST!!!!!!!
            /*String s = "Before, node x=";
            s += currNode.getLayout().getCenter().x;
            s += " ,node y=";
            s += currNode.getLayout().getCenter().y;
            s += ". Going TO:";
            s += add(currNode.getLayout().getCenter(), velocity).x;
            s += " , ";
            s += add(currNode.getLayout().getCenter(), velocity).y;
            System.out.println(s);*/
            if (priority.equals("nodeFirst"));
                calc_graph.setNodeCenter(currNode, add(currNode.getLayout().getCenter(), velocity));
            /*System.out.println("after");//*/    
        }
        
        
        //FIXME!!! Bad practice, should use list of objects with fiels for node
        //and velocity. 
        //FIXME!! Iterator use without check of hasNext() or need of use.
        Iterator<PointD> nodesVelocitiesIterator = nodesVelocities.iterator();
        if (priority.equals("velocityFirst"))
            for (INode currNode : nodes)
                calc_graph.setNodeCenter(currNode, add(currNode.getLayout().getCenter(), nodesVelocitiesIterator.next()));


    }
        
    private PointD angleSpring(Crossing crossing, String mode){
        PointD direction = new PointD(0,0);
        double angleOfReference =0;

        PointD leftTarget = crossing.getIndexOfFirstSeg().getTargetNode().getLayout().getCenter();
        PointD leftSource = crossing.getIndexOfFirstSeg().getSourceNode().getLayout().getCenter();
        PointD rightTarget = crossing.getIndexOfSecondSeg().getTargetNode().getLayout().getCenter();
        PointD rightSource = crossing.getIndexOfSecondSeg().getSourceNode().getLayout().getCenter();


        switch (mode){
            case "leftTarget":
                direction = getTangent(leftTarget, leftSource, rightTarget);
                break;
            case "rightTarget":
                direction = getTangent(rightTarget, rightSource, leftTarget);
                break;
            case "leftSource":
                direction = getTangent(leftSource, leftTarget, rightSource);
                break;
            case "rightSource":
                direction = getTangent(rightSource, rightTarget, rightSource);
                break;
                
        }

        angleOfReference = crossing.getSineOfAngle();

        return times(direction, log(angleOfReference / 90 )* 2);
    }
    
    private PointD getTangent(PointD mover, PointD base, 
            PointD repeller){
                
        double cs = cos(Math.PI/2);
        double sn = sin(Math.PI/2);
        
        double x = mover.getX() - base.getX();
        double y = mover.getY() - base.getY();
        double px = x * cs - y * sn;
        double py = x * sn + y * cs;
        
        PointD direction = new PointD(px, py);
        if(abs(px)!=0)
            direction = new PointD(px/abs(px), py/abs(px));      
        

        if (add(direction, mover).distanceTo(repeller) > add(negate(direction), mover).distanceTo(repeller)){
            direction = negate(direction);
        }
        
        return direction;
    }
}
