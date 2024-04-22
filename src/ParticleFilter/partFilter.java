package ParticleFilter;

import gui.MapJPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
* partFilter implements a particle filter with a set of random particles representing possible robot positions.
* 
* Note: this is the starting version for an assignment and does not work yet. The method resampleParticles must be re-written.
* 
* @author  Tom Gibbons
* @version 2.1
* @since   Fall 2016 
*/
public class partFilter {
	
	ArrayList<particle> partList = new ArrayList<particle>(); 	// The array list containing all the particles
	MapJPanel map;							// a link to the map for displaying the particles
	int num_part = 500;						// the number of particles to create. 
	public particle probablePart;					// most probable particle and estimate of where the robot is
	
   /**
   * Constructor with map size and robot location.
   */
	public partFilter() {
		for (int i=0; i<num_part; i++) {
			particle p = new particle();
			partList.add(p);
		}
		probablePart = partList.get(0);			// initialize the most probable particle to the first one to start with
	}
   /**
   * Constructor with map size and robot location.
   * @param mapP link to the map panel for displaying the particles on the screen
   * @param maxX and maxY Maximum dimensions of the map in pixels
   */	
	public partFilter(MapJPanel mapP, int maxX, int maxY) {
		particle p;
		map = mapP;
		for (int i=0; i<num_part; i++) {
			do {
				p = new particle(maxX, maxY,i);
			} while (map.pixelFree(p.x,p.y));
			partList.add(p);
		}
		probablePart = partList.get(0);			// initialize the most probable particle to the first one to start with

	}
   /**
   * Constructor with map size and robot location.
   * @param mapP link to the map panel for displaying the particles on the screen
   * @param maxX and maxY Maximum dimensions of the map in pixels
   * @param robotX and robotY Location of the robot on the map. Particles will be created around this location
   * @param robotOrientation orientation of the robot in radians so 2*pi = 360 degrees
   */
	public partFilter(MapJPanel mapP, int maxX, int maxY, int robotX, int robotY, double robotOrientation) {
		particle p;
		map = mapP;
		Random r = new Random();
		double spreadFactor = 100.0;		
		for (int i=0; i<num_part; i++) {
			do {
				p = new particle(maxX, maxY,i);
				p.x = (int) (robotX + r.nextGaussian() * spreadFactor);
				p.y = (int) (robotY + r.nextGaussian() * spreadFactor);
				p.orientation = robotOrientation + r.nextGaussian();
				p.x = Math.max(0,Math.min(maxX-1,p.x));
				p.y = Math.max(0,Math.min(maxY-1,p.y));
			    //System.out.println(" --- new particle "+i+" robotX="+robotX+" robotY="+robotY+" partX="+p.x+" partY="+p.y);
			} while (map.pixelFree(p.x,p.y));
			partList.add(p);
		}
		probablePart = partList.get(0);			// initialize the most probable particle to the first one to start with

	}
	
   /**
   * print out each particle using a for each loop and the particles print method
   */
	public void display() {
		for(particle p: partList){
			p.print();
		}
	}
   /**
   * print out each particle using a for loop. Can be altered to print out the fitness also
   */	
	public void print() {
		for (int i=0; i<num_part; i++) {
			System.out.print("P "+i+" ");
			partList.get(i).print();
			//System.out.println(" --- fitness  "+partList.get(i).fitness);
		}
	}
   /**
   * draw a single particle on the graphics context
   * @param x - Particle x location
   * @param y - Particle y location
   * @param orient - Particle orientation
   * @param size - Size of the rectangle to use
   * @param g - Graphics context to use
   * @param c - Color to use
   */
	void drawPart(int x, int y, double orient, int size, Graphics2D g, Color c){
		g.setColor(c);
		g.fillRect(x, y, size, size);
		int xc = x + size/2;
		int yc = y + size/2;
		g.drawLine(xc, yc, (int) Math.round(xc + (Math.cos(orient) * size)), (int) Math.round(yc - (Math.sin(orient) * size)));
	}
	
