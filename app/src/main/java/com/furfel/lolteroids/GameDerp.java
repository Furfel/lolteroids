package com.furfel.lolteroids;

import android.util.Log;

public class GameDerp implements Runnable {

	public Bullet[] bullets = new Bullet[64];
	public Lolteroid[] lolteroids = new Lolteroid[48];
	public boolean isRunning=false, paused=true, gameover=false, hiscore=false;
	
	public static int playerRot=0;
	public static int destPlayerRot=0;
	public int lives = 3;
	public int score = 0;
	public int hiScore=0;
	
	public static long bulletsShot=0,totalScore=0,totalPlays=0,particlesCreated=0; public static long[] lolteroidsDestroyed = new long[4];
	
	public int nextLolteroidCountdown=0, nextLolteroidDefaultCountDown=50, hitCooldown=0;
	
	private Lolteroids lt;
	
	public GameDerp(Lolteroids lt) {
		for(int i=0;i<bullets.length;i++)
			bullets[i] = new Bullet(0,0,0);
		for(int i=0;i<lolteroids.length;i++)
			lolteroids[i] = new Lolteroid();
		this.lt=lt;
	}
	
	public void start() {
		isRunning=true;
		new Thread(this).start();
	}
	
	public void shoot(float d) {
		int i=0; boolean found=false;
		while(!found && i<bullets.length) {
			if(!bullets[i].exists) {
				bullets[i].createBullet(400,240,d);
				bulletsShot++;
				found=true;
			}
			i++;
		}
	}
	
	public void newGame() {
		for(int i=0;i<bullets.length;i++) {
			bullets[i].exists=false;
			}
		for(int i=0;i<lolteroids.length;i++) {
			lolteroids[i].exists=false;
			lolteroids[i].particlesE=false;
		}
		score=0; lives=3; totalPlays++; gameover=false; hiscore=false;
		playerRot=0; destPlayerRot=0;
	}
	
	public void createLolteroid(int lev) {
		int i=0; boolean found=false;
		while(!found && i<lolteroids.length) {
			if(!lolteroids[i].exists && !lolteroids[i].particlesE) {
				lolteroids[i].createLolteroid(lev);
				found=true;
			}
			i++;
		}
	}
	
	public static void saveGame() {
		
	}
	
	public void tick() {
		for(int i=0;i<bullets.length;i++)
			if(bullets[i].exists) bullets[i].tick();
		for(int i=0;i<lolteroids.length;i++)
			if(lolteroids[i].exists) {
				lolteroids[i].tick();
				if(hitCooldown<=0 && lolteroids[i].intersect(384, 224, 416, 236)) {
					hitCooldown=40;
					lives--;
					if(lives<=0) {
						gameover=true;
						AudioDerp.playSound(4);
						if(score>hiScore) {hiScore=score; hiscore=true;}
					}
					else AudioDerp.playSound(3);
					break;
				} else
				for(int j=0;j<bullets.length;j++)
					if(bullets[j].exists)
						if(lolteroids[i].contains(bullets[j].x, bullets[j].y)) {bullets[j].destroy(); score+=50*Math.pow(2, lolteroids[i].type); totalScore+=50*Math.pow(2, lolteroids[i].type); lolteroids[i].destroy(); break;}
				}
			else if(lolteroids[i].particlesE) {
				lolteroids[i].particleTick();
			}
		if(playerRot-destPlayerRot<0) {
			playerRot++;
		} else if(playerRot-destPlayerRot>0) {
			if(playerRot>0) playerRot--; else playerRot=14;
		}
		
		if(nextLolteroidCountdown>0) {
			nextLolteroidCountdown--;
		}
		
		if(countLolteroids()<5+(int)(score/3000) && nextLolteroidCountdown<=0) {
			for(int i=0;i<1+score/4000;i++)
				createLolteroid((score>15000)?2:(score>5000)?1:0);
			nextLolteroidCountdown=nextLolteroidDefaultCountDown-(int)(score/3500);
			if(nextLolteroidCountdown<5) nextLolteroidCountdown=5;
		}
		
		if(hitCooldown>0) hitCooldown--;
		
		
	}
	
	public int countLolteroids() {
		int c=0;
		for(int i=0;i<lolteroids.length;i++)
			if(lolteroids[i].exists) {c++;}
		return c;
	}
	
	public void run() {
		while(isRunning) {
			if(!paused && !gameover) tick();
			try {Thread.sleep(40);} catch(Exception e) {}
		}
	}
	
}
