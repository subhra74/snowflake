package com.jediterm.terminal.emulator.charset;

/**
 * Provides an enum with names for the supported character sets.
 */
public enum CharacterSet
{
  ASCII( 'B' )
    {
      @Override
      public int map( int index )
      {
        return -1;
      }
    },
  BRITISH( 'A' )
    {
      @Override
      public int map( int index )
      {
        if ( index == 3 )
        {
          // Pound sign...
          return '\u00a3';
        }
        return -1;
      }
    },
  DANISH( 'E', '6' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 32:
            return '\u00c4';
          case 59:
            return '\u00c6';
          case 60:
            return '\u00d8';
          case 61:
            return '\u00c5';
          case 62:
            return '\u00dc';
          case 64:
            return '\u00e4';
          case 91:
            return '\u00e6';
          case 92:
            return '\u00f8';
          case 93:
            return '\u00e5';
          case 94:
            return '\u00fc';
          default:
            return -1;
        }
      }
    },
  DEC_SPECIAL_GRAPHICS( '0', '2' )
    {
      @Override
      public int map( int index )
      {
        if ( index >= 64 && index < 96 )
        {
          return ( ( Character )CharacterSets.DEC_SPECIAL_CHARS[index - 64][0] ).charValue();
        }
        return -1;
      }
    },
  DEC_SUPPLEMENTAL( 'U', '<' )
    {
      @Override
      public int map( int index )
      {
        if ( index >= 0 && index < 64 )
        {
          // Set the 8th bit...
          return index + 160;
        }
        return -1;
      }
    },
  DUTCH( '4' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 3:
            return '\u00a3';
          case 32:
            return '\u00be';
          case 59:
            return '\u0133';
          case 60:
            return '\u00bd';
          case 61:
            return '|';
          case 91:
            return '\u00a8';
          case 92:
            return '\u0192';
          case 93:
            return '\u00bc';
          case 94:
            return '\u00b4';
          default:
            return -1;
        }
      }
    },
  FINNISH( 'C', '5' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 59:
            return '\u00c4';
          case 60:
            return '\u00d4';
          case 61:
            return '\u00c5';
          case 62:
            return '\u00dc';
          case 64:
            return '\u00e9';
          case 91:
            return '\u00e4';
          case 92:
            return '\u00f6';
          case 93:
            return '\u00e5';
          case 94:
            return '\u00fc';
          default:
            return -1;
        }
      }
    },
  FRENCH( 'R' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 3:
            return '\u00a3';
          case 32:
            return '\u00e0';
          case 59:
            return '\u00b0';
          case 60:
            return '\u00e7';
          case 61:
            return '\u00a6';
          case 91:
            return '\u00e9';
          case 92:
            return '\u00f9';
          case 93:
            return '\u00e8';
          case 94:
            return '\u00a8';
          default:
            return -1;
        }
      }
    },
  FRENCH_CANADIAN( 'Q' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 32:
            return '\u00e0';
          case 59:
            return '\u00e2';
          case 60:
            return '\u00e7';
          case 61:
            return '\u00ea';
          case 62:
            return '\u00ee';
          case 91:
            return '\u00e9';
          case 92:
            return '\u00f9';
          case 93:
            return '\u00e8';
          case 94:
            return '\u00fb';
          default:
            return -1;
        }
      }
    },
  GERMAN( 'K' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 32:
            return '\u00a7';
          case 59:
            return '\u00c4';
          case 60:
            return '\u00d6';
          case 61:
            return '\u00dc';
          case 91:
            return '\u00e4';
          case 92:
            return '\u00f6';
          case 93:
            return '\u00fc';
          case 94:
            return '\u00df';
          default:
            return -1;
        }
      }
    },
  ITALIAN( 'Y' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 3:
            return '\u00a3';
          case 32:
            return '\u00a7';
          case 59:
            return '\u00ba';
          case 60:
            return '\u00e7';
          case 61:
            return '\u00e9';
          case 91:
            return '\u00e0';
          case 92:
            return '\u00f2';
          case 93:
            return '\u00e8';
          case 94:
            return '\u00ec';
          default:
            return -1;
        }
      }
    },
  SPANISH( 'Z' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 3:
            return '\u00a3';
          case 32:
            return '\u00a7';
          case 59:
            return '\u00a1';
          case 60:
            return '\u00d1';
          case 61:
            return '\u00bf';
          case 91:
            return '\u00b0';
          case 92:
            return '\u00f1';
          case 93:
            return '\u00e7';
          default:
            return -1;
        }
      }
    },
  SWEDISH( 'H', '7' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 32:
            return '\u00c9';
          case 59:
            return '\u00c4';
          case 60:
            return '\u00d6';
          case 61:
            return '\u00c5';
          case 62:
            return '\u00dc';
          case 64:
            return '\u00e9';
          case 91:
            return '\u00e4';
          case 92:
            return '\u00f6';
          case 93:
            return '\u00e5';
          case 94:
            return '\u00fc';
          default:
            return -1;
        }
      }
    },
  SWISS( '=' )
    {
      @Override
      public int map( int index )
      {
        switch ( index )
        {
          case 3:
            return '\u00f9';
          case 32:
            return '\u00e0';
          case 59:
            return '\u00e9';
          case 60:
            return '\u00e7';
          case 61:
            return '\u00ea';
          case 62:
            return '\u00ee';
          case 63:
            return '\u00e8';
          case 64:
            return '\u00f4';
          case 91:
            return '\u00e4';
          case 92:
            return '\u00f6';
          case 93:
            return '\u00fc';
          case 94:
            return '\u00fb';
          default:
            return -1;
        }
      }
    };

  private final int[] myDesignations;

  /**
   * Creates a new {@link CharacterSet} instance.
   *
   * @param designations
   *          the characters that designate this character set, cannot be
   *          <code>null</code>.
   */
  CharacterSet(int... designations)
  {
    myDesignations = designations;
  }

  // METHODS

  /**
   * Returns the {@link CharacterSet} for the given character.
   *
   * @param designation
   *          the character to translate to a {@link CharacterSet}.
   * @return a character set name corresponding to the given character,
   *         defaulting to ASCII if no mapping could be made.
   */
  public static CharacterSet valueOf( char designation )
  {
    for ( CharacterSet csn : values() )
    {
      if ( csn.isDesignation( designation ) )
      {
        return csn;
      }
    }
    return ASCII;
  }

  /**
   * Maps the character with the given index to a character in this character
   * set.
   *
   * @param index
   *          the index of the character set, >= 0 && < 128.
   * @return a mapped character, or -1 if no mapping could be made and the
   *         ASCII value should be used.
   */
  public abstract int map( int index );

  /**
   * Returns whether or not the given designation character belongs to this
   * character set's set of designations.
   *
   * @param designation
   *          the designation to test for.
   * @return <code>true</code> if the given designation character maps to this
   *         character set, <code>false</code> otherwise.
   */
  private boolean isDesignation( char designation )
  {
    for (int myDesignation : myDesignations) {
      if (myDesignation == designation) {
        return true;
      }
    }
    return false;
  }
}
