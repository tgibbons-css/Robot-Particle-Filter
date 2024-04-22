package ParticleFilter;

import java.awt.Color;
import java.awt.Graphics2D;

import gui.MapJPanel;

// sensor measurements from the robot
// initially this will be 9 distance measurements in mm and inches taken by the kinect
// Kinect will take 3 measurements, one facing forward, one with kinect turned pi/2 to left, and one with kinect turned -pi/2 to right.
// Easch kinect measurement with return 3 measurements.  One straight ahead, one 0.49 radsians (28 degrees) left, one -0.49 radsians (-28 degrees) right.
public class measurement {
	//int num_meas = 4;		// number of measurements
	int num_meas = 9;		// number of measurements
	
	double[] meas = new double[num_meas];
	double[] angle = new double[num_meas];
	
	public measurement() {
		//initialize measurement angles.  Take measurements with kinect pointed -90, 0 and +90 degrees.  Each measurement records distance at -28,0,+28 degrees.
/*
		angle[0] = 0;
		angle[1] = Math.PI/2;
		angle[2] = Math.PI;
		angle[3] = Math.PI*3/2;
		
*/
		angle[0] = -2.06;
		angle[1] = -1.57;
		angle[2] = -1.08;
		angle[3] = -0.49;
		angle[4] = 0.00;
		angle[5] = 0.49;
		angle[6] = 1.08;
		angle[7] = 1.57;
		angle[8] = 2.06;
		
		for (int i=0;i<meas.length;i++) {
			meas[i] = 0;
		}
	}
	
	public measurement(measurement orig) {
		for (int i=0;i<meas.length;i++) {
			angle[i] = orig.angle[i];
			meas[i] = orig.meas[i];
		}	
		orig.num_meas = meas.length;	
	}
	
	public void copy(measurement cop) {
		for (int i=0;i<meas.length;i++) {
			cop.angle[i] = angle[i];
			cop.meas[i] = meas[i];
		}	
		cop.num_meas = meas.length;
	}
	
	public void print() {
		for (int i=0;i<meas.length;i++) {
			System.out.printf("%6.1f ",meas[i]);
		}
	}
	
	public void calcMeasurement(int x,	int y,	double orientation, MapJPanel map) {
		for (int i=0;i<meas.length;i++) {
			double ang = (angle[i] + orientation + Math.PI*2) % (Math.PI*2);
			double dist = map.scanObstacle(x,y,ang);
			meas[i] = Math.min(dist,200);			// set the maximum range of sensors to 100 
		}
	}
	
	double Gaussian(double mu, double sigma, double x){
        //calculates the probability of x for 1-dim Gaussian with mean mu and var. sigma
		double g = Math.exp(-1 * Math.pow((mu - x),2) / (Math.pow(sigma,2)) / 2.0) / Math.sqrt(2.0 * Math.PI * Math.pow(sigma,2));
        return g;
	}
        
	
	// # calculates how likely a measurement should be
	public double calcFitness (measurement goal) {
		double fit = 1.0;

		for (int i=0;i<meas.length;i++) {
			fit *= Gaussian(meas[i], 100.0, goal.meas[i]);
		}
		/*  simplified version that does not work as well
		double tot = 0;
		double dist;
		for (int i=0;i<meas.length;i++) {
			dist = Math.pow((meas[i]-goal.meas[i]), 2);
			tot += dist;
		}
		tot = Math.sqrt(tot) / meas.length;
		fit = 1/tot;
		*/
		return fit;
	}
	
	// Calculate Euclidean distance from this measurement to the goal measurement
	public double calcDistance (measurement goal) {
		double tot = 0;
		double dist;
		for (int i=0;i<meas.length;i++) {
			dist = Math.pow((meas[i]-goal.meas[i]), 2);
			tot += dist;
		}
/*		
		System.out.print("Dist from ");
		print();
		System.out.print(" to ");
		goal.print();
		System.out.println(" is "+tot+" --- Dist is "+Math.sqrt(tot) / meas.length);
		
*/		return Math.sqrt(tot) / meas.length;
	}

	// draw measurements
	public void drawlines (int x, int y, double orientation, Graphics2D g2) {
		// draw lines for each sensor reading
		Color oldColor = g2.getColor();
		g2.setColor(Color.GREEN);
		for (int i=0;i<meas.length;i++) {
			double ang = (angle[i] + orientation + Math.PI*2) % (Math.PI*2);
			int x2 = (int) Math.round(x + (Math.cos(ang) * meas[i]));
			int y2 = (int) Math.round(y - (Math.sin(ang) * meas[i]));
			g2.drawLine(x,y,x2,y2);			
		}
		g2.setColor(oldColor);		
	}
}
