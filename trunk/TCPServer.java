package edu.asu.eas.snac.cloudvpn.networking;



import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.asu.eas.snac.cloudvpn.entities.Configuration;
import edu.asu.eas.snac.cloudvpn.messages.FeedbackMessage;
import edu.asu.eas.snac.cloudvpn.messages.FeedbackType;
import edu.asu.eas.snac.cloudvpn.messages.Message;
import edu.asu.eas.snac.cloudvpn.messages.MessageType;
import edu.asu.eas.snac.cloudvpn.messages.MessageUtility;
import edu.asu.eas.snac.cloudvpn.messages.RegisterMessage;
import edu.asu.eas.snac.cloudvpn.messages.TransmittableMessage;

import edu.asu.eas.snac.cloudvpn.managers.Manager;

public class TCPServer implements Runnable{

    public static final String SERVERIP = "127.0.0.1";
    private int serverPort = Configuration.registererPort;
    private String username;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    
    public TCPServer() {
    	
    }
	public int send (TransmittableMessage msg) {
		try {
			//ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(msg);
		} 
		catch (Exception e) {
			return -1;
		}
		return 0;
		
	}
	
	public TransmittableMessage recieve () {
		TransmittableMessage msg = null;
		try {
			
			msg = (TransmittableMessage) ois.readObject();
		} 
		catch (Exception e) {
			return null;
		}
		return msg;
	}
    
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(this.getServerPort());
			System.out.println("I'm listening on port # "+ this.getServerPort());
			while (true) {
				socket = serverSocket.accept();
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					oos = new ObjectOutputStream(socket.getOutputStream());

				}
				catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("S: Connected to  a client...");
				
				TransmittableMessage msg = this.recieve(); //Receive the sessionId && username from the client
				System.out.println("S: I recieved a message from the client...");
				Message message = MessageUtility.convertTranssmitableMessageToMessage(msg);
				RegisterMessage registerMsg = null;
				boolean valid = false;
				if (message.getMsgType() == MessageType.REGISTER_MSG) {
					registerMsg = (RegisterMessage) message;
					System.out.println("S: It's a registration message");
					System.out.println("S: Recieved info :");
					System.out.println(registerMsg.getUsername()+ " "+ registerMsg.getPassword()+ " "+
							registerMsg.getEmail()+ " " + registerMsg.getIMEI() + " " + registerMsg.getPhone()); 
					// WRITE TO THE FILE
					boolean written = false;
					try {
						FileWriter outFile = new FileWriter("chap-secrets.txt",true);
						PrintWriter out = new PrintWriter(outFile);
						out.println(registerMsg.getUsername()+ "\tl2tpd\t"+ registerMsg.getPassword()+ "\t*\n");
						out.close();
						written = true;
					} catch (IOException e){
						e.printStackTrace();
					}

					
					if (written) { //if writing successful
						FeedbackMessage fmsg = new FeedbackMessage();
						fmsg.setMsgType(MessageType.FEEDBACK_MSG);
						fmsg.setReturnNo(FeedbackType.SUCCESS);
						oos.writeObject( MessageUtility.convertMessageToTransmittableMessage(fmsg));
						System.out.println("I just sent a feedback message to phone.");
						System.out.println(fmsg);
					}
					else {
						FeedbackMessage fmsg = new FeedbackMessage();
						fmsg.setMsgType(MessageType.FEEDBACK_MSG);
						fmsg.setReturnNo(FeedbackType.REGISTURE_FAILURE);
						this.send( MessageUtility.convertMessageToTransmittableMessage(fmsg));
						System.out.println("I just sent a feedback message to phone.");
						System.out.println(fmsg);
					}


				}
				
			}



		}catch (Exception e) {
			System.out.println("S: Error");
			e.printStackTrace();
		}

	}
        		 
        	 
        	 
        	 
    

    public static void main (String a[]) {

    	Thread tcpServerThread = new Thread(new TCPServer());
    	tcpServerThread.start();
    }

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getServerPort() {
		return serverPort;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
