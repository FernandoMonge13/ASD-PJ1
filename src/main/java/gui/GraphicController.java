package gui;

import Code.Card;
import Code.CardGetter;
import Code.List.Circular.CardActions;
import Code.List.CircularLinked.Circular;
import Code.List.Stack.Stack;
import Code.List.Stack.RandomCards;
import Code.Player;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Observable;
import javax.swing.*;


/**
 * This class makes easier the control of the graphic
 * interface.
 * The Main use only this class directly to make any
 * change in the graphic interface.
 *
 * @version 1.1
 */
public class GraphicController extends Observable implements ActionListener {

    boolean flags[] = {true, true , true, true, false, false, false, false, false, false};


    private final static Logger logger = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
    JButton btn_invitado = new JButton("Enter as guest");
    JButton btn_anfitrion = new JButton("Enter as host");
    JButton button1;
    JButton button2;
    JButton button3;
    JButton button4;
    JButton button5;
    JButton button6;
    JButton button7;
    JButton button8;
    JButton button9;
    JButton button10;
    JButton deck;
    JButton skipTurn;
    JButton [] handgame = {this.button1, this.button2, this.button3, this.button4, this.button5,
            this.button6, this.button7, this.button8, this.button9, this.button10, this.deck};
    MyFrame frame;

    RandomCards randomHand = new RandomCards();
    Stack newNode = new Stack();
    Circular circular = new Circular();

    private int position;
    private int fullHand = 4;

    CardActions controller = new CardActions(flags);

    /**
     * This constructor calls the method to create a frame.
     */
    public GraphicController(){

        createFrame();
        createGameButtons();
        controller.setFlagList(flags);

    }

    public boolean[] getFlags() {
        return flags;
    }

    public void createGameButtons() {

        skipTurn = new JButton("skip turn");
        skipTurn.setBounds(1100, 420, 90, 25);
        skipTurn.addActionListener(this);

        for(int i = 0; i < 16; ++i){
            newNode.insertNode(randomHand.RandomDeck());
        }

        int handImages [] = randomHand.RandomHand();

        int posx =20;

        for (int i = 0; i < 11; i++) {
            if (i < 4 ){
                ImageIcon buttonImage = new ImageIcon("src/main/java/gui/img/cards/"+ handImages[i] +".png");
                handgame[i] = new JButton();
                handgame[i].setBounds(posx, 480, 180, 250);
                handgame[i].setIcon(new ImageIcon(buttonImage.getImage().getScaledInstance(handgame[i].getWidth(), handgame[i].getHeight(), Image.SCALE_SMOOTH)));
                handgame[i].addActionListener(this);
                handgame[i].setEnabled(true);
                circular.insert(String.valueOf(handImages[i]));

            }else if(i < 10){

                ImageIcon buttonImage = new ImageIcon("src/main/java/gui/img/cards/"+ 50 +".png");
                handgame[i] = new JButton();
                handgame[i].setBounds(posx, 480, 180, 250);
                handgame[i].setIcon(new ImageIcon(buttonImage.getImage().getScaledInstance(handgame[i].getWidth(), handgame[i].getHeight(), Image.SCALE_SMOOTH)));
                handgame[i].addActionListener(this);
                handgame[i].setEnabled(false);
                circular.insert("");

            }
            else{
                ImageIcon buttonImage = new ImageIcon("src/main/java/gui/img/cards/"+(i + 1) +".png");
                handgame[i] = new JButton();
                handgame[i].setBounds(1050, 130, 180, 250);
                handgame[i].setIcon(new ImageIcon(buttonImage.getImage().getScaledInstance(handgame[i].getWidth(), handgame[i].getHeight(), Image.SCALE_SMOOTH)));
                handgame[i].addActionListener(this);
                handgame[i].setEnabled(true);

            }

            posx += 120;
        }
        //circular.showCircular();

    }

    /**
     * Creates a frame that initializes with the canvas of the
     * menu and add listeners to the menu buttons.
     */
    public void createFrame(){

        this.frame = new MyFrame();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.goMenu(btn_anfitrion, btn_invitado);
        btn_anfitrion.addActionListener(this);
        btn_invitado.addActionListener(this);
    }


    /**
     * This ActionListener is activated when a menu button is pressed,
     * then chance to the game Canvas and notify the button pressed
     * to the observer of the Main.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {


        Object selection = e.getSource();


        if(selection == btn_anfitrion){

            this.frame.goGame(handgame, skipTurn);

            this.setChanged();
            this.notifyObservers("host");
            this.clearChanged();
        }

        else if (selection == btn_invitado){

            this.frame.goGame(handgame, skipTurn);
            this.setChanged();
            this.notifyObservers("guest");
            this.clearChanged();
        }

        else if (selection == skipTurn){
            Card card = CardGetter.getCard("40");
            this.setChanged();
            this.notifyObservers(card);
            this.clearChanged();
        }

        else if (selection == handgame[10]){


            if (fullHand < 10 && newNode.stackSize() > 0){

                int ID = newNode.delete();
                //this.setChanged();
                //this.notifyObservers("deck");
                //this.clearChanged();
                controller.setFlagList(flags);
                position = controller.SiteAnalysis();

                ImageIcon buttonImage = new ImageIcon("src/main/java/gui/img/cards/"+ ID +".png");
                handgame[position].setIcon(new ImageIcon(buttonImage.getImage().
                        getScaledInstance(handgame[position].getWidth(), handgame[position].getHeight(), Image.SCALE_SMOOTH)));

                handgame[position].setEnabled(true);
                controller.getFlagList();
                ++fullHand;

                circular.modify(position, String.valueOf(ID));


            }else{
                logger.log(Level.WARNING, "The hand game is full of the stack is out");
            }
        }

        else {

            //Searches for the button pressed
            for(int index = 0; index <= 9; ++index){

                if (selection == handgame[index]){

                    String circularID = circular.getID(index);
                    Card card = CardGetter.getCard(circularID);

                    //Check that the player has enough mana for the summon
                    if (card.manaCost <= Player.getMana() || Player.getMaxPower()) {

                        if (Player.getCounterMP() < 3){
                            card.frozenTurn = Player.getMaxPower();
                        }
                        handgame[index].setEnabled(false);
                        flags[index] = false;
                        fullHand -= 1;
                        this.frame.updateSummonedCard(circularID);
                        this.setChanged();
                        this.notifyObservers(card);
                        this.clearChanged();
                    }

                    else{
                        logger.log(Level.WARNING, "You don´t have enough mana for this summon");
                    }
                    break;
                }
            }
        }
    }

    public void updateStats(String life, String mana){
        this.frame.updateStats(life, mana);
    }


    public void closeProgram(){
        this.frame.closePorgram();
    }
}