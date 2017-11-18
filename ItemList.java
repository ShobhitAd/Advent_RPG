import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;
import java.util.Vector;

import javax.imageio.*;
import javax.swing.*;


/**
 *  Used for character and chest inventories
 * @category Basic
 *
 */
public class ItemList{
	
	//Basic Item characteristics
	Vector<String> ITEM_NAME= new Vector();
	Vector<Integer> ITEM_PRICE= new Vector();
	Vector<Integer> ITEM_QUANTITY= new Vector();
	Vector<Image> ITEM_IMAGE= new Vector();
	Vector<String> ITEM_TAG= new Vector();
	
	//Stats such as damage and protection
	Vector<Stats> STATS= new Vector();
	
	//Hotkey used to access the item
	Vector<Integer> HOTKEY= new Vector();
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Items\\";
	
	//Used for chests and dead bodies
	boolean OPEN_INVEN=false;
	
	//Specify the type of item list
	String Type="";
	
	int Count=0;
	
	/**
	 * Constructor
	 */
	public ItemList(){
	//Do Nothing
	}
	
	/**
	 * Add an item to a characters inventory
	 * @param Name Name of the Item
	 * @param Price Price of the item
	 * @param Quantity Quantity of the Item
	 * @param FileN File name to get the image of the Item
	 * @param tag Tag to identify the type of item
	 */
	public void addItem(String Name,int Price,int Quantity,String FileN,String tag,String[] sts){
		
		try{
			//Check if this type of item in already in the characters inventory
			int Counter=-1;
			
			for(int i=0;i<ITEM_NAME.size();i++){
				if(ITEM_NAME.elementAt(i).equalsIgnoreCase(Name)){
					Counter=i;
					break;
				}
			}
		
			//If not, then add as new item
			if(Counter==-1){
				ITEM_NAME.add(Name);
				ITEM_PRICE.add(Price);
				ITEM_QUANTITY.add(Quantity);
				if(isJar()){
					ITEM_IMAGE.add(getImage("/Graphics/Items/",FileN));
				}else{
					ITEM_IMAGE.add(ImageIO.read(Paths.get(DIRECT+FileN).toFile()));
				}
				
				ITEM_TAG.add(tag);
				STATS.add(new Stats(sts));
				HOTKEY.add(-1);
			}else{
				//If yes then stack it on
				ITEM_QUANTITY.setElementAt(ITEM_QUANTITY.elementAt(Counter)+Quantity, Counter);
			}
		}catch(Exception e){}
	}
	/**
	 * Add an item to a characters inventory
	 * @param Name Name of the Item
	 * @param Price Price of the item
	 * @param Quantity Quantity of the Item
	 * @param img Image of the Item
	 * @param tag Tag to identify the type of item
	 */
	public void addItem(String Name,int Price,int Quantity,Image img,String tag,String[] sts){
		try{
			//Check if this type of item in already in the characters inventory
			int Counter=-1;
			
			for(int i=0;i<ITEM_NAME.size();i++){
				if(ITEM_NAME.elementAt(i).equalsIgnoreCase(Name)){
					Counter=i;
					break;
				}
			}
			//If not, then add as new item
			if(Counter==-1){
				ITEM_NAME.add(Name);
				ITEM_PRICE.add(Price);
				ITEM_QUANTITY.add(Quantity);
				ITEM_IMAGE.add(img);
				ITEM_TAG.add(tag);
				STATS.add(new Stats(sts));
				HOTKEY.add(-1);
			}else{
				//If yes then stack it on
				ITEM_QUANTITY.setElementAt(ITEM_QUANTITY.elementAt(Counter)+Quantity, Counter);
			}
		}catch(Exception e){}
	}
	/**
	 * Add an item to a characters inventory
	 * @param Name Name of the Item
	 * @param Price Price of the item
	 * @param Quantity Quantity of the Item
	 * @param FileN File name to get the image of the Item
	 * @param tag Tag to identify the type of item
	 */
	public void addItem(String Name,int Price,int Quantity,String FileN,String tag,Stats sts){
		
		try{
			//Check if this type of item in already in the characters inventory
			int Counter=-1;
			
			for(int i=0;i<ITEM_NAME.size();i++){
				if(ITEM_NAME.elementAt(i).equalsIgnoreCase(Name)){
					Counter=i;
					break;
				}
			}
			//If not, then add as new item
			if(Counter==-1){
				ITEM_NAME.add(Name);
				ITEM_PRICE.add(Price);
				ITEM_QUANTITY.add(Quantity);
				if(isJar()){
					ITEM_IMAGE.add(getImage("/Graphics/Items/",FileN));
				}else{
					ITEM_IMAGE.add(ImageIO.read(Paths.get(DIRECT+FileN).toFile()));
				}
				ITEM_TAG.add(tag);
				STATS.add(sts);
				HOTKEY.add(-1);
			}else{
				//If yes then stack it on
				ITEM_QUANTITY.setElementAt(ITEM_QUANTITY.elementAt(Counter)+Quantity, Counter);
			}
		}catch(Exception e){}
	}
	/**
	 * Add an item to a characters inventory
	 * @param Name Name of the Item
	 * @param Price Price of the item
	 * @param Quantity Quantity of the Item
	 * @param img Image of the Item
	 * @param tag Tag to identify the type of item
	 */
	public void addItem(String Name,int Price,int Quantity,Image img,String tag,Stats sts){
		try{
			//Check if this type of item in already in the characters inventory
			int Counter=-1;
			
			for(int i=0;i<ITEM_NAME.size();i++){
				if(ITEM_NAME.elementAt(i).equalsIgnoreCase(Name)){
					Counter=i;
					break;
				}
			}
			//If not, then add as new item
			if(Counter==-1){
				ITEM_NAME.add(Name);
				ITEM_PRICE.add(Price);
				ITEM_QUANTITY.add(Quantity);
				ITEM_IMAGE.add(img);
				ITEM_TAG.add(tag);
				STATS.add(sts);
				HOTKEY.add(-1);
			}else{
				//If yes then stack it on
				ITEM_QUANTITY.setElementAt(ITEM_QUANTITY.elementAt(Counter)+Quantity, Counter);
			}
		}catch(Exception e){}
	}
		
