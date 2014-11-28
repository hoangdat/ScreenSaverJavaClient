/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiscreensaver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import rmiscreensaver.CharacterMessage;
import rmiscreensaver.ContentMessage;
import rmiscreensaver.RegisterMessage;

/**
 *
 * @author hoangdat
 * @editor Muttet
 */
public interface CenterController extends Remote{
    public RegisterMessage register(RegisterMessage resMes) throws RemoteException;
    public void changeBackground(String Imgname) throws RemoteException;
    public void bcchangeBackground(String Imgname) throws RemoteException;
    public void configureContent(String backgroundName, String characterName, int numberCha) throws RemoteException;
    public void bcconfigureContent(String backgroundName, String characterName, int numberCha) throws RemoteException;
    public ContentMessage loadContent() throws RemoteException;
    public ArrayList<CharacterMessage> getCharacter() throws RemoteException;
    public void sendReady(boolean isReady) throws RemoteException;
    public boolean start() throws RemoteException;
    public boolean bcstart() throws RemoteException;
    public void putClientQueue(ArrayList<CharacterMessage> arrChars) throws RemoteException;
}
