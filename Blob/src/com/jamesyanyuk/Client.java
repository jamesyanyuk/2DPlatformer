package com.jamesyanyuk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{
	Socket s;
	DataInputStream in;
	DataOutputStream out;
	boolean left,right,up,down,stopped;
	String username = "Player";
	
	public void connect(){
		try {
			s = new Socket("localhost",3113);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			
			out.writeByte(0);
			out.writeUTF(username);
			out.flush();
		} catch (UnknownHostException e) {
			
		} catch (IOException e) {
			
		}
	}

	public void run() {
		while(!stopped){
			try {
				while(in.available()>0){
					int msg = in.readByte();
					handleMsg(msg);
				}
			} catch (IOException e) {
				
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleMsg(int msg) throws IOException {
		switch(msg){
		case 0:
			Game.self.pid = in.readByte();
			Game.self.x = in.readInt();
			Game.self.y = in.readInt();
			Game.self.name = Game.me.username;
			break;
		case 1:
			Entity newe = new Entity(50,-10);
			newe.pid = in.readByte();
			newe.name = in.readUTF();
			Game.ents.add(newe);
			break;
		case 2:
			Entity rem = null;
			int id = in.readByte();
			for(Entity e:Game.ents){
				if(e.pid==id){
					rem = e;
				}
			}
			if(rem!=null){
				Game.ents.remove(rem);
			}
			break;
		case 3:
			int wid = in.readByte();
			int kid = in.readByte();
			boolean on = in.readBoolean();
			for(Entity e:Game.ents){
				if(e.pid==wid){
					if(kid==0){e.up=on;}
					if(kid==1){e.right=on;}
					if(kid==2){e.down=on;}
					if(kid==3){e.left=on;}
				}
			}
			break;
		case 4:
			int pid = in.readByte();
			int x = in.readInt();
			int y = in.readInt();
			for(Entity e:Game.ents){
				if(e.pid==pid){
					e.x = x;
					e.y = y;
				}
			}
			break;
		case 5:
			int map = in.readByte();
			for(int e=0;e<40;e++){
				for(int i=0;i<Game.mapheight;i++){
					Game.maps[map].setTileAt(e, i, in.readByte());
				}
			}
			Game.maps[map].loaded();
			updatePos();
			break;
		case 6:
			int m = in.readByte();
			int tx = in.readByte();
			int ty = in.readByte();
			int nt = in.readByte();
			Game.maps[m].setTileAt(tx, ty, nt);
			Game.maps[m].updateLight();
			if(m>0){Game.maps[m-1].updateLight();}
			if(m<49){Game.maps[m+1].updateLight();}
			break;
		case 7:
			int eid = in.readInt();
			int emx = in.readByte();
			int ex = in.readByte();
			int ey = in.readByte();
			int et = in.readByte();
			Game.addObject(eid,emx,ex,ey,et);
			break;
		case 8:
			int ekid = in.readInt();
			Game.remObject(ekid);
			break;
		}
	}

	public void close() {
		stopped = true;
		try {
			out.writeByte(2);
			out.flush();
		} catch (IOException e) {
		}
	}

	public void updateKey(int i, boolean on) {
		try {
			out.writeByte(3);
			out.writeByte(i);
			out.writeBoolean(on);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void requestTileChange(int map, int tx, int ty, int t){
		Game.maps[map].setTileAt(tx, ty, 4);
		try {
			out.writeByte(6);
			out.writeByte(map);
			out.writeByte(tx);
			out.writeByte(ty);
			out.writeByte(t);
			out.flush();
		} catch (IOException e) {
		}
	}

	public void updatePos() {
		try {
			out.writeByte(4);
			out.writeInt((int)Game.self.x);
			out.writeInt((int)Game.self.y);
			out.flush();
		} catch (IOException e) {
		}
	}

	public void requestMap(int i) {
		try {
			out.writeByte(5);
			out.writeByte(i);
			out.flush();
		} catch (IOException e) {
		}
	}

	public void placeObject(int m, int x, int y, int t) {
		try {
			out.writeByte(7);
			out.writeByte(m);
			out.writeByte(x);
			out.writeByte(y);
			out.writeByte(t);
			out.flush();
		} catch (IOException e) {
		}
	}
}
