package monopol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class PropertyTest {
    
    Property gate = new Property("Testgate", 200, 2, 50);

    @Test
    public void constructerTest(){
        //Tester ikke navn, ettersom alle gatenavn er bestemt fra før
        assertThrows(IllegalArgumentException.class, () -> {
            gate = new Property("Testgate2", 30, 3, 30);
        }, "Eiendomspris kan ikke være så lav");
        assertThrows(IllegalArgumentException.class, () -> {
            gate = new Property("Testgate3", 200, 13, 30);
        }, "Indeksen kan ikke eksistere");
        assertThrows(IllegalArgumentException.class, () -> {
            gate = new Property("Testgate4", 200, 3, 600);
        }, "ALTFOR høy leie");

        gate = new Property("RiktigGate", 200, 4, 50);
        assertEquals(200, gate.getPrice(),"Prisen stemmer ikke");
        assertEquals(4, gate.getIndex(),"Indeksen stemmer ikke");
        assertEquals(50, gate.getRent(),"Leien stemmer ikke");
        assertEquals(null, gate.getOwner(),"Eieren skal ikke eksistere enda");
    }

    @Test
    public void constructerTest2(){
        Player testPlayer = new Player("Checo", 1, 500, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            gate = new Property("Testgate2", 3, 30, 30,testPlayer);
        }, "Eiendomspris kan ikke være så lav");
        assertThrows(IllegalArgumentException.class, () -> {
            gate = new Property("Testgate3", 13, 200, 30,testPlayer);
        }, "Indeksen kan ikke eksistere");
        assertThrows(IllegalArgumentException.class, () -> {
            gate = new Property("Testgate4", 3, 200, 600,testPlayer);
        }, "ALTFOR høy leie");

        gate = new Property("RiktigGate", 4, 200, 50, testPlayer);
        assertEquals(200, gate.getPrice(),"Prisen stemmer ikke");
        assertEquals(4, gate.getIndex(),"Indeksen stemmer ikke");
        assertEquals(50, gate.getRent(),"Leien stemmer ikke");
        assertEquals(testPlayer, gate.getOwner(),"Eieren stemmer ikke");
        assertEquals(gate, testPlayer.getplayerProperties().get(0));
    }

    @Test
    public void testRaiseRent(){
        gate.raiseRent();
        assertEquals(125, gate.getRent(),"Leien øktes ikke");
    }


}
