package App;

import Menus.LogInMenu;
import Menus.MainMenu;


public class App 
{
    private static String currentUser = "defaultuser";
    private static int currentUserID = 89;

    public static int flappyBirdID = 1;
    public static int snakeID = 2;
    public static int pacManID = 3;
    public static int mineSweeperID = 4;


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
       new MainMenu();

    }

}
