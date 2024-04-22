package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ParticleFilter.partFilter;
import Robot.RobotPC;

public class MapJPanel extends JPanel {

	private BufferedImage bimage;
	public int mapWidth;				// map width in pixels
	public int mapHeight;				// map height in pixels
	partFilter pfilter;					//  particle filter
	boolean dispParticles = false;		// display the particles or not
	RobotPC robo;						// the robot
	boolean dispRobo = false;			// display the robot or not
	int grey_value = 128;
	Graphics2D g2;					// graphics element for drawing
	
	/**
	 * Create the panel.
	 */
	public MapJPanel(String mapFilename) {
		try {
			bimage = ImageIO.read(new File(mapFilename));
			mapWidth = bimage.getWidth();
			mapHeight = bimage.getHeight();
		} catch (Exception ex) {
			System.out.println("Error opening map image file: "+ex.getMessage());
		}

		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	    g2 = (Graphics2D)g;
	    g.drawImage(bimage, 0, 0, null);

        if (dispParticles) {
        	pfilter.drawParticles(g2);
        }
        if (dispRobo) {
        	robo.drawRobot(g2);
        }
        
        
	}
	
	public void displayCanvasAsIntegers() {
		for(int y=0; y<mapHeight; y++){
			for(int x=0; x<mapWidth; x++){
				int pixel = bimage.getRGB(x,y);
			    int blue = (pixel) & 0xff;
				System.out.printf("%4d ",blue);
		    }
			System.out.println();
		}
	}

	public void setPfilter(partFilter newPfilter) {
		pfilter = newPfilter;
		dispParticles = true;
	}
	
	public void setRobot(RobotPC newRobo) {
		robo = newRobo;
		dispRobo = true;
	}
	
	// get the grey (actually blue) value of a pixel
	public int getPixel(int x, int y) {
		//return (testbuf.get(y*image.width()+x)&0xFF);
		return(bimage.getRGB(x, y) & 0xFF);
	}
	
	public boolean pixelFree(int x, int y) {
		return(getPixel(x,y) < grey_value );
	}

	public double scanObstacle(int x, int y, double angle) {
		// project a vector in the direction of angle starting at x,y and determine distance before an object is found
		// angle is in radians with 0 being straight to the left or east
		// draw_lines is true if we should draw the line found
		boolean obj_detected = false;
		int x_start = x;
		int y_start = y;
		double distance = 0;			// distance to the first obstacle
		//System.out.print("scanObstacle init to : "+ x_start+" , "+y_start);
		//System.out.println("---scanObstacle angle : "+ angle +" and tan : "+Math.tan(Math.PI/2 -angle));

		if ( y<0 || y>=mapHeight || x<0 || x>=mapWidth ) {
			//System.out.println("Map scanObstacles --- outside of map");
			obj_detected = true;
			distance = 0;
		} else if (getPixel(x,y) < grey_value) {
				// on top of an obstacle
				//System.out.println("Map scanObstacles --- on top of obstacle");
				obj_detected = true;
				distance = 0;
		}
		
		while (! obj_detected) {
			if( (angle<Math.PI/4)||(angle>Math.PI*7/4)) { 
				// scan left in positive x direction
				//System.out.println("[RIGHT] Pos X scanning "+ x+" , "+y+" angle : "+ angle +" and tan : "+Math.tan(angle));
				x ++;
				double yy = -1.0 * Math.tan(angle) * Math.abs(x-x_start);			// not sure if this tan() works for all angles
				y = y_start + (int) Math.round(yy); 				
			} else if(angle<Math.PI*3/4) { 
				// scan up in negative y direction
				//System.out.println("[UP] Neg Y scanning "+ x+" , "+y+" angle : "+ angle +" and tan : "+Math.tan(Math.PI/2 -angle));
				y --;
				double xx = Math.tan(Math.PI/2 -angle) * Math.abs(y-y_start);			// not sure if this tan() works for all angles
				x = x_start + (int) Math.round(xx); 
			} else if(angle<Math.PI*5/4) { 
				// scan right in negative x direction
				//System.out.println("[LEFT] Neg X scanning "+ x+" , "+y+" angle : "+ angle +" and tan : "+Math.tan(angle));
				x --;
				double yy = Math.tan(angle) * Math.abs(x-x_start);			// not sure if this tan() works for all angles
				y = y_start + (int) Math.round(yy); 				
			} else {
				// scan down in positive y direction
				//System.out.println("[UP] Neg Y scanning "+ x+" , "+y+" angle : "+ angle +" and tan : "+Math.tan(Math.PI/2 -angle));
				y ++;
				double xx = -1.0 * Math.tan(Math.PI/2 -angle) * Math.abs(y-y_start);			// not sure if this tan() works for all angles
				x = x_start + (int) Math.round(xx); 
			}
			
			//System.out.println("scanObstacle checking at : "+ x +" , "+y);
			if ( y<0 || y>=mapHeight || x<0 || x>=mapWidth ) {
				// out of range of image
				//System.out.println("scanObstacle our of range --- height: "+image.height()+" and width:"+image.width());
				obj_detected = true;
				distance = Math.sqrt( Math.pow(x - x_start, 2) + Math.pow(y - y_start, 2) );
			} else if (getPixel(x,y) < grey_value) {					// is an obstacle pixel 1???
				// found an obstacle
				//System.out.println("scanObstacle +++ obstacle at : "+ x+" , "+y+" value of "+getPixel(x,y));
				obj_detected = true;
				distance = Math.sqrt( Math.pow(x - x_start, 2) + Math.pow(y - y_start, 2) );
			}
		}

		return distance;

	}


}
