import java.awt.*;
import java.nio.file.*;

import javax.imageio.*;

import java.util.*;

import javax.swing.*;

import java.awt.image.*;
/**
 * 
 * @category Basic
 * 
 * Used to create different type of characters
 *
 */

//Enum depends on the Sprite type
enum Type1{
	DOWN,LEFT,RIGHT,UP
}
enum Type2{
	DOWN,LEFT,RIGHT,UP
}
enum Type3{
	UP,RIGHT,DOWN,LEFT
}
public class GameCharacter {
	//Position and Size
	public double X=0, Y=0;
	public double W=0,H=0;
	//Frames
	public int FX=0,FY=0;

	//Where to find character sprites
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Character\\";
	String DIRECT2=System.getProperty("user.dir")+"\\Graphics\\Effects\\";
	
	//Max number of frames
	int SIZE=0;
	
	//Used for delay in framechange()
	int COUNTER=0;

	//Sprite Type
	int TYPE=1;

	//Sprite
	BufferedImage imgCHARAC,imgBLOOD,imgQStart,imgQEnd;
	
	//Collision maps
	Vector<GridMap> MAP= new Vector<GridMap>();
	
	//Collision objects
	Vector<GameObject> OBJECTS= new Vector<GameObject>();
	
	//Collision characters
	Vector<GameCharacter> CHARACTERS= new Vector<GameCharacter>();

	//Collision characters
	Vector<Building> BUILD= new Vector<Building>();
	
	//Spells
	Vector<MagicAttack[]> SPELL_LIST= new Vector<MagicAttack[]>();

	
	Vector<Quest> QUEST_LIST= new Vector<Quest>();
	
	//Size
	double GROWTHFACTOR=1;
	
	//Create character inventory
	ItemList INVENTORY= new ItemList();
	
	//Character class
	String CLASS="",Cl="";
	
	//Basic stats
	double MAX_HEALTH=100,MAX_MANA=100;
	int HEALTH=(int)MAX_HEALTH,MANA=(int)MAX_MANA,GOLD=0;
	
	//Movement
	int REGEN_COUNTER=0,SPEED=1;
	
	//Used for magic attacks
	int MAGIC_COUNTER=0;
	
	//Possible AIs
	NPCAI NAI;
	EnemyAI EAI;
	
	//Restrictions
	boolean ISDEAD=false,COLL_LISTENER=false,CANMOVE=true;
	
	//List of available magic attacks
	MagicAttack[] FIREBALL= new MagicAttack[11],SHOCK= new MagicAttack[11],DARKNESS= new MagicAttack[11],LIFE_DRAIN= new MagicAttack[11];

	//Character Stats
	Stats STATS=new Stats(this);
	
	//Weapons and armor
	MeeleAttack MEELE_WEAPON= new MeeleAttack(0,0,"Sword_1.png",this);
	Image SHIELD=null;
	
	//Death sound
	AudioEffects audDEATH= new AudioEffects(false);
	
	//Progress bar used for basic tasks
	ProgressBar BASIC_PROCESS, BASIC_EFFECTS;
	
	int SPAWN_X=-1,SPAWN_Y=-1;
	
