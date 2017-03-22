import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Passwords extends JApplet
{
    private JTextField accName, username, password, email, id;
    private Account[] accounts;

    private JComboBox list;
    private File file=null;
    private boolean skipLogin=true;
    private static String defaultSourceFile="C:\\Users\\Rob\\IdeaProjects\\PWLog\\out\\production\\PWLog\\Login.txt";
    private String newItem="Add new Account";
    private User u=null;
/**
 *Main
 * calls new JApplet.Passwords object
 * @param args arguments
 */
    public static void main(String[] args)
    {

           // System.out.print("Hello User");
            JFrame frame = new JFrame();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(500, 300);
            frame.setTitle("PWLog");

            Passwords applet = new Passwords();
            frame.add(applet);
            frame.setVisible(true);

    }



/**
 *Constructor
 * calls login, creates pane object
 */
    public Passwords()
    {
        file=null;
        Boolean access=login();
        if(access){
            if(u!=null)
            {
                file = new File("C:\\Users\\Rob\\IdeaProjects\\PWLog\\out\\production\\PWLog\\"+u.getDefaultFile());
            }else
                {
                file = null;
            }
        }

        Pane pane = new Pane();
        add(pane);

        openFile(file);

    }
/**
* login()
 * request login from user
 * @return whether login was successful
 */
    private Boolean login(){
        File f=new File(defaultSourceFile);
        if(f.exists()) {
            u = new Login(defaultSourceFile).login();

            if (u != null) {
                //System.out.println(u);
                return true;
            }
        }
        else if(skipLogin)
            return true;

        return false;

    }

/**
 *openFile(File)
 * loads accounts and list according to file
 * @param file location of the file to grab login information
 */
    private void openFile(File file)
    {
            //System.out.println("File: "+file.getName());
            if (loadFile(file)) {

                loadList();
            }
    }


/**
 *getFile()
 * select file from directory
 * @return selected file
 */
    private File getFile()
    {
        JFileChooser jfc=new JFileChooser();
        File file;
        if(jfc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
        {
            file= jfc.getSelectedFile();
            //System.out.println("Filename: "+file.getName());
            return file;

        }
        else return null;

    }

/**
 *addUser()
 * new User to list
 */
    private void addUser()
    {
        String username=input("Enter Username");
        String password=input("Enter Password");
        u=new User(username,password.toCharArray());
        ArrayList<String> users=getUsers();
        users.add(u.toString());
        saveUsers(users);
        openFile(null);

    }

/**
 *deleteUser()
 * remove current user from list
 */
    private void deleteUser()
    {
        ArrayList<String> users=getUsers();
        int index=findUser(u,users);
        if(index>=0)
            users.remove(index);
        else
            message("User not deleted");
        if(users.size()>0) {
            String[] info = users.get(0).split("-");

            u = new User(info[0], info[1].toCharArray());
        }
        saveUsers(users);
        openFile(null);
    }

/**
 *findUser(User, ArrayList<String>)
 * find user in array
 * @param u user to find in array
 * @param users array of user information
 */
    private int findUser(User u, ArrayList<String> users)
    {
        int i=0;
        for(String user: users)
        {
            System.out.println("Find: "+u+" == "+user);
            if(user.equals(u.toString()))
            {
                return i;
            }
            else
                i++;
        }
        return -1;
    }

/**
 *getUsers()
 * create list of Users info from file
 * @return User info list
 */
    private ArrayList<String> getUsers()
    {
        ArrayList<String> users=new ArrayList<>();
        try {
            DataInputStream input=new DataInputStream(new FileInputStream(new File(defaultSourceFile)));
            String line;
            line=input.readLine();
            while(line!=null)
            {
                users.add(line);
                System.out.println("Get: "+line);
                line=input.readLine();
            }
            input.close();


        }catch (IOException e){
            return new ArrayList<>();
        }
        return users;
    }

/**
 * saveUsers(ArrayList<String>)
 * saves user information to file
 * @param users list of user info
 */
    private void saveUsers(ArrayList<String> users) {
        try {
            java.io.PrintWriter output=new java.io.PrintWriter(new File(defaultSourceFile));
            for(String user: users) {
                output.println(user.trim());
                System.out.println("Save: "+user);
            }
            output.flush();
            output.close();

        }catch (IOException e){

        }

    }

/**
 *getFileLines(File)
 * go through file to count number of lines
 * @return number of lines in file
 */
    private int getFileLines(File f){
        try(LineNumberReader lnr = new LineNumberReader(new FileReader(f))) {
            while(lnr.skip(Long.MAX_VALUE) > 0){};
            return lnr.getLineNumber();
        }catch (IOException ioe){return 0;}
    }


/**
 * loadFile(File)
 * load file into accounts
 * @param file location of the file to grab account information
 */
    private boolean loadFile(File file)
    {
        if(file==null)
        {
            accounts=new Account[0];
            return false;
        }
        DataInputStream input;
        int size;
        String line;
        int curr;
        StringTokenizer tokens;
        try
        {
            size = getFileLines(file)+1;
            //System.out.println("Size:"+size);
            if(size>0) {
                //restart from start of file
                input = new DataInputStream(new FileInputStream(file));

                //create accounts list
                accounts = new Account[size];

                //get account information
                curr = 0;

                //skip line
                line=input.readLine();
                //System.out.println(line);
                // open txt file
                String fileName = file.getName();
                if (fileName.substring(fileName.length() - 4, fileName.length()).equals(".txt")) {
                    //first account
                    line=input.readLine();

                    while (line != null) {
                        //System.out.println(line);
                        //split
                        tokens = new StringTokenizer(line, "-");
                        if(tokens.countTokens()>0) {
                            try {
                                String account, id, email, username, password;
                                if (tokens.hasMoreTokens())
                                    account = tokens.nextToken();
                                else account = "";
                                if (tokens.hasMoreTokens())
                                    id = tokens.nextToken();
                                else id = "";
                                if (tokens.hasMoreTokens())
                                    email = tokens.nextToken();
                                else email = "";
                                if (tokens.hasMoreTokens())
                                    username = tokens.nextToken();
                                else username = "";
                                if (tokens.hasMoreTokens())
                                    password = tokens.nextToken();
                                else password = "";
                                accounts[curr] = new Account(account, id, email, username, password);
                                //System.out.println(curr+"-"+accounts[curr]);
                            } catch (NoSuchElementException nsee) {
                                //nsee.printStackTrace();
                            }

                            //move to next account
                            curr++;
                        }
                        line = input.readLine();

                    }
                    return true;
                }

                //open dat file - incomplete
                else if (fileName.substring(fileName.length() - 4, fileName.length()).equals(".dat")) {
                    while (input.available() > 0) {
                        tokens = new StringTokenizer(input.readUTF(), "-");
                        accounts[curr++] = new Account(tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), tokens.nextToken());
                        input.readChar();
                    }

                    return true;
                } else
                    errorMessage("Could not read file type");
            }

        }catch(IOException ioe)
        {
            //errorMessage("File not Found");
            //ioe.printStackTrace();
        }
        return false;

    }
    /**
     *Messages
     * simplify coding
     * @param message message to be posted
     */
    private void errorMessage(String message)
    {
        JOptionPane.showMessageDialog(null,"ERROR - "+message);
    }
    private void message(String message)
    {
        JOptionPane.showMessageDialog(null,message);
    }
    private String input(String message){   return JOptionPane.showInputDialog(null,message); }



/*******PANE.class*********/
    class Pane extends JPanel
    {
        /**
         *Constructor
         * GUI for Passwords
         */
        public Pane()
        {
            setJMenuBar(menuBar());
            JButton deleteBtn, updateBtn, addBtn;

            list=new JComboBox();
            list.addItem(newItem);
            list.setSelectedIndex(0);
            accounts=new Account[1];

            accName=new JTextField();
            accName.addFocusListener(new FocusListener()
            {
                public void focusGained(FocusEvent fe)
                {
                    accName.selectAll();
                }
                public void focusLost(FocusEvent fe){}
            });
            username=new JTextField();
            username.addFocusListener(new FocusListener()
            {
                public void focusGained(FocusEvent fe)
                {
                    username.selectAll();
                }
                public void focusLost(FocusEvent fe){}
            });

            password=new JTextField();
            password.addFocusListener(new FocusListener()
            {
                public void focusGained(FocusEvent fe)
                {
                    password.selectAll();
                }
                public void focusLost(FocusEvent fe){}
            });

            email=new JTextField();
            email.addFocusListener(new FocusListener()
            {
                public void focusGained(FocusEvent fe)
                {
                    email.selectAll();
                }
                public void focusLost(FocusEvent fe){}
            });

            id=new JTextField();
            id.addFocusListener(new FocusListener()
            {
                public void focusGained(FocusEvent fe)
                {
                    id.selectAll();
                }
                public void focusLost(FocusEvent fe)  {}
            });
            ButtonAction action=new ButtonAction();
            addBtn=new JButton("ADD");
            addBtn.addActionListener(action);

            deleteBtn=new JButton("REMOVE");
            deleteBtn.addActionListener(action);

            updateBtn=new JButton("UPDATE");
            updateBtn.addActionListener(action);

            JPanel center=new JPanel();
            center.setLayout(new GridLayout(6,2));
            center.add(list);
            center.add(new JLabel(""));
            center.add(new JLabel("Account"));
            center.add(accName);
            center.add(new JLabel("ID"));
            center.add(id);
            center.add(new JLabel("Username"));
            center.add(username);
            center.add(new JLabel("Password"));
            center.add(password);
            center.add(new JLabel("Email"));
            center.add(email);
            clear();


            JPanel south=new JPanel();
            south.setLayout(new FlowLayout());
            south.add(addBtn);
            south.add(updateBtn);
            south.add(deleteBtn);

            setLayout(new BorderLayout());
            add(center,BorderLayout.CENTER);
            add(south,BorderLayout.SOUTH);

        }

    }
/**
 *menuBar()
 * creates menu bar
 * @return  full menu bar
 */
    private JMenuBar menuBar(){
        //Where the GUI is created:
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;
        MenuAction action=new MenuAction();

//Create the menu bar.
        menuBar = new JMenuBar();

//Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.addActionListener(action);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save",KeyEvent.VK_S);
        menuItem.addActionListener(action);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save As",KeyEvent.VK_A);
        menuItem.addActionListener(action);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Close");
        menuItem.addActionListener(action);
        menuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(menuItem);

//a group of radio button menu items
  /*      menu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Another one");
        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

//a group of check box menu items

        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("Another one");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menu.add(cbMenuItem);

//a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);*/

//Build second menu in the menu bar.
        menu = new JMenu("Accounts");
        menu.setMnemonic(KeyEvent.VK_U);
        menuBar.add(menu);

        menuItem = new JMenuItem("Update Account");
        menuItem.addActionListener(action);
        menuItem.setMnemonic(KeyEvent.VK_P);
        menu.add(menuItem);

        menuItem = new JMenuItem("Delete Account");
        menuItem.addActionListener(action);
        menuItem.setMnemonic(KeyEvent.VK_D);
        menu.add(menuItem);

        menu = new JMenu("Users");
        menu.setMnemonic(KeyEvent.VK_U);
        menuBar.add(menu);

        menuItem = new JMenuItem("Add User");
        menuItem.addActionListener(action);
        menuItem.setMnemonic(KeyEvent.VK_A);
        menu.add(menuItem);

        menuItem = new JMenuItem("Delete User");
        menuItem.addActionListener(action);
        menuItem.setMnemonic(KeyEvent.VK_A);
        menu.add(menuItem);

        submenu = new JMenu("User Settings");
        submenu.addActionListener(action);
        submenu.setMnemonic(KeyEvent.VK_G);

        cbMenuItem = new JCheckBoxMenuItem("");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        submenu.add(cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("");
        cbMenuItem.setMnemonic(KeyEvent.VK_Q);
        submenu.add(cbMenuItem);

        menu.add(submenu);

        return menuBar;


    }

/**
 *loadList(int)
 * alls fillList and sets selected to given index
 * @param index index to select list
 */
    private void loadList(int index)
    {
        fillList();

        list.setSelectedIndex(index);
        displayPW(index);
    }

/**
 *loadList()
 * calls fillList and sets selected to 0
 */
    private void loadList(){
        fillList();

        list.setSelectedIndex(0);
        displayPW(0);
    }

/**
 *fillList()
 * creates a list based on the account names
 */
    private void fillList()
    {
        clear();
        list.removeAllItems();
        if(accounts.length>0) {
            //list.addItem(newItem);
            for (int i = 0; i < accounts.length; i++) {
                if(accounts[i]!=null) {
                    list.addItem(accounts[i].toString());
                    //System.out.println(accounts[i]);
                }
                else list.addItem("");
            }


            list.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent ie) {
                    if (list.getSelectedIndex() >= 0 && list.getSelectedIndex()<list.getItemCount()) {
                        clear();
                        displayPW(list.getSelectedIndex());
                    }
                }
            });
        }
    }

