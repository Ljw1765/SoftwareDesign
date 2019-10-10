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

class CodeEditor extends JFrame implements ActionListener
{
    static JTextPane code; //text area
    JFrame mainPage; //main page of the code editor

    CodeEditor()
    {
        mainPage = new JFrame("Team Wolverines"); //title of the window
		mainPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        code = new JTextPane();
		code.getStyledDocument().addDocumentListener(new MyDocumentListener());

        JMenuBar MainMenuBar = new JMenuBar(); //menu bar of main page

        //file menu
        JMenu fileMenu = new JMenu("File");
        //items for file Menu
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem closeFile = new JMenuItem("Close");
        //actions for each item
        newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        closeFile.addActionListener(this);
        //add actions to the file menu
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(closeFile);

        //project menu
        JMenu projectMenu = new JMenu("Project");
        //items for project menu
        JMenuItem newProject = new JMenuItem("New");
        JMenuItem openProject = new JMenuItem("Open");
        JMenuItem saveProject = new JMenuItem("Save");
        JMenuItem closeProject = new JMenuItem("Close");
        //actions for each item
        newProject.addActionListener(this);
        openProject.addActionListener(this);
        saveProject.addActionListener(this);
        closeProject.addActionListener(this);
        //add actions to the project menu
        projectMenu.add(newProject);
        projectMenu.add(openProject);
        projectMenu.add(saveProject);
        projectMenu.add(closeProject);

        //project menu
        JMenu executeMenu = new JMenu("Execute");
        //items for project menu
        JMenuItem compileProject = new JMenuItem("Compile");
        JMenuItem runProject = new JMenuItem("Run");
        //actions for each item
        compileProject.addActionListener(this);
        runProject.addActionListener(this);
        //add actions to the project menu
        executeMenu.add(compileProject);
        executeMenu.add(runProject);

        //adding the menus to the main menu bar
        MainMenuBar.add(projectMenu);
        MainMenuBar.add(fileMenu);
        MainMenuBar.add(executeMenu);

        mainPage.setJMenuBar(MainMenuBar);
        mainPage.add(code);
        mainPage.setSize(500, 500);
        mainPage.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

//      if (s.equals("cut"))
//          code.cut();
//      else if (s.equals("copy"))
//          code.copy();
//      else if (s.equals("paste"))
//          code.paste();
//      else
            
		if (s.equals("Save"))
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
					w.write(code.getText());

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
//      else if (s.equals("Print")) 
//		{
//        	try 
//			{
//             	// print the file
//             	code.print();
//         	} 
//			catch (Exception evt) 
//			{
//             	JOptionPane.showMessageDialog(mainPage, evt.getMessage());
//         	}
//      }
		else if (s.equals("Open")) {
			// Create an object of JFileChooser class
			code.setText("");
			JFileChooser j = new JFileChooser("f:");

			// Invoke the showsOpenDialog function to show the save dialog 
			int r = j.showOpenDialog(null);

			// If the user selects a file 
			if (r == JFileChooser.APPROVE_OPTION) 
			{
				// Set the label to the path of the selected directory 
				File fi = new File(j.getSelectedFile().getAbsolutePath());

				try {
					// String 
					String s1 = "", sl = "";

					// File reader 
					FileReader fr = new FileReader(fi);

					// Buffered reader 
					BufferedReader br = new BufferedReader(fr);

					while ((s1 = br.readLine()) != null)
					{
						appendToPane(code, s1 + "\n", Color.BLACK);
					}
					checkKeywords();
				} catch (Exception evt) {
					JOptionPane.showMessageDialog(mainPage, evt.getMessage());
				}
			}
			// If the user cancelled the operation 
			else
				JOptionPane.showMessageDialog(mainPage, "the user cancelled the operation");
		} 
		else if (s.equals("New")) 
		{
			code.setText("");
		} 
		else if (s.equals("Close")) 
		{
			//mainPage.setVisible(false);
			code.setText("");
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
				try
				{
					MutableAttributeSet attributes = new SimpleAttributeSet();
					StyleConstants.setForeground(attributes, color);

					StyledDocument doc = code.getStyledDocument();
					String text = doc.getText(0, doc.getLength());
					
					
					int pos = 0;
					while ((pos = text.indexOf(pattern, pos)) >= 0) {
						doc.setCharacterAttributes(pos, pattern.length(), attributes, true);
						pos += pattern.length();
					}
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage());
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
}