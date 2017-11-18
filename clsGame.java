import java.awt.*;

import javax.swing.*;
import javax.imageio.*;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.border.*;
import javax.swing.event.*;
/**
 * @category Application
 * 
 * Actual game window
 * 
 */

@SuppressWarnings("serial")
public class clsGame extends JFrame implements ActionListener,KeyListener,MouseListener,MouseMotionListener,ListSelectionListener{

	//Timer
	Timer tmrANIMATE= new Timer(1,this);
	
	//Basic framework
		//Main
	JPanel pnlTOP= new JPanel(),pnlCENTER= new JPanel(new BorderLayout()),pnlBOTTOM= new JPanel(new SpringLayout()),pnlLEFT=new JPanel(new BorderLayout()),pnlRIGHT=new JPanel(new BorderLayout());
		//List of player and target items
	JList<String> lstPLAYER_INVEN= new JList<String>(),lstTARGET_INVEN= new JList<String>();
		//Player inventory components
	JPanel pnlPLAYER_PIC= new JPanel(),pnlSUBTITLE= new JPanel(new BorderLayout()),pnlPLAYER_IN_TOP= new JPanel(),pnlPLAYER_IN_BOT= new JPanel(new GridLayout(2,1)),pnlPLAYER_IN_CENTER= new JPanel(new GridLayout()),pnlPLAYER_IN_SUBBOT= new JPanel(new GridLayout(1,2));
	JLabel lblPLAYER_ITEMNAME= new JLabel(),lblPLAYER_ITEMTITLE=new JLabel("Inventory"),lblSUBTITLE=new JLabel(),lblPLAYER_GOLD=new JLabel();
	JTextArea lblPLAYER_STATS= new JTextArea("Stats");
	JButton jbPLAYER_USE= new JButton("Use"),jbPLAYER_SELL= new JButton("Sell");
	
		//TARGET inventory components
	JPanel pnlTARGET_PIC= new JPanel(),pnlSUBTITLE2= new JPanel(new BorderLayout()),pnlTARGET_IN_TOP= new JPanel(),pnlTARGET_IN_BOT= new JPanel(new GridLayout(2,1)),pnlTARGET_IN_CENTER= new JPanel(new GridLayout()),pnlTARGET_IN_SUBBOT= new JPanel(new GridLayout(1,2));
	JLabel lblTARGET_ITEMNAME= new JLabel(),lblTARGET_ITEMTITLE=new JLabel("TARGET Inventory"),lblSUBTITLE2=new JLabel();
	JButton jbTARGET_TAKE= new JButton("Loot"),jbTARGET_BUY= new JButton("Buy");
	JTextArea lblTARGET_STATS= new JTextArea("Stats");
	
	//Toggle button for inventory
	JToggleButton jbINVENTOGGLE= new JToggleButton("Inventory"),jbSTATSTOGGLE= new JToggleButton("Player Stats"), jbUPGRADES= new JToggleButton("Upgrades"),jbQUESTTOGGLE= new JToggleButton("Quests");
	
	//Special Fonts
	Font fntDRAGON=new Font("Arial",20,20),fntTITLE=new Font("Arial",20,20),fntDIABLO=new Font("Arial",20,20),fntOLD1=new Font("Arial",20,20),fntOLD2=new Font("Arial",20,20),fntOLD3=new Font("Arial",20,20),fntOLD4=new Font("Arial",20,20),fntOLD5=new Font("Arial",20,20),fntGOW=new Font("Arial",20,20);
	
	//Maps
	GridMap MAP= new GridMap(0,0,0);
	GridMap MINI_MAP= new GridMap(0,0,0);
	

	
	//Characters
	//GameCharacter PLAYER= new GameCharacter(100,100,80,65,"Player.png",3,1,"Player");
	GameCharacter PLAYER= new GameCharacter(100,100,32,48,"Player1.png",3,1,"Player");
	GameCharacter PRISONER=null,BANDIT_BOSS=null;
	GameCharacter[] NPC=null,SKELETON=null,SHAMAN=null,DARKELF=null,BANDIT=null;
	GameCharacter gchSELECTED=null;
	Vector<GameCharacter> ACTIVE_CHARS= new Vector<GameCharacter>();
	
	//Inventory
	ItemList TARGET;
	
	
	//Objects
	GameObject FOUNTAIN,TIPSTONES[],FORGE,TORCH[],CAULDRON,BED[],CHEST[],DOOR[];
	GameObject gobSELECTED=null;
	Vector<GameObject> ACTIVE_OBJECTS= new Vector<GameObject>();
	
	//Building
	Building WORKSHOP,ALCHEMIST,PORTALS[],DUNGEON,DUNGEON_EXIT;
	Building gbuSELECTED=null;
	
	
	//Draw displacement
	int DX=0,DY=0;
	
	//Graphics
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Backgrounds\\",ICON_DIR=System.getProperty("user.dir")+"\\Graphics\\Icon\\";
	Image imgBOTTOM;
	
	//Toggle variables
	boolean blnPLAYER_INVENTOGGLE=false,blnTARGET_INVENTOGGLE=false,blnFOG=false;
	boolean blnPAUSE=false,blnHELP;
	
	//List Index
	int INDEX=0,TARGETINDEX=0,MAGIC_INDEX=0;
	
	//Map Name
	String MNAME="";
	
	//Player stats
	Stats_Window StatsWindow=new Stats_Window(PLAYER,pnlCENTER);
	
	//Player level up window
	Upgrades_Window Upgrades_Panel=new Upgrades_Window(PLAYER,pnlCENTER);
	
	//Quest window
	Quest_Window QUEST_WINDOW;
	
	
	//Background music
	AudioEffects audBGM= new AudioEffects(true);
	AudioEffects audInven= new AudioEffects(false),audStats= new AudioEffects(false),audQuests= new AudioEffects(false);
	
	MsgBox ALERT;
	
	/**
	 * Start Up constructor
	 */
	public clsGame(String MapName,boolean Fog){
		
		//Initialization
		super("");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setLayout(new BorderLayout());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);

		//Make Stats Invisible
		Upgrades_Panel.setVisible(false);
		StatsWindow.setVisible(false);

		
		//Get Map Name
		MNAME=MapName;
		blnFOG=Fog;

		
		//Get special fonts
		String Dir=System.getProperty("user.dir")+"\\Graphics\\Fonts\\";
		try {
			
			fntDRAGON=Font.createFont(Font.PLAIN, Paths.get(Dir+"Dovahkiin.otf").toFile());
			fntTITLE=Font.createFont(Font.PLAIN, Paths.get(Dir+"Cartoon.ttf").toFile());
			fntDIABLO=Font.createFont(Font.PLAIN, Paths.get(Dir+"Diablo.ttf").toFile());
			fntOLD1=Font.createFont(Font.PLAIN, Paths.get(Dir+"Old1.ttf").toFile());
			fntOLD2=Font.createFont(Font.PLAIN, Paths.get(Dir+"Old2.ttf").toFile());
			fntOLD3=Font.createFont(Font.PLAIN, Paths.get(Dir+"Old3.ttf").toFile());
			fntOLD4=Font.createFont(Font.PLAIN, Paths.get(Dir+"Old4.ttf").toFile());
			fntOLD5=Font.createFont(Font.PLAIN, Paths.get(Dir+"Old5.ttf").toFile());
			fntGOW=Font.createFont(Font.PLAIN, Paths.get(Dir+"GOW.ttf").toFile());
			

		} catch (Exception e) {System.out.print("asd");}

				
		
		//Set up framework
		
		//Top
		this.add(pnlTOP,BorderLayout.NORTH);
		//Style
		pnlTOP.setBorder(BorderFactory.createDashedBorder(Color.black));
		pnlTOP.setOpaque(true);
		pnlTOP.setBackground(Color.black);
			//Inventory toggle
		pnlTOP.add(jbINVENTOGGLE);
		jbINVENTOGGLE.addActionListener(this);
		jbINVENTOGGLE.setBackground(Color.black);
		jbINVENTOGGLE.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbINVENTOGGLE.setFont(fntDIABLO.deriveFont(Font.PLAIN, 30));
		jbINVENTOGGLE.setForeground(Color.white);
			//Stats Toggle
		pnlTOP.add(jbSTATSTOGGLE);
		jbSTATSTOGGLE.addActionListener(this);
		jbSTATSTOGGLE.setBackground(Color.black);
		jbSTATSTOGGLE.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbSTATSTOGGLE.setFont(fntDIABLO.deriveFont(Font.PLAIN, 30));
		jbSTATSTOGGLE.setForeground(Color.white);
		//Upgrades Toggle
		pnlTOP.add(jbUPGRADES);
		jbUPGRADES.addActionListener(this);
		jbUPGRADES.setBackground(Color.black);
		jbUPGRADES.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbUPGRADES.setFont(fntDIABLO.deriveFont(Font.PLAIN, 30));
		jbUPGRADES.setForeground(Color.white);
		//Quest Log Toggle
		pnlTOP.add(jbQUESTTOGGLE);
		jbQUESTTOGGLE.addActionListener(this);
		jbQUESTTOGGLE.setBackground(Color.black);
		jbQUESTTOGGLE.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbQUESTTOGGLE.setFont(fntDIABLO.deriveFont(Font.PLAIN, 30));
		jbQUESTTOGGLE.setForeground(Color.white);
		
		
		//Left
		this.add(pnlLEFT,BorderLayout.WEST);
		//Style
		pnlLEFT.setPreferredSize(new Dimension(this.getWidth()/2-10,100));
		pnlLEFT.setVisible(false);
			//Top
		pnlLEFT.add(pnlPLAYER_IN_TOP,BorderLayout.NORTH);
			//Style
		pnlPLAYER_IN_TOP.setOpaque(true);
		pnlPLAYER_IN_TOP.setBackground(Color.black);
				//Title "Inventory
		pnlPLAYER_IN_TOP.add(lblPLAYER_ITEMTITLE);
		lblPLAYER_ITEMTITLE.setForeground(Color.white);
		lblPLAYER_ITEMTITLE.setFont(fntTITLE.deriveFont(Font.PLAIN,50));
			//Center
		pnlLEFT.add(pnlPLAYER_IN_CENTER,BorderLayout.CENTER);
	
				//Item pic
		pnlPLAYER_IN_CENTER.add(pnlPLAYER_PIC);
				//Style
		pnlPLAYER_PIC.setOpaque(true);
		pnlPLAYER_PIC.setBackground(Color.black);
	
				//Sub title panel
		pnlPLAYER_IN_CENTER.add(pnlSUBTITLE);
				//Style
		pnlSUBTITLE.setOpaque(true);
		pnlSUBTITLE.setBackground(Color.black);
		
					//Sub title
		pnlSUBTITLE.add(lblSUBTITLE,BorderLayout.NORTH);
		lblSUBTITLE.setForeground(Color.white);
		lblSUBTITLE.setFont(fntTITLE.deriveFont(Font.PLAIN,30));
		lblSUBTITLE.setText("Qnt.  Name:             Price:");

