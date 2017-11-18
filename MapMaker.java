/**
 * @category Application
 * 
 * GUI used to create and edit map files
 * 
 */
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;

public class MapMaker extends JFrame implements ActionListener,MouseListener,MouseMotionListener{
	//Map
	GridMap Map= new GridMap(25,20,50);
	
	//Double buffering
	Image dbImage;
	Graphics dbg;
	
	//Update Timer
	Timer tmrAnimate= new Timer(1,this);
	//
	boolean Moveable=false;
	//Basic Framework
	JPanel pnlCenter= new JPanel(new FlowLayout()),pnlTop= new JPanel(),pnlBottom= new JPanel(),pnlLeft= new JPanel(),pnlRight= new JPanel();
	JLabel lblTitle= new JLabel("MAP MAKER"),lblStats= new JLabel(""); 
	
	//Borders
	Border blacklineBorder = BorderFactory.createLineBorder(Color.black);
	Border titledBorder = BorderFactory.createTitledBorder(blacklineBorder, "Colors");
	
	//Color Variables
	String[] ColNames;//=new String[122];
	JButton [] Colors;
	
	double pX=0,pY=0;
	//Directory for saving and opening gmap files
	String Direct=System.getProperty("user.dir")+'\\';
	
	int DX=20,DY=20;
	//Menus
	JMenuBar Main= new JMenuBar(); 
		JMenu mnuFile= new JMenu("File");
			JMenuItem mnuFile_New= new JMenuItem("New");
			JMenuItem mnuFile_Save= new JMenuItem("Save");
			JMenuItem mnuFile_Open= new JMenuItem("Open");
			JMenuItem mnuFile_ChangeDirect= new JMenuItem("Change Directory");
			JMenuItem mnuFile_ChangeSize= new JMenuItem("Change Map View Size");
			JMenuItem mnuFile_Exit= new JMenuItem("Exit");
		JMenu mnuOpts=new JMenu("Options");
			JMenu mnuOpts_Mode= new JMenu("Box fill Type");
				JRadioButtonMenuItem mnuOpts_Mode_Normal=new JRadioButtonMenuItem("Normal fill",true);
				JRadioButtonMenuItem mnuOpts_Mode_Wall=new JRadioButtonMenuItem("Walls",false);
				
			
	//Selected color fill		
	int Fill=0;
	
	
	
