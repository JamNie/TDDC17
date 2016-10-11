public class TutorialController extends Controller {

    public SpringObject object;

    ComposedSpringObject cso;

    /* These are the agents senses (inputs) */
	DoubleFeature x; /* Positions */
	DoubleFeature y;
	DoubleFeature vx; /* Velocities */
	DoubleFeature vy;
	DoubleFeature angle; /* Angle */

    /* Example:
     * x.getValue() returns the vertical position of the rocket 
     */
	

	/* These are the agents actuators (outputs)*/
	RocketEngine leftRocket;
	RocketEngine middleRocket;
	RocketEngine rightRocket;

    /* Example:
     * leftRocket.setBursting(true) turns on the left rocket 
     */
	
	public void init() {
		cso = (ComposedSpringObject) object;
		x = (DoubleFeature) cso.getObjectById("x");
		y = (DoubleFeature) cso.getObjectById("y");
		vx = (DoubleFeature) cso.getObjectById("vx");
		vy = (DoubleFeature) cso.getObjectById("vy");
		angle = (DoubleFeature) cso.getObjectById("angle");

		leftRocket = (RocketEngine) cso.getObjectById("rocket_engine_left");
		rightRocket = (RocketEngine) cso.getObjectById("rocket_engine_right");
		middleRocket = (RocketEngine) cso.getObjectById("rocket_engine_middle");
	}

    public void tick(int currentTime) {
    	/*
    	 * Implement the tick() method so that it receives readings from the sensors "angle", "vx", "vy" and prints them out on the standard output.*/

    	double previous_vx = vx.getValue();
    	double previous_vy = vy.getValue();
    	double previous_angle = angle.getValue();
    	
    	if(previous_vy > 2) {
    		middleRocket.setBursting(true);
    	}
    	else {
    		middleRocket.setBursting(false);
    	}

    	System.out.print("\n");
    	
    	System.out.print("Angle: " + previous_angle + "\n");
    	System.out.print("vx: " + previous_vx + "\n");
    	System.out.print("vy: " + previous_vy + "\n");

    	/* TODO: Insert your code here */
    	
    }

}
