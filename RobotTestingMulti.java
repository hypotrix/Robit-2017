import java.io.FileNotFoundException;

import lejos.hardware.Button;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;

/**
 * 
 * @author Micah Nelson
 *
 */
public class RobotTestingMulti {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		
		RobotController controller = new RobotController();
	
		controller.reloader.fetchSample(controller.sample, 0);
		if(controller.sample[0] == 1)
		{
			System.out.println("Touch Ready!");
		}
		System.out.println("Golfing Ready:\n Press as button!");
		Button.waitForAnyPress();
					//For testing surface rotation
					/*int angle = 444;*/ //current rotation is 444 testing on bsc mat
		//Plays golf forever.
		
		//controller.booper.rotate(300);
		//controller.armReload(false);
		//controller.moveBotTo(80);
		while(true){
			
			//This to play golf
			if(controller.ifPhaseCenter())
				controller.centerBall();
			 if(controller.ifPhaseApproach())
			controller.approachBall();
			 if(controller.ifPhaseBoop())
				controller.boopBall();
			 
			
				
			}		
		
		
			
			/*
			  //For testing surface rotation
			  int angle = 426; //current rotation
			 
		while(true){
			System.out.println("Current: "+ angle);
			
			controller.pilot.rotate(angle);
			try{Thread.sleep(10000);}catch(InterruptedException e){}
			angle += 1;
		}
		*/
		
	
		
		
		
	}
}
