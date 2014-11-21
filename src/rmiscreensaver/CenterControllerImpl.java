/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiscreensaver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Framework;
import main.Game;
import rmiscreensaver.CharacterMessage;
import rmiscreensaver.ContentMessage;
import rmiscreensaver.RegisterMessage;

/**
 *
 * @author hoangdat
 */
public class CenterControllerImpl extends UnicastRemoteObject implements CenterController{
    
    public final static int SESSION_CONFIGURING = 0;
    public final static int SESSION_RUNNING     = 1;
    
    public CenterControllerImpl() throws RemoteException {
        
    }

    @Override
    public RegisterMessage register(RegisterMessage resMes) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeBackground(String Imgname) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bcchangeBackground(String Imgname) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ContentMessage loadContent() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configureContent(String backgroundName, String characterName, int numberCha) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bcconfigureContent(String backgroundName, String characterName, int numberCha) throws RemoteException {
        Framework.nameBackground = backgroundName;
        Framework.nameCharacter = characterName;
//        System.out.println("Dau vao: " + backgroundName + "\n" 
//                         + characterName);
//        System.out.println("Dau ra: " + Framework.nameBackground + "\n" 
//                         + Framework.nameCharacter);
        Framework.countCharacter = numberCha;
        Framework.isDoneConfig = true;
    }

    @Override
    public boolean start() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean bcstart() throws RemoteException {
        Framework.isStarted = true;
        if (Framework.isStarted) {
            System.out.println("kich hoat roi");
        } else {
            System.out.println("khong kich hoat duoc");
        }
        return true;
    }

    @Override
    public ArrayList<CharacterMessage> getCharacter() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putClientQueue(ArrayList<CharacterMessage> arrChars) throws RemoteException {
        System.out.println("Goi den put queue");
        try {
            if (arrChars.size() > 0) {
                for (int i = 0; i < arrChars.size(); i++) {
                    System.out.println("Put queue "+arrChars.get(i).getId()
                    + " " + arrChars.get(i).getX() + " ClientID " + arrChars.get(i).getClientID());
                }
                
                Game.queues.put(arrChars);
            }
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(CenterControllerImpl.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendReady(boolean isReady) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
