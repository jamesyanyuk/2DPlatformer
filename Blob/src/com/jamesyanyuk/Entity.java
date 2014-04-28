package com.jamesyanyuk;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

public class Entity {
	int pid,map = 25;
	String name;
	double x,y,hsp,vsp;
	boolean free = true;
	boolean left,right,up,down,flip,vjump;
	Image sprite = Gfx.blob[0];
	Map mymap;
	
	public Entity(int i, int j) {
		x = i;
		y = j;
	}

	public void loop(){
		if(x<8){x=8;hsp=-hsp;}
		if(x>640*50-8){x=640*50-8;hsp=-hsp;}
		map = (int)(Math.floor(x/640));
		map = Math.max(0, Math.min(49,map));
		mymap = Game.maps[map];
			if(left && !down){
				hsp -= 0.1;
				if(hsp<-3){
					hsp = -3;
				}
			}
			if(right&&!down){
				hsp += 0.1;
				if(hsp>3){
					hsp = 3;
				}
			}
			if(!right && !left){
				if(!down){
					hsp *= 0.97;
				}
				else{
					hsp *= 0.99;
				}
			}
			if(!up){
				vjump = false;
			}
			if(up && (!free||vjump)){
				free = true;
				vjump = true;
				vsp -= 0.5;
				if(vsp<=-3.5){
					vjump = false;
				}
			}
			sprite = Gfx.blob[0];
			if(vsp!=0){
				sprite = Gfx.blob[2];
			}
			if(free){
				vsp += 0.1;//Gravity
			}
			if(down){
				sprite = Gfx.blob[1];
			}
			if(mymap.isAir((int)Math.floor((x-(map*640)-7)/16), (int)Math.floor((y+1)/16)) && mymap.isAir((int)Math.floor((x-(map*640)+7)/16), (int)Math.floor((y+1)/16))){
				free = true;
			}
			if(hsp>0){
				flip = false;
			}
			if(hsp<0){
				flip = true;
			}
			
			if(Game.maps[map]!=null){
				if(!Game.maps[map].loaded){
					vsp = 0;
					hsp = 0;
				}
			}
			else{
				vsp = 0;
				hsp = 0;
			}
			
			double hrep = hsp;
			double vrep = vsp;
			
			while(hrep>0.1){
				hrep -= 0.1;
				if(mymap.isAir((int)Math.floor((x-(map*640)+7.1)/16), (int)Math.floor((y)/16))){
					x += 0.1;
				}
				else{
					hsp = 0;
					hrep = 0;
				}
			}
			while(hrep<-0.1){
				hrep += 0.1;
				if(mymap.isAir((int)Math.floor((x-(map*640)-7.1)/16), (int)Math.floor((y)/16))){
					x -= 0.1;
				}
				else{
					hsp = 0;
					hrep = 0;
				}
			}
			while(vrep>0.1){
				vrep -= 0.1;
				if(mymap.isAir((int)Math.floor((x-(map*640)-7)/16), (int)Math.floor((y+0.1)/16)) && mymap.isAir((int)Math.floor((x-(map*640)+7)/16), (int)Math.floor((y+0.1)/16))){
					y += 0.1;
				}
				else{
					free = false;
					vsp = 0;
					vrep = 0;
				}
			}
			while(vrep<-0.1){
				vrep += 0.1;
				if(mymap.isAir((int)Math.floor((x-(map*640)-7)/16), (int)Math.floor((y-12.1)/16)) && mymap.isAir((int)Math.floor((x-(map*640)+7)/16), (int)Math.floor((y-12.1)/16))){
					y -= 0.1;
				}
				else{
					vsp = 0;
					vrep = 0;
				}
			}
			
			float light = mymap.getLightAt((int)Math.floor((x-(map*640))/16), (int)Math.floor((y)/16));
			Color darken = new Color(light,light,light);
			if(flip){
				sprite.getFlippedCopy(true, false).draw(Math.round(x)-10-Game.viewx,Math.round(y)-20-Game.viewy,darken);
				Gfx.blob[3].getFlippedCopy(true, false).draw(Math.round(x)-10-Game.viewx,Math.round(y)-20-Game.viewy,darken);
			}
			else{
				sprite.draw(Math.round(x)-10-Game.viewx,Math.round(y)-20-Game.viewy,darken);
				Gfx.blob[3].draw(Math.round(x)-10-Game.viewx,Math.round(y)-20-Game.viewy,darken);
			}
	}
}
