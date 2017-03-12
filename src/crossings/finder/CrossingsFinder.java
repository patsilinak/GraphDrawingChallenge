/*Kosmas code
 * Thursday December 15th:
 * Corrected crossEdges (used to be crossLineSegment)
 * 
 * Corrected (/Changed) by Panagiotis
 */


package crossings.finder;

import java.util.ArrayList;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.utils.IListEnumerable;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

public class CrossingsFinder {
	
    private static final double noCrossing = -1;
	
    public static ArrayList<Crossing> BFAllCrossings(IGraph g) {

        ArrayList<Crossing> result = new ArrayList<Crossing>();
        
        IListEnumerable<IEdge> edges = g.getEdges();
        int len=edges.size();
        for (int i =0; i<len-1; i++)
            for (int j= i +1; j<len; j++) {
                double sine = crossEdges(edges.getItem(i),edges.getItem(j));
                // was sine > 0
                // NOT sine anymore!!
                if (sine >= 0) 
                    result.add(new Crossing(edges.getItem(i),edges.getItem(j),sine));
            }
        return result;
    }
	
	
    private static double crossEdges(IEdge e1,IEdge e2) {
			
        double result=noCrossing;
     
        PointD a = e1.getSourceNode().getLayout().getCenter();
        PointD b = e1.getTargetNode().getLayout().getCenter();
        PointD c = e2.getSourceNode().getLayout().getCenter();
        PointD d = e2.getTargetNode().getLayout().getCenter();
        

        boolean testOne = (crossProduct(a,b,a,c)*crossProduct(a,b,a,d) < 0 );
        boolean testTwo = (crossProduct(c,d,c,a)*crossProduct(c,d,c,b) < 0 );
        
        if (testOne && testTwo ) { 
            /*double length1 = getLineSegmentLength(a,b);
            double length2 = getLineSegmentLength(c,d);
            result = crossProduct(a,b,c,d)/(length1*length2);
            if (result < 0 ) 
                result = -result;*/
            double angle1 = atan2(a.getX() - b.getX(), a.getY() - b.getY());
            double angle2 = atan2(c.getX() - d.getX(), c.getY() - d.getY());
            double angle = angle1-angle2;
            result = toDegrees(angle) ;
            //Θέλουμε τη γωνία μεταξύ των ευθύγραμμων τμημάτων με ένα άκρο την 
            //τομή των ακμών και άλλο άκρο το "Destination" της κάθε ακμής 
            //αντίστοιχα.
            if (result < 0)
                result = -result;
            if (abs(result) > 180)
                result = 360 - abs(result);
        }
        return result;
        
    }
/*	
    private static double getLineSegmentLength(PointD a, PointD b) {
		
        //calculate length of line segment: sqrt (a^2+b^2) 
		
        return Math.sqrt((a.getX()-b.getX())*(a.getX()-b.getX()) + (a.getY()-b.getY())*(a.getY()-b.getY()));
    }
	*/
    
    /*private static double zeroAngleChecker(PointD a, PointD b, PointD c, PointD d){
        
        if (a)
    }*/
    
    private static double crossProduct(PointD a, PointD b, PointD c, PointD d) {
		
        double x_a = a.getX();
        double y_a = a.getY();
        double x_b = b.getX();
        double y_b = b.getY();
        double x_c = c.getX();
        double y_c = c.getY();
        double x_d = d.getX();
        double y_d = d.getY();
		
		
        return (x_b-x_a)*(y_d-y_c)-(y_b-y_a)*(x_d-x_c);
		
    }

    public static double smallestAngle(ArrayList<Crossing> list){
        
        double min = 180;
        for (Crossing curr : list){
            if (curr.getSineOfAngle() < min)
                min = curr.getSineOfAngle();
            if (180 - curr.getSineOfAngle() < min)
                min = 180 - curr.getSineOfAngle();
        }
        return min;
    }
}