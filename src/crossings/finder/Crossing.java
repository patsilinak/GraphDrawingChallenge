package crossings.finder;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;

public class Crossing implements Comparable {
	
	//this class defines a crossing, found by our crossings finder.
	
	private IEdge edge1, edge2;
	//PointD crossingPoint;
	//int crossingID;
	
	private double sineOfAngle;
	
	public Crossing(IEdge left,IEdge right, double sine) {
		//a very basic constructor
		
		this.edge1=left;
		this.edge2=right;
		this.sineOfAngle=sine;
		//this.crossingID=crossingID;
		
	}
	
	public IEdge getIndexOfFirstSeg(){return edge1;}
	public IEdge getIndexOfSecondSeg(){return edge2;}
	public double getSineOfAngle(){return sineOfAngle;}
	
	
	public int compareTo(Object o){
	Crossing other = (Crossing) o;
		
		if(this.sineOfAngle < other.sineOfAngle)
			return - 1;
		
		if (this.sineOfAngle == other.sineOfAngle)
			return 0;
		
		return 1;
	}

	
}


