package sample.object.user;

public class LoginResponse {
	
	private Boolean success;
	private String error;
	private String token;
	
	public LoginResponse(Boolean success, String error, String token){
		this.success = success;
		this.error = error;
		this.token = token;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}
