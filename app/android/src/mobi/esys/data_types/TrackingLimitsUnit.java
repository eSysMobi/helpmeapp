package mobi.esys.data_types;

public class TrackingLimitsUnit {
	private transient String timeLimit;
	private transient String speedLimit;

	public TrackingLimitsUnit() {
		super();
	}

	public TrackingLimitsUnit(String timeLimit, String speedLimit) {
		super();
		this.timeLimit = timeLimit;
		this.speedLimit = speedLimit;
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(String speedLimit) {
		this.speedLimit = speedLimit;
	}

	@Override
	public String toString() {
		return "TrackingLimitsUnit [getTimeLimit()=" + getTimeLimit()
				+ ", getSpeedLimit()=" + getSpeedLimit() + "]";
	}

}
