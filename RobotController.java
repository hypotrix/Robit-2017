//import java.awt.List;
//import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
//import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


//import RobotTestingMulti.MyListener;

import lejos.hardware.Button;
//import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.lcd.LCD;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

import lejos.hardware.port.MotorPort;
//import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
//import lejos.hardware.sensor.HiTechnicCompass;
//import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.SensorMode;
//import lejos.hardware.sensor.SensorModes;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;

import lejos.robotics.chassis.Wheel;

import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
//import lejos.robotics.geometry.Line2D;
//import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.MoveListener;

/**
 * 
 * @author Micah Nelson
 *
 */
public class RobotController {
	MyListener myListener = new MyListener();
	
	public class MyListener implements MoveListener   
	{
		public Move ourMove;
		
		public Move getMovement(){
			return ourMove;
		}
			
		

		@Override
		public void moveStarted(Move event, MoveProvider mp) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void moveStopped(Move event, MoveProvider mp) {
			// TODO Auto-generated method stub
			ourMove = event;
			
		}
		
	}
	String location = "directions.txt";
	File logFile = new File(location);
	
	//double[] booperDistance = {41.38, 41.38, 55.17,82.76,91.03,100.00,353.10,303.44};
	
	//double holeLocationX = 0;
	//double holeLocationY = 0;
	public DecimalFormat numberFormat = new DecimalFormat("#.00");
	public boolean iJustMovedNowLost = false;
	//motor units per 90 degrees
	static final double DEGREESTOMOTOR = 106.5; //Current Best, wisconsin.
	public int probablyMoved = 0;
	public boolean firstHit = false;
	static final double lineRunnerSpeed = -50;
	static final String lineColor = "RED";
	static final double lineFinderLostRotate = 5;
	static final double lineFinderRotateMaxAngle = 80;
	static boolean golfing = false;
	public double locationXCamera = 0;
	public double locationYCamera = 0;
	public double radiusOfSensorFromMiddle = -82.75; //In motor units.
	static ArrayList<Double> directionsX = new ArrayList<Double>();
	static ArrayList<Double> directionsY = new ArrayList<Double>();
	public boolean firstBoop = true;
	static double switchPointAtThisDistance = 300;
	//static final double chassisDividebyTimeNumber = 10;
	//static final double travelToThenBoopDistance = 15;
	final double ROBOTTOBALLDISTANCE = 150.67;// 6 inches from ball, 100 motor units = 3.75 inches //modified
	static double currentXLocation = 0;
	static double currentYLocation = 0;
	static double keepDegreesFromStart = 0;
	static double nextTravelPointX = 0;
	static double nextTravelPointY = 0;
	//double minusTheStartDirection = 0;
	//double finalXLocation = 1000;
	//double finalYLocation = 0;
	boolean ballUnknown = true;
	boolean justBooped = false;
	
	static double directionOfBot = 0;

	public static enum Phase {

		CENTER, APPROACH, BOOP, TEST

	}
	public static enum LinePhase {

		START, DRIVING, LOST, ARRIVED, LineChangeDirections, SKIP

	}
	static final double findBallRotation = 35;

	static final double CENTER = 140;

	static final double LEFT_DEADZONE = 15;

	static final double RIGHT_DEADZONE = 20;
	
	static final double DEADZONE = 20;
	

	//started .289, changed to .189, changed to .139
	static final double ANGLE_FACTOR = 0.139;
	

	
	EV3ColorSensor rightColorSensor = null;
	SampleProvider rightColor = null;
	float[] sampleRightColor = null;
	
	 EV3TouchSensor touch = new EV3TouchSensor(SensorPort.S4);
	 SensorMode reloader = touch.getTouchMode();
	 float[] sample = new float[touch.sampleSize()];
	 
