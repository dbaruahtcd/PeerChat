/*
 * User interface.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONValue;

public class PeerUI extends Thread{
	
	private Peer peer;
	private BufferedReader reader;

	    public PeerUI(Peer peer){
		this.peer = peer;
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run(){
		joinNetwork();
		while(true){
			menu();
		}
	}
	
	public int hashCode(String str){
		int hash = 0;
		for(int i = 0; i < str.length(); i++){
			hash = hash * 31 + str.charAt(i);
		}
		return Math.abs(hash);
	}
	
	// Join Network Method.
	
	public void joinNetwork(){
		try {
			System.out.println("Join the network.....");
			System.out.println("Enter your desired node ID");
		//	int hash = hashCode(reader.readLine()); Alternate hashing process
			String nodeId = reader.readLine();
			System.out.println("Enter Gateway IP Address (Blank for bootstrap node)");
			String gatewayIp = reader.readLine();
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("type","JOINING_NETWORK");
			map.put("node_id", nodeId);
			map.put("ip_address", InetAddress.getLocalHost().getHostAddress().toString());
			String jsonText = JSONValue.toJSONString(map);
			// Initialization 
			peer.setIpAddress(InetAddress.getLocalHost().getHostAddress().toString());
			peer.setNodeId(nodeId);
			peer.communicate(gatewayIp, jsonText);
		}
		 catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	// Chat Method
	
	public void chat(){
		try{
			System.out.println("Enter Destination ID (00 for network broadcast)");
			String targetId = reader.readLine();
			System.out.println("Enter Your Message.....");
			String message = reader.readLine();
			peer.chat(targetId, message);
		} 
		 catch(IOException e){
			e.printStackTrace();
		}
	}
	
	// Ping Method
	
	public void ping(){
		try{
			System.out.println("Enter Node ID For Ping");
			String targetId = reader.readLine();
			System.out.println("Pinging......");
			peer.ping(targetId);
			Boolean ack = false;
			long startTime = System.currentTimeMillis();
			while((System.currentTimeMillis()-startTime)<5000){
				if(peer.getPingAck().equals(targetId)){
					ack = true;
					break;
				}
				System.out.print(".");
				Thread.sleep(500);
			}
			if(ack == true){
				System.out.println(" Ping successful. Node available...");
			}
			else{
				System.out.println(" The target node is dead ");
			}
		} 
		 catch(IOException e){
			e.printStackTrace();
		} 
		 catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void leave(){
		peer.leaveNetwork();
		System.exit(0);
	}
	
	// Menu
	public void menu(){
		try{
			System.out.println("");
			System.out.println("Menu: Enter number");
			System.out.println("1. Send Chat Message");
			System.out.println("2. View Routing Table");
			System.out.println("3. Ping Node");
			System.out.println("4. Leave Network");
			String choice = reader.readLine();
			if(choice.equals("1")){
				chat();
			}
			else if(choice.equals("2")){
                peer.printRoutes();
			}
			else if(choice.equals("3")){
			    ping();
			}
			else if(choice.equals("4")){
				leave();
			}
			else{
				System.out.println("Please enter a valid input");
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
