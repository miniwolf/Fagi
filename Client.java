import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket; 

    public Client(Socket s) {
	socket = s;
	out = null;
	in = null;
    }

    private boolean sendMessage(String msg) throws IOException {
	if(!socket.isClosed()) {
	    try {
		out = new PrintWriter(socket.getOutputStream());
		out.println(msg);
		out.flush();
		out.close();
	    } catch(IOException e) {
		e.printStackTrace();
		return false;
	    }
	} else {
	    throw new IOException("Socket closed");
	}
	return true;
    }
    
    private String RecieveMessage() throws IOException {
	String msg = "";
	StringBuilder msgContent = new StringBuilder();
	if (!socket.isClosed()) {
	    try {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (msg != null) {
		    msg = in.readLine();
		    if (msg == null) { break; }
		    msgContent.append(msg);
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} else {
	    throw new IOException("Socket closed");
	}
	return msgContent.toString();
    }
}
		    