					//Inventory List
		JScrollPane scroll_pane= new JScrollPane(lstPLAYER_INVEN,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlSUBTITLE.add(scroll_pane,BorderLayout.CENTER);
					//Style
		lstPLAYER_INVEN.setOpaque(true);
		lstPLAYER_INVEN.setBackground(Color.black);
		lstPLAYER_INVEN.setForeground(Color.white);
		
		lstPLAYER_INVEN.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,3)));
		lstPLAYER_INVEN.setFont(new Font("Arial",20,20));
		
		pnlSUBTITLE.add(lblPLAYER_STATS,BorderLayout.SOUTH);
		lblPLAYER_STATS.setOpaque(true);
		lblPLAYER_STATS.setBackground(Color.black);
		lblPLAYER_STATS.setForeground(Color.white);
		lblPLAYER_STATS.setEditable(false);
		lblPLAYER_STATS.setLineWrap(true);
		lblPLAYER_STATS.setFont(fntGOW.deriveFont(Font.PLAIN,25));
			//Bottom
		pnlLEFT.add(pnlPLAYER_IN_BOT,BorderLayout.SOUTH);
			//Style
		pnlPLAYER_IN_BOT.setOpaque(true);
		pnlPLAYER_IN_BOT.setBackground(Color.black);
				//Item name in dohvakin
		pnlPLAYER_IN_BOT.add(lblPLAYER_ITEMNAME);
				//Style
		lblPLAYER_ITEMNAME.setForeground(Color.white);
		lblPLAYER_ITEMNAME.setFont(fntDRAGON.deriveFont(Font.PLAIN,45));
		lblPLAYER_ITEMNAME.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,5)));
				//Buttons
		pnlPLAYER_IN_BOT.add(pnlPLAYER_IN_SUBBOT);
				//Style
		pnlPLAYER_IN_SUBBOT.setOpaque(true);
		pnlPLAYER_IN_SUBBOT.setBackground(Color.black);
					//Use button
		pnlPLAYER_IN_SUBBOT.add(jbPLAYER_USE);
					//Style
		jbPLAYER_USE.setBackground(Color.black);
		jbPLAYER_USE.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbPLAYER_USE.setFont(fntDIABLO.deriveFont(Font.PLAIN, 40));
		jbPLAYER_USE.setForeground(Color.white);
		jbPLAYER_USE.setMnemonic('e');
		jbPLAYER_USE.addActionListener(this);
					//Sell button
		pnlPLAYER_IN_SUBBOT.add(jbPLAYER_SELL);
					//Style
		jbPLAYER_SELL.setBackground(Color.black);
		jbPLAYER_SELL.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbPLAYER_SELL.setFont(fntDIABLO.deriveFont(Font.PLAIN, 40));
		jbPLAYER_SELL.setForeground(Color.white);
		jbPLAYER_SELL.setMnemonic('s');
		jbPLAYER_SELL.addActionListener(this);
					//Gold Label button
		pnlPLAYER_IN_SUBBOT.add(lblPLAYER_GOLD);
					//Style
		lblPLAYER_GOLD.setBackground(Color.black);
		lblPLAYER_GOLD.setFont(fntDIABLO.deriveFont(Font.PLAIN, 30));
		lblPLAYER_GOLD.setForeground(Color.white);
	
		
		
		//Right
		
		this.add(pnlRIGHT,BorderLayout.EAST);
		//Style
		pnlRIGHT.setPreferredSize(new Dimension(this.getWidth()/2-10,100));
		pnlRIGHT.setVisible(false);
			//Top
		pnlRIGHT.add(pnlTARGET_IN_TOP,BorderLayout.NORTH);
			//Style
		pnlTARGET_IN_TOP.setOpaque(true);
		pnlTARGET_IN_TOP.setBackground(Color.black);
				//Title "Inventory
		pnlTARGET_IN_TOP.add(lblTARGET_ITEMTITLE);
		lblTARGET_ITEMTITLE.setForeground(Color.white);
		lblTARGET_ITEMTITLE.setFont(fntTITLE.deriveFont(Font.PLAIN,50));
			//Center
		pnlRIGHT.add(pnlTARGET_IN_CENTER,BorderLayout.CENTER);
	
				//Item pic
		pnlTARGET_IN_CENTER.add(pnlTARGET_PIC);
				//Style
		pnlTARGET_PIC.setOpaque(false);
		
				//Sub title panel
		pnlTARGET_IN_CENTER.add(pnlSUBTITLE2);
				//Style
		pnlSUBTITLE2.setOpaque(true);
		pnlSUBTITLE2.setBackground(Color.black);
		
					//Sub title
		pnlSUBTITLE2.add(lblSUBTITLE2,BorderLayout.NORTH);
					//Style
		lblSUBTITLE2.setForeground(Color.white);
		lblSUBTITLE2.setFont(fntTITLE.deriveFont(Font.PLAIN,30));
		lblSUBTITLE2.setText("Qnt.  Name:             Price:");
		
					//Inventory List
		JScrollPane scroll_pane2= new JScrollPane(lstTARGET_INVEN,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlSUBTITLE2.add(scroll_pane2,BorderLayout.CENTER);
					//Style
		lstTARGET_INVEN.setOpaque(true);
		lstTARGET_INVEN.setBackground(Color.black);
		lstTARGET_INVEN.setForeground(Color.white);
		lstTARGET_INVEN.addListSelectionListener(this);
		lstTARGET_INVEN.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,3)));
		lstTARGET_INVEN.setFont(new Font("Arial",20,20));
		
			//Stats button
		pnlSUBTITLE2.add(lblTARGET_STATS,BorderLayout.SOUTH);
					//Style
		lblTARGET_STATS.setOpaque(true);
		lblTARGET_STATS.setBackground(Color.black);
		lblTARGET_STATS.setForeground(Color.white);
		lblTARGET_STATS.setEditable(false);
		lblTARGET_STATS.setLineWrap(true);
		lblTARGET_STATS.setFont(fntGOW.deriveFont(Font.PLAIN,25));
			//Bottom
		pnlRIGHT.add(pnlTARGET_IN_BOT,BorderLayout.SOUTH);
			//Style
		pnlTARGET_IN_BOT.setOpaque(true);
		pnlTARGET_IN_BOT.setBackground(Color.black);
		pnlTARGET_IN_BOT.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,5)));
				//Item name in dohvakin
		pnlTARGET_IN_BOT.add(lblTARGET_ITEMNAME);
				//Style
		lblTARGET_ITEMNAME.setForeground(Color.white);
		lblTARGET_ITEMNAME.setFont(fntDRAGON.deriveFont(Font.PLAIN,45));
				//Buttons
		pnlTARGET_IN_BOT.add(pnlTARGET_IN_SUBBOT);
				//Style
		pnlTARGET_IN_SUBBOT.setOpaque(true);
		pnlTARGET_IN_SUBBOT.setBackground(Color.black);
					//Use button
		pnlTARGET_IN_SUBBOT.add(jbTARGET_TAKE);
					//Style
		jbTARGET_TAKE.setBackground(Color.black);
		jbTARGET_TAKE.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbTARGET_TAKE.setFont(fntDIABLO.deriveFont(Font.PLAIN, 40));
		jbTARGET_TAKE.setForeground(Color.white);
		jbTARGET_TAKE.setMnemonic('l');
		jbTARGET_TAKE.addActionListener(this);
					//Sell button
		pnlTARGET_IN_SUBBOT.add(jbTARGET_BUY);
					//Style
		jbTARGET_BUY.setBackground(Color.black);
		jbTARGET_BUY.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.white,2)));
		jbTARGET_BUY.setFont(fntDIABLO.deriveFont(Font.PLAIN, 40));
		jbTARGET_BUY.setForeground(Color.white);
		jbTARGET_BUY.addActionListener(this);
		jbTARGET_BUY.setMnemonic('b');
		
		
		
		
		
		
		//Bottom
		this.add(pnlBOTTOM,BorderLayout.SOUTH);
		pnlBOTTOM.setBorder(BorderFactory.createDashedBorder(Color.black));
		pnlBOTTOM.setPreferredSize(new Dimension(this.getSize().width,150));
		
		//Center
		this.add(pnlCENTER,BorderLayout.CENTER);
		
		//Loading Screen
		setVisible(false);
		Loading_Screen LOAD= new Loading_Screen();
		try{
			Image img=null;
			if(isJar()){
				img=this.getImage("\\Graphics\\Backgrounds\\","Load.jpg");
			}else{
				img=ImageIO.read(Paths.get(DIRECT+"Load.jpg").toFile());
			}
			LOAD.getGraphics().drawImage(img, 0, 0,LOAD.getWidth(),LOAD.getHeight(), null);
			LOAD.getGraphics().drawString("Loading...",20, 35);
			
		}catch(Exception e){}
		
		//Setup the map
		Setup(MapName,false);
		
		//Set player speed
		PLAYER.SPEED=5;
		
		//Component Listeners
		this.addKeyListener(this);
		pnlCENTER.addMouseMotionListener(this);
		jbINVENTOGGLE.addKeyListener(this);
		jbSTATSTOGGLE.addKeyListener(this);
		jbQUESTTOGGLE.addKeyListener(this);
		jbUPGRADES.addKeyListener(this);
		jbPLAYER_SELL.addKeyListener(this);
		jbPLAYER_USE.addKeyListener(this);
		jbTARGET_BUY.addKeyListener(this);
		jbTARGET_TAKE.addKeyListener(this);
		pnlLEFT.addKeyListener(this);
		lstPLAYER_INVEN.addKeyListener(this);
		lstPLAYER_INVEN.addListSelectionListener(this);
		lstTARGET_INVEN.addListSelectionListener(this);
		pnlCENTER.addMouseListener(this);
		//cmbMAGIC.addKeyListener(this);
		
		
		//Remove loading screen
		LOAD.setVisible(false);
		setVisible(true);
		QUEST_WINDOW=new Quest_Window(new Font[]{fntOLD2,fntDIABLO,fntGOW},pnlCENTER.getWidth()/2,pnlTOP.getHeight()+pnlCENTER.getHeight()/2,500,600,PLAYER,jbQUESTTOGGLE);
		
		
		//Start timer
		tmrANIMATE.start();
		
		//Initialize progress bar
		PLAYER.BASIC_PROCESS= new ProgressBar(fntDIABLO.deriveFont(Font.PLAIN,36),PLAYER);
		ALERT=new MsgBox(fntDIABLO,fntGOW,this);
		//this.addKeyListener(ALERT);
		//JOptionPane.showMessageDialog(null, "Dun with constructor");
	}
	
	/**
	 * Initialize class instance
	 * @param args
	 */
	public static void main(String[] args){
		clsGame ABC= new clsGame("Town",false);
		//clsGame ABC= new clsGame("Town",false);
		//clsGame ABC= new clsGame("dungeon",true);
		
	}
	
	/**
	 * Control timers and click events
	 */
	public void actionPerformed(ActionEvent event){
///////////////////////////////////////////////////////TIMER//////////////////////////////////////////////////////////		
		if(event.getSource()==tmrANIMATE){
			//Update graphics 
			
			update(pnlCENTER.getGraphics());
			update2(pnlBOTTOM.getGraphics());
			
			//Update Inventory graphics
			if(blnPLAYER_INVENTOGGLE){
				//Player Inventory
				update3(pnlPLAYER_PIC.getGraphics());
			}
			
			if(blnTARGET_INVENTOGGLE){
				//Target Inventory
				update4(pnlTARGET_PIC.getGraphics());
			}
			
			update5(StatsWindow.pnlLeft.getGraphics());
			update6(Upgrades_Panel.getGraphics());
			
			//Shift the map for special viewing
			MapShift();
			
			//Player Mana regen
			PLAYER.ManaRegen(2);
			
			for(int i=0;i<PLAYER.QUEST_LIST.size();i++){
				PLAYER.QUEST_LIST.elementAt(i).UpdateQuest();
			}
			
			
			
			
			//XP update
			switch(MNAME.toLowerCase()){
				case "town":
					//Xp from killing npcs
					if(SKELETON!=null){
						PLAYER.KillXP(SKELETON);
					}
					break;
				case "dungeon":
					if(SHAMAN!=null){
						PLAYER.KillXP(SHAMAN);
					}
					if(DARKELF!=null){
						PLAYER.KillXP(DARKELF);
					}
					break;
			}
			
			//Enable/disable buttons
			jbPLAYER_SELL.setEnabled(pnlRIGHT.isVisible() &&(PLAYER.INVENTORY.ITEM_NAME.size()>0));
			if(TARGET!=null){
				jbTARGET_BUY.setEnabled((TARGET.ITEM_NAME.size()>0 && !TARGET.OPEN_INVEN));
				jbTARGET_TAKE.setEnabled(TARGET.OPEN_INVEN);
				jbPLAYER_SELL.setEnabled(!TARGET.OPEN_INVEN);
			}
			
			jbUPGRADES.setVisible(PLAYER.STATS.XP_POINTS>0);
			
			

		}
		
///////////////////////////////////////////////////////DISPLAY UPGRADES PANEL//////////////////////////////////////////////////////////		
		else if(event.getSource()==jbUPGRADES){
			Upgrades_Panel.setVisible(jbUPGRADES.isSelected());
			Upgrades_Panel.requestFocus();
		}		
		
///////////////////////////////////////////////////////DISPLAY PLAYER STATS//////////////////////////////////////////////////////////		
		else if(event.getSource()==jbSTATSTOGGLE){
			
			StatsWindow.lblSTATS.setText(PLAYER.STATS.toString());
			StatsWindow.setVisible(jbSTATSTOGGLE.isSelected());
			StatsWindow.requestFocus();
			audStats.playAudio("Stats.wav");
		}		
///////////////////////////////////////////////////////DISPLAY PLAYER INVENTORY BUTTON//////////////////////////////////////////////////////////		
		else if(event.getSource()==jbINVENTOGGLE){
			
			//Toggle
			blnPLAYER_INVENTOGGLE=!blnPLAYER_INVENTOGGLE;
			pnlLEFT.setVisible(blnPLAYER_INVENTOGGLE);
			//Set default index
			PLAYER.INVENTORY.displayItems(lstPLAYER_INVEN);
			INDEX=0;
			audInven.playAudio("Inventory.wav");
		}
///////////////////////////////////////////////////////DISPLAY PLAYER INVENTORY BUTTON//////////////////////////////////////////////////////////		
		else if(event.getSource()==jbQUESTTOGGLE){
			QUEST_WINDOW.Toggle();
			QUEST_WINDOW.setPulse(false);
			audQuests.playAudio("quest window.wav");
		}		
///////////////////////////////////////////////////////USE BUTTON//////////////////////////////////////////////////////////		
		else if(event.getSource()==jbPLAYER_USE){
			if(INDEX<0||INDEX>PLAYER.INVENTORY.ITEM_NAME.size())return;
			//Get index
			int Ind=(PLAYER.INVENTORY.ITEM_QUANTITY.elementAt(INDEX)>1 ||!PLAYER.INVENTORY.ITEM_TAG.elementAt(INDEX).equalsIgnoreCase("potion") )?INDEX:0;
			//Use Item
			PLAYER.INVENTORY.ItemEffect(INDEX, PLAYER);
			//Update Player inventory
			PLAYER.INVENTORY.displayItems(lstPLAYER_INVEN);
			update3(pnlPLAYER_PIC.getGraphics());
			//Update index
			lstPLAYER_INVEN.setSelectedIndex(Ind);
			
		}
		
///////////////////////////////////////////////////////SELL BUTTON//////////////////////////////////////////////////////////				
		else if(event.getSource()==jbPLAYER_SELL){
			if(INDEX<0||INDEX>PLAYER.INVENTORY.ITEM_NAME.size())return;
			//if no index is selected
			if((TARGET==null)||(INDEX<0))return;
			//Get Index
			int Ind=(PLAYER.INVENTORY.ITEM_QUANTITY.elementAt(INDEX)>1)?INDEX:0;
			//Sell item to trader
			PLAYER.Sell(TARGET, INDEX);
			//Update Player Inventory
			PLAYER.INVENTORY.displayItems(lstPLAYER_INVEN);
			update3(pnlPLAYER_PIC.getGraphics());
			//Update Target Inventory
			TARGET.displayItems(lstTARGET_INVEN);
			update4(pnlTARGET_PIC.getGraphics());
			//Update index
			lstPLAYER_INVEN.setSelectedIndex(Ind);
			
		}
		
///////////////////////////////////////////////////////BUY BUTTON//////////////////////////////////////////////////////////						
		else if(event.getSource()==jbTARGET_BUY){
			//if no index is selected
			if((TARGET==null))return;
			if(TARGETINDEX<0||TARGETINDEX>TARGET.ITEM_NAME.size())return;
			//Get Index
			int Ind=(TARGET.ITEM_QUANTITY.elementAt(TARGETINDEX)>1)?TARGETINDEX:0;
			//Buy item from trader
			PLAYER.Buy(TARGET, TARGETINDEX);
			//Update Player Inventory
			PLAYER.INVENTORY.displayItems(lstPLAYER_INVEN);
			update3(pnlPLAYER_PIC.getGraphics());
			//Update Target Inventory
			TARGET.displayItems(lstTARGET_INVEN);
			update4(pnlTARGET_PIC.getGraphics());
			//Update targetindex
			lstTARGET_INVEN.setSelectedIndex(Ind);	
			
			if(TARGET.ITEM_NAME.size()==0){blnTARGET_INVENTOGGLE=false;pnlRIGHT.setVisible(false);}
		}
		
///////////////////////////////////////////////////////LOOT BUTTON//////////////////////////////////////////////////////////								
		else if(event.getSource()==jbTARGET_TAKE){
			//if no index is selected
			if((TARGET==null))return;
			if(TARGETINDEX<0||TARGETINDEX>TARGET.ITEM_NAME.size())return;
			
			//Get Index
			int Ind=(TARGET.ITEM_QUANTITY.elementAt(TARGETINDEX)>1)?TARGETINDEX:0;
			
			//Take item from the target
			PLAYER.Take(TARGET, TARGETINDEX);
			
			//Update Player Inventory
			PLAYER.INVENTORY.displayItems(lstPLAYER_INVEN);
			update3(pnlPLAYER_PIC.getGraphics());
			
			//Update Target Inventory
			TARGET.displayItems(lstTARGET_INVEN);
			update4(pnlTARGET_PIC.getGraphics());
			
			//Update targetindex
			lstTARGET_INVEN.setSelectedIndex(Ind);
			
			if(TARGET.ITEM_NAME.size()==0){blnTARGET_INVENTOGGLE=false;pnlRIGHT.setVisible(false);}
			
		}
		
	}
	
	/**
	 *Update Center Panel Graphics 
	 */
	public void update(Graphics g){
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//What to draw
		
			//Background
		dbg.fill3DRect(0, 0, pnlCENTER.getWidth(), pnlCENTER.getHeight(), false);

////////////////////////////////////////////////////////////////MAIN MAP/////////////////////////////////////////////////////////////////////////////////////
		
			//Map
		MAP.Draw(dbg, DX, DY);
		
		
			//Map related elements
		switch(MNAME){
			case "Town":				
			
				//Objects and buildings
					//Fountain
				FOUNTAIN.Draw(dbg, DX, DY, 2);
					//Forge
				FORGE.Draw(dbg, DX, DY, 1.5);
					//Workshop
				WORKSHOP.Draw(dbg, DX, DY, 1.25);
					//Alchemist Hut
				ALCHEMIST.Draw(dbg, DX, DY, 1.5);
					//Cauldron
				CAULDRON.Draw(dbg, DX, DY, 0.6);
					//Dungeon
				DUNGEON.Draw(dbg, DX, DY, 1.5);
					//Bed
				for(int i=0;i<BED.length;i++){
					BED[i].Draw(dbg, DX, DY, 1.5);
				}	
					//Portal
				for(int i=0;i<PORTALS.length;i++){
					PORTALS[i].Draw(dbg, DX, DY, 2);
				}
					//Chest
				for(int i=0;i<CHEST.length;i++){
					CHEST[i].Draw(dbg, DX, DY, 1.5);
				}
				
				//Characters
					//NPC
				for(int i=0;i<NPC.length;i++){
					NPC[i].Draw(dbg, DX, DY, 1.35);
				}
					//SKELETON
				for(int i=0;i<SKELETON.length;i++){
					SKELETON[i].Draw(dbg, DX, DY, 1.5);
					SKELETON[i].MEELE_WEAPON.Draw(dbg, DX, DY, 1,SKELETON[i].X+SKELETON[i].W/2,SKELETON[i].Y+SKELETON[i].H/2);
					
				}
				
			break;
			case "dungeon":
				DUNGEON_EXIT.Draw(dbg, DX, DY, 2);
				if(SHAMAN!=null){
					for(int i=0;i<SHAMAN.length;i++){
						SHAMAN[i].Draw(dbg, DX, DY, 2.25);
						SHAMAN[i].MEELE_WEAPON.Draw(dbg, DX, DY, 1,SHAMAN[i].X+SHAMAN[i].W/2,SHAMAN[i].Y+SHAMAN[i].H/2);
					}
				}
				if(DARKELF!=null){
					for(int i=0;i<DARKELF.length;i++){
						DARKELF[i].Draw(dbg, DX, DY, 2.25);
						DARKELF[i].MEELE_WEAPON.Draw(dbg, DX, DY, 1,DARKELF[i].X+DARKELF[i].W/2,DARKELF[i].Y+DARKELF[i].H/2);
					}
				}
			break;	
			case "Tutorial":
				//Torchs
			for(int i=0;i<TORCH.length;i++){
				TORCH[i].Draw(dbg, DX, DY, 1.5);
			}
			for(int i=0;i<DOOR.length;i++){
				DOOR[i].Draw(dbg, DX, DY, 1.5);
			}
			PRISONER.Draw(dbg, DX, DY, 1.35);
			
			for(int i=0;i<BANDIT.length;i++){
				BANDIT[i].Draw(dbg, DX, DY, 1.5);
			}
			//Bed
			for(int i=0;i<BED.length;i++){
				BED[i].Draw(dbg, DX, DY, 1.5);
			}	
			//Bed
			for(int i=0;i<TIPSTONES.length;i++){
				TIPSTONES[i].Draw(dbg, DX, DY, 1.5);
			}	
			
				break;
		}
		
		//Player
			//Character
		PLAYER.Draw(dbg, DX, DY,1.35);
			//MagicAttack
		double G=1;
		switch(PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX)[0].NAME.toLowerCase()){
			case "fireball":
			case "shock":
			case "darkness":
				G=1.5;
				break;
			
			case "life drain":
				G=3;
				break;
		}
		
		
		PLAYER.MagicDraw(PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX), dbg, DX, DY, G);
			//Sword
		if(PLAYER.MEELE_WEAPON!=null){
			PLAYER.MEELE_WEAPON.Draw(dbg, DX, DY, 1,PLAYER.X+PLAYER.W/2,PLAYER.Y+PLAYER.H/2);
		}
		//Draw the fog
		if(blnFOG){
			MAP.FogDraw(dbg, DX, DY);
			
		}
		