	 //HiTechnicCompass compass = new HiTechnicCompass(SensorPort.S3);
	 //SampleProvider angle = compass.getMode("Compass");
	 //float[] compassSample = new float[angle.sampleSize()];
	 EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S3);
	 SampleProvider angle = gyro.getAngleMode();
	 float[] angleSample = new float[angle.sampleSize()];
	
	static EV3MediumRegulatedMotor booper = new EV3MediumRegulatedMotor(MotorPort.D);

	static EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.B);

	static EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.C);

	static Wheel wheel1 = WheeledChassis.modelWheel(right, 43.5).offset(-80).invert(false);
	
	

	static Wheel wheel2 = WheeledChassis.modelWheel(left, 43.5).offset(80).invert(false);
	
	
	static Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
	
	

	
	static MovePilot pilot = new MovePilot(chassis);
	
	static LegoPixy pixy = new LegoPixy(SensorPort.S1);
	
	static PixyRectangle rec = null;
	
	static Phase p = Phase.CENTER;
	
	static LinePhase lPhase = LinePhase.START;
	
	
	
	public RobotController() throws FileNotFoundException{
		
		pilot.addMoveListener(myListener);
		golfing = true;
		booper.setSpeed(400);
		pilot.setLinearSpeed(300);
		//setXLocation(100);
		getDirectionsArray();
		//armReload(false);
		
		gyro.reset();
		angle.fetchSample(angleSample, 0);
		System.out.println(angleSample[0]);
		//Start up complete. Ask to continue.
		//System.out.println("testing:");
		//pilot.setAngularSpeed(20);
		//compass.startCalibration();
		//pilot.rotate(DEGREESTOMOTOR * 8);
		
		//compass.stopCalibration();
		//try{Thread.sleep(5000);}catch(InterruptedException e){}
		//angle.fetchSample(compassSample, 0);
		//System.out.println(compassSample[0]);
		//minusTheStartDirection -= compassSample[0];
		//setDirectionOfBot(compassSample[0]);
		//pilot.setAngularSpeed(100);
		
		//Notes: rotate 90 + 13 = 90 degree rotation
		// 103 = 90 degrees
		//
		// (moved)    X
		//  ----  = -----
		//   103      90
		
	
		
	}
	public RobotController(boolean LineRunner) {
		
		golfing = false;
		pilot.setLinearSpeed(200);
		
		setXLocation(radiusOfSensorFromMiddle);
		//setXLocation(-12.5);
	}

	public static double getDirectionOfBot(){
		return directionOfBot;
	}
	private static void setDirectionOfBot(double newDirection){
		
		if(newDirection < 0)
			newDirection += 360;
		if(newDirection >= 360)
			newDirection -= 360;
		directionOfBot = newDirection;
		
	}
	
	/**
	 * set it the number used for the rotation of motor. It will
	 * calculate the number in degrees and change the robots internal
	 * compass.
	 * @param rotateNumber
	 */
	public void changeDirectionOfBotWithRoatator(double rotateNumber){
		double temp = getDirectionOfBot();
		rotateNumber *= -1;
		//Changed the rotation number into degrees
		temp = temp + ((rotateNumber / DEGREESTOMOTOR) * 90);
		while(true){
			if(temp >= 360.0){
				temp -= 360;
			}
			else if(temp <= -360.0){
				temp += 360;
			}
			else
				break;
		}
		if(temp < 0)
			temp = 360 + temp;
		
		//used to be temp value
		//angle.fetchSample(compassSample, 0);
		//System.out.println(compassSample[0]);
		
		angle.fetchSample(angleSample, 0);
		gyro.reset();
		setDirectionOfBot(getDirectionOfBot() + angleSample[0]);
		System.out.println(getDirectionOfBot());
		//System.out.println("Degree: " + numberFormat.format(temp));
		
	}
	public  static double changeDegreesToMotorTurn(double degreeChange){
	
		double temp = degreeChange;
		
		while(temp > 360 || temp < -360)
		{
			if(temp > 360)
				temp -= 360;
			else
				temp += 360;
		}
		double tempMotorTurn = 0;
		
		temp = getDirectionOfBot() - temp;
		
		//Changed the rotation number into degrees
		tempMotorTurn = ((temp / 90) * DEGREESTOMOTOR);
		

		//System.out.println("Mortor moved by degrees: " + numberFormat.format(tempMotorTurn));
		//try{Thread.sleep(2000);}catch(InterruptedException e){}
		return tempMotorTurn;
		
	}
	
	public void moveMeTo(double x, double y){
		
		double tempX = getXLocation();
		double tempY = getYLocation();
		
		double distance = 0;
		//this will move the bot to the right direction
		findAngleTo(x,y);
		
		
		distance = Math.sqrt(Math.pow((y- tempY),2) + Math.pow((x - tempX), 2));
		moveBotTo(distance);
		
	}
	public void moveBotTo(double distance){
		pilot.stop();
		pilot.travel(distance);
		pilot.stop();
		iHaveMoved(distance);
	}
	
	public static double getXLocation(){
		return currentXLocation;
	}
	public static double getYLocation(){
		return currentYLocation;
	}
	public static void setXLocation(double newXLocation){
		currentXLocation = newXLocation;
	}
	public static void setYLocation(double newYLocation){
		currentYLocation = newYLocation;
	}
	public static void iHaveMoved(double travelNumber){
		double tempX = getXLocation();
		double tempY = getYLocation();
		double tempAngle = getDirectionOfBot();
		
		 tempY += (Math.sin(Math.toRadians(tempAngle)) * travelNumber);
		 tempX += (Math.cos(Math.toRadians(tempAngle)) * travelNumber);
		 
		setXLocation(tempX);
		setYLocation(tempY);
		if (golfing)
		setNextPointToTravelTo();
		//System.out.println("Distance: " + numberFormat.format(travelNumber));
		//System.out.println("X: " + numberFormat.format(getXLocation()));
		//System.out.println("Y: " + numberFormat.format(getYLocation()));
		//try{Thread.sleep(3000);}catch(InterruptedException e){}
	}
	public void findAngleTo(double x, double y){
		double tempX = getXLocation();
		double tempY = getYLocation();
		
		double angleToTurnTo = 0;
		double temp = 0;
		
		if((x - tempX) == 0.0){
			if(y-tempY > 0)
				angleToTurnTo = 90;
			else
				angleToTurnTo = 270;
		}
		else if((y-tempY) == 0)
		{
			if(x-tempX > 0)
				angleToTurnTo = 0;
			else
				angleToTurnTo = 180;
		}
		else
		{
			
			angleToTurnTo = Math.toDegrees(Math.atan((y-tempY)/(x-tempX)));
			if((x-tempX) > 0){
				if((y - tempY) < 0)
					angleToTurnTo = 360 + angleToTurnTo;
			}
			else if((x-tempX) < 0)
					angleToTurnTo = 180 + angleToTurnTo;
				
		
			
		}	
			temp = changeDegreesToMotorTurn(angleToTurnTo);
			turnMe(temp);
			
		
	}

	public void turnMe(double rotationNumber){
		
		double fullCircle = DEGREESTOMOTOR * 4;
		double posHalfWayPoint = 2 * DEGREESTOMOTOR;
		double negHalfWayPoint = -2 * DEGREESTOMOTOR;
		
		double rotateMe = rotationNumber;
		
		while(true){
		if(rotateMe >= fullCircle){rotateMe -= fullCircle;}
		else if(rotateMe <= (fullCircle * -1)){rotateMe += fullCircle;}
		else
			break;
		}
		
		if(rotateMe <= negHalfWayPoint)
			rotateMe += fullCircle;
		
		if(rotationNumber >= posHalfWayPoint)
			rotateMe -= fullCircle;
		
		keepDegreesFromStart += rotateMe;
		//System.out.println("RotatMe: " + rotateMe);
		//try{Thread.sleep(5000);}catch(InterruptedException e){}
		
		pilot.stop();
		pilot.rotate(rotateMe);
		pilot.stop();
		changeDirectionOfBotWithRoatator(rotateMe);
	}

	public void centerBall(){
		
		double angle = 0;
		double ballRotation = findBallRotation;
		//probablyMoved = 0;
		while(true){
			
			//for(int i = 0; i > 10; i++){
			try {
				rec = pixy.getBiggestBlob();
				
				//break;
					} catch (Exception ex) {System.out.println("Camera cannot access pixy class.");}

			//}

		if (rec != null) {
			
///////////////////////////////////////////CENTER///////////////////////////////////////////////////
			if (p == Phase.CENTER) {

				if (rec.getCenterX() == 0) {
					
					
					//turn the robot to find ball
					
					angle +=ballRotation;
					if(angle >= 120 || angle <= -120){
						turnMe(-angle);
						angle = 0;
						ballRotation *= -1;
						probablyMoved++;
					}
					
					if(justBooped && probablyMoved >= 2){
						//try{Thread.sleep(2000);}catch(InterruptedException e){}
						probablyMoved = 0;
						moveBotTo(250);
						justBooped = false;
					}
					else if(probablyMoved >= 2){
						
						probablyMoved = 0;
						moveBotTo(-100);
						iJustMovedNowLost = false;
						justBooped = true;
						
					}
					
					turnMe(ballRotation);
					
				}  
				else if (rec.getCenterX() < CENTER - LEFT_DEADZONE || rec.getCenterX() > CENTER + RIGHT_DEADZONE) {
					// Turn towards the ball
					double turn = (rec.getCenterX() - CENTER) * ANGLE_FACTOR;
					//turn *= -1;
					turnMe(turn);
					justBooped = false;
					angle = 0;
				} 
				else {
					p = Phase.APPROACH;
					justBooped = false;
					
					break;
				}//End of else

			} //End of Enter If
		}//End on if null
		
		}//End of while
		probablyMoved = 0;
	}//End of class
	public void approachBall(){
		
		//System.out.println("X: " + numberFormat.format(getXLocation()));
		//System.out.println("Y: " + numberFormat.format(getYLocation()));
		//try{Thread.sleep(5000);}catch(InterruptedException e){}

		//long startTime = 0;
		//long endTime = 0;
		//double difference = 0;
		boolean iMoved = false;
			while(true){
				try {
					rec = pixy.getBiggestBlob();
					
					//break;
						} catch (Exception ex) {System.out.println("Camera cannot access pixy class.");}
				
				if(rec.getCenterX() == 0){
					if(pilot.isMoving()){
						break;
					}
					p = Phase.CENTER;
					break;
				}
				if ((rec.getCenterX()) < CENTER - DEADZONE || rec.getCenterX() > CENTER + DEADZONE) {
					//Need to realign call
					iJustMovedNowLost = false;
					p = Phase.CENTER;
					break;	
				}
				
				if(rec.getCenterY() > 165) {
					pilot.stop();
					//Next to ball.
					//System.out.println("rec = " + rec.getCenterY());
					//try{Thread.sleep(5000);}catch(InterruptedException e){}
					iJustMovedNowLost = false;
					p = Phase.BOOP;
					//chassis.stop();
					
					
					//endTime = System.currentTimeMillis();
					break;
				}
				
				if (p == Phase.APPROACH){
					//Start moving forward. light sleep, add the movement to location
					
					if(!iMoved){
					pilot.forward();
					iJustMovedNowLost = true;
					iMoved = true;
					}
					try{Thread.sleep(50);}catch(InterruptedException e){}
					
					
					
				}
			}//End of while
			
			
			//difference = (endTime - startTime);
			//difference /= 1000.0;
			//System.out.println("Time :" + difference);
			if(pilot.isMoving())
			pilot.stop();
			//System.out.println(myListener.getMovement().getDistanceTraveled());
			if(iMoved){
			System.out.println(myListener.getMovement().getDistanceTraveled());
			iHaveMoved(myListener.getMovement().getDistanceTraveled());
			
			}
			
	}//End of class
	/**
	 * Hits the ball then calls armReload()
	 */
	public void boopBall(){
		
		p = Phase.BOOP;
		if (p == Phase.BOOP) {
			
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			
			try {
				rec = pixy.getBiggestBlob();
				
				//break;
					} catch (Exception ex) {System.out.println("Camera cannot access pixy class.");}
			
			if(rec.getCenterY() == 0){
				moveBotTo(15);
			}
			else if(rec.getCenterY() < 165)
				moveBotTo(75);
			else if(rec.getCenterY() < 175)
				moveBotTo(55);
			else if(rec.getCenterY() < 188)
				moveBotTo(50);
			else if(rec.getCenterY() < 200)
				moveBotTo(40);
			else if(rec.getCenterY() < 200)
				moveBotTo(15);
			
				
			
			MoveToHitBall();
			
			
			
			
		}
		
		if (p == Phase.BOOP) {
			
			//iHaveMoved(travelToThenBoopDistance);
			//Rotate crank
			
			firstHit = true;
			booper.rotate(300);
			//Display i have hit it
			//System.out.println("Booped");
			justBooped = true;
			probablyMoved = 2;
			//Wait to reload
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			
			//Call reload
			armReload(false);
				
			//Set it ready to restart	
			p = Phase.CENTER;
		} 
	}
	
	/** 
	 * Reloads the arm to get ready for next swing
	 */
	public void armReload(boolean reverse){	
		int i = 50;
		while(true){
			reloader.fetchSample(sample, 0);
			if(sample[0] == 1)
			{
				System.out.println("Touch");
				booper.suspendRegulation();
				break;
			}
			else
			{
				if(reverse)
					booper.rotate(-15);
				else
					booper.rotate(15);
				i--;
			}
			if( i < 0){
				System.out.println("Unable to Reload");
				break;
			}
		}
	}
	public void closeMotors(){

		pixy.close();
		touch.close();
		gyro.close();
		left.close();

		right.close();
		
		booper.close();
		
	}
	public boolean ifPhaseCenter(){
		if(p == Phase.CENTER)
			return true;
		return false;
	}
	public boolean ifPhaseApproach(){
		if(p == Phase.APPROACH)
			return true;
		return false;
	}
	public boolean ifPhaseBoop(){
		if(p == Phase.BOOP)
			return true;
		return false;
	}
	public String colorCode(int colorIndex){
		String colorName = "";
		switch(colorIndex){
		case Color.NONE: colorName = "NONE"; break;
		case Color.BLACK: colorName = "BLACK"; break;
		case Color.BLUE: colorName = "BLUE"; break;
		case Color.GREEN: colorName = "GREEN"; break;
		case Color.YELLOW: colorName = "YELLOW"; break;
		case Color.RED: colorName = "RED"; break;
		case Color.WHITE: colorName = "WHITE"; break;
		case Color.BROWN: colorName = "BROWN"; break;
	}
		return colorName;
	}
	public void lineRunnerXYFixer(){
		
		double tempX = 0;
		double tempY = 0;
		
		tempX = Math.cos(Math.toRadians(getDirectionOfBot())) * radiusOfSensorFromMiddle;
		tempY = Math.sin(Math.toRadians(getDirectionOfBot())) * radiusOfSensorFromMiddle;
		
		locationXCamera = getXLocation() + tempX;
		locationYCamera = getYLocation() + tempY;
	}
	public void lineRunner(){
		
		try{
		rightColorSensor = new EV3ColorSensor(SensorPort.S2);
		rightColor = rightColorSensor.getColorIDMode();
		sampleRightColor = new float[rightColor.sampleSize()];
		System.out.println("LineRunner Ready: Press a button!");
		}catch(Exception e){
			System.out.println("Not loaded, Restart");
		}
		
		Button.waitForAnyPress();
        gyro.reset();
        angle.fetchSample(angleSample, 0);
		String colorName = "";
		int colorId = 0;
		int findEnd = 0;
		
		pilot.setLinearAcceleration(100);
		
		pilot.setAngularSpeed(40);
		//double tempX = currentXLocation;
		//double tempY = currentYLocation;
		
		
		//int countToRecord = 5;
		//int counter = 0;
		ArrayList<Double> listX = new ArrayList<Double>();
		listX.add(0.0);
		ArrayList<Double> listY = new ArrayList<Double>();
		listY.add(0.0);
		//boolean notInListYet = true;
		
		double tempAngle = 0;
		
		
		boolean leftTurnFirst = true;
            
		if(lPhase == LinePhase.START){
			setDirectionOfBot(180);
		
		while(true){
			
			rightColor.fetchSample(sampleRightColor, 0);
			colorId = (int)sampleRightColor[0];
			colorName = colorCode(colorId);
			
			
			if(colorName.equals(lineColor)){
				lPhase = LinePhase.DRIVING;
				findEnd = 0;
				lineRunnerXYFixer();
				listX.add(locationXCamera);
				listY.add(locationYCamera);
				System.out.println("Added to list:");
				System.out.println("X: " + locationXCamera);
				System.out.println("Y: " + locationYCamera);
			}
			else{
				if(pilot.isMoving())
				{
					if(colorName.equals("BLACK")){
						System.out.println("Is this the Hole?");
						try{Thread.sleep(10000);}catch(InterruptedException e){}
					}
					pilot.stop();
				}
				lPhase = LinePhase.LOST;
				//if (pilot.isMoving()){
				
				//pilot.stop();
				//iHaveMoved(-pilot.getMovement().getDistanceTraveled());
				//}
			}
			if(lPhase == LinePhase.DRIVING){
				tempAngle = 0;
				moveBotTo(-20);
				//if(!pilot.isMoving())
				//pilot.backward();
			}
			else if(lPhase == LinePhase.LOST){
			
				if(tempAngle > lineFinderRotateMaxAngle || tempAngle < (lineFinderRotateMaxAngle * -1))
				{
					
					turnMe(changeDegreesToMotorTurn(getDirectionOfBot() + (tempAngle * -1)));
					tempAngle = 0;
					if(leftTurnFirst)
						leftTurnFirst = false;
					else
						leftTurnFirst = true;
					
						findEnd++;
				}
				if(leftTurnFirst){
					tempAngle -= lineFinderLostRotate;
					turnMe(changeDegreesToMotorTurn(getDirectionOfBot() - lineFinderLostRotate));
				
				} 
				else{
					tempAngle += lineFinderLostRotate;
					turnMe(changeDegreesToMotorTurn(getDirectionOfBot() + lineFinderLostRotate));
				}
				if(findEnd >= 2){
					System.out.println("Breaking, DONE");
					
					if(listX.size() > 1)
					writeToFile(listX,listY);
					break;
				}
			
			}
		}//End of While
	}//End of if LinePhase.START
	

	}
	public void writeToFile(ArrayList<Double> listX, ArrayList<Double> listY){
		
		//String location = "directions.txt";
		//File file = null;
		
		BufferedWriter writer = null;
		
		try {
			
            writer = new BufferedWriter(new FileWriter(logFile));
            //writer.
            //writer.write("54 ");
            //writer.write("542 ");
        }catch(Exception e){e.printStackTrace();} 
		
		boolean readyToPrint = true;
		
		while(!listX.isEmpty()){
			
			///Start
				if(Math.abs(listY.get(1)) >= Math.abs(listY.get(0))){
					readyToPrint = true;
					listX.remove(0);
					listY.remove(0);
				}
				else {
					 
					try {
						if(readyToPrint == true){
							System.out.println("X: " + listX.get(0).intValue());
							System.out.println("Y: " + listY.get(0).intValue());
							try{Thread.sleep(1000);}catch(InterruptedException e){}
							writer.write(listX.get(0).toString() + " ");
							writer.write(listY.get(0).toString() + " ");
							readyToPrint = false;
						}
						
						
						listX.remove(0);
						listY.remove(0);
						
					}catch(Exception e){e.printStackTrace();} 
				}
				try {
					if(listX.size() == 1){
					
					System.out.println("Last Ones: \nX: " + listX.get(0).intValue());
					System.out.println("Y: " + listY.get(0).intValue());
					try{Thread.sleep(5000);}catch(InterruptedException e){}
				writer.write(listX.get(0).toString() + " ");
				writer.write(listY.get(0).toString() + " ");
				break;
					}}catch(Exception e){e.printStackTrace();} 
			

		
		
		
		//End
		}
		//Close Writer File
				try {writer.close();} catch (Exception e) {}
		
	}
	public void getDirectionsArray() throws FileNotFoundException{
		//String location = "directions.txt";
		//File logFile = new File(location);
		Scanner scnr = null;
		System.out.println("tes");
		directionsX.clear();
		directionsY.clear();
		//double addToFirst
		
		double tempx = 0;
		double tempy = 0;
        try{
        	scnr = new Scanner(logFile);
        	while(true){
        		int x = 30;
        		int y = 0;
        		if(scnr.hasNextDouble()){
        			tempx = scnr.nextDouble();
        			tempy = scnr.nextDouble();
        			
        			if(tempy < 0)
        			y *= -1;
        			
        			directionsX.add((tempx) * 4);
        			directionsY.add((tempy) * 4);
        		
        		}
        		else
        			break;
        	}
    
    
        }catch (InputMismatchException e)
        {
            System.out.println("File not found1.");

        }
	}
	public double getClosestPointOnLineX(){
		return nextTravelPointX;
	}
	public double getClosestPointOnLineY(){
		return nextTravelPointY;
	}
	public static void setNextPointToTravelTo(){
		double distance = 0;
		boolean foundPoint = false;
		
		for (int i = 0; i < directionsX.size() - 1; i++){
			
			if(directionsX.get(i) > getXLocation()){
				distance = Math.sqrt(Math.pow((directionsY.get(i)- getYLocation()),2) + Math.pow((directionsX.get(i) - getXLocation()), 2));
				if(distance > switchPointAtThisDistance){
					nextTravelPointX = directionsX.get(i);
					nextTravelPointY = directionsY.get(i);
					foundPoint = true;
					break;
				}
			}
			
			
		}
		if(!foundPoint){
		nextTravelPointX = directionsX.get(directionsX.size() - 1);
		nextTravelPointY = directionsY.get(directionsY.size() - 1);
		}
		
	}
	/**
	 * calculates where to move the bot to hit the ball in the right direction
	 * 
	 */
	
	public void MoveToHitBall(){
		//Known
		
		
		//try{Thread.sleep(500);}catch(InterruptedException e){}
		//double temp = changeDirectionOfBotWithDegrees(180);
		//turnMe(temp);
		//moveBotTo(150);
		double robotToBallDistance = ROBOTTOBALLDISTANCE;
		double angToClosestPointFromBall = 0;
		double newRobotLocationX = 0;
		double newRobotLocationY = 0;
		double temp = 0;
		//Get the balls x y position
		double ballLocationX = getXLocation() + (Math.cos(Math.toRadians(getDirectionOfBot())) * robotToBallDistance);
		double ballLocationY = getYLocation() + (Math.sin(Math.toRadians(getDirectionOfBot())) * robotToBallDistance);
		//Get the angle from ball to hole
		//fix the hole location part
		angToClosestPointFromBall = findAngleToTwoPoints(ballLocationX,ballLocationY, getClosestPointOnLineX(),getClosestPointOnLineY());
		//angleToRobotFromBall += 180;
		newRobotLocationX = (ballLocationX + ((Math.cos(Math.toRadians(angToClosestPointFromBall)) * (robotToBallDistance + 50)) * -1));
		newRobotLocationY = (ballLocationY + ((Math.sin(Math.toRadians(angToClosestPointFromBall)) * (robotToBallDistance + 50)) * -1));
		
		
		double angRobToBall = findAngleToTwoPoints(getXLocation(),getYLocation(),ballLocationX,ballLocationY);
		double angRobToHole = findAngleToTwoPoints(getXLocation(),getYLocation(),getClosestPointOnLineX(),getClosestPointOnLineY());
		
		double distance = Math.sqrt(Math.pow((getClosestPointOnLineY()- getYLocation()),2) + Math.pow((getClosestPointOnLineX() - getXLocation()), 2));
		if(distance <= 600.00){
			booper.setSpeed(300);
		}
		else
			booper.setSpeed(400);
		
		double temp2 = Math.abs(angRobToHole - angRobToBall);
		if( temp2 >= 50 && temp2 <= 310){
			//System.out.println(angRobToHole + " rob2ho");
			//System.out.println(angRobToBall + " rob2ba");
			//try{Thread.sleep(5000);}catch(InterruptedException e){}

			
			
			if(angToClosestPointFromBall <= 180){
				moveBotTo(-40);
				pilot.rotate(40);
				pilot.stop();
				booper.rotate(80);
				moveBotTo(80);
				pilot.rotate(-70);
				pilot.stop();
				try{Thread.sleep(500);}catch(InterruptedException e){}
				moveBotTo(60);
				pilot.rotate(-40);
				pilot.stop();
			}
			else{
				moveBotTo(-40);
				pilot.rotate(-40);
				pilot.stop();
				booper.rotate(80);
				moveBotTo(80);
				pilot.rotate(70);
				pilot.stop();
				try{Thread.sleep(500);}catch(InterruptedException e){}	
				moveBotTo(60);
				pilot.rotate(40);
				pilot.stop();
			}
			armReload(true);
			p = Phase.CENTER;
			angRobToBall = findAngleToTwoPoints(getXLocation(),getYLocation(),ballLocationX,ballLocationY);
			temp = changeDegreesToMotorTurn(angRobToBall);
			turnMe(temp);
		}
		else if(Math.abs(angRobToHole - angRobToBall) >= 10 && Math.abs(angRobToHole - angRobToBall) <= 350){
			moveBotTo(-50);
			moveMeTo(newRobotLocationX, newRobotLocationY);
			
		//moveMeBehindBall(ballLocationX, ballLocationY, newRobotLocationX, newRobotLocationY);
			p = Phase.CENTER;
			temp = changeDegreesToMotorTurn(angToClosestPointFromBall);
			turnMe(temp);
			moveBotTo(-40);
		}
		
		//moveMeBehindBall(ballLocationX, ballLocationY, newRobotLocationX, newRobotLocationY);
		//moveMeTo(newRobotLocationX, newRobotLocationY);
		//moveMeTo(currentXLocation, newRobotLocationY);
		
		
		 //double temp = changeDegreesToMotorTurn(angleToRobotFromBall);
		//turnMe(temp);
		
		
	}
	public void moveMeBehindBall(double ballLocationX, double ballLocationY, double newRobotLocationX, double newRobotLocationY){
		
		//double angRobToBall = findAngleToTwoPoints(getXLocation(),getYLocation(),ballLocationX,ballLocationY);
		//double angRobToNewRob = findAngleToTwoPoints(getXLocation(),getYLocation(),newRobotLocationX,newRobotLocationY);;
		//double angBallToPoint = findAngleToTwoPoints(ballLocationX,ballLocationY, getClosestPointOnLineX(), getClosestPointOnLineY());
		
		
		
		//moveBotTo(-60);
		moveMeTo(newRobotLocationX, newRobotLocationY);
		/*
		if(Math.abs(angRobToNewRob - angRobToBall) < 40){
			if(angRobToNewRob >= 30 && angRobToNewRob <= 90){
				moveMeTo(ballLocationX,ballLocationY - 200);
				moveMeTo(ballLocationX + 200,ballLocationY);
			 
				
			}
			else if(angRobToNewRob >= 90 && angRobToNewRob <= 180){
				moveMeTo(ballLocationX,ballLocationY - 200);
				moveMeTo(ballLocationX - 200,ballLocationY);
				
			}
			else if(angRobToNewRob >= 180 && angRobToNewRob <= 270){
				moveMeTo(ballLocationX,ballLocationY + 200);
				moveMeTo(ballLocationX - 200,ballLocationY);
				 
			}
			else if(angRobToNewRob >= 270 && angRobToNewRob <= 360){
				moveMeTo(ballLocationX,ballLocationY + 200);
				moveMeTo(ballLocationX + 200,ballLocationY);
				
			}	
		}
		moveMeTo(newRobotLocationX,newRobotLocationY);
		*/
	}
	public double findAngleToTwoPoints(double x, double y, double x2, double y2){

		double angleToTurnTo = 0;
		
		
		if((x2 - x) == 0.0){
			if(y2-y > 0)
				angleToTurnTo = 90;
			else
				angleToTurnTo = 270;
		}
		else if((y2-y) == 0)
		{
			if(x2-x > 0)
				angleToTurnTo = 0;
			else
				angleToTurnTo = 180;
		}
		else
		{
			
			angleToTurnTo = Math.toDegrees(Math.atan((y2-y)/(x2-x)));

			if((x2-x) > 0){
				if((y2 - y) < 0)
					angleToTurnTo = 360 + angleToTurnTo;
			}
			else if((x2-x) < 0)
					angleToTurnTo = 180 + angleToTurnTo;
		}	
		
		
		return angleToTurnTo;
		
	}
	
	
	
	
}
