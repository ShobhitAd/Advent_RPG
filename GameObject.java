import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * 
 * @category Basic
 * 
 * Used to create different type of characters
 *
 */

public class GameObject implements ActionListener {
	//Position and Size
	public double X=0, Y=0;
	public double W=0,H=0;
	//Frames
	public int FX=0,FY=0;

	//Where to find character sprites
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Objects\\";
	
	//Animate
	boolean ISACTIVE=false;

	
	//Used for delay in framechange()
	int COUNTER=0;

	//Max frames
	int MX=0,MY=0;

	//Timer
	Timer tmrANIM= new Timer(1,this);
	
	//Draw growthfactor
	double GROWTHFACTOR=1;
	
	//Sprite
	Image imgOBJ;
	
	//Inventory(for chests)
	ItemList INVEN= new ItemList();
	
	//Object class
	String CLASS="Object"; 
	
	AudioEffects audEFFECTS= new AudioEffects(false);
	
	String TITLE,DESCRIPT;
	
	/**
	 * Constructor to create a character
	 * @param x X Position 
	 * @param y Y Position
	 * @param w Width of Image
	 * @param h	Height of Image
	 * @param FileN Name of image file
	 * @param S Max number of frames
	 */
	public GameObject(double x,double y,double w,double h,String FileN,int mx,String Cl){
		//Set position and size
		X=x;Y=y;
		W=w;H=h;
		
		//Get Image
		
		try{
			if(isJar()){
				//Character
				imgOBJ=getImage("/Graphics/Objects/",FileN);	
			}else{
				//Character
				imgOBJ=ImageIO.read(Paths.get(DIRECT+FileN).toFile());
			}
			
		}catch(Exception e){}
		
		//Set max
		MX=mx;
	
		//Set class
		CLASS=Cl;

		//Initialize objects depending on class
		switch(CLASS.toLowerCase()){
		
		case "fountain":
			//ACTIVATE
			ISACTIVE=true;
			break;
			
		case "forge":
			//ACTIVATE
			ISACTIVE=true;
			break;	
			
		case "cauldron":
			//ACTIVATE
			ISACTIVE=true;
			break;	
		
		case "torch":
			ISACTIVE=true;
			break;
		case "chest":
			//ACTIVATE
			INVEN.OPEN_INVEN=true;
			INVEN.Type="Chest";
			break;	
	
		}
		
		
		tmrANIM.start();
	}
	
	public void setTipMessage(String Tit,String Des){
		TITLE=Tit;
		DESCRIPT=Des;
	}
	
	
	/**
	 * Used to generate items in a chest
	 * @param Lvl
	 * @param Num
	 */
	public void GenerateItems(int Lvl,int Num){
		for(int i=0;i<Num;i++){
			int ItemType=(int)Math.round(Math.random()*10);
			
			switch (ItemType){
				case 0://WEAPON
					int Opt=(int)Math.round(Math.random()*3);
					
					
					if(Opt==0){
						if(Lvl<=5){
							INVEN.addItem("Silver Dagger", 250,1, "Dagger_3.png","Meele",new String[]{"Damage=10","Attack Speed=1"});
						}
						else if(Lvl>5 && Lvl<=8){
							INVEN.addItem("Steel Dagger", 450,1, "Dagger_5.png","Meele",new String[]{"Damage=35","Attack Speed=1"});
						}
						else if(Lvl>8 && Lvl<=10){
							INVEN.addItem("Assassin blade", 750,1, "Dagger_6.png","Meele",new String[]{"Damage=45","Attack Speed=1"});
						}
						else if(Lvl>10 && Lvl<=13){
							INVEN.addItem("Serpent Dagger", 5000,1, "Dagger_2.png","Meele",new String[]{"Damage=50","Attack Speed=1"});
						}
						else if(Lvl>13){
							INVEN.addItem("Dagger of Time", 5050,1, "Dagger_1.png","Meele",new String[]{"Damage=75","Attack Speed=1"});
						}
					}
					
					if(Opt==1){
						if(Lvl<=5){
							INVEN.addItem("Iron Sword", 50,1, "Sword_2.png","Meele",new String[]{"Damage=15","Attack Speed=0.65"});
							INVEN.addItem("Iron Scimitar", 150,1, "Sword_7.png","Meele",new String[]{"Damage=15","Attack Speed=0.75"});
						}
						else if(Lvl>5 && Lvl<=8){
							INVEN.addItem("Silver Sword", 100,1, "Sword_5.png","Meele",new String[]{"Damage=20","Attack Speed=0.5"});
						
						}
						else if(Lvl>8 && Lvl<=10){						
							INVEN.addItem("Steel Sword", 450,1, "Sword_1.png","Meele",new String[]{"Damage=30","Attack Speed=0.65"});
							INVEN.addItem("Steel Scimitar", 500,1, "Sword_4.png","Meele",new String[]{"Damage=50","Attack Speed=0.75"});
						}
						else if(Lvl>10 && Lvl<=13){
							INVEN.addItem("Steel Katana", 450,1, "Sword_6.png","Meele",new String[]{"Damage=40","Attack Speed=0.95"});
						}
						else if(Lvl>13 && Lvl<=16){							
							INVEN.addItem("Butcher's Sword", 5000,1, "Sword_3.png","Meele",new String[]{"Damage=100","Attack Speed=0.55"});
						}
						else if(Lvl>16){						
							INVEN.addItem("Blood Thirster", 6000,1, "Sword_8.png","Meele",new String[]{"Damage=200","Attack Speed=0.75"});
						}
					}
						/*
					INVENTORY.addItem("Iron Hammer", 150,1, "Hammer_1.png","Meele",new String[]{"Damage=40","Attack Speed=0.15"});
					INVENTORY.addItem("Steel Hammer", 450,1, "Hammer_0.png","Meele",new String[]{"Damage=60","Attack Speed=0.15"});
					INVENTORY.addItem("Iron Mace", 50,1, "Mace_1.png","Meele",new String[]{"Damage=15","Attack Speed=0.5"});
					*/
					
					break;
				case 1://ARMOR
					
					//INVEN.addItem("Steel Helmet", 250,1, "Head_1.png","H_armor",new String[]{"Armor=20"});
					/*
					INVENTORY.addItem("Iron Helmet", 150,1, "Head_2.png","H_armor",new String[]{"Armor=15"});
					INVENTORY.addItem("Iron Horn Helmet", 350,1, "Head_6.png","H_armor",new String[]{"Armor=50","Magic Resist=0"});
					INVENTORY.addItem("Steel Horn Helmet", 500,1, "Head_7.png","H_armor",new String[]{"Armor=80","Magic Resist=0"});
					INVENTORY.addItem("Skysteel Helmet", 4000,1, "Head_4.png","H_armor",new String[]{"Armor=60","Magic Resist=25"});
					 */
					break;
				case 2://SHIELD
					//INVEN.addItem("Wooden Shield", 50,1, "Shield_1.png","Shield",new String[]{"Armor=10","Magic Resist=0"});
					break;
				case 3://
					break;
				default://POTION
					
					int Opt2=(int)Math.round(Math.random()*2);
					
					if(Opt2==0){
						INVEN.addItem("Health Potion", 25,1, "Health Pot.png","Potion",new String[]{"Points=50"});
					}else{
						INVEN.addItem("Mana Potion", 25,1, "Mana Pot.png","Potion",new String[]{"Points=50"});
					}
					break;
					
			}
			
			
		}
	}
	public void Move(int dx,int dy){
		
		//Move
		X+=dx;
		Y+=dy;
			
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
		g.drawImage(imgOBJ, (int)(X+Dx), (int)(Y+Dy), (int)(X+Dx+W*G), (int)(Y+Dy+H*G), (int)(W*FX), (int)(H*FY), (int)(W*FX+W), (int)(H*FY+H), null);
		GROWTHFACTOR=G;
	}
	