///////////////////////////////////////////////////////////////////MINI MAP//////////////////////////////////////////

			//BACKGROUND
		dbg.setColor(new Color(0,0,0,200));
		dbg.draw3DRect(pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size-10, 0, MINI_MAP.BX*MINI_MAP.Size+10, MINI_MAP.BY*MINI_MAP.Size+10, true);
		dbg.fill3DRect(pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size-10, 0, MINI_MAP.BX*MINI_MAP.Size+10, MINI_MAP.BY*MINI_MAP.Size+10, true);
			//MINI MAP
		MINI_MAP.Draw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0);
			//MINI MAP FOG
		if(blnFOG){
			
			MINI_MAP.Fog=MAP.Fog;
			
			//Fog of war
			for(int i=0;i<MINI_MAP.BRIGHT.length;i++){
				MINI_MAP.BRIGHT[i]=2;
			}
			
			//Fog
			MINI_MAP.FogDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0);
		}

			//PLAYER 
		PLAYER.MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, ((double)MINI_MAP.Size/MAP.Size),Color.green.darker());
		
			//Draw Character depending on map
		switch(MNAME){
			case "Town":				
				//NPC
				NPC[0].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, ((double)MINI_MAP.Size/MAP.Size),Color.white);
				NPC[1].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, ((double)MINI_MAP.Size/MAP.Size),Color.white);
				NPC[2].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, ((double)MINI_MAP.Size/MAP.Size),Color.white);
				NPC[3].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, ((double)MINI_MAP.Size/MAP.Size),Color.white);
				for(int i=0;i<NPC.length;i++){
					if(NPC[i].NAI.State.equalsIgnoreCase("quest")){
						NPC[i].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, ((double)MINI_MAP.Size/MAP.Size),Color.yellow);
					}
				}
				//PORTAL
				PORTALS[0].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, 0.2, ((double)MINI_MAP.Size/MAP.Size));
				PORTALS[1].MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, 0.2, ((double)MINI_MAP.Size/MAP.Size));
				
				//DUNGEON
				DUNGEON.MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, 0.15, ((double)MINI_MAP.Size/MAP.Size));
				break;
			case "dungeon":
				//DUNGEON
				DUNGEON_EXIT.MapDraw(dbg, pnlCENTER.getWidth()-MINI_MAP.BX*MINI_MAP.Size, 0, 0.15, ((double)MINI_MAP.Size/MAP.Size));
				
		}	
		
		//Draw Progress bar
		if(PLAYER.BASIC_PROCESS.STATUS.equalsIgnoreCase("active")){
			PLAYER.BASIC_PROCESS.Draw(dbg, pnlCENTER.getWidth(), pnlCENTER.getHeight());
		}
		
////////////////////////////////////////////////////////CHARACTER HEALTH//////////////////////////////////////////
		
		//MouseOver character
		if(gchSELECTED!=null){
			//Display Character Type
				//Set Font
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN, 25));
				//Back Box
			dbg.setColor(Color.black);
			FontMetrics fntMet=this.getFontMetrics(dbg.getFont());
			dbg.fill3DRect((int)(gchSELECTED.X+DX-gchSELECTED.W/2-2), (int)(gchSELECTED.Y+DY-22), fntMet.stringWidth(gchSELECTED.CLASS)+5, 22, false);
				//Draw Name
			dbg.setColor(Color.white);
			dbg.drawString(gchSELECTED.CLASS, (int)(gchSELECTED.X+DX-gchSELECTED.W/2), (int)(gchSELECTED.Y+DY-5));
		
			//Display character health
				//Scale health
			double TARGETHealth=(gchSELECTED.HEALTH/gchSELECTED.MAX_HEALTH)*700;
				//Back Box
			dbg.setColor(Color.gray.darker().darker());
			dbg.fillRoundRect(this.getWidth()/2-700/2, 10, 700,75, 20, 20);
				//Health bar
			dbg.setColor(Color.getHSBColor(0f, 0.99f, 0.62f));
			dbg.fillRoundRect(this.getWidth()/2-700/2, 10, (int)Math.round(TARGETHealth),75, 20,20);
				//Character class
			dbg.setColor(Color.white);
			dbg.setFont(fntOLD3.deriveFont(Font.PLAIN, 40));
			dbg.drawString(gchSELECTED.CLASS, this.getWidth()/2-gchSELECTED.CLASS.length()*8, 40);
				//Character Level
			dbg.setFont(fntOLD5.deriveFont(Font.PLAIN, 24));
			dbg.drawString("Level: "+gchSELECTED.STATS.LEVEL, this.getWidth()/2-40, 62);
				//Health
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN, 20));
			dbg.drawString("Health: "+gchSELECTED.HEALTH+"/"+Math.round(gchSELECTED.MAX_HEALTH), this.getWidth()/2-80, 77);
		}

////////////////////////////////////////////////////////////DISPLAY NAME//////////////////////////////////////////////////////
		
		else if(gobSELECTED!=null){
			//Display Character Type
			//Set Font
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN, 25));
			//Back Box
			dbg.setColor(Color.black);
			FontMetrics fntMet=this.getFontMetrics(dbg.getFont());
			dbg.fill3DRect((int)(gobSELECTED.X+DX-2), (int)(gobSELECTED.Y+DY-22), fntMet.stringWidth(gobSELECTED.CLASS)+5, 22, false);
			//Draw Name
			dbg.setColor(Color.white);
			dbg.drawString(gobSELECTED.CLASS, (int)(gobSELECTED.X+DX), (int)(gobSELECTED.Y+DY-5));
	
	    }
		else if(gbuSELECTED!=null){
			//Display Character Type
			//Set Font
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN, 25));
			//Back Box
			dbg.setColor(Color.black);
			FontMetrics fntMet=this.getFontMetrics(dbg.getFont());
			dbg.fill3DRect((int)(gbuSELECTED.X+DX-2), (int)(gbuSELECTED.Y+DY-22), fntMet.stringWidth(gbuSELECTED.CLASS)+5, 22, false);
			//Draw Name
			dbg.setColor(Color.white);
			dbg.drawString(gbuSELECTED.CLASS, (int)(gbuSELECTED.X+DX), (int)(gbuSELECTED.Y+DY-5));
	
	    }
		
///////////////////////////////////////////////////////PAUSE//////////////////////////////////////////////////////////
		
		//Pause screen
		if(blnPAUSE){
			
			//Background
			dbg.setColor(new Color(0,0,0,190));
			dbg.fill3DRect(pnlCENTER.getWidth()/2-110, pnlCENTER.getHeight()/2-25, 220, 50, true);
			
			//Border and caption
			dbg.setColor(new Color(255,255,255,200));
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN, 50));
			dbg.draw3DRect(pnlCENTER.getWidth()/2-110, pnlCENTER.getHeight()/2-25, 220, 50, true);
			dbg.drawString("PAUSED", pnlCENTER.getWidth()/2-100, pnlCENTER.getHeight()/2+15);
		}

