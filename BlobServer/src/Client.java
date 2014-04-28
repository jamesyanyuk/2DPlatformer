import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class Client extends Thread{
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
	String name;
	int pid;
	boolean stopped;
	int x=320+25*640,y;
	
	public Client(Socket s) {
		socket = s;
		try {
			out = new DataOutputStream(s.getOutputStream());
			in = new DataInputStream(s.getInputStream());
		} catch (IOException e) {
		}
		System.out.println(socket.getInetAddress().getHostAddress()+" connected");
		start();
		
		for(Entity e:World.ents){
			e.sendTo(this);
		}
	}
	
	private void save() {
		File player = new File("world/players/"+name+".dat");
		try {
			OutputStream write = new FileOutputStream(player);
			
			int mapx = (int)(Math.floor(x/640));
			int xx = (int)(Math.floor((x-mapx*640)/16));
			int yy = (int)(Math.floor(y/16));
			
			write.write(mapx);
			write.write(xx);
			write.write(yy);
			
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	private void load(String name) {
		File player = new File("world/players/"+name+".dat");
		if(!player.exists()){
			try {
				player.createNewFile();
				System.out.println(name+" > Player file created");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try {
				
				InputStream read = new FileInputStream(player);
				int map = read.read();
				x = map*640+16*read.read();
				y = 16*read.read();
				
			} catch (FileNotFoundException e) {
			} catch (IOException e) {	
			}
		}
	}
	
	private void handle(int r){
		switch(r){
		case 0:
			//New user
		try {
			name = in.readUTF();
			pid = ServerManager.getID();
			
			load(name);
			
			System.out.println(name + " joined(ID "+pid+")");
			
			out.writeByte(0);
			out.writeByte(pid);
			out.writeInt(8+x);
			out.writeInt(y);
			out.flush();
			
			//Send to all others, and send others to me
			for(Client c:Main.manager.players){
				if(c.pid!=pid){
					c.out.writeByte(1);
					c.out.writeByte(pid);
					c.out.writeUTF(name);
					c.out.flush();
					
					out.writeByte(1);
					out.writeByte(c.pid);
					out.writeUTF(c.name);
					out.flush();
				}
			}
		} catch (IOException e1) {
			timeout();
		}
			break;
		case 2:
			try {
			save();
			Main.manager.players.remove(this);
			stopped = true;
			System.out.println(name + " left(ID "+pid+")");
			for(Client c:Main.manager.players){
				if(c.pid!=pid){
					c.out.writeByte(2);
					c.out.writeByte(pid);
					c.out.flush();
				}
			}
			} catch (IOException e1) {
				timeout();
			}
			break;
		case 3:
			try {
			int key = in.readByte();
			boolean on = in.readBoolean();
			for(Client c:Main.manager.players){
				if(c.pid!=pid){
					try{
					c.out.writeByte(3);
					c.out.writeByte(pid);
					c.out.writeByte(key);
					c.out.writeBoolean(on);
					c.out.flush();
					} catch (IOException e1) {
						c.timeout();
					}
				}
			}
			} catch (IOException e1) {
				timeout();
			}
			break;
		case 4:
			try {
			x = in.readInt();
			y = in.readInt();
			} catch (IOException e1) {
				timeout();
			}
			for(Client c:Main.manager.players){
				if(c.pid!=pid){
					try{
					c.out.writeByte(4);
					c.out.writeByte(pid);
					c.out.writeInt(x);
					c.out.writeInt(y);
					c.out.flush();
					} catch (IOException e1) {
						c.timeout();
					}
				}
			}
			
			break;
		case 5:
			try {
			int map = in.readByte();
			Region get = null;
			for(Region reg:World.regions){
				if(reg.x==map){
					get = reg;
				}
			}
			if(get!=null){
				out.writeByte(5);
				out.writeByte(map);
				for(int e=0;e<40;e++){
					for(int i=0;i<World.mapheight;i++){
						out.writeByte(get.tile[e][i]);
					}
				}
				out.flush();
			}
			} catch (IOException e1) {
				timeout();
			}
			break;
		case 6:
			try {
			//Tile change event
			int tmap = in.readByte();
			int tx = in.readByte();
			int ty = in.readByte();
			int t = in.readByte();
			Region reg = World.getRegion(tmap);
			if(t==0){
				if(ty>=reg.height[tx]){
					t = 3;//Make it background tile
				}
			}
			reg.updateTile(tx,ty,t);
			} catch (IOException e1) {
				timeout();
			}
			break;
		case 7:
			try {
			//Object place event
			int emap = in.readByte();
			int ex = in.readByte();
			int ey = in.readByte();
			int et = in.readByte();
			World.ents.add(new Entity(emap,ex,ey,et));
			break;
			} catch (IOException e1) {
				timeout();
			}
		}
	}

	public void run(){
		while(!stopped){
			try {
				while(in.available()>0){
					handle(in.readByte());
				}
			} catch (IOException e) {
				timeout();
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {
				timeout();
			}
		}
	}

	public void timeout() {
		save();
		stopped = true;
		System.out.println(name + " left(ID "+pid+")");
		for(Client c:Main.manager.players){
			if(c.pid!=pid){
				try {
					c.out.writeByte(2);
					c.out.writeByte(pid);
					c.out.flush();
				} catch (IOException e) {
					c.timeout();
				}
			}
		}
		Main.manager.players.remove(this);
	}
}
