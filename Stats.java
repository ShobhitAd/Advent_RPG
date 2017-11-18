/**
 * 
 * @category Basic
 *
 */
public class Stats {
	//Leveling up mechanics	
	int LEVEL = 1;
	int XP=0,MAX_XP=100;

	//Character specific stats
	int STRENGTH =1,DEXTERITY = 1,LUCK = 1,INTELLIGENCE = 1, POINTS=1,XP_POINTS=0;

	//General stats
	int DAMAGE=0,MDAMAGE=0,ARMOR=0,MAGIC_RESIST=0;
	double ATTACK_SPEED=1;
	
	//Used for character stats
	GameCharacter CHARAC;
	
	AudioEffects audLvlUp= new AudioEffects(false);
	
	/**
	 * Stats character constructor
	 * @param Ch Game Character
	 */
	public Stats(GameCharacter Ch){
		//Randomize stats
		STRENGTH = (int)Math.round(Math.random()*10 + 1);
		DEXTERITY= (int)Math.round(Math.random()*10 + 1);
		LUCK= (int)Math.round(Math.random()*10 + 1);
		INTELLIGENCE= (int)Math.round(Math.random()*10 + 1);
		CHARAC=Ch;

	}
	public Stats(){
		
	}
	/**
	 * General constructor
	 */
	public Stats(String[] sts){
		setStats(sts);
	}
	
	/**
	 * Increase Player or item Xp for certain tasks
	 * @param Increase
	 */
	public void IncreaseXP(int Increase){
		//Increase
		XP+=Increase;

		//Level up
		if(XP>=MAX_XP){
			XP-=MAX_XP;
			LevelUp();
		}
		
	}
	/**
	 * Level up the character
	 */
	public void LevelUp(){
		LEVEL++;
		XP_POINTS+=5;
		
		//Increase health and mana
		CHARAC.MAX_HEALTH=CHARAC.MAX_HEALTH +  50 + Math.round(50 * ((double)STRENGTH/100));
		CHARAC.MAX_MANA=CHARAC.MAX_MANA +  50 + Math.round(50 * ((double)INTELLIGENCE/100));
		CHARAC.HEALTH=(int)CHARAC.MAX_HEALTH;
		CHARAC.MANA=(int)CHARAC.MAX_MANA;
		
		//Increase damage and magic damage
		DAMAGE= (int)Math.round((double)STRENGTH);
		MDAMAGE= (int)Math.round((double)INTELLIGENCE);
		
		//Increase max xp
		MAX_XP=LEVEL*100;
		
		audLvlUp.playAudio("level up.wav");

	}

	/**
	 * 
	 * @param set array of Stats to set
	 */
	public void setStats(String[] set){
		//Go through each command
		for(int i=0;i<set.length;i++){
			//Parse Command
			
			int Start=set[i].indexOf("=");
			String Property=removeString(set[i].substring(0, Start)," "),Value=removeString(set[i].substring(Start+1)," ");
			
			
			//Assign Stat
			switch(Property.toLowerCase()){
				
				case "level"://LEVEL
					LEVEL=Integer.parseInt(Value);
					break;
					
				case "strength"://STRENGTH
					STRENGTH=Integer.parseInt(Value);
					break;
					
				case "dexterity"://DEXTERITY
					DEXTERITY=Integer.parseInt(Value);
					break;
					
				case "luck"://LUCK
					LUCK=Integer.parseInt(Value);
					break;
					
				case "intelligence"://INTELLIGENCE
					INTELLIGENCE=Integer.parseInt(Value);
					break;
					
				case "damage"://DAMAGE
					DAMAGE=Integer.parseInt(Value);
					break;
					
				case "armor"://ARMOR
					ARMOR=Integer.parseInt(Value);
					break;
					
				case "magicresist"://MAGIC RESISTANCE
					MAGIC_RESIST=Integer.parseInt(Value);
					break;
					
				case "attackspeed"://ATTACKSPEED
					ATTACK_SPEED=Double.parseDouble(Value);
					break;
					
				case "points"://POINTS
					POINTS=Integer.parseInt(Value);
					break;
	
	
					
			}
			
		}
		
	}
	
	/**
	 * Remove certain substrings(usually spaces)
	 * @param Orig Original unprocessed string
	 * @param C substring to remove
	 * @return processed string
	 */
	public String removeString(String Orig,String C){
		//Start
		int Start=Orig.indexOf(C);
		
		while(Start!=-1){
			//Remove substring
			Orig=Orig.substring(0, Start)+Orig.substring( Start+C.length());
			Start=Orig.indexOf(C);
		}
		
		return Orig;
	}
	
	/**
	 * Display Stats
	 */
	public void showStats() {
		
		String Text="Level: " + LEVEL+"\n"
				   +"EXP: " + XP+"\n"
				   +"EXP to level up : " + MAX_XP+"\n"
				   +"Strength: " + STRENGTH+"\n"
				   +"Dexterity: " + DEXTERITY+"\n"
				   +"Luck: " + LUCK+"\n"
				   +"Intelligence: " + INTELLIGENCE+"\n"
				   +"Armor: " + ARMOR ;
		
		//JOptionPane.showMessageDialog(null, Text);
	}
	
	public String toString(){
		String Text="Level: " + LEVEL+"\n"
				   +"Strength: " + STRENGTH+"\n"
				   +"Dexterity: " + DEXTERITY+"\n"
				   +"Luck: " + LUCK+"\n"
				   +"Intelligence: " + INTELLIGENCE+"\n"
				   +"Armor: " + ARMOR ;
		
		return Text;
	}
	/*
	public void spellUnlock(){
		for (int i = 0; i < Spells.length; i ++){
			Spell spell = (Spell) Spells[i];
			if (spell.levelReq == playerStats.level){
				spell.locked = false;
			}
		}
	}

	 */
}
