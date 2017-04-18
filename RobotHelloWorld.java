/**
 * 
 */
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.MovePilot;
/**
 * @author micah
 *
 */
public class RobotHelloWorld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("hi, micah");
		
		Button.waitForAnyPress();
		// TODO Auto-generated method stub
		EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.C);

		EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.B);
		
		EV3LargeRegulatedMotor arm = new EV3LargeRegulatedMotor(MotorPort.D);

		Wheel wheel1 = WheeledChassis.modelWheel(right, 43.5).offset(-80).invert(false);

		Wheel wheel2 = WheeledChassis.modelWheel(left, 43.5).offset(80).invert(false);
		
		//Wheel armSwing = WheeledChassis.modelWheel(arm, 43.5).offset(-80).invert(false);

		Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		//Chassis chassis2 = new WheeledChassis(new Wheel { wheel1 }, WheeledChassis.TYPE_DIFFERENTIAL);
		MovePilot pilot = new MovePilot(chassis);		
		
		
		arm.rotate(100);
		arm.rotate(-100);
		
		pilot.travel(220);
		arm.rotate(100);
		arm.rotate(-100);

		//armSwing.getMotor().rotate(100);
		
		//pilot.rotate(360);
		//wheel1.getMotor().rotate(100);
		//wheel1.getMotor().rotate(-100);
		//MovePilot fork = new MovePilot(Wheel1);
		//pilot.travel(50);
		//pilot.rotate(90);
		//pilot.rotate(-90);
		//pilot.travel(-500);
		//pilot.stop();
//		Motor.D.forward();
//		Motor.B.forward();
//		
//		try {
//		Thread.sleep(1000);
//		}
//		catch (Exception ex) {
//			
//		}
//		
//		Motor.D.stop();
//		Motor.B.stop();
//		
		Button.waitForAnyPress();
	}

}
