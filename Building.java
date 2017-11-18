import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * 
 * @category Basic
 * 
 * Used to create different type of characters
 *
 */

public class Building {
	//Position and Size
	public double X=0, Y=0;
	public double W=0,H=0;
	//Frames
	public int FX=0,FY=0;

	//Where to find character sprites
	String DIRECT=System.getProperty("user.dir")+"\\Graphics\\Buildings\\";
	String CLASS="CL";
	double GROWTHFACTOR=1;
	
	//Teleportation Target
	Point TARGET;
	//Sprite
	Image imgBUILDING;
	
	/**
	 * Constructor to create a character
	 * @param x X Position 
	 * @param y Y Position
	 * @param w Width of Image
	 * @param h	Height of Image
	 * @param FileN Name of image file
	 * @param S Max number of frames
	 */
	public Building(double x,double y,double w,double h,String FileN,String Cl){
		//Set position and size
		X=x;Y=y;
		W=w;H=h;
		
		//Get Image
		try{
			if(isJar()){
				imgBUILDING=getImage("/Graphic/Buildings/",FileN);
			}else{
				imgBUILDING=ImageIO.read(Paths.get(DIRECT+FileN).toFile());
			}
			
		}catch(Exception e){}
		
		//Name of Building
		CLASS=Cl;
		
	}
	
	/**
	 * Set teleportation target
	 * @param x Target X
	 * @param y Target Y
	 */
	public void setTarget(int x,int y){
		TARGET=new Point(x,y);
	}
	/**
	 * Draw building in game window
	 * @param g Graphics
	 * @param Dx X Displacement
	 * @param Dy Y Displacement
	 * @param G Growth factor
	 */
	public void Draw(Graphics g,int Dx,int Dy,double G){
		//Draw Image
		g.drawImage(imgBUILDING, (int)(X+Dx), (int)(Y+Dy), (int)(X+Dx+W*G), (int)(Y+Dy+H*G), (int)(W*FX), (int)(H*FY), (int)(W*FX+W), (int)(H*FY+H), null);
		GROWTHFACTOR=G;
	}
	
	/**
	 * Draw building on minimap
	 * @param g Graphics
	 * @param Dx X Displacement
	 * @param Dy Y Displacement
	 * @param G GrowthFactor
	 * @param Scale Scale down to mini map size
	 */
	public void MapDraw(Graphics g,int Dx,int Dy,double G,double Scale){
		//Draw Image
		g.drawImage(imgBUILDING, (int)(X*Scale+Dx), (int)(Y*Scale+Dy), (int)(X*Scale+Dx+W*G), (int)(Y*Scale+Dy+H*G), (int)(W*FX), (int)(H*FY), (int)(W*FX+W), (int)(H*FY+H), null);
		
	}
	
	
	/**
	 * Check if this x and y coordinate is within the component
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public boolean Contains(double x,double y){
	//Check collision	
		if((x>X)&&(x<X+W*GROWTHFACTOR)){
			if((y>Y)&&(y<Y+H*GROWTHFACTOR)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Interact with building
	 * @param Charac character that interacts with building
	 * @param cls instance of the game class
	 */
	public void Interact(GameCharacter Charac,clsGame cls){
		//Take action depending on the class of building
		switch(CLASS.toLowerCase()){

			case "portal":
				//Teleport player to location
				if(FY==0){
					Charac.X=TARGET.x;
					Charac.Y=TARGET.y;
				}
				break;
				
			case "dungeon level 1":
				//Open new map
				Charac.SPAWN_X=90;
				Charac.SPAWN_Y=90;
				cls.Setup("dungeon", true);
				
				break;
				
			case "dungeon_exit(town)":
				//Open new map
				Charac.SPAWN_X=2723;
				Charac.SPAWN_Y=1891;
				cls.Setup("Town", false);
				
				break;	
		}

	}
	
	public BufferedImage getImage(String Direct,String FName){
		//JOptionPane.showMessageDialog(null, "Building");
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