//////////////////////////////////////////////////////////HELP SCREEN/////////////////////////////////////////////////
		
		if(blnHELP){
			
			//Background
			dbg.setColor(new Color(0,0,0,200));
			dbg.fill3DRect(pnlCENTER.getWidth()/2-230, pnlCENTER.getHeight()/2-240, 460, 486, true);
			
			//Border
			dbg.setColor(new Color(255,255,255,200));
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN, 50));
			dbg.draw3DRect(pnlCENTER.getWidth()/2-230, pnlCENTER.getHeight()/2-240, 460, 486, true);
			
			//Title
			dbg.drawString("Help/Hints", pnlCENTER.getWidth()/2-100, pnlCENTER.getHeight()/2-200);
			
			//First Hint
			dbg.setFont(fntOLD5.deriveFont(Font.PLAIN, 30));
			dbg.drawString("1) Use WASD to Move", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150);
			
			//Second Hint
			dbg.drawString("2) Press I to open inventory", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*1);
			
			//Third Hint
			dbg.drawString("3) Press CapsLock to open stats", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*2);

			//Fourth Hint
			dbg.drawString("4) Right click to interact with", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*3);
			dbg.drawString("   characters and objects", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*4);

			//Fifth Hint			
			dbg.drawString("5) Left click to use equipped", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*5);
			dbg.drawString("   weapon or staff(for magic)", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*6);
			
			//Sixth Hint			
			dbg.drawString("6) Press up/down arrow keys to", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*7);
			dbg.drawString("   select spell", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*8);
			
			//Seventh Hint			
			dbg.drawString("7) To hotkey an item select it", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*9);
			dbg.drawString("   in the inventory and press a", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*10);
			dbg.drawString("   num key", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*11);
			
			//Eighth Hint
			dbg.drawString("8) click use button to use or", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*12);
			dbg.drawString("   equip/unequip an item", pnlCENTER.getWidth()/2-225, pnlCENTER.getHeight()/2-150+30*13);


			
			
			
		}
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}
	
	/**
	 * Update Bottom panel graphics
	 * @param g
	 */
	public void update2(Graphics g){
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//What to draw
		
		try{
			//Draw bottom panel image
			if(isJar()){
				imgBOTTOM=getImage("/Graphics/Backgrounds/","BottomPanel.png");
			}else{
				imgBOTTOM=ImageIO.read(Paths.get(DIRECT+"BottomPanel.png").toFile());
			}
			
			dbg.drawImage(imgBOTTOM, 0, 0, pnlBOTTOM.getSize().width, pnlBOTTOM.getSize().height, null);
			
			//2D Graphics component
			Graphics2D G=(Graphics2D)dbg;

///////////////////////////////////////////////////////////HEALTH & MANA//////////////////////////////////////////////////////////////			
			//Player Health "Bar"
				//Unfill health
			dbg.setColor(Color.black);
			double HealthAngle=((1-PLAYER.HEALTH/PLAYER.MAX_HEALTH)*100*1.65);
			Arc2D.Double HealthArc= new Arc2D.Double(((double)55/1366)*pnlBOTTOM.getWidth(),20, ((double)130/1366)*pnlBOTTOM.getWidth(), 107, 90-HealthAngle,HealthAngle*2, 1);
			HealthArc.setArcType(Arc2D.CHORD);
			G.fill(HealthArc);
				//Display Health
			dbg.setColor(Color.white);
			dbg.setFont(fntDIABLO.deriveFont(Font.BOLD,15));
			dbg.drawString(PLAYER.HEALTH+"/"+Math.round(PLAYER.MAX_HEALTH), (int)Math.round(((double)85/1366)*pnlBOTTOM.getWidth()), 73);

			//Player Mana "Bar"
				//Unfill mana
			dbg.setColor(Color.black);
			double ManaAngle=((1-PLAYER.MANA/PLAYER.MAX_MANA)*100*1.65);
			Arc2D.Double ManaArc= new Arc2D.Double(((double)1182/1366)*pnlBOTTOM.getWidth(),20, ((double)130/1366)*pnlBOTTOM.getWidth(), 107, 90-ManaAngle,ManaAngle*2, 1);
			ManaArc.setArcType(Arc2D.CHORD);
			G.fill(ManaArc);
				//Display mana
			dbg.setColor(Color.white);
			dbg.setFont(fntDIABLO.deriveFont(Font.BOLD,15));			
			dbg.drawString(PLAYER.MANA+"/"+Math.round(PLAYER.MAX_MANA),(int)Math.round(((double)1215/1366)*pnlBOTTOM.getWidth()), 73);

/////////////////////////////////////////////////////////XP BAR/////////////////////////////////////////////////////////			
			
			//XP Bar
				//Display
			dbg.setColor(Color.white);
			dbg.setFont(fntGOW.deriveFont(Font.PLAIN,15));
			dbg.drawString("XP: "+PLAYER.STATS.XP+"/"+PLAYER.STATS.MAX_XP, (int)Math.round(((double)625/1366)*pnlBOTTOM.getWidth()), 12);
				//Back Bar
			dbg.setColor(new Color(0,0,0,200));
			dbg.fill3DRect((int)Math.round(((double)190/1366)*pnlBOTTOM.getWidth()), 15, (int)Math.round(((double)975/1366)*pnlBOTTOM.getWidth()), 13, true);
				//XP
			dbg.setColor(Color.white);
			dbg.fill3DRect((int)Math.round(((double)190/1366)*pnlBOTTOM.getWidth()), 18, (int)Math.round(((double)PLAYER.STATS.XP/PLAYER.STATS.MAX_XP)* (int)Math.round(((double)975/1366)*pnlBOTTOM.getWidth())), 8, true);
			
///////////////////////////////////////////////////////HOTKEY BAR//////////////////////////////////////////////////
			
			//HotKey Bar
				//Background
			dbg.setColor(new Color(0,0,0,200));
			dbg.fill3DRect((int)Math.round(((double)670/1366)*pnlBOTTOM.getWidth()), 65, (int)Math.round(((double)500/1366)*pnlBOTTOM.getWidth()), 60, true);
			
			//Hotkey related
			for(int i=0;i<10;i++){
				
				dbg.setColor(Color.white);
				if(PLAYER.INVENTORY.findHotKey(i)!=-1){
					
					int Ind=PLAYER.INVENTORY.findHotKey(i);
					
					//Draw item image
					dbg.drawImage(PLAYER.INVENTORY.ITEM_IMAGE.elementAt(Ind), (int)Math.round(((double)(670+i*50+5)/1366)*pnlBOTTOM.getWidth()), 75, (int)Math.round(((double)40/1366)*pnlBOTTOM.getWidth()), 50, null);
					
					//If item is equipped then draw yellow line
					if(PLAYER.INVENTORY.ITEM_NAME.elementAt(Ind).indexOf("<Eq>")!=-1){
						dbg.setColor(Color.yellow);
					}
					
					//Display item quantity
					if(PLAYER.INVENTORY.ITEM_TAG.elementAt(Ind).equalsIgnoreCase("potion") || PLAYER.INVENTORY.ITEM_TAG.elementAt(Ind).equalsIgnoreCase("food")){
						dbg.drawString(PLAYER.INVENTORY.ITEM_QUANTITY.elementAt(Ind)+"", (int)Math.round(((double)(670+i*50+20)/1366)*pnlBOTTOM.getWidth()), 75+30);
					}
				}
				
				//Draw Border and Hotkey number
				dbg.draw3DRect((int)Math.round(((double)(670+i*50)/1366)*pnlBOTTOM.getWidth()), 65,(int)Math.round(((double)48/1366)*pnlBOTTOM.getWidth()), 60, true);
				dbg.drawString(i+"", (int)Math.round(((double)(670+i*50+2)/1366)*pnlBOTTOM.getWidth()), 80);
				
			}

//////////////////////////////////////////////////////////////GOLD////////////////////////////////////////////////			
			
			//Display amount of gold
			dbg.setColor(Color.white);
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN,30));
			dbg.drawString(PLAYER.GOLD+"", (int)Math.round(((double)705/1366)*pnlBOTTOM.getWidth()), 60);
			
			//Draw Gold Icon
			dbg.setColor(new Color(0,0,0,200));
			
			Image imgGold_Icon=null;
			if(isJar()){
				imgGold_Icon=getImage("/Graphics/Backgrounds/","Gold_2.png");
			}else{
				imgGold_Icon=ImageIO.read(Paths.get(DIRECT+"Gold_2.png").toFile());
			}
			
			dbg.fill3DRect((int)Math.round(((double)670/1366)*pnlBOTTOM.getWidth()), 35, 30, 30, true);
			dbg.drawImage(imgGold_Icon, (int)Math.round(((double)670/1366)*pnlBOTTOM.getWidth()), 35, 30, 30, null);
			
			/*
			dbg.setColor(Color.black);
			dbg.fill3DRect((int)Math.round(((double)200/1366)*pnlBOTTOM.getWidth()), 93, 465, 55, true);
			dbg.setColor(Color.white);
			dbg.drawString("asd", (int)Math.round(((double)201/1366)*pnlBOTTOM.getWidth()), 110);
			*/
			
