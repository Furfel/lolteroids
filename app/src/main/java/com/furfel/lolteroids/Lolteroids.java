package com.furfel.lolteroids;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.*;

public class Lolteroids extends Activity {

	public Point centralPoint = new Point();
	public GameDerp game;
	public Display display;
	public float currentRot=0.0f;
	
	InterstitialAd ia;
	
	public static final String INTERSTITIAL="<key>", BANNER="";
	
	AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
	Typeface hs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		centralPoint.set(dm.widthPixels/2, dm.heightPixels/2);
		display = new Display(this,this,dm.widthPixels,dm.heightPixels);
		setContentView(R.layout.activity_lolteroids);

		ia = new InterstitialAd(this);
		ia.setAdUnitId(INTERSTITIAL);
		ia.setAdListener(al);
		AdRequest ar = new AdRequest.Builder()
		.addKeyword("doge").addKeyword("awesome").addKeyword("meme").addKeyword("lol")
		.addKeyword("fun").addKeyword("funny").addKeyword("retro").addKeyword("gaming")
		.addKeyword("asteroids").addKeyword("nintendo").addKeyword("nes").addKeyword("atari")
		.addKeyword("amiga").addKeyword("zx").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
		ia.loadAd(ar);
		new Thread(new Runnable() {public void run() {display.loadImages();}}).start();
		new DataLoader().execute();
		game = new GameDerp(this);
		game.start();
	}

	public AdListener al = new AdListener() {

		public void onAdLoaded() {
			super.onAdLoaded();
			ia.show();
		}
	};
	
	private boolean adReady=false;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.lolteroids, menu);
		return true;
	}
	
	public boolean onKeyDown(int c, KeyEvent e) {
		switch(e.getKeyCode()) {
		
			case KeyEvent.KEYCODE_BACK: {
					if(currentScreen==SCREEN_GAME) {
						if(game.paused || game.gameover) {
							currentScreen=SCREEN_MENU; setContentView(R.layout.menu_layout);

							AdRequest ar = new AdRequest.Builder()
								.addKeyword("doge").addKeyword("awesome").addKeyword("meme").addKeyword("lol").addKeyword("fun").addKeyword("funny").addKeyword("retro").addKeyword("gaming").addKeyword("asteroids").addKeyword("nintendo").addKeyword("nes").addKeyword("atari").addKeyword("amiga").addKeyword("zx")
									.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
							AdView adv = (AdView) findViewById(R.id.adView);
							adv.loadAd(ar);
							ImageView iv = (ImageView) findViewById(R.id.imageView1);	iv.startAnimation(anim);
							TextView tv = (TextView) findViewById(R.id.textView1); tv.setTypeface(hs); tv.setText("HI-SCORE: "+game.hiScore);
							}
						else {display.paused=true; game.paused=true; AudioDerp.paused=true;}
					} else {
						finish();
					}
				} return true;

			default: super.onKeyDown(c, e);
		}
		return false;
	}
	
	public void onDestroy() {
		super.onDestroy();
		game.isRunning=false;
		AudioDerp.stopSounds();
		display.isRunning=false;
	}
	
	private int currentScreen=0;
	public static final int SCREEN_MENU=0;
	public static final int SCREEN_GAME=1;
	
	public boolean onTouchEvent(MotionEvent evt) {
		switch(evt.getAction()) {
		
			case MotionEvent.ACTION_DOWN: {
				if(currentScreen==SCREEN_GAME) {
					
					if(!game.paused && !game.gameover) {	
						float dY = evt.getY() - (float)centralPoint.y, dX = evt.getX() - (float)centralPoint.x;
						game.shoot((float)Math.atan2(dY, dX));
						currentRot = (float)Math.toDegrees(Math.atan2(dY, dX));
						int localrot=(int)(((float)Math.toDegrees(Math.atan2(dY, dX))+90.0f)/24.0f);
						if(localrot<0) localrot=15+localrot;
						GameDerp.destPlayerRot=localrot;
						Log.d("Lolteroids",""+localrot);
					} else if(game.gameover){
						currentScreen=SCREEN_MENU;
						setContentView(R.layout.menu_layout);
						AdRequest ar = new AdRequest.Builder()
						.addKeyword("doge").addKeyword("awesome").addKeyword("meme").addKeyword("lol")
						.addKeyword("fun").addKeyword("funny").addKeyword("retro").addKeyword("gaming")
						.addKeyword("asteroids").addKeyword("nintendo").addKeyword("nes").addKeyword("atari")
						.addKeyword("amiga").addKeyword("zx").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
						AdView adv = (AdView) findViewById(R.id.adView);
						adv.loadAd(ar);
						ImageView iv = (ImageView) findViewById(R.id.imageView1);
						iv.startAnimation(anim);
						TextView tv = (TextView) findViewById(R.id.textView1);
						tv.setTypeface(hs);
						tv.setText("HI-SCORE: "+game.hiScore);
					} else {
						game.paused=false;
						AudioDerp.paused=false;
						display.paused=false;
					}
				} else if(currentScreen==SCREEN_MENU) {
					
				}
				
			} break;
		
		}
		return true;
	}

	public void onPause() {
		super.onPause();
		AudioDerp.stopSounds();
		game.paused=true;
		display.paused=true;
		display.drawSpeed=100;
		if(dataSaved) {
		dataSaved=false;
		new Thread(dataSaver).start();
		}
	}
	
	private Runnable dataSaver = new Runnable(){
		public void run() {
			saveData();
		}
	};
	
	public void onResume() {
		super.onResume();
		AudioDerp.resumeSounds();
		display.drawSpeed=25;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
		if(v.getId()==R.id.menu_layout_id) {
			setContentView(display);
			game.newGame();
			currentScreen=SCREEN_GAME;
			game.paused=false;
			display.paused=false;
			AudioDerp.paused=false;
			AudioDerp.playSound(2);
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			display.start();
			setContentView(R.layout.menu_layout);
			AdRequest ar = new AdRequest.Builder()
					.addKeyword("doge").addKeyword("awesome").addKeyword("meme").addKeyword("lol")
					.addKeyword("fun").addKeyword("funny").addKeyword("retro").addKeyword("gaming")
					.addKeyword("asteroids").addKeyword("nintendo").addKeyword("nes").addKeyword("atari")
					.addKeyword("amiga").addKeyword("zx").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
			AdView adv = (AdView) findViewById(R.id.adView);
			adv.loadAd(ar);
			ImageView iv = (ImageView) findViewById(R.id.imageView1);
			anim.setDuration(750);
			anim.setStartOffset(20);
			anim.setInterpolator(new AnticipateInterpolator(1.0f));
			anim.setRepeatMode(Animation.REVERSE);
			anim.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(anim);
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setTypeface(hs);
			tv.setText("HI-SCORE: "+game.hiScore);
		}
	}; 
	
	public class DataLoader extends AsyncTask<Void, Integer, Integer> {

		protected Integer doInBackground(Void... arg0) {
			AudioDerp.initSounds(Lolteroids.this);
			loadData();
			hs = Typeface.createFromAsset(getAssets(), "fonts/lolteroids.ttf");
			while(!AudioDerp.loaded) {try{Thread.sleep(100);}catch(Exception e) {}}
			int q=0;
			while(!adReady && q<100) {try{Thread.sleep(100); q++;}catch(Exception e){}}
			return null;
		}
		
		protected void onPostExecute(Integer result) {
			handler.sendEmptyMessage(0);
		}
		
	};

	private static final String KEY="<key>";

	public void loadData() {
		String key=KEY; int keyp=0; Byte xbuf,kbuf,buf;
		File chk = getFileStreamPath("hiscores");
		if(chk.exists()){
		try
		{
			InputStream is = openFileInput("hiscores");
			DataInputStream dis = new DataInputStream(is);
			FileOutputStream fos = new FileOutputStream(new File(getCacheDir(),"tmpscr"));
			DataOutputStream dos = new DataOutputStream(fos);
			while(dis.available()>0)
			{
				buf=dis.readByte();
				if(keyp<key.length()-1) keyp++; else keyp=0;
				kbuf=(byte)key.charAt(keyp);
				xbuf=(byte) (kbuf^buf);
				dos.writeByte(xbuf);
			}
			dis.close(); dos.close();
			FileInputStream fis = new FileInputStream(new File(getCacheDir(),"tmpscr"));
			dis = new DataInputStream(fis);
			byte ver = dis.readByte(); //VERSION VERIFICATION
			if(ver==1) {
			game.hiScore=dis.readInt();
			GameDerp.totalPlays=dis.readLong();
			GameDerp.totalScore=dis.readLong();
			GameDerp.bulletsShot=dis.readLong();
			GameDerp.particlesCreated=dis.readLong();
			for(int i=0;i<GameDerp.lolteroidsDestroyed.length;i++)
				GameDerp.lolteroidsDestroyed[i] = dis.readLong();
			} else {Toast.makeText(this, "Incompatible save version. Data was not loaded.", Toast.LENGTH_SHORT).show();}
			dis.close();
			File delcache=new File(getCacheDir(),"tmpscr");
			if(delcache!=null) delcache.delete();
		}
		catch (IOException e) {}
		}
	}
	
	private byte CURRENTVER=1;
	
	public boolean dataSaved=true;
	
	public void saveData() {
		String key=KEY; int keyp=0; Byte xbuf,kbuf,buf;
		try{
			FileOutputStream fos = new FileOutputStream(new File(getCacheDir(),"tmpscr"));
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeByte(CURRENTVER);
			dos.writeInt(game.hiScore);
			dos.writeLong(GameDerp.totalPlays);
			dos.writeLong(GameDerp.totalScore);
			dos.writeLong(GameDerp.bulletsShot);
			dos.writeLong(GameDerp.particlesCreated);
			for(int i=0;i<GameDerp.lolteroidsDestroyed.length;i++)
				dos.writeLong(GameDerp.lolteroidsDestroyed[i]);
			dos.close();
			FileInputStream fis = new FileInputStream(new File(getCacheDir(),"tmpscr"));
			DataInputStream dis = new DataInputStream(fis);
			OutputStream os = openFileOutput("hiscores", Context.MODE_PRIVATE);
			dos = new DataOutputStream(os);
			while(dis.available()>0)
			{
				buf=dis.readByte();
				if(keyp<key.length()-1) keyp++; else keyp=0;
				kbuf=(byte)key.charAt(keyp);
				xbuf=(byte) (kbuf^buf);
				dos.writeByte(xbuf);
			}
			dis.close(); dos.close();
			File delcache=new File(getCacheDir(),"tmpscr");
			if(delcache!=null) delcache.delete();
		}
		catch(IOException e) {}
		dataSaved=true;
	}
	
}
