

public class Quest {

	public Quest_Type QTYPE;
	public String NAME="",DESCRIPTION="",PREV_DESCRIPTION="";
	public int GOLD,XP;
	public int STATUS;
	public GameCharacter KILL_TARGET,REC_TARGET;
	public GameCharacter QUEST_GIVER;
	public ItemList TARGET_LIST=new ItemList(); 
	public clsGame GAME=null;
	AudioEffects audUPDATE= new AudioEffects(false);
	
	public Quest(String name,String description,int gold, int xp,Quest_Type type,GameCharacter QuestGiver){
		NAME=name;
		DESCRIPTION=description;
		PREV_DESCRIPTION=description;
		GOLD=gold;
		XP=xp;
		QTYPE=type;
		QUEST_GIVER=QuestGiver;
		
		STATUS=0;		
		
		
	}
	
	public String Display(){
		String Text="**********************************"+NAME+"**********************************\n";
		Text+=DESCRIPTION+"\n\n";
		Text+="GOLD: "+GOLD+"                   XP: "+XP;
		
		return Text;
	}
	public void setTargets(GameCharacter Charac){
		switch(QTYPE){
		case KILL:
			KILL_TARGET=Charac;
			break;
		case DELIVERY:
			break;
		case ITEM:
			break;
			
		}
	}
	
	public void setTargets(GameCharacter Charac,String ItemNames[]){
		switch(QTYPE){
		case KILL:
			KILL_TARGET=Charac;
			for(int i=0;i<ItemNames.length;i++){
				TARGET_LIST.addItem(ItemNames[i], 0, 1, ItemNames[i]+".png", "Quest Item", new String[]{});
			}
			break;
		case DELIVERY:
			REC_TARGET=Charac;
			for(int i=0;i<ItemNames.length;i++){
				TARGET_LIST.addItem(ItemNames[i], 0, 1, ItemNames[i]+".png", "Quest Item", new String[]{});
			}
			break;
		case ITEM:
			break;
			
		}
		
	}
	
	public void UpdateQuest(){
		if(STATUS==3){
			DESCRIPTION="This quest has been completed";
			if(!DESCRIPTION.equals(PREV_DESCRIPTION)){
				GAME.QUEST_WINDOW.setPulse(true);
				play_UpdateSound();
				PREV_DESCRIPTION=DESCRIPTION;
				
			}
			
			return;
		}
		switch(QTYPE){
		case KILL:
			if(KILL_TARGET.ISDEAD){
				STATUS=2;
			}
			break;
		case DELIVERY:
			boolean hasItems=true;
			for(int i=0;i<TARGET_LIST.ITEM_NAME.size();i++){
				hasItems=hasItems &&(REC_TARGET.INVENTORY.hasItem(TARGET_LIST.ITEM_NAME.elementAt(i)));
			}
			if(hasItems){
				STATUS=2;
			}
			break;
		case ITEM:
			break;
			
		}
		if(STATUS==2){
			DESCRIPTION="Go back and collect your reward";
			if(!DESCRIPTION.equals(PREV_DESCRIPTION)){
				GAME.QUEST_WINDOW.setPulse(true);
				play_UpdateSound();
				PREV_DESCRIPTION=DESCRIPTION;
			}
			
		}
	}
	
	public void Deliver(GameCharacter Player,GameCharacter Rec){
		if(QTYPE!=Quest_Type.DELIVERY)return;
		
		
		boolean hasItems=true,isTarget=(Rec==REC_TARGET);
		
		for(int i=0;i<TARGET_LIST.ITEM_NAME.size();i++){
			hasItems=hasItems &&(Player.INVENTORY.hasItem(TARGET_LIST.ITEM_NAME.elementAt(i)));
		}
				
		if(hasItems && isTarget){
			for(int i=0;i<TARGET_LIST.ITEM_NAME.size();i++){
				Player.INVENTORY.removeItem(TARGET_LIST.ITEM_NAME.elementAt(i), TARGET_LIST.ITEM_QUANTITY.elementAt(i));
				Rec.INVENTORY.addItem(TARGET_LIST.ITEM_NAME.elementAt(i),TARGET_LIST.ITEM_PRICE.elementAt(i),TARGET_LIST.ITEM_QUANTITY.elementAt(i), TARGET_LIST.ITEM_IMAGE.elementAt(i), TARGET_LIST.ITEM_TAG.elementAt(i), TARGET_LIST.STATS.elementAt(i));
			}
		}
	}
	
	public void play_UpdateSound(){
		audUPDATE.playAudio("quest update.wav");
	}
	
	
	
}
