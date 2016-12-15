package com.furfel.lolteroids;

import com.furfel.lolteroids.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class AudioDerp {

	private static SoundPool pool;
	private static Context context;
	
	public static boolean paused=false,loaded=false;
	
	public static final int OMNOM=0;
	public static final int ACHI=1;
	
	public static int[] streams;
	public static final int[] list = {
		R.raw.shoot,
		R.raw.boom,
		R.raw.startgame,
		R.raw.hurt,
		R.raw.gameover
	};
	
	public void AudioDerp() {
	}
	
	static OnLoadCompleteListener olcl = new OnLoadCompleteListener() {
		public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
			if(sampleId==streams[streams.length-1] && status == 0)
				loaded=true;
		}
	};
	
	public static void initSounds(Context context) {
		AudioDerp.context=context;
		pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		streams = new int[list.length];
		pool.setOnLoadCompleteListener(olcl);
		for(int i=0;i<list.length;i++)
			streams[i] = pool.load(context, list[i], 1);
	}
	
	public static void playSound(int sound) {
		AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax;
	    if(!paused) pool.play(streams[sound], volume, volume, 1, 0, 1f); 
	}
	
	public static void stopSounds() {
		if(pool!=null)
		pool.autoPause();
		paused=true;
	}
	
	public static void resumeSounds() {
		if(pool!=null)
		pool.autoResume();
		paused=false;
	}
}
