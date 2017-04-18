/**
 * 
 */
import java.io.FileNotFoundException;
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;

/**
 * @author micah
 *
 */


public class RobotClass {
	
	 static EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.B);

	 static EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.C);
	 static Wheel wheel1 = WheeledChassis.modelWheel(right, 43.5).offset(-80).invert(false);
	 static Wheel wheel2 = WheeledChassis.modelWheel(left, 43.5).offset(80).invert(false);	
	 static Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
	 static MovePilot pilot = new MovePilot(chassis);
	 
	 static EV3TouchSensor touch = new EV3TouchSensor(SensorPort.S4);
	 static SensorMode didIHit = touch.getTouchMode();
	 static float[] touchSample = new float[didIHit.sampleSize()];
	 
	 //static NXTColorSensor colorSensorFront = new NXTColorSensor(SensorPort.S3);
	 //static ColorSensor frontColor = new ColorSensor(SensorPort.S3);
	 static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	 
	 static SampleProvider color = colorSensor.getColorIDMode();;
	 static float[] colorSample = new float[colorSensor.sampleSize()];
	
	/**
	 * @param args
	 */
	public static void main(String[] args)  throws FileNotFoundException{
		
		pilot.setLinearSpeed(200);
		pilot.setAngularSpeed(100);
		
		String colorName = null;
		int colorId = 0;
		
		boolean turning = false;
		int rightTurnPos = 150;
		//int switchTurn = -1;
		
		System.out.println("Press to Start");
		Button.waitForAnyPress();
		while(true){
			
			color.fetchSample(colorSample, 0);
			colorId = (int)colorSample[0];
			colorName = colorCode(colorId);
			System.out.println(colorName);
			didIHit.fetchSample(touchSample, 0);
			if(colorName == "GREEN"){
				break;
			}
			if(touchSample[0] == 1){
				if(pilot.isMoving())
				pilot.stop();
				pilot.travel(-100);
				pilot.travelArc(100, 500);
			}
			if(colorName == "BLACK"){
				turning = false;
				if(!pilot.isMoving())
				pilot.forward();
			}
			
			if(colorName != "BLACK"){
					if(turning == false){
						pilot.stop();
						rightTurnPos *= -1;
						pilot.rotate(35);
						
						turning = true;
					}
					else
						pilot.rotate(-5);
						
					
					
				
			}
			
		}
		pilot.rotate(400);

	}
	public static void MoveAroundBarth(){
		
	}
	public static String colorCode(int colorIndex){
		String colorName = "";
		switch(colorIndex){
		case Color.NONE: 	colorName = "NONE"; 	break;
		case Color.BLACK: 	colorName = "BLACK"; 	break;
		case Color.BLUE: 	colorName = "BLUE"; 	break;
		case Color.GREEN: 	colorName = "GREEN"; 	break;
		case Color.YELLOW: 	colorName = "YELLOW"; 	break;
		case Color.RED: 	colorName = "RED"; 		break;
		case Color.WHITE: 	colorName = "WHITE"; 	break;
		case Color.BROWN: 	colorName = "BROWN"; 	break;
	}
		return colorName;
	}

}