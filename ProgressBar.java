import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
/**
 * Used to delay tasks
 * @category Basic
 *
 */
public class ProgressBar implements ActionListener {

	//Duration in seconds
	int DURATION=0,COUNTER=0;
	
	//event caption and status
	String CAPTION="",STATUS="";
	
	
	//Width of progress bar
	int WIDTH=500;
	
	//run the progress bar
	Timer tmrCOUNT= new Timer(1,this);
	
	//Font used for caption
	Font fntStyle=null;
	
	//Character doing the task
	GameCharacter CHARAC=null;
	
	
	Vector<String> TASK_LIST= new Vector<String>(), TASK_CAPTION= new Vector<String>();
	Vector<Integer> TASK_DURATION= new Vector<Integer>();
	
	/**
	 * Constructor for the bar
	 * @param fnt Font used for caption
	 * @param Charac GameCharacter doing the task
	 */
	public ProgressBar(Font fnt,GameCharacter Charac){
		//Assign
		fntStyle=fnt;
		CHARAC=Charac;
	}
	
	
	/**
	 * set Task for character
	 * @param Capt Caption for task
	 * @param Dur Duration for task
	 */
	public void setTask(String Capt,int Dur){
		//Reset Counter
		COUNTER=0;
		
		//Set caption and duration of task
		DURATION=Dur;
		CAPTION=Capt;
		
		//Activate
		STATUS="active";
		
		//Stop movement
		//CHARAC.CANMOVE=false;
		tmrCOUNT.start();
	}

	

	
	/**
	 * Draw a progress bar on the bottom
	 * @param g Graphics
	 * @param W Width of the screen
	 * @param H Height of the screen
	 */
	public void Draw(Graphics g,int W, int H){
		
		//Progress bar background
		g.setColor(new Color(20,20,20,170));
		g.fill3DRect(W/2-WIDTH/2, H-30, WIDTH, 30, false);
		
		//Progress bar filled
		g.setColor(new Color(0,255,0,200));
		g.fill3DRect(W/2-WIDTH/2, H-30, (int)Math.round(((double)(COUNTER)/(DURATION*1000))*WIDTH), 30, true);
		
		//Display caption
		g.setColor(new Color(255,255,255,255));
		g.setFont(fntStyle);
		g.drawString(CAPTION, W/2-CAPTION.length()*fntStyle.getSize()/4, H-5);
		
		
		//Progress bar border
		for(int i=0;i<4;i++){
			g.setColor(new Color(200+i*5,200+i*5,200+i*5,200));
			g.draw3DRect(W/2-WIDTH/2-i, H-30-i, WIDTH+2*i, 30+2*i, true);
		}
		
	}
	
	/**
	 *  Run timer
	 * @param event Action event
	 */
	public void actionPerformed(ActionEvent event){
		
		//Counter speed
		int Speed=100;
		
		//Increment counter
		COUNTER+=Speed;
		
		//Complete task
		if(COUNTER>=DURATION*1000){
			
			//Complete
			COUNTER=DURATION*1000;
			STATUS="complete";
			
			
			//Take action depending on event
			switch(CAPTION.toLowerCase()){
			
				case "sleeping":
					CHARAC.X-=40;
					break;
				
				case "speed":
					CHARAC.SPEED-=5;
					break;
			}
			
			//Disable timer
			CHARAC.CANMOVE=true;
			tmrCOUNT.stop();
			
		}

	}
	
	
}