	/**
	 * Remove item from the players inventory
	 * @param Name Name of Item
	 * @param Quantity Quantity of Item
	 */
	public void removeItem(String Name,int Quantity){
		//Check if the item is in the inventory
		int Counter=-1;
		for(int i=0;i<ITEM_NAME.size();i++){
			if(ITEM_NAME.elementAt(i).equalsIgnoreCase(Name)){
				//Get item index
				Counter=i;
				break;
			}
		}
		
		if(Counter==-1) return;
		//If more than 1 then decrease the quant by Quantity
		if(ITEM_QUANTITY.elementAt(Counter)-Quantity>=1){
			ITEM_QUANTITY.setElementAt(ITEM_QUANTITY.elementAt(Counter)-Quantity, Counter);
		}else{
			//Remove item form the inventory
			removeItem(Counter);
		}
		
	}
	/**
	 * Remove item from the players inventory
	 * @param Index of item
	 * @param Quantity Quantity of Item
	 */
	public void removeItem(int Index,int Quantity){
		if (Index==-1)return;
		
		//If item stack
		if(ITEM_QUANTITY.elementAt(Index)-Quantity>=1){
			//Decrease stack by quantity
			ITEM_QUANTITY.setElementAt(ITEM_QUANTITY.elementAt(Index)-Quantity, Index);
		}else{
			//Remove item completely from inventory
			removeItem(Index);
		}
		
	}
	/**
	 * Remove item from the players inventory
	 * @param Index of item
	 */	
	public void removeItem(int Index){
		ITEM_NAME.remove(Index);
		ITEM_PRICE.remove(Index);
		ITEM_IMAGE.remove(Index);
		ITEM_QUANTITY.remove(Index);
		ITEM_TAG.remove(Index);
		STATS.remove(Index);
		HOTKEY.remove(Index);
	}
	
	
	/**
	 * General string method . Add spaces in a sentence
	 * @param Num
	 * @return
	 */
	public String Space(int Num){
		String Sp="";
		//Add specified number of spaces
		for(int i=0;i<Num;i++){
			Sp+=" ";
		}
		return Sp;
	}
	
	/**
	 * Check if a player has a certain item
	 * @param Name Name of item
	 * @return Whether or not the player has the item
	 */
	public boolean hasItem(String Name){
		
		return ITEM_NAME.contains(Name);
		
	}
	