   /**
   * draw all the particles on the graphics context
   * @param g - Graphics context to use
   */
	public void drawParticles(Graphics2D g) {
		Color oldColor = g.getColor();;
		for(particle p: partList){
			drawPart(p.x, p.y, p.orientation, 4, g, Color.RED);
			Random rand = new Random();
			// only draw lines for a small number of particles
			int change  = rand.nextInt(num_part/5);
			if (change<=1) {
				p.meas.drawlines(p.x, p.y, p.orientation, g);				// draw the measurement lines to obstacles in 1% of particles
				//Double f = p.fitness;
				//g.drawString(f.toString(), p.x+2, p.y);
				
				//Double d = p.distance;
				//g.drawString(d.toString(), p.x, p.y+10);
				//g.drawString(String.format("%.2f",d), p.x+5, p.y+12);
			} 
		}
		// draw the probable locaton of the robot in yellow
		drawPart(probablePart.x, probablePart.y, probablePart.orientation, 10, g, Color.YELLOW);
		g.setColor(oldColor);
	}
   /**
   * move particle forward dist pixels or unit
   * @param dist - Distance to move in pixels
   */
	public void moveforward(double dist) {
		for(particle p: partList){
			p.moveforward(dist);
		}	
	}
   /**
   * rotate particle angle radians
   * @param angle - Angle to rotate in radians where 2pi radians = 360 degrees
   */	
	public void rotate(double angle) {
		for(particle p: partList){
			p.rotate(angle);
		}	
	}
	/**
   * update the sensor measurements for all the particles
   * @param map - map to use to find walls
   */		
	public void updateMeasurements(MapJPanel map) {
		for(particle p: partList){
			p.updateMeasurement(map);
		}		
		
	}
   /**
   * add random noise to all the particles
   */			
	public void addNoise() {
		for(particle p: partList){
			p.addNoise();
		}		
	}
   /**
   * returns a fitness value between 0 and 1 for each particle.  The higher the number, the more fit
   * @param goal - The robot's sensor measurements to compare against
   */	
	public void calcFitness(measurement goal) {
		double max = 0;
		double dist = 0;
		double prob = 0;
		double totalProb = 0;
		for(particle p: partList){
			dist = p.calcDistance(goal);
			if(dist>max) {
				max = dist;
			}
		}
		for(particle p: partList){
			prob = p.calcFitness(goal);
			totalProb += prob;
		}
		// normalize fitness so they sum to 1.0 for probabilities
		// also find the most probable particle
		max = 0;
		for(particle p: partList){
			prob = p.normalizeFitness(totalProb);
			if (prob>max) {
				max = prob;
				probablePart = p;
			}
			//System.out.println("Probability of particle "+p.index+" is "+p.probability);
		}
	}
   /**
    * =====================================================
    * For the assignment modify the code in the method below
    * =====================================================
    * resampleParticles() should select particles based on their probability.  The higher a particle's probability the more often it should be selected
    *  --- partList is the arraylist of particles in the filter
    *  --- given a particle p, p.probability measures how good this particle is.  This ranges from 0.0 to 1.0 and the sum of all the probabilities of all the particles is 1.0
    */	
	public void resampleParticles() {
		Random rand = new Random();                                     // needed for random number generation
		int index = rand.nextInt(num_part);                             // random integer between 0 and number of particles
		ArrayList<particle> particlesCopy = new ArrayList<particle>(); 	// Empty Arraylist to copy particles into
		particle pNew;							// will hold the new particle created
		particle pOrig;							// will hold the old particle to copy
		// loop through number of particles. Creating one new particle each time through loop
		for (int i=0; i<num_part; i++) {
			index = pickParticle1();                                // call one of the methods for picking a particle to keep
			pOrig = partList.get(index);                            // using the index, grab the old particle
			pNew = new particle(pOrig);				// create the new copy of the original particle
			particlesCopy.add(pNew);				// add the new copy to the particle list
		}  // end for loop
		partList = particlesCopy;                                       // replace original array of particles with new ones
	} // end of resampleParticles() method
	
        /**
          * pickParticle0 simply picks a random index and ignores the fitness 
          */
        public int pickParticle0() {
            Random rand = new Random();                 // we need a random number generator
            int index = rand.nextInt(num_part);         // pick a random index
            return index;
        } //end of pick particle 0

        /**
          * pickParticle1 does what??? 
          */        
        public int pickParticle1() {
            Random rand = new Random();                 // we need a random number generator
            int index = rand.nextInt(num_part);         // pick a random index
            for(int i=0; i<10; i++){
                int temp = rand.nextInt(num_part);     // pick another random index               
                if(partList.get(index).probability<partList.get(temp).probability){
                    index = temp;  
                }
            }
            return index;
        } //end of pick particle 1

        /**
          * pickParticle2 does what??? 
          */        
        public int pickParticle2(){
		double sumProb; 
		Random rand = new Random();                 // we need a random number generator
		double probLimit = rand.nextFloat();        //random between 0.0 and 1.0	
		int index = 0;
                sumProb = partList.get(index).probability;
		while (sumProb < probLimit){
                    index++;
		    sumProb += partList.get(index).probability;
		}
		return index;			
	}//end of pick particle 2
        
        /**
          * pickParticle3 does what??? 
          */        
        public int pickParticle3() {
            int index;
            double highProb = 0.001;
            Random rand = new Random();                 // we need a random number generator
            do {       
                index = rand.nextInt(num_part);         // pick a random index    
            } while (partList.get(index).probability < highProb);           
            return index;
        } //end of pick particle 3
        
}  // end of this class
