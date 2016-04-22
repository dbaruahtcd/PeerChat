/*
 * Main node class.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;


public class Peer {
	
	
	private String ipAddress;
	private String nodeId;
	private String pingAck = "";
	private PeerUI peerUI;
	private HashMap<String, String> routingTable = new HashMap<String, String>();
	
	
	public Peer(){
		peerUI = new PeerUI(this);
		peerUI.start();
	}
	
	public void addToRouting(String nodeId, String ipAddress){
		routingTable.put(nodeId, ipAddress);
	}
	
	public void removeFromRouting(String leavingId){
		routingTable.remove(leavingId);
	}
	
	@SuppressWarnings("unchecked")
	public void routingInfo(String joinNodeId, String joinIpAddress){
		Map<String, Serializable> routingInfo = new LinkedHashMap<String, Serializable>();
		routingInfo.put("type", "ROUTING_INFO");
		routingInfo.put("gateway_id", nodeId);
		routingInfo.put("node_id", joinNodeId);
		routingInfo.put("ip_address", ipAddress);
		// Iterating through the hash map to construct a routing table
		
		JSONArray routeTable = new JSONArray();
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> entry = iterator.next();
			Map<String, String> route = new LinkedHashMap<String, String>();
			route.put("node_id", entry.getKey());
			route.put("ip_address", entry.getValue());
			routeTable.add(route);
		}
		routingInfo.put("route_table", routeTable);
		// Send routing information back to joining node.
		communicate(joinIpAddress, JSONValue.toJSONString(routingInfo));
		// Sending message to all other nodes to update their routing table
		joinRelay(joinNodeId, joinIpAddress);
		
		
	}
	
	public void communicate(String ipAddress, String message){
		try{
			Socket socket = new Socket(ipAddress, 8767);
			DataOutputStream sendMessage = new DataOutputStream(socket.getOutputStream());
			sendMessage.writeBytes(message + "\n");
			sendMessage.close();
			socket.close();
			
		}catch(Exception e){
			System.out.println("ERROR: Could Open Socket to: " + ipAddress);
		}
	}
	
	public void joinRelay(String joinId, String joinIp){
		// Create JSON String
		Map<String, String> relay = new LinkedHashMap<String, String>();
		relay.put("type", "JOINING_NETWORK_RELAY");
		relay.put("node_id", joinId);
		relay.put("ip_address", joinIp);
		// Send to all nodes in routing table
		broadcast(JSONValue.toJSONString(relay));
	}
	
	public void chat(String targetId, String message){
		// Create the JSON message
		
		Map<String, String> chat = new LinkedHashMap<String, String>();
		chat.put("type", "CHAT");
		chat.put("target_id", targetId);
		chat.put("sender_id", nodeId);
		chat.put("text", message);
		if(targetId.equals("00")){
			broadcast(JSONValue.toJSONString(chat));
		}
		else{
			String ip = routingTable.get(targetId);
			communicate(ip, JSONValue.toJSONString(chat));
		}
	}
	 
	// Broadcast method
	public void broadcast(String message){
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		while(iterator.hasNext()){
			communicate(iterator.next().getValue(), message);
		}
	}
	
	// Routing Table display method
	public void printRoutes(){
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		System.out.println("| ID | IPADDRESS |");
		while(iterator.hasNext()){
			System.out.println("|----------------|");
			Entry<String, String> entry = iterator.next();
			System.out.println("| " + entry.getKey() + " | " + entry.getValue() + " |");
			System.out.println("");
		}
	}
	
	// Ping method
	public void ping(String targetId){
		//Create JSON String
		Map<String, String> ping = new LinkedHashMap<String, String>();
		ping.put("type", "PING");
		ping.put("target_id", targetId);
		ping.put("sender_id", nodeId);
		ping.put("ip_address", ipAddress);
		String targetIp = routingTable.get(targetId);
		communicate(targetIp, JSONValue.toJSONString(ping));
	}
	
	public void sendAck(String targetId){
		// Create JSON String
		Map<String, String> ack = new LinkedHashMap<String, String>();
		ack.put("type", "ACK");
		ack.put("node_id", nodeId);
		ack.put("ip_address", ipAddress);
		String targetIp = routingTable.get(targetId);
		communicate(targetIp, JSONValue.toJSONString(ack));
	}
	
	public void leaveNetwork(){
		Map<String, String> leave = new LinkedHashMap<String, String>();
		leave.put("type", "LEAVING_NETWORK");
		leave.put("node_id", nodeId);
		// Inform all nodes in the routing table
		broadcast(JSONValue.toJSONString(leave));
	}

	    public static void main(String[] args) throws IOException {
		Peer server = new Peer();
		GatewayHandler gateWayHandler = new GatewayHandler(server, new ServerSocket(5949));
		gateWayHandler.start();
	}
	
	// Getters & Setters
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getPingAck() {
		return pingAck;
	}

	public void setPingAck(String pingAck) {
		this.pingAck = pingAck;
	}
}

