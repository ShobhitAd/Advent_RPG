import java.awt.event.*;
import javax.swing.*;

/**
 * Used to animate the Npcs
 * @category AI
 *
 */
public class NPCAI implements ActionListener {
	
	//Action timer
	Timer tmrACTION= new Timer(1,this);
	
	//Decide when to switch actions
	int ACTION_COUNTER=300,MAX=300,Speed=1;
	
	//Choice to make
	int OPTION=0,PREVIOUS=0;
	
	//NPC Character
	GameCharacter NPC;
	
	//Mental State
	String State="Calm";
	
	/**
	 *Initialize AI 
	 * @param ch
	 */
	public NPCAI(GameCharacter ch){
		NPC=ch;
		tmrACTION.start();
	}
	
	/**
	 * Perform logical actions 
	 */
	public void actionPerformed(ActionEvent event){
		//If dead then kill AI
		if(NPC.ISDEAD){tmrACTION.stop();}
		if(!NPC.CANMOVE){return;}
		
		ACTION_COUNTER++;
		
		if(ACTION_COUNTER>MAX){
			//Randomize option
			while(OPTION==PREVIOUS){
				OPTION=(int)Math.round((Math.random()*10));
			}
			
			PREVIOUS=OPTION;
			ACTION_COUNTER=0;
		}
		
		//Take action depending on mental state
		switch(State.toLowerCase()){
		
			case "calm":
				//CALM STATE
				switch(OPTION){
					case 0:
						//Move Right
						NPC.Move(Speed, 0);
						//Avoid Collision
						if(NPC.Collision(NPC.X+NPC.W*NPC.GROWTHFACTOR+Speed,NPC.Y))ACTION_COUNTER=MAX+1;
						break;
						
					case 1:
						//Move Left
						NPC.Move(-Speed, 0);
						//Avoid Collision
						if(NPC.Collision(NPC.X-Speed,NPC.Y))ACTION_COUNTER=MAX+1;
						break;
						
					case 2:
						//Move Down
						NPC.Move(0,Speed);
						//Avoid Collision
						if(NPC.Collision(NPC.X,NPC.Y+NPC.H*NPC.GROWTHFACTOR+Speed))ACTION_COUNTER=MAX+1;
						break;
					
					case 3:
						//Move Up
						NPC.Move(0,-Speed);
						//Avoid Collision
						if(NPC.Collision(NPC.X,NPC.Y-Speed))ACTION_COUNTER=MAX+1;
						break;
					
					case 4:
						
						break;
						
					default:
						//Stand
						if(NPC.TYPE==1){
							NPC.FY=0;
							NPC.FX=0;
						}
						else if(NPC.TYPE==2){
							NPC.FY=0;
							NPC.FX=1;
						}
						else if(NPC.TYPE==3){
							NPC.FY=0;
							NPC.FX=1;
						}
						break;
						
				}
				
				break;
				
			case "alarmed":
			//BEING ATTACKED
				switch(OPTION){
					case 0:
					case 4:
						//Run right
						NPC.Move(Speed+2, 0);
						//Avoid Collision
						if(NPC.Collision(NPC.X+NPC.W*NPC.GROWTHFACTOR+Speed,NPC.Y))ACTION_COUNTER=MAX+1;
						break;
					case 1:
					case 5:
						//Run Left
						NPC.Move(-Speed-2, 0);
						//Avoid Collision
						if(NPC.Collision(NPC.X-Speed,NPC.Y))ACTION_COUNTER=MAX+1;
						break;
					case 2:
					case 6:
						//Run Down
						NPC.Move(0,Speed-2);
						//Avoid Collision
						if(NPC.Collision(NPC.X,NPC.Y+NPC.H*NPC.GROWTHFACTOR+Speed))ACTION_COUNTER=MAX+1;
						break;
					case 3:
					case 7:
						//Run Up
						NPC.Move(0,-Speed-2);
						//Avoid Collision
						if(NPC.Collision(NPC.X,NPC.Y-Speed))ACTION_COUNTER=MAX+1;
						break;
					case 8:
					case 9:	
						//Calm Down
						State="Calm";
						
						ACTION_COUNTER=MAX+1;
						break;
						
					default:
						//Use health potion
						
						//Check if npc has a health potion
						if(NPC.INVENTORY.hasItem("Health Potion")){
							//Use potion
							int Index=NPC.INVENTORY.ITEM_NAME.indexOf("Health Potion");
							NPC.INVENTORY.ItemEffect(Index,NPC);
						}
						
						ACTION_COUNTER=MAX+1;
						break;
						
				}
			
				break;
			
			//Quest giving Npcs
				
			case "quest":
				
				//Stand
				if(NPC.TYPE==1){
					NPC.FY=0;
					NPC.FX=0;
				}
				else if(NPC.TYPE==2){
					NPC.FY=0;
					NPC.FX=1;
				}
				else if(NPC.TYPE==3){
					NPC.FY=0;
					NPC.FX=1;
				}
				break;
			
			//NPC waiting for player to complete quest
			case "waiting":
				
				break;
		
	
				
		}
		
	}
}