	/**
	 * Constructor to create a character
	 * @param x X Position 
	 * @param y Y Position
	 * @param w Width of Image
	 * @param h	Height of Image
	 * @param FileN Name of image file
	 * @param S Max number of frames
	 */
	public GameCharacter(double x,double y,double w,double h,String FileN,int S,int Ty,String c){
		//Set position and size
		X=x;Y=y;
		W=w;H=h;
		
		BASIC_PROCESS= new ProgressBar(new Font("Arial",36,36),this);
		BASIC_EFFECTS= new ProgressBar(new Font("Arial",36,36),this);
		//Add magic attacks
			//Fireball
		for(int i=0;i<FIREBALL.length;i++){
			FIREBALL[i]= new MagicAttack(-500,-500,39,41,"Fireball.png",3,1,2,"Fireball");
			FIREBALL[i].STATS.setStats(new String[]{"Damage=10","Points=10"});
			FIREBALL[i].CASTER=this;
		}
			//Shock
		for(int i=0;i<SHOCK.length;i++){
			SHOCK[i]= new MagicAttack(-500,-500,39,41,"Shock.png",2,1,0,"Shock");
			SHOCK[i].STATS.setStats(new String[]{"Damage=20","Points=15"});
			SHOCK[i].CASTER=this;
		}
			//Darkness
		for(int i=0;i<DARKNESS.length;i++){
			DARKNESS[i]= new MagicAttack(-500,-500,165,164,"Darkness.png",3,1,2,"Darkness");
			DARKNESS[i].STATS.setStats(new String[]{"Damage=100","Points=50"});
			DARKNESS[i].CASTER=this;
		}
			//Life Drain
		for(int i=0;i<LIFE_DRAIN.length;i++){
			LIFE_DRAIN[i]= new MagicAttack(-500,-500,32,32,"Life Drain.png",7,1,0,"Life Drain");
			LIFE_DRAIN[i].STATS.setStats(new String[]{"Damage=50","Points=25"});
			LIFE_DRAIN[i].CASTER=this;
		}
		//Get Image
		try{
			
			if(isJar()){
				//Character
				imgCHARAC=getImage("/Graphics/Character/",FileN);
				
				//Blood
				int BloodType=(int)Math.round(Math.random()*3)+1;
				imgBLOOD=getImage("/Graphics/Effects/","Dead"+BloodType+".png");
						
				//Quest
				imgQStart=getImage("/Graphics/Effects/","Quest_Start.png");
				imgQEnd=getImage("/Graphics/Effects/","Quest_Complete.png");
				
			}else{
				//Character
				imgCHARAC=ImageIO.read(Paths.get(DIRECT+FileN).toFile());
				
				//Blood
				int BloodType=(int)Math.round(Math.random()*3)+1;
				imgBLOOD=ImageIO.read(Paths.get(DIRECT2+"Dead"+BloodType+".png").toFile());
				
				//Quest
				imgQStart=ImageIO.read(Paths.get(DIRECT2+"Quest_Start.png").toFile());
				imgQEnd=ImageIO.read(Paths.get(DIRECT2+"Quest_Complete.png").toFile());
				
				
			}
			
			
		}catch(Exception e){}
		
		//Max number of frames
		SIZE=S;
		
		//Sprite type
		TYPE=Ty;
		
		//Assign class
		CLASS=c;
		Cl=c;
		
		//Add items and attacks according to class
		if(CLASS.equals("Player")){
			
			//Add items
			INVENTORY.addItem("Health Potion", 25,1, "Health Pot.png","Potion",new String[]{"Points=50"});
			INVENTORY.addItem("Rusted Dagger", 20,1, "Dagger_4.png","Meele",new String[]{"Damage=10","Attack Speed=1"});
			INVENTORY.addItem("Wooden Staff", 20,1, "Staff_1.png","Meele",new String[]{"Damage=5","Attack Speed=1"});
			
			//Equip items
			INVENTORY.ItemEffect(1, this);
			//MEELE_WEAPON=null;
			//Assign Magic
			SPELL_LIST.add(FIREBALL);
			
			//Inventory type
			INVENTORY.Type="Player";
		}
		else if(CLASS.equals("Citizen")){
			
			//Add items
			INVENTORY.addItem("Health Potion", 25,1, "Health Pot.png","Potion",new String[]{"Points=50"});
			
			//Add Ai
			NAI=new NPCAI(this);
			
			//Add Gold
			this.GOLD=(int)Math.round(Math.random()*15+5);
			
			INVENTORY.Type="Friendly";
		} 
		else if(CLASS.equals("Blacksmith")){
			
			//Add items
			INVENTORY.addItem("Silver Dagger", 250,1, "Dagger_3.png","Meele",new String[]{"Damage=10","Attack Speed=1"});
			INVENTORY.addItem("Steel Dagger", 450,1, "Dagger_5.png","Meele",new String[]{"Damage=35","Attack Speed=1"});
			//INVENTORY.addItem("Assassin blade", 750,1, "Dagger_6.png","Meele",new String[]{"Damage=45","Attack Speed=1"});
			//INVENTORY.addItem("Serpent Dagger", 5000,1, "Dagger_2.png","Meele",new String[]{"Damage=50","Attack Speed=1"});
			//INVENTORY.addItem("Dagger of Time", 5050,1, "Dagger_1.png","Meele",new String[]{"Damage=75","Attack Speed=1"});
			
			
			INVENTORY.addItem("Steel Sword", 450,1, "Sword_1.png","Meele",new String[]{"Damage=30","Attack Speed=0.65"});
			INVENTORY.addItem("Iron Sword", 50,1, "Sword_2.png","Meele",new String[]{"Damage=15","Attack Speed=0.65"});
			INVENTORY.addItem("Silver Sword", 100,1, "Sword_5.png","Meele",new String[]{"Damage=20","Attack Speed=0.5"});
			//INVENTORY.addItem("Iron Scimitar", 150,1, "Sword_7.png","Meele",new String[]{"Damage=15","Attack Speed=0.75"});
			//INVENTORY.addItem("Steel Scimitar", 500,1, "Sword_4.png","Meele",new String[]{"Damage=50","Attack Speed=0.75"});
			//INVENTORY.addItem("Steel Katana", 450,1, "Sword_6.png","Meele",new String[]{"Damage=40","Attack Speed=0.95"});
			//INVENTORY.addItem("Butcher's Sword", 5000,1, "Sword_3.png","Meele",new String[]{"Damage=100","Attack Speed=0.55"});
			//INVENTORY.addItem("Blood Thirster", 6000,1, "Sword_8.png","Meele",new String[]{"Damage=200","Attack Speed=0.75"});
			
			INVENTORY.addItem("Iron Hammer", 150,1, "Hammer_1.png","Meele",new String[]{"Damage=40","Attack Speed=0.15"});
			//INVENTORY.addItem("Steel Hammer", 450,1, "Hammer_0.png","Meele",new String[]{"Damage=60","Attack Speed=0.15"});
			//INVENTORY.addItem("Iron Mace", 50,1, "Mace_1.png","Meele",new String[]{"Damage=15","Attack Speed=0.5"});
			
			
			INVENTORY.addItem("Steel Helmet", 250,1, "Head_1.png","H_armor",new String[]{"Armor=20"});
			INVENTORY.addItem("Iron Helmet", 150,1, "Head_2.png","H_armor",new String[]{"Armor=5"});
			//INVENTORY.addItem("Iron Horn Helmet", 350,1, "Head_6.png","H_armor",new String[]{"Armor=50","Magic Resist=0"});
			//INVENTORY.addItem("Steel Horn Helmet", 500,1, "Head_7.png","H_armor",new String[]{"Armor=80","Magic Resist=0"});
			//INVENTORY.addItem("Skysteel Helmet", 4000,1, "Head_4.png","H_armor",new String[]{"Armor=60","Magic Resist=25"});
			
			INVENTORY.addItem("Iron Cuirass", 250,1, "Chest_4.png","C_armor",new String[]{"Armor=20"});
			INVENTORY.addItem("Steel Cuirass", 350,1, "Chest_1.png","C_armor",new String[]{"Armor=30"});
			//INVENTORY.addItem("Scale Cuirass", 550,1, "Chest_3.png","C_armor",new String[]{"Armor=50"});
			//INVENTORY.addItem("Dark metal Cuirass", 750,1, "Chest_6.png","C_armor",new String[]{"Armor=70"});
			//INVENTORY.addItem("Master Cuirass", 3050,1, "Chest_5.png","C_armor",new String[]{"Armor=80","Magic Resist=25"});
			//INVENTORY.addItem("Legendary Cuirass", 3050,1, "Chest_2.png","C_armor",new String[]{"Armor=100","Magic Resist=100"});
			
			INVENTORY.addItem("Wooden Shield", 50,1, "Shield_1.png","Shield",new String[]{"Armor=5","Magic Resist=0"});
			
			//Add AI
			NAI=new NPCAI(this);
			
			//Identify as trader
			INVENTORY.Type="Trader";
			
			//Set Stats
			STATS.setStats(new String[]{"Level = 5 "});
			MAX_HEALTH=200;
			HEALTH=(int)MAX_HEALTH;
		} 
		else if(CLASS.equals("Alchemist")){
			//Add Items
			INVENTORY.addItem("Health Potion", 25,50, "Health Pot.png","Potion",new String[]{"Points=50"});
			INVENTORY.addItem("Mana Potion", 20,50, "Mana Pot.png","Potion",new String[]{"Points=50"});
			INVENTORY.addItem("Speed Potion", 10,50, "Speed Pot.png","Potion",new String[]{"Points=5"});
			//INVENTORY.addItem("Invisib Potion", 50, 10, "Invisibility Pot.png","Potion",new String[]{"Points=5"});
			
			//Add AI
			NAI=new NPCAI(this);
			
			//Identify as trader
			INVENTORY.Type="Trader";
		} 
		else if(CLASS.equals("Inn Keeper")){
			//Add Items
			INVENTORY.addItem("Roasted Fish", 15,10, "Fish.png","Food",new String[]{"Points=5"});
			INVENTORY.addItem("Apple", 15,10, "Apple.png","Food",new String[]{"Points=2"});

			//Add AI
			NAI=new NPCAI(this);
			
			//Identify as trader
			INVENTORY.Type="Trader";
		}
		else if(CLASS.equals("Mage")){
			
			INVENTORY.addItem("Leather Cap", 250,1, "Head_8.png","H_armor",new String[]{"Armor=5","Magic Resist=25"});
			INVENTORY.addItem("Dark Leather Cap", 300,1, "Head_9.png","H_armor",new String[]{"Armor=5","Magic Resist=50"});
			//INVENTORY.addItem("Jesters Cap", 500,1, "Head_5.png","H_armor",new String[]{"Armor=10","Magic Resist=90"});
			//INVENTORY.addItem("Skull Helmet", 5000,1, "Head_3.png","H_armor",new String[]{"Armor=100","Magic Resist=100"});
			INVENTORY.addItem("Shock Spell Stone", 250,1, "Stone_1.png","SpellStoner",new String[]{"Damage="+SHOCK[0].STATS.DAMAGE});
			//INVENTORY.addItem("Darkness Spell Stone", 500,1, "Stone_1.png","SpellStoner",new String[]{"Damage="+DARKNESS[0].STATS.DAMAGE});
			INVENTORY.addItem("Life Drain Spell Stone", 300,1, "Stone_1.png","SpellStoner",new String[]{"Damage="+LIFE_DRAIN[0].STATS.DAMAGE});
			
			//Add AI
			NAI=new NPCAI(this);
			
			//Identify as trader
			INVENTORY.Type="Trader";
			
		}
		else if(CLASS.equals("Skeleton")){
			
			//Add items
			INVENTORY.addItem("Bone Club", 5,1, "Mace_2.png","Meele",new String[]{"Damage=5","Attack Speed=1"});
			
			//Add Gold
			this.GOLD=(int)Math.round(Math.random()*10+2);
			
			//Use Item
			INVENTORY.ItemEffect(0, this);
			
			//Add AI
			EAI=new EnemyAI(this);
		}
		else if(CLASS.equals("Skeleton Chieftan")){
			
			//Add Item
			INVENTORY.addItem("Iron Sword", 50,1, "Sword_2.png","Meele",new String[]{"Damage=15","Attack Speed=0.65"});
			INVENTORY.addItem("Health Potion", 25,1, "Health Pot.png","Potion",new String[]{"Points=50"});
			
			//Add Gold
			this.GOLD=(int)Math.round(Math.random()*50+25);
			
			//Use Item
			INVENTORY.ItemEffect(0, this);
			
			//Assign Stats
			STATS.LEVEL=3;
			HEALTH=250;
			MAX_HEALTH=250;
			
			//Opify
			Opify(1/1.25);
			
			//Add AI
			EAI=new EnemyAI(this);
		}
		else if(CLASS.equals("Shaman")){
			
			//Add items
			INVENTORY.addItem("Health Potion", 25,1, "Health Pot.png","Potion",new String[]{"Points=50"});
			
			//Add Ai
			NAI=new NPCAI(this);
						
		} 
		else if(CLASS.equals("Dark Elf")){
			
			//Add items
			INVENTORY.addItem("Rusted Dagger", 20,1, "Dagger_4.png","Meele",new String[]{"Damage=10","Attack Speed=1"});
			INVENTORY.addItem("Iron Helmet", 150,1, "Head_2.png","H_armor",new String[]{"Armor=5"});
		
			//Assign Stats
			STATS.LEVEL=2;
			HEALTH=150;
			MAX_HEALTH=150;
			
			//Add Gold
			this.GOLD=(int)Math.round(Math.random()*15+2);
			
			//Use Item
			INVENTORY.ItemEffect(0, this);
			INVENTORY.ItemEffect(1, this);
			
			//Add Ai
			EAI=new EnemyAI(this);
						
		} 
		else if(CLASS.equals("Prisoner")){
			INVENTORY.addItem("Key", 0, 1, "Key.png", "Key", new String[]{});
			//NAI= new NPCAI(this);
		}
		
		

	}

