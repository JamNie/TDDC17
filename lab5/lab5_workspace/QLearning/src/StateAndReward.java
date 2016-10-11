public class StateAndReward {
	static int numberOfAngleStates = 12;
	static int numberOfVyStates = 5;
	static int numberOfVxStates = 5;
	

	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {
		String state =  discreteAngle(angle) + "_";
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		return (Math.PI - Math.abs(angle));
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		return discreteAngle(angle) + "_" + discreteVY(vy) + "_" + discreteVX(vx);
	}
	
	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {
		double angle_reward = (Math.PI - Math.abs(angle));
		double vx_reward = Math.abs(vx);
		double vy_reward = Math.abs(vy);

		return angle_reward - vy_reward - vx_reward;
	}
	
	public static int discreteAngle(double angle) {
		return discretize(angle, numberOfAngleStates,-Math.PI,Math.PI);
	}
	
	public static int discreteVX(double value) {
		return discretize(value,  numberOfVxStates, -10, 10);
	}
	
	public static int discreteVY(double value) {
		return discretize(value, numberOfVyStates, -10, 20);
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
