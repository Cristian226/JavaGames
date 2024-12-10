package org.example;

import Interfaces.LogInMenu;
import Interfaces.MainMenu;


public class App 
{
    private static String currentUser;
    private static int currentUserID = 0;


    public static void setUsername(String username){
        currentUser = username;
    }

    public static void setUserID(int id){
        currentUserID = id;
    }

    public static String getUsername(){
        return currentUser;
    }

    public static void main( String[] args )
    {
        LogInMenu logInMenu = new LogInMenu();

    }

}
