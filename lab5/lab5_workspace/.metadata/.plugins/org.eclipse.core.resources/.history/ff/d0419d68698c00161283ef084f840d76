public class StateAndReward {
	
	public static String getStateAngle(double angle, double vx, double vy) {
		String state = "UndefinedState";
		
		/*
		 
		 Defines the state based on the angle
		 
					 		     -.3 G .3
					 		A	   \  /			B
					 				\/
					 -1.5 -------------------	 1.5
					  				|		
					  		D		|		C
					  				?
		 */
		if(angle <= -0.03 && angle > -1.5) {
			// State A
			state = "STATE-A";
		} else if(angle > 0.03 && angle < 1.5) {
			// State B
			state = "STATE-B";
		} else if(angle > -0.03 && angle <= 0.03) {
			// Goal State
			state = "STATE-G";
		} else if(angle >= 1.5) {
			// State C
			state = "STATE-C";
		} else {
			// State D
			state = "STATE-D";
		}
		
		
		return state;
		
	}
	
	public static double getRewardAngle(double angle, double vx, double vy) {
		double reward = 0;
		
		if(angle <= -0.03 && angle > -1.5) {
			// State A
			reward += 3;
		} else if(angle > 0.03 && angle < 1.5) {
			// State B
			reward += 3;
		} else if(angle > -0.03 && angle <= 0.03) {
			// Goal State
			reward = 10;
		} else if(angle >= 1.5) {
			// State C
			
		} else {
			// State D
			
		}
		
		return reward;
	}
	public static final int ANGLE_RESOLUTION = 11;
	public static final int VY_RESOLUTION = 7;
	
	public static final int VELOCITY_BOUND = 1;
	
	
	/*reward discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		String state = "STATE-ANGLE-" +
						discretize(angle,ANGLE_RESOLUTION,-.8,.8) +
						"-VY-" + discretize(vy,VY_RESOLUTION,-VELOCITY_BOUND-3,VELOCITY_BOUND);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		double a_reward = 0;
		double y_reward = 0;
		
		int a_state = discretize(angle,ANGLE_RESOLUTION,-3,3);
		int y_state = discretize(vy,VY_RESOLUTION,-VELOCITY_BOUND,VELOCITY_BOUND);
		
		int a_optimum = (ANGLE_RESOLUTION-1)/2;
		int y_optimum = (VY_RESOLUTION-1)/2;
		
		if(a_state == a_optimum){
			a_reward = 4*a_optimum;
		}else {
			a_reward = (a_optimum - Math.abs(a_state - a_optimum));
		}
		
		if(y_state == y_optimum){
			y_reward = 5*y_optimum;
		}else {
			y_reward = (y_optimum - Math.abs(y_state - y_optimum));
		}

		
		//System.out.println("x: "+x_reward);
		System.out.println("y: "+y_reward);
		System.out.println("a: "+a_reward);
		
		return a_reward + /*x_reward*/ + y_reward;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
