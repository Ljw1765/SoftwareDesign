import javax.swing.*;

public class MainFrame {
    private JPanel panel1;
    private JMenuBar myMenuBar;
    private JMenu fileMenu;
    private JMenu projectMenu;
    private JMenu compileMenu;
    private JMenu executeMenu;
    private JTextField keywordsTextField;
    private JMenuItem myOpen;
    private JMenuItem myCreate;
    private JMenuItem myClose;
    private JMenuItem mySave;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainFrame");
        frame.setContentPane(new MainFrame().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
