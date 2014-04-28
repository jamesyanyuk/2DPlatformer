import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	static boolean running = true;
	public static void main(String[] argv){
		try {
			ServerSocket server = new ServerSocket(3113);
			if(server!=null){
				System.out.println("Server started");
			}
			while(running){
				Socket tmp = server.accept();
				Client c = new Client(tmp);
				c.start();
			}
		} catch (IOException e) {
			System.out.println("Server error");
		}
		System.exit(0);
	}
}
