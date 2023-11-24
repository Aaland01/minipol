package monopol;


import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MonopolController{

    Monopol monopol;
    SaveGame monopolSave;

    private boolean compPlayer;

    Timer smooth = new Timer();


//----------------------------------------- Corners

    @FXML
    private Pane startUpDisplay,gameoverDisplay;

    @FXML
    private StackPane bars; 

//----------------------------------------- Players

    @FXML
    private Circle player1,player2;

//----------------------------------------- Buttons

    @FXML
    private Button move,buy,endTurn,startGame,saveGame,loadGame,endGameButton,dropDown;

    @FXML
    private ToggleButton CPUbutton;
//----------------------------------------- Display

    @FXML
    private Label terminal,playerDisplay,propertyDisplay,currentPlayerDisplay;
    @FXML
    private Label playerPropertiesDisplay,errorMessage,saveMessage,dice,computerDisp;

    @FXML
    private TextField player1input,player2input;




//---------------------------------------------------------------------- Knappe-funksjoner
    //----------------------------------------------------- Start up

    @FXML
    public void handleStartGame(){
        try{
            if(compPlayer) {
                this.monopol = new Monopol(player1input.getText(), player2input.getText());
                monopol.playAgainstComp(player2input.getText());
            } else {
                this.monopol = new Monopol(player1input.getText(), player2input.getText());
                saveGame.setDisable(false);
            } 
            startUpDisplay.setVisible(false);
            terminal.setText(monopol.toString());
            updateAllDisplays();
            bars.setVisible(false);
            
        }catch (IllegalArgumentException e){
            errorMessage.setText(e.getMessage());
        }catch (Exception a){
            errorMessage.setText("Error: "+a);
        }
        
        //validate player
    }

    //----------------------------------------------------- Throw Dice

    @FXML
    public void handleDice(){
        try{
            int oldPos = monopol.getCurrentPlayer().getPos();
            if (oldPos==9){
                monopol.move();
                terminal.setText("You are now released from jail");
                if(!monopol.inJail(monopol.getOtherPlayer())){
                    bars.setVisible(false);
                }
            }else{
                monopol.move();
                dice.setText(""+monopol.getJumps());
                switch (monopol.getCurrentPlayer().getPos()){
                    case 6:
                        updateTerminal("This is a tax space. \nPay 100$ for passage!");
                        buy.setText("Pay tax");
                        break;
                    case 9:
                        updateTerminal("You are now in jail and can not \nmove the next turn");
                        bars.setVisible(true);
                        break;
                    default:
                    if (oldPos > monopol.getCurrentPlayer().getPos()){
                        updateTerminal("You passed start! + 100$");
                    }
                    if (monopol.onRentSpace(monopol.getCurrentPlayer())){
                        buy.setText("Pay rent");
                        updateTerminal("You don't own this property \nand have to pay rent");
                    }
                }
            }
        }catch (Exception e){
            updateTerminal(e.getMessage());
        }finally{
            updateAllDisplays();
        }

    }
    
    //----------------------------------------------------- Buy

    @FXML
    public void handleBuy(){
        if (buy.getText().equals("Pay rent")){
            try{
                monopol.payRent();
                if (monopol.getWinner()!=null){
                    endGame();
                }else{
                    buy.setText("Buy");
                    terminal.setText(monopol.getProperty(monopol.getCurrentPlayer().getPos())
                    .getRent()+"$ has been paid\nto "+monopol.getOtherPlayer());
                }

            }catch (Exception e) {
                updateTerminal(e.getMessage());
            }
        }else if (buy.getText().equals("Pay tax")){
            try{
                monopol.payTax();
                if (monopol.getWinner()!=null){
                    endGame();
                }else{
                    buy.setText("Buy");
                    terminal.setText("100$ has been paid");
                }
            }catch (Exception e) {
                updateTerminal(e.getMessage());
            }
        }else{
            try{
                monopol.buyProperty();
                Property property = monopol.getProperty(monopol.getCurrentPlayer().getPos());
                if (monopol.sameColorOwned(property)){
                    terminal.setText("You just bought "+property.getStreetName()
                        +"!\nYou also own the other street\nRent went up!");
                }else{
                    terminal.setText("You just bought "+property.getStreetName()
                        +"\nand your turn is now over!");}
            }catch (Exception e){
                updateTerminal(e.getMessage());
            }
        }updateAllDisplays();
    }
    
    //----------------------------------------------------- End Turn

    @FXML
    public void handleEndTurn(){
        try{
            if(compPlayer){
                if (!monopol.getCurrentPlayer().HasMoved()){
                    throw new IllegalStateException("You have to move first!");
                }else if (!monopol.getCurrentPlayer().hasPaid()){
                    throw new IllegalStateException("You have to pay first!");
                }else{
                    endTurn.setDisable(true);move.setDisable(true);buy.setDisable(true);saveGame.setDisable(true);
                    terminal.setText(("Player 2's turn!"));
        
                    comp();
                }
            }else{
                monopol.otherPlayer();
                updateAllDisplays();
                terminal.setText(monopol.getCurrentPlayer().getName()+"s turn");
            }
            
        }
        catch (Exception e){
            updateTerminal(e.getMessage());
        }
    }
    

    //--------------------------------------------------------------------------- Display - oppdateringer
    @FXML
    public void updateAllDisplays(){
        updateScenePlayerPos();
        updatePlayerDisplay();
        updateOtherPlayerDisplay();
        updatePropertyDisplay();
        saveMessage.setText("");
        if(gameoverDisplay.isVisible()){
            Player winner = monopol.getWinner();
            if(winner==null){
                terminal.setText("Game ended");
            }else{
                terminal.setText(winner+" won the game\nwith "+winner.getplayerProperties().size()
                +" properties, and\n"+winner.getBalance()+"$ left to spare!");
                currentPlayerDisplay.setText("----");playerPropertiesDisplay.setText("");playerDisplay.setText("");
            }
            
        }
    }

    @FXML
    public void updateTerminal(String info){
        if (terminal.getText().contains(info)){
            return;
        }else{
            terminal.setText(terminal.getText() + "\n"+info);
        }
    }

    @FXML
    public void updatePlayerDisplay(){
        Player player = monopol.getCurrentPlayer();
        playerDisplay.setText("Balance = " + player.getBalance() + "\nPosition = "+player.getPos()+"\n");
        playerPropertiesDisplay.setText(player.propertiesToString());
        currentPlayerDisplay.setText(monopol.getCurrentPlayer().toString());
        if (monopol.getCurrentPlayer().getNr()==1){
            player1.setRadius(12.0);
            player2.setRadius(8.0);
        }if (monopol.getCurrentPlayer().getNr()==2){
            player2.setRadius(12.0);
            player1.setRadius(8.0);
        }
    }

    @FXML
    private void updateOtherPlayerDisplay(){
        computerDisp.setText("Balance = " + monopol.getOtherPlayer().getBalance() + "\n"+monopol.getOtherPlayer().propertiesToString());
        saveMessage.setText("Updated");
    }

    @FXML
    public void updatePropertyDisplay(){
        int index=monopol.getCurrentPlayer().getPos();
        switch(index){
            case 0:
            propertyDisplay.setText("Start\n\nRecieve 100$\nfor passing!");break;
            case 3:
            propertyDisplay.setText("Jail release\n\nSit tight");break;
            case 6:
            propertyDisplay.setText("Tax\nPay 100$ \n\nNo questions allowed");break;
            case 9:
            propertyDisplay.setText("Jail!\n\nYou were caught, \nrookie mistake.");break;
            default:
            propertyDisplay.setText(monopol.getProperty(index).toString());
        }
    }

    @FXML
    public void saveEndVisible(){
        if (dropDown.getText().equals("X")){
            saveGame.setVisible(false);endGameButton.setVisible(false);
            dropDown.setText("▼");
        }else{
            saveGame.setVisible(true);endGameButton.setVisible(true);
            dropDown.setText("X");
        }
    }

    @FXML
    public void computerPlayerToggle(){
        if(!compPlayer){
            player2input.setText("Computer");
            compPlayer=true;
        }else{
            compPlayer=false;
            player2input.clear();
        }
    }

    private boolean compInjail=false;

    private void comp(){
        TimerTask move = new TimerTask() {
            public void run(){
                Platform.runLater(()-> computerMove());
            }
        };
        TimerTask task = new TimerTask() {
            public void run(){
                Platform.runLater(()-> computerTurn());
            }
        };
        long delay = 3000L;
        smooth.schedule(move, 2000L);
        if(compInjail){
            return;
        }
        smooth.schedule(task, delay);
    }

    private void computerMove(){
        if(monopol.inJail(monopol.getPlayer(2))){
            updateTerminal("Player 2 was released from jail");
            monopol.computerMove();
            if(!monopol.inJail(monopol.getPlayer(1))){
                bars.setVisible(false);
            }
            updateAllDisplays();
            return;
        }
        monopol.computerMove();
        updateAllDisplays();
        dice.setText(""+monopol.getJumps());
        updateTerminal("Player 2 rolled "+monopol.getJumps());
    }

    private void computerTurn(){
        if(monopol.inJail(monopol.getPlayer(2))){
            updateTerminal("Player 2 is stuck in jail!");
            bars.setVisible(true);
        }
        monopol.computerTurn();
        int x = monopol.getPlayer(2).getplayerProperties().size();
        int y = monopol.getPlayer(2).getBalance();
        if(monopol.getPlayer(2).getplayerProperties().size()>x){
            updateTerminal("Player 2 just bought a property!");
            System.out.println(monopol.getPlayer(2).getBalance());
        }else if(monopol.getPlayer(2).getBalance()<y){
            if(monopol.getPlayer(2).getPos()==6){
                updateTerminal("Player 2 just paid tax!");
            }else{updateTerminal("Player 2 just paid rent!");}
        }
        updateAllDisplays();
        monopol.getCurrentPlayer().setHasMoved(false);
        endTurn.setDisable(false);move.setDisable(false);buy.setDisable(false);saveGame.setDisable(false);
        updateTerminal((monopol.getPlayer(1).getName()+"s turn"));
    }
    

    //--------------------------------------------------------------------------- Posisjon - oppdateringer
    @FXML
    public void updateScenePlayerPos(){ 
        //-------------------------------------------------- spiller1 brikke

        int playerPos = monopol.getPlayer(1).getPos();
        if (playerPos>=0 && playerPos<=3){
            player1.setTranslateY(96);
            player1.setTranslateX(77 - playerPos*61);
        }   
        if (playerPos>=4 && playerPos<7){
            player1.setTranslateX(-106);
            player1.setTranslateY(35 - (playerPos-4)*61);
        }
        if (playerPos>=7 && playerPos<10){
            player1.setTranslateY(-87);
            player1.setTranslateX(-45 + (playerPos-7)*61);
        }
        if (playerPos==10 || playerPos==11){
            player1.setTranslateX(77);
            player1.setTranslateY(-26 + (playerPos-10)*61);
        }

        //-------------------------------------------------- spiller2 brikke

        playerPos = monopol.getPlayer(2).getPos();
        if (playerPos>=0 && playerPos<=3){
            player2.setTranslateY(96);
            player2.setTranslateX(103 - playerPos*61);
        }   
        if (playerPos>=4 && playerPos<7){
            player2.setTranslateX(-80);
            player2.setTranslateY(35 - (playerPos-4)*61);
        }
        if (playerPos>=7 && playerPos<10){
            player2.setTranslateY(-87);
            player2.setTranslateX(-19 + (playerPos-7)*61);
        }
        if (playerPos==10 || playerPos==11){
            player2.setTranslateX(103);
            player2.setTranslateY(-26 + (playerPos-10)*61);
            }
    }

    //-------------------------------------------------------------------------------- New/end game

    @FXML
    public void endGame(){
        gameoverDisplay.setVisible(true);computerDisp.setVisible(false);
        endGameButton.setVisible(false);saveGame.setVisible(false);
        dice.setVisible(false);dropDown.setText("▼");dropDown.setVisible(false);
        updateAllDisplays();
        move.setDisable(true);endTurn.setDisable(true);buy.setText("Buy");
    }

    @FXML
    public void handleNewGame(){
        startUpDisplay.setVisible(true);
        gameoverDisplay.setVisible(false);computerDisp.setVisible(false);
        playerDisplay.setText("");currentPlayerDisplay.setText("");playerPropertiesDisplay.setText("");
        move.setDisable(false);endTurn.setDisable(false);
        dice.setVisible(true);dropDown.setVisible(true);
        player1input.clear();player2input.clear();errorMessage.setText("");
        compPlayer=false;CPUbutton.setSelected(false);
        bars.setVisible(false);
    }

    @FXML
    public void saveToFile(){
        if (monopol.getCurrentPlayer().HasMoved()){
            saveMessage.setText("You can only save at\nthe start of a turn!");
            saveMessage.setTextFill(Color.CRIMSON);
        }else{
            try{
                monopolSave = new SaveGame(monopol);
                monopolSave.saveToFile("GameSave.txt");
                saveMessage.setTextFill(Color.FORESTGREEN);
                saveMessage.setText(">Game saved");
                saveEndVisible();
            }catch(Exception e){
                updateTerminal(e.getMessage());
                System.out.println(e);
            }
        }
    }

    @FXML
    public void loadFromFile(){
        try{
            monopolSave = new SaveGame();
            monopolSave.loadFromFile("GameSave.txt");
            this.monopol=monopolSave.getSave();
            if(monopol.isCompPlayer()){
                this.compPlayer=true;
            }
            startUpDisplay.setVisible(false);
            terminal.setText("Game loaded:\n"+monopol.toString());
            updateAllDisplays();
            if(monopol.inJail(monopol.getCurrentPlayer()) || monopol.inJail(monopol.getOtherPlayer())){
                bars.setVisible(true);
            }
        }catch(Exception e){
            errorMessage.setText("Error loading Game");
            System.out.println(e);System.out.println(e.getLocalizedMessage());
        }
    }

}
