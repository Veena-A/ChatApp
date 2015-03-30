package com.span.client;
import java.awt.Button;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel:
import java.awt.TextArea;
import java.awt.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

class ChatClient2 extends Frame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Socket socket;
	TextField newMessageTxt;
	TextArea ConversationTxt;
	Button btnSend, btnClose;
	String sendTo;
	String LoginName;
	Thread t = null;
	DataOutputStream dout;
	DataInputStream din;
	JComboBox<String> users = new JComboBox<>();

	ChatClient2(String LoginName) throws Exception {
		super(LoginName);
		this.LoginName = LoginName;
		newMessageTxt = new TextField(50);
		ConversationTxt = new TextArea(50, 50);
		btnSend = new Button("Send");
		btnClose = new Button("Close");
		socket = new Socket("10.12.201.164",50000);
		

		din = new DataInputStream(socket.getInputStream());
		dout = new DataOutputStream(socket.getOutputStream());
		dout.writeUTF(LoginName);
		
		t = new Thread(this);
		t.start();

	}

	void setup() {
		setSize(600, 400);
		setLayout(new GridLayout(2, 1));

		add(ConversationTxt);
		Panel p = new Panel();

		users.setSize(500, 40);

		p.add(users);
		p.add(newMessageTxt);
		p.add(btnSend);
		p.add(btnClose);
		add(p);
		show();
	}

	public boolean action(Event e, Object o) {
		if (e.arg.equals("Send")) {
			while (true) {
				sendTo = JOptionPane.showInputDialog(null, "Enter UserName : ",
						"ChatApp", 1);
				if (sendTo.equalsIgnoreCase("") || sendTo == null) {

				} else {
					break;
				}
			}

			try {
				dout.writeUTF(sendTo + " " + "DATA" + " " +LoginName + " "
						+ newMessageTxt.getText().toString());
				ConversationTxt.append("\n" + " sentTo " + sendTo + " : "
						+ newMessageTxt.getText().toString());
				newMessageTxt.setText("");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.arg.equals("Close")) {
			try {
				dout.writeUTF(LoginName + " LOGOUT");
				System.exit(1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return super.action(e, o);
	}

	public static void main(String args[]) throws Exception {
		String LoginName = null;
		LoginName = JOptionPane.showInputDialog(null, "Enter UserName : ",
				"ChatApp", 1);
		if (LoginName.equalsIgnoreCase("") || LoginName == null) {
			JOptionPane.showMessageDialog(null, "UserName  Required");
			System.exit(0);
		}
		ChatClient client = new ChatClient(LoginName);
		client.setup();
	}

	public void run() {
		while (true) {
			try {
				
				
				StringTokenizer tokens = new StringTokenizer(din.readUTF().toString(), ":");

				String message = null;
				String fromName = null;

				boolean isMessage = false;

				do {

					if (isMessage) 
					{
						String temp=tokens.nextToken();
						
						if(!LoginName.equalsIgnoreCase(temp))
						users.addItem(temp);
						
						System.out.println(users.toString());
						
					} else {
						fromName = tokens.nextToken();

						message = tokens.nextToken();

						isMessage = true;
					}
				} while (tokens.hasMoreElements());

				ConversationTxt.append("\n" + fromName + " Says :" + message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
