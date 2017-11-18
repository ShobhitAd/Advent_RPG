import java.awt.*;
import java.nio.file.*;

import javax.imageio.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.util.Vector;

/**
 * 
 * @category Basic
 * 
 * Used to create different types of magic attacks
 *
 */
 enum MagicType1{
	RIGHT,UP,LEFT,DOWN
}
 enum MagicType2{
	UP,RIGHT,LEFT,DOWN
}
 
public class MagicAttack implements ActionListener {
	//Position and Size
	public double X=0, Y=0;
	public double W=0,H=0;
	//Frames
	public int FX=0,FY=0;

	//Where to find character sprites
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Magic\\";
	
	//Animate
	boolean ISACTIVE=false;

	
	//Used for delay in framechange()
	int COUNTER=0;
	
	//max frames
	int MX=0,MY=0;
	
	//displacement amount
	int dX=15,dY=0;
	//Which frame to start at
	int Start=0;
	// Timer
	Timer tmrAnim= new Timer(1,this);
	
	//
	double GROWTHFACTOR=1;
	//Sprite
	Image imgATTACK;
	
	int TYPE=1;
	
	String NAME="";
	
	Vector<GameCharacter> CHARAC= new Vector<GameCharacter>();
	Vector<GridMap> MAP= new Vector<GridMap>();
	Vector<GameObject> OBJECTS= new Vector<GameObject>();
	Vector<Building> BUILD= new Vector<Building>();
	Stats STATS= new Stats();

	GameCharacter CASTER;
	
	AudioEffects audHIT= new AudioEffects(false); 
	
	/**
	 * Constructor to create a magic attack
	 * @param x X Position 
	 * @param y Y Position
	 * @param w Width of Image
	 * @param h	Height of Image
	 * @param FileN Name of image file
	 * @param S Max number of frames
	 */
	
	public MagicAttack(double x,double y,double w,double h,String FileN,int mx,int Ty,int St,String N){
		//Set position and size
		X=x;Y=y;
		W=w;H=h;
		
		//Get Image
		try{
			if(isJar()){
				imgATTACK=getImage("/Graphics/Magic/",FileN);
			}else{
				imgATTACK=ImageIO.read(Paths.get(DIRECT+FileN).toFile());
			}
		}catch(Exception e){}
		
		//Set max
		MX=mx;
		
		FX=0;
		
		TYPE=Ty;
		Start=St;
		
		NAME=N;
		tmrAnim.start();
	}
	
	public void Move(int dx,int dy){
		
		//Move
		X+=dx;
		Y+=dy;
		
		//Assign FY according to direction
		switch(TYPE){
			//Sprite Type 1
			case 1:
				if(dx>0){
					//RIGHT
					FY=MagicType1.RIGHT.ordinal();
				}
				else if(dx<0){
					//LEFT
					FY=MagicType1.LEFT.ordinal();
				}
				else if(dy<0){
					//UP
					FY=MagicType1.UP.ordinal();
				}
				else if(dy>0){
					//DOWN
					FY=MagicType1.DOWN.ordinal();
				}

				break;
			case 2:
				if(dx>0){
					//RIGHT
					FY=MagicType2.RIGHT.ordinal();
				}
				else if(dx<0){
					//LEFT
					FY=MagicType2.LEFT.ordinal();
				}
				else if(dy<0){
					//UP
					FY=MagicType2.UP.ordinal();
				}
				else if(dy>0){
					//DOWN
					FY=MagicType2.DOWN.ordinal();
				}

				break;		
		}
			
	}
		

	/**
	 * Draw character in game window
	 * @param g Graphics
	 * @param Dx X Displacement
	 * @param Dy Y Displacement
	 * @param G Growth factor
	 */
	public void Draw(Graphics g,int Dx,int Dy,double G){
		//Draw Image
		g.drawImage(imgATTACK, (int)(X+Dx), (int)(Y+Dy), (int)(X+Dx+W*G), (int)(Y+Dy+H*G), (int)(W*FX), (int)(H*FY), (int)(W*FX+W), (int)(H*FY+H), null);
		GROWTHFACTOR=G;
	}
	
	/**
	 * Frame Change in X direction
	 * @param MAX last frame index
	 * @param Delay Delay between frame change
	 * @param Dir moves back or forward through frames
	 */
	public void FrameChangeX(int MAX,int Delay,int Dir,int Start){
		COUNTER++;
		
		if(COUNTER>Delay){
			//Change Frame
			FX+=Dir;
			
			//Reset when frame number exceeds bounds
			if(FX<0){FX=MAX;}
			if(FX>MAX){FX=Start;}
			
			//Reset
			COUNTER=0;
		}
		
		
	}
	/**
	 * If the character dies then remove magic collision listeners
	 * @param Charac Game Character
	 */
	public void removeCollChar(GameCharacter Charac){
		CHARAC.remove(Charac);
	}
	/**
	 * Add Magic Attack Collision to character
	 * @param Charac Game Character
	 */
	public void addCollChar(GameCharacter Charac){
		CHARAC.add(Charac);
		Charac.COLL_LISTENER=true;

	}
	
	/**
	 * Add Magic Attack Collision to multiple characters
	 * @param Charac Array of Game Characters 
	 */
	
	public void addCollChar(GameCharacter[] Charac){
		for(int i=0;i<Charac.length;i++){
			
			CHARAC.add(Charac[i]);
			Charac[i].COLL_LISTENER=true;

		}
	}
	/**
	 * Add map collision to magic attacks
	 * @param M Grid Map
	 */
	public void addColisionObject(GridMap M){
		MAP.add(M);
	}
	/**
	 * Add object collision to magic attacks
	 * @param O GameObject
	 */

