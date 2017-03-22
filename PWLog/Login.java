

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;

/**
 * Login.java
 * Created by Rob on 3/11/2017.
 */
public class Login {
    private ArrayList<User> users=null;
    private Boolean accessGranted=false;
    private User u;
    /**
     * main()
     * calls new Login object
     */
    public static void main(String[] args) {
        String filename="C:\\Users\\Rob\\IdeaProjects\\PWLog\\out\\production\\PWLog\\Login.txt";

        System.out.print("Login: "+new Login(filename).login());
    }
    /**
     * fileNav()
     * Used for decoding
     * prints the directory
     *
     * @throws IOException file not found
     */
    private void fileNav(){
        java.util.List<String> results = new ArrayList<String>();
        File[] files = new File("C:\\Users\\Rob\\IdeaProjects\\PWLog\\out\\production\\PWLog").listFiles();
//If this pathname does not denote a directory, then listFiles() returns null.
        if(files!=null) {

            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                }
            }
        }
        else
            System.out.println("no files found");
    }

    /**
     *Constructor
     * populates user array
     * @param filename location of the file to grab login information
     */
    public Login(String filename){

        users=new ArrayList<>();
        loadFile(filename);
    }

    /**
     *Constructor
     * adds single user to array
     * @param username users username
     * @param password users password
     */
    public Login(String username, String password)
    {
        users=new ArrayList<>();
        users.add(new User(username, password.toCharArray()));
    }

    /**
     * loadFile(String)
     * populates Users ArrayList using the stored info
     *
     * @param filename file path to where the login info is stored
     * @throws IOException when file is miss-handled
     */
    private void loadFile(String filename){
        try{
            //fileNav();
            File f = new File(filename);
            if(f.exists() && !f.isDirectory()) {
                System.out.println("File "+filename+" found");

                String[] current = null;
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line=reader.readLine();
                //System.out.println("Line: "+line);
                while (line != null) {
                    current = line.split("-");
                    users.add(new User(current[0], current[1].toCharArray()));
                    //System.out.print("Add:" + current[0] + "-" + current[1]);
                    line=reader.readLine();
                }
            }
            else {
                System.out.println("File does not exist");
            }
        }catch (IOException ioe){
            System.out.println("Could not read file");
            //ioe.printStackTrace();

        }
    }
    /**
     * loginGUI()
     * GUI requesting username and password
     */
    private void loginGUI(){
        //System.out.print("Get Info");
        JFrame frame=new JFrame();


        frame.setSize(500,100);
        JPanel pane=new JPanel();
        JButton enterBtn = new JButton("Enter");
        JButton cancelBtn= new JButton("Cancel");

        pane.setLayout(new GridLayout(3,2));
        JTextField userTxt=new JFormattedTextField();
        JPasswordField passTxt=new JPasswordField();
        pane.add(new JLabel("Enter Username"));
        pane.add(userTxt);

        pane.add(new JLabel("Enter Password"));
        pane.add(passTxt);
        pane.add(enterBtn);
        pane.add(cancelBtn);


        frame.add(pane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        KeyListener keyListener=new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    if(userTxt.getText().equals(""))
                    {
                        accessGranted=true;
                    }else
                        accessGranted=validateUser(setUser(userTxt.getText(),passTxt.getPassword()));
                    if(accessGranted)
                    {
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        };
        userTxt.addKeyListener(keyListener);
        passTxt.addKeyListener(keyListener);
        enterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(userTxt.getText().equals("")&&passTxt.getPassword().length==0){
                    accessGranted=true;
                }else
                    accessGranted=validateUser(setUser(userTxt.getText(),passTxt.getPassword()));
                if(accessGranted){
                    frame.setVisible(false);
                    frame.dispose();
                }

            }
        });
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });


    }

    /**
     * setUser(String,char[])
     * create new User object from info given
     * @param username entered username
     * @param password entered password
     * @return user object with the given information
     */
    private User setUser(String username, char[] password){
        u=new User(username,password);
        return u;
    }
    /**
     * validateUser(User)
     * check if entered User matches current users
     * @param u entered User info
     * @return true/false the entered information matched in the array
     */
    private Boolean validateUser(User u){
        for (User user:users){
            if (user.isUser(u.getUser())) {

                if(user.isPass(u.getPassword())) {
                    System.out.println("Access Granted");
                    accessGranted=true;
                    return accessGranted;
                }
                else {
                    JOptionPane.showMessageDialog(null, "Invalid Password");
                    break;
                }
            }


        } JOptionPane.showMessageDialog(null,"Invalid Username");
        return false;
    }
/**
 * login()
 * callable method starts login process
 * @return User object of the successful login (null if unsuccessful)
 */
    public User login(){
        if(users==null || users.size()<1)
            return new User("","".toCharArray());
        loginGUI();

//create thread to keep checking validation?
        while(!accessGranted) {
            try {
                wait(100);
            }catch (Exception e){

            }
        }

        if(accessGranted)
            return u;
        else
            return null;
    }

}

/**USER
 * User.class
 *
 */
class User {
    private char[] password = null;
    private String user = null;
/**
 * Constructor
 * @param u username
 * @param p password
 *
 */
    public User(String u, char[] p) {
        user = u;
        password = p;
    }
/**Getters/Setters
 */
    public String getUser() {
        return user;
    }
    public void setUser(String u){ user=u;}
    public void setPassword(String p){password=p.toCharArray();}
    public char[] getPassword() {
        return password;
    }
    public String getDefaultFile(){return user+".txt"; }
/**
 * toString()
 * @return concatenated username and password
 */

    public String toString() {
        String pass="";
        if (password != null) {
            for (char pw:password ) {
                pass += Character.toString(pw);
            }
        }
        return user + "-" + pass;
    }
/**
 * isUser(String)
 * verifies if the string matches the user's username
 *
 * @param u username
 * @return true/false if the inputted String matches the username
 */
    public Boolean isUser(String u) {
        return user.equals(u);
    }
/**
 * isPass(char[])
 * verifies if the string matches the user's password
 *
 * @param p password
 * @return true/false if the inputted char[] matches the password
 */
    public Boolean isPass(char[] p) {
        if (password.length == p.length) {
            for (int i = 0; i < p.length; i++) {
                //every letter has to be the same
                if (password[i] != p[i])
                    //different letters
                    return false;
            }
            //same password
             return true;
        }
        //different lengths
        else return false;

    }


}
