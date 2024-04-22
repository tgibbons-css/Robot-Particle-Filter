package ParticleFilter;
import gui.MapJPanel;

import java.util.Random;

// this is a single particle in a particle filter.  Each particle is a possible location, with orientation, of the robot.
public class particle {
	
	public int index;
	public int x;
	public int y;
	int maxX;
	int maxY;
	public double orientation;			// heading of robot in radians with 0 being straight to the right or positive x direction and pi/2 radians is up or positive y
	public double distance;			// sum of the distance of each senesor reading
	public double fitness;				// likelihood of this particle matching robots, does not sum to one
	public double probability;			// probability of this particle matching robots, these do sum to 1.0
	measurement meas;
	
	particle() {
		//index = newIndex;
		maxX = 100;
		maxY = 100;
		randPos(maxX, maxY);								// initial x and y to random values
		meas = new measurement();
	}
	
	particle(particle orig) {
		index = orig.index;
		x = orig.x;
		y = orig.y;
		maxX = orig.maxX;
		maxY = orig.maxY;
		orientation = orig.orientation;			
		distance = orig.distance;		
		fitness = orig.fitness;				
		probability = orig.probability;
		meas = new measurement(orig.meas);
	}
	particle(int maxX, int maxY, int newIndex) {
		index = newIndex;
		this.maxX = maxX;
		this.maxY = maxY;
		randPos(maxX, maxY);								// initial x and y to random values
		//System.out.println("New particle at at "+x+", "+y);
		meas = new measurement();
	}

	void copy (particle cop) {
		cop.index = index;
		cop.x = x;
		cop.y = y;
		cop.maxX = maxX;
		cop.maxY = maxY;
		cop.orientation = orientation;			
		cop.distance = distance;		
		cop.fitness = fitness;				
		cop.probability = probability;
		cop.meas = new measurement();
		meas.copy(cop.meas);
	}

	
	// set the maximum size of the grid or image map
	public void randPos(int maxX, int maxY) {
		Random rand = new Random();
		x = rand.nextInt(maxX); 							// initial x position
		y = rand.nextInt(maxY); 							// initial y position
		orientation = rand.nextFloat() * 2.0 * Math.PI; 	// initial orientation in radians
	}
	
	void print() {
		System.out.print("Particle "+index+" ");
		System.out.print(" at "+x+", "+y);
		System.out.printf(" %6.1f radians ",orientation);
		meas.print();
		System.out.printf(" --- dist %.1f fit %.3f prob %.6f ", distance, fitness,  probability);
		System.out.println();
	}
	
	void updateMeasurement(MapJPanel map) {
		meas.calcMeasurement(x,	y, orientation, map);	
	}
	
	// move particle forward dist pixels or units
	void moveforward(double dist) {
		x = (int) Math.round(x + (Math.cos(orientation) * dist));
		y = (int) Math.round(y - (Math.sin(orientation) * dist));
	}
	
	// rotate particle angle radians
	void rotate(double angle) {
		orientation = (orientation + angle + (2*Math.PI)) % (2*Math.PI);
	}
	
	double calcDistance(measurement goal) {
		distance = meas.calcDistance(goal);
		return distance;
	}
	
	double calcFitness(measurement goal) {
		//System.out.print("Particle "+index);
		fitness = meas.calcFitness(goal);
		return fitness;
	}
	
	public double normalizeFitness(double totProb) {
		// reset fitness values so that they are probabilities... sum to one
		probability = fitness / totProb;
		return probability;
	}
	
	void addNoise() {
		int noiseVal = 40;
		Random rand = new Random();
		int change;
		change = rand.nextInt(noiseVal) - noiseVal/2;
		//System.out.print("Adding noise "+change+" ... ");
		x = x + change; 											// noise x position
		if (x<0) x = 0;
		if (x>maxX) x = maxX;
		y = y + rand.nextInt(noiseVal) - noiseVal/2; 							// noise y position
		if (y<0) y =0;
		if (y>maxY) y = maxY;
		orientation += rand.nextFloat() * 0.2 - 0.1; 			// initial orientation in radians
 
	}

}
