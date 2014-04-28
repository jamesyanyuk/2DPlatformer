package com.jamesyanyuk;

public class DynamicObject {
	public int id,mapx,x,y,type;
	
	public DynamicObject(int eid, int emx, int ex, int ey, int et) {
		id = eid;
		mapx = emx;
		x = ex;
		y = ey;
		type = et;
	}

	public void loop(){
		Gfx.getObject(type).draw(x*16-Game.viewx+mapx*640,y*16-Game.viewy);
	}
}
