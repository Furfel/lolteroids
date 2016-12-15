package com.furfel.lolteroids;

import java.util.Random;

public class Particle {

	public int particle=0;
	public int life=0;
	public boolean exists=false;
	public float x=0f, y=0f, dir=0.0f;
	public static final float PARTICLE_MOTION=2.0f;
	public static Random r = new Random();
	
	public Particle(float x, float y, float dir) {
		this.x=x; this.y=y; this.dir=(float) Math.toRadians(dir); particle = r.nextInt(5); life = 50+r.nextInt(10); exists=true;
	}
	
	public void tick() {
		x+=(float)Math.cos(dir)*PARTICLE_MOTION;
		y+=(float)Math.sin(dir)*PARTICLE_MOTION;
		particle = r.nextInt(5);
		life--;
		if(life<=0) exists=false;
	}
	
}
