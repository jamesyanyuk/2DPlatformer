import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Region {
	int x;
	int[][] tile = new int[40][World.mapheight];
	int[] height = new int[40];
	public Region(int x){
		this.x = x;
		load();
	}
	
	public Region(int x, int[][] map, int[] heightmap){
		this.x = x;
		tile = map;
		height = heightmap;
		save();
	}
	
	public void load(){
		File f = new File("world/region_"+x+".map");
		try {
			InputStream read = new FileInputStream(f);
			for(int i=0;i<40;i++){
				height[i] = read.read();
			}
			for(int i=0;i<40;i++){
				for(int e=0;e<World.mapheight;e++){
					tile[i][e]=read.read();
				}
			}
		} catch (FileNotFoundException e) {
			fix();
		} catch (IOException e) {
			fix();
		}
	}

	public void fix() {
		System.out.println("Region is corrupted...ignoring...");
		for(int i=0;i<40;i++){
			height[i]=0;
			for(int e=0;e<World.mapheight;e++){
				tile[i][e]=0;
			}
		}
		save();
	}
	
	public void save(){
		File f = new File("world/region_"+x+".map");
		try {
			OutputStream write = new FileOutputStream(f);
			for(int i=0;i<40;i++){
				write.write(height[i]);
			}
			for(int i=0;i<40;i++){
				for(int e=0;e<World.mapheight;e++){
					write.write(tile[i][e]);
				}
			}
			System.out.println("Region "+x+" saved");
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

	public void updateTile(int tx, int ty, int t) {
		tile[tx][ty]=t;
		for(Client c:Main.manager.players){
			if(Math.abs(x*640-c.x)<960){
				try {
					c.out.writeByte(6);
					c.out.writeByte(x);
					c.out.writeByte(tx);
					c.out.writeByte(ty);
					c.out.writeByte(t);
					c.out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
