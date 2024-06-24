
//first time ever using this type of method. It is so cool and incredibly useful, endless possibilities!
public enum DeckState {
    FULL(52, 48),
    MOSTLY_FULL(47, 39),
    HALF(38, 26),
    LOW(25, 13),
    VERY_LOW(12, 1),
    EMPTY(0, 0);

    // fields for storing values within the enum states
    private final int maxCards;
    private final int minCards;

    // constructor 
    DeckState(int maxCards, int minCards) {
        this.maxCards = maxCards;
        this.minCards = minCards;
    }

    // getters in place just in case i need them later
    public int getMaxCards() {    
        return maxCards;        
    }

    public int getMinCards() {
        return minCards;
    }

    public static DeckState getStatus(int cardCount) {
        for (DeckState state : values()) {
            if (cardCount >= state.getMinCards() && cardCount <= state.getMaxCards()) {
                return state;
            }
        }
        return EMPTY;
    }
}
  


