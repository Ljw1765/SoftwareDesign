import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.*;

public class MainFrame extends JFrame{
    private JPanel panel1;
    private JMenuBar myMenuBar;
    private JMenuBar myMenuBarBar;
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

    //  MAIN METHOD
    public static void main(String[] args) {
//      JFrame frame = new JFrame("MainFrame");
//      frame.setContentPane(new MainFrame().panel1);
//      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      frame.pack();
//      frame.setVisible(true);
		new MainFrame();
    }
	
	//  CONSTRUCTOR FOR OUR WINDOW
    public MainFrame(){
        JScrollPane scrollPane = new JScrollPane(codeEdit, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		codeEdit = new JTextArea();

        //  Add Filter for text files
        FileFilter txtFilter = new FileNameExtensionFilter("Plain text", "txt");
        fc.setFileFilter(txtFilter);

        //  MENU and MENU ITEMS
        
		myMenuBar = new JMenuBar();
		
		file = new JMenu("File");
        file.add(Open);
        file.add(Save);
        file.add(New);
        file.add(Close);
		
        myMenuBar.add(file);
		
		add(myMenuBar);
        setJMenuBar(myMenuBar);
        
		//add(scrollPane);
        //scrollPane.add(codeEdit);
        add(codeEdit);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //  PERFORM ACTIONS HERE:
    Action Open = new AbstractAction("Open"){
        @Override
        public void actionPerformed(ActionEvent e){
            openFile();
        }
    };

    Action Save = new AbstractAction("Save") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    };

    Action New = new AbstractAction("New") {
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
    public void openFile(){
        if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			File fi = new File(fc.getSelectedFile().getAbsolutePath());
			
			try { 
				// String 
				String s1 = "", sl = ""; 

				// File reader 
				FileReader fr = new FileReader(fi); 

				// Buffered reader 
				BufferedReader br = new BufferedReader(fr); 

				// Initilize sl 
				sl = br.readLine(); 

				// Take the input from the file 
				while ((s1 = br.readLine()) != null) { 
					sl = sl + "\n" + s1; 
				} 

				// Set the text 
				codeEdit.setText(sl); 
			} 
			catch (Exception evt) { 
				JOptionPane.showMessageDialog(new JFrame(), evt.getMessage()); 
			}
		}
    }

    private void createFile() { //  COME BACK TO THIS METHOD
        codeEdit.setText("");
    }

    public void saveFile(){
        if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            FileWriter fw = null;
            try{
                String fileName = fc.getSelectedFile().getAbsolutePath();
				if(true)//TODO: Need to check for pre-existing extension in file name
				{
					fw = new FileWriter(fc.getSelectedFile().getAbsolutePath());
				}
				else
				{
					fw = new FileWriter(fc.getSelectedFile().getAbsolutePath() + ".txt");
				}
                codeEdit.write(fw);
                fw.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    
}