////////////////////////////////////////////////////////////MAGIC ICON///////////////////////////////////////////////////			
			
			//Draw Spell Icon
			dbg.setColor(Color.black);
			Image imgMageIcon=null;
			if(isJar()){
				imgMageIcon=getImage("/Graphics/Icon/",PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX)[0].NAME+".png");
			}else{
				imgMageIcon=ImageIO.read(Paths.get(ICON_DIR+PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX)[0].NAME+".png").toFile());
			}

			
			dbg.fill3DRect((int)Math.round(((double)(200)/1366)*pnlBOTTOM.getWidth()), 30, 410, 62, true);
			dbg.drawImage(imgMageIcon, (int)Math.round(((double)200/1366)*pnlBOTTOM.getWidth()), 23, 75, 75, null);
			
			//Display Spell Name
			dbg.setColor(Color.white);
			dbg.setFont(fntGOW.deriveFont(Font.PLAIN,55));
			dbg.drawString(PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX)[0].NAME, (int)Math.round(((double)280/1366)*pnlBOTTOM.getWidth()), 73);
			
			
			
			
		}catch(Exception e){}
		
				
		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}
	/**
	 * Update player inventory graphics
	 * @param g
	 */
	public void update3(Graphics g){
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//What to draw
		
		//Background
		dbg.setColor(Color.black);
		dbg.fill3DRect(0, 0, pnlPLAYER_PIC.getWidth(), pnlPLAYER_PIC.getHeight(), false);		
		
		//If index out of bounds then exit
		if(INDEX<0 || INDEX>=PLAYER.INVENTORY.ITEM_NAME.size() ||PLAYER.INVENTORY.ITEM_NAME.size()<=0 ) return;
		
		//Draw Item Graphics
		int G=2;
		dbg.drawImage(PLAYER.INVENTORY.ITEM_IMAGE.elementAt(INDEX), pnlPLAYER_PIC.getWidth()/2-PLAYER.INVENTORY.ITEM_IMAGE.elementAt(INDEX).getWidth(null)*G/2,  pnlPLAYER_PIC.getHeight()/2-PLAYER.INVENTORY.ITEM_IMAGE.elementAt(INDEX).getHeight(null)*G/2,PLAYER.INVENTORY.ITEM_IMAGE.elementAt(INDEX).getWidth(null)*G,PLAYER.INVENTORY.ITEM_IMAGE.elementAt(INDEX).getHeight(null)*G, null);
		
		//Display ItemName in dovakin
		int Ind=PLAYER.INVENTORY.ITEM_NAME.elementAt(INDEX).indexOf("<Eq>");
		Ind=(Ind==-1)?PLAYER.INVENTORY.ITEM_NAME.elementAt(INDEX).length():Ind;
		
		lblPLAYER_ITEMNAME.setText(PLAYER.INVENTORY.ITEM_NAME.elementAt(INDEX).substring(0, Ind));
		
		//Update player gold		
		lblPLAYER_GOLD.setText("Gold: ~"+PLAYER.GOLD);
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}

	/**
	 * Update target inventory graphics
	 * @param g
	 */
	public void update4(Graphics g){
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//What to draw
		
		//Background
		dbg.setColor(Color.black);
		dbg.fill3DRect(0, 0, pnlTARGET_PIC.getWidth(), pnlTARGET_PIC.getHeight(), false);		
		
		//If index out of bounds then exit
		if(TARGETINDEX<0 || TARGETINDEX>=TARGET.ITEM_NAME.size()||TARGET==null) return;
		
		//Draw Item Graphics
		int G=2;
		dbg.drawImage(TARGET.ITEM_IMAGE.elementAt(TARGETINDEX), pnlTARGET_PIC.getWidth()/2-TARGET.ITEM_IMAGE.elementAt(TARGETINDEX).getWidth(null)*G/2,  pnlTARGET_PIC.getHeight()/2-TARGET.ITEM_IMAGE.elementAt(TARGETINDEX).getHeight(null)*G/2,TARGET.ITEM_IMAGE.elementAt(TARGETINDEX).getWidth(null)*G,TARGET.ITEM_IMAGE.elementAt(TARGETINDEX).getHeight(null)*G, null);
		
		//Display item name in dovakin
		lblTARGET_ITEMNAME.setText(TARGET.ITEM_NAME.elementAt(TARGETINDEX));
		
		
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}

	/**
	 * Update stats screen
	 * @param g
	 */
	public void update5(Graphics g){
		if(this.getExtendedState()!=JFrame.MAXIMIZED_BOTH||!jbSTATSTOGGLE.hasFocus()){
			if(StatsWindow.isVisible()){jbSTATSTOGGLE.doClick();}
			return;
		}
		
		
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//What to draw
		
		//Draw the player on the left panel
		dbg.setColor(Color.black);
		dbg.fill3DRect(0, 0, StatsWindow.pnlLeft.getWidth(), StatsWindow.pnlLeft.getHeight(), true);	
		dbg.drawImage(PLAYER.imgCHARAC, 55, 80,140, 205, (int)(PLAYER.W*0), (int)(PLAYER.H*0), (int)(PLAYER.W*0+PLAYER.W), (int)(PLAYER.H*0+PLAYER.H), null);	
		
		//Draw the equipped weapon on the player's hand
		if(PLAYER.MEELE_WEAPON!=null){
			
			//If the weapon is a meele weapon
			if(PLAYER.MEELE_WEAPON.NAME.indexOf("Staff")==-1){
				dbg.drawImage(PLAYER.MEELE_WEAPON.imgWEAPON, 130-PLAYER.MEELE_WEAPON.imgWEAPON.getWidth(null)/2, 175-PLAYER.MEELE_WEAPON.imgWEAPON.getHeight(null), null);
			}
			//If the weapon is a staff
			else{
				
				//Draw the staff
				dbg.drawImage(PLAYER.MEELE_WEAPON.imgWEAPON, 130, 175-PLAYER.MEELE_WEAPON.imgWEAPON.getHeight(null), null);	
			
				//Draw the spell
				try{
					
					Image imgMageIcon= null;
					if(isJar()){
						imgMageIcon=getImage("/Graphics/Icon/",PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX)[0].NAME+".png");
					}else{
						imgMageIcon=ImageIO.read(Paths.get(ICON_DIR+PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX)[0].NAME+".png").toFile());
					}
					dbg.drawImage(imgMageIcon, 70-imgMageIcon.getWidth(null)/2, 175-imgMageIcon.getHeight(null)/2, null);
				}catch(Exception e){}
			}
		}
		//Draw shield
		if(PLAYER.SHIELD!=null){
			dbg.drawImage(PLAYER.SHIELD, 70-PLAYER.SHIELD.getWidth(null)/2, 175-PLAYER.SHIELD.getHeight(null)/2, null);
		}
		
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
	
	}
	
	/**
	 * Update the graphics of the update window
	 * @param g
	 */
	public void update6(Graphics g){
		if(this.getExtendedState()!=JFrame.MAXIMIZED_BOTH||!jbUPGRADES.hasFocus()){
			if(Upgrades_Panel.isVisible()){jbUPGRADES.doClick();}
			return;
		}
		
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//Draw Background
		try{
			Image img=null;
			if(isJar()){
				img=this.getImage("\\Graphics\\Backgrounds\\","Menu.jpg");
			}else{
				img=ImageIO.read(Paths.get(DIRECT+"\\Menu.jpg").toFile());
			}
			dbg.drawImage(img, 0, 0,Upgrades_Panel.getWidth(),Upgrades_Panel.getHeight(), null);
		}catch(Exception e){System.out.println("asd");}

		//Display Title
		dbg.setFont(fntOLD3.deriveFont(Font.PLAIN,70));
		dbg.setColor(new Color(255,200,0));
		dbg.drawString("Upgrades", 100, 100);
		
		//Total XP Points available
		dbg.setFont(fntGOW.deriveFont(Font.BOLD,30));
		dbg.setColor(Color.red);
		dbg.drawString("XP Points:    "+PLAYER.STATS.XP_POINTS, 50, 430);
		
		//Draw categories for upgrade
		int dist=65;
		
			//Category title
		dbg.setFont(fntGOW.deriveFont(Font.PLAIN,30));
		dbg.setColor(Color.white);
		dbg.drawString("Strength", 40, 150);
		dbg.drawString("Intelligence", 40, 150+dist);
		dbg.drawString("Dexterity", 40, 150+dist*2);
		dbg.drawString("Luck", 40, 150+dist*3);
		
		
		
			//Actual point in category
		dbg.drawString(PLAYER.STATS.STRENGTH+"", 245+55, 150);
		dbg.drawString(PLAYER.STATS.INTELLIGENCE+"", 245+55, 150+dist);
		dbg.drawString(PLAYER.STATS.DEXTERITY+"", 245+55, 150+dist*2);
		dbg.drawString(PLAYER.STATS.LUCK+"", 245+55, 150+dist*3);
		
		
			//Category description
				//STRENGTH
		dbg.setFont(fntOLD1.deriveFont(Font.PLAIN,15));
		dbg.setColor(Color.white);
		dbg.drawString("Affects Max Health", 40, 170);
		dbg.drawString("and physical damage", 40, 170+20);
		
				//INTELLIGENCE
		dbg.drawString("Affects Max Mana", 40, 170+dist);
		dbg.drawString("and magic damage", 40, 170+20+dist);

				//DEXTERITY
		dbg.drawString("Affects Weapon skill", 40, 170+dist*2);
		dbg.drawString("level and attack speed", 40, 170+20+dist*2);

				//LUCK
		dbg.drawString("Affects Item and", 40, 170+dist*3);
		dbg.drawString("gold drops", 40, 170+20+dist*3);
		
		

		//Plus minus for each category
		dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN,60));
		
		int Vals[]={PLAYER.STATS.STRENGTH,PLAYER.STATS.INTELLIGENCE,PLAYER.STATS.DEXTERITY,PLAYER.STATS.LUCK};
		
		for(int i=0;i<4;i++){
			//Minus sign
			if(Vals[i]>0){
				//Red background
				dbg.setColor(Color.red);
				dbg.draw3DRect(204+55, 130+dist*i, 24, 24, true);
				dbg.fill3DRect(204+55, 130+dist*i, 24, 24, true);
				
				//Minus
				dbg.setColor(Color.white);
				dbg.drawString("-", 200+55, 160+dist*i);
			}
			
			//Plus sign
				//Red background
			dbg.setColor(Color.red);
			dbg.draw3DRect(288+55, 130+dist*i, 24, 24, true);
			dbg.fill3DRect(288+55, 130+dist*i, 24, 24, true);
		
				//Plus
			dbg.setColor(Color.white);			
			dbg.drawString("+", 200+136, 160+dist*i);
		}
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
	
	}
		
		

	/**
	 * Move the map around to support the players view
	 */
	public void MapShift(){
		//Shift Left
		
		Point2D.Double Center= new Point2D.Double(pnlCENTER.getWidth()/2-PLAYER.W/2*PLAYER.GROWTHFACTOR,pnlCENTER.getHeight()/2-PLAYER.H/2*PLAYER.GROWTHFACTOR);
		
		if(blnFOG){
			MAP.RevealFog(PLAYER);
		/*
			if(TORCH!=null){
				for(int i=0;i<TORCH.length;i++){
					MAP.RevealFog(TORCH[i],130);
				}
			}
			*/
		}
		//if(Math.abs(PLAYER.X)>pnlCENTER.getWidth())DX=-(int)(PLAYER.X-pnlCENTER.getWidth()/2);
		//if(Math.abs(PLAYER.Y)>pnlCENTER.getHeight())DY=-(int)(PLAYER.Y-pnlCENTER.getHeight()/2);
		double XSpeed=Math.abs(PLAYER.X+PLAYER.W/2*PLAYER.GROWTHFACTOR+DX-Center.x);
		double YSpeed=Math.abs(PLAYER.Y+PLAYER.H/2*PLAYER.GROWTHFACTOR+DY-Center.y);
		
		if(PLAYER.X+PLAYER.W/2*PLAYER.GROWTHFACTOR+DX>Center.x){
			DX-=XSpeed/((XSpeed<300)?4:1);//(PLAYER.SPEED+3);
		}
		//Shift Right
		if(PLAYER.X+PLAYER.W/2*PLAYER.GROWTHFACTOR+DX<Center.x){
			DX+=XSpeed/((XSpeed<300)?4:1);//(PLAYER.SPEED+3);
		}
		//Shift Up
		if(PLAYER.Y+PLAYER.H/2*PLAYER.GROWTHFACTOR+DY>Center.y){
			DY-=YSpeed/((YSpeed<300)?4:1);//(PLAYER.SPEED+3);
		}
		//Shift Down
		if(PLAYER.Y+PLAYER.H/2*PLAYER.GROWTHFACTOR+DY<Center.y){
			DY+=YSpeed/((YSpeed<300)?4:1);//(PLAYER.SPEED+3);
		}
	}
	
	/**
	 * PLAYER Movement and Collision
	 */
	public void keyPressed(KeyEvent event) {
		//ABC.setVisible(false);
		
		int Speed=1;

/////////////////////////////////////////////////////////PLAYER MOVEMENT/////////////////////////////////////////////////////////		
		//Move Right
		if(event.getKeyCode()==event.VK_D){
			//Wall Collision
			if(PLAYER.Collision(PLAYER.X+PLAYER.W*PLAYER.GROWTHFACTOR+Speed/Math.abs(Speed), PLAYER.Y+PLAYER.H/2*PLAYER.GROWTHFACTOR))return;
			
			//Move
			PLAYER.Move(Speed, 0);
		}
		
		//Move Left
		else if(event.getKeyCode()==event.VK_A){
			//Wall Collision
			if(PLAYER.Collision(PLAYER.X-Speed/Math.abs(Speed), PLAYER.Y+PLAYER.H/2*PLAYER.GROWTHFACTOR))return;

			//Move
			PLAYER.Move(-Speed, 0);
		}
		
		//Move Up
		else if(event.getKeyCode()==event.VK_W){
			//Wall Collision
			if(PLAYER.Collision(PLAYER.X+PLAYER.W/2*PLAYER.GROWTHFACTOR, PLAYER.Y-Speed/Math.abs(Speed)))return;
			
			//Move
			PLAYER.Move(0, -Speed);
		}
		
		//Move Down
		else if(event.getKeyCode()==event.VK_S){
			//Wall Collision
			if(PLAYER.Collision(PLAYER.X+PLAYER.W/2*PLAYER.GROWTHFACTOR, PLAYER.Y+PLAYER.H*PLAYER.GROWTHFACTOR+Speed/Math.abs(Speed)))return;

			//Move
			PLAYER.Move(0, Speed);
		}
/////////////////////////////////////////////////////////CHEAT/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_Z){
			PLAYER.GOLD+=10000;
			PLAYER.STATS.IncreaseXP(50);
			System.out.println(DX+", "+DY);
		}
		
		
/////////////////////////////////////////////////////////Magic Selector/////////////////////////////////////////////////////////		
		
		else if(event.getKeyCode()==event.VK_DOWN){
			if(MAGIC_INDEX+1<PLAYER.SPELL_LIST.size()){
				MAGIC_INDEX++;
			}
		}

		else if(event.getKeyCode()==event.VK_UP){
			if(MAGIC_INDEX-1>-1){
				MAGIC_INDEX--;
			}
		}
/////////////////////////////////////////////////////////HOTKEY/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_0 || event.getKeyCode()==event.VK_NUMPAD0){
			useHotKey(0);
		}
		
		else if(event.getKeyCode()==event.VK_1 || event.getKeyCode()==event.VK_NUMPAD1){
			useHotKey(1);
		}
		
		else if(event.getKeyCode()==event.VK_2 || event.getKeyCode()==event.VK_NUMPAD2){
			useHotKey(2);
		}
		
		else if(event.getKeyCode()==event.VK_3 || event.getKeyCode()==event.VK_NUMPAD3){
			useHotKey(3);
		}
		
		else if(event.getKeyCode()==event.VK_4 || event.getKeyCode()==event.VK_NUMPAD4){
			useHotKey(4);
		}
		
		else if(event.getKeyCode()==event.VK_5 || event.getKeyCode()==event.VK_NUMPAD5){
			useHotKey(5);
		}
		
		else if(event.getKeyCode()==event.VK_6 || event.getKeyCode()==event.VK_NUMPAD6){
			useHotKey(6);
		}
		
		else if(event.getKeyCode()==event.VK_7 || event.getKeyCode()==event.VK_NUMPAD7){
			useHotKey(7);
		}
		
		else if(event.getKeyCode()==event.VK_8 || event.getKeyCode()==event.VK_NUMPAD8){
			useHotKey(8);
		}
		
		else if(event.getKeyCode()==event.VK_9 || event.getKeyCode()==event.VK_NUMPAD9){
			useHotKey(9);
		}

/////////////////////////////////////////////////////////PLAYER MOVEMENT/////////////////////////////////////////////////////////				
		else if(event.getKeyCode()==event.VK_K){
			PLAYER.MANA=(int)PLAYER.MAX_MANA;
			//SKELETON[0].ISDEAD=true;
		}
/////////////////////////////////////////////////////////QUEST LOG/////////////////////////////////////////////////////////				
		else if(event.getKeyCode()==event.VK_Q){
			jbQUESTTOGGLE.doClick();
			
			
		}
/////////////////////////////////////////////////////////PLAYER INVENTORY/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_I){			
			jbINVENTOGGLE.doClick();
		}

/////////////////////////////////////////////////////////PAUSE/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_P){
			
			blnHELP=false;
			blnPAUSE=!blnPAUSE;
			
			setPaused(blnPAUSE);
			
			
		}
		
/////////////////////////////////////////////////////////HELP/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_H){

			blnPAUSE=false;
			blnHELP=!blnHELP;

			setPaused(blnHELP);


		}
		
/////////////////////////////////////////////////////////ENTER/////////////////////////////////////////////////////////		
				
		else if(event.getKeyCode()==event.VK_ENTER){
			
			if(jbPLAYER_SELL.hasFocus()){jbPLAYER_SELL.doClick();}
			if(jbTARGET_BUY.hasFocus()){jbTARGET_BUY.doClick();}
			
			
		}
/////////////////////////////////////////////////////////PLAYER STATS/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_CAPS_LOCK){
			jbSTATSTOGGLE.requestFocus();
			jbSTATSTOGGLE.doClick();
		}

/////////////////////////////////////////////////////////QUIT/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_ESCAPE){
			if(jbSTATSTOGGLE.isSelected()){jbSTATSTOGGLE.doClick();}
			if(jbUPGRADES.isSelected()){jbUPGRADES.doClick();}
			
			if(blnPLAYER_INVENTOGGLE){jbINVENTOGGLE.doClick();}
			blnTARGET_INVENTOGGLE=false;
			pnlRIGHT.setVisible(false);
			
			System.exit(0);
		}

/////////////////////////////////////////////////////////QUIT/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_L){
			PLAYER.INVENTORY.addItem("Key", 0, 1, "Key.png", "Key", new String[]{});
		}
/////////////////////////////////////////////////////////QUIT/////////////////////////////////////////////////////////		
		else if(event.getKeyCode()==event.VK_M){
			for(int i=0;i<DARKELF.length;i++){
				DARKELF[i].PAttack(DARKELF[i].MEELE_WEAPON);
			}
			JOptionPane.showMessageDialog(null, "asdsadasd");
		}

		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if(!PLAYER.CANMOVE)return;

