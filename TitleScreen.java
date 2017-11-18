import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
/**
 * 
 * @category Title screen
 * First screen that appears when the game starts
 * 
 *
 */


public class TitleScreen extends JFrame implements ActionListener,MouseMotionListener,MouseListener {

	//Used for torch animation
	Timer tmrANIM= new Timer(1,this);
	
	//Folder directories for the graphics
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Backgrounds\\",DIRECT2=System.getProperty("user.dir")+"\\Graphics\\Special\\";
	
	//Graphics
	Image TITLE,BACK,TORCH;
	
	//Torch variables
	int W=166,H=240;
	int X1=0,Y1=130;
	double G=0.5;
	int FX=3;
	
	int COUNTER=0;
	
	//Buttons
	Vector <Point> Buttons_Coord= new Vector<Point>(0);
	Vector <Image> Buttons_Pic= new Vector<Image>(0);
	Vector <Integer> Buttons_State= new Vector<Integer>(0);
	
	//Audio
	AudioEffects audBGM= new AudioEffects(true),audTORCH= new AudioEffects(true);
	
	/**
	 * Constructor used to initialize everything
	 */
	public TitleScreen(){
		//Initialize
		super("");
		setBounds(this.getToolkit().getScreenSize().width/2-250,this.getToolkit().getScreenSize().height/2-300,500,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		setVisible(true);
		
		//Get images
		try{
			
			if(isJar()){
				JOptionPane.showMessageDialog(null, "its a jar");
				TITLE=getImage("/Graphics/Backgrounds/","Icon.png");
				BACK=getImage("/Graphics/Backgrounds/","intro.png");
				TORCH=getImage("/Graphics/Special/","torch.png");
				
			}else{
				TITLE=ImageIO.read(Paths.get(DIRECT+"Icon.png").toFile());
				BACK=ImageIO.read(Paths.get(DIRECT+"intro.png").toFile());
				TORCH=ImageIO.read(Paths.get(DIRECT2+"torch.png").toFile());
			}
			
		}catch(Exception e){JOptionPane.showMessageDialog(null, "Title Screen::could not open image");}
		
		//Set button coordinates
		Buttons_Coord.add(new Point(100,200));
		Buttons_Coord.add(new Point(100,300));
		Buttons_Coord.add(new Point(100,400));
		
		//Get button graphics
		for(int i=0;i<Buttons_Coord.size();i++){
			Buttons_State.add(1);
			Buttons_Pic.add(TORCH);
		}
		
		//Start Audio
		audBGM.playAudio("haunting.wav");
		audTORCH.playAudio("crackle.wav");
		
		//Start timer
		tmrANIM.start();
	}
	
	/**
	 * Create an instance of this class
	 * @param args
	 */
	public static void main(String args[]){
		TitleScreen ABC= new TitleScreen();
	}
	
	/**
	 * Animation Timer
	 */
	public void actionPerformed(ActionEvent event){
		//Update graphics
		update(this.getGraphics());
		//Animate torch
		FrameChange(6,15,1);
		
		
		try{
			//Update button image
			for(int i=0;i<Buttons_Pic.size();i++){
				if(isJar()){
					Buttons_Pic.setElementAt(getImage("/Graphics/Special/","btn"+i+"-"+Buttons_State.elementAt(i)+".png"),i);
				}else{
					Buttons_Pic.setElementAt(ImageIO.read(Paths.get(DIRECT2+"btn"+i+"-"+Buttons_State.elementAt(i)+".png").toFile()),i);
				}
			}
		}catch(Exception e){}
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
			if(FX>MAX){FX=3;}
			
			//Reset
			COUNTER=0;
		}
		
		
	}
	/**
	 * Update the graphics
	 */
	public void update(Graphics g){
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		
		//What to draw
		
		//Draw the background image
		dbg.drawImage(BACK, 0, 0, 500, 600, null);
		
		//Draw the logo
		dbg.drawImage(TITLE, 0, 0, 500, 200, null);
		
		//Draw the torches
		dbg.drawImage(TORCH, X1, Y1, (int)(X1+W*G), (int)(Y1+H*G), (int)(W*FX), 0, (int)(W*FX+W), H, null);
		dbg.drawImage(TORCH, X1, Y1+300, (int)(X1+W*G), (int)(Y1+300+H*G), (int)(W*FX), 0, (int)(W*FX+W), H, null);
		dbg.drawImage(TORCH, X1+400, Y1, (int)(X1+400+W*G), (int)(Y1+H*G), (int)(W*FX-1), 0, (int)(W*FX-1+W), H, null);
		dbg.drawImage(TORCH, X1+400, Y1+300, (int)(X1+400+W*G), (int)(Y1+300+H*G), (int)(W*FX-1), 0, (int)(W*FX-1+W), H, null);
		
		//Draw the buttons
		for(int i=0;i<Buttons_Pic.size();i++){
			dbg.drawImage(Buttons_Pic.elementAt(i), Buttons_Coord.elementAt(i).x, Buttons_Coord.elementAt(i).y, 300, 100, null);
		}
		

		//Double buffering
		g.drawImage(dbImage,0,0,this);
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * used to detect mouseover for buttons
	 */
	public void mouseMoved(MouseEvent event) {
		for(int i=0;i<Buttons_Coord.size();i++){
			//Set state to unselected
			Buttons_State.setElementAt(1, i);
			
			//Detect collision
			if ((event.getX()>=Buttons_Coord.elementAt(i).x) && (event.getX()<=Buttons_Coord.elementAt(i).x+300)){
				if ((event.getY()>=Buttons_Coord.elementAt(i).y) && (event.getY()<=Buttons_Coord.elementAt(i).y+100)){
					//Set state to selected
					Buttons_State.setElementAt(2, i);
				}
			}
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
	/**
	 * Detect button click
	 */
	public void mousePressed(MouseEvent event) {
		for(int i=0;i<Buttons_Coord.size();i++){
			//Detect collision
			if ((event.getX()>=Buttons_Coord.elementAt(i).x) && (event.getX()<=Buttons_Coord.elementAt(i).x+300)){
				if ((event.getY()>=Buttons_Coord.elementAt(i).y) && (event.getY()<=Buttons_Coord.elementAt(i).y+100)){
					
					//Take action depending on button index
					switch(i){
					
						case 0://New game
							tmrANIM.stop();
							this.dispose();
							
							audBGM.Stop();audTORCH.Stop();
							clsGame ABC= new clsGame("Town",false);

							break;
						
						case 1://Load game
							
							break;
							
						case 2://Exit game
							
							System.exit(0);
							break;
							
					}
					
				}
			}
		}

		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
