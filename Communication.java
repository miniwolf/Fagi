import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
 
public class Communication {
 
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static String host = "dyndns.jonne.dk";
    private static int port = 4242;
 
    public Communication() {
	out = null;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ioe) {
            System.err.print(ioe.getStackTrace());
            try {
                socket.close();
            } catch (Exception e) {
                System.err.print(e.getStackTrace().toString());
                System.exit(1);
            }
        }
    }
 
    public void closeIn() {
        try {
            in.close();
        } catch(Exception e) { }
    }
 
    public void reopenIn() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(Exception e) { }
    }
 
    public boolean SendMessage(String nmsg) {
        try {
	    StringBuilder msg = new StringBuilder("M");
	    msg.append(nmsg);
            out = new PrintWriter(socket.getOutputStream());
            out.println(msg.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.print(e.getStackTrace().toString());
            return false;
        }
        return true;
    }
 
    public String ReceiveMessage() {
        String msg = "";
        StringBuilder msgContent = new StringBuilder();

        try {
            while (msg != null) {
                msg = in.readLine();
                if (msg == null) {
                    break;
		}
                JOptionPane.showMessageDialog(null, msg);
                msgContent.append(msg);
            }
        } catch (IOException ioe) {
            System.err.print(ioe.getStackTrace().toString());
            try {
                socket.close();
            } catch (Exception e) {
                System.err.print(e.getStackTrace().toString());
            }
        }
        return msgContent.toString();
    }

    public boolean login(String login) {
	return true;
    }
	

    
}
