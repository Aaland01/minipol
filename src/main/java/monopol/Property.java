package monopol;

public class Property {
    //----------------------------------------------------------- Tilstand
    private String streetName;
    private int price, rent, index;
    private Player owner;
  
    //----------------------------------------------------------- Konstruktør

    public Property(String street, int price, int index, int rent){
        if ((index>0 && index<12) && (price>49 && price<801) && (rent>9 && rent<501)){
            this.streetName=street;
            this.price=price;
            this.index=index;
            this.rent=rent;
        }
        else{throw new IllegalArgumentException("Ugyldige verdier for en Property klasse");}
    }

    //----------------------------------------------------------- Konstruktør for lagret spill

    public Property(String street, int index, int price, int rent, Player owner){
        if ((index>0 && index<12) && (price>49 && price<801) && (rent>9 && rent<501)){
            this.streetName=street;
            this.price=price;
            this.index=index;
            this.rent=rent;
            owner.addProperty(this);
        }
        else{throw new IllegalArgumentException("Ugyldige verdier for en Property klasse");}
    }

    //----------------------------------------------------------- Gettere

    public String getStreetName() {
        return streetName;
    }

    public int getPrice() {
        return price;
    }

    public Player getOwner() {
        return owner;
    }

    public int getRent() {
        return rent;
    }

    public int getIndex() {
        return this.index;
    }

    //----------------------------------------------------------- Settere

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    //----------------------------------------------------------- Andre metoder

    public void raiseRent(){
        this.rent*=2.5;
    }
    
    @Override
    public String toString() {
        if(owner==null){
            return streetName + "\n\nOwner: None\n\nPrice: " + price + "\n\nRent: " + rent;
        }
        return streetName + "\n\nOwner: "+ owner + "\n\nPrice: " + price + "\n\nRent: " + rent;
    }

}
    
   
