import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
	static boolean notstopped = true;
	static ServerSocket server = null;
	static int port = 3113;
	static ServerManager manager = new ServerManager();
	
	public static void main(String[] argv){
		World.getWorld();
		manager.start();
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Couldn't start server on port "+port+"!");
		}
		if(server.isBound()){
			System.out.println("Server started on port "+port);
			while(notstopped){
				Socket s = null;
				try {
					s = server.accept();
				} catch (IOException e) {
				}
				if(notstopped && s!=null){
					manager.addClient(s);
				}
			}
		}
		System.out.println("Closing down");
		try {
			manager.close();
			server.close();
		} catch (IOException e) {
			System.out.println("Server never started");
		}
		System.out.println("Goodbye");
		System.exit(-1);
	}

	
}
