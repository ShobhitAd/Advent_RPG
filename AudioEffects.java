import java.io.*;
import java.nio.file.*;

import javax.sound.sampled.*;

/**
 * @category Add On
 * Used to add SFX and music
 *
 */
public class AudioEffects {

	//Audio Player object
	Clip AUDIO_CLIP;
	
	//Loop audio
	boolean blnLOOP;
	
	//where to find the audio
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Audio\\";
	
	/**
	 * Constructor for audio
	 * @param Loop set if loop is true or false
	 */
	public AudioEffects(boolean Loop){
		blnLOOP=Loop;
	}
	
	/**
	 * Play audio
	 * @param Fname File name
	 */
	public void playAudio(String Fname){

		try {
			//Load audio file
			File soundFile=null;
			if(isJar()){
				soundFile=new File(getClass().getResource("/Graphics/Audio/"+Fname).toURI());
			}else{
				soundFile=Paths.get(DIRECT+Fname).toFile();
			}
			
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			  
			//Initialize
			AUDIO_CLIP = AudioSystem.getClip();
			
			//Open the audio file
			AUDIO_CLIP.open(audioInputStream);
			
			//Set start position
			AUDIO_CLIP.setFramePosition(0);
			
			//Loop the audio
			if(blnLOOP){AUDIO_CLIP.loop(Clip.LOOP_CONTINUOUSLY);}
			  
			//Start the audio
			AUDIO_CLIP.start();
		      
		} catch (Exception e) {System.out.println("Can't play "+Fname);}
		
	}
	
	/**
	 * Stop the audio
	 */
	public void Stop(){
		if(AUDIO_CLIP!=null)AUDIO_CLIP.stop();
		AUDIO_CLIP=null;
	}
	public boolean isJar(){
		Path pth= Paths.get(System.getProperty("user.dir")+"/Advent.jar");
		
		
		if(pth.toFile().exists())return true;
		return false;
	}
}
