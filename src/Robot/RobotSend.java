package Robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RobotSend {
	// This class sends data to the robot in a basic protocol.
	//  All data is sent in integer pairs.  The first integer is the data type, on of the constants below.  The second integer is the value 
	// constants for types of data
	final int KINECT_SERVO = -1001;
	final int TILT_SERVO = -1002;
	final int MOTOR_LEFT = -1003;
	final int MOTOR_RIGHT = -1004;
	final int MOTOR_PWM = -1005;
	final int MOTOR_PWMSTOP = -1006;
	final int QUIT = -1099;
	
	DataInputStream inDat;
	DataOutputStream outDat;
	

	public RobotSend () {
            System.out.println("RobotSend - 1");
	}
	
	public void send_kinect_angle(int ang) {
            System.out.println("send_kinect_angle - 5");
	}
	
	public void send_tilt_angle(int ang) {
            System.out.println("send_tilt_angle 1 - " + TILT_SERVO); 
	}
	
	public void send_mot_left(int pow) {
            System.out.println("send_mot_left - 1");
	}

	public void send_mot_right(int pow) {
            System.out.println("send_mot_right - 1");
	}
	
	public void send_PWM(int pow, int speed) {

	}
	
	public void  send_PWMstop() {
	}
	
	public void  send_Quit() {
	}

	public void close () {
	}

}