	/**
	 * do damage to character
	 * @param Dmg Amount of Damage
	 */
	public void Damage(int Dmg, int Type){
	//If character is already dead then dont do damage	
		if(ISDEAD)return;
		
		//Do damage
		if(Type==1){
			//DAMAGE FROM PHYSICAL ATTACK
			if(STATS.ARMOR>Dmg)return;
			HEALTH=HEALTH-Dmg+STATS.ARMOR;
		}
		else if(Type==2){
			//DAMAGE FROM MAGIC ATTACK
			if(STATS.MAGIC_RESIST>Dmg)return;
			HEALTH=HEALTH-Dmg+STATS.MAGIC_RESIST;
		}
		//If an Npc then run and hide
		if(NAI!=null){NAI.State="alarmed";}
		
		//Death condition
		if(HEALTH<=0){
			//If player is dead
			if(this.CLASS.equals("Player")){
				
				HEALTH=0;
				
				//Quit game
				JOptionPane.showMessageDialog(null, "You are dead");
				X=100;Y=100;
				Opify(50);
				HEALTH=(int)MAX_HEALTH;
				
			
		}else{
			//If other character
				//set Death stats
				ISDEAD=true;
				HEALTH=0;
				//display death
				CLASS=Cl+" (Dead)";
				
				//Rot effect
				for(int i=0;i<imgCHARAC.getWidth();i++){
					for(int j=0;j<imgCHARAC.getHeight();j++){
						imgCHARAC.setRGB(i, j, imgCHARAC.getRGB(i,j)*(int)Math.pow(3,3));
						
					}
				}
				
				//Make inventory open to looting
				INVENTORY.OPEN_INVEN=true;
				
				
				
			}
		}
	}
	
