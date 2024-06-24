import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class BlackJack {

    
   

    private class Card{

        String value;
        String type;

        Card(String value, String type) {

            this.value = value;

            this.type = type;


        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {  // Ace, Jack, Queen, or King
                if (value == "A") {
                    return 11;
                }
                
                return 10;
            }
            return Integer.parseInt(value); //2-10
        }

        public boolean isAce() {

            return value == "A";
        }


        public String getImagePath() {
            
            return "./cards/" + toString() + ".png";
        }
    }



    ArrayList<Card> deck;
    Random random = new Random();

    //dealer variables 
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player variables 
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    //card graphics
    int cardWidth = 110;     // 1/1.4 ratio 
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            
            try {
                //draw hidden card image
                Image hiddenCardImage = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if(!stayButton.isEnabled()){
                    hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);

                //draw dealer's hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);

                }

                //draw player's hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);

                }

                //display winner
                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("Stay: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21) {

                        message = "You Lose!";

                    }

                    else if (dealerSum > 21) {

                        message = "You Win!";

                    }

                    //both you and dealer <= 21
                    else if (playerSum == dealerSum) {
                        
                        message = "Tie!";
                    }

                    else if (playerSum > dealerSum) {

                        message = "You Win!";

                    }

                    else if (playerSum < dealerSum) {

                        message = "You Lose!";

                    }

                    //draw message to panel
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);

                    //<-----thinking about slipping something in right here the game to automatically make the restartButton enabled. idk if itll work tho...
                    restartButton.setEnabled(true);
                
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    //basic buttons from guide
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    //new buttons from me
    JButton restartButton = new JButton("Restart");






    BlackJack() {
        startGame();

        //frame or "board"
        frame.setVisible(true);
        frame.setSize(boardWidth, boardWidth);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //panel 
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);


        //buttons + adding them to button panel
        hitButton.setFocusable(false);        //<----- focusable makes them usable i think?
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        //my new buttons
        //restartButton.setFocusable(false);  //gonna toy with this. might make a menu and layer this sort of shit in so you dont misclick and restart a good game
        restartButton.setEnabled(false);
        buttonPanel.add(restartButton);


        //add button panel to the frame ez
        frame.add(buttonPanel, BorderLayout.SOUTH);

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                startGame();

                stayButton.setEnabled(true);
                hitButton.setEnabled(true);
                
                gamePanel.repaint();                            //this works basically how i want. might keep and use knowledge from this improvement to layer in a menu functionality 
                                                                //but for now (6/18/24 5:04 pm) im gonna try only letting it (restart button) be enabled after "the game"
                
            }
        });

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce()? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21) {            //A + 2 + J --> 1 + 2 + J
                    hitButton.setEnabled(false);
                }

                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce()? 1 : 0;
                    dealerHand.add(card);

                }

                gamePanel.repaint();
            }
        });



        gamePanel.repaint();




    }

    public void startGame(){
        //start game now returns the enabled status of the restartButton to its default 
        restartButton.setEnabled(false);



        //deck methods
        buildDeck();
        shuffleDeck();

        //dealer 
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1); //remove card from last index
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER: ");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);



        //player 
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("PLAYER: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);

    }

    //builds a deck in a way that acts as a physical deck would
    public ArrayList<Card> buildDeck() {

        deck = new ArrayList<Card>();

        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++ ) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);

            }
        }

        System.out.println("BUILD DECK:  ");

        System.out.println(deck);

        return deck; //returns the actual deck


    }

    public void shuffleDeck() {

        for (int i = 0; i < deck.size(); i++){
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);

            deck.set(i, randomCard);
            deck.set(j, currCard);

        }

        System.out.println("AFTER SHUFFLE: ");
        System.out.println(deck);

    }


    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
        playerSum -= 10;
        playerAceCount -= 1;
        }
        return playerSum;
    }


    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }


    //BEGINNING OF IMPLEMENTATION OF ADVANCED FEATURES THAT ARE PLANNED. E.G. (adding a shoe mechanic, counters, trainers) //





    

}
