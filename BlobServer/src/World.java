import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class World {
	static ArrayList<Region> regions = new ArrayList<Region>();
	static ArrayList<Entity> ents = new ArrayList<Entity>();
	static ArrayList<Entity> newents = new ArrayList<Entity>();
	static int regioncount = 50;
	static int seed;
	static int mapheight = 180;
	
	public static void getWorld(){
		
		File f = new File("world");
		if(f.exists()){
			System.out.println("World exists, loading");
			for(int i=0;i<regioncount;i++){
				regions.add(new Region(i));
			}
			
			File f2 = new File("world/objects.dat");
			if(f2.exists()){
				try {
					InputStream read = new FileInputStream(f2);
					while(read.available()>=4){
						ents.add(new Entity(read.read(),read.read(),read.read(),read.read()));
					}
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
			System.out.println("World loaded");
		}
		else{
			System.out.println("World not found, generating new one");
			f.mkdir();
			generate();
		}
		File players = new File("world/players");
		if(!players.exists()){
			players.mkdir();
			System.out.println("Players folder created");
		}
	}
	
	public static Region getRegion(int mapx){
		for(Region r:regions){
			if(r.x==mapx){
				return r;
			}
		}
		return null;
	}
	
	public static int getEntityId(){
		return (int)(Math.random()*10000);
	}

	private static void generate() {
		int[][] world = new int[40*regioncount][mapheight];
		
		System.out.println("Generating world");
		int base = 40;
		int[] height = new int[40*regioncount];
		for(int e=0;e<40*regioncount;e++){
			height[e] = (int) (base+Math.random()*10);
			if(height[e]<2){
				height[e] = 2;
			}
			if(Math.random()<0.02){
				if(Math.random()<0.5){
					base += 5;
				}
				else{
					base -= 5;
				}
			}
		}
		System.out.println("Adding hills");
		for(int e=0;e<40*regioncount;e++){//Create pwnsome hills
			if(Math.random()<0.01){
				int size = 20+(int)(Math.random()*20);
				double factor = 0.4+Math.random()*0.5;
				for(int o=0;o<size;o++){
					if(e+o>=0&&e+o<2000){
						int add = (int)Math.abs(Math.sin((double)o/(double)size*Math.PI)*size*factor);//Try ALL the values!
						height[e+o]-= add;
					}
				}
			}
		}
		
		
		System.out.println("Smoothening terrain");
		for(int e=5;e<40*regioncount-5;e++){//Make the terrain smoothen out
			int count = 0;
			for(int o=-5;o<5;o++){
				count+=height[e+o];
			}
			height[e]=(int) (count/10);
		}
		System.out.println("Setting materials");
		
		for(int e=0;e<40*regioncount;e++){
			for(int i=0;i<mapheight;i++){//Set default dirt
				if(i>=height[e]){
					world[e][i]=2;
					if(i==height[e]){
						world[e][i]=1;
					}
				}
				else{
					world[e][i]=0;
				}
			}
		}
		
		System.out.println("Placing ores");
		for(int l=0;l<500;l++){
			System.out.println((double)l/5+"% done");
			int x = (int)(Math.random()*40*regioncount);
			int y = (int)(Math.random()*mapheight);
			int type = 10;
			if(Math.random()<0.5){
				type = 12;
				if(Math.random()<0.5){
					type = 14;
					if(Math.random()<0.5){
						type = 16;
						if(Math.random()<0.5){
							type = 18;
						}
					}
				}
			}
			if(y-5>height[x]){
				world = setWorldTileChance(world,x,y,1,type,height);
			}
		}
		
		System.out.println("Sending worms to dig caves");
		for(int w=0;w<100;w++){
			System.out.println(w+"% done");
			double x = Math.random()*40*regioncount;
			double y = 60+Math.random()*60;
			double direction = Math.random()*360;
			double size = 0.8+Math.random()*0.2;
			while(size>0){
				size-=0.025*Math.random();
				direction += -10+Math.random()*20;
				x += Math.cos(direction*Math.PI/180);
				y += Math.sin(direction*Math.PI/180);
				setWorldTileChanceWorm(world,(int)Math.round(x),(int)Math.round(y),size,3,height);
			}
		}
		
		System.out.println("Dividing regions");
		for(int r=0;r<regioncount;r++){
			int[][] part = new int[40][mapheight];
			int[] rheight = new int[40];
			for(int e=0;e<40;e++){
				for(int i=0;i<mapheight;i++){
					part[e][i]=world[e+r*40][i];
					rheight[e]=height[e+r*40];
				}
			}
			regions.add(new Region(r,part,rheight));
		}
		System.out.println("World generation completed");
	}

	private static int[][] setWorldTileChance(int[][] world, int x, int y, double chance, int type, int[] height) {
		if(Math.random()<chance){
			if(x>=0&&x<regioncount*40&&y>=height[x]&&y<mapheight){
				world[x][y]=type;
				double factor = 0.1;
				if(type==12){factor=0.15;}
				if(type==14){factor=0.2;}
				if(type==16){factor=0.25;}
				if(type==18){factor=0.3;}
				double add = -0.05+Math.random()*0.1;
				setWorldTileChance(world,x+1,y,chance-factor+add,type,height);
				setWorldTileChance(world,x-1,y,chance-factor+add,type,height);
				setWorldTileChance(world,x,y+1,chance-factor+add,type,height);
				setWorldTileChance(world,x,y-1,chance-factor+add,type,height);
				setWorldTileChance(world,x-1,y-1,chance-factor*2.5+add,type,height);
				setWorldTileChance(world,x+1,y-1,chance-factor*2.5+add,type,height);
				setWorldTileChance(world,x+1,y+1,chance-factor*2.5+add,type,height);
				setWorldTileChance(world,x-1,y+1,chance-factor*2.5+add,type,height);
			}
		}
		return world;
	}
	
	private static int[][] setWorldTileChanceWorm(int[][] world, int x, int y, double chance, int type, int[] height) {
		if(Math.random()<chance){
			if(x>=0&&x<regioncount*40&&y>=0&&y<mapheight && world[x][y]!=0){
				world[x][y]=type;
				double factor = 0.1;
				if(type==12){factor=0.15;}
				if(type==14){factor=0.2;}
				if(type==16){factor=0.25;}
				if(type==18){factor=0.3;}
				double add = -0.05+Math.random()*0.1;
				setWorldTileChance(world,x+1,y,chance-factor+add,type,height);
				setWorldTileChance(world,x-1,y,chance-factor+add,type,height);
				setWorldTileChance(world,x,y+1,chance-factor+add,type,height);
				setWorldTileChance(world,x,y-1,chance-factor+add,type,height);
				setWorldTileChance(world,x-1,y-1,chance-factor*2.5+add,type,height);
				setWorldTileChance(world,x+1,y-1,chance-factor*2.5+add,type,height);
				setWorldTileChance(world,x+1,y+1,chance-factor*2.5+add,type,height);
				setWorldTileChance(world,x-1,y+1,chance-factor*2.5+add,type,height);
			}
		}
		return world;
	}

	public static void save() {
		for(Region r:regions){
			r.save();
		}
		
		File f = new File("map/objects.dat");
		try {
			OutputStream write = new FileOutputStream(f);
			for(Entity e:World.ents){
				write.write(e.mapx);
				write.write(e.x);
				write.write(e.y);
				write.write(e.type);
			}
			System.out.println("Objects state saved");
		} catch (FileNotFoundException e) {
			try {
				f.createNewFile();
				save();
			} catch (IOException e1) {
			}
		} catch (IOException e) {
			f.delete();
		}
	}
}