/**
 *displayPW(int)
 * fills text boxes with account information
 * @param index index of the account to grab
 */
    private void displayPW(int index)
    {
        if(accounts!=null && index>=0 && index < accounts.length && accounts [index]!=null) {
            try {
                accName.setText(accounts[index].getAccount());
                username.setText(accounts[index].getUsername());
                password.setText(accounts[index].getPassword());
                email.setText(accounts[index].getEmail());
                id.setText(accounts[index].getID());
            } catch (ArrayIndexOutOfBoundsException e) {
                //e.printStackTrace();
            }
        }
    }

/**
 * newAccount()
 * widen array to allow for another account
 */
    private void newAccount()
    {
        Account[] temp=new Account[accounts.length+1];
        System.arraycopy(accounts, 0, temp, 0, accounts.length);
        accounts=temp;
    }

/**
 *updateAccount(int)
 * update account information from text boxes
 * @param index index of account to update
 */
    private void updateAccount(int index)
    {
        if(index >=0 && index<accounts.length) {
            if (accounts[index] == null)
                accounts[index] = new Account();

            accounts[index].setAccount(accName.getText());
            accounts[index].setID(id.getText());
            accounts[index].setUsername(username.getText());
            accounts[index].setPassword(password.getText());
            accounts[index].setEmail(email.getText());
        }
        //message("Updated account: "+accounts[index].toString());
    }

