package stonemoving.state;

/**
 * Separate all the possible path into 3 kinds to make settings.
 */
public enum Board {
    /**
     * The value is 0.
     */
    STONE,
    /**
     * The value is 1.
    */
    UNFRAMED,

    /**
     * The value is 2.
      */
    FRAMED,

    DIREC;

    /**
     * Returns the instance represented by the value specified.
      * @param value which represent an instance.
     * @return the instance specified.
     */
    public static Board of(int value) {
        if (value < 0 || value >= values().length) {
            throw new IllegalArgumentException();
        }
        return values()[value];
    }

    /**
     * Get the value that specifies an instance.
      * @return the value that specifies an instance.
     */
    public int getValue() {
        return ordinal();
    }

    /**
     * Change the instance to string.
      * @return the string name of the instance.
     */
    public String toString() {
        return Integer.toString(ordinal());
    }

}