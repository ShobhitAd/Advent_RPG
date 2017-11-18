import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.imageio.*;
//import java.nio.file.*;
//import javax.imageio.*;
import javax.swing.*;

public class MsgBox extends JWindow implements ActionListener,MouseMotionListener,MouseListener{

	Timer tmrANIM= new Timer(1,this);
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Backgrounds\\",DIRECT2=System.getProperty("user.dir")+"\\Graphics\\Special\\";
	String TITLE="",DESCRIPT="";
	int CONT_STATE=1;
	Font fntDIABLO,fntGOW;
	boolean blnTOGGLE=false;
	clsGame GAME;
	AudioEffects audALERT= new AudioEffects(false);
	public MsgBox(Font fnt1,Font fnt2,clsGame G){
		super();
		int W=300,H=200;
		setBounds(this.getToolkit().getScreenSize().width/2-W,this.getToolkit().getScreenSize().height/2-H,2*W,2*H);
		fntDIABLO=fnt1;
		fntGOW=fnt2;
		GAME=G;
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	//	tmrANIM.start();
		//setVisible(true);
	}
	
	public void Draw(String title,String descript){
		blnTOGGLE=!blnTOGGLE;
		if(blnTOGGLE){
			tmrANIM.start();
			GAME.setPaused(true);
			setVisible(true);
			audALERT.playAudio("Alert.wav");
		}else{
			tmrANIM.stop();
			GAME.setPaused(false);
			setVisible(false);
		}
		
		TITLE=title;
		DESCRIPT=descript;
		
		
	}


	public void actionPerformed(ActionEvent event){
		update(this.getGraphics());	
	}
	
	
	public void TypeText(Graphics g,int x,int y,String Text){
		int Pos=0,PrevPos=0;
		while(Pos!=-1){
			Pos=Text.indexOf("\n", PrevPos+1);
			
			g.drawString(Text.substring(PrevPos, ((Pos!=-1)?Pos:Text.length()-1)),x,y);
			PrevPos=Pos;
			y+=30;
			
		}
	}
	
	public void update(Graphics g){
		
		//Double buffering
		Image dbImage=createImage(getWidth(),getHeight());
		Graphics dbg=dbImage.getGraphics();
		FontMetrics fntMet;
		//What to draw
		dbg.setColor(new Color(0,0,0));
		try{
			Image imgBack=ImageIO.read(new File(DIRECT+"Dialog.png"));
			dbg.drawImage(imgBack, 0, 0,getWidth(),getHeight(), null);
			
			dbg.setFont(fntDIABLO.deriveFont(Font.PLAIN,50));
			dbg.setColor(Color.yellow);
			fntMet=this.getFontMetrics(dbg.getFont());
			dbg.drawString(TITLE, getWidth()/2-fntMet.stringWidth(TITLE)/2, 50);
	
			dbg.setFont(fntGOW.deriveFont(Font.PLAIN,30));
			dbg.setColor(Color.white);
			fntMet=this.getFontMetrics(dbg.getFont());
			//dbg.drawString(DESCRIPT, 50, 110);
			TypeText(dbg,50,115,DESCRIPT);
			
			Image imgContinue=ImageIO.read(new File(DIRECT2+"Continue-"+CONT_STATE+".png"));
			dbg.drawImage(imgContinue,  getWidth()/2-100, getHeight()-100,200,75, null);
		}catch(Exception e){
			
		}
		//dbg.draw3DRect(0, 0, getWidth(), getHeight(), true);
		//dbg.fill3DRect(0, 0, getWidth(), getHeight(), true);
		
		//Double buffering
		g.drawImage(dbImage,0,0,this);
	
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(CONT_STATE==2){
			Draw("","");
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		int X=getWidth()/2-100,Y=getHeight()-100,W=200,H=75;
		CONT_STATE=1;
		
		if(event.getX()>X && event.getX()<X+W){
			if(event.getY()>Y && event.getY()<Y+H){
				CONT_STATE=2;
			}
		}
	}	
}
