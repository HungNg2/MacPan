/*
 * MacPan version 0.1
 * SWE WS 21/22
 * Authors:
 * Janosch Lentz
 * David Rechkemmer
 */

package lost.macpan;

import lost.macpan.panel.Intro;
import lost.macpan.panel.MainMenu;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * App Class that handles the application
 */
public class App extends JFrame implements ActionListener {
    private static final int width = 960;
    private static final int height = 700;
    private static final int serializeId = 123456789;
    private Timer timer;
    public Sound music = new Sound();
    public static final int beispiel = 0;
    /**
     * Constructor method for App Class
     * sets the JFrame attributes
     */
    public App(){
        int delay = 5000; //müssen testen um auf 4 Sekunden zu kommen jz ca. 2 Sek

        timer = new Timer(delay, this);
        setTitle("MacPan");
        setMinimumSize(new Dimension(width, height));
        getContentPane().setSize(width, height);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);        //sets fullscreen
        //setUndecorated(true);                           //removes window header
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new Intro());
        setResizable(false);
        pack();
        setVisible(true);
        playMusic(0, false);
        timer.start();
    }

    /**
     * handles the timeout to change the panels when the app starts
     * overriden method from ActionListener
     * @param evt don't need to be set
     */
    @Override
    public void actionPerformed ( ActionEvent evt) {
        MainMenu mM = new MainMenu(this);
        setContentPane(mM);
        revalidate();
        timer.stop();
    }

    public void playMusic(int track, boolean iterruptingOther) {
        music.setFile(track, iterruptingOther);
        music.play();
        music.loop();
    }

    public Sound getMusic() {
        return music;
    }
}
