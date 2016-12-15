package com.furfel.lolteroids;

import android.graphics.RectF;
import android.util.Log;

public class Lolteroid extends RectF {

	public float dir = 0.0f;
	public int type = 0, lr=0;
	public boolean exists=false, particlesE=false;
	public Particle[] particles;
	
	public Lolteroid() {
		
	}
	
	public void tick() {
		if(exists)
			offset(-(float)Math.cos(dir)*(3.0f+(float)type*1.5f), -(float)Math.sin(dir)*(3.0f+(float)type*1.5f));
		if(left>900 || left<-300 || top<-100 || top>600) {
			exists=false;
			Log.d("Lolteroids","Destroyed lolteroid");
		}
	}
	
	public void particleTick() {
		boolean allDead=true;
		for(int i=0;i<particles.length;i++) {
			if(particles[i].exists) {particles[i].tick(); allDead=false;}
			}
		particlesE = !allDead;
	}
	
	public void destroy() {
		exists=false;
		particlesE = true;
		particles = new Particle[2+Particle.r.nextInt(4)];
		GameDerp.particlesCreated+=particles.length;
		GameDerp.lolteroidsDestroyed[type]++;
		float st = Particle.r.nextFloat()*90.0f;
		for(int i=0;i<particles.length;i++) {
			particles[i] = new Particle(centerX(),centerY(),st+i*(360.0f/(float)particles.length));
		}
		AudioDerp.playSound(1);
	}
	
	public void createLolteroid(int lev) {
		if(Particle.r.nextInt()%2==0) {
			left = Particle.r.nextFloat()*800.0f;
			if(Particle.r.nextInt()%2==0)
				top = -50.0f;
			else
				top = 530.0f;
		} else {
			top = Particle.r.nextFloat()*480.0f;
			if(Particle.r.nextInt()%2==0)
				left = -50.0f;
			else
				left = 850.0f;
		}
		right = left+32.0f;
		bottom = top+32.0f;
		if(left>400.0f) lr=1; else lr=0;
		type = Particle.r.nextInt(2+lev);
		dir = (float) Math.atan2(centerY() - 240.0f, centerX() - 400.0f);
		exists=true;
		Log.d("Lolteroids","new at:"+left+","+top+" dir:"+dir);
	}
	
}
