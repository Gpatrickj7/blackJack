import java.awt.*;
import java.awt.event.ActionListener;  // Add this
import java.awt.event.ActionEvent; 
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



    private ArrayList<Card> deck;
    private Player player;
    private Dealer dealer;
    private int bet;
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


                //add score to display 
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.WHITE);

                // Player score with bust check
                int playerCurrentSum = reducePlayerAce();
                String playerScoreText = "Player Score: " + playerCurrentSum;
                if (playerCurrentSum > 21) {
                    g.setColor(Color.RED);
                    playerScoreText += " BUST!";
                }
                g.drawString(playerScoreText, 20, 300);

                //reset color for dealer score 
                g.setColor(Color.WHITE);
            
                // Dealer score - only show full score when game is over
                String dealerScoreText;
                if (!stayButton.isEnabled()) {
                    int dealerCurrentSum = reduceDealerAce();
                    dealerScoreText = "Dealer Score: " + dealerCurrentSum;
                    if (dealerCurrentSum > 21){
                        g.setColor(Color.RED);
                        dealerScoreText += " BUST!";
                    }

                } else {
                // Only show the visible card's value during play
                dealerScoreText = "Dealer Shows: " + dealerHand.get(0).getValue();
                }
                g.drawString(dealerScoreText, 20, 200);



                //display winner
                if (!stayButton.isEnabled() || checkForBust()) {
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
                    nextHandButton.setEnabled(true);
                
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
    JButton nextHandButton = new JButton("Next Hand");






    BlackJack() {
        startGame(true);

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
        //Next Hand button 
        nextHandButton.setEnabled(false);
        buttonPanel.add(nextHandButton);
        
        //gonna toy with this. might make a menu and layer this sort of shit in so you dont misclick and restart a good game
        restartButton.setEnabled(false);
        buttonPanel.add(restartButton);

        



        //add button panel to the frame ez
        frame.add(buttonPanel, BorderLayout.SOUTH);

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                startGame(true);

                
                
                gamePanel.repaint();                            //this works basically how i want. might keep and use knowledge from this improvement to layer in a menu functionality 
                                                                //but for now (6/18/24 5:04 pm) im gonna try only letting it (restart button) be enabled after "the game"
                
            }
        });

        //next hand button action listener
        nextHandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                startGame(false);  // false = keep current deck
                
                gamePanel.repaint();
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
                if(checkForBust()) {
                    gamePanel.repaint();
                    return;
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

    public void startGame(boolean resetDeck){
        //start game now returns the enabled status of the restartButton to its default 
        restartButton.setEnabled(false);
        nextHandButton.setEnabled(false);
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);



        
        //deck methods - only create new deck if needed
        if (resetDeck) {  // Restart button = always new deck
            deck = buildDeck();
            shuffleDeck();
            System.out.println("New deck created. Cards remaining: " + deck.size());
        } 
        else if (deck == null || DeckState.getStatus(deck.size()) == DeckState.VERY_LOW) {  // Next hand = only new deck if needed
            deck = buildDeck();
            shuffleDeck();
            System.out.println("New deck created due to low cards. Cards remaining: " + deck.size());
        }
        else {
            System.out.println("Continuing with existing deck. Cards remaining: " + deck.size() );
        }

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
            //card = deck.remove(deck.size() - 1);
            card = drawCard();
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

    //getter for deck
    public ArrayList<Card> getDeck() {
        return deck;
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

    private boolean checkForBust() {

        //check player bust
        if (reducePlayerAce() > 21) {

            hitButton.setEnabled(false);
            stayButton.setEnabled(false);
            restartButton.setEnabled(true);

            return true;
        }

        //check dealer bust
        if (!stayButton.isEnabled() && reduceDealerAce() > 21) {
            restartButton.setEnabled(true);

            return true;
        }
        return false;
    }


    //remove card from actual deck
    public Card drawCard() {

        //possibly implement dealing with empty deck here
        if (deck.isEmpty()) {
            buildDeck();
            shuffleDeck();
        }
        return deck.remove(deck.size() - 1);
    }

    //defined classes for player/dealer. this will allow for easy scaling within the game and helps me stay organized.
    public class Player {
        private ArrayList<Card> hand;
        private int sum;
        private int aceCount;
        

    
        public Player() {
    
            hand = new ArrayList<>();
            sum = 0;
            aceCount = 0;
    
        }
    
        public void addCard(Card card) {
            hand.add(card);
            sum += card.getValue();
            if (card.isAce()) {
                aceCount++;
            }
        }
    
        public ArrayList<Card> getHand() {
            return hand;
        }
    
        public int getSum() {
            return sum;
        }
    
        public int getAceCount() {
            return aceCount;
        }
    
        public void reset() {
            hand.clear();
            sum = 0;
            aceCount = 0;
        }
    }
    
    public class Dealer extends Player {
        private Card hiddenCard;
    
        public void setHiddenCard(Card card) {
            this.hiddenCard = card;
        }
    
        public Card getHiddenCard() {
            return hiddenCard;
        }
    }


    

}
