
import java.net.InetSocketAddress;
import java.net.Socket;


public interface PeerChat {
	
	
	public void init(Socket socket, int uid); //Initialize with a TCD server socket and unique id for node
	public long joinNetwork(InetSocketAddress bootstrapNode); //returns network_id, a locally  generated number to identify peer network
	public boolean leaveNetwork(long networkID); // parameter is perviously returned peer network identifier
	public void chat(String text, String[] tags);
	public ChatResult[] getChat(String[] words);
	
	

}
