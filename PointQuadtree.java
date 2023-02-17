import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 *
 * @author Sajjad C Kareem
 * @author Josue Godeme
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {

		//if the point x or y is out of bounds, just return
		if (p2.getX() < x1 || p2.getX() > x2 || p2.getY() < y1 || p2.getY() > y2) return;

//		if (point.getX() == p2.getX() && point.getY()==p2.getY()) return;

		if (checkQuadrant(p2) == 1) //if point is in quadrant 1
		{
			if (c1 != null) //if the child is not null
			{
				c1.insert(p2); //then just insert p in child 1
			} else{ // else if the child is null
				c1 = new PointQuadtree<E>(p2, (int)point.getX(), y1, x2, (int)point.getY()); //set child 1 to a new tree holding the point
			}
		}
		else if (checkQuadrant(p2) == 2) //if point is in quadrant 2
		{
			if(c2 != null) //if the child is not null
			{
				c2.insert(p2); //then just insert p in child 2
			} else { // else if the child is null
				c2 = new PointQuadtree<>(p2, x1, y1, (int)point.getX(), (int)point.getY());//set child 2 to a new tree holding the point
			}
		}
		else if (checkQuadrant(p2) == 3) //if point is in quadrant 3
		{
			if(c3 != null) //if the child is not null
			{
				c3.insert(p2); //then just insert p in child 3
			} else { // else if the child is null
				c3 = new PointQuadtree<>(p2, x1, (int)point.getY(), (int)point.getX(), y2); //set child 3 to a new tree holding the point
			}
		}
		else if (checkQuadrant(p2) == 4) //if point is in quadrant 4
		{
			if(c4 != null)//if the child is not null
			{
				c4.insert(p2); //then just insert p in child 4
			} else { // else if the child is null
				c4 = new PointQuadtree<>(p2, (int)point.getX(), (int)point.getY(), x2, y2); //set child 4 to a new tree holding the point
			}
		}

	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {

		int num = 1; //start of with a size of 1 due to root node

		if (hasChild(1)) //if quadrant 1 has a child
			num += c1.size(); //then recurse to that child and add what it returns to the size

		if (hasChild(2)) //if quadrant 2 has a child
			num += c2.size(); //then recurse to that child and add what it returns to the size

		if (hasChild(3)) //if quadrant 3 has a child
			num += c3.size(); //then recurse to that child and add what it returns to the size

		if (hasChild(4)) //if quadrant 4 has a child
			num += c4.size(); //then recurse to that child and add what it returns to the size

		return num; //return the size
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 *
	 * I was told by a TA that is it not necessary to make use of this method if we didn't
	 * then it would be fine :D
	 */
	public List<E> allPoints() {
		ArrayList<E> allPoints = new ArrayList<>(); //create a list of allPoints
		return allPoints; //return the list
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr)
	{

		ArrayList<E> points = new ArrayList<>(); //create a list that will hold all the points within the circle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) //if the circle intersects the rectangle
		{
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) //if the tree's point is in the circle
			{
				points.add(point); //add the point to the list
			}
			if (hasChild(1)) //if quadrant 1 has a child
			{
				points.addAll(c1.findInCircle(cx, cy, cr)); //Recurse with that child and add all the points in circle to the list
			}
			if (hasChild(2)) //same with quadrant 2
			{
				points.addAll(c2.findInCircle(cx, cy, cr));
			}
			if (hasChild(3)) //same with quadrant 3
			{
				points.addAll(c3.findInCircle(cx, cy, cr));
			}
			if (hasChild(4)) //same with quadrant 4
			{
				points.addAll(c4.findInCircle(cx, cy, cr));
			}
		}
		return points; //return the list of all the points within the circle
	}

	/**
	 *
	 * Helper method that calculates which quadrant the point is in
	 *
	 * @param p - pont that is passed
	 * @return - the integer representing which quadrant the point is in
	 */
	public int checkQuadrant(E p)
	{
		double px = point.getX(); //get the point X
		double py = point.getY(); //get the point Y

		if (p.getX() >= px && p.getY() <= py) { //check if the point is within bounds of quadrant 1
			return 1; //return quadrant 1
		} else if (p.getX() <= px && p.getY() <= py) { //check if the point is within bounds of quadrant 2
			return 2; //return quadrant 2
		} else if (p.getX() <= px && p.getY() >= py) { //check if the point is within bounds of quadrant 3
			return 3; //return quadrant 3
		} else if (p.getX() >= px && p.getY() >= py) { //check if the point is within bounds of quadrant 4
			return 4; //return quadrant 4
		} else {
			return -1; //else just return -1
		}
	}

}