/**
 *deleteAccount(int)
 * removes account at given position from the accounts array
 * @param pos index of file to remove
 * @return deleted account info
 */
    private String deleteAccount(int pos) {

        if (accounts != null && pos >= 0 && pos < accounts.length && accounts[pos] != null) {

            String info = accounts[pos].fullInfo();
            accounts[pos] = null;
            list.removeItemAt(pos);

            Account[] temp = new Account[accounts.length - 1];
            int i = -1;//accounts
            int j = 0;//temp
            while (++i < accounts.length) {
                if (accounts[i] != null) {
                    temp[j++] = accounts[i];
                }
            }

            accounts = temp;

            clear();

            message("Deleted account: " + info);
            return info;
        } else return null;
    }

/**
 *saveAs()
 * find file to save to
 */
    private void saveAs()
    {
        //System.out.println("Save As()");
        JFileChooser jfc=new JFileChooser();
        if(jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION && jfc.getSelectedFile()!=null)
        {
            file=jfc.getSelectedFile();
            String fileName=file.getName();
            String fileExt=fileName.substring(fileName.length()-4, fileName.length());
            if(!fileExt.equals(".txt") && !fileExt.equals(".dat"))
            {
                fileName+=".txt";
                file.renameTo(new File(fileName));
            }
            saveOver();

        }else {
            //errorMessage("File Not Saved");
        }
    }

