public class Login {

    public Login() {
	
    }

    /**
     * @param username string from the textField
     * @param password char[] from passwordField
     */

    public boolean doLogin(String username, char[] password) {
	StringBuilder login = new StringBuilder("M");
	login.append(username);
	login.append(password);
	String requestLogin = login.toString();
	// Send request using requestLogin
	return true;
    }

}