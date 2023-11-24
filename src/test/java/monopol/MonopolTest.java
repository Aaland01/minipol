package monopol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonopolTest {
    
    Monopol monopol;

    
    @BeforeEach
    public void setup(){
        monopol = new Monopol("Eivind", "Anna");
        monopol.move(2);monopol.buyProperty();monopol.otherPlayer();
        monopol.move(4);monopol.buyProperty();monopol.otherPlayer();
        monopol.move(6);monopol.buyProperty();monopol.otherPlayer();
    }

    @Test
    public void constructerTest(){
        Monopol testMonopol = new Monopol("Knut", "Abi");
        assertEquals(testMonopol.getPlayer(1), testMonopol.getCurrentPlayer(),
        "Spiller lages ikke på riktig måte, eller blir ikke currentPlayer");
        assertEquals(testMonopol.getPlayer(2), testMonopol.getOtherPlayer(),
        "Spiller lages ikke på riktig måte, eller det blir feil currentPlayer");
        assertFalse(testMonopol.getProperties().isEmpty());
        assertNull(testMonopol.getWinner(), "Kan ikke ha en vinner");
        assertEquals(0, testMonopol.getJumps(),"Ingen har kastet terning enda");
    }

    @Test
    public void constructerTest2(){
        Player player1 = new Player("Eivind", 1, 250, 9);
        Player player2 = new Player("Annna", 2, 600, 2);
        List<Property> properties = new ArrayList<>();
        Property gate8 = new Property("Realfagsbygget",  11, 800, 160, player1); properties.add(gate8);
        Property gate7 = new Property("Hovedbygget", 700, 10, 140); properties.add(gate7);
        Property gate6 = new Property("IT-bygget", 600, 8, 120); properties.add(gate6);
        Property gate5 = new Property("EL-huset", 7, 500, 100, player2); properties.add(gate5);
        Property gate4 = new Property("Gamle Kjemi", 400, 5, 80); properties.add(gate4);
        Property gate3 = new Property("Gamle Fysikk", 300, 4, 60); properties.add(gate3);
        Property gate2 = new Property("Kjelhuset", 2, 200, 40, player2); properties.add(gate2);
        Property gate1 = new Property("Stripa", 1, 100, 20, player2); properties.add(gate1);

        Monopol testMonopol = new Monopol(player1, player2, properties, 2);
        assertEquals(testMonopol.getPlayer(2), testMonopol.getCurrentPlayer(),
        "Spilleren og currentplayer er ikke like");
        assertEquals(testMonopol.getPlayer(1), testMonopol.getOtherPlayer(),
        "Spiller lages ikke på riktig måte, eller det blir feil currentPlayer");
        assertFalse(testMonopol.getProperties().isEmpty());
        assertNull(testMonopol.getWinner(), "Kan ikke ha en vinner");
        assertEquals(0, testMonopol.getJumps(),"Ingen har kastet terning enda");
        assertEquals(2, testMonopol.getCurrentPlayer().getPos());
        assertEquals(9, testMonopol.getOtherPlayer().getPos());
        testMonopol.move(2);testMonopol.buyProperty();
        testMonopol.otherPlayer();
        testMonopol.move(1);testMonopol.otherPlayer();
        assertEquals(3, testMonopol.getOtherPlayer().getPos());

    }


    @Test
    public void testMoveAndUpdatePos(){
        monopol.move();
        assertThrows(IllegalStateException.class, () -> {
            monopol.move();
        }, "Kan ikke bevege seg to ganger på rad");

        setup();
        monopol.move(8);
        assertEquals(0, monopol.getCurrentPlayer().getPos(),"Posisjonen skal bli 0 når forbi 11'te posisjon");
        assertEquals(600, monopol.getCurrentPlayer().getBalance(),"Fikk ikke 100$ for å passere start");
        monopol.otherPlayer();monopol.move(1);monopol.otherPlayer();
        monopol.move(1);monopol.otherPlayer();monopol.move();
        assertEquals(3, monopol.getCurrentPlayer().getPos());
        monopol.otherPlayer();monopol.move(1);
        assertFalse(monopol.getCurrentPlayer().hasPaid());
    }

    @Test
    public void testMove(){
        //Denne metoden er prikk lik move() bare at den tar inn et tall istedenfor å kaste terning
        //tester derfor bare at man ikke kan legge inn for høyt eller negativ argument
        assertThrows(IllegalArgumentException.class, () -> {
            monopol.move(-2);
        }, "Kan ikke bevege seg negativt");
        assertThrows(IllegalArgumentException.class, () -> {
            monopol.move(32);
        }, "Kan ikke bevege seg mer enn 11 av gangen");
    }

    @Test
    public void testPropertiesAndTax(){
        assertThrows(IllegalStateException.class, () -> {
            monopol.buyProperty();
        }, "Kan ikke kjøpe noe før man har beveget seg");

        monopol.move(2);
        assertThrows(IllegalStateException.class, () -> {
            monopol.otherPlayer();
        }, "Må betale tax før man gjør seg ferdig med runden sin");

        monopol.payTax();monopol.otherPlayer();
        monopol.move(2);
        assertThrows(IllegalStateException.class, () -> {
            monopol.buyProperty();
        }, "Kan ikke kjøpe noe man ikke har råd til");

        assertThrows(IllegalStateException.class, () -> {
            monopol.payTax();
        }, "Kan ikke betale Tax utenom på tax feltet");

        monopol.otherPlayer();
        assertThrows(IllegalStateException.class, () -> {
            monopol.payTax();
        }, "Kan ikke betale tax på starten av runden");
        
        monopol.move(11);
        int oldRent = monopol.getProperty(monopol.getCurrentPlayer().getPos()).getRent();
        monopol.buyProperty();
        assertTrue(monopol.getProperty(monopol.getCurrentPlayer().getPos()).getRent()>oldRent);
        
        monopol.otherPlayer();monopol.move(4);
        assertThrows(IllegalStateException.class, () -> {
            monopol.buyProperty();
        }, "Kan ikke kjøpe noe du allerede eier");

        monopol.otherPlayer();monopol.move(3);monopol.payRent();
        assertThrows(IllegalArgumentException.class, () -> {
            monopol.buyProperty();
        }, "Kan ikke kjøpe noe den andre spilleren eier");

    }

    @Test
    public void payRentTest(){
        monopol.move(4);
        assertThrows(IllegalStateException.class, () -> {
            monopol.otherPlayer();
        }, "Kan ikke ende runden før du har betalt leie");
        monopol.payRent();
        assertEquals(120, monopol.getProperty(monopol.getCurrentPlayer().getPos()).getRent(),
         "Leia stemmer ikke");
        assertEquals(380, monopol.getCurrentPlayer().getBalance(), "Pengene ble ikke trekt fra");
        assertEquals(120, monopol.getOtherPlayer().getBalance(), "Pengene ble ikke lagt til eier");

        monopol.otherPlayer();monopol.move(6);
        assertThrows(IllegalStateException.class, () -> {
            monopol.payRent();
        }, "Kan ikke betale leie til deg selv");
        
        monopol.otherPlayer();monopol.move(9);monopol.buyProperty();monopol.otherPlayer();
        monopol.move(2);

        monopol.payRent();
        assertEquals(70, monopol.getCurrentPlayer().getBalance(),"Dobbel leie ble ikke betalt");
        assertEquals(230, monopol.getOtherPlayer().getBalance(),"Dobbel leie ble lagt til");

        monopol.otherPlayer();monopol.move(2);
        assertThrows(IllegalStateException.class, () -> {
            monopol.payRent();
        }, "Kan ikke betale leie som ikke finnes");

        monopol.otherPlayer();
        assertThrows(IllegalStateException.class, () -> {
            monopol.payRent();
        }, "Kan ikke betale uten å ha beveget deg");
    }

    @Test
    public void gameOverTest(){
        //payRent ender game
        monopol.getCurrentPlayer().removeFunds(450);
        monopol.move(4);
        assertTrue(monopol.getWinner()==null);
        monopol.payRent();
        assertFalse(monopol.getWinner()==null);
        //payTax ender game
        setup();
        monopol.getCurrentPlayer().removeFunds(450);
        monopol.move(2);
        assertTrue(monopol.getWinner()==null);
        monopol.payTax();
        assertFalse(monopol.getWinner()==null);
        //kan gå i 0 uten å ende gamet
        setup();
        monopol.getCurrentPlayer().removeFunds(400);
        monopol.move(2);
        assertTrue(monopol.getWinner()==null);
        monopol.payTax();
        assertTrue(monopol.getWinner()==null);

        setup();
        monopol.getCurrentPlayer().removeFunds(380);
        monopol.move(4);
        assertTrue(monopol.getWinner()==null);
        monopol.payRent();
        assertTrue(monopol.getWinner()==null);

    }

    @Test
    public void otherPlayerTest(){
        assertThrows(IllegalStateException.class, () -> {
            monopol.otherPlayer();
        }, "Kan ikke bytte spiller uten å ha beveget deg først");
        assertEquals(monopol.getPlayer(1), monopol.getOtherPlayer());
        assertNotEquals(monopol.getPlayer(1), monopol.getCurrentPlayer());
        assertEquals(monopol.getPlayer(2), monopol.getCurrentPlayer());
        monopol.move(1);monopol.otherPlayer();
        assertEquals(monopol.getPlayer(2), monopol.getOtherPlayer());
        assertEquals(monopol.getPlayer(1), monopol.getCurrentPlayer());
        
    }

    @Test
    public void getPropertyTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            monopol.getProperty(13);
        }, "Kan ikke hente ut en ikke-eksisterende indeks");
        assertThrows(IllegalArgumentException.class, () -> {
            monopol.getProperty(-2);
        }, "Kan ikke hente ut en ikke-eksisterende indeks");
        
    }

    @Test
    public void sameColorOwnedTest(){
        assertFalse(monopol.sameColorOwned(monopol.getProperty(2)));
        assertFalse(monopol.sameColorOwned(monopol.getProperty(1)));
        assertFalse(monopol.sameColorOwned(monopol.getProperty(11)));
        assertEquals(60, monopol.getProperty(4).getRent());
        assertEquals(80, monopol.getProperty(5).getRent());
        monopol.move(1);monopol.buyProperty();
        assertTrue(monopol.sameColorOwned(monopol.getProperty(4)));
        assertEquals(150, monopol.getProperty(4).getRent());
        assertEquals(200, monopol.getProperty(5).getRent());
    }





}
