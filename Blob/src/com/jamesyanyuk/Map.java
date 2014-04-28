package com.jamesyanyuk;

import org.newdawn.slick.Color;

public class Map {
	boolean requesting,loaded,rightfixed,leftfixed;
	int map;
	int[][] tile = new int[40][Game.mapheight];
	float[][] light = new float[40][Game.mapheight];
	int[][] outline = new int[40][Game.mapheight];
	
	public void load(int me){
		if(!requesting){
			map = me;
			requesting = true;
			Game.me.requestMap(map);
		}
	}
	
	public void loaded(){
		loaded = true;
		requesting = false;
		for(int e=0;e<40;e++){
			for(int i=0;i<Game.mapheight;i++){
				updateOutline(e,i);
			}
		}
	}

	public void loop(){
		if(!rightfixed && map < 49){
			if(Game.maps[map+1]!=null){
				if(Game.maps[map+1].loaded){
					updateSides();
					Game.maps[map+1].updateSides();
					rightfixed = true;
				}
			}
		}
		if(!leftfixed && map > 0){
			if(Game.maps[map-1]!=null){
				if(Game.maps[map-1].loaded){
					updateSides();
					Game.maps[map-1].updateSides();
					leftfixed = true;
				}
			}
		}
		for(int e=0;e<40;e++){
			for(int i=0;i<Game.mapheight;i++){
				int t = getTileAt(e,i);
				if(t>0){
					drawTile(e,i,t);
				}
			}
		}
		for(int e=0;e<40;e++){
			for(int i=0;i<Game.mapheight;i++){
				int t = getOutlineAt(e,i);
				if(t>0){
					drawOutline(e,i,t);
				}
			}
		}
	}
	
	public void drawTile(int x, int y, int t){
		if(x*16-Game.viewx+map*640>=-16&&x*16-Game.viewx+map*640<640&&y*16-Game.viewy>=-16&&y*16-Game.viewy<480){
			Color l = new Color(light[x][y],light[x][y],light[x][y]);
			Gfx.getTile(t).draw(x*16-Game.viewx+map*640,y*16-Game.viewy, l);
			if(Game.damage!=0){
				if(Game.selectmap==map&&x==Game.selectx&&y==Game.selecty){
					int d = (int) Math.min(4, Math.floor(Game.damage/10));
					Gfx.damage[d].draw(x*16-Game.viewx+map*640,y*16-Game.viewy, l);
				}
			}
		}
	}
	
