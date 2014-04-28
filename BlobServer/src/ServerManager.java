import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;


public class ServerManager extends Thread{
	ArrayList<Client> players = new ArrayList<Client>();
	
	public void run(){
		while(Main.notstopped){
			//Loop
			getInput();
			
			ArrayList<Entity> remove = new ArrayList<Entity>();
			for(Entity e:World.ents){
				e.loop();
				if(e.remove){
					remove.add(e);
				}
			}
			for(Entity r:remove){
				World.ents.remove(r);
			}
			for(Entity n:World.newents){
				World.ents.add(n);
			}
			World.newents = new ArrayList<Entity>();
			
			try {
				sleep(10);
			} catch (InterruptedException e) {
			}
		}
		interrupt();
	}

	private void getInput() {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		try {
			if(r.ready()){
				handleInput(r.readLine());
				r.reset();
			}
		} catch (IOException e) {
		}
	}

	private void handleInput(String in) {
		if(in.equalsIgnoreCase("exit")){
			try {
				World.save();
				Main.notstopped = false;
				@SuppressWarnings("unused")
				Socket s = new Socket("localhost",Main.port);
			} catch (IOException e) {
			}
		}
		if(in.equalsIgnoreCase("save")){
			World.save();
		}
	}

	public void addClient(Socket s) {
		Client cli = new Client(s);
		cli.setPriority(NORM_PRIORITY);
		players.add(cli);
	}

	public void close() throws IOException {
		for(Client c:players){
			c.out.close();
			c.in.close();
			c.socket.close();
		}
	}

	public static int getID() {
		return (int)(Math.random()*125);
	}
}