	public void ItemEffect(int Index,GameCharacter C){
		//If the player is already dead then exit
		if(C.ISDEAD)return;
		
		switch(ITEM_NAME.elementAt(Index)){
		//Use item by name
			case "Health Potion":
				//Add Health
				C.HEALTH+=STATS.elementAt(Index).POINTS;
				if(C.HEALTH>C.MAX_HEALTH)C.HEALTH=(int)C.MAX_HEALTH;
				
				//Remove the item
				removeItem(Index, 1);
				
				return;
			
			case "Mana Potion":
				//Add Mana
				C.MANA+=STATS.elementAt(Index).POINTS;
				if(C.MANA>C.MAX_MANA)C.MANA=(int)C.MAX_MANA;
				
				//Remove the item
				removeItem(Index, 1);
				return;
				
			case "Speed Potion":
		
				//Add speed
				C.SPEED+=STATS.elementAt(Index).POINTS;
				C.BASIC_EFFECTS.setTask("Speed", 70);
				
				//Remove the item
				removeItem(Index, 1);

				return;
			case "Shock Spell Stone":
				//Add shock spell to character spell list
				C.SPELL_LIST.add(C.SHOCK);
				
				//Remove item
				removeItem(Index, 1);
				return;

			case "Darkness Spell Stone":
				//Add darkness spell to character spell list
				C.SPELL_LIST.add(C.DARKNESS);
				
				//Remove item
				removeItem(Index, 1);
				return;
			case "Life Drain Spell Stone":
				//Add darkness spell to character spell list
				C.SPELL_LIST.add(C.LIFE_DRAIN);
				
				//Remove item
				removeItem(Index, 1);
				return;
	
				
		}
//////////////////////////////////////////////////////////WEAPON EQUIP/////////////////////////////////////////////////////////////////////////
		if(ITEM_TAG.elementAt(Index).indexOf("Meele")!=-1){
			//If the item is not equipped
			if(ITEM_NAME.elementAt(Index).indexOf("<Eq>")==-1){
				
				//"Unequip" all meele items	
				for(int i=0;i<ITEM_NAME.size();i++){
				
					if(ITEM_NAME.elementAt(i).indexOf("<Eq>")!=-1 && ITEM_TAG.elementAt(i).equalsIgnoreCase("Meele")){
						ITEM_NAME.setElementAt(ITEM_NAME.elementAt(i).substring(0, ITEM_NAME.elementAt(i).indexOf("<Eq>")),i);
					
					}
					
				}
				
				//Equip weapon
				C.MEELE_WEAPON.imgWEAPON=ITEM_IMAGE.elementAt(Index);
				C.MEELE_WEAPON.STATS=STATS.elementAt(Index);
				C.MEELE_WEAPON.NAME=ITEM_NAME.elementAt(Index);
				
				//Add the equipped item tag at the end of the item name
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index)+"<Eq>", Index);
			
			}else{
				//Unequip weapon
				C.MEELE_WEAPON.imgWEAPON=null;
				
				//remove the equipped item tag at the end of the item name
				int End=ITEM_NAME.elementAt(Index).length()-new String("<Eq>").length();
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index).substring(0, End), Index);
			}
		}
		
////////////////////////////////////////////HELMET & CAP EQUIP////////////////////////////////////////////////////////		
		if(ITEM_TAG.elementAt(Index).equalsIgnoreCase("H_armor")){
			//If not equipped
			if(ITEM_NAME.elementAt(Index).indexOf("<Eq>")==-1){
				
				//"Unequip" all head items	
				for(int i=0;i<ITEM_NAME.size();i++){
					
					if(ITEM_NAME.elementAt(i).indexOf("<Eq>")!=-1 && ITEM_TAG.elementAt(i).equalsIgnoreCase("H_armor")){
						ITEM_NAME.setElementAt(ITEM_NAME.elementAt(i).substring(0, ITEM_NAME.elementAt(i).indexOf("<Eq>")),i);
						C.STATS.ARMOR-=STATS.elementAt(i).ARMOR;
						C.STATS.MAGIC_RESIST-=STATS.elementAt(i).MAGIC_RESIST;
						
					}
					
				}
				
				//Equip  head armor
				C.STATS.ARMOR+=STATS.elementAt(Index).ARMOR;
				C.STATS.MAGIC_RESIST+=STATS.elementAt(Index).MAGIC_RESIST;
				
				//Add the equipped item tag at the end of the item name				
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index)+"<Eq>", Index);
			
			}else{
				
				//Unequip
				C.STATS.ARMOR-=STATS.elementAt(Index).ARMOR;
				
				//remove the equipped item tag at the end of the item name
				int End=ITEM_NAME.elementAt(Index).length()-new String("<Eq>").length();
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index).substring(0, End), Index);
			}

		}
		
