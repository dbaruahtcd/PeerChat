
import java.net.InetSocketAddress;
import java.net.Socket;


public interface PeerChat {
	
	
	void init(Socket socket, int uid); //Initialize with a TCD server socket and unique id for node
	long joinNetwork(InetSocketAddress bootstrapNode); //returns network_id, a locally  generated number to identify peer network
	boolean leaveNetwork(long networkID); // parameter is perviously returned peer network identifier
	void chat(String text, String[] tags);
	ChatResult[] getChat(String[] words);
	
	

}