	public void addColisionObject(GameObject O){
		OBJECTS.add(O);
	}

	/**
	 * Add building collision to magic attacks
	 * @param B Game building
	 */

	public void addColisionObject(Building B){
		BUILD.add(B);
	}
	/**
	 * Add map collision to magic attacks
	 * @param M Array of Grid Maps
	 */
	public void addColisionObject(GridMap[] M){
		for(int i=0;i<M.length;i++){
			MAP.add(M[i]);
		}
	}
	/**
	 * Add object collision to magic attacks
	 * @param O Array of GameObjects
	 */
	public void addColisionObject(GameObject[] O){
		for(int i=0;i<O.length;i++){
			OBJECTS.add(O[i]);
		}
	}
	
	public void removeAllColisionObjects(){
		CHARAC= new Vector<GameCharacter>();
		MAP= new Vector<GridMap>();
		OBJECTS= new Vector<GameObject>();
		BUILD= new Vector<Building>();

		
	}
	/**
	 * Allows the character to walk over various surfaces
	 * @param F Selected fill number
	 * @return whether or not the character can walk on this surface
	 */
	public boolean isValidSurface(int F){
		//List of valid surfaces
		int[] fill={0,98,106,107,117,118,119,120,121,122,123,124,125,126,127};
		
		for(int i=0;i<fill.length;i++){
			if(fill[i]==F){
				//If valid
				return true;
			}
		}
		
		//If not
		return false;
	}
	
	/**
	 * Wall Collision
	 * @param x X Position
	 * @param y Y Position
	 * @param M Map
	 * @return if character is about to collide with the wall
	 */
	public boolean WallCollision(double x, double y,GridMap M){
		//If invalid coordinate
		try{
			if(M.getBlockNum(x, y)==-1){
				//Collision check
				if(isValidSurface(M.Fill[M.getBlockNum(x+3, y+3)])){
					return false;
				}
				return true;
			}
			//Collision check
			if(isValidSurface(M.Fill[M.getBlockNum(x, y)])){
				return false;
			}
		}catch(Exception e){}
		return true;
	}
	
	/**
	 * Check for collision between the magic attack and object
	 * @param Obj Game Object
	 * @return if colliding
	 */
	public boolean ObjectCollision(GameObject Obj){
		if((X+W*GROWTHFACTOR>Obj.X) &&(X<Obj.X+Obj.W*Obj.GROWTHFACTOR)){
			if((Y+H*GROWTHFACTOR>Obj.Y) &&(Y<Obj.Y+Obj.H*Obj.GROWTHFACTOR)){
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 *Check for collision between the magic attack and character 
	 * @param Charac Game Character
	 * @return if colliding
	 */
	
	public boolean CharacterCollision(GameCharacter Charac){
		double G=(TYPE==2)?Charac.W*Charac.GROWTHFACTOR/1:0;
		double G2=(TYPE==2)?Charac.H*Charac.GROWTHFACTOR/1:0;
		
		
		if((X+W*GROWTHFACTOR-G>Charac.X) &&(X+G<Charac.X+Charac.W*Charac.GROWTHFACTOR)){
			if((Y+H*GROWTHFACTOR-G2>Charac.Y) &&(Y+G2<Charac.Y+Charac.H*Charac.GROWTHFACTOR)){
				//Do damage to character
				
				audHIT.playAudio(NAME+".wav");
				
				Charac.Damage(STATS.DAMAGE+Charac.STATS.MDAMAGE,2);
				
				switch(NAME.toLowerCase()){
					case "fireball":
						break;
					case "shock":
						break;
					case "darkness":
						break;
					case "life drain":
						CASTER.Heal(STATS.DAMAGE/2);
						break;
						
				}
				
				
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Check for collision between the magic attack and building
	 * @param Charac Building
	 * @return if colliding
	 */
	public boolean BuildingCollision(Building Charac){
		if((X+W*GROWTHFACTOR>Charac.X) &&(X<Charac.X+Charac.W*Charac.GROWTHFACTOR)){
			if((Y+H*GROWTHFACTOR>Charac.Y) &&(Y<Charac.Y+Charac.H*Charac.GROWTHFACTOR)){
				return true;
			}
		}
		
		return false;
		
	}
	/**
	 * Check all collision types 
	 * @return if colliding
	 */
	public boolean Collision(){
		boolean blnCol=false;
		//Map Collision
		for(int i=0;i<MAP.size();i++){
			blnCol=blnCol || WallCollision(X,Y,MAP.elementAt(i));
		}
		
		//Game Object Collision
		for(int i=0;i<OBJECTS.size();i++){
			blnCol=blnCol || ObjectCollision(OBJECTS.elementAt(i));
		}
		
		//Game Character Collision
		for(int i=0;i<CHARAC.size();i++){
			blnCol=blnCol || CharacterCollision(CHARAC.elementAt(i));
		}
		
		//Game Building Collision
		for(int i=0;i<BUILD.size();i++){
			blnCol=blnCol || BuildingCollision(BUILD.elementAt(i));
		}

		return blnCol;
	
	}
	/**
	 * Run Animation
	 */
	public void actionPerformed(ActionEvent event){
		if(ISACTIVE){
			if(Collision()){
				X=-5000;
				Y=-5000;
			}
			Move(dX,dY);
			FrameChangeX(MX,5,1,Start);
		}
	}
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "Magic");
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
