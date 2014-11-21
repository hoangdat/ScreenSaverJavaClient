package main;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.Canvas;
import main.Game;
import rmiscreensaver.CenterController;
import rmiscreensaver.CenterControllerImpl;
import rmiscreensaver.ContentMessage;
import rmiscreensaver.RegisterMessage;

/**
 * Framework that controls the game (Game.java) that created it, update it and draw it on the screen.
 * 
 * @author www.gametutorial.net
 */

public class Framework extends Canvas {
    
    private String id;
    private CenterControllerImpl client;
    private CenterController server;
    
    private boolean isAdmin;
    public static boolean isDoneConfig = false; 
    public static boolean isStarted = false;
    public static String nameBackground = "";
    public static String nameCharacter = "";
    public static int countCharacter;
    private int countClickInMenu;
    
    private static final int countTest = 2;
    
    
    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
    
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;
    
    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;
    
    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
    
    /**
     * Possible states of the game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, 
                                                    OPTIONS, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;
    
    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;
    
    // The actual game
    private Game game;
    
    
    public Framework ()
    {
        super();
        
        isAdmin          = false;
        isDoneConfig     = false;
        isStarted        = false;
        countClickInMenu = 0;
        
        gameState = GameState.VISUALIZING;
        
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }
    
    
   /**
     * Set variables and objects.
     * This method is intended to set the variables and objects for this class, 
     * variables and objects for the actual game can be set in Game.java.
     */
    private void Initialize()
    {

    }
    
    /**
     * Load files - images, sounds, ...
     * This method is intended to load files for this class, files for the actual 
     * game can be loaded in Game.java.
     */
    private void LoadContent()
    {
    
    }
    
    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
        
        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;
        
        while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;
                    
                    game.UpdateGame();
                    
                    lastTime = System.nanoTime();
                break;
                case GAMEOVER:
                    //...
                break;
                case MAIN_MENU:
                    if (!isAdmin) {
                        if (isDoneConfig) {
                            URL bgURL = getClass().getClassLoader().getResource(nameBackground);
                            URL chURL = getClass().getClassLoader().getResource(nameCharacter);
                            if (isStarted) {
                                newGame(bgURL, chURL, countCharacter);
                            }
                        }
                    }
                break;
                case OPTIONS:
                    //...
                break;
                case GAME_CONTENT_LOADING:
                    //...
                break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                break;
                case VISUALIZING:
                    // On Ubuntu OS (when I tested on my old computer) this.getWidth() method doesn't return the correct value immediately (eg. for frame that should be 800px width, returns 0 than 790 and at last 798px). 
                    // So we wait one second for the window/frame to be set to its correct size. Just in case we
                    // also insert 'this.getWidth() > 1' condition in case when the window/frame size wasn't set in time,
                    // so that we although get approximately size.
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();
                        
                        try {
                            client = new CenterControllerImpl();
                            Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                            server = (CenterController) Naming.lookup("ScreenSaver_Service");
                            
                            RegisterMessage regMes = new 
                                    RegisterMessage(frameWidth, frameHeight, client);
                            
                            RegisterMessage resultMes = new RegisterMessage();
                            int i = 0;
                            for (i = 0; i < 3; i++) {
                                resultMes = server.register(regMes);
                                if (resultMes != null) {
                                    break;
                                }
                            }
                            
                            System.out.println("=-===-=-=-=-=-=-==-=");
                            System.out.println(resultMes.getId());
                            
                            if (i == 2 && resultMes == null) {
                                throw new RuntimeException("Registed failed");
                            }
                            
                            if (resultMes.isIsAdmin()) {
                                
                                setIsAdmin(true);
                                setId(resultMes.getId());
                                gameState = GameState.STARTING;
                                
                            } else {
                                
                                setIsAdmin(false);
                                if (resultMes.getServerState() == 
                                        CenterControllerImpl.SESSION_CONFIGURING) {
                                    
                                    setId(resultMes.getId());
                                    gameState = GameState.STARTING;
                                    
                                } else {
                                    //System.out.println("Vao Truong hop giua chung");
                                    setId(resultMes.getId());
                                    //test ki truong hop nay
                                    ContentMessage content =  server.loadContent();
                                    //lap tuc tham gia vong lap game ngay
                                    URL bgURL = getClass().getClassLoader()
                                            .getResource(content.getBackgroundName());
                                    
                                    URL chURL = getClass().getClassLoader()
                                            .getResource(content.getCharacterName());
                                    
                                    int chCount = content.getCharacterCount();
                                    
                                    System.out.println(content.getBackgroundName());
                                    
                                    newGame(bgURL, chURL, chCount);
                                }
                            }
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // When we get size of frame we change status.
                        //gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                break;
            }
            