	/**
	 * Prototype 
	 * give player  a leveled up look
	 */
	public void Opify(double Fac){
		
		for(int i=0;i<imgCHARAC.getWidth();i++){
			for(int j=0;j<imgCHARAC.getHeight();j++){
				if(imgCHARAC.getRGB(i,j)!=imgCHARAC.getRGB(2,2)){
					imgCHARAC.setRGB(i, j, (int)Math.round(imgCHARAC.getRGB(i,j)*Fac));
					
				}
			}
		}

		
		
	}
	
	/**
	 * Regenerate the players mana
	 * @param Mn amount to regen
	 */
	public void ManaRegen(int Mn){
		//Counter
		REGEN_COUNTER++;
		
		if(REGEN_COUNTER>30){
			//Regen
			if(MANA<MAX_MANA)MANA+=Mn;
			REGEN_COUNTER=0;
			
		}
	}
	
	/**
	 * Move the character
	 * @param dx x displacement
	 * @param dy y displacement
	 */
	public void Move(int dx,int dy){
		if(!CANMOVE)return;
		//Move
		X+=dx*SPEED;
		Y+=dy*SPEED;
			
		//Set FY depending on the sprite Type
		switch(TYPE){
			case 1:
				if(dx>0){
					FY=Type1.RIGHT.ordinal();
				}
				else if(dx<0){
					FY=Type1.LEFT.ordinal();
				}
				else if(dy>0){
					FY=Type1.DOWN.ordinal();
				}
				else if(dy<0){
					FY=Type1.UP.ordinal();
				}
				
				break;
			case 2:
				if(dx>0){
					FY=Type2.RIGHT.ordinal();
				}
				else if(dx<0){
					FY=Type2.LEFT.ordinal();
				}
				else if(dy>0){
					FY=Type2.DOWN.ordinal();
				}
				else if(dy<0){
					FY=Type2.UP.ordinal();
				}
			case 3:
				if(dx>0){
					FY=Type3.RIGHT.ordinal();
				}
				else if(dx<0){
					FY=Type3.LEFT.ordinal();
				}
				else if(dy>0){
					FY=Type3.DOWN.ordinal();
				}
				else if(dy<0){
					FY=Type3.UP.ordinal();
				}
					
				break;	
		}
		
		//X Frame Change
		FrameChange(SIZE,4,1);
			
			
	}
	
	/**
	 * add a Map collision object
	 * @param M GridMap
	 */
	public void addColisionObject(GridMap M){
		MAP.add(M);
	}
	/**
	 * add a Object collision object
	 * @param O Object
	 */
	public void addColisionObject(GameObject O){
		OBJECTS.add(O);
	}
	/**
	 * add Character collision object
	 * @param C Character
	 */
	public void addColisionObject(GameCharacter C){
		CHARACTERS.add(C);
	}
	/**
	 * add Building collision object
	 * @param B Building
	 */
	public void addColisionObject(Building B){
		BUILD.add(B);
	}
	
	/**
	 * add an array of Building collision object
	 * @param M Array of GridMap
	 */
	public void addColisionObject(Building[] B){
		for(int i=0;i<B.length;i++){
			BUILD.add(B[i]);
		}
	}
	
	/**
	 * add a Map collision object
	 * @param M Array of GridMap
	 */
	public void addColisionObject(GridMap[] M){
		for(int i=0;i<M.length;i++){
			MAP.add(M[i]);
		}
	}
	
	/**
	 * add a Object collision object
	 * @param O Array of Objects
	 */
	public void addColisionObject(GameObject[] O){
		for(int i=0;i<O.length;i++){
			OBJECTS.add(O[i]);
		}
	}
	
	/**Remove all the collision objects from the player
	 * 
	 */
	
	public void removeCollisionObject(GameObject[] obj){
		for(int i=0;i<obj.length;i++){
			OBJECTS.remove(obj[i]);
			//OBJECTS.add(obj[i]);
		}
	}
	public void removeCollisionObject(GameObject obj){
	//	for(int i=0;i<obj.length;i++){
			OBJECTS.remove(obj);
			//OBJECTS.add(obj[i]);
		//}
	}
	public void removeAllColisionObjects(){
		//Collision maps
		MAP= new Vector<GridMap>();
		
		//Collision objects
		OBJECTS= new Vector<GameObject>();
		
		//Collision characters
		CHARACTERS= new Vector<GameCharacter>();

		//Collision characters
		BUILD= new Vector<Building>();
		

	}
	
