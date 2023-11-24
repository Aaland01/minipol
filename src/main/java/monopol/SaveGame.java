package monopol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveGame implements IFsaveGame{
    
    private Monopol monopol;

    public SaveGame(Monopol monopol){
        this.monopol=monopol;
    }

    public SaveGame(){
        ;
    }

    public Monopol getSave(){
        return monopol;
    }

    //-------------------------------------------------------- Skrive til fil

    @Override
    public void saveToFile(String file) throws IOException, FileNotFoundException{
        if (monopol.getCurrentPlayer().HasMoved()){
            throw new IllegalStateException("Can't save a game in the middle of a turn");}
        if (this.monopol==null){throw new NullPointerException("No game to save");}

        File gameSave = new File(file);
        FileWriter writer = new FileWriter(gameSave);
        writer.write(monopolString());
        writer.flush();writer.close();
    }

    public String monopolString(){
        if(monopol.isCompPlayer()){
            return "Save\n"+playerToString(1)+playerToString(2)+"0\n"+propertyToString();
        }else{
            return "Save\n"+playerToString(1)+playerToString(2)+
            monopol.getCurrentPlayer().getNr()+"\n"+propertyToString();
        }

    }

    private String playerToString(int nr){
        Player player = monopol.getPlayer(nr);
        String playerString= "Spiller,"+player.getName()+','+nr+','+
        player.getBalance()+','+
        player.getPos();
        return playerString+"\n";
    }

    private String propertyToString(){
        String propertiesString="";
        List<Property> properties = monopol.getProperties();
        for (Property property : properties) {
            propertiesString+=property.getStreetName()+','+
            property.getIndex()+','+
            property.getPrice()+','+
            property.getRent()+',';
            if (property.getOwner()==null){
                propertiesString+="null,"+'\n';
            }else{
                propertiesString+=""+property.getOwner().getNr()+'\n';
            }
        }return propertiesString;
    }
    
    //-------------------------------------------------------- Lese fra fil

    @Override
    public void loadFromFile(String file) throws IOException, FileNotFoundException{
        File gameLoad = new File(file);
        BufferedReader reader = new BufferedReader(new FileReader(gameLoad));
        String str;
        Player player1=new Player("ErrorEn", 1, 0, 0);
        Player player2=new Player("ErrorTo", 2, 0, 0);
        int curP=1;
        List<Property> properties = new ArrayList<>();
        str = reader.readLine();
        if (str==null){
            reader.close();
            throw new NullPointerException("Missing saved file to load");
        }else if(!str.equals("Save")){
            reader.close();
            throw new NullPointerException("File is unreadable");
        }
        while ((str=reader.readLine()) != null){
            String[] verdier = str.split(",");
            if(verdier[0].equals("Spiller")){
                switch(verdier[2]){
                    case "1":
                    player1=stringToPlayer(verdier);
                    case "2":
                    player2=stringToPlayer(verdier);
                }
            }else if(str.equals("0") || str.equals("1") || str.equals("2")){
                curP=Integer.parseInt(str);
            }else{properties.add(stringToProperty(verdier, player1, player2));}
        }this.monopol=new Monopol(player1, player2, properties, curP);
        reader.close();
    }

    private Player stringToPlayer(String[] verdier){
        String navn = verdier[1];
        int nr = Integer.parseInt(verdier[2]);
        int balance = Integer.parseInt(verdier[3]);
        int pos = Integer.parseInt(verdier[4]);
        return new Player(navn, nr, balance, pos);
    }

    private Property stringToProperty(String[] verdier, Player player1, Player player2){
        String navn = verdier[0];
        int index = Integer.parseInt(verdier[1]);
        int price = Integer.parseInt(verdier[2]);
        int rent = Integer.parseInt(verdier[3]);
        if (verdier[4].equals("null")){
            return new Property(navn, price, index, rent);
        }else{
            if (verdier[4].equals("1")){
                Player owner = player1;
                return new Property(navn, index, price, rent, owner);
            }if (verdier[4].equals("2")){
                Player owner = player2;
                return new Property(navn, index, price, rent, owner);
            }else{throw new IllegalArgumentException("Error reading string to property");}
        }
        
    }

}
