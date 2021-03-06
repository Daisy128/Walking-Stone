package stonemoving.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoneStateTest {

    @Test
    void testIsSolved() {
        assertTrue(new StoneState(7,7).isSolved());
        assertFalse(new StoneState(0,0).isSolved());
    }

    @Test
    void testCanBeMoved() {
        StoneState state = new StoneState();
        assertFalse(state.canBeMoved(1,1));
        assertFalse(state.canBeMoved(0,2));
        assertFalse(state.canBeMoved(2,0));
        assertFalse(state.canBeMoved(0,0));
        assertTrue(state.canBeMoved(0,4));
        assertTrue(state.canBeMoved(4,0));
    }

    @Test
    void testMoveToNext() {
        StoneState state = new StoneState();
        StoneState current1 = new StoneState(0,1);
        StoneState current2 = new StoneState(0,2) ;
        state.moveToNext(0,1);
        assertEquals(state, current1);
        state.moveToNext(0,2);
        assertEquals(state,current2);
    }

}