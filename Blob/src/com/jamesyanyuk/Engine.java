package com.jamesyanyuk;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

public class Engine {
	public static void main(String[] argv){
		try {
			Display.setDisplayMode(new DisplayMode(640,480));
			Display.create();
		} catch (LWJGLException e) {
			System.out.println("OpenGL Window could not be created!");
		}
		
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0,Display.getDisplayMode().getWidth(),Display.getDisplayMode().getHeight(),0,0,1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		Gfx.load();
		Game.init();
		
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			float red = 0.15f+(float)Game.self.y/1000f;//+(float)(Math.sin((double)Game.gametime/1000)*0.3);
			float green = 0.5f;//+(float)(Math.sin((double)Game.gametime/1000)*0.3);
			float blue = 0.8f;//+(float)(Math.sin((double)Game.gametime/1000)*0.3);
			glClearColor(red,green,blue,0);
			glClear(GL_COLOR_BUFFER_BIT);
			glLoadIdentity();
			
			Game.loop();
			
			Display.update();
			Display.sync(60);
		}
		
		Game.end();
		
		Display.destroy();
	}
}
