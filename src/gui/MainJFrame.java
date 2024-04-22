package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollBar;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import javax.swing.JButton;

import Robot.RobotPC;
import Robot.RobotSend;
import ParticleFilter.partFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainJFrame extends JFrame {

	private JPanel contentPane;
	private JLabel lblRobotConroller;

	private boolean conn_NXT = true; // Set to false when testing the interface
										// without the NXT
	public partFilter pfilter;			// Particle Filter
	public RobotPC robo;				// Robot Interface
	MapJPanel mapPanel;					// Map image in a JPanel
	double inchesPerPixel;				// number of inches per pixel on the map
	
	private JButton btnTurnLeft;
	private JButton btnTurnRight;
	private JLabel lblStatus;
	private JTextField txtTime;
	private JTextField txtPower;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainJFrame frame = new MainJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainJFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 843, 533);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblRobotConroller = new JLabel("Robot Conroller - 2016 Start");
		lblRobotConroller.setHorizontalAlignment(SwingConstants.CENTER);
		lblRobotConroller.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblRobotConroller.setBounds(10, 11, 337, 32);
		contentPane.add(lblRobotConroller);

		txtTime = new JTextField();
		txtTime.setText("1000");
		txtTime.setBounds(200, 241, 86, 20);
		contentPane.add(txtTime);
		txtTime.setColumns(10);

		txtPower = new JTextField();
		txtPower.setText("10");
		txtPower.setBounds(200, 272, 86, 20);
		contentPane.add(txtPower);
		txtPower.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Time (msec)");
		lblNewLabel_2.setBounds(200, 226, 64, 14);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Power Level");
		lblNewLabel_3.setBounds(200, 259, 81, 14);
		contentPane.add(lblNewLabel_3);
		
		JButton btnActivate = new JButton("Forward");
		btnActivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblStatus.setText("Running - Forward");
				System.out.println("Running - Forward");				
				Integer tim = Integer.parseInt(txtTime.getText());
				int powVal = Integer.parseInt(txtPower.getText());
				
				robo.stepForward(tim, powVal);
				pfilter.moveforward(10);
				doUpdate();
				
				lblStatus.setText("Done - Forward");
				System.out.println("Done - Forward");
			}
		});
		btnActivate.setBounds(56, 186, 107, 23);
		contentPane.add(btnActivate);

		btnTurnLeft = new JButton("Turn Left");
		btnTurnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblStatus.setText("Running - Left Turn");
			
				Integer tim = Integer.parseInt(txtTime.getText());
				int powVal = Integer.parseInt(txtPower.getText());
				robo.turnLeft(tim, powVal);
				pfilter.rotate(Math.PI/2);
				doUpdate();
				lblStatus.setText("Done - Left Turn");

			}
		});
		btnTurnLeft.setBounds(56, 123, 107, 23);
		contentPane.add(btnTurnLeft);

		btnTurnRight = new JButton("Turn Right");
		btnTurnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblStatus.setText("Running - Right Turn");
				System.out.println("Running - Right Turn");			
				Integer tim = Integer.parseInt(txtTime.getText());
				Integer powVal = Integer.parseInt(txtPower.getText());
				
				robo.turnRight(tim, powVal);
				pfilter.rotate(-1*Math.PI/2);
				doUpdate();
				
				lblStatus.setText("Done - Right Turn");
				System.out.println("Done - Right Turn");

			}
		});
		btnTurnRight.setBounds(56, 157, 107, 23);
		contentPane.add(btnTurnRight);

		lblStatus = new JLabel("Status");
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblStatus.setBounds(24, 245, 312, 63);
		contentPane.add(lblStatus);

		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// send the quit command to the robot, close the connection and
				// quit the controller		
				robo.shutdown();		
				System.exit(0);
			}
		});
		btnQuit.setBounds(173, 126, 122, 83);
		contentPane.add(btnQuit);

		// Load map file
		String mapFilename;
		//mapFilename = "HouseMap2_gray.jpg";
		mapFilename = "tower_3rd_bw.jpg";
		mapFilename = "tower_3rd_bw_small.jpg";
		inchesPerPixel = 3.0;
		
		System.out.println("Loading map : "+mapFilename);
		// Load MapJPanel and display map
		mapPanel = new MapJPanel(mapFilename);
		mapPanel.setBounds(357, 11, mapPanel.mapWidth, mapPanel.mapHeight );
		contentPane.add(mapPanel);
		System.out.println("Map size : "+mapPanel.mapWidth+" , "+mapPanel.mapHeight);
		
		// Instantiate robot and particles.  Register particle system with map
		robo = new RobotPC(150,400,Math.PI*3/2);			// robot position for tower outside Tom's Office
		//robo = new RobotPC(95,35,Math.PI*3/2);			// robot position for house map
		mapPanel.setRobot(robo);
		robo.setMap(mapPanel);
		//pfilter  = new partFilter(mapPanel, mapPanel.mapWidth, mapPanel.mapHeight);			// particle filter with random particles over entire map
		pfilter  = new partFilter(mapPanel, mapPanel.mapWidth, mapPanel.mapHeight, robo.x, robo.y, robo.orientation);				// random particles around robot
		mapPanel.setPfilter(pfilter);
		
		JButton btnRepaint = new JButton("Update");
		btnRepaint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doUpdate();
				
			}
		});
		btnRepaint.setBounds(56, 54, 107, 23);
		contentPane.add(btnRepaint);


	}
	
	public void redawMap() {
		mapPanel.repaint();
		this.repaint();
	}
	
	public void doUpdate() {
		System.out.println("doUpdate");
		robo.updateMeasurements();
		robo.print();
	
    	pfilter.updateMeasurements(mapPanel);
    	pfilter.calcFitness(robo.sensorMeas);
    	//pfilter.print();									// uncomment this to display particle info after each move
    	pfilter.resampleParticles();
    	
    	pfilter.addNoise();

 		redawMap();
	}
}