	/**
	 * add Character collision object
	 * @param C Array of Characters
	 */
	public void addColisionObject(GameCharacter[] C){
		for(int i=0;i<C.length;i++){
			CHARACTERS.add(C[i]);
		}
	}
	/**
	 * Allows the character to walk over various surfaces
	 * @param F Selected fill number
	 * @return whether or not the character can walk on this surface
	 */
	public boolean isValidSurface(int F){
		//List of valid surfaces
		int[] fill={0,98,106,107,117,118,119,120,121,122,123,124,125,126,127,147,178};
		
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
		}catch(Exception e){return WallCollision(x+M.Size/2,y+M.Size/2,M);}
		return true;
	}

	/**
	 * Object Collision
	 * @param x X Position
	 * @param y Y Position
	 * @param Obj Game Object
	 * @return if character is about to collide with an object
	 */	
	public boolean ObjectCollision(double x, double y,GameObject Obj){
		if((x>Obj.X) &&(x<Obj.X+Obj.W*Obj.GROWTHFACTOR)){
			if((y>Obj.Y) &&(y<Obj.Y+Obj.H*Obj.GROWTHFACTOR)){
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Character Collision
	 * @param x X Position
	 * @param y Y Position
	 * @param Charac GameCharacter
	 * @return if character is about to collide with a character
	 */
	public boolean CharacterCollision(double x, double y,GameCharacter Charac){
		if((x>Charac.X) &&(x<Charac.X+Charac.W*Charac.GROWTHFACTOR)){
			if((y>Charac.Y) &&(y<Charac.Y+Charac.H*Charac.GROWTHFACTOR)){
				
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Building Collision
	 * @param x X Position
	 * @param y Y Position
	 * @param Charac Game Building
	 * @return if character is about to collide with a structure
	 */	
	public boolean BuildingCollision(double x, double y,Building Charac){
		if((x>Charac.X) &&(x<Charac.X+Charac.W*Charac.GROWTHFACTOR)){
			if((y>Charac.Y) &&(y<Charac.Y+Charac.H*Charac.GROWTHFACTOR)){
				return true;
			}
		}
		
		return false;
		
	}

	/**
	 * Check character collision with all kinds of objects
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @return if Character is about to collide with something
	 */
	public boolean Collision(double x, double y){
		boolean blnCol=false;
		//Wall Collision
		for(int i=0;i<MAP.size();i++){
			blnCol=blnCol || WallCollision(x,y,MAP.elementAt(i));
		}
		//Game Object Collision
		for(int i=0;i<OBJECTS.size();i++){
			blnCol=blnCol || ObjectCollision(x,y,OBJECTS.elementAt(i));
		}
		//Game Character collision
		for(int i=0;i<CHARACTERS.size();i++){
			blnCol=blnCol || CharacterCollision(x,y,CHARACTERS.elementAt(i));
		}
		//Game Building Collision
		for(int i=0;i<BUILD.size();i++){
			blnCol=blnCol || BuildingCollision(x,y,BUILD.elementAt(i));
		}
		

		return blnCol;
	}
	
	/**
	 * Draw character in minimap
	 * @param g Graphics
	 * @param Dx X Displacement
	 * @param Dy Y Displacement
	 */
	public void MapDraw(Graphics g,int Dx,int Dy,double Scale,Color col){
		//Color
		g.setColor(col.darker().darker().darker());
		g.drawOval((int)(X*Scale+Dx), (int)(Y*Scale+Dy), 7, 7);
		g.setColor(col);
		g.fillOval((int)(X*Scale+Dx), (int)(Y*Scale+Dy), 7, 7);
	}
	/**
	 * Draw character in game window
	 * @param g Graphics
	 * @param Dx X Displacement
	 * @param Dy Y Displacement
	 * @param G Growth factor
	 */
	public void Draw(Graphics g,int Dx,int Dy,double G){
		//Draw blood
		if(ISDEAD){
			g.drawImage(imgBLOOD,(int)(X+Dx-25),(int)(Y+Dy-15),(int)(W*GROWTHFACTOR+35),(int)(H*GROWTHFACTOR+35),null );		
		}
		
		//Quest Givers
		if(CLASS!="Player"){
			for(int i=0;i<QUEST_LIST.size();i++){
				if(QUEST_LIST.elementAt(i).STATUS==0){
					g.drawImage(imgQStart, (int)(X+Dx+5), (int)(Y+Dy-30),(int)W,35,  null);
				}
				else if(QUEST_LIST.elementAt(i).STATUS==2){
					g.drawImage(imgQEnd, (int)(X+Dx), (int)(Y+Dy-30),(int)W+5,35,  null);
				}
			}
		}
		
		//Draw character
		g.drawImage(imgCHARAC, (int)(X+Dx), (int)(Y+Dy), (int)(X+Dx+W*G), (int)(Y+Dy+H*G), (int)(W*FX), (int)(H*FY), (int)(W*FX+W), (int)(H*FY+H), null);
		
		GROWTHFACTOR=G;
	}
	
	/**
	 * Draw Magic Attack
	 * @param Magic Array of magic attacks
	 * @param g Graphics 
	 * @param Dx Draw X displacement
	 * @param Dy Draw Y displacement
	 * @param G Growth Factor
	 */
	public void MagicDraw(MagicAttack[] Magic,Graphics g,int Dx,int Dy,double G){
		
		for(int i=0;i<Magic.length;i++){
			Magic[i].Draw(g, Dx, Dy, G);
		}
	}
	
	/**
	 * If the character is dead don't make the magic attacks collide
	 */
	public void removeMagicCollision(){
		//Fireball
		for(int i=0;i<FIREBALL.length;i++){
			FIREBALL[i].removeAllColisionObjects();
		}

		//Shock
		for(int i=0;i<SHOCK.length;i++){
			SHOCK[i].removeAllColisionObjects();
		}
		
		//Darkness
		for(int i=0;i<DARKNESS.length;i++){
			DARKNESS[i].removeAllColisionObjects();
		}
		
		//Life Drain
		for(int i=0;i<LIFE_DRAIN.length;i++){
			LIFE_DRAIN[i].removeAllColisionObjects();
		}
	}
	
	/**
	 * Frame Change in X direction
	 * @param MAX last frame index
	 * @param Delay Delay between frame change
	 * @param Dir moves back or forward through frames
	 */
	public void FrameChange(int MAX,int Delay,int Dir){
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
	 * Spawn a character on the map
	 * @param MiX Minimum X value
	 * @param MiY Minimum Y value
	 * @param MaX Maximum X value
	 * @param MaY Maximum Y value
	 */
	public void Spawn(int MiX,int MiY,int MaX,int MaY){
		double Nx=0,Ny=0;
		
		//Random coordinate
		Nx=Math.random()*MaX+MiX;
		Ny=Math.random()*MaY+MiY;
		
		//Store block number
		int BNum=MAP.elementAt(0).getBlockNum(Nx+W/2, Ny+H/2);
		
		//if invalid block number
		if(BNum==-1){Spawn(MiX,MiY,MaX,MaY);}
		
		//if invalid surface
		else if(!isValidSurface(MAP.elementAt(0).Fill[BNum])){Spawn(MiX,MiY,MaX,MaY);}
		
		//if colliding with something
		else if(this.Collision(Nx+W/2, Ny+H/2)){Spawn(MiX,MiY,MaX,MaY);}
		
		else{
			X=Nx;
			Y=Ny;
		}
		
		
	}
	/**
	 * Find the distance between two characters
	 * @param Targ Target character
	 * @return distance
	 */
	public double Dist(GameCharacter Targ){
		//find x squared and y squared
		double dx=Math.pow(((X+W/2*GROWTHFACTOR)-(Targ.X+Targ.W/2*Targ.GROWTHFACTOR)),2);
		double dy=Math.pow(((Y+H/2*GROWTHFACTOR)-(Targ.Y+Targ.H/2*Targ.GROWTHFACTOR)),2);
		
		//find distance
		return (Math.sqrt(dx+dy));
	}

	/**
	 * Find the distance between two characters
	 * @param Targ Target character
	 * @return distance
	 */
	public double Dist(GameObject Targ){
		//find x squared and y squared
		double dx=Math.pow(((X+W/2*GROWTHFACTOR)-(Targ.X+Targ.W/2*Targ.GROWTHFACTOR)),2);
		double dy=Math.pow(((Y+H/2*GROWTHFACTOR)-(Targ.Y+Targ.H/2*Targ.GROWTHFACTOR)),2);
		//find distance
		return (Math.sqrt(dx+dy));
	}
	/**
	 * Find the distance between a character and a building
	 * @param Targ Target Building
	 * @return distance
	 */
	public double Dist(Building Targ){
		
		//find x squared and y squared
		double dx=Math.pow(((X+W/2*GROWTHFACTOR)-(Targ.X+Targ.W/2*Targ.GROWTHFACTOR)),2);
		double dy=Math.pow(((Y+H/2*GROWTHFACTOR)-(Targ.Y+Targ.H/2*Targ.GROWTHFACTOR)),2);
		
		//find distance
		return (Math.sqrt(dx+dy));
	}
	
	/**
	 * Chase another character
	 * @param Targ Target character
	 * @param Error alignment error allowed
	 */
	public void FollowChar(GameCharacter Targ,int Error){
		
		//Find distance
		double dx=(Targ.X+Targ.W/2*Targ.GROWTHFACTOR)-(X+W/2*GROWTHFACTOR);
		double dy=(Targ.Y+Targ.H/2*Targ.GROWTHFACTOR)-(Y+H/2*GROWTHFACTOR);
		
		
		//Y Movement
		if(dy>Error){
			//Down
			if(this.Collision(X, Y+H*GROWTHFACTOR)){EAI.STATE="calm";}
			Move(0,SPEED+1);
		}else if(dy<Error){
			//Up
			if(this.Collision(X, Y)){EAI.STATE="calm";}
			Move(0,-SPEED-1);
		}
		
		
		//X Movement
		if(dx>Error){
			//Right
			if(this.Collision(X+W*GROWTHFACTOR, Y)){EAI.STATE="calm";}
			Move(SPEED+1,0);
		}else if(dx<-Error){
			//Left
			if(this.Collision(X, Y)){EAI.STATE="calm";}
			Move(-SPEED-1,0);
		}
		
	}
	
	/**
	 * Set the direction of the character 
	 * @param Pos String specifying direction
	 */
	public void setPos(String Pos){
		//Set position
		switch(TYPE){
			case 1:
				switch(Pos){
					case "Up":
						FY=Type1.UP.ordinal();
					break;
					case "Down":
						FY=Type1.DOWN.ordinal();
					break;
					case "Left":
						FY=Type1.LEFT.ordinal();
					break;
					case "Right":
						FY=Type1.RIGHT.ordinal();
					break;
					
				}
			break;	
			case 2:
				switch(Pos){
					case "Up":
						FY=Type2.UP.ordinal();
					break;
					case "Down":
						FY=Type2.DOWN.ordinal();
					break;
					case "Left":
						FY=Type2.LEFT.ordinal();
					break;
					case "Right":
						FY=Type2.RIGHT.ordinal();
					break;
					
				}
			break;	
			case 3:
				switch(Pos){
					case "Up":
						FY=Type3.UP.ordinal();
					break;
					case "Down":
						FY=Type3.DOWN.ordinal();
					break;
					case "Left":
						FY=Type3.LEFT.ordinal();
					break;
					case "Right":
						FY=Type3.RIGHT.ordinal();
					break;
					
				}
			break;	

		}
	}
	/**
	 * Sell an item to a shopkeeper
	 * @param Targ Target Inventory
	 * @param Index Item Index
	 */
	public void Sell(ItemList Targ,int Index){
		//Un-equip item
		if(INVENTORY.ITEM_NAME.elementAt(Index).indexOf("<Eq>")!=-1){
			INVENTORY.ItemEffect(Index, this);
		}
		
		//Give the target this item
		Targ.addItem(this.INVENTORY.ITEM_NAME.elementAt(Index), this.INVENTORY.ITEM_PRICE.elementAt(Index), 1, this.INVENTORY.ITEM_IMAGE.elementAt(Index),this.INVENTORY.ITEM_TAG.elementAt(Index),this.INVENTORY.STATS.elementAt(Index));
		
		//Get payment from target
		this.GOLD+=this.INVENTORY.ITEM_PRICE.elementAt(Index);
		
		//Remove item from the character inventory
		this.INVENTORY.removeItem(Index,1);
	}
	/**
	 * Buy an item from a shopkeeper
	 * @param Targ Target Inventory
	 * @param Index Item Index
	 */
	public void Buy(ItemList Targ,int Index){
		//If item is too expensive
		if(Targ.ITEM_PRICE.elementAt(Index)>this.GOLD){JOptionPane.showMessageDialog(null, "Too rich for your blood");return;}
		
		//add Item to character inventory
		this.INVENTORY.addItem(Targ.ITEM_NAME.elementAt(Index), Targ.ITEM_PRICE.elementAt(Index), 1, Targ.ITEM_IMAGE.elementAt(Index),Targ.ITEM_TAG.elementAt(Index),Targ.STATS.elementAt(Index));
		
		//remove Gold
		this.GOLD-=Targ.ITEM_PRICE.elementAt(Index);
		
		//remove item from target inventory
		Targ.removeItem(Index,1);
	}
/**
 * Loot item from a dead body
 * @param Targ Target Inventory
 * @param Index Item Index
 */
	public void Take(ItemList Targ,int Index){
		//Add item to character inventory
		
		//unequip item
		if(Targ.ITEM_NAME.elementAt(Index).indexOf("<Eq>")!=-1){
			Targ.ItemEffect(Index, this);
		}
		this.INVENTORY.addItem(Targ.ITEM_NAME.elementAt(Index), Targ.ITEM_PRICE.elementAt(Index), 1, Targ.ITEM_IMAGE.elementAt(Index),Targ.ITEM_TAG.elementAt(Index),Targ.STATS.elementAt(Index));
		
		//Remove item from target inven 
		Targ.removeItem(Index,1);
	}
	
/**
 * Magic Attack 	
 * @param Magic Array of magic attacks
 */
	public void MAttack(MagicAttack[] Magic){
		//If insufficient mana
		if(MANA-Magic[MAGIC_COUNTER].STATS.POINTS<0)return;
		//Stop
		FIREBALL[MAGIC_COUNTER].ISACTIVE=false;
		
		
		switch(TYPE){
			case 1:
				//set x and y coords depending on the direction the character is facing
				switch(FY){
				
					case 0: //Down
						//Set x and y coords
						Magic[MAGIC_COUNTER].X=X+W/2-Magic[MAGIC_COUNTER].W/2*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						Magic[MAGIC_COUNTER].Y=Y+H-Magic[MAGIC_COUNTER].H/4*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						//Set frame 0
						Magic[MAGIC_COUNTER].FX=0;
						//Movement Speed
						Magic[MAGIC_COUNTER].dX=0;Magic[MAGIC_COUNTER].dY=20;
						break;
					
					case 1: //Left
						//Set x and y coords
						Magic[MAGIC_COUNTER].X=X-Magic[MAGIC_COUNTER].W/4*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						Magic[MAGIC_COUNTER].Y=Y+H/2-Magic[MAGIC_COUNTER].H/2*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						//Set frame 0
						Magic[MAGIC_COUNTER].FX=0;
						//Movement Speed
						Magic[MAGIC_COUNTER].dX=-20;Magic[MAGIC_COUNTER].dY=0;
						break;
					
					case 2: //Right
						//Set x and y coords
						Magic[MAGIC_COUNTER].X=X+W-Magic[MAGIC_COUNTER].W/4*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						Magic[MAGIC_COUNTER].Y=Y+H/2-Magic[MAGIC_COUNTER].H/2*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						//Set frame 0
						Magic[MAGIC_COUNTER].FX=0;
						//Movement Speed
						Magic[MAGIC_COUNTER].dX=20;Magic[MAGIC_COUNTER].dY=0;
						break;
					
					case 3: //Up
						//Set x and y coords
						Magic[MAGIC_COUNTER].X=X+W/2-Magic[MAGIC_COUNTER].W/2*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						Magic[MAGIC_COUNTER].Y=Y-Magic[MAGIC_COUNTER].H/2*Magic[MAGIC_COUNTER].GROWTHFACTOR;
						//Set frame 0
						Magic[MAGIC_COUNTER].FX=0;
						//Movement Speed
						Magic[MAGIC_COUNTER].dX=0;Magic[MAGIC_COUNTER].dY=-20;
						break;					
				}
		}
			//Activate
			Magic[MAGIC_COUNTER].ISACTIVE=true;
			
			//Consume mana 
			MANA-=Magic[MAGIC_COUNTER].STATS.POINTS;
			
			//Change counter
			MAGIC_COUNTER++;
			if(MAGIC_COUNTER==Magic.length)MAGIC_COUNTER=0;
	}
	
	/**
	 * Physical Meele Attack
	 * @param Me
	 */
	public void PAttack(MeeleAttack Me){
		
		switch(TYPE){
		
			case 1:
				//Meele Attack
				if(FY==Type1.UP.ordinal()){Me.PAttack(Type1.UP.name());}
				else if(FY==Type1.DOWN.ordinal()){Me.PAttack(Type1.DOWN.name());}
				else if(FY==Type1.LEFT.ordinal()){Me.PAttack(Type1.LEFT.name());}
				else if(FY==Type1.RIGHT.ordinal()){Me.PAttack(Type1.RIGHT.name());}
				
				break;
			case 2:
				//Meele Attack
				if(FY==Type2.UP.ordinal()){Me.PAttack(Type2.UP.name());}
				else if(FY==Type2.DOWN.ordinal()){Me.PAttack(Type2.DOWN.name());}
				else if(FY==Type2.LEFT.ordinal()){Me.PAttack(Type2.LEFT.name());}
				else if(FY==Type2.RIGHT.ordinal()){Me.PAttack(Type2.RIGHT.name());}
				
				break;
			case 3:
				//Meele Attack
				if(FY==Type3.UP.ordinal()){Me.PAttack(Type3.UP.name());}
				else if(FY==Type3.DOWN.ordinal()){Me.PAttack(Type3.DOWN.name());}
				else if(FY==Type3.LEFT.ordinal()){Me.PAttack(Type3.LEFT.name());}
				else if(FY==Type3.RIGHT.ordinal()){Me.PAttack(Type3.RIGHT.name());}
				
				break;	
		}
	}
	
	
	/**
	 * get xp and gold from killing characters
	 * @param Charac collision characters
	 */
	public void KillXP(GameCharacter Charac){
		//When the character dies
		if(Charac.ISDEAD && Charac.COLL_LISTENER){
			
			Charac.COLL_LISTENER=false;
			audDEATH.playAudio("death.wav");
			
			//Remove collision listener
			for(int j=0;j<FIREBALL.length;j++){
				FIREBALL[j].removeCollChar(Charac);
			}
			for(int j=0;j<SHOCK.length;j++){
				SHOCK[j].removeCollChar(Charac);
			}
			for(int j=0;j<DARKNESS.length;j++){
				DARKNESS[j].removeCollChar(Charac);
			}
			for(int j=0;j<LIFE_DRAIN.length;j++){
				LIFE_DRAIN[j].removeCollChar(Charac);
			}

			//Add Xp
			STATS.IncreaseXP(10*Charac.STATS.LEVEL);
			//Add Gold
			this.GOLD+=Charac.GOLD;
		}
	}

	/**
	 * get xp and gold from killing characters
	 * @param Charac collision characters
	 */
	public void KillXP(GameCharacter[] Charac){
		//When the character dies
		for(int i=0;i<Charac.length;i++){
			if(Charac[i].ISDEAD && Charac[i].COLL_LISTENER){
				
				Charac[i].COLL_LISTENER=false;
				audDEATH.playAudio("death.wav");
				
				
				//Remove collision listener
				for(int j=0;j<FIREBALL.length;j++){
					FIREBALL[j].removeCollChar(Charac[i]);
				}
				for(int j=0;j<SHOCK.length;j++){
					SHOCK[j].removeCollChar(Charac[i]);
				}
				for(int j=0;j<DARKNESS.length;j++){
					DARKNESS[j].removeCollChar(Charac[i]);
				}
				for(int j=0;j<LIFE_DRAIN.length;j++){
					LIFE_DRAIN[j].removeCollChar(Charac[i]);
				}

				//Add Xp
				STATS.IncreaseXP(10*Charac[i].STATS.LEVEL);
				//Add Gold
				this.GOLD+=Charac[i].GOLD;
			}
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
	
	
	/**
	 * Used for debugging get list of spells available to character
	 * @return List
	 */
	public String[] getSpellList(){
		//Make Array
		String[] arg=new String[SPELL_LIST.size()];
		
		//Assign names
		for(int i=0;i<arg.length;i++){
			arg[i]=SPELL_LIST.elementAt(i)[0].NAME;
			System.out.println(arg[i]);
		}
		
		//Return list
		return arg;
	}
	
	/**
	 * Spells collide with the given characters
	 * @param Charac Game character array 
	 */
	public void addMagicCharacCollision(GameCharacter[] Charac){
		
		//FIREBALL
		for(int i=0;i<FIREBALL.length;i++){
			FIREBALL[i].addCollChar(Charac);
		}
		
		//SHOCK
		for(int i=0;i<SHOCK.length;i++){
			SHOCK[i].addCollChar(Charac);
		}
		
		//DARKNESS
		for(int i=0;i<DARKNESS.length;i++){
			DARKNESS[i].addCollChar(Charac);
		}
		
		//LIFE DRAIN
		for(int i=0;i<LIFE_DRAIN.length;i++){
			LIFE_DRAIN[i].addCollChar(Charac);
		}
	}

	/**
	 * Spells collide with the given characters
	 * @param Charac Game character array 
	 */
	public void addMagicCharacCollision(GameCharacter Charac){
		
		//FIREBALL
		for(int i=0;i<FIREBALL.length;i++){
			FIREBALL[i].addCollChar(Charac);
		}
		
		//SHOCK
		for(int i=0;i<SHOCK.length;i++){
			SHOCK[i].addCollChar(Charac);
		}
		
		//DARKNESS
		for(int i=0;i<DARKNESS.length;i++){
			DARKNESS[i].addCollChar(Charac);
		}

		//LIFE DRAIN
		for(int i=0;i<LIFE_DRAIN.length;i++){
			LIFE_DRAIN[i].addCollChar(Charac);
		}

	}
	
	/**
	 * Spells collide with walls
	 * @param Map GridMap 
	 */
	public void addMagicMapCollision(GridMap Map){
		
		//FIREBALL
		for(int i=0;i<FIREBALL.length;i++){
			FIREBALL[i].addColisionObject(Map);
		}
		
		//SHOCK
		for(int i=0;i<SHOCK.length;i++){
			SHOCK[i].addColisionObject(Map);
		}
		
		//DARKNESS
		for(int i=0;i<DARKNESS.length;i++){
			//DARKNESS[i].addColisionObject(Map);
		}

		//LIFE DRAIN
		for(int i=0;i<LIFE_DRAIN.length;i++){
			LIFE_DRAIN[i].addColisionObject(Map);
		}

	
	}

	public void Heal(int Amount){
		HEALTH+=Amount;
		if(HEALTH>MAX_HEALTH)HEALTH=(int)MAX_HEALTH;
	}
	public void giveQuest(GameCharacter Reciever){
		if(QUEST_LIST.size()>0){
			if(QUEST_LIST.elementAt(0).STATUS==0){
				Reciever.QUEST_LIST.add(QUEST_LIST.elementAt(0));
				QUEST_LIST.elementAt(0).play_UpdateSound();
				if(QUEST_LIST.elementAt(0).QTYPE==Quest_Type.DELIVERY){
					for(int i=0;i<QUEST_LIST.elementAt(0).TARGET_LIST.ITEM_NAME.size();i++){
						Reciever.INVENTORY.addItem(QUEST_LIST.elementAt(0).TARGET_LIST.ITEM_NAME.elementAt(i), QUEST_LIST.elementAt(0).TARGET_LIST.ITEM_PRICE.elementAt(i), QUEST_LIST.elementAt(0).TARGET_LIST.ITEM_QUANTITY.elementAt(i), QUEST_LIST.elementAt(0).TARGET_LIST.ITEM_IMAGE.elementAt(i), QUEST_LIST.elementAt(0).TARGET_LIST.ITEM_TAG.elementAt(i), QUEST_LIST.elementAt(0).TARGET_LIST.STATS.elementAt(i));
					}
				}
				QUEST_LIST.elementAt(0).STATUS=1;
			}
		}
	}

	public void CompleteQuest(int Index){
		Quest qstComp=QUEST_LIST.elementAt(Index);
		qstComp.STATUS=3;
		qstComp.QUEST_GIVER.QUEST_LIST.elementAt(0).STATUS=3;
		qstComp.QUEST_GIVER.QUEST_LIST.removeElementAt(0);
		STATS.IncreaseXP(qstComp.XP);
		GOLD+=qstComp.GOLD;
				
	}
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "GameCharacter");
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

