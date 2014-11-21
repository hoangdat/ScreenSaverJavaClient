package main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.Character;
import rmiscreensaver.CenterController;
import rmiscreensaver.CenterControllerImpl;
import rmiscreensaver.CharacterMessage;

/**
 * Actual game.
 * 
 * @author www.gametutorial.net
 */

public class Game {
    private String               id;
    private CenterControllerImpl client;
    private CenterController     server;
    private boolean              isAdmin;
    private BufferedImage        characterImg;
    private BufferedImage        backgroundImg;
    private int chSize;
    private ArrayList<Character> allCharacters;
    
    public static ArrayBlockingQueue<ArrayList<CharacterMessage>> queues;
    

    public BufferedImage getCharacterImg() {
        return characterImg;
    }

    public void setCharacterImg(BufferedImage characterImg) {
        this.characterImg = characterImg;
    }

    public BufferedImage getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(BufferedImage backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CenterControllerImpl getClient() {
        return client;
    }

    public void setClient(CenterControllerImpl client) {
        this.client = client;
    }

    public CenterController getServer() {
        return server;
    }

    public void setServer(CenterController server) {
        this.server = server;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getChSize() {
        return chSize;
    }

    public void setChSize(int chSize) {
        this.chSize = chSize;
    }
    

    public Game()
    {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();
                
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    public Game(CenterController server, CenterControllerImpl client, 
                        boolean isAdmin, String id, URL bgURL, URL chURL, int numChar) {
        
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        this.setIsAdmin(isAdmin);
        this.id = id;
        System.out.println("Vua khoi tao gia tri cho ID: " + id);
        this.setServer(server);
        this.setClient(client);
        this.setChSize(numChar);
        
        Thread threadForInitGame = new Thread() {

            @Override
            public void run() {
                Initialize();
                LoadContent(bgURL, chURL, numChar);
                
                
                if (isAdmin) {
                    try {
                        server.start();
                    } catch (RemoteException e) {
                        Logger.getLogger(Game.class.getName())
                                    .log(Level.SEVERE, null, e);
                    } 
                }
                
                Framework.gameState = Framework.GameState.PLAYING;
            }
            
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {
        allCharacters = new ArrayList<Character>();
        queues = new ArrayBlockingQueue<ArrayList<CharacterMessage>>(60, true);
    }
    
    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent()
    {
    
    }
    
    //sai o day chua khoi tao numChar
    private void LoadContent(URL bgURL, URL characterURL, int numChar) {
        
        try {
            
            backgroundImg = ImageIO.read(bgURL);
            characterImg  = ImageIO.read(characterURL);
            
            //khoi tao charater luon
            ArrayList<CharacterMessage> arrChars = server.getCharacter();
            
            if (arrChars.isEmpty()) {
                return;
            } else {
                for (CharacterMessage chM : arrChars) {
                    Character ch = new Character(chM.getId(), 
                                                 characterImg, 
                                                 chM.getCharacterWidth(), 
                                                 chM.getCharacterHeight(), 
                                                 false);
                    System.out.println(chM.getCharacterWidth() + " " 
                            + chM.getCharacterHeight());
                    allCharacters.add(ch);
                }
            }
            
            //set bien content done
        } catch (Exception e) {
            Logger.getLogger(Game.class.getName())
                               .log(Level.SEVERE, null, e);
        }
        
    }
    
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
        
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        
    }
    
    
    public void UpdateGame() {
        try {
            ArrayList<CharacterMessage> headMess = queues.take();
            
            if (headMess.isEmpty()) {
                return;
            }
            
            for (int i = 0; i < allCharacters.size(); i++) {
                Character tempCh = allCharacters.get(i);
                boolean isExisting = false;
                System.out.println("allCharacter la: " + tempCh.getId());
                
                
                for (int j = 0; j < headMess.size(); j++) {
                    CharacterMessage tempChMess = headMess.get(j);
                    System.out.println("====" + tempChMess.getId());
                    System.out.println("====" + tempCh.getId());
                    
                    if (tempChMess.getId().equals(tempCh.getId())) {
                        isExisting = true;
                        System.out.println("+CharacterCID+++" + tempChMess.getClientID() +"=====");
                        System.out.println("+IDClient+++++++" + getId() + "=====");
                        if (getId().equals(tempChMess.getClientID())) {
                            
                            System.out.println(tempChMess.getId() + "Da cung ClientID la: " 
                                    + tempChMess.getClientID());
                            tempCh.setIsDraw(true);
                            
                            tempCh.Update(tempChMess);
                            
                        } else {
                            System.out.println("Khong duoc ve vi khac ClientID character VS Client " 
                                    + tempChMess.getClientID() + " vs " + getId());
                            tempCh.setIsDraw(false);
                        }
                    }
                }
                
                if (isExisting == false) {
                    System.out.println("Khong duoc ve vi ID khong co " + tempCh.getId());
                    tempCh.setIsDraw(false);
                }
            }
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, 
                                        Framework.frameHeight, null);
        
        for (int i = 0; i < allCharacters.size(); i++) {
            allCharacters.get(i).Draw(g2d);
        }
    }
}
