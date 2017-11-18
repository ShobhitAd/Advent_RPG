import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.imageio.*;

import java.nio.file.*;
import java.util.Vector;
/**
 * 
 * @category Basic
 * Used to create different types of meele attacks
 * 
 *
 */
public class MeeleAttack implements ActionListener {
	
	//Position and Size
	public double X=0, Y=0;
	public double ANGLE=0,MAX_ANGLE=180;
	Point CHARACTER=new Point();
    
	//Where to find character sprites
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Items\\";
	
	//Animate
	boolean ISACTIVE=false;
	
	// Timer
	Timer tmrANIM= new Timer(1,this);
	
	//Growthfactor of the weapon
	double GROWTHFACTOR=1;
	
	//Sprite
	Image imgWEAPON;
	
	String DIR="",NAME="";
	
	//Characters with which the weapon can collide
	Vector<GameCharacter> CollChars= new Vector<GameCharacter> ();
	//The wielder of the weapon
	GameCharacter WIELDER;
	//Weapon Stats
	Stats STATS= new Stats();

	
	AudioEffects audSLASH= new AudioEffects(false);

	/**
	 * Constructor to create meele attack 
	 * @param x initial x
	 * @param y initial y
	 * @param FileN Where to find Image
	 */
	public MeeleAttack(double x,double y,String FileN,GameCharacter W){
		//Set position and size
		X=x;Y=y;
		
		//Get Image
		try{
			if(isJar()){
				imgWEAPON=getImage("/Graphics/Items/",FileN);
			}else{
				imgWEAPON=ImageIO.read(Paths.get(DIRECT+FileN).toFile());
			}
			
		}catch(Exception e){}
		
		WIELDER=W;
		
		//Start timer
		tmrANIM.start();
	}
	
		
	/**
	 * Where to draw the weapon
	 * @param g Component Graphics
	 * @param Dx X Draw Displacement
	 * @param Dy Y Draw Displacement
	 * @param G GrowthFactor
	 * @param Tx Character Center X
	 * @param Ty Character Center Y
	 */
	public void Draw(Graphics g,int Dx,int Dy,double G,double Tx, double Ty){
		//If attack is not active then exit
		if(!ISACTIVE)return;
		
		//Draw Image
			//Convert Graphics
		Graphics2D g2D=(Graphics2D)g;
			//Get required variables
				//Rotation
		double ROT_REQUIRED =Math.toRadians(ANGLE);
				//Position
		double LOC_X = imgWEAPON.getWidth(null) / 2;
		double LOC_Y = imgWEAPON.getHeight(null);
				//Character position
		CHARACTER.x=(int)(Tx);
	    CHARACTER.y=(int)(Ty);
		
	    	//Use affine transform to modify the image
	    		//Scale to size
		AffineTransform aftScale = AffineTransform.getScaleInstance(G, G);
        		//Move
		AffineTransform aftTrans = AffineTransform.getTranslateInstance((Tx+Dx-imgWEAPON.getWidth(null)/2*aftScale.getScaleX()),(Ty+Dy-imgWEAPON.getHeight(null)*aftScale.getScaleY()));
        		//Rotate
		AffineTransform aftRot = AffineTransform.getRotateInstance(ROT_REQUIRED, LOC_X, LOC_Y);
        
			//Concat transformations
        aftTrans.concatenate(aftScale);
        aftTrans.concatenate(aftRot);
        aftTrans.translate(0, -20);
       
       //Draw modified image
       g2D.drawImage(imgWEAPON, aftTrans, null);
       GROWTHFACTOR=G;
	}
	
	/**
	 * Add meele attack collision to a character
	 * @param Charac Game Character
	 */
	public void addCollChar(GameCharacter Charac){
		CollChars.add(Charac);
		Charac.COLL_LISTENER=true;
	}

	/**
	 * Add meele attack collision to multiple characters
	 * @param Charac Array of Game Character
	 */
	public void addCollChar(GameCharacter[] Charac){
		for(int i=0;i<Charac.length;i++){
			CollChars.add(Charac[i]);
			Charac[i].COLL_LISTENER=true;

		}
	}
	
	
	/**
	 * Detect attack collision with a character
	 * @param Charac Game Character
	 * @return if colliding
	 */
	public boolean CharacterCollision(GameCharacter Charac){
		
		//X and y distance
		double DistX=Charac.X+Charac.W/2-CHARACTER.x;
		double DistY=Charac.Y+Charac.H/2-CHARACTER.y;
		
		//Check if direction is correct to do damage
		if(DistX>0 && DIR.equalsIgnoreCase("left"))return false;
		if(DistX<0 && DIR.equalsIgnoreCase("right"))return false;
		if(DistY>0 && DIR.equalsIgnoreCase("up"))return false;
		if(DistY<0 && DIR.equalsIgnoreCase("down"))return false;

		//Check Distance
		if(Math.sqrt(Math.pow(DistX,2)+Math.pow(DistY,2))<=imgWEAPON.getHeight(null)*GROWTHFACTOR+Charac.W*Charac.GROWTHFACTOR/2+15){
			//Do Damage
			
			Charac.Damage(STATS.DAMAGE+WIELDER.STATS.DAMAGE,1);
			audSLASH.playAudio("slash.wav");
		}
		
		return false;
		
	}
	/**
	 * Check Collision
	 * @return if colliding
	 */
	public boolean Collision(){
		boolean blnCol=false;
		
		//Game character Collision
		for(int i=0;i<CollChars.size();i++){
			blnCol=blnCol || CharacterCollision(CollChars.elementAt(i));
		}

		return blnCol;
	
	}
	
	/**
	 * Attack Animation 
	 */
	public void actionPerformed(ActionEvent event){
		if(ISACTIVE){
			//Increase the angle
			ANGLE+=25*STATS.ATTACK_SPEED;
			//Exit if max angle is reached
			if (ANGLE>=MAX_ANGLE){
				ISACTIVE=false;
				Collision();
				
			}
		}
	}

	/**
	 * Set up position for attack
	 * @param Dir Attack direction
	 */
	public void PAttack(String Dir){
		//If no weapon is equipped
		if(ISACTIVE ||imgWEAPON==null)return;
		
		switch(Dir.toLowerCase()){
		
			case "up":
				
				ANGLE=-90;
				MAX_ANGLE=90;
				break;
			
			case "down":
		
				ANGLE=90;
				MAX_ANGLE=275;
				break;
				
			case "left":
				
				ANGLE=180;
				MAX_ANGLE=360;
				break;
				
			case "right":
				
				ANGLE=0;
				MAX_ANGLE=180;
				break;
				
		}

		//Activate	
		DIR=Dir;
		ISACTIVE=true;
                    
	}
	 
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "MeeleAttack");
		try {
		    BufferedImage bg = ImageIO.read(getClass().getResource(Direct+FName));
		    return bg;
		    
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, getClass().toString());
		}
		return null;
	}
	public boolean isJar(){
		Path pth= Paths.get(System.getProperty("user.dir")+"/Advent.jar");
		

		if(pth.toFile().exists())return true;
		return false;
	}
	

	
	
}
