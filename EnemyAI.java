import java.awt.event.*;
import javax.swing.*;

/**
 * Used to animate enemies
 * @category AI
 *
 */
public class EnemyAI implements ActionListener {
	
	//Action Timer
	Timer tmrACTION= new Timer(1,this);
	
	//Change actions depending on counter
	int ACTION_COUNTER=300,ATTACK_COUNTER=0,MAX=300,Speed=1;
	
	//Choose action
	int OPTION=0,PREVIOUS=0;
	
	//Attack speed
	int ATTACK_SPEED=30;
	
	//Characters
	GameCharacter ENE,FOE;
	
	//Mental state
	String STATE="Calm";
	
	// Chance that the enemy will flee when low health
	int FLEE_WHEN_LOW= (int)Math.round(Math.random()*4);
	
	
	/**
	 *Initialize AI 
	 * @param ch get Character
	 */
	public EnemyAI(GameCharacter ch){
		ENE=ch;
		FOE=ENE;
		tmrACTION.start();
	}
	
	/**
	 * Perform logical actions 
	 */
	public void actionPerformed(ActionEvent event){
		//If dead then kill AI
		if(ENE.ISDEAD){tmrACTION.stop();}
		
		//If frozen then stop moving
		if(!ENE.CANMOVE){return;}
		
		//Increase counter
		ACTION_COUNTER++;
		
		if(ACTION_COUNTER>MAX){
			//Randomize option
			while(OPTION==PREVIOUS){
				OPTION=(int)Math.round((Math.random()*10));
			}
			//Reset
			PREVIOUS=OPTION;
			ACTION_COUNTER=0;
		}
		
		
		//Get mental state
		STATE=(ENE.Dist(FOE)<100 || (ENE.HEALTH<ENE.MAX_HEALTH && ENE.Dist(FOE)<500))?"Hostile":"Calm";
		
		//Flee if low probability
		if(FLEE_WHEN_LOW==1){
			STATE=(ENE.HEALTH<=20)?"alarmed":STATE;
		}
		
		//Take action depending on mental state
		switch(STATE.toLowerCase()){
		
			case "calm":
				//CALM STATE
				switch(OPTION){
					case 0:
						//Move Right
						ENE.Move(Speed, 0);
						//Avoid Collision
						if(ENE.Collision(ENE.X+ENE.W*ENE.GROWTHFACTOR+Speed,ENE.Y))ACTION_COUNTER=MAX+1;
						break;
						
					case 1:
						//Move Left
						ENE.Move(-Speed, 0);
						//Avoid Collision
						if(ENE.Collision(ENE.X-Speed,ENE.Y))ACTION_COUNTER=MAX+1;
						break;
						
					case 2:
						//Move Down
						ENE.Move(0,Speed);
						//Avoid Collision
						if(ENE.Collision(ENE.X,ENE.Y+ENE.H*ENE.GROWTHFACTOR+Speed))ACTION_COUNTER=MAX+1;
						break;
					
					case 3:
						//Move Up
						ENE.Move(0,-Speed);
						//Avoid Collision
						if(ENE.Collision(ENE.X,ENE.Y-Speed))ACTION_COUNTER=MAX+1;
						break;
						
					default:
						//Stand
						if(ENE.TYPE==1){
							ENE.FY=0;
							ENE.FX=0;
						}
						else if(ENE.TYPE==2){
							ENE.FY=2;
							ENE.FX=1;
						}
						break;
						
				}
				
				break;
				
			case "hostile":
			//BEING ATTACKED
				
				if(ENE.MEELE_WEAPON!=null){	
					if(ENE.MEELE_WEAPON.NAME.indexOf("Staff")!=-1){
					//Magic
						
					}else{
						//Meele
					//Check distance
					
						if(ENE.Dist(FOE)>70){
							//Chase enemy
							ENE.FollowChar(FOE,20);
						}else{
							//position
							if(FOE.X+FOE.W/4*FOE.GROWTHFACTOR<ENE.X){ENE.setPos("Left");}
							if(FOE.X>ENE.X+ENE.W/4*ENE.GROWTHFACTOR){ENE.setPos("Right");}
							if(FOE.Y+FOE.H/4*FOE.GROWTHFACTOR<ENE.Y){ENE.setPos("Up");}
							if(FOE.Y>ENE.Y+ENE.H/4*ENE.GROWTHFACTOR){ENE.setPos("Down");}
						
							
							//Attack	
							ATTACK_COUNTER++;
							if(ATTACK_COUNTER>ATTACK_SPEED){
								System.out.println("Im supposed to attack you");
								ENE.PAttack(ENE.MEELE_WEAPON);
								ATTACK_COUNTER=0;
							}
							
							//Chance of using a health potion
							int Opt=(int)Math.round(Math.random());
							
							if(Opt==1 && ENE.HEALTH<ENE.MAX_HEALTH){
								//Use health potion
								if(ENE.INVENTORY.hasItem("Health Potion")){
									int Index=ENE.INVENTORY.ITEM_NAME.indexOf("Health Potion");
									ENE.INVENTORY.ItemEffect(Index,ENE);
								}
								
							}
						}						
					}
			
				}	
				break;
				
			case "alarmed":
				//BEING ATTACKED
					switch(OPTION){
						case 0:
						case 4:
							//Run right
							ENE.Move(Speed+2, 0);
							//Avoid Collision
							if(ENE.Collision(ENE.X+ENE.W*ENE.GROWTHFACTOR+Speed,ENE.Y))ACTION_COUNTER=MAX+1;
							break;
						case 1:
						case 5:
							//Run Left
							ENE.Move(-Speed-2, 0);
							//Avoid Collision
							if(ENE.Collision(ENE.X-Speed,ENE.Y))ACTION_COUNTER=MAX+1;
							break;
						case 2:
						case 6:
							//Run Down
							ENE.Move(0,Speed-2);
							//Avoid Collision
							if(ENE.Collision(ENE.X,ENE.Y+ENE.H*ENE.GROWTHFACTOR+Speed))ACTION_COUNTER=MAX+1;
							break;
						case 3:
						case 7:
							//Run Up
							ENE.Move(0,-Speed-2);
							//Avoid Collision
							if(ENE.Collision(ENE.X,ENE.Y-Speed))ACTION_COUNTER=MAX+1;
							break;
						case 8:
						case 9:	
							//Calm Down
							STATE="Calm";
							
							ACTION_COUNTER=MAX+1;
							break;
							
						default:
							//Use health potion
							
							//Check if npc has a health potion
							if(ENE.INVENTORY.hasItem("Health Potion")){
								//Use potion
								int Index=ENE.INVENTORY.ITEM_NAME.indexOf("Health Potion");
								ENE.INVENTORY.ItemEffect(Index,ENE);
							}
							
							ACTION_COUNTER=MAX+1;
							break;
							
					}
				
					break;
					
		}
		
		
	}
}
