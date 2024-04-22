package Robot;

import gui.MapJPanel;

import java.awt.Color;
import java.awt.Graphics2D;

import ParticleFilter.measurement;
import ParticleFilter.particle;

public class RobotPC {

	//public double planForwardAmount;
	//public double planTurnAmount;
	private RobotSend robotSnd;
	private boolean liveRobot = false;
	public int x;
	public int y;
	public double orientation;
	public measurement sensorMeas;
	private MapJPanel mapPanel;

 	public RobotPC() {
		//planForwardAmount = 0.0;
		//planTurnAmount = 0.0;
		x = 95;
		y = 35;
		orientation = Math.PI*3/2;
		if (liveRobot) {
			robotSnd = new RobotSend();
		}
		sensorMeas = new measurement();
		//sensorMeas.calcMeasurement(x, y, orientation, mapPanel);
	}
 	
 	public RobotPC(int newX, int newY, double newOrient) {
		x = newX;
		y = newY;
		orientation = newOrient;
		if (liveRobot) {
			robotSnd = new RobotSend();
		}
		sensorMeas = new measurement();
		//sensorMeas.calcMeasurement(x, y, orientation, mapPanel);
	}
		
 	public void setMap(MapJPanel newmapPanel) {
 		mapPanel = newmapPanel;
 	}
 	
 	
	private void activateMotors(Integer rightPower, Integer leftPower, Integer tim) {
		if (liveRobot) {
			robotSnd.send_mot_right(rightPower);
			robotSnd.send_mot_left(leftPower);			
		}

		try {
			System.out.println("Sleeping");
			Thread.sleep(tim);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		if (liveRobot) {
			robotSnd.send_mot_right(0);
			robotSnd.send_mot_left(0);
		}
	}
	
	
	public void turnLeft(Integer tim, Integer powVal) {
		activateMotors(powVal, -1*powVal, tim);
		if (!liveRobot) {
			orientation =  (orientation + Math.PI/2 + 2*Math.PI) % (2*Math.PI);
			sensorMeas.calcMeasurement(x, y, orientation, mapPanel);
		}
	}
	
	public void turnRight(Integer tim, Integer powVal) {
		activateMotors(-1*powVal, powVal, tim);
		if (!liveRobot) {
			orientation =  (orientation - Math.PI/2) % (2*Math.PI);
			sensorMeas.calcMeasurement(x, y, orientation, mapPanel);
		}
	}
	
	public void stepForward(Integer tim, Integer powVal){
		activateMotors(powVal, powVal, tim);
		if (!liveRobot) {
			int dist = 10;
			x = (int) Math.round(x + (Math.cos(orientation) * dist));
			y = (int) Math.round(y - (Math.sin(orientation) * dist));
			sensorMeas.calcMeasurement(x, y, orientation, mapPanel);
		}
	}
	
	public void updateMeasurements() {
		sensorMeas.calcMeasurement(x, y, orientation, mapPanel);
	}
	
	public void shutdown(){
		if (liveRobot) {
			robotSnd.send_Quit();
			robotSnd.close();
		}
	}
	
	public void drawRobot(Graphics2D g) {
		Color oldColor = g.getColor();
		g.setColor(Color.BLUE);
		g.fillOval(x, y, 10, 10);
		int xc = x + 5;
		int yc = y + 5;
		g.drawLine(xc, yc, (int) Math.round(xc + (Math.cos(orientation) * 10)), (int) Math.round(yc - (Math.sin(orientation) * 10)));
		g.setColor(oldColor);
		sensorMeas.drawlines(x, y, orientation, g);
	}

	public void print() {
			System.out.print("Robot at "+x+", "+y);
			System.out.printf(" %6.1f radians ",orientation);
			sensorMeas.print();
			System.out.println();
	}
}