/////////////////////////////////////////////////////SCROLL CLICK/////////////////////////////////////////////////////		
		if(event.getButton()==2){
			JOptionPane.showMessageDialog(null, "("+(event.getX()-DX)+", "+(event.getY()-DY)+")");

			PLAYER.X=(event.getX()-DX);
			PLAYER.Y=(event.getY()-DY);
		}
/////////////////////////////////////////////////////RIGHT CLICK/////////////////////////////////////////////////////		
		
		if(event.getButton()==3){
			//Initialize
			TARGET=null;
			
			switch(MNAME){
				case "Town":				
				//NPC
				for(int i=0;i<NPC.length;i++){
					//Collision
					if(NPC[i].Contains(event.getX()-DX, event.getY()-DY)){
						//Check distance
						if(PLAYER.Dist(NPC[i])<250){
							//Assign
							TARGET=NPC[i].INVENTORY;
							
						}
						if(PLAYER.Dist(NPC[i])<100){
								if(NPC[i].NAI.State.equalsIgnoreCase("quest")){
									if(NPC[i].QUEST_LIST.elementAt(0).STATUS==0){
										
										if(NPC[i].QUEST_LIST.elementAt(0).QTYPE==Quest_Type.KILL){
											NPC[i].QUEST_LIST.elementAt(0).setTargets(SKELETON[0]);
										}else if(NPC[i].QUEST_LIST.elementAt(0).QTYPE==Quest_Type.DELIVERY){
											NPC[i].QUEST_LIST.elementAt(0).setTargets(NPC[3],new String[]{"Secret Scroll"});
										}
										NPC[i].giveQuest(PLAYER);
										QUEST_WINDOW.setPulse(true);
										PLAYER.QUEST_LIST.elementAt(PLAYER.QUEST_LIST.size()-1).GAME=this;
									}
								}
								for(int j=0;j<PLAYER.QUEST_LIST.size();j++){
									if(NPC[i].NAI.State.equalsIgnoreCase("quest")){
										if(PLAYER.QUEST_LIST.elementAt(j).STATUS==2 && (PLAYER.QUEST_LIST.elementAt(j).QUEST_GIVER==NPC[i])){
									
											PLAYER.CompleteQuest(j);
											NPC[i].NAI.State="Calm";
																	
										}
									}
									PLAYER.QUEST_LIST.elementAt(j).Deliver(PLAYER, NPC[i]);
							}
						}			
					}		
				}
				
				for(int i=0;i<SKELETON.length;i++){
					//Collision
					if(SKELETON[i].Contains(event.getX()-DX, event.getY()-DY)){
						//Check distance
						if(PLAYER.Dist(SKELETON[i])<150){
							//Assign
							TARGET=SKELETON[i].INVENTORY;
						}
					}		
				}
				
				if(gobSELECTED!=null){
					if(gobSELECTED.CLASS.equalsIgnoreCase("chest")){
						
						TARGET=gobSELECTED.INVEN;
					}
				}
				
				
				break;
				case "dungeon":
					for(int i=0;i<SHAMAN.length;i++){
						//Collision
						if(SHAMAN[i].Contains(event.getX()-DX, event.getY()-DY)){
							//Check distance
							if(PLAYER.Dist(SHAMAN[i])<150){
								//Assign
								TARGET=SHAMAN[i].INVENTORY;
							}
						}		
					}
					for(int i=0;i<DARKELF.length;i++){
						//Collision
						if(DARKELF[i].Contains(event.getX()-DX, event.getY()-DY)){
							//Check distance
							if(PLAYER.Dist(DARKELF[i])<150){
								//Assign
								TARGET=DARKELF[i].INVENTORY;
								
							}
						}		
					}
			}
			
			//If no target is selected
			if(TARGET==null ){
				//Remove inventory window
				blnTARGET_INVENTOGGLE=false;
				pnlRIGHT.setVisible(false);
			}
			else if(TARGET.Type.equalsIgnoreCase("Trader")||TARGET.OPEN_INVEN){
				//If inventory is empty, don't open inventory window
				if(TARGET.ITEM_NAME.size()<=0){
					if(TARGET.Type.equalsIgnoreCase("chest"))gobSELECTED.FY=3;
					JOptionPane.showMessageDialog(null, "Empty");
					return;
				}
				//Open inventory
				blnTARGET_INVENTOGGLE=true;
				pnlRIGHT.setVisible(true);
				TARGET.displayItems(lstTARGET_INVEN);
				TARGETINDEX=0;
			}
			else{
				TARGETINDEX=0;
			}
			if(gobSELECTED!=null){
				if(PLAYER.Dist(gobSELECTED)<gobSELECTED.H*gobSELECTED.GROWTHFACTOR){
					gobSELECTED.Interact(PLAYER,this);
				}
			}
			
			if(gbuSELECTED!=null){
				if(PLAYER.Dist(gbuSELECTED)<gbuSELECTED.H*gbuSELECTED.GROWTHFACTOR){
					gbuSELECTED.Interact(PLAYER,this);
				}
			}
			
		}
			
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(!PLAYER.CANMOVE)return;

