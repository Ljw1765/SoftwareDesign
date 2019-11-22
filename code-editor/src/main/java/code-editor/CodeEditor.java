package Wolverines;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.event.*;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.Files.*;
import java.nio.file.Paths.*;
import java.util.List;
import java.util.stream.*;

class CodeEditor extends JFrame implements ActionListener
{
    
    //static JSyntaxPane codeNew; //text area
    JFrame mainPage; //main page of the code editor
	static JTabbedPane tabPane; //Tabs
	static File projectDir;
	
    CodeEditor()
    {
        //Window Details
		mainPage = new JFrame("Team Wolverines"); //Title bar
		mainPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		tabPane = new JTabbedPane();
		tabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				checkKeywords();
			}
		});
		
		//Menu bar
        JMenuBar MainMenuBar = new JMenuBar();

        //File menu
        JMenu fileMenu = new JMenu("File");
        
		JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem closeFile = new JMenuItem("Close");
        
		newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        closeFile.addActionListener(this);
        
		fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(closeFile);

        //Files Menu
        JMenu filesMenu = new JMenu("Source");
        
		JMenuItem addFile = new JMenuItem("Add a File");
        JMenuItem removeFile = new JMenuItem("Remove This File");
        
		addFile.addActionListener(this);
        removeFile.addActionListener(this);
        
		filesMenu.add(addFile);
        filesMenu.add(removeFile);

        //Execute menu
        JMenu executeMenu = new JMenu("Execute");
        
		JMenuItem compileProject = new JMenuItem("Compile");
        JMenuItem runProject = new JMenuItem("Run");
        
		compileProject.addActionListener(this);
        runProject.addActionListener(this);
        
		executeMenu.add(compileProject);
        executeMenu.add(runProject);

        //Adding the menus to the main menu bar
        MainMenuBar.add(fileMenu);
        MainMenuBar.add(filesMenu);
        MainMenuBar.add(executeMenu);
		
		//Add the menu bar
        mainPage.setJMenuBar(MainMenuBar);
		
		//Set up & add the tabPane
		//initialize();
		mainPage.add(tabPane);
		
		//Window Details
        mainPage.setSize(500, 500);
        mainPage.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
		
		if (s.equals("New")) 
		{
			int chooseResult = -1;
			tabPane.removeAll();
			
			// Create an object of JFileChooser class
			JFileChooser j = new JFileChooser();
			j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// Disable the "All files" option.
			j.setAcceptAllFileFilterUsed(false);
			
			//Select the directory
			chooseResult = j.showOpenDialog(this);
				
			if(chooseResult == JFileChooser.ERROR_OPTION)
			{
				JOptionPane.showMessageDialog(null, "There was an error.");
			}
			else if (chooseResult == JFileChooser.APPROVE_OPTION){
				//Get selected directory
				projectDir = j.getSelectedFile();
				
				//Check if Java Files aready exist here
				try (Stream<Path> walk = Files.walk(Paths.get(projectDir.toString()),1)) {
					List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".java")).collect(Collectors.toList());
					
					if(result.size() > 0)
					{
						JOptionPane.showMessageDialog(null, "It seems there's already a Java project here. If you want to include these files, use the Open command instead.");
					}
				}
				catch (IOException evt) {
					//If no readable Java files are in this directory
					JOptionPane.showMessageDialog(null, "There was an error loading a file.");
					evt.printStackTrace();
				}
				catch (NullPointerException evt) {
					//If no readable Java files are in this directory
					JOptionPane.showMessageDialog(null, "There are no accessible java files here.");
					evt.printStackTrace();
				}
				
				addSourceFile();
			}
			else {
				System.out.println("No Selection ");
			}
		} 
		else if (s.equals("Open")) {
			int chooseResult = -1;
			
			// Create an object of JFileChooser class
			JFileChooser j = new JFileChooser();
			j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// Disable the "All files" option.
			j.setAcceptAllFileFilterUsed(false);
			
			//Select the directory
			chooseResult = j.showOpenDialog(this);
				
			if(chooseResult == JFileChooser.ERROR_OPTION)
			{
				JOptionPane.showMessageDialog(null, "There was an error.");
			}
			else if (chooseResult == JFileChooser.APPROVE_OPTION) { 
				//Get selected directory
				projectDir = j.getSelectedFile();
				
				try (Stream<Path> walk = Files.walk(Paths.get(projectDir.toString()),1)) {
					List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".java")).collect(Collectors.toList());
					result.forEach(System.out::println);
					
					//Create needed objects for reading files
					File fi;
					FileReader fr;
					BufferedReader br;
					JTextPane newPane;
					String s1 = "";
					
					//Remove current tabs
					tabPane.removeAll();
					
					//Read files and create new panes
					for(int i = 0; i < result.size(); i += 1)
					{
						fi = new File(result.get(i));
						fr = new FileReader(fi);
						br = new BufferedReader(fr);
						newPane = new JTextPane();
						newPane.getStyledDocument().addDocumentListener(new MyDocumentListener());
						while ((s1 = br.readLine()) != null)
						{
							appendToPane(newPane, s1 + "\n", Color.BLACK);
						}
						tabPane.addTab(fi.getName(), null, newPane, null);
					}
					checkKeywords();
				}
				catch (IOException evt) {
					//If no readable Java files are in this directory
					JOptionPane.showMessageDialog(null, "There was an error loading a file.");
					evt.printStackTrace();
				}
				catch (NullPointerException evt) {
					//If no readable Java files are in this directory
					JOptionPane.showMessageDialog(null, "There are no accessible java files here.");
					evt.printStackTrace();
				}
			}
			else {
				System.out.println("No Selection ");
			}
		} 
		else if (s.equals("Save"))
		{
			// Create an object of JFileChooser class 
			JFileChooser j = new JFileChooser("f:");

			// Invoke the showsSaveDialog function to show the save dialog 
			int r = j.showSaveDialog(null);

			if (r == JFileChooser.APPROVE_OPTION) {

				// Set the label to the path of the selected directory 
				File fi = new File(j.getSelectedFile().getAbsolutePath());

				try {
					// Create a file writer 
					FileWriter wr = new FileWriter(fi, false);

					// Create buffered writer to write 
					BufferedWriter w = new BufferedWriter(wr);

					// Write 
					//w.write(code.getText());

					w.flush();
					w.close();
				} catch (Exception evt) {
					JOptionPane.showMessageDialog(mainPage, evt.getMessage());
				}
			}
			// If the user cancelled the operation 
			else
				JOptionPane.showMessageDialog(mainPage, "the user cancelled the operation");
		}
		else if (s.equals("Close")) 
		{
			tabPane.removeAll();
		}
		else if (s.equals("Compile")) 
		{
			JOptionPane.showMessageDialog(null, "Compiling code...");
			Runtime runTime = Runtime.getRuntime();
			try 
			{
				Process process = runTime.exec("javac .\\test.cpp");
				InputStream inputStream = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				InputStream errorStream = process.getErrorStream();
				InputStreamReader esr = new InputStreamReader(errorStream);

				int n1;
				char[] c1 = new char[1024];
				StringBuffer standardOutput = new StringBuffer();
				while ((n1 = isr.read(c1)) > 0) {
					standardOutput.append(c1, 0, n1);
				}
				JOptionPane.showMessageDialog(null, "Standard Output\n\n" + standardOutput.toString());

				int n2;
				char[] c2 = new char[1024];
				StringBuffer standardError = new StringBuffer();
				while ((n2 = esr.read(c2)) > 0) {
					standardError.append(c2, 0, n2);
				}
				JOptionPane.showMessageDialog(null, "Standard Error\n\n" + standardError.toString());
				process.destroy();
			} 
			catch (IOException err) 
			{
				err.printStackTrace();
			}
		}
		else if (s.equals("Run")) 
		{
			JOptionPane.showMessageDialog(null, "Running...");
		}
    }
	
	static class MyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			checkKeywords();
		}
		public void removeUpdate(DocumentEvent e) {
			checkKeywords();
		}
		public void changedUpdate(DocumentEvent e) {
				
		}
	}
	
	public static void checkKeywords()
	{
		try
		{
			highlightSyntax("if", Color.BLUE);
			highlightSyntax("else", Color.BLUE);
			highlightSyntax("for", Color.BLUE);
			highlightSyntax("while", Color.BLUE);
			highlightSyntax("+", Color.RED);
			highlightSyntax("-", Color.RED);
			highlightSyntax("/", Color.RED);
			highlightSyntax("\\", Color.RED);
			highlightSyntax("*", Color.RED);
			highlightSyntax("||", Color.RED);
			highlightSyntax("&&", Color.RED);
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog(null, err.getMessage());
		}
	}
	
	public static void highlightSyntax(String pattern, Color color)
	{
		Runnable doHighlight = new Runnable() {
			@Override
			public void run() {
				if(tabPane.getTabCount() == 0)
				{
					return;
				}
				try
				{
					MutableAttributeSet attributes = new SimpleAttributeSet();
					StyleConstants.setForeground(attributes, color);
					
					StyledDocument doc = ((JTextPane)tabPane.getSelectedComponent()).getStyledDocument();
					String text = doc.getText(0, doc.getLength());
					
					
					int pos = 0;
					while ((pos = text.indexOf(pattern, pos)) >= 0) {
						doc.setCharacterAttributes(pos, pattern.length(), attributes, true);
						pos += pattern.length();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};       
		SwingUtilities.invokeLater(doHighlight);
	}
	
    public static void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getStyledDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
	
    public static void initialize()
    {
        //Default File
		JTextPane code; //text area
        code = new JTextPane();
        code.getStyledDocument().addDocumentListener(new MyDocumentListener());
		tabPane.addTab("Tab 1", null, code, null);
    }
	
    public static void addSourceFile() {
        //Default File
		String name = JOptionPane.showInputDialog(null, "Enter the name of the source file to add.");
		
		JTextPane code; //text area
        code = new JTextPane();
        code.getStyledDocument().addDocumentListener(new MyDocumentListener());
		tabPane.addTab(name, null, code, null);
    }
}