package monopol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Monopol{

    private Player player1;
    private Player player2;
    private Player winner;
    private Player currentPlayer;
    private List<Player> players;
    private List<Property> properties = new ArrayList<>();
    private int jumps;
    boolean compPlayer;

    //-------------------------------------------------------- Konstruktør

    public Monopol(String player1,String player2){
        this.player1 = new Player(1,player1);
        this.player2 = new Player(2,player2);
        players = Arrays.asList(this.player1, this.player2);
        constructProperties();
        SetCurrentPlayer(this.player1);

    }

    //----------------------------------------------------------- Konstruktør for lagret spill

    public Monopol(Player player1, Player player2, List<Property> properties,int curP){
        this.player1 = player1;
        this.player2 = player2;
        players = Arrays.asList(this.player1, this.player2);
        this.properties=properties;
        if(curP==0){
            SetCurrentPlayer(player1);
            this.compPlayer=true;
        }else{
            SetCurrentPlayer(getPlayer(curP));
        }
    }

    //-------------------------------------------------------- Gatene 

    private void constructProperties(){
        Property gate8 = new Property("Realfagsbygget", 800, 11, 160); properties.add(gate8);
        Property gate7 = new Property("Hovedbygget", 700, 10, 140); properties.add(gate7);
        Property gate6 = new Property("IT-bygget", 600, 8, 120); properties.add(gate6);
        Property gate5 = new Property("EL-huset", 500, 7, 100); properties.add(gate5);
        Property gate4 = new Property("Gamle Kjemi", 400, 5, 80); properties.add(gate4);
        Property gate3 = new Property("Gamle Fysikk", 300, 4, 60); properties.add(gate3);
        Property gate2 = new Property("Kjelhuset", 200, 2, 40); properties.add(gate2);
        Property gate1 = new Property("Stripa", 100, 1, 20); properties.add(gate1);
    }

    //----------------------------------------------------------- Bevegelse

    /**
     * Makes the currentplayer move 1-6 spaces once,
     * or if the player is in jail, moves the currentplayer to the
     * lower left corner. 
     * 
     * @throws IllegalStateException if the currentPlayer has already moved
     */

    public void move(){ 
        if (currentPlayer.HasMoved()){
            throw new IllegalStateException("You cannot move twice");
        }else if (inJail(currentPlayer)){
            currentPlayer.setPos(3);
        }else{
            this.jumps = dice();
            updatePlayerPos(jumps);
        }
    }

    //----------------------------------------------------------- Move-metode for testkode

    public void move(int spaces){
        if (spaces>11 || spaces<1){throw new IllegalArgumentException("Illegal move");} 
        else{
            if (currentPlayer.HasMoved()){
                throw new IllegalStateException("You cannot move twice");
            }else if (inJail(currentPlayer)){
                currentPlayer.setPos(3);
            }else{
                this.jumps = spaces;
                updatePlayerPos(jumps);
            }
        }
    }
   
    //----------------------------------------------------------- Terning

    private int dice(){ 
        return (int)(Math.random() * 6 +1);
    }
    
    private void updatePlayerPos(int diceRoll){
        currentPlayer=getCurrentPlayer();
        int oldPos=currentPlayer.getPos();
        int newPos = oldPos+diceRoll;
        if (newPos>11){
            currentPlayer.addFunds(100);
            currentPlayer.setPos(newPos-12);
        }else{
            currentPlayer.setPos(newPos);
        }
        if (onRentSpace(currentPlayer) || currentPlayer.getPos()==6){
            currentPlayer.setHasPaid(false);
        }
    }

    //----------------------------------------------------------- Validering

    private boolean checkGameOver(int price){
        return (currentPlayer.getBalance() - price)<0;
    }

    public boolean inJail(Player player){
        return (player.getPos()==9);
    }

    private boolean isOtherPlayersProperty(Property property){
        if (property.getOwner()==null){
            return false;
        }
        return property.getOwner().equals(getOtherPlayer());
    }

    public boolean onRentSpace(Player player){
        int pos = player.getPos();
        if (pos%3==0){    //hjørner (pos: 0,3,6,9)
            return false;
        }else{
            return isOtherPlayersProperty(getProperty(pos));
        }
    }

    //FOR COMPUTER
    public boolean onRentSpace(){
        int pos = player2.getPos();
        if (pos%3==0){    //hjørner (pos: 0,3,6,9)
            return false;
        }else{
            return isOtherPlayersProperty();
        }
    }
    private boolean isOtherPlayersProperty(){
        Property property = getProperty(player2.getPos());
        if (property.getOwner()==null){
            return false;
        }
        return property.getOwner().equals(player1);
    }

    //--------------------------------------------------------- Betaling
    //------------------------------------ Property
    public void buyProperty(){ 
        if (!currentPlayer.HasMoved()){
            throw new IllegalStateException("You have to move first!");}
        int pos =currentPlayer.getPos();
        if (pos%3==0){
            throw new IllegalStateException("This is a corner square.\nIt cannot be bought");
        }Property property= getProperty(pos);
        if (property.getOwner()==null){
            currentPlayer.buyProperty(property);                
            if (sameColorOwned(property)){
                property.raiseRent();
                getNeighbProperty(property).raiseRent();
            }
        }else if (isOtherPlayersProperty(property)){
            throw new IllegalArgumentException("The other player owns this street!");
        }else{throw new IllegalStateException("You already own this street!");}
        
    }

    //------------------------------------ Rent
    public void payRent(){
        if (!currentPlayer.HasMoved()){
            throw new IllegalStateException("You have to move first");
        }if (!onRentSpace(currentPlayer)){
            throw new IllegalStateException("No rent to pay!");
        }
        int rent = getProperty(currentPlayer.getPos()).getRent();
        if (checkGameOver(rent)){
            endGame();
        }else{
            currentPlayer.removeFunds(rent);
            getOtherPlayer().addFunds(rent);
            currentPlayer.setHasPaid(true);
        }
    }

    //------------------------------------ Tax
    public void payTax(){
        if (!currentPlayer.HasMoved()){
            throw new IllegalStateException("You have to move first!");}
        if (currentPlayer.getPos()!=6){
            throw new IllegalStateException("No tax to pay!");
        } 
        if (checkGameOver(100)){
            endGame();
        }else{
            currentPlayer.removeFunds(100);
            currentPlayer.setHasPaid(true);
        }
    }

    //------------------------------------------------------ Players
    private void SetCurrentPlayer(Player player){
        this.currentPlayer=player;
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public void otherPlayer(){
        if (!currentPlayer.HasMoved()){
            throw new IllegalStateException("You have to move first!");
        }else if (!currentPlayer.hasPaid()){
            throw new IllegalStateException("You have to pay first!");
        }else if (compPlayer){
            computerAction();
            currentPlayer.setHasMoved(false);
        }else{
            SetCurrentPlayer(getOtherPlayer());
            getOtherPlayer().setHasMoved(false);
        }
    }
    //------------------------------------------------------ ComputerPlayer
    public void playAgainstComp(String name){
        this.player2.setName(name);
        players=Arrays.asList(this.player1, this.player2);
        compPlayer = true;
    }

    public void computerMove(){
        if (inJail(player2)){
            player2.setPos(3);
            System.out.println("Jail fase");
            return;
        }

        this.jumps = dice();
        int oldPos=player2.getPos();
        int newPos=oldPos + jumps;
        if (newPos>11){
            player2.addFunds(100);
            player2.setPos(newPos-12);
        }else{
            player2.setPos(newPos);
        }System.out.println("Fase 2");
    }

    public void computerTurn(){
        if (onRentSpace()){
            int rent = getProperty(player2.getPos()).getRent();
            if (rent>player2.getBalance()){
                endGame();
            }else{
                player2.removeFunds(rent);
                player1.addFunds(rent);
                System.out.println("Paid rent");
            }

        }else if (player2.getPos()==6){
            if (player2.getBalance()<100){
                endGame();
            }else{
                player2.removeFunds(100);
                System.out.println("Paid tax");
            }
        }else if((player2.getPos()%3)==0){
            System.out.println("Corner square");
            return;

        }else if (getProperty(player2.getPos()).getOwner()==null){
            Property property = getProperty(player2.getPos());
            if (player2.getBalance()>property.getPrice()){
                if (sameColorOwned(property,0)){
                    player2.buyProperty(property);
                    property.raiseRent();
                    getNeighbProperty(property).raiseRent();
                    System.out.println("Bought prop same color");
                }else{
                    int n = (player2.getBalance()-property.getPrice())/50;
                    System.out.println(n);
                    for (int i=0;i<n;i++){
                        if((int)(Math.random() * 6 +1)>4){
                            player2.buyProperty(property);
                            System.out.println("Bought prop");
                            break;
                        }
                        System.out.println(i + "<" + n);
                    }
                }
            }
        }
    }

    private void computerAction(){
        computerMove();
        try{Thread.sleep(1500);}
        catch(Exception e){System.out.println(e.getMessage());}
        computerTurn();
    }


    //------------------------------------------------------ Properties

    public Property getProperty(int index){
        if(index>0 && index<12){
            List<Property> iterateProperties = this.properties;
            return iterateProperties.stream().filter(property -> property.getIndex()==index).findAny().get();
        }else throw new IllegalArgumentException("Invalid index");
    }

    private Property getNeighbProperty(Property property){
        int index = property.getIndex();
        if ((index+1)%3==0){
            return getProperty(index-1);
        }if ((index-1)%3==0){
            return getProperty(index+1);
        }else{throw new IllegalArgumentException("Error fetching neighbProp");}
    }

    public boolean sameColorOwned(Property property){
        if (getNeighbProperty(property).getOwner()==null){
            return false;
        }if (getNeighbProperty(property).getOwner().equals(currentPlayer)){
            return true;
        }else{return false;}
    }

    public boolean sameColorOwned(Property property, int comp){
        if (getNeighbProperty(property).getOwner()==null){
            return false;
        }if(comp==0){
            return getNeighbProperty(property).getOwner().equals(player2);
        }else{System.out.println("Samecolor error");return false;}
    }

    //------------------------------------------------------ Andre Gettere

    public Player getPlayer(int nr){
        if (nr==1){return this.player1;}
        if (nr==2){return this.player2;}
        else{throw new IllegalArgumentException("Illegal argument for fetching player");}
    }
    public Player getWinner(){
        return this.winner;
    }

    public int getJumps(){
        return this.jumps;
    }
    public Player getOtherPlayer(){
        for(Player player : players){
            if (!player.equals(currentPlayer)){
                return player;
            }
        }throw new IllegalStateException("Error fetching otherPlayer");
    }

    public List<Property> getProperties(){
        return this.properties;
    }

    public boolean isCompPlayer(){
        return compPlayer;
    }

    //------------------------------------------------------ End Game

    public void endGame(){
        this.winner=getOtherPlayer();
        System.out.println("Game over! "+winner.getName()+" won the game!");
    }

    @Override
    public String toString() {
        return "Monopol: \nplayer1 = " + player1 + "\nplayer2 = " + player2;
    }

    public static void main(String[] args) {
        Monopol monopol = new Monopol("Knut", "Anna");
        monopol.move();
        
    }
    
}