	int sX=0,sY=0,dX=0,dY=0;
	public MapMaker(){
		//Initialize JFrame
		super("");
		setBounds(0,0,500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLayout(new BorderLayout());
		
		
		//Top
		add(pnlTop,BorderLayout.NORTH);
		pnlTop.add(lblTitle);
		lblTitle.setFont(new Font("Arial",50,50));
		
		//Center
		
		add(pnlCenter,BorderLayout.CENTER);
		pnlCenter.addMouseListener(this);
		pnlCenter.addMouseMotionListener(this);
		
		//Bottom
		add(pnlBottom,BorderLayout.SOUTH);
		pnlBottom.add(lblStats);
		
		//Left
		JScrollPane Holder= new JScrollPane(pnlLeft,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(Holder,BorderLayout.WEST);
		pnlLeft.setBorder(titledBorder);
		pnlLeft.setLayout(new GridLayout(Map.imgCounter,1));
		
		//Right
		add(pnlRight,BorderLayout.EAST);
		
		//Start timer
		tmrAnimate.start();
		
		//Add menus
		//Set menu bar
		this.setJMenuBar(Main);
			//Add File Menu
			Main.add(mnuFile);
				//File menu button
				mnuFile.add(mnuFile_New);
				mnuFile_New.addActionListener(this);
				
				//Save menu button
				mnuFile.add(mnuFile_Save);
				mnuFile_Save.addActionListener(this);
				
				//Open menu button
				mnuFile.add(mnuFile_Open);
				mnuFile_Open.addActionListener(this);
				
				mnuFile.addSeparator();
				
				//Directory change menu button
				mnuFile.add(mnuFile_ChangeDirect);
				mnuFile_ChangeDirect.addActionListener(this);

				//Directory change menu button
				mnuFile.add(mnuFile_ChangeSize);
				mnuFile_ChangeSize.addActionListener(this);

				
				//Exit menu button
				mnuFile.add(mnuFile_Exit);
				mnuFile_Exit.addActionListener(this);
		
				Main.add(mnuOpts);
				mnuOpts.add(mnuOpts_Mode);
				mnuOpts_Mode.add(mnuOpts_Mode_Normal);
				mnuOpts_Mode_Normal.addActionListener(this);
				mnuOpts_Mode.add(mnuOpts_Mode_Wall);
				mnuOpts_Mode_Wall.addActionListener(this);
				
		ColNames= new String[Map.imgCounter];		
		for(int i=0;i<ColNames.length;i++){
			ColNames[i]="Texture "+i;
		}
		
		//Add color buttons		
		Colors= new JButton[ColNames.length];
		
		for(int i=0;i<Colors.length;i++){
			//Create button with text
			Colors[i]= new JButton(ColNames[i],new ImageIcon( Map.imgTile[i]));
			//Add to panel
			pnlLeft.add(Colors[i]);
			//Add actionlisteners
			Colors[i].addActionListener(this);
		}
		
	
		
	}
	public static void main(String[] args){
		MapMaker ABC= new MapMaker();
	}
	
	/**
	 * Refresh graphics using double buffering
	 * @param g Graphics of component
	 * @param Draw to Draw or to just refresh
	 */
	public void update(Graphics g,boolean Draw){
		//Double buffering
		dbImage=createImage(getWidth(),getHeight());
		dbg=dbImage.getGraphics();
		
		//What to draw
		if(Draw)
		{
			Map.Draw(dbg,DX,DY);
		}
		
		int StartX=(sX<=dX)?sX:dX,StartY=(sY<=dY)?sY:dY;
		dbg.setColor(new Color(0,0,0,250));
		dbg.draw3DRect(StartX, StartY, Math.abs(sX-dX), Math.abs(sY-dY), true);

		dbg.setColor(new Color(0,0,0,50));
		dbg.fill3DRect(StartX, StartY, Math.abs(sX-dX), Math.abs(sY-dY), true);

		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}
	
	/**
	 * Used to run event procedures
	 * for clicks and timers
	 */
	public void actionPerformed(ActionEvent event){
		//Update graphics using the timer
		update(pnlCenter.getGraphics(),true);
		
		if(Moveable){
			if(pX>this.getSize().width-250){
				DX-=3;
			}
			if(pX<250){
				if(DX+3>0)return;
				DX+=3;
			}
			if(pY>this.getSize().height-250){
				//if(DY-3+Map.BY*Map.Size<300)return;
				DY-=3;
			}
			if(pY<250){
				if(DY+3>0)return;
				DY+=3;
			}
		}
		//if timer then exit
		if(event.getActionCommand()==null)return;
		
		
		if(event.getSource()==mnuFile_New){
			//Create a new map
			try{
				//Number of X Blocks
				int BX=Integer.parseInt(JOptionPane.showInputDialog("Number of X Blocks"));
				//Number of Y Blocks
				int BY=Integer.parseInt(JOptionPane.showInputDialog("Number of Y Blocks"));
				//Create map
				Map= new GridMap(BX,BY,50);
				
			}catch(Exception e){JOptionPane.showMessageDialog(null, "Invalid input try agian");return;}
			
		}
		
		else if(event.getSource()==mnuFile_Save){
			//Save the map
			
			//Get file name
			String FileN=JOptionPane.showInputDialog("Enter the File name");
			
			//Check if input is valid
			if((FileN==null) || (FileN.equals(""))){
				JOptionPane.showMessageDialog(null, "Invalid file name");
				return;
			}
			
			//Save
			Map.SaveMap(Direct, FileN);
		}
		
		else if(event.getSource()==mnuFile_Open){
			//Open existing map
			
			//Get file name
			String FileN=JOptionPane.showInputDialog("Enter the File name");
			
			//Check if input is valid
			if((FileN==null) || (FileN.equals(""))){
				JOptionPane.showMessageDialog(null, "Invalid file name");
				return;
			}
			
			//Open the map
			Map=Map.OpenMap(Direct,FileN,50,Map);
		}
		else if(event.getSource()==mnuFile_ChangeDirect){
			//Change the directory in which files are saved or opened
			try{
				//Get new directory
				String D= JOptionPane.showInputDialog("Enter new Directory",Direct);
			
				//Check if directory exists
				if((!Files.exists(Paths.get(D)))||D.equals("")){
					JOptionPane.showMessageDialog(null, "This directory does not exist");
					return;
				}
			
				//Assign directory
				Direct=D;
			}
			catch(Exception e){JOptionPane.showMessageDialog(null, "This directory does not exist");}
			
		}
		
		else if(event.getSource()==mnuFile_ChangeSize){
		
			try{
				
				int S=Integer.parseInt(JOptionPane.showInputDialog("Enter the File name",Map.Size));
				int[] F=Map.Fill;
				Map= new GridMap(Map.BX,Map.BY,S);
				Map.Fill=F;
				
				
			}catch(Exception e){JOptionPane.showMessageDialog(null, "Invalid input");}
		}
		
		else if(event.getSource()==mnuFile_Exit){
			//Quit program
			System.exit(0);
		}
		
		else if(event.getSource()==mnuOpts_Mode_Normal){
			mnuOpts_Mode_Wall.setSelected(false);
		}
		else if(event.getSource()==mnuOpts_Mode_Wall){
			mnuOpts_Mode_Normal.setSelected(false);
		}
		

		//Assign fill color
		for(int i=0;i<ColNames.length;i++){
			if(event.getActionCommand().equals(ColNames[i])){
				Fill=i;
			}
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent event) {
		
		if(event.getButton()!=1)return;
		//Get block number
		int Block= Map.getBlockNum(event.getX()-DX,event.getY()-DY);
		if(Block==-1)return;
		
		//Assign color
		Map.Fill[Block]=Fill;
		
		
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
		// TODO Auto-generated method stub
		sX=event.getX();
		sY=event.getY();
		dX=sX;
		dY=sY;
		
	}
	@Override
	public void mouseReleased(MouseEvent event) {
		
		if(event.getButton()==3){
			Moveable=!Moveable;
		}
		// TODO Auto-generated method stub
		
		int StartX=(sX<=dX)?sX:dX,StartY=(sY<=dY)?sY:dY;

		
		if(mnuOpts_Mode_Normal.isSelected()){
			for(int i=StartX;i<StartX+Math.abs(sX-dX);i+=Map.Size/2){
				for(int j=StartY;j<StartY+Math.abs(sY-dY);j+=Map.Size/2){
					int BNum=Map.getBlockNum(i-DX, j-DY);
					if(BNum!=-1){
						Map.Fill[BNum]=Fill;
					}
				}
			}
			
		}
		else if(mnuOpts_Mode_Wall.isSelected()){
			int PBNum=-1,BNum=-1;
			int W=Math.abs(sX-dX),H=Math.abs(sY-dY);
			final int VFill=3,HFill=7,URFill=1,DRFill=8,ULFill=11,DLFill=13,SFill=4;
			
			if(W>=Map.Size && H>=Map.Size){//Enclose
				
			}else{//Just a wall
				if(W>H){
					for(int i=StartX;i<StartX+W;i+=Map.Size/2){
						BNum=Map.getBlockNum(i, StartY+0.5);
						if(BNum!=-1){
							int UBNum=BNum-Map.BX,DBNum=BNum+Map.BX,LBNum=BNum-1,RBNum=BNum+1;
							int UBFill=0,DBFill=0,LBFill=0,RBFill=0;
							if(UBNum>=0){
								UBFill=Map.Fill[UBNum];
							}
							if(DBNum>=0){
								DBFill=Map.Fill[DBNum];
							}
							if(RBNum>=0){
								RBFill=Map.Fill[RBNum];
							}
							if(LBNum>=0){
								LBFill=Map.Fill[LBNum];
							}
							
							
							if(LBFill!=HFill && RBFill==HFill && Map.Fill[BNum]==VFill){
								if(UBFill==VFill || DBFill!=VFill ){
									Map.Fill[BNum]=DRFill;
								}
								else{
									Map.Fill[BNum]=URFill;
								}
								continue;
								
							}
							if(RBFill!=HFill && LBFill==HFill && Map.Fill[BNum]==VFill){
								if(UBFill==VFill || DBFill!=VFill ){
									Map.Fill[BNum]=DLFill;
								}
								else{
									Map.Fill[BNum]=ULFill;
								}
								continue;
							}
							
								//Horizontal Wall
								Map.Fill[BNum]=HFill;
							
						}
					}
				}else if(W<H){
					for(int j=StartY;j<StartY+H;j++){
						BNum=Map.getBlockNum(StartX+0.5, j);
						if(BNum!=-1){
							Map.Fill[BNum]=VFill;
						}
					}
					
				}else{
					
				}
			}
			
			
		}
		
		sX=0;
		sY=0;
		dX=0;
		dY=0;
		
	}
	@Override
	public void mouseDragged(MouseEvent event) {
		dX=event.getX();
		dY=event.getY();
	}
	@Override
	public void mouseMoved(MouseEvent event) {
		//Update stats
		int Block= Map.getBlockNum(event.getX()-DX,event.getY()-DY);
		String BFill="--";
		
		if(Block!=-1){BFill=Map.Fill[Block]+"";}
		
		lblStats.setText("Block Number: "+Block+"     Fill: "+BFill);
		
		pX=event.getX();
		pY=event.getY();
		
		
		
	}
}
