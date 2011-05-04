package edu.asu.eas.snac.cloudvpn.networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

import edu.asu.eas.snac.cloudvpn.messages.TransmittableMessage;



public class TCPClient{
	
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	//This method returns a TCP socket given a server IP and a port number.
	
	
	public boolean connect (String serverIp, int port) {
		socket = null;
		try {
			Log.i("INFO", "I'm trying to connect to IP:" + serverIp + " on port: " + port );
			System.out.println("I'm trying to connect to IP:" + serverIp + " on port: " + port );
			InetAddress serverAddr = InetAddress.getByName(serverIp);
			socket = new Socket(serverAddr, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
		}
		catch (Exception e) {
			System.out.println("I failed.");
             return false;
        }
		return true;	
	}
	public boolean disconnect() {
		try {
			if (socket!= null)
				socket.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
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
			//ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			msg = (TransmittableMessage) ois.readObject();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return msg;
	}
}