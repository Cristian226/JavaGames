package App;

import Menus.LogInMenu;


public class App 
{
    private static String currentUser = "defaultuser";
    private static int currentUserID = 89;


    public static void setUsername(String username){
        currentUser = username;
    }

    public static void setUserID(int id){
        currentUserID = id;
    }

    public static String getUsername(){
        return currentUser;
    }

    public static int getUserID(){
        return currentUserID;
    }


    public static void main( String[] args )
    {
        LogInMenu logInMenu = new LogInMenu();

    }

}
