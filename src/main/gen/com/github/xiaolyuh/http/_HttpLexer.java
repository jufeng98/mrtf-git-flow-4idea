// Generated by JFlex 1.9.1 http://jflex.de/  (tweaked for IntelliJ platform)
// source: _HttpLexer.flex

package com.github.xiaolyuh.http;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.github.xiaolyuh.http.psi.HttpTypes.*;


public class _HttpLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0, 0
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\25\u0100\1\u0200\11\u0100\1\u0300\17\u0100\1\u0400\35\u0100"+
    "\121\u0500\1\u0600\70\u0100\10\u0700\37\u0100\1\u0800\u1000\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\4\2\22\0\1\3\1\4\1\5\1\6"+
    "\1\7\1\10\1\11\1\12\2\7\1\5\1\13\1\7"+
    "\1\14\1\15\1\16\12\17\1\20\1\21\1\22\1\7"+
    "\1\23\1\24\1\25\3\26\1\27\1\30\1\31\1\32"+
    "\4\31\1\33\2\31\1\34\1\35\2\31\1\36\1\37"+
    "\1\40\5\31\1\41\1\42\1\12\1\0\1\43\1\0"+
    "\7\44\1\45\7\44\1\46\2\44\1\47\1\50\2\44"+
    "\1\51\3\44\1\52\1\53\1\54\1\13\6\0\1\2"+
    "\32\0\1\1\66\0\1\12\u01a8\0\1\1\177\0\13\1"+
    "\21\0\2\12\12\0\2\2\5\0\1\1\57\0\1\1"+
    "\240\0\1\1\1\12\16\0\2\12\356\0\u01a6\55\132\0"+
    "\u0100\56\1\0\1\12\6\0\2\12\2\0\1\12\15\0"+
    "\2\12\344\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[2304];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\1\1\2\1\3\1\4\1\1\3\2\1\5\1\6"+
    "\5\1\1\7\4\1\3\0\2\10\1\11\1\12\6\1"+
    "\5\10\1\1\1\0\1\1\2\13\1\14\1\15\1\14"+
    "\2\10\1\1\1\16\1\1\1\17\1\10\1\20\1\0"+
    "\2\20\3\10\1\1\1\0\1\1\1\21\1\1\1\0"+
    "\3\20\2\14\1\22\1\1\1\23";

  private static int [] zzUnpackAction() {
    int [] result = new int[74];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\57\0\136\0\215\0\274\0\353\0\u011a\0\u0149"+
    "\0\u0178\0\u01a7\0\u01d6\0\u0205\0\u0234\0\u0263\0\u0292\0\u02c1"+
    "\0\u02f0\0\u031f\0\u034e\0\u037d\0\u03ac\0\u03db\0\u040a\0\u011a"+
    "\0\u0439\0\u0468\0\57\0\u0497\0\u04c6\0\u04f5\0\u0524\0\u0553"+
    "\0\u0582\0\u05b1\0\u05e0\0\u02f0\0\u060f\0\u063e\0\u066d\0\u069c"+
    "\0\u06cb\0\u03ac\0\u06fa\0\u03db\0\u0729\0\u04f5\0\u0758\0\u0497"+
    "\0\u0787\0\u0234\0\u07b6\0\u0234\0\u07e5\0\57\0\u0814\0\u0814"+
    "\0\u0843\0\u0872\0\u08a1\0\u08d0\0\u08ff\0\u092e\0\u095d\0\u0234"+
    "\0\u0843\0\u098c\0\u03db\0\u08ff\0\u098c\0\u098c\0\u08ff\0\u069c"+
    "\0\u09bb\0\u0234";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[74];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\3\3\2\2\1\4\1\5\1\2\1\5\2\2"+
    "\1\6\1\7\1\10\2\5\1\2\1\11\1\12\2\2"+
    "\1\13\1\14\1\13\1\15\1\16\2\15\1\17\3\15"+
    "\1\20\2\2\1\21\1\22\3\21\1\23\1\24\1\2"+
    "\1\5\2\2\60\0\3\3\53\0\2\4\1\0\53\4"+
    "\10\0\1\5\1\0\1\5\5\0\2\5\5\0\13\5"+
    "\3\0\7\5\1\0\1\5\16\0\1\25\3\0\1\26"+
    "\5\0\13\27\3\0\6\27\21\0\4\30\6\0\13\30"+
    "\1\0\10\30\3\0\1\30\15\0\2\30\1\31\1\30"+
    "\6\0\13\30\1\0\10\30\3\0\1\30\5\0\1\32"+
    "\21\0\13\32\3\0\6\32\30\0\1\33\42\0\1\5"+
    "\1\0\1\5\2\0\1\27\2\0\1\5\1\34\5\0"+
    "\13\15\3\0\6\15\1\5\1\0\1\5\11\0\1\5"+
    "\1\0\1\5\2\0\1\27\2\0\1\5\1\34\5\0"+
    "\2\15\1\35\10\15\3\0\6\15\1\5\1\0\1\5"+
    "\11\0\1\5\1\0\1\5\2\0\1\27\2\0\1\5"+
    "\1\36\5\0\13\15\3\0\6\15\1\5\1\0\1\5"+
    "\11\0\1\5\1\0\1\5\2\0\1\27\2\0\1\5"+
    "\1\36\5\0\2\15\1\37\10\15\3\0\6\15\1\5"+
    "\1\0\1\5\11\0\1\5\1\0\1\5\2\0\1\27"+
    "\2\0\1\5\1\36\5\0\6\15\1\40\3\15\1\41"+
    "\3\0\6\15\1\5\1\0\1\5\3\0\5\20\1\0"+
    "\1\20\1\0\10\20\3\0\1\20\1\0\25\20\1\0"+
    "\2\20\10\0\1\5\1\0\1\5\2\0\1\42\2\30"+
    "\1\43\1\36\5\0\13\44\1\0\2\30\6\44\1\5"+
    "\1\0\1\5\1\30\10\0\1\5\1\0\1\5\2\0"+
    "\1\42\2\30\1\43\1\36\5\0\13\44\1\0\2\30"+
    "\4\44\1\45\1\44\1\5\1\0\1\5\1\30\10\0"+
    "\1\5\1\0\1\5\2\0\1\42\2\30\1\43\1\36"+
    "\5\0\13\44\1\0\2\30\3\44\1\46\2\44\1\5"+
    "\1\0\1\5\1\30\2\0\5\20\1\0\1\47\1\50"+
    "\1\47\5\20\2\47\3\0\1\20\1\0\13\47\3\20"+
    "\6\47\1\51\1\0\1\47\1\20\15\0\1\52\2\0"+
    "\1\53\1\26\5\0\13\52\3\0\6\52\10\0\1\54"+
    "\1\0\1\54\1\0\2\54\3\0\6\54\4\0\13\54"+
    "\1\0\11\54\1\0\2\54\15\0\1\27\3\0\1\26"+
    "\5\0\13\27\3\0\6\27\5\0\2\55\1\0\11\55"+
    "\4\31\6\55\13\31\1\55\10\31\3\55\1\31\4\0"+
    "\2\32\7\0\1\32\2\0\1\32\6\0\13\32\3\0"+
    "\6\32\3\0\1\32\4\0\1\54\1\0\1\54\1\0"+
    "\1\56\1\54\1\5\2\0\3\57\1\60\1\56\1\54"+
    "\4\0\13\60\1\0\2\57\6\60\1\56\1\0\1\56"+
    "\1\57\10\0\1\5\1\0\1\5\2\0\1\27\2\0"+
    "\1\5\1\36\5\0\5\15\1\61\5\15\3\0\6\15"+
    "\1\5\1\0\1\5\5\0\1\54\1\0\1\54\1\0"+
    "\1\56\1\54\1\5\2\0\3\54\2\56\1\54\4\0"+
    "\13\56\1\0\2\54\7\56\1\0\1\56\1\54\10\0"+
    "\1\5\1\0\1\5\2\0\1\27\2\0\1\5\1\36"+
    "\5\0\11\15\1\62\1\15\3\0\6\15\1\5\1\0"+
    "\1\5\11\0\1\5\1\0\1\5\2\0\1\27\2\0"+
    "\1\5\1\36\5\0\10\15\1\63\2\15\3\0\6\15"+
    "\1\5\1\0\1\5\11\0\1\5\1\0\1\5\2\0"+
    "\1\27\2\0\1\5\1\36\5\0\11\15\1\64\1\15"+
    "\3\0\6\15\1\5\1\0\1\5\16\0\1\42\3\30"+
    "\1\26\5\0\13\42\1\0\2\30\6\42\3\0\1\30"+
    "\10\0\1\5\1\0\1\5\2\0\3\30\1\43\1\5"+
    "\5\0\13\43\1\0\2\30\6\43\1\5\1\0\1\5"+
    "\1\30\10\0\1\5\1\0\1\5\2\0\1\42\2\30"+
    "\1\43\1\36\5\0\13\44\1\0\2\30\4\44\1\65"+
    "\1\44\1\5\1\0\1\5\1\30\4\0\1\66\1\67"+
    "\1\0\1\70\1\71\1\70\1\71\1\0\1\70\1\72"+
    "\2\73\1\74\1\75\1\67\2\0\1\67\1\70\13\46"+
    "\1\0\1\30\1\73\6\46\1\71\1\70\1\71\1\30"+
    "\2\0\5\20\1\0\1\47\1\0\1\47\5\20\2\47"+
    "\3\0\1\20\1\0\13\47\3\20\7\47\1\0\1\47"+
    "\1\20\1\0\6\50\1\0\1\50\1\76\46\50\1\0"+
    "\5\20\1\0\1\47\1\0\1\47\5\20\2\47\3\0"+
    "\1\20\1\67\13\47\3\20\7\47\1\0\1\47\1\20"+
    "\15\0\1\53\2\0\1\53\6\0\13\53\3\0\6\53"+
    "\5\0\2\55\1\0\53\55\4\0\1\54\1\0\1\54"+
    "\1\0\2\54\3\0\4\57\2\54\4\0\13\57\1\0"+
    "\10\57\1\54\1\0\1\54\1\57\10\0\1\5\1\0"+
    "\1\5\2\0\1\27\2\0\1\5\1\36\5\0\2\15"+
    "\1\77\10\15\3\0\6\15\1\5\1\0\1\5\11\0"+
    "\1\5\1\0\1\5\2\0\1\27\2\0\1\5\1\36"+
    "\5\0\11\15\1\100\1\15\3\0\6\15\1\5\1\0"+
    "\1\5\11\0\1\5\1\0\1\5\2\0\1\42\2\30"+
    "\1\43\1\36\5\0\13\44\1\0\2\30\2\44\1\46"+
    "\3\44\1\5\1\0\1\5\1\30\4\0\1\66\1\67"+
    "\1\0\4\70\1\0\2\70\1\67\2\70\2\67\2\0"+
    "\1\67\14\70\2\0\12\70\5\0\1\66\1\67\1\0"+
    "\1\70\1\71\1\70\1\71\1\0\2\70\1\67\1\70"+
    "\1\71\1\101\1\67\2\0\1\67\1\70\13\71\2\0"+
    "\1\70\7\71\1\70\1\71\5\0\1\66\1\67\1\0"+
    "\4\70\1\0\1\70\1\72\3\73\1\102\1\67\2\0"+
    "\1\67\1\70\13\72\1\0\1\30\1\73\6\72\3\70"+
    "\1\30\4\0\1\66\1\67\1\0\4\70\1\0\1\70"+
    "\4\73\2\67\2\0\1\67\1\70\13\73\1\0\1\30"+
    "\7\73\3\70\1\30\4\0\1\66\1\67\1\0\1\70"+
    "\1\71\1\70\1\71\1\0\1\70\3\73\1\74\1\101"+
    "\1\67\2\0\1\67\1\70\13\74\1\0\1\30\1\73"+
    "\6\74\1\71\1\70\1\71\1\30\4\0\1\103\1\67"+
    "\1\54\1\70\1\104\1\105\1\71\1\0\1\70\1\105"+
    "\1\106\1\105\1\104\1\107\1\106\2\0\1\67\1\70"+
    "\13\104\1\0\1\54\1\105\7\104\1\70\1\104\1\54"+
    "\1\0\6\50\1\0\1\50\1\76\43\50\1\110\2\50"+
    "\7\0\1\5\1\0\1\5\2\0\1\27\2\0\1\5"+
    "\1\36\5\0\11\15\1\111\1\15\3\0\6\15\1\5"+
    "\1\0\1\5\5\0\1\103\1\67\1\54\1\70\2\105"+
    "\1\70\1\0\1\70\1\105\1\106\2\105\2\106\2\0"+
    "\1\67\1\70\13\105\1\0\1\54\10\105\1\70\1\105"+
    "\1\54\10\0\1\5\1\0\1\5\2\0\1\27\2\0"+
    "\1\5\1\36\5\0\2\15\1\112\10\15\3\0\6\15"+
    "\1\5\1\0\1\5\2\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[2538];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\1\1\11\22\1\3\0\3\1\1\11\14\1\1\0"+
    "\15\1\1\11\1\0\6\1\1\0\3\1\1\0\10\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[74];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** Number of newlines encountered up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  @SuppressWarnings("unused")
  protected int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
  public _HttpLexer() {
    this((java.io.Reader)null);
  }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public _HttpLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { return URL_FORM_ENCODE;
            }
          // fall through
          case 20: break;
          case 2:
            { return BAD_CHARACTER;
            }
          // fall through
          case 21: break;
          case 3:
            { return WHITE_SPACE;
            }
          // fall through
          case 22: break;
          case 4:
            { return REQUEST_COMMENT;
            }
          // fall through
          case 23: break;
          case 5:
            { return T_LT;
            }
          // fall through
          case 24: break;
          case 6:
            { return T_RT;
            }
          // fall through
          case 25: break;
          case 7:
            { return JSON_TEXT;
            }
          // fall through
          case 26: break;
          case 8:
            { return PATH;
            }
          // fall through
          case 27: break;
          case 9:
            { return XML_TEXT;
            }
          // fall through
          case 28: break;
          case 10:
            { return T_RT_DBL;
            }
          // fall through
          case 29: break;
          case 11:
            { return MULTIPART_SEPERATE;
            }
          // fall through
          case 30: break;
          case 12:
            { return HEADER_DESC;
            }
          // fall through
          case 31: break;
          case 13:
            { return LINE_COMMENT;
            }
          // fall through
          case 32: break;
          case 14:
            { return GET;
            }
          // fall through
          case 33: break;
          case 15:
            { return PUT;
            }
          // fall through
          case 34: break;
          case 16:
            { return URL_DESC;
            }
          // fall through
          case 35: break;
          case 17:
            { return POST;
            }
          // fall through
          case 36: break;
          case 18:
            { return JS_SCRIPT;
            }
          // fall through
          case 37: break;
          case 19:
            { return DELETE;
            }
          // fall through
          case 38: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
