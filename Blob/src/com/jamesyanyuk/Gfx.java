package com.jamesyanyuk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.TextureLoader;

public class Gfx {
	static Image[] tiles,outlines,objects,blob,damage;
	
	public static void load(){
		boolean err = false;
		try {
			SpriteSheet blobsheet = new SpriteSheet(new Image(TextureLoader.getTexture("PNG", new FileInputStream("gfx/blob.png"))),20,20);
			blob = new Image[blobsheet.getHorizontalCount()*blobsheet.getVerticalCount()];
			for(int i=0;i<blobsheet.getHorizontalCount();i++){
				blob[i] = blobsheet.getSprite(i,0);
			}
			SpriteSheet breaksheet = new SpriteSheet(new Image(TextureLoader.getTexture("PNG", new FileInputStream("gfx/break.png"))),16,16);
			damage = new Image[breaksheet.getHorizontalCount()*breaksheet.getVerticalCount()];
			for(int i=0;i<breaksheet.getHorizontalCount();i++){
				damage[i] = breaksheet.getSprite(i,0);
			}
			SpriteSheet tilesheet = new SpriteSheet(new Image(TextureLoader.getTexture("PNG", new FileInputStream("gfx/tileset.png"))),16,16);
			tiles = new Image[tilesheet.getHorizontalCount()*tilesheet.getVerticalCount()];
			for(int e=0;e<tilesheet.getVerticalCount();e++){
				for(int i=0;i<tilesheet.getHorizontalCount();i++){
					tiles[e*tilesheet.getHorizontalCount()+i] = tilesheet.getSprite(i,e);
				}
			}
			SpriteSheet objectsheet = new SpriteSheet(new Image(TextureLoader.getTexture("PNG", new FileInputStream("gfx/objects.png"))),16,32);
			objects = new Image[objectsheet.getHorizontalCount()*objectsheet.getVerticalCount()];
			for(int e=0;e<objectsheet.getVerticalCount();e++){
				for(int i=0;i<objectsheet.getHorizontalCount();i++){
					objects[e*objectsheet.getHorizontalCount()+i] = objectsheet.getSprite(i,e);
				}
			}
			SpriteSheet outlinesheet = new SpriteSheet(new Image(TextureLoader.getTexture("PNG", new FileInputStream("gfx/outline.png"))),20,20);
			outlines = new Image[outlinesheet.getHorizontalCount()];
			for(int i=0;i<outlinesheet.getHorizontalCount();i++){
				outlines[i] = outlinesheet.getSprite(i,0);
			}
		
		} catch (FileNotFoundException e) {
			err = true;
		} catch (IOException e) {
			err = true;
		}
		if(err){
			System.out.println("Not all resources have loaded!");
		}
	}

	public static Image getTile(int t) {
		if(t<0){t=0;}
		if(t>tiles.length-1){
			t = tiles.length-1;
		}
		return tiles[t];
	}
	
	public static Image getOutline(int t) {
		return outlines[t];
	}

	public static Image getObject(int type) {
		return objects[type];
	}
}
