package stonemoving.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testOf() {
        assertThrows(IllegalArgumentException.class, () -> Board.of(-1));
        assertEquals(Board.STONE, Board.of(0));
        assertEquals(Board.UNFRAMED, Board.of(1));
        assertEquals(Board.FRAMED, Board.of(2));
        assertEquals(Board.DIREC, Board.of(3));
        assertThrows(IllegalArgumentException.class, () -> Board.of(4));
    }

    @Test
    void testToString() {
        assertEquals("0",Board.STONE.toString());
        assertEquals("1",Board.UNFRAMED.toString());
        assertEquals("2",Board.FRAMED.toString());
    }
}