	public int getTileAt(int x, int y){
		if(y>=0 && y<Game.mapheight){
			if(x>=0 && x<40){
				return tile[x][y];
			}
			else{
				if(x<0){
					if(map>0){
						if(Game.maps[map-1]!=null){
							if(Game.maps[map-1].loaded){
								return Game.maps[map-1].getTileAt(40+x,y);
							}
							else{
								return 0;
							}
						}
						else{
							return 0;
						}
					}
					else{
						return 0;
					}
				}
				else{
					if(map<49){
						if(Game.maps[map+1]!=null){
							if(Game.maps[map+1].loaded){
								return Game.maps[map+1].getTileAt(x-40,y);
							}
							else{
								return 0;
							}
						}
						else{
							return 0;
						}
					}
					else{
						return 0;
					}
				}
			}
		}
		else{
			return 0;
		}
	}
	public boolean isAir(int x, int y){
		if(y>=0 && y<Game.mapheight){
			return (getTileAt(x,y)==0||getTileAt(x,y)==3);
		}
		else{
			return true;
		}
	}
	public void drawOutline(int x, int y, int t){
		if(y*16-Game.viewy>=-16&&y*16-Game.viewy<480){
			Gfx.getOutline(t).draw(x*16-2-Game.viewx+map*640,y*16-2-Game.viewy);
		}
	}
	public int getOutlineAt(int x, int y){
		if(x>=0 && x<40 && y>=0 && y<Game.mapheight){
			return outline[x][y];
		}
		else{
			return 0;
		}
	}
	public void setTileAt(int x, int y, int t){
		if(x>=0 && x<40 && y>=0 && y<Game.mapheight){
			tile[x][y] = t;
			updateOutline(x,y);
			updateOutline(x+1,y);
			updateOutline(x-1,y);
			updateOutline(x,y+1);
			updateOutline(x,y-1);
		}
	}
	public float getLightAt(int x, int y){
		if(y>=0 && y<Game.mapheight){
			if(x>=0 && x<40){
				return light[x][y];
			}
			else{
				if(x<0){
					if(map>0){
						if(Game.maps[map-1]!=null){
							if(Game.maps[map-1].loaded){
								return Game.maps[map-1].getLightAt(40+x,y);
							}
							else{
								return 0;
							}
						}
						else{
							return 0;
						}
					}
					else{
						return 0;
					}
				}
				else{
					if(map<49){
						if(Game.maps[map+1]!=null){
							if(Game.maps[map+1].loaded){
								return Game.maps[map+1].getLightAt(x-40,y);
							}
							else{
								return 0;
							}
						}
						else{
							return 0;
						}
					}
					else{
						return 0;
					}
				}
			}
		}
		else{
			return 0;
		}
	}
	public void setLightAt(int x, int y, float t){
		if(x>=0 && x<40 && y>=0 && y<Game.mapheight){
			light[x][y] = t;
		}
	}
	public void setOutlineAt(int x, int y, int t){
		if(x>=0 && x<40 && y>=0 && y<Game.mapheight){
			outline[x][y] = t;
		}
	}
	public void updateLight(){
		float[][] tmplight = new float[40][Game.mapheight];
		for(int e=0;e<40;e++){
			float lightleft = 1;
			for(int i=0;i<Game.mapheight;i++){
				int tile = getTileAt(e,i);
				tmplight[e][i]=lightleft;
				if(tile!=0){
					if(tile==3){
						lightleft-=0.05f;
					}
					else{
						lightleft-=0.1f;
					}
				}
				
			}
		}
		float[][] tmplight2 = new float[40][Game.mapheight];
		for(int e=0;e<40;e++){
			for(int i=0;i<Game.mapheight;i++){
				double count = 0;
				for(int e2=-5;e2<5;e2++){
					for(int i2=-5;i2<5;i2++){
						int thistile = getTileAt(e+e2,i+i2);
						if(thistile != 0){
							if(thistile==3){
								count += 1;
							}
							else{
								count += 0.5;
							}
						}
					}
				}
				tmplight2[e][i]=(2f*(1-(float)(count/100)))*tmplight[e][i];
			}
		}
		for(int e=0;e<40;e++){
			for(int i=0;i<Game.mapheight;i++){
				setLightAt(e,i,tmplight2[e][i]);
			}
		}
	}
	public void updateSides(){
		for(int i=0;i<Game.mapheight;i++){
			updateOutline(0,i);
			updateOutline(39,i);
		}
		updateLight();
	}
	public void updateOutline(int e, int i) {
		int self = getTileAt(e,i);
		int left = getTileAt(e-1,i);
		int right = getTileAt(e+1,i);
		int top = getTileAt(e,i-1);
		int bottom = getTileAt(e,i+1);
		if(e>=0 && e<40 && i>=0 && i<Game.mapheight){
			outline[e][i] = 0;
			if(self>0){
				if(left!=self&&right!=self&&top!=self&&bottom!=self){
					outline[e][i]=9;
				}
				if(left==self&&right!=self&&top!=self&&bottom!=self){
					outline[e][i]=15;
				}
				if(left!=self&&right==self&&top!=self&&bottom!=self){
					outline[e][i]=12;
				}
				if(left!=self&&right!=self&&top==self&&bottom!=self){
					outline[e][i]=14;
				}
				if(left!=self&&right!=self&&top!=self&&bottom==self){
					outline[e][i]=13;
				}
				if(left!=self&&right!=self&&top==self&&bottom==self){
					outline[e][i]=11;
				}
				if(left==self&&right==self&&top!=self&&bottom!=self){
					outline[e][i]=10;
				}
				if(left==self&&right!=self&&top==self&&bottom!=self){
					outline[e][i]=3;
				}
				if(left!=self&&right==self&&top==self&&bottom!=self){
					outline[e][i]=4;
				}
				if(left!=self&&right==self&&top!=self&&bottom==self){
					outline[e][i]=1;
				}
				if(left==self&&right!=self&&top!=self&&bottom==self){
					outline[e][i]=2;
				}
				if(left==self&&right==self&&top!=self&&bottom==self){
					outline[e][i]=5;
				}
				if(left!=self&&right==self&&top==self&&bottom==self){
					outline[e][i]=8;
				}
				if(left==self&&right!=self&&top==self&&bottom==self){
					outline[e][i]=6;
				}
				if(left==self&&right==self&&top==self&&bottom!=self){
					outline[e][i]=7;
				}
			}
		}
	}
}
