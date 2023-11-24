package monopol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    Player spiller1;
    Player spiller2;
    Property gate1;
    Property gate2;

    ArrayList<Property> properties = new ArrayList<Property>();
    
    @BeforeEach
    public void setup(){
        spiller1 = new Player(1, "Knut");
        spiller2 = new Player(2, "Anna");
        gate1 = new Property("Testgate 1", 200, 2, 20);
        gate2 = new Property("Testgate 2", 400, 4, 100);
        properties.add(gate1);properties.add(gate2);
    }

    @Test
    public void testConstructor(){
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player(3, "Eivind");
        }, "Kan ikke ha annet enn spiller 1 og 2");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player(1, "Knut3");
        }, "Kan ikke ha tall i navn");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player(1, "/,:knut");
        }, "Kan ikke ha symboler i navn");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player(1, "");
        }, "Kan ikke ha blankt navn");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player(1, "Sebastian");
        }, "Kan ikke ha navn lenger enn 8 bokstaver");


        Player faktiskRiktig = new Player(1, "Riktig");
        assertEquals(faktiskRiktig.getName(), "Riktig", "navn stemmer ikke");
        assertEquals(faktiskRiktig.getBalance(), 800, "Rusk i økonomien!");
        assertEquals(faktiskRiktig.getPos(), 0, "Må starte med pos 0");
        assertEquals(faktiskRiktig.getNr(), 1, "Nr stemmer ikke");
    }

    @Test
    public void testConstructor2(){
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player("Eivind",3,400,5);
        }, "Kan ikke ha annet enn spiller 1 og 2");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player("Eivind",1,40000,5);
        }, "Kan ikke ha så høy balanse");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player("Eivind",1,400,13);
        }, "Kan ikke ha den posisjonen");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1= new Player("Knutisbabaen",1,400,5);
        }, "Kan ikke ha navn lenger enn 8 bokstaver");

        Player faktiskRiktig = new Player("Riktig",2,0,0);
        assertEquals(faktiskRiktig.getName(), "Riktig", "navn stemmer ikke");
        assertEquals(faktiskRiktig.getBalance(), 0, "Rusk i økonomien!");
        assertEquals(faktiskRiktig.getPos(), 0, "Må starte med pos 0");
        assertEquals(faktiskRiktig.getNr(), 2, "Nr stemmer ikke");
    }

    @Test
    public void testAddProperty(){
        spiller1.addProperty(gate1);
        assertEquals(spiller1, gate1.getOwner(),"Eierne stemmer ikke");
        assertEquals(gate1, spiller1.getplayerProperties().get(0),"Noe stemmer ikke med eiendomslista til spilleren");
    }

    @Test
    public void testBuyProperty(){
        spiller1.setBalance(300);
        
        assertThrows(IllegalStateException.class, () -> {
            spiller1.buyProperty(gate2);
        }, "Kan ikke kjøpe eiendom du ikke har råd til");

        spiller1.buyProperty(gate1);
        assertEquals(100, spiller1.getBalance(),"Pengene må trekkes fra");
    }

    @Test
    public void setPosTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.setPos(-2);
        }, "Kan ikke ha negativ posisjon");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.setPos(30);
        }, "Kan ikke ha posisjon større enn 12");

        spiller1.setPos(8);
        assertEquals(8, spiller1.getPos(),"Posisjonene stemmer ikke");
    }
    
    @Test
    public void addFundsTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.addFunds(-200);
        }, "Kan ikke legge til negative verdier");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.addFunds(0);
        }, "Ingen vits i å kunne legge til ingenting");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.addFunds(200000);
        }, "Kan ikke legge til så mye");

        spiller1.addFunds(400);
        assertEquals(1200, spiller1.getBalance(),"Pengene må legges til");
    }

    @Test
    public void removeFundsTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.removeFunds(-200);
        }, "Kan ikke fjerne negative verdier");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.removeFunds(0);
        }, "Ingen vits i å kunne fjerne ingenting");
        assertThrows(IllegalArgumentException.class, () -> {
            spiller1.removeFunds(1000);
        }, "Kan ikke fjerne mer enn man kan ta");

        spiller1.removeFunds(400);
        assertEquals(400, spiller1.getBalance(),"Pengene må fjernes");
    }
}
