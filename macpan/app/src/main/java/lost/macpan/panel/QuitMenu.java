package lost.macpan.panel;

import lost.macpan.utils.ResourceHandler;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Die Klasse QuitMenu erstellt das Bestätigungsfenster, wenn der Benutzer das Spiel beenden möchte
 *
 * @author Fatih
 */
public class QuitMenu extends JPanel implements ActionListener, ResourceHandler {
    // Erstellen der Buttons Ja und Nein für die Eingabe
    private final JButton yesBtn = new JButton("Ja");
    private final JButton noBtn = new JButton("Nein");

    private JFrame parentFrame;
    private JLabel topLabel, subLabel;
    private Image img_1, img_2;
    private JLabel background;
    private Image backgroundImg;

    public QuitMenu(JFrame frame) {
        parentFrame = frame;
        // Laden der Bilder, falls diese vorhanden sind
        try {
            img_1 = ImageIO.read(getFileResourcesAsStream("images/panelImages/Quit.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            img_2 = ImageIO.read(getFileResourcesAsStream("images/panelImages/Quit_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            backgroundImg = ImageIO.read(getFileResourcesAsStream("images/panelImages/BackgroundImage.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (backgroundImg != null) {
            background = new JLabel(new ImageIcon(backgroundImg));
        }
        // Bilder als ImageIcon
        if (img_1 != null) {
            topLabel = new JLabel(new ImageIcon(img_1));
        }
        if (img_2 != null) {
            subLabel = new JLabel(new ImageIcon(img_2));
        }
        setLayout(null);
        // Positionierung der Labels und Buttons
        background.setBounds(0, 0, 950, 700);
        topLabel.setBounds(-20, 10, 950, 200);
        subLabel.setBounds(175, 250, 600, 200);
        yesBtn.setBounds(740, 550, 160, 50);
        noBtn.setBounds(30, 550, 160, 50);
        add(subLabel);
        add(topLabel);
        add(yesBtn);
        add(noBtn);
        add(background);
        yesBtn.addActionListener(this);
        noBtn.addActionListener(this);
    }

    // Funktionalität der Buttons festlegen
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == yesBtn) {
            // Fenster wird geschlossen und das Spiel ist beendet
            parentFrame.dispose();
        } else if (e.getSource() == noBtn) {
            //Hauptmenü wird geladen
            MainMenu mainMenu = new MainMenu(parentFrame);
            parentFrame.setContentPane(mainMenu);
            parentFrame.revalidate();
        }
    }
}
