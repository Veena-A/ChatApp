# ChatApp
Client server chat application
package com.span;
//Chat Server

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

class ChatServer
{
static Vector ClientSockets;
static Vector LoginNames;

ChatServer() throws Exception
{
   ServerSocket soc=new ServerSocket(50000);
   ClientSockets=new Vector();
   LoginNames=new Vector();
   while(true)
   {    
       Socket CSoc=soc.accept();        
       AcceptClient obClient=new AcceptClient(CSoc);
   }
}
public static void main(String args[]) throws Exception
{
  
   ChatServer ob=new ChatServer();
}

class AcceptClient extends Thread
{
Socket ClientSocket;
DataInputStream din;
DataOutputStream dout;
AcceptClient (Socket CSoc) throws Exception
{
   ClientSocket=CSoc;

   din=new DataInputStream(ClientSocket.getInputStream());
   dout=new DataOutputStream(ClientSocket.getOutputStream());
   
   String LoginName=din.readUTF();

   System.out.println("User Logged In :" + LoginName);
   LoginNames.add(LoginName);
   ClientSockets.add(ClientSocket);    
   start();
}

public void run()
{
   while(true)
   {
       
       try
       {
           String msgFromClient=new String();
           msgFromClient=din.readUTF();
           StringTokenizer st=new StringTokenizer(msgFromClient);
           
           String Sendto=st.nextToken();     
           
           String MsgType=st.nextToken();
           
           int iCount=0;

           if(MsgType.equals("LOGOUT"))
           {
               for(iCount=0;iCount<LoginNames.size();iCount++)
               {
                   if(LoginNames.elementAt(iCount).equals(Sendto))
                   {
                       LoginNames.removeElementAt(iCount);
                       ClientSockets.removeElementAt(iCount);
                       System.out.println("User " + Sendto +" Logged Out ...");
                       break;
                   }
               }

           }
           if(MsgType.equalsIgnoreCase("getUsers"))
           {
          	 
          	 Socket tSoc=(Socket)ClientSockets.elementAt(iCount);                            
               DataOutputStream tdout=new DataOutputStream(tSoc.getOutputStream());
               
               for(Object objUser : LoginNames)
               {
              	 String userName = (String) objUser;
              	 
              	 tdout.writeUTF(userName);
               }
                                           
               break;
           }
           else
           {
          	 
               String msg="";
               
               String fromName =st.nextToken();
               msg = fromName + ":";
               System.out.println("from : "+ fromName + "sendTo" + Sendto +"Message "+ MsgType);
               
               while(st.hasMoreTokens())
               {
                   msg=msg +" "+ st.nextToken();
               }
               for (Object objName : LoginNames)
               {
					String strName=(String)objName;
					msg = msg + ":" + strName;
			     }
               for(iCount=0;iCount<LoginNames.size();iCount++)
               {
                   if(LoginNames.elementAt(iCount).equals(Sendto))
                   {    
                       Socket tSoc=(Socket)ClientSockets.elementAt(iCount);                            
                       DataOutputStream tdout=new DataOutputStream(tSoc.getOutputStream());
                       tdout.writeUTF(msg);                            
                       break;
                   }
               }
               if(iCount==LoginNames.size())
               {
                   dout.writeUTF("I am offline");
               }
               else
               {
                   
               }
           }
           if(MsgType.equals("LOGOUT"))
           {
               break;
           }

       }
       catch(Exception ex)
       {
         ex.printStackTrace();
       }
   }        
}
}
}
