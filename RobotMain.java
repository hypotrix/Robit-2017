import java.text.DecimalFormat;

import lejos.hardware.Button;

import lejos.hardware.lcd.LCD;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

import lejos.hardware.port.MotorPort;

import lejos.hardware.port.SensorPort;

import lejos.robotics.chassis.Chassis;

import lejos.robotics.chassis.Wheel;

import lejos.robotics.chassis.WheeledChassis;

import lejos.robotics.navigation.MovePilot;



public class RobotMain {
	
	static DecimalFormat numberFormat = new DecimalFormat("#.00");
	
	static double currentXLocation = 0;
	static double currentYLocation = 0;
	
	double finalXLocation = 1000;
	double finalYLocation = 0;
	
	static double directionOfBot = 0;

	public enum Phase {

		CENTER, APPROACH, BOOP, TEST

	}
	static final double findBallRotation = 55;

	static final double CENTER = 140;

	static final double LEFT_DEADZONE = 20;

	static final double RIGHT_DEADZONE = 20;

	static final double ANGLE_FACTOR = 0.289;

	static EV3MediumRegulatedMotor booper = new EV3MediumRegulatedMotor(MotorPort.D);

	//EV3MediumRegulatedMotor camArm = new EV3MediumRegulatedMotor(MotorPort.C);

