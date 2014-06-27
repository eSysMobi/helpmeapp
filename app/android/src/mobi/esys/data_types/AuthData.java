package mobi.esys.data_types;

public class AuthData {
	private transient String provider;
	private transient String socID;
	private transient String accessToken;
	private transient String expireTime;

	public AuthData() {
		super();
	}

	public AuthData(String provider, String socID, String accessToken,
			String expireTime) {
		super();
		this.provider = provider;
		this.socID = socID;
		this.accessToken = accessToken;
		this.expireTime = expireTime;
	}

	public AuthData(String provider, String socID, String accessToken) {
		super();
		this.provider = provider;
		this.socID = socID;
		this.accessToken = accessToken;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getSocID() {
		return socID;
	}

	public void setSocID(String socID) {
		this.socID = socID;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	@Override
	public String toString() {
		return "AuthData [getProvider()=" + getProvider() + ", getSocID()="
				+ getSocID() + ", getAccessToken()=" + getAccessToken()
				+ ", getExpireTime()=" + getExpireTime() + "]";
	}

}
