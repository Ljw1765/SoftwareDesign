import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainFrame extends JFrame{
    private JPanel panel1;
    private JMenuBar myMenu;
    private JMenuBar myMenuBar;
    private JMenu file;
    private JMenu fileMenu;
    private JMenu projectMenu;
    private JMenu compileMenu;
    private JMenu executeMenu;
    private JTextField keywordsTextField;
    private JMenuItem myOpen;
    private JMenuItem myCreate;
    private JMenuItem myClose;
    private JMenuItem mySave;
    private JTextArea codeEdit;
    private JFileChooser fc = new JFileChooser();

    //  CONSTRUCTOR FOR OUR WINDOW
    public MainFrame(){
        JScrollPane scrollPane = new JScrollPane(codeEdit, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        //  Add Filter for text files
        FileFilter txtFilter = new FileNameExtensionFilter("Plain text", "txt");
        fc.setFileFilter(txtFilter);

        //  MENU and MENU ITEMS
        add(scrollPane);
        myMenu = new JMenuBar();
        setJMenuBar(myMenu);
        myMenu.add(file);
        file.add(Open);
        file.add(Save);
        file.add(Create);
        file.add(Close);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //  PERFORM ACTIONS HERE:
    Action Open = new AbstractAction("Open"){
        @Override
        public void actionPerformed(ActionEvent e){
            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                openFile(fc.getSelectedFile().getAbsolutePath());
            }
        }
    };

    Action Save = new AbstractAction("Save") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    };

    Action Create = new AbstractAction("Create") {
        @Override
        public void actionPerformed(ActionEvent e) {
            createFile();
        }
    };

    Action Close = new AbstractAction("Close") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };


    //  PERFORM ACTIONS METHODS HERE:
    public void openFile(String fileName){
        FileReader fr = null;
        try{
            fr = new FileReader(fileName);
            codeEdit.read(fr, null);
            fr.close();
            setTitle(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() { //  COME BACK TO THIS METHOD
        if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            FileWriter fw = null;
            try{
                fw = new FileWriter(fc.getSelectedFile().getAbsolutePath());
                codeEdit.write(fw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile(){
        if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            FileWriter fw = null;
            try{
                fw = new FileWriter(fc.getSelectedFile().getAbsolutePath() + ".txt");
                codeEdit.write(fw);
                fw.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //  MAIN METHOD
    public static void main(String[] args) {
//        JFrame frame = new JFrame("MainFrame");
//        frame.setContentPane(new MainFrame().panel1);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
        new MainFrame();
    }

}