//////////////////////////////////////////////CHEST ARMOR EQUIP////////////////////////////////////////////////////		
		if(ITEM_TAG.elementAt(Index).equalsIgnoreCase("C_armor")){
			
			//If not equipped
			if(ITEM_NAME.elementAt(Index).indexOf("<Eq>")==-1){
				
				//"Unequip" all chest items	
				for(int i=0;i<ITEM_NAME.size();i++){
					if(ITEM_NAME.elementAt(i).indexOf("<Eq>")!=-1 && ITEM_TAG.elementAt(i).equalsIgnoreCase("C_armor")){
						ITEM_NAME.setElementAt(ITEM_NAME.elementAt(i).substring(0, ITEM_NAME.elementAt(i).indexOf("<Eq>")),i);
						C.STATS.ARMOR-=STATS.elementAt(i).ARMOR;
						C.STATS.MAGIC_RESIST-=STATS.elementAt(i).MAGIC_RESIST;
						
					}
				}
				
				//Equip armor
				C.STATS.ARMOR+=STATS.elementAt(Index).ARMOR;
				C.STATS.MAGIC_RESIST+=STATS.elementAt(Index).MAGIC_RESIST;
				
				//Add the equipped item tag at the end of the item name
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index)+"<Eq>", Index);
			
			}else{
				
				//Unequip
				C.STATS.ARMOR-=STATS.elementAt(Index).ARMOR;
				
				//remove the equipped item tag at the end of the item name
				int End=ITEM_NAME.elementAt(Index).length()-new String("<Eq>").length();
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index).substring(0, End), Index);
			}

		}

		
////////////////////////////////////////////////SHIELD EQUIP//////////////////////////////////////////////////////
		
		if(ITEM_TAG.elementAt(Index).equalsIgnoreCase("Shield")){
			
			//If not equipped
			if(ITEM_NAME.elementAt(Index).indexOf("<Eq>")==-1){
				
				//"Unequip" all items	
				for(int i=0;i<ITEM_NAME.size();i++){
					
					if(ITEM_NAME.elementAt(i).indexOf("<Eq>")!=-1 && ITEM_TAG.elementAt(i).equalsIgnoreCase("Shield")){
						ITEM_NAME.setElementAt(ITEM_NAME.elementAt(i).substring(0, ITEM_NAME.elementAt(i).indexOf("<Eq>")),i);
						C.STATS.ARMOR-=STATS.elementAt(i).ARMOR;
						C.STATS.MAGIC_RESIST-=STATS.elementAt(i).MAGIC_RESIST;
						
					}
					
				}
				
				//Equip armor
				C.STATS.ARMOR+=STATS.elementAt(Index).ARMOR;
				C.STATS.MAGIC_RESIST+=STATS.elementAt(Index).MAGIC_RESIST;
				C.SHIELD=ITEM_IMAGE.elementAt(Index);
				
				//Add the equipped item tag at the end of the item name
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index)+"<Eq>", Index);
			
			}else{
				//Unequip
				C.STATS.ARMOR-=STATS.elementAt(Index).ARMOR;
				
				//Remove the equipped item tag at the end of the item name
				int End=ITEM_NAME.elementAt(Index).length()-new String("<Eq>").length();
				ITEM_NAME.setElementAt(ITEM_NAME.elementAt(Index).substring(0, End), Index);
				
				//No shield
				C.SHIELD=null;
			}

		}
		
	}
	
	/**
	 * Used to display the inventory in a list box
	 * @param lstInven Inventory
	 */
	public void displayItems(JList lstInven){
		Object[] List= new String[ITEM_IMAGE.size()+1];
	
		//Concat data
		for(int i=0;i<ITEM_IMAGE.size();i++){
			List[i]=(ITEM_QUANTITY.elementAt(i)+Space(8)+ITEM_NAME.elementAt(i)+Space(22-ITEM_NAME.elementAt(i).length()-1)+"~"+ITEM_PRICE.elementAt(i));
		}
		
		//Assign data
		lstInven.setListData(List);
	
	
	}
	
	/**
	 * Get the index of a hotkeyed item
	 * @param HKey Hotkey number
	 * @return Index
	 */
	public int findHotKey(int HKey){
		
		for(int i=0;i<HOTKEY.size();i++){
			if(HOTKEY.elementAt(i)==HKey){return i;}
		}
		
		return -1;
	}
	
	/**
	 * Set the hotkey number of an item
	 * @param Index index of the item
	 * @param HKey hotkey number
	 */
	public void setHotKey(int Index,int HKey){
		int Index2=findHotKey(HKey);
		
		if(Index2 !=-1){HOTKEY.setElementAt(-1,Index2); }
		HOTKEY.setElementAt(HKey, Index);;
		
		
	}
	
	
	/**
	 * Display the Inventory as Text
	 */
	public String toString(){
		String Text="Quantity:     Item Name:             Item Price:";
		
		for(int i=0;i<ITEM_IMAGE.size();i++){
			Text+=("\n"+ITEM_QUANTITY.elementAt(i)+Space(20-new String(ITEM_QUANTITY.elementAt(i)+"").length())+ITEM_NAME.elementAt(i)+Space(22-ITEM_NAME.elementAt(i).length())+ITEM_PRICE.elementAt(i));
		}
		return Text;
	}
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "Itemlist");
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