/**
 *save()
 * calls saveAs or save Over
 */
    private void save()
    {
        if(file!=null)
            saveOver();
        else if(u!=null)
        {
            file=new File("C:\\Users\\Rob\\IdeaProjects\\PWLog\\out\\production\\PWLog\\"+u.getDefaultFile());
            saveOver();
        }
        else
            saveAs();
    }

/**
 *saveOver()
 * save all account information in file
 */
    private void saveOver()
    {
        updateAccount(list.getSelectedIndex());

        try
        {
            String fileName=file.getName();
            if(fileName.substring(fileName.length()-4,fileName.length()).equals(".dat"))
            {
                DataOutputStream output=new DataOutputStream(new FileOutputStream(file));


                for (Account account : accounts) {
                    if (account != null) {
                        output.writeUTF(account.fullInfo());
                        output.writeChar('\n');
                    }
                }
                output.close();


            }
            else if(fileName.substring(fileName.length()-4,fileName.length()).equals(".txt"))
            {
                java.io.PrintWriter output=new java.io.PrintWriter(file);
                //write first line
                output.println("ACCOUNT-ID-EMAIL-USERNAME-PASSWORD");
                for (Account account : accounts) {
                    if (account != null) {
                        output.println(account.fullInfo());
                    }
                }
                output.close();
            }

            //message("Accounts saved to file "+file.getName());
        }catch(IOException ioe)
        {
            errorMessage("**ERROR**Could not save file.");
        }
    }

/**
 *clear()
 * empties text boxes
 */
    private void clear()
    {
        accName.setText("");
        id.setText("");
        username.setText("");
        password.setText("");
        email.setText("");
    }

/*******ACTION*********/

