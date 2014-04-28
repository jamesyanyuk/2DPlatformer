import java.io.IOException;


public class Entity {
	public int id,mapx,x,y,timer,type;
	public boolean interact,remove;//Trigger for doors etc.
	
	public Entity(int m, int x2, int y2, int type2) {
		mapx = m;
		x = x2;
		y = y2;
		type = type2;
		id = World.getEntityId();
		
		for(Client c:Main.manager.players){
			sendTo(c);
		}
	}

	public void sendTo(Client c) {
		try {
			c.out.writeByte(7);
			c.out.writeInt(id);//Destroy this id
			c.out.writeByte(mapx);
			c.out.writeByte(x);
			c.out.writeByte(y);
			c.out.writeByte(type);
			c.out.flush();
		} catch (IOException e) {
			c.timeout();
		}
	}

	public void loop(){
		
		//Plant growth
		timer += (int)(Math.random()*Math.random()*4);
		
		if(type==16 && timer>15000){
			changeTo(17);
		}
		if(type==17 && timer>13000){
			changeTo(18);
		}
		if(type==18 && timer>13000){
			if(Math.random()<0.01){
				changeTo(23);
			}
			else{
				changeTo(19);
			}
		}
		if(type==19 && timer>13000){
			if(Math.random()<0.1){
				if(Math.random()<0.3){
					changeTo(21);
				}
				else{
					changeTo(20);
				}
			}
			else{
				changeTo(22);
			}
		}
		//End of plant growth
		
		if(timer>999999){
			timer=0;
		}
	}
	
	public void changeTo(int type){
		for(Client c:Main.manager.players){
			try {
				c.out.writeByte(8);
				c.out.writeInt(id);//Destroy this id
				c.out.flush();
				remove = true;
				World.newents.add(new Entity(mapx,x,y,type));
			} catch (IOException e) {
				c.timeout();
			}
		}
	}
}
