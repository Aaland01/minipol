package monopol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SaveGameTest {
    

    Monopol monopol;
    SaveGame testSpill = new SaveGame();

    @BeforeEach
    public void setup(){
        monopol = new Monopol("Knut", "Anna");
        monopol.move(5);monopol.buyProperty();monopol.otherPlayer();
        monopol.move(4);monopol.buyProperty();monopol.otherPlayer();
        monopol.move(4);monopol.otherPlayer();
        monopol.move(3);monopol.buyProperty();monopol.otherPlayer();

    }

    @Test
    public void testSaveToFile() throws FileNotFoundException, IOException{

        testSpill=new SaveGame();
        assertThrows(NullPointerException.class, () -> {
            testSpill.saveToFile("testFil.txt");
        }, "Kan ikke lagre et tomt spill");

        testSpill=new SaveGame(monopol);
        testSpill.getSave().move();
        assertThrows(IllegalStateException.class, () -> {
            testSpill.saveToFile("testFil.txt");
        }, "Kan ikke lagre midt i en runde");

        setup();
        testSpill=new SaveGame(monopol);
        testSpill.saveToFile("testFil.txt");

        File gameLoad = new File("testFil.txt");
        BufferedReader reader = new BufferedReader(new FileReader(gameLoad));
        String spillDetaljer;
        List<String> lagretSpill = new ArrayList<>();
        while ((spillDetaljer=reader.readLine()) != null){
            String[] str=spillDetaljer.split(",");
            for (int i=0; i<str.length; i++) {
                lagretSpill.add(str[i]);
            }
        }
        reader.close();

        assertEquals("Knut", lagretSpill.get(2)); // Spiller 1
        assertEquals("400", lagretSpill.get(4)); // Spiller 1 sin balanse
        assertEquals("9", lagretSpill.get(5)); // Spiller 1 sin posisjon
        assertEquals("Anna", lagretSpill.get(7)); // Spiller 2
        assertEquals("0", lagretSpill.get(9)); // Spiller 2 sin balanse
        assertEquals("7", lagretSpill.get(10)); // Spiller 2 sin posisjon
        assertEquals("1", lagretSpill.get(11)); // Hvem som er currentPlayer

    }

    @Test
    public void loadFromFileTest() throws FileNotFoundException, IOException{
        testSpill=new SaveGame(monopol);
        testSpill.saveToFile("testFil.txt");

        testSpill.getSave().move();testSpill.getSave().otherPlayer();
        testSpill.getSave().getCurrentPlayer().addFunds(800);
        testSpill.getSave().move(1);
        testSpill.getSave().buyProperty();
        testSpill.getSave().otherPlayer();

        assertEquals(3, testSpill.getSave().getPlayer(1).getPos());
        assertEquals(8, testSpill.getSave().getPlayer(2).getPos());
        assertEquals(250, testSpill.getSave().getProperty(7).getRent());

        testSpill.loadFromFile("testFil.txt");
        //Sjekker at det er tilbke til scratch(loaden)

        assertEquals(9, testSpill.getSave().getPlayer(1).getPos());
        assertEquals(7, testSpill.getSave().getPlayer(2).getPos());
        assertEquals(100, testSpill.getSave().getProperty(7).getRent());

        PrintWriter pw = new PrintWriter("testFil.txt");
        pw.close();
        assertThrows(NullPointerException.class, () -> {
            testSpill.loadFromFile("testFil.txt");
        }, "Skal ikke lage et spill om det ikke er noe spill å laste");

        
    }


}