/////////////////////////////////////////////////////LEFT CLICK/////////////////////////////////////////////////////		
		if(event.getButton()==1){
			if(PLAYER.MEELE_WEAPON==null)return;
			System.out.println("("+(event.getX()-DX)+", "+(event.getY()-DY)+")");
			if(PLAYER.MEELE_WEAPON.NAME.indexOf("Staff")!=-1){
				PLAYER.MAttack(PLAYER.SPELL_LIST.elementAt(MAGIC_INDEX));
			}
			else{
				PLAYER.PAttack(PLAYER.MEELE_WEAPON);
			}
	
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.requestFocus();
		
	}
	@Override
	
	
	public void valueChanged(ListSelectionEvent event) {
		if(event.getSource()==lstPLAYER_INVEN){
			//Update Index
			INDEX=lstPLAYER_INVEN.getSelectedIndex();
			
			//Display Stats
			if(INDEX>=0 &&  INDEX<PLAYER.INVENTORY.ITEM_NAME.size()){
				switch(PLAYER.INVENTORY.ITEM_TAG.elementAt(INDEX).toLowerCase()){
					
					//POTION
					case "potion":
						//Show points
						lblPLAYER_STATS.setText("Effect Value: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).POINTS);
						break;
					
					//MEELE WEAPON	
					case "meele":
						//Show damage and attack speed
						lblPLAYER_STATS.setText("Damage: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).DAMAGE+"\nAttack Speed: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).ATTACK_SPEED);
						break;
						
					//HEAD ARMOR	
					case "h_armor":
						//Show Armor and magic resist
						lblPLAYER_STATS.setText("Armor: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).ARMOR+"\nMagic Resist: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).MAGIC_RESIST);
						break;
					
					//CHEST ARMOR	
					case "c_armor":
						//Show Armor and magic resist
						lblPLAYER_STATS.setText("Armor: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).ARMOR+"\nMagic Resist: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).MAGIC_RESIST);
						break;	
						
					//SHIELD	
					case "shield":
						//Show Armor and magic resist						
						lblPLAYER_STATS.setText("Armor: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).ARMOR+"\nMagic Resist: "+PLAYER.INVENTORY.STATS.elementAt(INDEX).MAGIC_RESIST);
						break;	
	
				}
			}
		}
		
		if(event.getSource()==lstTARGET_INVEN){
			//Update target index
			TARGETINDEX=lstTARGET_INVEN.getSelectedIndex();
			
			//Display Stats
			if(TARGET==null)return;
			if(TARGETINDEX>=0 &&  TARGETINDEX<TARGET.ITEM_NAME.size()){
				switch(TARGET.ITEM_TAG.elementAt(TARGETINDEX).toLowerCase()){
					//POTION
					case "potion":
						//Display Points
						lblTARGET_STATS.setText("Effect Value: "+TARGET.STATS.elementAt(TARGETINDEX).POINTS);
						break;
						
					//MEELE WEAPON	
					case "meele":
						//Display damage and attack speed
						lblTARGET_STATS.setText("Damage: "+TARGET.STATS.elementAt(TARGETINDEX).DAMAGE+"\nAttack Speed: "+TARGET.STATS.elementAt(TARGETINDEX).ATTACK_SPEED);
						break;
					
					//HEAD ARMOR	
					case "h_armor":
						//Display Armor and magic resist
						lblTARGET_STATS.setText("Armor: "+TARGET.STATS.elementAt(TARGETINDEX).ARMOR+"\nMagic Resist: "+TARGET.STATS.elementAt(TARGETINDEX).MAGIC_RESIST);
						break;
						
					//CHEST ARMOR	
					case "c_armor":
						//Display Armor and magic resist
						lblTARGET_STATS.setText("Armor: "+TARGET.STATS.elementAt(TARGETINDEX).ARMOR+"\nMagic Resist: "+TARGET.STATS.elementAt(TARGETINDEX).MAGIC_RESIST);
						break;	
						
					//SHIELD	
					case "shield":
						//Display Armor and magic resist
						lblTARGET_STATS.setText("Armor: "+TARGET.STATS.elementAt(TARGETINDEX).ARMOR+"\nMagic Resist: "+TARGET.STATS.elementAt(TARGETINDEX).MAGIC_RESIST);
						break;	

				}
			}
		}
		
	}
	@Override
	public void mouseDragged(MouseEvent event) {
		
	}
	@Override
	public void mouseMoved(MouseEvent event) {
		//initialize
		gchSELECTED=null;
		gobSELECTED=null;
		gbuSELECTED=null;
		
		switch(MNAME){
			case "Town":	

				
///////////////////////////////////////////////////////////////////CHARACTERS////////////////////////////////////////////////				
				//NPC
				for(int i=0;i<NPC.length;i++){
					//Collision
					if(NPC[i].Contains(event.getX()-DX,event.getY()-DY)){
						//Assign
						gchSELECTED=NPC[i];
					}
				}
				
				for(int i=0;i<SKELETON.length;i++){
					//Collision
					if(SKELETON[i].Contains(event.getX()-DX,event.getY()-DY)){
						//Assign
						gchSELECTED=SKELETON[i];
					}
				}
				
//////////////////////////////////////////////////////OBJECTS//////////////////////////////////////////////////				
				//Fountain
				if(FOUNTAIN.Contains(event.getX()-DX,event.getY()-DY))gobSELECTED=FOUNTAIN;
				
				//Forge
				else if(FORGE.Contains(event.getX()-DX,event.getY()-DY))gobSELECTED=FORGE;	
				
				//Cauldron
				else if(CAULDRON.Contains(event.getX()-DX,event.getY()-DY))gobSELECTED=CAULDRON;
				
				//Bed
				for(int i=0;i<BED.length;i++){
					if(BED[i].Contains(event.getX()-DX,event.getY()-DY))gobSELECTED=BED[i];
				}
				
				//Chest
				for(int i=0;i<CHEST.length;i++){
					if(CHEST[i].Contains(event.getX()-DX,event.getY()-DY))gobSELECTED=CHEST[i];
				}
				
////////////////////////////////////////////////////BUILDINGS//////////////////////////////////////////////////////////
				//Workshop
				if(WORKSHOP.Contains(event.getX()-DX,event.getY()-DY))gbuSELECTED=WORKSHOP;
				
				//Alchemist hut
				else if(ALCHEMIST.Contains(event.getX()-DX,event.getY()-DY))gbuSELECTED=ALCHEMIST;
				
				//Dungeon
				else if(DUNGEON.Contains(event.getX()-DX,event.getY()-DY))gbuSELECTED=DUNGEON;
				
				
				//Portals
				for(int i=0;i<PORTALS.length;i++){
					if(PORTALS[i].Contains(event.getX()-DX,event.getY()-DY))gbuSELECTED=PORTALS[i];
				}
				
				break;
				
			case "dungeon":
				//Dungeon exit
				if(DUNGEON_EXIT.Contains(event.getX()-DX,event.getY()-DY))gbuSELECTED=DUNGEON_EXIT;
				
				for(int i=0;i<SHAMAN.length;i++){
					//Collision
					if(SHAMAN[i].Contains(event.getX()-DX,event.getY()-DY)){
						//Assign
						gchSELECTED=SHAMAN[i];
					}
				}
				for(int i=0;i<DARKELF.length;i++){
					//Collision
					if(DARKELF[i].Contains(event.getX()-DX,event.getY()-DY)){
						//Assign
						gchSELECTED=DARKELF[i];
					}
				}
				break;
			case "Tutorial":
				if(PRISONER.Contains(event.getX()-DX,event.getY()-DY)){
					//Assign
					gchSELECTED=PRISONER;
				}
				
				for(int i=0;i<DOOR.length;i++){
					//Collision
					if(DOOR[i].Contains(event.getX()-DX,event.getY()-DY)){
						//Assign
						gobSELECTED=DOOR[i];
					}
				}
				
				for(int i=0;i<TIPSTONES.length;i++){
					//Collision
					if(TIPSTONES[i].Contains(event.getX()-DX,event.getY()-DY)){
						//Assign
						gobSELECTED=TIPSTONES[i];
					}
				}
				
				//Bed
				if(BED[0].Contains(event.getX()-DX,event.getY()-DY))gobSELECTED=BED[0];
				
				break;
		}
	}
	
	/**
	 * Use a hotkeyed item or hotkey an item
	 * @param HK Hotkey number
	 */
	public void useHotKey(int HK){
		
		//Set hotkey 
		if(INDEX>=0 &&  INDEX<PLAYER.INVENTORY.ITEM_NAME.size() && lstPLAYER_INVEN.hasFocus()){
			PLAYER.INVENTORY.setHotKey(INDEX, HK);	
		}
		
		//Use hotkeyed item
		else if(PLAYER.INVENTORY.findHotKey(HK)!=-1){
			PLAYER.INVENTORY.ItemEffect(PLAYER.INVENTORY.findHotKey(HK), PLAYER);
		}
	}
	
	/**
	 * Pause the game
	 * @param Pause boolean to pause/unpause
	 */
	public void setPaused(boolean Pause){
		//PAUSE
		if(Pause){
			//Stop all characters
			for(int i=0;i<ACTIVE_CHARS.size();i++){
				ACTIVE_CHARS.elementAt(i).CANMOVE=false;
			}
			
			//Stop all active objects
			for(int i=0;i<ACTIVE_OBJECTS.size();i++){
				ACTIVE_OBJECTS.elementAt(i).tmrANIM.stop();
			}
			
			//make all additional windows unavailable
			jbINVENTOGGLE.setEnabled(false);
			jbSTATSTOGGLE.setEnabled(false);
			jbQUESTTOGGLE.setEnabled(false);
			jbUPGRADES.setEnabled(false);			
		}
		//UNPAUSE
		else{
			//Un freeze all characters
			for(int i=0;i<ACTIVE_CHARS.size();i++){
				ACTIVE_CHARS.elementAt(i).CANMOVE=true;
			}
			
			//Un freeze active objects
			for(int i=0;i<ACTIVE_OBJECTS.size();i++){
				ACTIVE_OBJECTS.elementAt(i).tmrANIM.start();
			}

			//Make additional windows available
			jbINVENTOGGLE.setEnabled(true);
			jbSTATSTOGGLE.setEnabled(true);
			jbQUESTTOGGLE.setEnabled(true);
			jbUPGRADES.setEnabled(true);
		}

	}
	
	/**
	 * Setup the game according to the map
	 * @param Mname Name of the map
	 * @param blnfog Enable/disable fog
	 */
	public void Setup(String Mname,boolean blnfog){
			
			//Assign
			MNAME=Mname;
			blnFOG=blnfog;
			
			
			//Disable everything
			
			Nullify();
			SHAMAN=null;
			DARKELF=null;
			PRISONER=null;
			BANDIT_BOSS=null;
			TORCH = null;
			
			audBGM.Stop();
			//Remove player collision
			PLAYER.removeAllColisionObjects();
			PLAYER.removeMagicCollision();
			
			
			//Open Map
			MAP=MAP.OpenMap(System.getProperty("user.dir")+"\\Graphics\\Maps\\", MNAME, 40, MAP);
			MINI_MAP=MINI_MAP.OpenMap(System.getProperty("user.dir")+"\\Graphics\\Maps\\", MNAME, 3, MINI_MAP);
				

			//Map
			PLAYER.addColisionObject(MAP);

				
				switch(MNAME){
					case "Town":
						
						//Initial Player Position
						if(PLAYER.SPAWN_X==-1){
							PLAYER.X=100;
							PLAYER.Y=100;
						}else{
							PLAYER.X=PLAYER.SPAWN_X;
							PLAYER.Y=PLAYER.SPAWN_Y;							
						}
						TARGET=null;

					//Game Objects	
						//Fountain
						FOUNTAIN= new GameObject(802,1202,96,95,"Fountain.png",4,"Fountain");
						ACTIVE_OBJECTS.add(FOUNTAIN);
						
						//Forge
						FORGE= new GameObject(770,50,51,57,"Forge_1.png",2,"Forge");
						ACTIVE_OBJECTS.add(FORGE);
						
						//Cauldron
						CAULDRON= new GameObject(1322,40,124,140,"Cauldron.png",4,"Cauldron");
						ACTIVE_OBJECTS.add(CAULDRON);
						
						//Bed
						BED= new GameObject[4];
						
						BED[0]=new GameObject(77,846,32,60,"Bed.png",4,"Bed");
						BED[1]=new GameObject(317,845,32,60,"Bed.png",4,"Bed");
						BED[2]=new GameObject(77,1666,32,60,"Bed1.png",4,"Bed");
						BED[3]=new GameObject(317,1665,32,60,"Bed1.png",4,"Bed");
				    
						for(int i=0;i<BED.length;i++){
							ACTIVE_OBJECTS.add(BED[i]);
						}
				    
						//Chest
						if(CHEST==null){
							CHEST= new GameObject[2];
						
							CHEST[0]=new GameObject(2400,1900,30,45,"Chest.png",4,"Chest");
							CHEST[0].GenerateItems(PLAYER.STATS.LEVEL, 2);
				    
							CHEST[1]=new GameObject(2838,1518,30,45,"Chest.png",4,"Chest");
							CHEST[1].GenerateItems(PLAYER.STATS.LEVEL, 5);
				    
							for(int i=0;i<CHEST.length;i++){
								ACTIVE_OBJECTS.add(CHEST[i]);
							}
						}
				    
					//Game Building	
						//Workshop
						WORKSHOP= new Building(487,40,100,100,"Workshop.png","Workshop");
						//Alchemist hut
						ALCHEMIST= new Building(976,40,100,100,"Alchemist Hut.png","Alchemist Hut");
						//Portals
						PORTALS= new Building[2];
				    
						PORTALS[0]=new Building(1727,1770,100,100,"Portal.png","Portal");
						PORTALS[0].setTarget(2400, 40);
				    
						PORTALS[1]=new Building(2440,40,100,100,"Portal.png","Portal");
						PORTALS[1].setTarget(1650, 1770);
				    
						//Dungeon Entrance
						DUNGEON=new Building(2763,1761,137,130,"Dungeon.png","Dungeon Level 1");
				    
						DUNGEON.FX=1;
				    
					//Game Characters	
						if(NPC==null){
							NPC= new GameCharacter[15];
					    	
							//Blacksmith
							NPC[0]=new GameCharacter(863,40,32,48,"NPC0.png",3,1,"Blacksmith");
							NPC[0].addColisionObject(MAP);
							NPC[0].addColisionObject(WORKSHOP);
							NPC[0].addColisionObject(FORGE);
							ACTIVE_CHARS.add(NPC[0]);
							
							//Alchemist
							NPC[1]=new GameCharacter(1154,40,32,32,"NPC1.png",2,2,"Alchemist");
							NPC[1].addColisionObject(MAP);
							NPC[1].addColisionObject(ALCHEMIST);
							NPC[1].addColisionObject(CAULDRON);
							ACTIVE_CHARS.add(NPC[1]);
							
							//Inn Keeper
							NPC[2]=new GameCharacter(46,1212,32,32,"NPC3.png",2,1,"Inn Keeper");
							NPC[2].addColisionObject(MAP);
							ACTIVE_CHARS.add(NPC[2]);
							
							//Mage
							NPC[3]=new GameCharacter(1206,1877,35,45,"NPC4.png",2,3,"Mage");
							NPC[3].addColisionObject(MAP);
							ACTIVE_CHARS.add(NPC[3]);
	
							//Citizens
							for(int i=4;i<NPC.length;i++){
								NPC[i]=new GameCharacter(0,0,32,48,"NPC2_"+Math.round(Math.random()*1+1)+".png",3,1,"Citizen");
								NPC[i].addColisionObject(MAP);
								NPC[i].addColisionObject(FOUNTAIN);
								NPC[i].addColisionObject(PORTALS);
								NPC[i].addColisionObject(BED);
								NPC[i].addColisionObject(PLAYER);
								NPC[i].Spawn(0,0,1947, 1995);
								ACTIVE_CHARS.add(NPC[i]);
							}
						
///////////////////////////////////////////////////////Quests//////////////////////////////////////////////////////////////////////////////////////////////////							
							int Rand= (int)Math.floor(Math.random()*(NPC.length-5))+4;
						
							NPC[Rand].QUEST_LIST.add(new Quest("Kill the Skeleton Chieftan","Kill the skeleton chieftan across the river",50,50,Quest_Type.KILL,NPC[Rand]));
							NPC[Rand].NAI.State="quest";
							NPC[Rand].X=1385;NPC[Rand].Y=968;
							
							NPC[Rand+1].QUEST_LIST.add(new Quest("Deliver the Scroll","Deliver this Scroll to the mage",10,20,Quest_Type.DELIVERY,NPC[Rand+1]));
							NPC[Rand+1].NAI.State="quest";
							
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////						
						}
						
						if(SKELETON==null){
							SKELETON= new GameCharacter[10];
							
							//Skeleton Boss
							SKELETON[0]=new GameCharacter(0,0,32,32,"ENEMY0.png",2,1,"Skeleton Chieftan");
								//Collision objects
							SKELETON[0].addColisionObject(MAP);
							SKELETON[0].addColisionObject(PLAYER);
							SKELETON[0].addColisionObject(PORTALS);
							SKELETON[0].addColisionObject(DUNGEON);
								//Target the player
							SKELETON[0].EAI.FOE=PLAYER;
							SKELETON[0].MEELE_WEAPON.addCollChar(PLAYER);
								//Spawn
							SKELETON[0].Spawn(2201,40,2962, 1962);
							ACTIVE_CHARS.add(SKELETON[0]);
						
							//Default skeletons
							for(int i=1;i<SKELETON.length;i++){
								SKELETON[i]=new GameCharacter(0,0,32,32,"ENEMY0.png",2,1,"Skeleton");
									//Collision objects
								SKELETON[i].addColisionObject(MAP);
								SKELETON[i].addColisionObject(PLAYER);
								SKELETON[i].addColisionObject(PORTALS);
								SKELETON[i].addColisionObject(DUNGEON);
									//Target the player								
								SKELETON[i].EAI.FOE=PLAYER;
								SKELETON[i].MEELE_WEAPON.addCollChar(PLAYER);
									//Spawn
								SKELETON[i].Spawn(2201,40,2962, 1962);
								ACTIVE_CHARS.add(SKELETON[i]);
							}
						}else{
												
							//Default skeletons
							for(int i=1;i<SKELETON.length;i++){
								SKELETON[i]=new GameCharacter(0,0,32,32,"ENEMY0.png",2,1,"Skeleton");
									//Collision objects
								SKELETON[i].addColisionObject(MAP);
								SKELETON[i].addColisionObject(PLAYER);
								SKELETON[i].addColisionObject(PORTALS);
								SKELETON[i].addColisionObject(DUNGEON);
									//Target the player								
								SKELETON[i].EAI.FOE=PLAYER;
								SKELETON[i].MEELE_WEAPON.addCollChar(PLAYER);
									//Spawn
								SKELETON[i].Spawn(2201,40,2962, 1962);
								ACTIVE_CHARS.add(SKELETON[i]);
							}
						}
					
						//Add Player collision
						ACTIVE_CHARS.add(PLAYER);
							//Fountain
						PLAYER.addColisionObject(FOUNTAIN);
							//Beds
						PLAYER.addColisionObject(BED);
							//Portals
						PLAYER.addColisionObject(PORTALS);
							//Dungeons
						PLAYER.addColisionObject(DUNGEON);
					
						//Magic attack
						PLAYER.addMagicCharacCollision(SKELETON);
						PLAYER.addMagicMapCollision(MAP);
						
						//Sword
						System.out.println(SKELETON.length);
						PLAYER.MEELE_WEAPON.addCollChar(SKELETON);
						
						break;
					
					case "dungeon":
						//Initialize
						PLAYER.X=PLAYER.SPAWN_X;
						PLAYER.Y=PLAYER.SPAWN_Y;

					
						//Dungeon Entrance
						DUNGEON_EXIT=new Building(20,30,98,91,"Dungeon_exit.png","Dungeon_Exit(Town)");
						
						SHAMAN= new GameCharacter[5];
					
						for(int i=0;i<SHAMAN.length;i++){
							SHAMAN[i]=new GameCharacter(0,0,24,32,"Char 5.png",2,3,"Shaman");
								//Collision objects
							SHAMAN[i].addColisionObject(MAP);
							SHAMAN[i].addColisionObject(PLAYER);
								//Target the player								
							//SHAMAN[i].EAI.FOE=PLAYER;
							SHAMAN[i].MEELE_WEAPON.addCollChar(PLAYER);
								//Spawn
							SHAMAN[i].Spawn(0,0,MAP.BX*MAP.Size, MAP.BY*MAP.Size);
							ACTIVE_CHARS.add(SHAMAN[i]);
						}
						DARKELF=new GameCharacter[5];
						for(int i=0;i<DARKELF.length;i++){
							DARKELF[i]=new GameCharacter(0,0,24,32,"Char 6.png",2,3,"Dark Elf");
								//Collision objects
							DARKELF[i].addColisionObject(MAP);
							DARKELF[i].addColisionObject(PLAYER);
								//Target the player								
							DARKELF[i].EAI.FOE=PLAYER;
							DARKELF[i].MEELE_WEAPON.addCollChar(PLAYER);
								//Spawn
							DARKELF[i].Spawn(0,0,MAP.BX*MAP.Size, MAP.BY*MAP.Size);
							ACTIVE_CHARS.add(DARKELF[i]);
						}
						
						//Magic attack
						PLAYER.addMagicMapCollision(MAP);
						PLAYER.addMagicCharacCollision(DARKELF);
						PLAYER.addMagicCharacCollision(SHAMAN);
						
						PLAYER.MEELE_WEAPON.addCollChar(DARKELF);
						PLAYER.MEELE_WEAPON.addCollChar(SHAMAN);
						
						break;
						
						case "Tutorial":
						
						//Initial Player Position
						if(PLAYER.SPAWN_X==-1){
							PLAYER.X=250;
							PLAYER.Y=250;
						}else{
							PLAYER.X=PLAYER.SPAWN_X;
							PLAYER.Y=PLAYER.SPAWN_Y;							
						}
						TARGET=null;
						blnFOG=true;
						
						TORCH=new GameObject[9];
						TORCH[0]=new GameObject(165,225,18,55,"Torch.png",7,"Torch");
						TORCH[1]=new GameObject(440,225,18,55,"Torch.png",7,"Torch");
						TORCH[2]=new GameObject(485,225,18,55,"Torch.png",7,"Torch");
						TORCH[3]=new GameObject(485+280,225,18,55,"Torch.png",7,"Torch");
						TORCH[4]=new GameObject(250,480,18,55,"Torch.png",7,"Torch");
						TORCH[5]=new GameObject(470,480,18,55,"Torch.png",7,"Torch");
						TORCH[6]=new GameObject(690,480,18,55,"Torch.png",7,"Torch");
						TORCH[7]=new GameObject(1008,711,18,55,"Torch.png",7,"Torch");
						TORCH[8]=new GameObject(1008,1032,18,55,"Torch.png",7,"Torch");
						
						
						if(blnFOG){
							if(TORCH!=null){
								for(int i=0;i<TORCH.length;i++){
									int Rad=(i<7)?140:100;
									MAP.RevealFog(TORCH[i],Rad);
								}
							}
							
						}
						
						DOOR=new GameObject[4];
						DOOR[0]=new GameObject(280,360,33,50,"Bars.png",7,"Prison Door");
						DOOR[1]=new GameObject(310,360,33,50,"Bars.png",7,"Prison Door");
						DOOR[2]=new GameObject(600,360,33,50,"Bars.png",7,"Prison Door");
						DOOR[3]=new GameObject(630,360,33,50,"Bars.png",7,"Prison Door");
						PLAYER.addColisionObject(DOOR);
						
						PRISONER=new GameCharacter(700,250,32,48,"Prisoner.png",3,1,"Prisoner");
						PRISONER.addColisionObject(MAP);
						PRISONER.addColisionObject(DOOR);
						
						
						
						BANDIT= new GameCharacter[4];
						BANDIT[0]=new GameCharacter(1002,500,32,32,"Bandit.png",3,1,"Bandit");
						BANDIT[1]=new GameCharacter(1002,1029,32,32,"Bandit.png",3,1,"Bandit");
						BANDIT[2]=new GameCharacter(247,1038,32,32,"Bandit.png",3,1,"Bandit");
						BANDIT[3]=new GameCharacter(1175,1572,32,32,"Bandit.png",3,1,"Bandit");

						BED= new GameObject[2];
						BED[0]=new GameObject(390,240,32,60,"Bed.png",4,"Bed");
						BED[1]=new GameObject(520,240,32,60,"Bed.png",4,"Bed");
						PLAYER.addColisionObject(BED);
						PRISONER.addColisionObject(BED);
						
						TIPSTONES= new GameObject[1];
						TIPSTONES[0]=new GameObject(200,250,29,27,"Tablet.png",4,"Inscription");
						TIPSTONES[0].setTipMessage("Tip", "- WASD to move\n- Right click to interact with\n objects ");
						break;
				}

				
	}
	public void Nullify(){
		
		FOUNTAIN=null;
		FORGE=null;
		CAULDRON=null;
		BED=null;
		CHEST=null;
		TORCH=null;	
		DOOR=null;
		TIPSTONES=null;
		//Building
		WORKSHOP=null;
		ALCHEMIST=null;
		PORTALS=null;
		DUNGEON=null;
		DUNGEON_EXIT=null;
		
	}
	public BufferedImage getImage(String Direct,String FName){
		
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

/////////////////////////////////////////////////////////////////////////LOADING SCREEN/////////////////////////////////////////////////
/**
 * 
 * @category Supplementary
 * Self explanatory
 */
@SuppressWarnings("serial")
class Loading_Screen extends JWindow{
	
	//Fonts
	Font fntDIABLO,fntGOW;
	
	//Wallpaper directory
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Backgrounds\\";
	//Fonts directory
	String Dir=System.getProperty("user.dir")+"\\Graphics\\Fonts\\";
	
	/**
	 * Constructor for the Loading screen
	 */
	public Loading_Screen(){
		//Initialize 
		super();
		setLayout(new GridLayout());
		setBounds(this.getToolkit().getScreenSize().width/2-140,this.getToolkit().getScreenSize().height/2-25,280,50);
		
		//Get Fonts
		try {
			fntDIABLO=Font.createFont(Font.PLAIN, Paths.get(Dir+"Diablo.ttf").toFile());
			fntGOW=Font.createFont(Font.PLAIN, Paths.get(Dir+"GOW.ttf").toFile());
			
		} catch (Exception e) {System.out.print("Loading Screen Error");}
		
		//Set font 
		setForeground(Color.white);
		setFont(fntGOW.deriveFont(Font.PLAIN,45));

		//Make it visible
		setVisible(true);
		
		
	}
	
	
	
}

/////////////////////////////////////////////////////////////////////////STATS WINDOW/////////////////////////////////////////////////
/**
* 
* @category Supplementary
* Stats Window
*/
@SuppressWarnings("serial")
class Stats_Window extends JWindow{
	//Components
	JTextArea lblStats=new JTextArea("       STATS"),lblName=new JTextArea("Name"),lblSTATS=new JTextArea("Name");
	JPanel pnlMain= new JPanel(new GridLayout(1,2)),pnlLeft= new JPanel();
	//Borders
	Border whiteline = BorderFactory.createLineBorder(Color.white,5);
	Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	Border raisedbevel = BorderFactory.createRaisedBevelBorder();
	Border loweredbevel = BorderFactory.createLoweredBevelBorder();
	//Fonts
	Font fntDRAGON,fntDIABLO,fntGOW;
	//Background
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Backgrounds\\";
	//Image
	Image img;
	
	/**
	 * Constructor for the stats window
	 * @param C Player 
	 * @param Center Center panel
	 */
	public Stats_Window(GameCharacter C,JPanel Center){
		//Initialize
		super();
		setLayout(new BorderLayout());
		setBounds(100,100,520,420);
		//Fonts directory
		String Dir=System.getProperty("user.dir")+"\\Graphics\\Fonts\\";
		
		//getFonts
		try {
			fntDRAGON=Font.createFont(Font.PLAIN, Paths.get(Dir+"Dovahkiin.otf").toFile());
			fntDIABLO=Font.createFont(Font.PLAIN, Paths.get(Dir+"Diablo.ttf").toFile());
			fntGOW=Font.createFont(Font.PLAIN, Paths.get(Dir+"GOW.ttf").toFile());
		} catch (Exception e) {System.out.print("asd");}
		
		
		//Stats Heading
		add(lblStats,BorderLayout.NORTH);
			//Style
		lblStats.setOpaque(true);
		lblStats.setForeground(Color.white);
		lblStats.setBackground(Color.black);
		lblStats.setFont(fntDIABLO.deriveFont(Font.CENTER_BASELINE,45));
		lblStats.setAlignmentX(lblStats.CENTER_ALIGNMENT);
		lblStats.setEditable(false);
			//Border
		lblStats.setBorder(BorderFactory.createCompoundBorder(whiteline,raisedbevel));
		
		//Main panel
		add(pnlMain,BorderLayout.CENTER);
		
		//Left panel
		pnlMain.add(pnlLeft);
			//Style
		pnlLeft.setOpaque(true);
		pnlLeft.setBackground(Color.black);
		
		//Right panel
		pnlMain.add(lblSTATS);
			//Style
		lblSTATS.setOpaque(true);
		lblSTATS.setForeground(Color.white);
		lblSTATS.setBackground(Color.black);
		lblSTATS.setFont(fntGOW.deriveFont(Font.CENTER_BASELINE,25));
		lblSTATS.setAlignmentX(lblStats.CENTER_ALIGNMENT);
		lblSTATS.setEditable(false);
			//Text
		lblSTATS.setText(C.STATS.toString());
			//Border
		lblSTATS.setBorder(BorderFactory.createEtchedBorder(Color.white, Color.gray));
		
		//Bottom Panel
		add(lblName,BorderLayout.SOUTH);
			//Style
		lblName.setOpaque(true);
		lblName.setForeground(Color.white);
		lblName.setBackground(Color.black);
		lblName.setFont(fntDRAGON.deriveFont(Font.CENTER_BASELINE,45));
		lblName.setAlignmentX(lblName.CENTER_ALIGNMENT);
		lblName.setEditable(false);
			//Border
		lblName.setBorder(BorderFactory.createCompoundBorder(whiteline,raisedbevel));
	
		
		//Make visible
		setVisible(true);
	}
	
	
	
}

/////////////////////////////////////////////////////////////////////////STATS WINDOW/////////////////////////////////////////////////
/**
* 
* @category Supplementary
* Stats Window
*/
@SuppressWarnings("serial")
class Upgrades_Window extends JWindow implements MouseListener{
	//Title
	JLabel lblTitle= new JLabel("asd");
	GameCharacter PLAYER;
	
	/**
	 * Constructor for upgrades window
	 * @param Pl Player
	 * @param Center Center panel
	 */
	public Upgrades_Window(GameCharacter Pl,JPanel Center){
		//Initialize
		super();
		setLayout(new BorderLayout());
		setBounds(this.getToolkit().getScreenSize().width-400,80,400,500);
		addMouseListener(this);
		setVisible(true);
		
		//Assign
		PLAYER=Pl;
		
	}
	
	/**
	 * Detect Click
	 */
	public void mouseClicked(MouseEvent event) {
		//Distance between lines
		int dist=65;
		//Array of values
		int Vals[]={PLAYER.STATS.STRENGTH,PLAYER.STATS.INTELLIGENCE,PLAYER.STATS.DEXTERITY,PLAYER.STATS.LUCK};

		for(int i=0;i<4;i++){
			//Collision with minus button
			if(event.getX()>259 && event.getX()<259+24){
				if(event.getY()>130+dist*i && event.getY()<130+dist*i+24){
					
					//Decrement
					if(Vals[i]==0){return;}
					if(i==0){
						//STRENGTH
						PLAYER.STATS.STRENGTH--;
					}
					else if(i==1){
						//INTELLIGENCE
						PLAYER.STATS.INTELLIGENCE--;
					}
					else if(i==2){
						//DEXTERITY
						PLAYER.STATS.DEXTERITY--;
					}
					else if(i==3){
						//LUCK
						PLAYER.STATS.LUCK--;
					}
					
					//Increase available points
					PLAYER.STATS.XP_POINTS++;
				}
			}
			
			//Collision with plus button
			if(event.getX()>288+55 && event.getX()<288+55+24){
				if(event.getY()>130+dist*i && event.getY()<130+dist*i+24){
					
					
					if(i==0){
						//STRENGTH
						PLAYER.STATS.STRENGTH++;
					}
					else if(i==1){
						//INTELLIGENCE
						PLAYER.STATS.INTELLIGENCE++;
					}
					else if(i==2){
						//DEXTERITY
						PLAYER.STATS.DEXTERITY++;
					}
					else if(i==3){
						//LUCK
						PLAYER.STATS.LUCK++;
					}
					//Decrease available points
					PLAYER.STATS.XP_POINTS--;
				}
			}
			
		}

		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
class Quest_Window extends JWindow implements ActionListener{
	Font fntTitle,fntSubTitle,fntSSubTitle;
	GameCharacter Player;
	boolean blnToggle=false;
	Timer tmrAnim= new Timer(1,this),tmrPulse=new Timer(1,this);
	JToggleButton jbToggle;
	int Counter=0, DPulse=10;

	
	public Quest_Window(Font[] fnts,int X,int Y,int W,int H,GameCharacter C,JToggleButton btn){
		fntTitle=fnts[2];
		fntSubTitle=fnts[1];
		fntSSubTitle=new Font("Arial",25,25);
		setBounds(X-W/2,Y-H/2,W,H);
		Player=C;
		jbToggle=btn;
		
	}
	
	public void Toggle(){
		blnToggle=!blnToggle;
		setVisible(blnToggle);
		tmrAnim.start();
		
	}
	public void update(Graphics g){
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		//What to draw
		
		dbg.setColor(Color.black);
		dbg.fill3DRect(0, 0, getWidth(), getHeight(), false);
		
		dbg.setColor(new Color(255,255,255,200));
		for(int i=0;i<9;i++){
			if((i<=3) ||(i>=6)){
				dbg.draw3DRect(i, i, getWidth()-2*i, getHeight()-2*i, true);
			}
		}
		
		dbg.setColor(Color.white);
		dbg.setFont(fntTitle.deriveFont(Font.PLAIN, 50));
		
		
		dbg.drawString("QUEST LOG", getWidth()/2-150, 50);
		
		
		if(Player.QUEST_LIST.size()==0){
			dbg.setFont(fntSubTitle.deriveFont(Font.PLAIN,36));
			dbg.setColor(Color.white);
			dbg.drawString("No Quests", 50, 150);			
		}else{
			int H=0;
			for(int i=0;i<Player.QUEST_LIST.size();i++){
				dbg.setFont(fntSubTitle.deriveFont(Font.PLAIN,36));
				dbg.setColor(Color.yellow);
				dbg.drawString(Player.QUEST_LIST.elementAt(i).NAME, 10, 150+H);
				
				dbg.setFont(fntSSubTitle);
				dbg.setColor(Color.white);
				dbg.drawString(Player.QUEST_LIST.elementAt(i).DESCRIPTION, 10, 200+H);

				dbg.setFont(fntSubTitle.deriveFont(Font.PLAIN,36));
				dbg.setColor(Color.white);
				dbg.drawString("GOLD: "+Player.QUEST_LIST.elementAt(i).GOLD, 10, 250+H);
				dbg.drawString("XP: "+Player.QUEST_LIST.elementAt(i).XP, 300, 250+H);
				
				H+=150;
				
			}
		}
		
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}
	
	public void actionPerformed(ActionEvent event){
		if(event.getSource()==tmrAnim){
			update(getGraphics());
		}
		if(event.getSource()==tmrPulse){
			jbToggle.setForeground(new Color(255,255,255,Counter));
			if(Counter+DPulse<0 || Counter+DPulse>255)DPulse=-DPulse;
			Counter+=DPulse;
			
		}
	}
	
	public void setPulse(boolean blnSet){
		if(blnSet){
			tmrPulse.start();
		}else{
			tmrPulse.stop();
			jbToggle.setForeground(new Color(255,255,255,255));
			Counter=0;
		}
	}
	
}