	/**
	 * Frame Change in X direction
	 * @param MAX last frame index
	 * @param Delay Delay between frame change
	 * @param Dir moves back or forward through frames
	 */
	public void FrameChangeX(int MAX,int Delay,int Dir){
		COUNTER++;
		
		if(COUNTER>Delay){
			//Change Frame
			FX+=Dir;
			
			//Reset when frame number exceeds bounds
			if(FX<0){FX=MAX;}
			if(FX>MAX){FX=0;}
			
			//Reset
			COUNTER=0;
		}
		
		
	}
	/**
	 * Run Animation
	 */
	public void actionPerformed(ActionEvent event){
		if(ISACTIVE){
			FrameChangeX(MX,5,1);
		}
	}
	
	/**
	 * Check if this x and y coordinate is within the component
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public boolean Contains(double x,double y){
	//Check collision	
		if((x>X)&&(x<X+W*GROWTHFACTOR)){
			if((y>Y)&&(y<Y+H*GROWTHFACTOR)){
				return true;
			}
		}
		
		return false;
	}
	
	public void Interact(GameCharacter Charac,clsGame ABC){
switch(CLASS.toLowerCase()){
		
		case "fountain":
			if(Charac.GOLD>0)Charac.GOLD--;
			break;
			
		case "forge":
			
			break;
			
		case "cauldron":
			
			break;
			
		case "bed":
			
			Charac.BASIC_PROCESS.setTask("Sleeping", 3);
			
			Charac.HEALTH=(int)Charac.MAX_HEALTH;
			Charac.MANA=(int)Charac.MAX_MANA;
			Charac.X=X+5;Charac.Y=Y+5;
			Charac.FX=0;Charac.FY=0;
			
			
					
			//Charac.X-=30;
			break;
		
		case "prison door":
			if(FX!=0) return;
			
			if(Charac.INVENTORY.hasItem("Key")){
				FX=1;
				Charac.removeCollisionObject(this);
				Charac.INVENTORY.removeItem("Key", 1);
				audEFFECTS.playAudio("MetalDoor.wav");
			}else{
				if(Charac.CLASS.equals("Player")){
					ABC.ALERT.Draw("Its Locked", "The door is locked you\n need a key to unlock it ");
				}
			}
			break;
		case "inscription":
		
			ABC.ALERT.Draw(TITLE,DESCRIPT);
			
			break;
			
			
		}
	}
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "Gameobject");
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
