package lost.macpan.game;

import lost.macpan.game.sprites.CoinSprite;
import lost.macpan.game.sprites.EnemySprite;
import lost.macpan.game.sprites.ExitSprite;
import lost.macpan.game.sprites.PlayerSprite;
import lost.macpan.game.sprites.Sprite;
import lost.macpan.game.Enemy;
import lost.macpan.panel.LooserMenu;
import lost.macpan.panel.MainMenu;
import lost.macpan.panel.OptionsMenu;
import lost.macpan.panel.WinnerMenu;
import lost.macpan.utils.ResourceHandler;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for displaying gameplay
 * @author Leon Wigro
 * @version 0.1.1
 */
public class GameWindow extends JPanel implements Runnable, ResourceHandler, KeyListener, ActionListener {
    //attributes
    Timer timer;
    private  int[] playerPos;
    char lastKey;

    private int originalTileSize = 16;              //corresponds to the sprite size
    private int scale = 2;                          //the scale to be used for rendering of sprites (e.g. a (16px)² sprite with scale 2 will be drawn as (32px)²
    public int tileSize = originalTileSize * scale; //tile size and effective sprite size
    protected int maxColumns = 32;                  //maximum amount of tiles that can be drawn horizontally
    private int maxRows = 24;                       //maximum amount of tiles that can be drawn vertically
    private int width = maxColumns * tileSize;      //width of the window (automatically adjusted based on tileSize and maxColumns)
    private int height = maxRows * tileSize;        //height of the window (automatically adjusted based on tileSize and maxRows)
    private int framerate = 60;                     //rate of draw loop repetitions
    public char[][] map;                            //char-array from which a frame will be drawn
    public int score;                               //for keeping track of the score
    private int hudHeight = 21;                     //determines the height of the HUD
    private List<Enemy> enemies = new ArrayList<Enemy>();
    private Thread thread;
    public JFrame parentFrame;

    public BufferedImage path;
    public BufferedImage wall;

    PlayerSprite playerSprite = new PlayerSprite(this);     //handles drawing the player sprite
    EnemySprite enemySprite = new EnemySprite(this);        //handles drawing enemy sprites
    ExitSprite exitSprite = new ExitSprite(this);           //handles drawing the exit sprite
    CoinSprite coinSprite = new CoinSprite(this);           //handles drawing coin sprites
    Sprite sprite = new Sprite(this);                       //handles drawing miscellaneous sprites
    HUD hud = new HUD(this);                                //handles drawing the in-game HUD

    /**
     * Flag array is built as follows: <br>
     * [0] = player____[true]>alive____[false]>dead <br>
     * [1] = armor (extra life)____[true]>collected____[false]>not collected <br>
     * [2] = speed boost____[true]>active____[false]>inactive <br>
     * [3] = key____[true]>collected____[false]>not collected <br>
     * [4] = pan (death touch)____[true]>active____[false]>inactive <br>
     * [5] = coin booster____[true]>active____[false]>inactive <br>
     * [6] = exit unlock____[true]>locked____[false]>unlocked <br>
     * [7] = enemy freeze____[true]>active____[false]>inactive   <br>
     */
    public boolean flags[];

