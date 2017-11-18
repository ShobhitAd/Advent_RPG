/**
 * @category Basic
 * 
 * Used to create, read and modify 2D maps
 * by saving them as .gmap files
 */
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.nio.file.*;
import java.io.*;


public class GridMap {
	//Number of X and Y blocks
	int BX=0,BY=0;
	//Fill number of each block
	int []Fill;

	//
	int imgCounter=0;

	//List of Fill Colors
	public Color[] Colors={Color.white.brighter(),Color.BLACK,Color.ORANGE,
					Color.YELLOW,Color.BLUE,Color.CYAN,Color.GREEN,Color.WHITE,Color.RED,Color.GRAY};
	//Coordinates of the blocks
	Point[] Grid;
	
	//boolean
	boolean[] Fog;
	int[] BRIGHT;
	
	//View size of the blocks
	int Size=0;
	
	//Tile Images
	String Direct=System.getProperty("user.dir")+"\\Graphics\\Tiles\\";
	Image[] imgTile;
	
	/**
	 * Constructor used to generate a new GridMap
	 * @param bx number of x blocks
	 * @param by number of y blocks
	 * @param s size of blocks
	 */
	public GridMap(int bx,int by,int s){
		//Assign to global variable	
		BX=bx;
		BY=by;
		Size=s;
		
		//Initialize Coordinates and Fill numbers
		Grid= new Point[BX*BY];	
		Fill= new int[BX*BY];
		Fog= new boolean[BX*BY];
		BRIGHT= new int[BX*BY];
		
		imgCounter=0;

		if(isJar()){
			imgCounter=180;
		}else{
			while(new File(Direct+imgCounter+".png").exists()){
				imgCounter++;
			}
		}
		

		
		imgTile= new Image[imgCounter];
		
		for(int i=0;i<BX*BY;i++){
			Grid[i]= new Point(0,0);
			Fill[i]=0;
			Fog[i]=false;
			BRIGHT[i]=0;
		}
		
		//Assign Coordinates for the blocks	
		for(int j=0;j<BY;j++){
			for(int i=0;i<BX;i++){
				
				//Block number
				int Index=i+j*BX;
		
				//Block coordinates
				Grid[Index].x=i*Size;
				Grid[Index].y=j*Size;
			}
		}
		//get Textures
		try{
			for(int i=0;i<imgTile.length;i++){
				if(isJar()){
					//Character
					imgTile[i]=getImage("/Graphics/Tiles/",i+".png");	
				}else{
					//Character
					imgTile[i]=ImageIO.read(new File(Direct+i+".png"));
				}
				
			}
		}catch(Exception e){}
	}
	
	/**
	 * Used to open a saved map for editing
	 * @param Direct Directory where the file is
	 * @param FName Name of the map file
	 * @param S View size of blocks
	 * @param Map Current GridMap
	 * @return The opened GridMap (or the old GridMap if error occurs)
	 */
	public GridMap OpenMap(String Direct, String FName,int S,GridMap Map){
		//Path of the file to open
		Path dir=null;
		try{
			if(isJar()){
				dir=Paths.get(getClass().getResource("/Graphics/Maps/"+FName+".gmap").toURI());
				//JOptionPane.showMessageDialog("", null);
			}else{
				dir=Paths.get(Direct+FName+".gmap");
			}
		}catch(Exception e){
			
		}
		
		//Check if map file exists
		if(!Files.exists(dir)){
			JOptionPane.showMessageDialog(null, "This file does not exist");
			return Map;
		}
		
		
		try {
			//File Reader
			Scanner Reader= new Scanner(dir.toFile());
			
			//Get number of blocks
			int bx=Integer.parseInt(Reader.nextLine());
			int by=Integer.parseInt(Reader.nextLine());
			
			//Get the fill numbers of each block
			int counter=0;
			int fill[]=new int[bx*by];
			
			//while(Reader.hasNext()){
			for(int i=0;i<bx*by;i++){
				fill[i]=Integer.parseInt(Reader.nextLine());
				//counter++;
			}
			
			//Generate new GridMap
			GridMap G= new GridMap(bx,by,S);
			
			//Assign the fill numbers
			G.Fill= fill;
			
			return G;
			
		} catch (Exception e) {}
		
		//return old map if error occurs
		return Map;
	}
	
	/**
	 * Draw the Map on the screen
	 * @param g Graphics of the component
	 * @param XOff X displacement
	 * @param YOff Y displacement
	 */
	public void  MapDraw(Graphics g,int XOff,int YOff){
		
		//For each block
		for(int i=0;i<BX*BY;i++){
			
			//If no fill then quit
			if(Fill[i]==0){continue;}
			
			//Color the block with the respective fill color
			g.setColor(Colors[Fill[i]]);
			g.fill3DRect(Grid[i].x+XOff, Grid[i].y+YOff,Size,Size, true);
			
		}
		
	}
	
	public void  Draw(Graphics g,int XOff,int YOff){
		//For each block
		for(int i=0;i<BX*BY;i++){
			//Draw texture
			g.drawImage(imgTile[Fill[i]], Grid[i].x+XOff, Grid[i].y+YOff, Grid[i].x+XOff+Size, Grid[i].y+YOff+Size, 0, 0, imgTile[Fill[i]].getWidth(null), imgTile[Fill[i]].getHeight(null), null);
						
		}
		
	}
	
