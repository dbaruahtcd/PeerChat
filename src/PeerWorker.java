/*
 * This class represents various functionalities that other nodes in the network can call.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public  class PeerWorker implements Runnable, PeerChat{

	private Socket socket;
	private Peer peer;
	
	
	public PeerWorker(Socket socket, Peer peer){
		this.socket = socket;
		this.peer = peer;
	}

	@Override
	public void init(Socket socket, int uid) {
		// TODO Auto-generated method stub
	}

	@Override
	public long joinNetwork(InetSocketAddress bootstrap_node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean leaveNetwork(long network_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void chat(String text, String[] tags) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ChatResult[] getChat(String[] words) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String command = "";
				while(reader.ready()){
					command = reader.readLine();
					}
				if(!command.isEmpty()){
					JSONObject json = (JSONObject) new JSONParser().parse(command);
					String type = json.get("type").toString();
					switch(type){
						
					case "JOINING_NETWORK":
							String nodeId = json.get("node_id").toString();
							String ipAddress = json.get("ip_address").toString();
							peer.addToRouting(nodeId, ipAddress);
							peer.routingInfo(nodeId, ipAddress);
							break;
				    
					case "ROUTING_INFO":
							// Routing Information
							JSONArray routes = (JSONArray) json.get("route_table");
							// Add new routes to our routing table.
							for(int i = 0; i <routes.size(); i++){
							JSONObject route = (JSONObject) new JSONParser().parse(routes.get(i).toString());
							String id = route.get("node_id").toString();
							String ip = route.get("ip_address").toString();
							peer.addToRouting(id, ip);
							}
							break;
					
					case "JOINING_NETWORK_RELAY":
							// Update routing table with new node details.
							String newId = json.get("node_id").toString();
							String newIp = json.get("ip_address").toString();
							peer.addToRouting(newId, newIp);
							break;
							
					case "CHAT":
							System.out.println("");
							System.out.println("Message From: " + json.get("sender_id"));
							System.out.println(json.get("text"));
							System.out.println("");
							break;
							
					case "LEAVING_NETWORK":
							String leavingId = json.get("node_id").toString();
							peer.removeFromRouting(leavingId);
							break;
							
					case "PING":
							String responseId = json.get("sender_id").toString();
							peer.sendAck(responseId);
							break;
				    
					case "ACK":
							String ackId = json.get("node_id").toString();
							peer.setPingAck(ackId);
							break;
							
					case "ACK_CHAT":
						break;
				    
					case "CHAT_RETRIEVE":
						break;
					
					case "CHAT_RESPONSE":
						break;
			}
		}
			}
		}
			
			catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	

	