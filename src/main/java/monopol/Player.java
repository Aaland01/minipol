package monopol;

import java.util.ArrayList;

public class Player {

    //----------------------------------------------------------- Tilstander
    
    private int balance = 800;
    private ArrayList<Property> playerProperties = new ArrayList<Property>();
    private String name;
    private int position, nr;  
    private boolean hasMoved;
    private boolean hasPaid=true;

    //----------------------------------------------------------- Konstruktør

    public Player(int nr,String spiller){
        if (spiller.matches("[a-zA-ZæøåÆØÅ]+") && (nr>0 && nr<=2) && !spiller.isBlank() && spiller.length()<9){
            this.name=spiller;
            this.position=0;
            this.nr=nr;
        }else{
            throw new IllegalArgumentException("Name cannot be blank, contain special characters or\nbe longer than 8 characters");
        }
    }

    //----------------------------------------------------------- Konstruktør for lagret spill

    public Player(String navn, int nr, int balance, int pos){
        if (navn.matches("[a-zA-ZæøåÆØÅ12]+") && (nr>0 && nr<=2) && !navn.isBlank() && navn.length()<9){
            this.name=navn;this.nr=nr;
            if(balance>=0 && balance<10000 && pos<12 && pos>=0){
                this.balance= balance;
                this.position=pos;
            }else{throw new IllegalArgumentException("Illegal balance og position index");}
        }else{
            throw new IllegalArgumentException("Error loading Player");
        }
    }

    //----------------------------------------------------------- Gettere

    public ArrayList<Property> getplayerProperties(){
        return playerProperties;
    }

    public int getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public int getPos(){
        return position;
    }

    public int getNr(){
        return nr;
    }

    //----------------------------------------------------------- Setters

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPos(int pos) {
        if (pos>=0 && pos<12){
            this.position=pos;
            setHasMoved(true);
        } else throw new IllegalArgumentException("Invalid position index");
    }

    public void setHasMoved(boolean value){
        this.hasMoved=value;
    }

    public void setHasPaid(boolean value){
        this.hasPaid=value;
    }
    
    //----------------------------------------------------------- Andre metoder

    public String propertiesToString(){
        String result = "Properties:\n";
        if (!playerProperties.isEmpty()){
            int i=0;
            for (Property property : playerProperties) {
                i++;
                if(i%2==1) {
                    result+=property.getStreetName()+", ";
                }else{result+=property.getStreetName()+",\n";}
            }
        } return result.replaceAll(",$", "");
    }

    public boolean HasMoved(){
        return hasMoved;
    }

    public boolean hasPaid(){
        return hasPaid;
    }

    public void addProperty(Property property){ 
        this.playerProperties.add(property);
        property.setOwner(this);  
    }

    public void buyProperty(Property property){
        if (this.balance<(property.getPrice())){
            throw new IllegalStateException("You cannot afford this property!");
        }else{
            removeFunds(property.getPrice());
            addProperty(property);
        }
    }

    public void addFunds(int money){
        if (money>0 && money<2001){
            this.balance+=money;
        } else{throw new IllegalArgumentException("Invalid amount of funds to add");}
       
    }

    public void removeFunds(int money){
        if (money>0 && money<=balance){
            this.balance-=money;
        } else{throw new IllegalArgumentException("Invalid amount of funds to remove");}
       
    }

    @Override
    public String toString() {
        return ""+name;
    }

}