            // Repaint the screen.
            repaint();
            
            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10) 
                timeLeft = 10; //set a minimum
            try {
                 //Provides the necessary delay and also yields control so that other thread can do work.
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }
    
    /**
     * Draw the game to the screen. It is called through repaint() method in GameLoop() method.
     */
    @Override
    public void Draw(Graphics2D g2d)
    {
        switch (gameState)
        {
            case PLAYING:
                game.Draw(g2d, mousePosition());
            break;
            case GAMEOVER:
                //...
            break;
            case MAIN_MENU:
                try {
                    
                    URL bgMenuURL = getClass().getClassLoader()
                            .getResource("main/background/background1.jpg");
                    BufferedImage bgMenuBF = ImageIO.read(bgMenuURL);
                    g2d.drawImage(bgMenuBF, 0, 0, frameWidth, frameHeight, null);
                    
                    if (isAdmin) {
                        
                        //draw a menu for background
                        g2d.drawString("Choose your background", frameWidth / 2 - 170, frameHeight / 2 - 120);
                        
                        URL op1URL = getClass().getClassLoader().getResource("main/background/background1.jpg");
                        BufferedImage bfOp1 = ImageIO.read(op1URL);
                        g2d.drawImage(bfOp1, frameWidth / 2 - 170, frameHeight / 2 - 30, 60, 60, null);
                        
                        URL op2URL = getClass().getClassLoader().getResource("main/background/background2.jpg");
                        BufferedImage bfOp2 = ImageIO.read(op2URL);
                        g2d.drawImage(bfOp2, frameWidth/2 - 170, frameHeight/2 + 60, 60, 60, null);
                        
                        //draw a menu for character
                        g2d.drawString("Choose your character", frameWidth/2 + 120, frameHeight/2-120);
                        
                        URL ch1URL = getClass().getClassLoader().getResource("main/characterimg/Option1.png");
                        BufferedImage bfch1 = ImageIO.read(ch1URL);
                        g2d.drawImage(bfch1, frameWidth/2 + 120, frameHeight / 2 - 30, 60, 60, null);
                        
                        URL ch2URL = getClass().getClassLoader().getResource("main/characterimg/Option2.png");
                        BufferedImage bfch2 = ImageIO.read(ch2URL);
                        g2d.drawImage(bfch2, frameWidth/2 + 120, frameHeight / 2 + 60, 60, 60, null);
                        
                    } else {
                        g2d.drawString("Waiting for config", 
                                    frameWidth/2 - 83, (int)(frameHeight * 0.65));
                    }
                    
                } catch (Exception e) {
                    Logger.getLogger(Framework.class.getName())
                                .log(Level.SEVERE, null, e);
                }
            break;
            case OPTIONS:
                //...
            break;
            case GAME_CONTENT_LOADING:
                //...
            break;
        }
    }
    
    
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game = new Game();
    }
    
    private void newGame(URL bgURL, URL chURL, int numChar) {
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game = new Game(server, client, isIsAdmin(), getId(), bgURL, chURL, numChar);
    }
    
    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game.RestartGame();
        
        // We change game status so that the game can start.
        gameState = GameState.PLAYING;
    }
    
    
    /**
     * Returns the position of the mouse pointer in game frame/window.
     * If mouse position is null than this method return 0,0 coordinate.
     * 
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();
            
            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }
    
    
    /**
     * This method is called when keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        
    }
    
    /**
     * This method is called when mouse button is clicked.
     * 
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (isAdmin) {
            switch (gameState) {
                
                case MAIN_MENU:
                    if (e.getButton() == MouseEvent.BUTTON1){
                        
                        if(new Rectangle(frameWidth / 2 - 170, frameHeight/2 - 30, 60, 60)
                                .contains(e.getPoint())) {
                            nameBackground = "main/background/background1.jpg";
                            countClickInMenu += 1;
                        } else if (new Rectangle(frameWidth/2 - 170, frameHeight/2 + 60, 60, 60)
                                .contains(e.getPoint())) {
                            nameBackground = "main/background/background2.jpg";
                            countClickInMenu += 1;
                            
                        } else if (new Rectangle(frameWidth/2 + 120, frameHeight/2 - 30, 60, 60)
                                .contains(e.getPoint())) {
                            nameCharacter = "main/characterimg/Option1.png";
                            countClickInMenu += 1;
                        } else if (new Rectangle(frameWidth/2 + 120, frameHeight/2 + 60, 60, 60)
                                .contains(e.getPoint())) {
                            nameCharacter = "main/characterimg/Option2.png";
                            countClickInMenu += 1;
                        }
                        
                        if (countClickInMenu >= 2 && !nameBackground.isEmpty()
                                                  && !nameCharacter.isEmpty()) {
                            //tien hanh dong bo background va content cho cac client khac
                            //tao new game cua minh
                            try {
                                //so ngoi sao hien gio dang fix, chua duoc lua chon
                                System.out.println("Vao cho goi newGame");
                                server.configureContent(nameBackground, nameCharacter, countTest);//block hay non block
                                URL bgURLchoosed = getClass().getClassLoader().getResource(nameBackground);
                                URL chURLchoosed = getClass().getClassLoader().getResource(nameCharacter);
                                newGame(bgURLchoosed, chURLchoosed, countTest);//test o day
                                
                            } catch (RemoteException re) {
                                Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, re);
                            }
                        }
                    }
                        
                    break;
            }
        }
    }
}