	public void  FogDraw(Graphics g,int XOff,int YOff){
		//For each block
		for(int i=0;i<BX*BY;i++){
			//Draw texture
			if(BRIGHT[i]==0){
				g.setColor(new Color(0,0,0,100));
				if(Grid[i].x+XOff+Size>0 && Grid[i].y+YOff+Size>0){
					g.fill3DRect(Grid[i].x+XOff, Grid[i].y+YOff, Size, Size,false);
				}
			}
			if(Fog[i])continue;
			
			
			g.setColor(Color.black.darker());
			//g.fill3DRect( Grid[i].x+XOff, Grid[i].y+YOff, Size, Size,true);
			//g.fillOval(Grid[i].x+XOff-10, Grid[i].y+YOff-10, Size+10, Size+10);
			g.fillRoundRect(Grid[i].x+XOff-10, Grid[i].y+YOff-10, Size+10, Size+10, 20, 20);
		}
		
	}
	
	public void RevealFog(GameCharacter Charac){
		Point2D.Double Center=new Point2D.Double(Charac.X+Charac.W/2*Charac.GROWTHFACTOR,Charac.Y+Charac.H/2*Charac.GROWTHFACTOR);
		
		for(int i=0;i<BRIGHT.length;i++){
			if(BRIGHT[i]!=1){
				BRIGHT[i]=0;
			}
		}
		
		int Radius=100,BRadius=75;
		for(double i=0;i<2*Math.PI;i+=Math.PI/10){
			for(int j=0;j<Radius+1;j+=5){	
				int Bnum=getBlockNum(Center.x+j*Math.cos(i),Center.y+j*Math.sin(i));
				if(Bnum==-1)continue;
				if(BRIGHT[Bnum]!=1){BRIGHT[Bnum]=2;}
				Fog[Bnum]=true;
			}
			/*
			for(int j=0;j<BRadius+1;j++){	
				int Bnum=getBlockNum(Center.x+j*Math.cos(i),Center.y+j*Math.sin(i));
				if(Bnum==-1)continue;
				BRIGHT[Bnum]=true;
				//Fog[Bnum]=true;
			}
			*/
		}
	
	}
	public void RevealFog(GameObject Obj,int RAD){
		Point2D.Double Center=new Point2D.Double(Obj.X+Obj.W/2*Obj.GROWTHFACTOR,Obj.Y+Obj.H/2*Obj.GROWTHFACTOR);
		
		
		
		int Radius=RAD,BRadius=75;
		for(double i=0;i<2*Math.PI;i+=Math.PI/10){
			for(int j=0;j<Radius+1;j++){	
				int Bnum=getBlockNum(Center.x+j*Math.cos(i),Center.y+j*Math.sin(i));
				if(Bnum==-1)continue;
				//if(BRIGHT[Bnum]==1){continue;}
				BRIGHT[Bnum]=1;
				Fog[Bnum]=true;
			}
			/*
			for(int j=0;j<BRadius+1;j++){	
				int Bnum=getBlockNum(Center.x+j*Math.cos(i),Center.y+j*Math.sin(i));
				if(Bnum==-1)continue;
				BRIGHT[Bnum]=true;
				//Fog[Bnum]=true;
			}
			*/
		}
	
	}
	/**
	 * Find the block number at a given coordinate
	 * @param X X coordinate
	 * @param Y Y coordinate
	 * @return Block number
	 */
	public int getBlockNum(double X,double Y){
		
		int BNum=-1;
		
		//Loop over all X blocks and Y blocks
		for(int j=0;j<BY;j++){
			for(int i=0;i<BX;i++){
				
				//Block number
				int Index=i+j*BX;
				
				//Check X
				if((X>Grid[Index].x)&&(X<Grid[Index].x+Size)){
					//Check Y
					if((Y>Grid[Index].y)&&(Y<Grid[Index].y+Size)){
						//Assign  Block number
						BNum=Index;
					}
				}
			}
		}
		
		
		return BNum;
	}
	
	/**
	 * Save the map info in the form of a .gmap file
	 * @param Direct Directory in which to save
	 * @param fileN Name of the file
	 */
	public void SaveMap(String Direct,String fileN){
		//File Path
		Path Map=Paths.get(Direct+fileN+".gmap");
		
		//Overwrite file if it already exists
		if(Files.exists(Map)){
			int Ans=JOptionPane.showConfirmDialog(null, "This file already exists, Do you wish to overwrite it","OverWrite",JOptionPane.YES_NO_OPTION);
			//if no or cancel
			if(Ans!=JOptionPane.YES_OPTION){return;}
			//Delete current file
			boolean a=Map.toFile().delete();
			
		}
		
		try {
			//Create the file
			Files.createFile(Map);
			
			//Write the Map info to the file
			
				//X and Y block numbers
			String Text=BX+"\n"+BY;
				//Block Fill numbers
			for(int i=0;i<Fill.length;i++){
				Text+=("\n"+Fill[i]);
			}
			
			//Write to file
			FileOutputStream out= new FileOutputStream(Map.toFile());
			out.write(Text.getBytes());
				
		} catch (Exception e) {}
		
		
	}
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "gridmap");
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
