
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PeerRequestHandler implements Runnable{
	
	private Socket socket ;
	private InputStreamReader streamIn ;
	private ChatServer server;
	private int joinID;
	private boolean killServer;
	
	
	public ClientRequestHandler(Socket socket, int joinID, ChatServer server)
	{
		this.socket = socket;
		this.joinID = joinID;
		this.server = server;
		this.killServer = false;
	}
	
	/* 
	 * This method is used to save to client joining information
	 */
	
	private void joinChatRoom(String chatroomName, String clientName)
	{
		ClientInfo client = new ClientInfo(joinID , clientName, socket);
		server.joinChatRoom(chatroomName, client);
	}
	
	//leave chatroom
	protected void leaveChatRoom(String chatRoomId, String clientId)
	{
		int chatRoom = Integer.parseInt(chatRoomId);
		int client = Integer.parseInt(clientId);
		server.leaveChatRoom(chatRoom, client);
	}
	
	//chat with the group
	protected void chat(String chatRoomId, String clientId, String message)
	{
		int chatRoom = Integer.parseInt(chatRoomId);
		int client = Integer. parseInt(clientId);
		if(!server.chat(chatRoom, client, message)){
			error(1);
		}
	}
	
	protected String helloResponse(String command){
		return command + "\nIP: "+ socket.getLocalAddress().toString().substring(1)+ "\nPort:";
	}
	
	//send an error message back to client
	protected void error(int error)
	{
		try
		{
			PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
			String errorMessage = "ERROR_CODE" + error + "\nERROR_DESCRIPTION :";
			String description = "";
			if(error == 0)
			{
				description = "Incorrect Format, Not enough lines \n";
			}
			else if( error == 1)
			{
				description = "ChatRoom does not exists\n";
			}
			writer.println(errorMessage + description);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
public void run(){
		try{
			while(true){
				if(executorService.getActiveCount() < executorService.getMaximumPoolSize()){
					Socket socket = serverSocket.accept();
					PeerWorker client = new PeerWorker(socket, server);
					GatewayHandler.executorService.execute(client);
				}
			  }
			}
		
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	// Getters & Setters
	
    public Peer getServer() {
		return server;
	}

	public void setServer(Peer server) {
		this.server = server;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
}
}
