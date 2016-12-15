package com.furfel.lolteroids;

public class Bullet {

	public float x = 0.0f, y = 0.0f, dir = 0.0f;
	public static final float BULLET_MOTION=6.0f;
	public boolean exists=false;
	
	public Bullet(float x, float y, float d) {
		this.x=x; this.y=y; this.dir=d;
	}
	
	public void tick() {
		if(x>816 || x<-16 || y>496 || y<-16) exists=false;
		
		if(exists) {
			x+=BULLET_MOTION*Math.cos(dir);
			y+=BULLET_MOTION*Math.sin(dir);
		}
	}
	
	public void destroy() {
		exists=false;
	}
	
	public void createBullet(float x, float y, float d){
		this.x=x; this.y=y; this.dir=d; exists=true;
		AudioDerp.playSound(0);
	}
	
}
