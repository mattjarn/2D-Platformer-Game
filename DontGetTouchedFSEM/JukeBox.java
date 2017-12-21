package DontGetTouchedFSEM;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class JukeBox {
	
	private static HashMap<String, Clip> tracks;

	public JukeBox(){
		tracks = new HashMap<>();
	}

	/**
	 * This function loads an audio clip into the Jukebox
	 * by accepting a string for the location and storing it in
	 * the hashmap with the specified key so it can be accessed later
	 * @param location
	 * @param key
	 */
	public void load(String location, String key) {
		if(tracks.get(key) != null) return;
		Clip clip;
		try {
			InputStream in = JukeBox.class.getResourceAsStream(location);
			InputStream bin = new BufferedInputStream(in);
			AudioInputStream ais = AudioSystem.getAudioInputStream(bin);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(),
				false
			);
			AudioInputStream stream = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(stream);
			tracks.put(key, clip);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Accepts the name of the song to play and looks it up in
	 * the hashmap. Then it checks if is running, if it is it stops it.
	 * Then it sets the start point to frame 0 and begins playing the clip
	 * @param key
	 */
	public void play(String key) {
		Clip c = tracks.get(key);
		if(c == null) return;
		if(c.isRunning()) c.stop();
		c.setFramePosition(0);
		while(!c.isRunning()) c.start();
	}

	/**
	 * This method stops the track if it is playing
	 * @param key
	 */
	public void stop(String key) {
		if(tracks.get(key) == null) return;
		if(tracks.get(key).isRunning()) tracks.get(key).stop();
	}

	/**
	 * This method resumes the track
	 * @param key
	 */
	public void resume(String key) {
		if(tracks.get(key).isRunning()) return; // if its already playing, dont do anything
		tracks.get(key).start(); //otherwise start track s
	}

	/**
	 * This method resumes a loop
	 * @param key
	 */
	public void resumeLoop(String key) {
		Clip c = tracks.get(key);
		if(c == null) return;
		c.loop(Clip.LOOP_CONTINUOUSLY);
	}

	/**
	 * This method creates a loop at the starting spot,
	 * and then continuously loops through the track
	 * @param key
	 */
	public void loop(String key) {
		Clip c = tracks.get(key);
		if(c == null) return;
		if(c.isRunning()) c.stop();
		c.setFramePosition(0);
		while(!c.isRunning())
			c.start();
	}

	/**
	 * Allows the user to set the volume of a track by looking up the key and then
	 * using a floating point number. Negative numbers make the track quieter
	 * @param key
	 * @param volume
	 */
	public void setVolume(String key, float volume) { //negative values make it quieter
		Clip c = tracks.get(key);
		if(c == null) return;
		FloatControl vol = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
		vol.setValue(volume);
	}
	
}