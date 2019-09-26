package Wolverines;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

class CodeEditor extends JFrame implements ActionListener
{
    JTextPane code; //text area
    JFrame mainPage; //main page of the code editor

    CodeEditor()
    {
        mainPage = new JFrame("Team Wolverines"); //title of the window
        code = new JTextPane();

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

        //adding the menus to the main menu bar
        MainMenuBar.add(projectMenu);
        MainMenuBar.add(fileMenu);

        mainPage.setJMenuBar(MainMenuBar);
        mainPage.add(code);
        mainPage.setSize(500, 500);
        mainPage.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

//        if (s.equals("cut"))
//            code.cut();
//        else if (s.equals("copy"))
//            code.copy();
//        else if (s.equals("paste"))
//            code.paste();
//        else
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
//            else if (s.equals("Print")) {
//            try {
//                // print the file
//                code.print();
//            } catch (Exception evt) {
//                JOptionPane.showMessageDialog(mainPage, evt.getMessage());
//            }
//        }
            else if (s.equals("Open")) {
            // Create an object of JFileChooser class
                code.setText("");
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsOpenDialog function to show the save dialog 
            int r = j.showOpenDialog(null);

            // If the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) {
                // Set the label to the path of the selected directory 
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // String 
                    String s1 = "", sl = "";

                    // File reader 
                    FileReader fr = new FileReader(fi);

                    // Buffered reader 
                    BufferedReader br = new BufferedReader(fr);

                    // Take the input from the file 
//                    while ((s1 = br.readLine()) != null)
//                    {
//                        sl = sl + "\n" + s1;
//                    }
                    // Set the text 
                    //code.setText(sl);

                    while ((s1 = br.readLine()) != null)
                    {
                        if (s1.contains("if"))
                            appendToPane(code, "if\n", Color.BLUE);
                        else if (s1.contains("else"))
                            appendToPane(code, "else\n", Color.BLUE);
                        else if (s1.contains("for"))
                            appendToPane(code, "for\n", Color.BLUE);
                        else if (s1.contains("while"))
                            appendToPane(code, "while\n", Color.BLUE);
                        else if (s1.contains("+"))
                            appendToPane(code, "+\n", Color.RED);
                        else if (s1.contains("-"))
                            appendToPane(code, "-\n", Color.RED);
                        else if (s1.contains("/"))
                            appendToPane(code, "/\n", Color.RED);
                        else if (s1.contains("*"))
                            appendToPane(code, "*\n", Color.RED);
                        else if (s1.contains("||"))
                            appendToPane(code, "||\n", Color.RED);
                        else if (s1.contains("&&"))
                            appendToPane(code, "&&\n", Color.RED);
                        else
                        {
                            appendToPane(code, s1 + "\n", Color.BLACK);
                        }
                    }
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(mainPage, evt.getMessage());
                }
            }
            // If the user cancelled the operation 
            else
                JOptionPane.showMessageDialog(mainPage, "the user cancelled the operation");
        } else if (s.equals("New")) {
            code.setText("");
        } else if (s.equals("Close")) {
            //mainPage.setVisible(false);
            code.setText("");
        }
    }

    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
}