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
import static graphdrawingchallenge.GraphDrawingChallenge.anglesPercentage;
import static graphdrawingchallenge.GraphDrawingChallenge.blockNewCrossings;
import static graphdrawingchallenge.GraphDrawingChallenge.useSmallestAngles;
import graphdrawingchallenge.tools.CrossingComparator;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.Collections;
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
    
    //Overloads for motion, should be done with varargs
    public void motion(int M, String mode, String priority){
        for (int i = 0; i<M; i++)
            move(mode, priority);
    }
    
    public void motion(int M, String mode, String priority, String anglesOfInterest){
        for (int i = 0; i<M; i++)
            move(mode, priority, anglesOfInterest);
    }
    //overloads for move()
    //probably should only be done for motion
    private void move(String mode){
        move(mode, "nodeFirst");
    }
    
    private void move(String mode, String priority){
        move (mode, priority, "All");
    }
    
    private void move(String mode, String priority, String anglesOfInterest){
        
        IListEnumerable<INode> nodes = calc_graph.getNodes();
        ArrayList<PointD> nodesVelocities = new ArrayList<>();

        for (INode currNode : nodes){
            
            crossings = BFAllCrossings(calc_graph);
            ArrayList<Crossing> sortedCrossings= crossings;
            PointD velocity = new PointD(0,0); 
            PointD force = new PointD(0,0);
            PointD previousNodeLocation = new PointD(0,0);
            Crossing smallestCross = new Crossing(null, null, 180);
            boolean smallestCrossFlag = false;
            int previousCrossingsNumber = 0;
            

            //Sorting and cropping for case of using only the smallest angles
            Crossing hey;
            int crossingsSize = sortedCrossings.size();
            if (useSmallestAngles){
                Collections.sort(sortedCrossings, new CrossingComparator());
                //Badbadbad
                for (int i = 0; i < crossingsSize - (int) (crossingsSize*anglesPercentage); i++){
                    hey = sortedCrossings.get(sortedCrossings.size() - 1);
                    sortedCrossings.remove(hey);
                }
                crossings = sortedCrossings;
               // crossings = sortedCrossings.subList(0, min(sortedCrossings.size(), (int) (sortedCrossings.size()/anglesPercentage)));
            }
            
            for (Crossing currCross : crossings){
                /*
                if ((currCross.getIndexOfFirstSeg().getTargetNode() == currNode))
                    force = add(force, angleSpring(currCross, "leftTarget"));
                if ((currCross.getIndexOfSecondSeg().getTargetNode() == currNode))
                    force = add(force, angleSpring(currCross, "rightTarget"));
                if ((currCross.getIndexOfFirstSeg().getSourceNode() == currNode) && mode.equals("rotateAll"))
                    force = add(force, angleSpring(currCross, "leftSource"));
                if ((currCross.getIndexOfSecondSeg().getSourceNode() == currNode) && mode.equals("rotateAll"))
                    force = add(force, angleSpring(currCross, "rightSource"));
                */
                if (!smallestCrossFlag){
                    smallestCross = currCross;
                    smallestCrossFlag = true;
                }
                if ((currCross.getIndexOfFirstSeg().getTargetNode() == currNode) ||
                    (currCross.getIndexOfSecondSeg().getTargetNode() == currNode)||
                    (currCross.getIndexOfFirstSeg().getSourceNode() == currNode) ||
                    (currCross.getIndexOfSecondSeg().getSourceNode() == currNode))
                    if(smallestCross.getSineOfAngle() > currCross.getSineOfAngle())
                        smallestCross = currCross;
                
                if (anglesOfInterest.equals("All"))
                    force = add(force, angleSpringCaller(mode, currCross, currNode));
                
            }
            
            if (anglesOfInterest.equals("Smallest") && smallestCrossFlag)
                force = add(force, angleSpringCaller(mode, smallestCross, currNode));
            
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
            if (blockNewCrossings){
                previousNodeLocation = currNode.getLayout().getCenter();
                previousCrossingsNumber = crossings.size();
            }
            if (priority.equals("nodeFirst"))
                calc_graph.setNodeCenter(currNode, add(currNode.getLayout().getCenter(), velocity));
            /*System.out.println("after");//*/  
            
            if (blockNewCrossings && BFAllCrossings(calc_graph).size() > previousCrossingsNumber)
                calc_graph.setNodeCenter(currNode, previousNodeLocation);
        }
        
        
        //FIXME!!! Bad practice, should use list of objects with fiels for node
        //and velocity. 
        //FIXME!! Iterator use without check of hasNext() or need of use.
        Iterator<PointD> nodesVelocitiesIterator = nodesVelocities.iterator();
        if (priority.equals("velocityFirst"))
            for (INode currNode : nodes)
                calc_graph.setNodeCenter(currNode, add(currNode.getLayout().getCenter(), nodesVelocitiesIterator.next()));


    }
    
    private PointD angleSpringCaller(String mode, Crossing cross, INode currNode){
                if ((cross.getIndexOfFirstSeg().getTargetNode() == currNode))
                    return angleSpring(cross, "leftTarget");
                if ((cross.getIndexOfSecondSeg().getTargetNode() == currNode))
                    return angleSpring(cross, "rightTarget");
                if ((cross.getIndexOfFirstSeg().getSourceNode() == currNode) && mode.equals("rotateAll"))
                    return angleSpring(cross, "leftSource");
                if ((cross.getIndexOfSecondSeg().getSourceNode() == currNode) && mode.equals("rotateAll"))
                    return angleSpring(cross, "rightSource");
                
                return new PointD(0,0);
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
                direction = getTangent(rightSource, rightTarget, leftSource); ////////!!!!Corrected rightSource to leftSource
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
        
        //FIXME !!! else;
        

        if (add(direction, mover).distanceTo(repeller) > add(negate(direction), mover).distanceTo(repeller)){
            direction = negate(direction);
        }
        
        return direction;
    }
}
