import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 *
 * @author Sajjad C Kareem
 * @author Josue Godeme
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();
		colliders = new ArrayList<>(); //initialize the colliders list
		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g)
	{

		// Ask all the blobs to draw themselves.
		for (Blob blob : blobs) //for each blob in the list of blobs
		{
			blob.draw(g); //draw the blob
		}
		// Ask the colliders to draw themselves in red.
		if (collisionHandler == 'c') //if it is in 'c' mode
		{
			for (Blob collider : colliders) //for each collided blob in the list of colliders
			{
				g.setColor(Color.red); //set color to red
				collider.draw(g); //draw the collided blobs in red
			}
		}

	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders()
	{
		// Create the tree
		// For each blob, see if anybody else collided with it
		PointQuadtree<Blob> tree = null;

		for(Blob blob: blobs) //for each blob in the list of blobs
		{
			if (tree == null) //if the tree is null
			{
				tree = new PointQuadtree<>(blob, 0, 0, width, height); //set the tree holding that bob
			} else {
				tree.insert(blob); //else insert the bob in the tree
			}
		}

		colliders = new ArrayList<>(); //initialize the list of colliders
		for (Blob blob : blobs) //for each blob in the list of blobs
		{
			//create a list that holds intersecting blobs
			List<Blob> intersectBlobs = tree.findInCircle(blob.getX(), blob.getY(), blob.getR() * 2);
			//calculated by finding in circle of the current blob around twice its radius

			for (Blob intersecting : intersectBlobs) //for each intersecting blob
			{
				if (blob != intersecting) //if it is not the same blob itself
				{
					colliders.add(blob); //add the blob to the list of colliders
					colliders.add(intersecting); //add the intersecting blobs to the list of colliders
				}
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
