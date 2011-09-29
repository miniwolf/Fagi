/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Login.java
 *
 * Handles logins.
 */

/**
 * @author miniwolf
 */

/*
 * TODO: Send request to Communication
 */

public class Login {
    private Communication communication;
    
    public Login() { 
	communication = new Communication();
    }

    /**
     * @param username string from the textField.getText method
     * @param password char[] from passwordField.getPassword method
     */

    public boolean doLogin(String username, char[] password) {
	StringBuilder login = new StringBuilder("L,");
	login.append(username + ","); 
	login.append(password);
	String requestLogin = login.toString();
	return communication.login(login.toString());
    }

}