	static EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.B);

	static EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.C);

	static Wheel wheel1 = WheeledChassis.modelWheel(right, 43.5).offset(-80).invert(false);

	static Wheel wheel2 = WheeledChassis.modelWheel(left, 43.5).offset(80).invert(false);

	static Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);

	static MovePilot pilot = new MovePilot(chassis);

	static LegoPixy pixy = new LegoPixy(SensorPort.S4);

	public static double getDirectionOfBot(){
		return directionOfBot;
	}
	private static void setDirectionOfBot(double newDirection){
		directionOfBot = newDirection;
		
	}
	
	/**
	 * set it the number used for the rotation of motor. It will
	 * calculate the number in degrees and change the robots internal
	 * compass.
	 * @param rotateNumber
	 */
	public  static void changeDirectionOfBotWithRoatator(double rotateNumber){
		double temp = getDirectionOfBot();
		rotateNumber *= -1;
		//Changed the rotation number into degrees
		temp = temp + ((rotateNumber / 103) * 90);
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
		setDirectionOfBot(temp);
		System.out.println("Degree: " + numberFormat.format(temp));
		
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
		
		System.out.println("Distance: " + numberFormat.format(travelNumber));
		System.out.println("X: " + numberFormat.format(getXLocation()));
		System.out.println("Y: " + numberFormat.format(getYLocation()));
	}
	public static void findAngle(double x, double y){
		double tempX = getXLocation();
		double tempY = getXLocation();
		
		tempY -= y;
		tempX -= x;
		
		if(x == 0){
			//setDirectionOfBot(0);
		}
	}

	public static void main(String[] args) {
		
		

		booper.setSpeed(booper.getMaxSpeed());

		pilot.setLinearSpeed(pilot.getMaxLinearSpeed());

		Phase p = Phase.CENTER;
		
		//Start up complete. Ask to continue.
		
		//Notes: rotate 90 + 13 = 90 degree rotation
		// 103 = 90 degrees
		//
		// (moved)    X
		//  ----  = -----
		//   103      90
		
		
		System.out.println("Press any button to start.");
		Button.waitForAnyPress();

		while (Button.ENTER.isUp()) {

			PixyRectangle rec = null;

			try {
				//allow multiple tests incase of pixy blink
				for(int i = 0; i < 5; i++)
				rec = pixy.getBiggestBlob();

			} catch (Exception ex) {

				try {

					Thread.sleep(200);

				} catch (InterruptedException e) {

					e.printStackTrace();

				}

			}

//			LCD.clearDisplay();

//			LCD.drawString("X :" + rec.getCenterX(), 0, 1);

//			LCD.drawString("Y :" + rec.getCenterY(), 0, 2);

//			LCD.drawString("W :" + rec.getWidth(), 0, 3);

//			LCD.drawString("H :" + rec.getHeight(), 0, 4);

//			LCD.drawString("PHASE :" + p.toString(), 0, 6);

			// center the ball before moving

			if (rec != null) {
				
///////////////////////////////////////////CENTER///////////////////////////////////////////////////
				if (p == Phase.CENTER) {

					if (rec.getCenterX() == 0) {
						
						//turn the robot to find ball
						pilot.rotate(findBallRotation);
						changeDirectionOfBotWithRoatator(findBallRotation);

					}  
					else if (rec.getCenterX() < CENTER - LEFT_DEADZONE) {

						double turn = (rec.getCenterX() - CENTER) * ANGLE_FACTOR;

						pilot.rotate(turn);
						changeDirectionOfBotWithRoatator(turn);

						//System.out.print("TURN " + move);
						
					} else if (rec.getCenterX() > CENTER + RIGHT_DEADZONE) {

						double turn = (CENTER - rec.getCenterX()) * ANGLE_FACTOR * -1;

						pilot.rotate(turn);
						changeDirectionOfBotWithRoatator(turn);

						//System.out.print("TURN " + move);

					} else {

						p = Phase.APPROACH;

						System.out.println("-APPROACH-");

					}

				} //End of Enter If



				// ball centered? then approach the ball

///////////////////////////////////////////APPROACH///////////////////////////////////////////////////
				if (p == Phase.APPROACH) {

					// determine roughly how far away it is now that we're

					double move = 336.929 - (2.95936 * rec.getCenterY())

							+ 0.00927175 * (rec.getCenterY() * rec.getCenterY());

					// move that distance

					move -= 50;

					move *= 1.1;

					pilot.travel(move);
					iHaveMoved(move);

					//System.out.println("MOVE " + move);

					
				

					// check centered

					if ((rec.getCenterX() - 40) < CENTER - LEFT_DEADZONE || rec.getCenterX() > CENTER + RIGHT_DEADZONE) {

						p = Phase.CENTER;

						System.out.println("-CENTER-");

					}

					// check distance
					
					else if (rec.getCenterY() < 100) {

						p = Phase.APPROACH;

						System.out.println("-APPROACH-");

					}

					// both good, better boop it.

					else {

						p = Phase.BOOP;

						System.out.println("-BOOP-");

					}

				} // End of approach



				// Ball centered AND close? Then the cam is down and we need to

				// boop.

///////////////////////////////////////////BOOP///////////////////////////////////////////////////
				if (p == Phase.BOOP) {

					// move forward slowly, we can either check this or put a

					// procedure here

					/*pilot.setLinearSpeed(150);

					pilot.travel(75*1.1);

					System.out.print("MOVE 75");

					// boop the ball!

					booper.rotate(350);

					pilot.setLinearSpeed(pilot.getMaxLinearSpeed());

					// cam up if it was put down

					// start process over

					p = Phase.CENTER;*/
					
					//pilot.travel(150);
					System.out.println("-CENTER-");
					//booper.rotate(100);
					//booper.rotate(100, false);
					
					//booper.rotate(-100, false);
					//booper.stop();
					//booper.rotate(50, true);
					try {
						booper.rotate(100, false);
						Thread.sleep(1000);
						booper.rotate(-100, false);
						

					} catch (InterruptedException e) {

						e.printStackTrace();

					}
					booper.suspendRegulation();
					//booper.rotate(-100);
					p = Phase.CENTER;
					
					System.out.println("After boop, now continue");
					continue;

				} // End of boop

				if (p == Phase.TEST) {

					pilot.travel(200);

					

				}

			}// End of if (null)
			
			

			
			try{
				Thread.sleep(4000);
			}catch(InterruptedException e){
				
			}
		}// end of while

		pixy.close();

		left.close();

		right.close();
		
		booper.close();



		try {

			Thread.sleep(200);

		} catch (InterruptedException e) {

			e.printStackTrace();

		}

	}
	
	

}