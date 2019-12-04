package Wolverines;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.event.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.stream.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class CodeEditor extends JFrame implements ActionListener
{
    
    //static JSyntaxPane codeNew; //text area
    static JFrame mainPage; //main page of the code editor
	static JTabbedPane tabPane; //Tabs
	static File projectDir;
	static boolean filesEdited = false;
	
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
		mainPage.getContentPane().setBackground(Color.DARK_GRAY);
        mainPage.setSize(500, 500);
        mainPage.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
		
		if (s.equals("New")) 
		{
			//Checkpoint! Make sure there aren't any unsaved changes before proceeding.
			if(!checkpoint())
				return;
			
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
						JOptionPane.showMessageDialog(null, "It seems there's already a Java project here. If you want to include these files, use the Open command instead.\n\nIf you don't you may risk overwriting parts of the existing project.");
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
			//Checkpoint! Make sure there aren't any unsaved changes before proceeding.
			if(!checkpoint())
				return;
			
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
					JPanel newPanel;
					JScrollPane scrollpane;
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
						newPanel = new JPanel(new BorderLayout());
						newPanel.add(newPane);
						newPane.getStyledDocument().addDocumentListener(new MyDocumentListener());
						scrollpane = new JScrollPane(newPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
						while ((s1 = br.readLine()) != null)
						{
							appendToPane(newPane, s1 + "\n", Color.BLACK);
						}
						tabPane.addTab(fi.getName(), null, scrollpane, null);
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
			saveEverything();
		}
		else if (s.equals("Close")) 
		{
			//Checkpoint! Make sure there aren't any unsaved changes before proceeding.
			if(!checkpoint())
				return;
			
			tabPane.removeAll();
			projectDir = null;
			filesEdited = false;
		}
		else if (s.equals("Add a File")) 
		{
			//Don't do this if a project directory hasn't been set yet!
			if(projectDir == null)
			{
				JOptionPane.showMessageDialog(mainPage, "Open a project directory first.");
			}
			else
			{
				addSourceFile();				
			}
		}
		else if (s.equals("Remove This File")) 
		{
			//Don't do this if a project directory hasn't been set yet!
			if(projectDir == null)
			{
				JOptionPane.showMessageDialog(mainPage, "Open a project directory first.");
			}
			else if(tabPane.getTabCount() == 0) //Also don't do this is there's nothing to delete
			{
				JOptionPane.showMessageDialog(mainPage, "There are no source files to delete!");
			}
			else
			{
				removeSourceFile();				
			}
		}
		else if (s.equals("Compile")) 
		{
			//Checkpoint! Make sure there aren't any unsaved changes before proceeding.
			if(!checkpoint())
				return;
			
			//Don't do this if a project directory hasn't been set yet!
			if(projectDir == null)
			{
				JOptionPane.showMessageDialog(mainPage, "Open a project directory first.");
			}
			else
			{
				Runtime runTime = Runtime.getRuntime();
				try 
				{
					Process process = runTime.exec("javac " + projectDir + "\\" + "*.java");
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
					if(standardOutput.toString().length() > 0)
						JOptionPane.showMessageDialog(null, "Standard Output\n\n" + standardOutput.toString());

					int n2;
					char[] c2 = new char[1024];
					StringBuffer standardError = new StringBuffer();
					while ((n2 = esr.read(c2)) > 0) {
						standardError.append(c2, 0, n2);
					}
					if(standardError.toString().length() > 0)
						JOptionPane.showMessageDialog(null, "Standard Error\n\n" + standardError.toString());
					
					process.destroy();
				} 
				catch (IOException err) 
				{
					JOptionPane.showMessageDialog(null, "There seems to be a problem opening javac. Make sure it's installed and can be called from the command line.");
				}
			}
		}
		else if (s.equals("Run")) 
		{
			//Don't do this if a project directory hasn't been set yet!
			if(projectDir == null)
			{
				JOptionPane.showMessageDialog(mainPage, "Open a project directory first.");
			}
			else
			{
				Runtime runTime = Runtime.getRuntime();
				try 
				{
					Process process = runTime.exec("java -cp " + projectDir + "\\" + " Main");
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
					if(standardOutput.toString().length() > 0)
						JOptionPane.showMessageDialog(null, "Standard Output\n\n" + standardOutput.toString());

					int n2;
					char[] c2 = new char[1024];
					StringBuffer standardError = new StringBuffer();
					while ((n2 = esr.read(c2)) > 0) {
						standardError.append(c2, 0, n2);
					}
					if(standardError.toString().length() > 0)
						JOptionPane.showMessageDialog(null, "Standard Error\n\n" + standardError.toString());
					
					process.destroy();
				} 
				catch (IOException err) 
				{
					JOptionPane.showMessageDialog(null, "There was a problem with the java executable. Make sure that Java is installed and can be called from the command line.");
				}
			}
		}
    }
	
	public static boolean checkpoint()
	{
		if(filesEdited)
		{
			int input = JOptionPane.showConfirmDialog(null, "There are unsaved changes. Would you like to save first?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			
			switch(input)
			{
				case 0: //Yes
					saveEverything();
					return true;
				case 1: //No
					return true;
				case 2: //Cancel
					return false;
			}
		}
		else
		{
			//Green light
			return true;
		}
		
		//Shouldn't get here, but eh.
		return false;
	}
	
	public static void saveEverything()
	{
		//Return if there's nothing to save.
		if(tabPane.getTabCount() == 0)
			return;
		
		//Make needed objects
		File fi;
		FileWriter wr;
		BufferedWriter w;
		
		//Save all of the files
		for(int i = 0; i < tabPane.getTabCount(); i += 1)
		{
			fi = new File(projectDir + "\\" + tabPane.getTitleAt(i));
			
			try {
				// Create a file writer 
				wr = new FileWriter(fi, false);

				// Create buffered writer to write 
				w = new BufferedWriter(wr);

				// Write 
				w.write(((JTextPane)(((JPanel)((JScrollPane)tabPane.getComponentAt(i)).getViewport().getView())).getComponent(0)).getText());

				w.flush();
				w.close();
			} catch (Exception evt) {
				JOptionPane.showMessageDialog(mainPage, evt.getMessage());
			}
		}
		
		filesEdited = false;
	}
	
	static class MyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			checkKeywords();
			filesEdited = true;
		}
		public void removeUpdate(DocumentEvent e) {
			checkKeywords();
			filesEdited = true;
		}
		public void changedUpdate(DocumentEvent e) {
			filesEdited = true;
		}
	}
	
	public static void checkKeywords()
	{
		if(tabPane.getTabCount() == 0)
		{
			return;
		}
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
			highlightStrings(Color.GREEN);
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog(null, err.getMessage());
		}
	}
	
	public static void resetColor(String pattern, Color color)
	{
		Runnable doResetHighlight = new Runnable() {
			@Override
			public void run() {
				if(tabPane.getTabCount() == 0)
				{
					return;
				}
				try
				{
					MutableAttributeSet attributes = new SimpleAttributeSet();
					StyleConstants.setForeground(attributes, Color.BLACK);
					
					StyledDocument doc = ((JTextPane)(((JPanel)((JScrollPane)tabPane.getSelectedComponent()).getViewport().getView())).getComponent(0)).getStyledDocument();
					String text = doc.getText(0, doc.getLength());
					
					doc.setCharacterAttributes(0, doc.getLength(), attributes, true);
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};       
		SwingUtilities.invokeLater(doResetHighlight);
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
					
					StyledDocument doc = ((JTextPane)(((JPanel)((JScrollPane)tabPane.getSelectedComponent()).getViewport().getView())).getComponent(0)).getStyledDocument();
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
	
    public static void highlightStrings(Color color)
	{
		Runnable doStringHighlight = new Runnable() {
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
					
					StyledDocument doc = ((JTextPane)(((JPanel)((JScrollPane)tabPane.getSelectedComponent()).getViewport().getView())).getComponent(0)).getStyledDocument();
					String text = doc.getText(0, doc.getLength());
					
					Pattern pattern = Pattern.compile("(\".*\")");
					Matcher matcher = pattern.matcher(text);
					
					int occurances = 0;
					// Check all occurrences
					while (matcher.find()) {
						occurances += 1;
							
						if(occurances % 2 == 1)
						{
							doc.setCharacterAttributes(matcher.start(), matcher.group(1).length(), attributes, true);
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};       
		SwingUtilities.invokeLater(doStringHighlight);
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
		String name = "";
		boolean set = false;
		
		while(!set)
		{
			name = "";
			name = JOptionPane.showInputDialog(null, "Enter the name of the source file to add.");
			
			if(name == null || name.equals(""))
			{
				return;
			}
			
			if(!name.endsWith(".java"))
			{
				name += ".java";
			}
			
			if(tabPane.indexOfTab(name) >= 0)
			{
				JOptionPane.showMessageDialog(null, "A Source file with that name already exists. Choose a different name.");
			}
			else
			{
				set = true;
				filesEdited = true;
			}
		}
		
		JTextPane code; //text area
		JPanel panel = new JPanel(new BorderLayout());
        code = new JTextPane();
		panel.add(code);
		JScrollPane scrollpane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //Scrollbar
        code.getStyledDocument().addDocumentListener(new MyDocumentListener());
		tabPane.addTab(name, null, scrollpane, null);
    }
	
    public static void removeSourceFile() {
        int input = JOptionPane.showConfirmDialog(null, "This is a destructive operation. Are you sure you want to delete this source file?", "Confirm deletion of source file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if(input == 0)
		{
			String name = tabPane.getTitleAt(tabPane.getSelectedIndex());
			tabPane.removeTabAt(tabPane.getSelectedIndex());
			
			File file = new File(projectDir + "\\" + name); 
          
			if(file.delete()) 
			{ 
				//Success
			} 
			else
			{ 
				JOptionPane.showMessageDialog(null, "Couldn't delete the file. How interesting!");
			} 
		}
    }
}