    /**
     * method for fetching sprites from  the "images" folder and assigning them to the corresponding BufferedImage sprite
     */
    public void fetchSprites() {
        try {
            path = ImageIO.read(getFileResourcesAsStream("images/Path-1.png.png"));
            wall = ImageIO.read(getFileResourcesAsStream("images/Wall.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public GameWindow(JFrame frame){
        parentFrame = frame;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        flags = new boolean[8];
        map = importMapArray("test.txt"); //import the map test
    }

    /**
     * Thread starter
     */
    public void start(){
        thread = new Thread(this);
        thread.start();

        this.addKeyListener(this);
        this.setFocusable(true);
        this.grabFocus();

        getPlayerPos();

        /* ENEMY INITIATION */
        for (int i = 0; i < maxColumns; i++) {
            for (int j = 0; j < maxRows; j++) {
                if(map[i][j] == 'g') {
                    enemies.add(new Enemy(i, j,this));
                }
            }
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameLogic();
            }
        },0,500);

    }


    /**
     * draw loop
     */
    @Override
    public void run() {
        fetchSprites();                                         //assigns sprites
        double frametime = 1000000000 / framerate;              //determines the time span any frame should be displayed
        double nextDrawTime = System.nanoTime() + frametime;    //determines at which point in time the next frame should start to be drawn
        while(thread != null){                                  //start of the draw loop
            //gameLogic();                                        //TO BE REPLACED see above
            repaint();                                          //draws the frame
            try {
                double remainingTime = (nextDrawTime - System.nanoTime()) / 1000000;    //determines for how long the current frame should continue to be displayed
                /*
                System.out.println("Maximum possible framerate (only up to " +
                        framerate + " displayed): " +
                        1000000000 / (framerate - remainingTime));      //TO BE REMOVED returns maximum possible frame rate going by current frame time
                 */
                if (remainingTime < 0)                  //determines how long the thread should sleep for
                    remainingTime = 0;                  //with negative or 0 remaining time the thread should sleep for 0ns
                thread.sleep((long) remainingTime);     //puts thread to sleep for the allotted time
                nextDrawTime += frametime;              //determines when the next frame should finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * generic method TO BE REPLACED
     */
    public void gameLogic(){
        //score = 42069;
        /*for (int i = 1; i < 8; i++) {
            flags[i] = true;
        }*/

        /* ENEMY LOGIC */
        for(int i = 0; i < enemies.size(); i++) {
            //enemies.get(i).move();
            System.out.println(
                    "Enemy " + (i+1) +
                    " {\n\tFacing:\t\t" + enemies.get(i).getFacingDirection() +
                    "\n\tUnter mir:\t'" + enemies.get(i).getAbove() +
                    "'\n\tVor mir:\t'" + enemies.get(i).detect("front") +
                    "'\n\tPassable:\t" + enemies.get(i).isPassable(enemies.get(i).detect("front")) + "\n}"
            );
            enemies.get(i).move();
        }

        move(lastKey);
        lastKey = 'o';
    }

    /**
     * method for drawing a frame <br>
     * can currently only draw sprites, no integration for any overly yet
     * @param g not to be edited
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < maxColumns; i++){       //parses the x-coordinate of "map"
            for (int j = 0; j < maxRows; j++){      //parses the y-coordinate of "map"
                char c = map[i][j];                 //fetches currently examined tile identifier
                if (c == 'p')
                    playerSprite.draw(g2, i, j);    //handles player sprite
                else if (c == 'g')
                    enemySprite.draw(g2, i, j);     //handles enemy sprite
                else if (c == 'x')
                    exitSprite.draw(g2, i, j);      //handles exit sprite
                else if (c == '*')
                    coinSprite.draw(g2, i, j);      //handles coin sprite
                else
                    sprite.draw(g2, i, j, c);       //handles static sprites
            }
        }
        hud.draw(g2, hudHeight);
        g2.dispose();
    }

    /**
     * method for importing a map as a char array
     * @author Sebastian
     *
     * @param pFileName name of the map to load (has to be in the levels folder)
     * @return charArray of the map at the filename
     */
    private char[][] importMapArray(String pFileName){

        char[][] map = new char[maxColumns][maxRows];
        String mapString = "";

        try {
            InputStream inputStream = getFileResourcesAsStream("levels/"+pFileName);
            mapString = convertStreamToString(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] rows = mapString.split("\n"); //Split String into String Array consisting of single Rows

        for(int i = 0; i < rows.length;i++ ) {           //For every row
            for (int o = 0; o < rows[i].length(); o++) {  //for every char in the row
                map[o][i] = rows[i].charAt(o);            //insert char into the map array
            }
        }
        return map;
    }

    public void getPlayerPos(){
        playerPos = new int [2];

        for (int i = 0; i < maxColumns; i++){
            for (int j = 0; j < maxRows; j++) {
                if(map[i][j] == 'p'){
                    playerPos[0] = i;
                    playerPos[1] = j;
                    System.out.println(i + "und" + j);
                    return;
                }
            }
        }
    }

    public void move(char key){ //momentan mit globaler variable
        if(key == 'w') {
            moveToNew(0,-1);
        } else if(key == 's'){
            moveToNew(0,1);
        } else if(key == 'a'){
            moveToNew(-1,0);
        } else if(key == 'd'){
            moveToNew(1,0);
        }

    }

    public void moveToNew(int x, int y) {
        char onNewPos = map[playerPos[0]+x][playerPos[1]+y];
        if(onNewPos == 'h') { // Momentan
            System.out.println("Wand im weg");
        }
        else{
            if(onNewPos == '*'){
                if(flags[5] == true){
                    score += 20;
                }else{
                    score +=10;
                }

            }
            else if(onNewPos == 'g'){
                if(flags[1]){
                    flags[1] = false;
                }
                else {
                    flags[0] = false;
                    thread.stop();

                    LooserMenu looserMenu = new LooserMenu(parentFrame);
                    parentFrame.setContentPane(looserMenu);
                    parentFrame.revalidate();
                    //Direkt mit Todes Bildschirm
                }
            }
            else if(onNewPos == 'k'){
                flags[3] = true;
            }else if(onNewPos == 'x') {
                if(!flags[3] == true){
                    return;
                }else{
                    thread.stop();

                    WinnerMenu winnerMenu = new WinnerMenu(parentFrame);
                    parentFrame.setContentPane(winnerMenu);
                    parentFrame.revalidate();
                    //Bildschirm (Todes oder Erfolgs)
                }
            }
            else if(onNewPos == 'a') {//Geschwindigkeitsbuff
                flags[2] = true;
            } else if(onNewPos == 'b') {//Gegner einfrieren
                flags[7] = true;
            } else if(onNewPos == 'c'){//Münzboost
                flags[5] = true;
            } else if(onNewPos == 'd'){//Zusatzleben
                flags[1] = true;
            }else if(onNewPos == 'e'){//Todesberührung
                flags[4] = true;
            }
            geh(x,y);
        }
    }

    public void geh(int x, int y){
        map[playerPos[0]][playerPos[1]] = '.';
        playerPos[0] += x;
        playerPos[1] += y;
        map[playerPos[0]][playerPos[1]] = 'p';
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_ESCAPE)
        {
            System.out.println("Geht");
        }
        lastKey = e.getKeyChar();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void actionPerformed(ActionEvent e){

    }
}
