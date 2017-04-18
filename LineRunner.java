import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Micah Nelson
 *
 */
public class LineRunner {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException {
		
		RobotController controller = new RobotController(true);
		
		//Starting lineRunner, goes until lost and checked surroundings.
		controller.lineRunner();
		
		controller.closeMotors();
		
	}

}
