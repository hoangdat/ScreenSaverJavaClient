/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MutTet
 */
public class BackGroudGame extends Canvas{
    static int numberStar;
    protected int width;
    protected int height;
    private static int degrees = 0;
    private static int timeChange = 0;
    
    public Font monoFont = new Font("Monospaced", Font.BOLD | Font.ITALIC, 40);

    public static int getDegrees() {
        return degrees;
    }

    public static void setDegrees(int degrees) {
        BackGroudGame.degrees = degrees;
    }

    public static int getTimeChange() {
        return timeChange;
    }

    public static void setTimeChange(int timeChange) {
        BackGroudGame.timeChange = timeChange;
    }    
    
    public void incDeg(){
        if(BackGroudGame.getDegrees() == 360){            
            BackGroudGame.setDegrees(0);
        } else {
            BackGroudGame.setDegrees(BackGroudGame.getDegrees()  + 1);
        }
    }
    
    
    public int getNumberStar() {
        return numberStar;
    }

    public void setNumberStar(int numberStar) {
        BackGroudGame.numberStar = numberStar;
    }
    
     public BackGroudGame(){
        this.numberStar = 200;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.width = screenSize.width;
        this.height = screenSize.height;
     }
    public BackGroudGame(int numberStart, int width, int height ) {
        this.numberStar = numberStart;
        this.width = width;
        this.height = height;
    }
    
    

    @Override
    public void Draw(Graphics2D g2d) {
        //Function nay se duoc lap luon tuc nho loop game
        //showInformation();
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        
        
        try {
            if(getTimeChange() == 0){
                for(int i = 0; i < this.getNumberStar(); i++){
                    int A = randomWithRange(0, this.width);
                    int B = randomWithRange(0, this.height);

                    int size = randomWithRange(1,7);           

                    g2d.setColor(Color.white);
                    g2d.fillOval(A, B, size, size); 
                }    
            } else {
                setTimeChange(getTimeChange() + 1);
                if(getTimeChange() == 100){
                    setTimeChange(0);
                }
            }
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(BackGroudGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void keyReleasedFramework(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int randomWithRange(int min, int max)
    {
       int range = (max - min) + 1;     
       return (int)(Math.random() * range) + min;
    }
    
    public void showInformation(){
        System.out.println("Number star: " + this.getNumberStar() 
                + "; width: " + this.width
                + "; height: " + this.height);        
    }
    
    public void getNameGroup(Graphics2D g2d, String nameGroup){
        
        g2d.setColor(Color.YELLOW); 
        g2d.setFont(this.monoFont);
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth("Le Xuan Hai");
        int h = fm.getAscent();
        g2d.drawString(nameGroup, 40, 40);   
    }
    
    
    public void DrawSun(Graphics2D g2d){        
        
        AffineTransform old = g2d.getTransform();
        incDeg();
        g2d.rotate(Math.toRadians(degrees));
        
        Color sunColor = new Color(246, 83, 39);
        g2d.setColor(sunColor);        
        g2d.fillOval(width - 200, 0, 200, 200 );
        
        g2d.setColor(Color.YELLOW);        
        g2d.fillOval(width - 175, 25, 150, 150 );
        
        g2d.setColor(Color.ORANGE);        
        g2d.fillOval(width - 150, 50, 100, 100 );
        
        g2d.setTransform(old);        
    }
}