/**
 *ButtonAction.class
 */
    class ButtonAction implements ActionListener
    {
        /**
         *actionPerformed(ActionEvent)
         * activates buttons
         * @param e actionEvent
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==list)
            {
                displayPW(list.getSelectedIndex());
            }
            else if(((JButton)e.getSource()).getText().equals("REMOVE"))
            {
                if(JOptionPane.showConfirmDialog(null,"Confirm: Delete this account?","Delete Account",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.YES_OPTION)
                    deleteAccount(list.getSelectedIndex());

            }
            else if(((JButton)e.getSource()).getText().equals("UPDATE"))
            {
                updateAccount(list.getSelectedIndex());
                loadList(list.getSelectedIndex());

            }
            else if(((JButton)e.getSource()).getText().equals("ADD"))
            {
                newAccount();

                loadList();
                clear();
                if(list.getItemCount()>0)
                list.setSelectedIndex(list.getItemCount()-1);
                else list.setSelectedIndex(0);
            }

        }
    }
/**
 *MenuAction.class
 */
    class MenuAction implements ActionListener
    {
/**
 *actionPerformed(ActionEvent)
 * activates menu
 * @param e actionEvent
 */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println();
            if (((JMenuItem)e.getSource()).getText().equals("Open"))
            {
                file=getFile();
                if(file!=null)
                openFile(file);
            }
            else if(((JMenuItem)e.getSource()).getText().equals("Close"))
            {
                System.exit(0);
            }
            else if (((JMenuItem)e.getSource()).getText().equals("Save"))
            {
                save();
            }
            else if(((JMenuItem)e.getSource()).getText().equals("Update Account")) {
                updateAccount(list.getSelectedIndex());
                loadList(list.getSelectedIndex());

            }
            else if(((JMenuItem)e.getSource()).getText().equals("Delete Account"))
            {
                deleteAccount(list.getSelectedIndex());
            }
            else if(((JMenuItem)e.getSource()).getText().equals("Add User"))
            {
                addUser();
            }
            else if(((JMenuItem)e.getSource()).getText().equals("Delete User"))
            {
                deleteUser();
            }
            else if(((JMenuItem)e.getSource()).getText().equals("User Settings"))
            {
                //settings
            }
            else if(((JMenuItem)e.getSource()).getText().equals("Save As"))
            {
                saveAs();
            }
        }
    }
}


/*******ACCOUNT*********/
/**
 *Account.class
 */
class Account
{
    private String account, username, password, email,id;
    /**
     *Constructor
     * new empty account
     */
     Account()
    {

     }

    /**
     *Constructor
     * populates account with content
     *
     * @param account account name
     * @param id account id#
     * @param email email address
     * @param password account password
     * @param username account username
     *
     */
     Account(String account,String id, String email, String username, String password)
    {
        this.account=account;

        if(!(account==null))
            this.username=username;
        if(!(password==null))
            this.password=password;
        if(!(email==null))
            this.email=email;
        if(!(id==null))
            this.id=id;
    }

/**
 *Getters/Setters
 */
     String getAccount()
    {
        return account;
    }
     String getUsername()
    {
        if(username==null)
            return "";
        else
            return username;
    }
     String getPassword()
    {
        if(password==null)
            return "";
        else
            return password;
    }
     String getEmail()
    {
        if(email==null)
            return "";
        else
            return email;
    }
     String getID()
    {
        return id;
    }
     void setAccount(String account)
    {
        this.account=account;
    }
     void setUsername(String username)
    {
        this.username=username;
    }
     void setPassword(String password)
    {
        this.password=password;
    }
     void setEmail(String email)
    {
        this.email=email;
    }
     void setID(String id)
    {
        this.id=id;
    } /**
 *toString()
 * prints account name
 * @return  account name
 */
    public String toString()
    {
        return account;
    }
/**
 *fullInfo()
 * used for printing to file
 * @return  all info for account
 */
String fullInfo()
    { //format ACCOUNT-ID-EMAIL-USERNAME-PASSWORD
        String info="";

        //ACCOUNT
        if(account!=null)
            info+=account;
        else
            info+=" ";

        //-
        info+="-";

        //ID
        if(id!=null)
            info+=id;
        else
            info+=" ";

        //-
        info+="-";

        //EMAIL
        if(email!=null)
            info+=email;
        else
            info+=" ";

        //-
        info+="-";

        //USERNAME
        if(username!=null)
            info+=username;
        else
            info+=" ";

        //-
        info+="-";

        //PASSWORD
        if(password!=null)
            info+=password;
        else
            info+=" ";

        return info;
    }

}


