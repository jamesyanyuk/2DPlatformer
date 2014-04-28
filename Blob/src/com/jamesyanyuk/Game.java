package com.jamesyanyuk;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Game {
	static int gametime = 0;
	static Client me;
	static Entity self = new Entity(640*25+320,0);
	static int update = 99;
	static Map[] maps = new Map[50];
	static int mapheight = 180;
	static ArrayList<Entity> ents = new ArrayList<Entity>();
	static int viewy,viewx;
	static int selectmap=-1,selectx=-1,selecty=-1;
	static double damage;
	static int cooldown;
	static ArrayList<DynamicObject> objects = new ArrayList<DynamicObject>();
	static ArrayList<DynamicObject> newobjects = new ArrayList<DynamicObject>();
	
	public static void init() {
		me = new Client();
		me.connect();
		me.start();
		ents.add(self);
		for(int e=0;e<50;e++){
			maps[e] = new Map();
		}
	}

	public static void loop() {
		try{
			for(DynamicObject d:objects){
				d.loop();
			}
			for(DynamicObject d:newobjects){
				objects.add(d);
			}
		}
		catch(ConcurrentModificationException e){
			//Strangeness
		}
		newobjects = new ArrayList<DynamicObject>();
		
		for(int e=self.map-1;e<=self.map+1;e++){
			if(e>=0&&e<50){
				if(maps[e].loaded){
					maps[e].loop();
				}
				else{
					maps[e].load(e);
				}
			}
		}
		
		for(Entity e:ents){
			e.loop();
		}
		
		double nviewy = (Game.self.y-240);
		double nviewx = (Game.self.x-320);
		viewx += (nviewx-viewx)/20;
		viewy += (nviewy-viewy)/20;
		if(viewx<0){viewx=0;}
		if(viewx>640*49){viewx=640*49;}
		if(viewy>mapheight*16-480){
			viewy = mapheight*16-480;
		}
		
		/*Temporary plant placing
		if(cooldown>0){
			cooldown--;
		}
		if(Mouse.isButtonDown(1) && cooldown == 0){
			cooldown = 30;
			int worldx = Mouse.getX()+viewx;
			int worldy = 480-Mouse.getY()+viewy;
			selectmap = (int)(Math.floor(worldx/640));
			selecty = (int)(Math.floor(worldy/16));
			selectx = (int)(Math.floor((worldx-selectmap*640)/16));
			int type = 16;
			Game.me.placeObject(selectmap,selectx,selecty,type);
		}
		*/
		
		if(Mouse.isButtonDown(0)){
			int worldx = Mouse.getX()+viewx;
			int worldy = 480-Mouse.getY()+viewy;
			selectmap = (int)(Math.floor(worldx/640));
			int oldx = selectx,oldy = selecty;
			selecty = (int)(Math.floor(worldy/16));
			selectx = (int)(Math.floor((worldx-selectmap*640)/16));
			if(selectx!=oldx||selecty!=oldy||Game.maps[selectmap].getTileAt(selectx, selecty)==4){
				damage = 0;
			}
			else{
				damage++;
				if(damage>50){
					damage = 0;
					Game.me.requestTileChange(selectmap, selectx, selecty, 0);
				}
			}
		}
		else{
			damage = 0;
			selectx = -1;
			selecty = -1;
		}
		
		gametime++;
		
		update++;
		if(update>50){
			update = 0;
			me.updatePos();
		}
		
		self.up = Keyboard.isKeyDown(Keyboard.KEY_UP);
		self.left = Keyboard.isKeyDown(Keyboard.KEY_LEFT);
		self.right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
		self.down = Keyboard.isKeyDown(Keyboard.KEY_DOWN);
		if(self.up!=me.up){me.updateKey(0,self.up);me.up=self.up;}
		if(self.right!=me.right){me.updateKey(1,self.right);me.right=self.right;}
		if(self.down!=me.down){me.updateKey(2,self.down);me.down=self.down;}
		if(self.left!=me.left){me.updateKey(3,self.left);me.left=self.left;}
	}

	public static void end() {
		me.close();
	}

	public static void addObject(int eid, int emx, int ex, int ey, int et) {
		newobjects.add(new DynamicObject(eid,emx,ex,ey,et));
	}

	public static void remObject(int ekid) {
		DynamicObject kill = null;
		for(DynamicObject d:objects){
			if(d.id==ekid){
				kill = d;
			}
		}
		if(kill!=null){
			objects.remove(kill);
		}
	}

}
