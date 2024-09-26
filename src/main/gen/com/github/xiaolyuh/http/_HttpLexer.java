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
    "\121\u0500\1\u0600\70\u0100\10\u0700\36\u0100\1\u0800\1\u0900\u1000\u0100";

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
    "\11\0\1\1\1\2\2\3\1\2\22\0\1\4\1\5"+
    "\1\6\1\7\1\10\1\11\1\12\1\13\2\14\1\15"+
    "\1\16\1\10\1\17\1\20\1\21\12\22\1\23\1\24"+
    "\1\25\1\26\1\27\1\30\1\31\3\32\1\33\1\34"+
    "\1\35\1\36\4\35\1\37\2\35\1\40\1\41\2\35"+
    "\1\42\1\43\1\44\5\35\1\45\1\46\1\47\1\0"+
    "\1\50\1\51\7\52\1\53\7\52\1\54\2\52\1\55"+
    "\1\56\2\52\1\57\3\52\1\60\1\61\1\62\1\63"+
    "\6\0\1\3\32\0\1\1\26\0\1\51\37\0\1\51"+
    "\37\0\1\51\u0188\0\1\1\177\0\13\1\11\0\1\51"+
    "\7\0\2\51\12\0\2\3\5\0\1\1\57\0\1\1"+
    "\240\0\1\1\2\51\7\0\2\51\4\0\2\51\356\0"+
    "\u01a6\64\132\0\u0100\65\143\0\3\51\233\0\1\51\6\0"+
    "\2\51\1\0\1\51\1\13\1\51\14\0\3\51\1\0"+
    "\2\51\340\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[2560];
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
    "\1\2\5\1\1\2\4\1\3\0\2\7\1\10\1\11"+
    "\1\0\6\1\1\12\5\7\1\0\2\1\2\13\1\14"+
    "\1\15\1\16\1\14\2\7\1\1\1\17\1\1\1\20"+
    "\1\7\1\21\1\0\2\21\3\7\1\1\1\0\1\1"+
    "\1\22\1\0\3\21\2\14\1\23\1\1\1\24";

  private static int [] zzUnpackAction() {
    int [] result = new int[77];
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
    "\0\0\0\66\0\154\0\242\0\330\0\u010e\0\u0144\0\u017a"+
    "\0\u01b0\0\u01e6\0\u021c\0\u0252\0\u0288\0\u02be\0\u02f4\0\u032a"+
    "\0\u0360\0\u0396\0\u03cc\0\u0402\0\u0438\0\u046e\0\u04a4\0\u04da"+
    "\0\u0144\0\u0510\0\u0546\0\66\0\u057c\0\u05b2\0\u05e8\0\u061e"+
    "\0\u0654\0\u068a\0\u06c0\0\u06f6\0\u072c\0\u0762\0\u0396\0\u0798"+
    "\0\u07ce\0\u0804\0\u083a\0\u0870\0\u046e\0\u08a6\0\u04a4\0\u08dc"+
    "\0\u057c\0\u061e\0\u0912\0\u05b2\0\u0948\0\u02be\0\u097e\0\u02be"+
    "\0\u09b4\0\66\0\u09ea\0\u09ea\0\u0870\0\u0a20\0\u0a56\0\u0a8c"+
    "\0\u0ac2\0\u0af8\0\u0b2e\0\u02be\0\u0b64\0\u04a4\0\u0ac2\0\u0b64"+
    "\0\u0b64\0\u0ac2\0\u0804\0\u0b9a\0\u02be";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[77];
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
    "\1\2\4\3\2\2\1\4\1\5\1\2\1\5\1\2"+
    "\1\5\2\2\1\6\1\7\1\10\2\5\1\2\1\11"+
    "\1\5\1\12\1\2\1\13\1\14\1\15\1\14\1\16"+
    "\1\17\2\16\1\20\3\16\1\21\4\2\1\22\1\23"+
    "\3\22\1\24\1\25\1\2\1\5\3\2\67\0\4\3"+
    "\61\0\2\4\2\0\61\4\11\0\1\5\1\0\1\5"+
    "\1\0\1\5\5\0\2\5\2\0\1\5\3\0\13\5"+
    "\5\0\7\5\1\0\1\5\22\0\1\26\3\0\1\27"+
    "\6\0\13\30\5\0\6\30\25\0\4\31\7\0\13\31"+
    "\1\0\1\31\1\0\1\31\1\0\6\31\4\0\1\31"+
    "\20\0\2\31\1\32\1\31\7\0\13\31\1\0\1\31"+
    "\1\0\1\31\1\0\6\31\4\0\1\31\6\0\1\33"+
    "\24\0\13\33\5\0\6\33\35\0\1\34\70\0\13\35"+
    "\5\0\6\35\16\0\1\5\1\0\1\5\1\0\1\5"+
    "\2\0\1\30\2\0\1\5\1\36\2\0\1\5\3\0"+
    "\13\16\5\0\6\16\1\5\1\0\1\5\13\0\1\5"+
    "\1\0\1\5\1\0\1\5\2\0\1\30\2\0\1\5"+
    "\1\36\2\0\1\5\3\0\2\16\1\37\10\16\5\0"+
    "\6\16\1\5\1\0\1\5\13\0\1\5\1\0\1\5"+
    "\1\0\1\5\2\0\1\30\2\0\1\5\1\40\2\0"+
    "\1\5\3\0\13\16\5\0\6\16\1\5\1\0\1\5"+
    "\13\0\1\5\1\0\1\5\1\0\1\5\2\0\1\30"+
    "\2\0\1\5\1\40\2\0\1\5\3\0\2\16\1\41"+
    "\10\16\5\0\6\16\1\5\1\0\1\5\13\0\1\5"+
    "\1\0\1\5\1\0\1\5\2\0\1\30\2\0\1\5"+
    "\1\40\2\0\1\5\3\0\6\16\1\42\3\16\1\43"+
    "\5\0\6\16\1\5\1\0\1\5\5\0\1\44\1\0"+
    "\1\44\1\0\1\44\13\0\1\44\7\0\13\44\2\0"+
    "\1\44\2\0\6\44\16\0\1\5\1\0\1\5\1\0"+
    "\1\5\2\0\1\45\2\31\1\46\1\40\2\0\1\5"+
    "\3\0\13\47\1\0\1\31\1\0\1\31\1\0\6\47"+
    "\1\5\1\0\1\5\1\0\1\31\11\0\1\5\1\0"+
    "\1\5\1\0\1\5\2\0\1\45\2\31\1\46\1\40"+
    "\2\0\1\5\3\0\13\47\1\0\1\31\1\0\1\31"+
    "\1\0\4\47\1\50\1\47\1\5\1\0\1\5\1\0"+
    "\1\31\11\0\1\5\1\0\1\5\1\0\1\5\2\0"+
    "\1\45\2\31\1\46\1\40\2\0\1\5\3\0\13\47"+
    "\1\0\1\31\1\0\1\31\1\0\3\47\1\51\2\47"+
    "\1\5\1\0\1\5\1\0\1\31\3\0\1\44\1\0"+
    "\1\44\1\0\1\44\1\0\1\5\1\52\1\5\1\0"+
    "\1\5\5\0\1\53\1\5\2\0\1\5\3\0\13\53"+
    "\5\0\6\53\1\54\1\0\1\53\22\0\1\55\2\0"+
    "\1\56\1\27\6\0\13\55\5\0\6\55\12\0\1\57"+
    "\1\0\1\57\1\0\2\57\2\0\2\57\1\0\6\57"+
    "\1\0\1\57\3\0\13\57\1\0\1\57\1\0\1\57"+
    "\1\0\7\57\1\0\1\57\1\0\1\57\20\0\1\30"+
    "\3\0\1\27\6\0\13\30\5\0\6\30\6\0\2\60"+
    "\2\0\13\60\4\32\7\60\13\32\1\60\1\32\1\60"+
    "\1\32\1\60\6\32\4\60\1\32\3\0\1\33\1\0"+
    "\4\33\1\0\4\33\1\0\13\33\1\0\13\33\3\0"+
    "\1\33\1\0\7\33\1\0\1\33\1\0\1\33\23\0"+
    "\1\61\3\0\1\61\3\0\13\61\5\0\7\61\1\0"+
    "\1\61\7\0\1\57\1\0\1\57\1\0\1\62\1\57"+
    "\1\5\1\0\1\62\1\57\1\0\3\63\1\64\1\62"+
    "\1\57\1\0\1\62\3\0\13\64\1\0\1\63\1\0"+
    "\1\63\1\0\6\64\1\62\1\0\1\62\1\0\1\63"+
    "\11\0\1\5\1\0\1\5\1\0\1\5\2\0\1\30"+
    "\2\0\1\5\1\40\2\0\1\5\3\0\5\16\1\65"+
    "\5\16\5\0\6\16\1\5\1\0\1\5\7\0\1\57"+
    "\1\0\1\57\1\0\1\62\1\57\1\5\1\0\1\62"+
    "\1\57\1\0\3\57\2\62\1\57\1\0\1\62\3\0"+
    "\13\62\1\0\1\57\1\0\1\57\1\0\7\62\1\0"+
    "\1\62\1\0\1\57\11\0\1\5\1\0\1\5\1\0"+
    "\1\5\2\0\1\30\2\0\1\5\1\40\2\0\1\5"+
    "\3\0\11\16\1\66\1\16\5\0\6\16\1\5\1\0"+
    "\1\5\13\0\1\5\1\0\1\5\1\0\1\5\2\0"+
    "\1\30\2\0\1\5\1\40\2\0\1\5\3\0\10\16"+
    "\1\67\2\16\5\0\6\16\1\5\1\0\1\5\13\0"+
    "\1\5\1\0\1\5\1\0\1\5\2\0\1\30\2\0"+
    "\1\5\1\40\2\0\1\5\3\0\11\16\1\70\1\16"+
    "\5\0\6\16\1\5\1\0\1\5\4\0\6\44\1\0"+
    "\51\44\1\0\3\44\20\0\1\45\3\31\1\27\6\0"+
    "\13\45\1\0\1\31\1\0\1\31\1\0\6\45\4\0"+
    "\1\31\11\0\1\5\1\0\1\5\1\0\1\5\2\0"+
    "\3\31\1\46\1\5\2\0\1\5\3\0\13\46\1\0"+
    "\1\31\1\0\1\31\1\0\6\46\1\5\1\0\1\5"+
    "\1\0\1\31\11\0\1\5\1\0\1\5\1\0\1\5"+
    "\2\0\1\45\2\31\1\46\1\40\2\0\1\5\3\0"+
    "\13\47\1\0\1\31\1\0\1\31\1\0\4\47\1\71"+
    "\1\47\1\5\1\0\1\5\1\0\1\31\5\0\1\72"+
    "\1\73\1\0\1\74\1\75\1\74\1\75\1\0\1\75"+
    "\1\0\1\74\1\76\2\77\1\100\1\101\1\73\1\0"+
    "\1\75\1\0\1\73\1\0\13\51\1\0\1\31\1\0"+
    "\1\77\1\0\6\51\1\75\1\74\1\75\1\74\1\31"+
    "\1\0\7\52\1\0\1\52\1\102\54\52\1\0\6\44"+
    "\1\0\1\53\1\44\1\53\1\44\1\53\5\44\2\53"+
    "\2\44\1\53\3\44\13\53\5\44\7\53\1\0\1\53"+
    "\2\44\5\0\1\72\1\73\1\0\1\74\1\75\1\74"+
    "\1\75\1\0\1\75\1\0\2\74\1\73\1\74\1\75"+
    "\1\54\1\73\1\0\1\75\1\0\1\73\1\0\13\75"+
    "\3\0\1\74\1\0\7\75\1\74\1\75\1\74\21\0"+
    "\1\56\2\0\1\56\7\0\13\56\5\0\6\56\6\0"+
    "\2\60\2\0\61\60\5\0\1\57\1\0\1\57\1\0"+
    "\2\57\2\0\2\57\1\0\4\63\2\57\1\0\1\57"+
    "\3\0\13\63\1\0\1\63\1\0\1\63\1\0\6\63"+
    "\1\57\1\0\1\57\1\0\1\63\11\0\1\5\1\0"+
    "\1\5\1\0\1\5\2\0\1\30\2\0\1\5\1\40"+
    "\2\0\1\5\3\0\2\16\1\103\10\16\5\0\6\16"+
    "\1\5\1\0\1\5\13\0\1\5\1\0\1\5\1\0"+
    "\1\5\2\0\1\30\2\0\1\5\1\40\2\0\1\5"+
    "\3\0\11\16\1\104\1\16\5\0\6\16\1\5\1\0"+
    "\1\5\13\0\1\5\1\0\1\5\1\0\1\5\2\0"+
    "\1\45\2\31\1\46\1\40\2\0\1\5\3\0\13\47"+
    "\1\0\1\31\1\0\1\31\1\0\2\47\1\51\3\47"+
    "\1\5\1\0\1\5\1\0\1\31\5\0\1\72\1\73"+
    "\1\0\4\74\1\0\1\74\1\0\2\74\1\73\2\74"+
    "\2\73\1\0\1\74\1\0\1\73\1\0\13\74\3\0"+
    "\1\74\1\0\12\74\6\0\1\72\1\73\1\0\4\74"+
    "\1\0\1\74\1\0\1\74\1\76\3\77\1\105\1\73"+
    "\1\0\1\74\1\0\1\73\1\0\13\76\1\0\1\31"+
    "\1\0\1\77\1\0\6\76\4\74\1\31\5\0\1\72"+
    "\1\73\1\0\4\74\1\0\1\74\1\0\1\74\4\77"+
    "\2\73\1\0\1\74\1\0\1\73\1\0\13\77\1\0"+
    "\1\31\1\0\1\77\1\0\6\77\4\74\1\31\5\0"+
    "\1\72\1\73\1\0\1\74\1\75\1\74\1\75\1\0"+
    "\1\75\1\0\1\74\3\77\1\100\1\54\1\73\1\0"+
    "\1\75\1\0\1\73\1\0\13\100\1\0\1\31\1\0"+
    "\1\77\1\0\6\100\1\75\1\74\1\75\1\74\1\31"+
    "\5\0\1\106\1\73\1\57\1\74\1\107\1\110\1\75"+
    "\1\0\1\107\1\57\1\74\1\110\1\111\1\110\1\107"+
    "\1\112\1\111\1\0\1\107\1\0\1\73\1\0\13\107"+
    "\1\0\1\57\1\0\1\110\1\0\7\107\1\74\1\107"+
    "\1\74\1\57\1\0\7\52\1\0\1\52\1\102\50\52"+
    "\1\113\3\52\10\0\1\5\1\0\1\5\1\0\1\5"+
    "\2\0\1\30\2\0\1\5\1\40\2\0\1\5\3\0"+
    "\11\16\1\114\1\16\5\0\6\16\1\5\1\0\1\5"+
    "\7\0\1\106\1\73\1\57\1\74\2\110\1\74\1\0"+
    "\1\110\1\57\1\74\1\110\1\111\2\110\2\111\1\0"+
    "\1\110\1\0\1\73\1\0\13\110\1\0\1\57\1\0"+
    "\1\110\1\0\7\110\1\74\1\110\1\74\1\57\11\0"+
    "\1\5\1\0\1\5\1\0\1\5\2\0\1\30\2\0"+
    "\1\5\1\40\2\0\1\5\3\0\2\16\1\115\10\16"+
    "\5\0\6\16\1\5\1\0\1\5\3\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[3024];
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
    "\1\1\1\11\23\1\3\0\3\1\1\11\1\0\14\1"+
    "\1\0\17\1\1\11\1\0\6\1\1\0\2\1\1\0"+
    "\10\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[77];
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
          case 21: break;
          case 2:
            { return BAD_CHARACTER;
            }
          // fall through
          case 22: break;
          case 3:
            { return WHITE_SPACE;
            }
          // fall through
          case 23: break;
          case 4:
            { return REQUEST_COMMENT;
            }
          // fall through
          case 24: break;
          case 5:
            { return T_LT;
            }
          // fall through
          case 25: break;
          case 6:
            { return T_RT;
            }
          // fall through
          case 26: break;
          case 7:
            { return PATH;
            }
          // fall through
          case 27: break;
          case 8:
            { return XML_TEXT;
            }
          // fall through
          case 28: break;
          case 9:
            { return T_RT_DBL;
            }
          // fall through
          case 29: break;
          case 10:
            { return JSON_TEXT;
            }
          // fall through
          case 30: break;
          case 11:
            { return MULTIPART_SEPERATE;
            }
          // fall through
          case 31: break;
          case 12:
            { return HEADER_DESC;
            }
          // fall through
          case 32: break;
          case 13:
            { return LINE_COMMENT;
            }
          // fall through
          case 33: break;
          case 14:
            { return VARIABLE_DEFINE;
            }
          // fall through
          case 34: break;
          case 15:
            { return GET;
            }
          // fall through
          case 35: break;
          case 16:
            { return PUT;
            }
          // fall through
          case 36: break;
          case 17:
            { return URL_DESC;
            }
          // fall through
          case 37: break;
          case 18:
            { return POST;
            }
          // fall through
          case 38: break;
          case 19:
            { return JS_SCRIPT;
            }
          // fall through
          case 39: break;
          case 20:
            { return DELETE;
            }
          // fall through
          case 40: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
