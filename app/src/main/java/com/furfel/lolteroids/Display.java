package com.furfel.lolteroids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class Display extends View implements Runnable {

	/* System */
	
	public boolean isRunning=true;
	public boolean paused=false;
	public int drawSpeed=25;
	
	/* Metrics */
	
	private int W=800,H=480,TILE_SIZE=32;
	private int MarginHorizontal=0, MarginVertical=0;
	private float scaleFactor=1.0f;
	
	/* Bitmaps and Graphics */
	
	private Bitmap spriteSheet, pausedBitmap, gameoverBitmap, hiscoreBitmap;
	private Paint screenLimiter = new Paint();
	private Paint screenLimiterOverlay = new Paint();
	
	/* References */
	
	private Lolteroids lt;
	
	public Display(Context context, Lolteroids lt, int w, int h) {
		super(context);
		W=w; H=h;
		this.lt=lt;
		if((float)W/(float)H>5f/3f) {
			scaleFactor = (float)H/480f;
			TILE_SIZE = (int) (scaleFactor*32);
			MarginHorizontal = (int)(W-(int)(5f*(float)H/3f))/2;
		} else {
			scaleFactor = (float)W/800f;
			TILE_SIZE = (int) (scaleFactor*32);
			MarginVertical = (int)(H-(int)(3f*(float)W/5f))/2;
		}
		screenLimiter.setColor(Color.GREEN);
		screenLimiter.setStyle(Paint.Style.STROKE);
		screenLimiter.setStrokeWidth(2.0f*scaleFactor);
		screenLimiterOverlay.setColor(Color.BLACK);
		screenLimiterOverlay.setStyle(Paint.Style.FILL);
	}
	
	public void onDraw(Canvas c) {
		
		/* Draw game objects */
		
		if(lt!=null && lt.game!=null) {
			for(int i=0;i<lt.game.bullets.length;i++)
				if(lt.game.bullets[i].exists)
					c.drawBitmap(spriteSheet,new Rect(240,0,256,16),new RectF(lt.game.bullets[i].x*scaleFactor+MarginHorizontal-TILE_SIZE/2,lt.game.bullets[i].y*scaleFactor+MarginVertical-TILE_SIZE/2,lt.game.bullets[i].x*scaleFactor+MarginHorizontal+TILE_SIZE/2,lt.game.bullets[i].y*scaleFactor+MarginVertical+TILE_SIZE/2),null);
			for(int i=0;i<lt.game.lolteroids.length;i++)
				if(lt.game.lolteroids[i].exists)
					c.drawBitmap(spriteSheet,new Rect(96+16*(lt.game.lolteroids[i].type*2+lt.game.lolteroids[i].lr),16,96+16*(lt.game.lolteroids[i].type*2+lt.game.lolteroids[i].lr+1),32),new RectF(lt.game.lolteroids[i].left*scaleFactor+MarginHorizontal,lt.game.lolteroids[i].top*scaleFactor+MarginVertical,lt.game.lolteroids[i].left*scaleFactor+MarginHorizontal+TILE_SIZE,lt.game.lolteroids[i].top*scaleFactor+MarginVertical+TILE_SIZE),null);
				else if(lt.game.lolteroids[i].particlesE) {
					for(int j=0;j<lt.game.lolteroids[i].particles.length;j++)
						if(lt.game.lolteroids[i].particles[j].exists)
							c.drawBitmap(spriteSheet,new Rect(lt.game.lolteroids[i].particles[j].particle*16,16,lt.game.lolteroids[i].particles[j].particle*16+16,32),new RectF(lt.game.lolteroids[i].particles[j].x*scaleFactor+MarginHorizontal-TILE_SIZE/2,lt.game.lolteroids[i].particles[j].y*scaleFactor+MarginVertical-TILE_SIZE/2,lt.game.lolteroids[i].particles[j].x*scaleFactor+MarginHorizontal+TILE_SIZE/2,lt.game.lolteroids[i].particles[j].y*scaleFactor+MarginVertical+TILE_SIZE/2),null);
				}
			if(lt.game.hitCooldown%2==0)
				c.drawBitmap(spriteSheet,new Rect(16*GameDerp.playerRot,0,16*GameDerp.playerRot+16,16),new RectF(W/2-TILE_SIZE/2,H/2-TILE_SIZE/2,W/2+TILE_SIZE/2,H/2+TILE_SIZE/2),null);
			
			/* Draw screen limiter */
			c.drawRect(MarginHorizontal+1, MarginVertical+1, W-MarginHorizontal-1, H-MarginVertical-1, screenLimiter);
			c.drawRect(0,0,MarginHorizontal,H,screenLimiterOverlay);
			c.drawRect(0,0,W,MarginVertical,screenLimiterOverlay);
			c.drawRect(W-MarginHorizontal,0,W,H,screenLimiterOverlay);
			c.drawRect(0,H-MarginVertical,W,H,screenLimiterOverlay);
			
			/* Draw hud */
			
			for(int i=0;i<lt.game.lives;i++) {
				c.drawBitmap(spriteSheet,new Rect(160,32,176,48),new RectF((float)MarginHorizontal+4f+(float)i*(float)TILE_SIZE,(float)MarginVertical+4f,(float)MarginHorizontal+4f+(float)(i+1)*(float)TILE_SIZE,(float)MarginVertical+(float)TILE_SIZE+4f),null);
			}
			
			char[] chars = String.format("%08d", lt.game.score).toCharArray();
			
			for(int i=0;i<chars.length;i++) {
				c.drawBitmap(spriteSheet,new Rect((chars[i]-48)*16,32,(chars[i]-48)*16+16,48),new RectF((float)W-(float)MarginHorizontal-(float)(9-i)*(float)TILE_SIZE,(float)MarginVertical+4f,(float)W-(float)MarginHorizontal-(float)(8-i)*(float)TILE_SIZE,(float)MarginVertical+4f+TILE_SIZE),null);
			}
			
			if(paused) {
				c.drawBitmap(pausedBitmap, W/2-pausedBitmap.getWidth()/2, H/2-pausedBitmap.getHeight()/2,null);
			} else if(lt.game.hiscore){
				c.drawBitmap(hiscoreBitmap, W/2-hiscoreBitmap.getWidth()/2, H/2-hiscoreBitmap.getHeight()/2,null);
				for(int i=0;i<chars.length;i++)
					c.drawBitmap(spriteSheet,new Rect((chars[i]-48)*16,32,(chars[i]-48)*16+16,48),new RectF((float)W/2f-4f*(float)TILE_SIZE+(float)(i*TILE_SIZE)+(float)MarginHorizontal,(float)H/2f+(float)+hiscoreBitmap.getHeight()+(float)MarginVertical-(float)TILE_SIZE/2f,(float)W/2f-4f*(float)TILE_SIZE+(i+1)*(float)TILE_SIZE+(float)MarginHorizontal,(float)H/2+(float)MarginVertical+hiscoreBitmap.getHeight()+(float)TILE_SIZE/2f),null);
			} else if(lt.game.gameover) {
				c.drawBitmap(gameoverBitmap, W/2-gameoverBitmap.getWidth()/2, H/2-gameoverBitmap.getHeight()/2,null);
			}
			
		}
	}
	
	public void loadImages() {
		try {
			spriteSheet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tiles),256,64,false);
			pausedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.paused), (int)(256*scaleFactor), (int)(64*scaleFactor), true);
			gameoverBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gameover), (int)(256*scaleFactor), (int)(64*scaleFactor), true);
			hiscoreBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hiscore), (int)(256*scaleFactor), (int)(64*scaleFactor), true);
		} catch(Exception e) {
			Log.d("Lolteroids",e.getMessage());
		}
	}

	private Handler hndl = new Handler() {
		public void handleMessage(Message msg) {
			invalidate();
		}
	};
	
	public void start() {
		new Thread(this).start();
	}
	
	public void run() {
		while(isRunning) {
			hndl.sendEmptyMessage(0);
			try {Thread.sleep(drawSpeed);} catch(Exception e) {}
		}
	}

}
