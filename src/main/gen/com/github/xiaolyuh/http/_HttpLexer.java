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
    "\1\0\1\u0100\1\u0200\1\u0300\1\u0400\1\u0500\1\u0600\1\u0700"+
    "\1\u0800\1\u0900\1\u0a00\1\u0b00\1\u0c00\1\u0d00\1\u0e00\1\u0f00"+
    "\1\u1000\1\u0100\1\u1100\1\u1200\1\u1300\1\u0100\1\u1400\1\u1500"+
    "\1\u1600\1\u1700\1\u1800\1\u1900\1\u1a00\1\u1b00\1\u0100\1\u1c00"+
    "\1\u1d00\1\u1e00\2\u1f00\1\u2000\1\u1f00\1\u2100\5\u1f00\1\u2200"+
    "\1\u2300\1\u2400\1\u1f00\1\u2500\1\u2600\2\u1f00\31\u0100\1\u2700"+
    "\121\u0100\1\u2800\4\u0100\1\u2900\1\u0100\1\u2a00\1\u2b00\1\u2c00"+
    "\1\u2d00\1\u2e00\1\u2f00\53\u0100\1\u3000\10\u3100\31\u1f00\1\u0100"+
    "\1\u3200\1\u3300\1\u0100\1\u3400\1\u3500\1\u3600\1\u3700\1\u3800"+
    "\1\u3900\1\u3a00\1\u3b00\1\u3c00\1\u0100\1\u3d00\1\u3e00\1\u3f00"+
    "\1\u4000\1\u4100\1\u4200\1\u4300\1\u1f00\1\u4400\1\u4500\1\u4600"+
    "\1\u4700\1\u4800\1\u4900\1\u4a00\1\u4b00\1\u4c00\1\u4d00\1\u4e00"+
    "\1\u4f00\1\u1f00\1\u5000\1\u5100\1\u5200\1\u1f00\3\u0100\1\u5300"+
    "\1\u5400\1\u5500\12\u1f00\4\u0100\1\u5600\17\u1f00\2\u0100\1\u5700"+
    "\41\u1f00\2\u0100\1\u5800\1\u5900\2\u1f00\1\u5a00\1\u5b00\27\u0100"+
    "\1\u5c00\2\u0100\1\u5d00\45\u1f00\1\u0100\1\u5e00\1\u5f00\11\u1f00"+
    "\1\u6000\24\u1f00\1\u6100\1\u6200\1\u1f00\1\u6300\1\u6400\1\u6500"+
    "\1\u6600\2\u1f00\1\u6700\5\u1f00\1\u6800\1\u6900\1\u6a00\5\u1f00"+
    "\1\u6b00\1\u6c00\4\u1f00\1\u6d00\2\u1f00\1\u6e00\16\u1f00\246\u0100"+
    "\1\u6f00\20\u0100\1\u7000\1\u7100\25\u0100\1\u7200\34\u0100\1\u7300"+
    "\14\u1f00\2\u0100\1\u7400\u0b06\u1f00\1\u2800\u02fe\u1f00";

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
    "\1\7\1\10\1\11\1\0\2\7\1\12\1\13\1\7"+
    "\1\14\1\15\1\16\12\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\3\27\1\30\1\31\1\32\1\33"+
    "\4\32\1\34\2\32\1\35\1\36\2\32\1\37\1\40"+
    "\1\41\5\32\1\42\1\43\1\44\1\0\1\45\1\0"+
    "\7\46\1\47\7\46\1\50\2\46\1\51\1\52\2\46"+
    "\1\53\3\46\1\54\1\13\1\55\1\13\6\0\1\2"+
    "\32\0\1\1\11\0\1\56\12\0\1\56\4\0\1\56"+
    "\5\0\27\56\1\0\37\56\1\0\u01ca\56\4\0\14\56"+
    "\16\0\5\56\7\0\1\56\1\0\1\56\21\0\165\56"+
    "\1\0\2\56\2\0\4\56\1\0\1\56\6\0\1\56"+
    "\1\0\3\56\1\0\1\56\1\0\24\56\1\0\123\56"+
    "\1\0\213\56\1\0\255\56\1\0\46\56\2\0\1\56"+
    "\6\0\51\56\10\0\55\56\1\0\1\56\1\0\2\56"+
    "\1\0\2\56\1\0\1\56\10\0\33\56\4\0\4\56"+
    "\35\0\13\56\5\0\100\56\12\57\4\0\146\56\1\0"+
    "\10\56\2\0\12\56\1\0\6\56\12\57\3\56\2\0"+
    "\1\56\20\0\73\56\2\0\145\56\16\0\12\57\54\56"+
    "\4\0\1\56\2\0\1\56\2\0\56\56\22\0\34\56"+
    "\4\0\13\56\65\0\25\56\1\0\10\56\25\0\17\56"+
    "\1\0\201\56\2\0\12\57\1\0\23\56\1\0\10\56"+
    "\2\0\2\56\2\0\26\56\1\0\7\56\1\0\1\56"+
    "\3\0\4\56\2\0\11\56\2\0\2\56\2\0\4\56"+
    "\10\0\1\56\4\0\2\56\1\0\5\56\2\0\12\57"+
    "\2\56\12\0\1\56\1\0\1\56\2\0\3\56\1\0"+
    "\6\56\4\0\2\56\2\0\26\56\1\0\7\56\1\0"+
    "\2\56\1\0\2\56\1\0\2\56\2\0\1\56\1\0"+
    "\5\56\4\0\2\56\2\0\3\56\3\0\1\56\7\0"+
    "\4\56\1\0\1\56\7\0\12\57\6\56\13\0\3\56"+
    "\1\0\11\56\1\0\3\56\1\0\26\56\1\0\7\56"+
    "\1\0\2\56\1\0\5\56\2\0\12\56\1\0\3\56"+
    "\1\0\3\56\2\0\1\56\17\0\4\56\2\0\12\57"+
    "\11\0\7\56\1\0\3\56\1\0\10\56\2\0\2\56"+
    "\2\0\26\56\1\0\7\56\1\0\2\56\1\0\5\56"+
    "\2\0\11\56\2\0\2\56\2\0\3\56\10\0\2\56"+
    "\4\0\2\56\1\0\5\56\2\0\12\57\1\0\1\56"+
    "\20\0\2\56\1\0\6\56\3\0\3\56\1\0\4\56"+
    "\3\0\2\56\1\0\1\56\1\0\2\56\3\0\2\56"+
    "\3\0\3\56\3\0\14\56\4\0\5\56\3\0\3\56"+
    "\1\0\4\56\2\0\1\56\6\0\1\56\16\0\12\57"+
    "\20\0\15\56\1\0\3\56\1\0\27\56\1\0\20\56"+
    "\3\0\10\56\1\0\3\56\1\0\4\56\7\0\2\56"+
    "\1\0\3\56\5\0\4\56\2\0\12\57\20\0\4\56"+
    "\1\0\10\56\1\0\3\56\1\0\27\56\1\0\12\56"+
    "\1\0\5\56\2\0\11\56\1\0\3\56\1\0\4\56"+
    "\7\0\2\56\7\0\1\56\1\0\4\56\2\0\12\57"+
    "\1\0\2\56\15\0\4\56\1\0\10\56\1\0\3\56"+
    "\1\0\63\56\1\0\3\56\1\0\5\56\5\0\4\56"+
    "\7\0\5\56\2\0\12\57\12\0\6\56\2\0\2\56"+
    "\1\0\22\56\3\0\30\56\1\0\11\56\1\0\1\56"+
    "\2\0\7\56\3\0\1\56\4\0\6\56\1\0\1\56"+
    "\1\0\10\56\6\0\12\57\2\0\2\56\15\0\72\56"+
    "\5\0\17\56\1\0\12\57\47\0\2\56\1\0\1\56"+
    "\1\0\5\56\1\0\30\56\1\0\1\56\1\0\27\56"+
    "\2\0\5\56\1\0\1\56\1\0\6\56\2\0\12\57"+
    "\2\0\4\56\40\0\1\56\27\0\2\56\6\0\12\57"+
    "\13\0\1\56\1\0\1\56\1\0\1\56\4\0\12\56"+
    "\1\0\44\56\4\0\24\56\1\0\22\56\1\0\44\56"+
    "\11\0\1\56\71\0\100\56\12\57\6\0\100\56\12\57"+
    "\4\56\2\0\46\56\1\0\1\56\5\0\1\56\2\0"+
    "\53\56\1\0\115\56\1\0\4\56\2\0\7\56\1\0"+
    "\1\56\1\0\4\56\2\0\51\56\1\0\4\56\2\0"+
    "\41\56\1\0\4\56\2\0\7\56\1\0\1\56\1\0"+
    "\4\56\2\0\17\56\1\0\71\56\1\0\4\56\2\0"+
    "\103\56\2\0\3\56\40\0\20\56\20\0\126\56\2\0"+
    "\6\56\3\0\u016c\56\2\0\21\56\1\1\32\56\5\0"+
    "\113\56\3\0\13\56\7\0\15\56\1\0\7\56\13\0"+
    "\25\56\13\0\24\56\14\0\15\56\1\0\3\56\1\0"+
    "\2\56\14\0\124\56\3\0\1\56\4\0\2\56\2\0"+
    "\12\57\41\0\3\56\2\0\12\57\6\0\131\56\7\0"+
    "\53\56\5\0\106\56\12\0\37\56\1\0\14\56\4\0"+
    "\14\56\12\0\12\57\36\56\2\0\5\56\13\0\54\56"+
    "\4\0\32\56\6\0\12\57\46\0\34\56\4\0\77\56"+
    "\1\0\35\56\2\0\1\56\12\57\6\0\12\57\15\0"+
    "\1\56\10\0\17\56\101\0\114\56\4\0\12\57\21\0"+
    "\11\56\14\0\60\56\12\57\72\56\14\0\70\56\10\0"+
    "\12\57\3\0\3\56\12\57\44\56\2\0\11\56\7\0"+
    "\53\56\2\0\3\56\20\0\3\56\1\0\47\56\5\0"+
    "\372\56\1\0\33\56\2\0\6\56\2\0\46\56\2\0"+
    "\6\56\2\0\10\56\1\0\1\56\1\0\1\56\1\0"+
    "\1\56\1\0\37\56\2\0\65\56\1\0\7\56\1\0"+
    "\1\56\3\0\3\56\1\0\7\56\3\0\4\56\2\0"+
    "\6\56\4\0\15\56\5\0\3\56\1\0\7\56\3\0"+
    "\13\1\35\0\2\2\5\0\1\1\17\0\2\56\23\0"+
    "\1\56\12\0\1\1\21\0\1\56\15\0\1\56\20\0"+
    "\15\56\63\0\41\56\21\0\1\56\4\0\1\56\2\0"+
    "\12\56\1\0\1\56\3\0\5\56\6\0\1\56\1\0"+
    "\1\56\1\0\1\56\1\0\4\56\1\0\13\56\2\0"+
    "\4\56\5\0\5\56\4\0\1\56\21\0\51\56\u022d\0"+
    "\64\56\171\0\1\60\234\0\57\56\1\0\57\56\1\0"+
    "\205\56\6\0\11\56\14\0\46\56\1\0\1\56\5\0"+
    "\1\56\2\0\70\56\7\0\1\56\17\0\30\56\11\0"+
    "\7\56\1\0\7\56\1\0\7\56\1\0\7\56\1\0"+
    "\7\56\1\0\7\56\1\0\7\56\1\0\7\56\1\0"+
    "\40\56\57\0\1\56\320\0\1\1\4\0\3\56\31\0"+
    "\17\56\1\0\5\56\2\0\5\56\4\0\126\56\2\0"+
    "\2\56\2\0\3\56\1\0\132\56\1\0\4\56\5\0"+
    "\53\56\1\0\136\56\21\0\33\56\65\0\306\56\112\0"+
    "\360\56\20\0\215\56\103\0\56\56\2\0\15\56\3\0"+
    "\20\56\12\57\2\56\24\0\63\56\1\0\12\56\1\0"+
    "\163\56\45\0\11\56\2\0\147\56\2\0\65\56\2\0"+
    "\5\56\60\0\61\56\30\0\64\56\14\0\106\56\12\0"+
    "\12\57\6\0\30\56\3\0\1\56\1\0\3\56\12\57"+
    "\44\56\2\0\44\56\14\0\35\56\3\0\101\56\16\0"+
    "\1\56\12\57\6\0\20\56\12\57\5\56\1\0\67\56"+
    "\11\0\16\56\2\0\12\57\6\0\27\56\3\0\111\56"+
    "\30\0\3\56\2\0\20\56\2\0\5\56\12\0\6\56"+
    "\2\0\6\56\2\0\6\56\11\0\7\56\1\0\7\56"+
    "\1\0\53\56\1\0\14\56\10\0\173\56\1\0\2\56"+
    "\2\0\12\57\6\0\244\56\14\0\27\56\4\0\61\56"+
    "\4\0\u0100\61\156\56\2\0\152\56\46\0\7\56\14\0"+
    "\5\56\5\0\14\56\1\0\15\56\1\0\5\56\1\0"+
    "\1\56\1\0\2\56\1\0\2\56\1\0\154\56\41\0"+
    "\153\56\22\0\100\56\2\0\66\56\50\0\14\56\4\0"+
    "\20\56\20\0\20\56\3\0\2\56\30\0\3\56\40\0"+
    "\5\56\1\0\207\56\23\0\12\57\7\0\32\56\4\0"+
    "\1\56\1\0\32\56\13\0\131\56\3\0\6\56\2\0"+
    "\6\56\2\0\6\56\2\0\3\56\43\0\14\56\1\0"+
    "\32\56\1\0\23\56\1\0\2\56\1\0\17\56\2\0"+
    "\16\56\42\0\173\56\105\0\65\56\210\0\1\56\202\0"+
    "\35\56\3\0\61\56\17\0\1\56\37\0\40\56\15\0"+
    "\36\56\5\0\53\56\5\0\36\56\2\0\44\56\4\0"+
    "\10\56\1\0\5\56\52\0\236\56\2\0\12\57\6\0"+
    "\44\56\4\0\44\56\4\0\50\56\10\0\64\56\234\0"+
    "\67\56\11\0\26\56\12\0\10\56\230\0\6\56\2\0"+
    "\1\56\1\0\54\56\1\0\2\56\3\0\1\56\2\0"+
    "\27\56\12\0\27\56\11\0\37\56\101\0\23\56\1\0"+
    "\2\56\12\0\26\56\12\0\32\56\106\0\70\56\6\0"+
    "\2\56\100\0\4\56\1\0\2\56\5\0\10\56\1\0"+
    "\3\56\1\0\35\56\2\0\3\56\4\0\1\56\40\0"+
    "\35\56\3\0\35\56\43\0\10\56\1\0\36\56\31\0"+
    "\66\56\12\0\26\56\12\0\23\56\15\0\22\56\156\0"+
    "\111\56\67\0\63\56\15\0\63\56\15\0\50\56\10\0"+
    "\12\57\306\0\35\56\12\0\1\56\10\0\41\56\217\0"+
    "\27\56\11\0\107\56\37\0\12\57\17\0\74\56\25\0"+
    "\31\56\7\0\12\57\6\0\65\56\1\0\12\57\4\0"+
    "\3\56\11\0\44\56\2\0\1\56\11\0\105\56\4\0"+
    "\4\56\3\0\12\57\1\56\1\0\1\56\43\0\22\56"+
    "\1\0\45\56\6\0\1\56\101\0\7\56\1\0\1\56"+
    "\1\0\4\56\1\0\17\56\1\0\12\56\7\0\73\56"+
    "\5\0\12\57\6\0\4\56\1\0\10\56\2\0\2\56"+
    "\2\0\26\56\1\0\7\56\1\0\2\56\1\0\5\56"+
    "\1\0\12\56\2\0\2\56\2\0\3\56\2\0\1\56"+
    "\6\0\1\56\5\0\7\56\2\0\7\56\3\0\5\56"+
    "\213\0\113\56\5\0\12\57\4\0\2\56\40\0\106\56"+
    "\1\0\1\56\10\0\12\57\246\0\66\56\2\0\11\56"+
    "\27\0\6\56\42\0\101\56\3\0\1\56\13\0\12\57"+
    "\46\0\71\56\7\0\12\57\66\0\33\56\2\0\17\56"+
    "\4\0\12\57\306\0\73\56\145\0\100\56\12\57\25\0"+
    "\1\56\240\0\10\56\2\0\56\56\2\0\10\56\1\0"+
    "\2\56\33\0\77\56\10\0\1\56\10\0\112\56\3\0"+
    "\1\56\42\0\71\56\7\0\11\56\1\0\55\56\1\0"+
    "\11\56\17\0\12\57\30\0\36\56\2\0\26\56\1\0"+
    "\16\56\111\0\7\56\1\0\2\56\1\0\54\56\3\0"+
    "\1\56\1\0\2\56\1\0\11\56\10\0\12\57\6\0"+
    "\6\56\1\0\2\56\1\0\45\56\1\0\2\56\1\0"+
    "\6\56\7\0\12\57\u0136\0\27\56\11\0\232\56\146\0"+
    "\157\56\21\0\304\56\274\0\57\56\321\0\107\56\271\0"+
    "\71\56\7\0\37\56\1\0\12\57\146\0\36\56\2\0"+
    "\5\56\13\0\67\56\11\0\4\56\14\0\12\57\11\0"+
    "\25\56\5\0\23\56\260\0\100\56\200\0\113\56\4\0"+
    "\71\56\7\0\21\56\100\0\2\56\1\0\1\56\34\0"+
    "\370\56\10\0\363\56\15\0\37\56\61\0\3\56\21\0"+
    "\4\56\10\0\u018c\56\4\0\153\56\5\0\15\56\3\0"+
    "\11\56\7\0\12\56\3\0\2\56\306\0\5\56\3\0"+
    "\6\56\10\0\10\56\2\0\7\56\36\0\4\56\224\0"+
    "\3\56\273\0\125\56\1\0\107\56\1\0\2\56\2\0"+
    "\1\56\2\0\2\56\2\0\4\56\1\0\14\56\1\0"+
    "\1\56\1\0\7\56\1\0\101\56\1\0\4\56\2\0"+
    "\10\56\1\0\7\56\1\0\34\56\1\0\4\56\1\0"+
    "\5\56\1\0\1\56\3\0\7\56\1\0\u0154\56\2\0"+
    "\31\56\1\0\31\56\1\0\37\56\1\0\31\56\1\0"+
    "\37\56\1\0\31\56\1\0\37\56\1\0\31\56\1\0"+
    "\37\56\1\0\31\56\1\0\10\56\2\0\62\57\67\56"+
    "\4\0\62\56\10\0\1\56\16\0\1\56\26\0\5\56"+
    "\1\0\17\56\120\0\7\56\1\0\21\56\2\0\7\56"+
    "\1\0\2\56\1\0\5\56\325\0\55\56\3\0\16\56"+
    "\2\0\12\57\4\0\1\56\u0171\0\60\56\12\57\6\0"+
    "\305\56\13\0\7\56\51\0\114\56\4\0\12\57\246\0"+
    "\4\56\1\0\33\56\1\0\2\56\1\0\1\56\2\0"+
    "\1\56\1\0\12\56\1\0\4\56\1\0\1\56\1\0"+
    "\1\56\6\0\1\56\4\0\1\56\1\0\1\56\1\0"+
    "\1\56\1\0\3\56\1\0\2\56\1\0\1\56\2\0"+
    "\1\56\1\0\1\56\1\0\1\56\1\0\1\56\1\0"+
    "\1\56\1\0\2\56\1\0\1\56\2\0\4\56\1\0"+
    "\7\56\1\0\4\56\1\0\4\56\1\0\1\56\1\0"+
    "\12\56\1\0\21\56\5\0\3\56\1\0\5\56\1\0"+
    "\21\56\164\0\32\56\6\0\32\56\6\0\32\56\166\0"+
    "\327\56\51\0\65\56\13\0\336\56\2\0\u0182\56\16\0"+
    "\u0131\56\37\0\36\56\342\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[29952];
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
    "\1\2\5\1\1\2\4\1\3\0\2\7\1\0\1\10"+
    "\1\0\6\1\1\0\1\11\5\7\2\0\1\1\2\12"+
    "\1\13\1\14\1\0\1\15\1\13\2\7\1\1\1\16"+
    "\1\1\1\17\1\11\1\7\1\20\1\0\2\20\3\7"+
    "\1\1\1\11\1\0\1\21\1\1\1\22\1\0\3\20"+
    "\2\13\1\23\1\1\1\24";

  private static int [] zzUnpackAction() {
    int [] result = new int[82];
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
    "\0\0\0\62\0\144\0\226\0\310\0\372\0\u012c\0\u015e"+
    "\0\u0190\0\u01c2\0\u01f4\0\u0226\0\u0258\0\u028a\0\u02bc\0\u02ee"+
    "\0\u0320\0\u0352\0\u0384\0\u03b6\0\u03e8\0\u041a\0\u044c\0\u047e"+
    "\0\u012c\0\u04b0\0\u04e2\0\62\0\u0514\0\u0546\0\u0578\0\u05aa"+
    "\0\u05dc\0\u060e\0\u0640\0\u0672\0\62\0\u06a4\0\u06d6\0\u0352"+
    "\0\u0708\0\u073a\0\u076c\0\u079e\0\u07d0\0\u041a\0\u0802\0\u044c"+
    "\0\u0834\0\u0866\0\u0514\0\u05aa\0\u0898\0\u0546\0\u08ca\0\u028a"+
    "\0\u08fc\0\u028a\0\u0672\0\u092e\0\62\0\u0960\0\u0960\0\u07d0"+
    "\0\u0992\0\u09c4\0\u09f6\0\u0a28\0\u076c\0\u0a5a\0\u04e2\0\u0a8c"+
    "\0\u028a\0\u0abe\0\u044c\0\u0a28\0\u0abe\0\u0abe\0\u0a28\0\u079e"+
    "\0\u0af0\0\u028a";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[82];
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
    "\1\6\1\7\1\10\2\5\1\2\1\11\1\5\1\12"+
    "\1\2\1\13\1\14\1\15\1\14\1\16\1\17\2\16"+
    "\1\20\3\16\1\21\2\2\1\5\1\22\1\23\3\22"+
    "\1\24\1\25\3\5\2\2\63\0\3\3\56\0\2\4"+
    "\1\0\56\4\10\0\1\5\1\0\1\5\5\0\2\5"+
    "\2\0\1\5\3\0\13\5\3\0\13\5\16\0\1\26"+
    "\3\0\1\27\6\0\13\30\4\0\6\30\22\0\4\31"+
    "\7\0\13\31\1\0\1\31\1\0\7\31\2\0\2\31"+
    "\16\0\2\31\1\32\1\31\7\0\13\31\1\0\1\31"+
    "\1\0\7\31\2\0\2\31\6\0\1\33\12\0\1\33"+
    "\7\0\13\33\3\0\7\33\2\0\2\33\26\0\1\34"+
    "\54\0\1\35\7\0\13\35\3\0\7\35\15\0\1\5"+
    "\1\0\1\5\2\0\1\30\2\0\1\5\1\36\2\0"+
    "\1\5\3\0\13\16\3\0\1\5\6\16\4\5\11\0"+
    "\1\5\1\0\1\5\2\0\1\30\2\0\1\5\1\36"+
    "\2\0\1\5\3\0\2\16\1\37\10\16\3\0\1\5"+
    "\6\16\4\5\11\0\1\5\1\0\1\5\2\0\1\30"+
    "\2\0\1\5\1\40\2\0\1\5\3\0\13\16\3\0"+
    "\1\5\6\16\4\5\11\0\1\5\1\0\1\5\2\0"+
    "\1\30\2\0\1\5\1\40\2\0\1\5\3\0\2\16"+
    "\1\41\10\16\3\0\1\5\6\16\4\5\11\0\1\5"+
    "\1\0\1\5\2\0\1\30\2\0\1\5\1\40\2\0"+
    "\1\5\3\0\6\16\1\42\3\16\1\43\3\0\1\5"+
    "\6\16\4\5\3\0\3\44\1\0\1\44\11\0\1\44"+
    "\24\0\1\45\1\0\7\44\2\0\1\44\11\0\1\5"+
    "\1\0\1\5\2\0\1\46\2\31\1\47\1\40\2\0"+
    "\1\5\3\0\13\50\1\0\1\31\1\0\1\47\6\50"+
    "\2\5\2\47\11\0\1\5\1\0\1\5\2\0\1\46"+
    "\2\31\1\47\1\40\2\0\1\5\3\0\13\50\1\0"+
    "\1\31\1\0\1\47\4\50\1\51\1\50\2\5\2\47"+
    "\11\0\1\5\1\0\1\5\2\0\1\46\2\31\1\47"+
    "\1\40\2\0\1\5\3\0\13\50\1\0\1\31\1\0"+
    "\1\47\3\50\1\52\2\50\2\5\2\47\3\0\3\53"+
    "\1\0\1\53\1\0\1\5\1\54\1\5\5\0\2\5"+
    "\2\0\1\5\3\0\13\5\3\0\7\5\1\55\3\5"+
    "\16\0\1\56\2\0\1\57\1\27\6\0\13\56\3\0"+
    "\1\57\6\56\2\0\2\57\5\0\1\60\1\0\1\60"+
    "\1\0\2\60\1\0\1\60\1\0\6\60\1\0\1\60"+
    "\3\0\13\60\1\0\1\60\1\0\13\60\16\0\1\30"+
    "\3\0\1\27\6\0\13\30\4\0\6\30\6\0\2\61"+
    "\1\0\11\61\4\32\7\61\13\32\1\61\1\32\1\61"+
    "\7\32\2\61\2\32\1\61\1\0\17\33\1\62\7\33"+
    "\13\62\3\33\7\62\4\33\1\0\1\33\17\0\1\63"+
    "\3\0\1\63\3\0\13\63\3\0\13\63\5\0\1\60"+
    "\1\0\1\60\1\0\1\64\1\60\1\5\1\60\1\0"+
    "\3\65\1\66\1\64\1\60\1\0\1\64\3\0\13\66"+
    "\1\0\1\65\1\0\7\66\2\64\2\66\11\0\1\5"+
    "\1\0\1\5\2\0\1\30\2\0\1\5\1\40\2\0"+
    "\1\5\3\0\5\16\1\67\5\16\3\0\1\5\6\16"+
    "\4\5\5\0\1\60\1\0\1\60\1\0\1\64\1\60"+
    "\1\5\1\60\1\0\3\60\2\64\1\60\1\0\1\64"+
    "\3\0\13\64\1\0\1\60\1\0\13\64\11\0\1\5"+
    "\1\0\1\5\2\0\1\30\2\0\1\5\1\40\2\0"+
    "\1\5\3\0\11\16\1\70\1\16\3\0\1\5\6\16"+
    "\4\5\11\0\1\5\1\0\1\5\2\0\1\30\2\0"+
    "\1\5\1\40\2\0\1\5\3\0\10\16\1\71\2\16"+
    "\3\0\1\5\6\16\4\5\11\0\1\5\1\0\1\5"+
    "\2\0\1\30\2\0\1\5\1\40\2\0\1\5\3\0"+
    "\11\16\1\72\1\16\3\0\1\5\6\16\4\5\2\0"+
    "\6\44\1\0\35\44\1\73\15\44\14\0\1\46\3\31"+
    "\1\27\6\0\13\46\1\0\1\31\1\0\1\31\6\46"+
    "\2\0\2\31\11\0\1\5\1\0\1\5\2\0\3\31"+
    "\1\47\1\5\2\0\1\5\3\0\13\47\1\0\1\31"+
    "\1\0\7\47\2\5\2\47\11\0\1\5\1\0\1\5"+
    "\2\0\1\46\2\31\1\47\1\40\2\0\1\5\3\0"+
    "\13\50\1\0\1\31\1\0\1\47\4\50\1\74\1\50"+
    "\2\5\2\47\5\0\1\75\1\76\1\0\1\77\1\100"+
    "\1\77\1\100\1\0\1\77\1\101\2\102\1\103\1\104"+
    "\1\76\1\0\1\100\1\0\1\76\1\0\13\52\1\0"+
    "\1\31\1\0\1\103\6\52\2\100\2\103\2\0\6\53"+
    "\1\0\46\53\1\105\4\53\6\54\1\0\1\54\1\106"+
    "\51\54\3\0\1\75\1\76\1\0\1\77\1\100\1\77"+
    "\1\100\1\0\2\77\1\76\1\77\1\100\1\55\1\76"+
    "\1\0\1\100\1\0\1\76\1\0\13\100\3\0\13\100"+
    "\16\0\1\57\2\0\1\57\7\0\13\57\3\0\7\57"+
    "\2\0\2\57\2\0\2\61\1\0\56\61\1\0\17\33"+
    "\1\62\4\33\1\107\2\33\13\62\3\33\7\62\4\33"+
    "\1\0\1\33\3\0\1\60\1\0\1\60\1\0\2\60"+
    "\1\0\1\60\1\0\4\65\2\60\1\0\1\60\3\0"+
    "\13\65\1\0\1\65\1\0\7\65\2\60\2\65\11\0"+
    "\1\5\1\0\1\5\2\0\1\30\2\0\1\5\1\40"+
    "\2\0\1\5\3\0\2\16\1\110\10\16\3\0\1\5"+
    "\6\16\4\5\11\0\1\5\1\0\1\5\2\0\1\30"+
    "\2\0\1\5\1\40\2\0\1\5\3\0\11\16\1\111"+
    "\1\16\3\0\1\5\6\16\4\5\11\0\1\5\1\0"+
    "\1\5\2\0\1\46\2\31\1\47\1\40\2\0\1\5"+
    "\3\0\13\50\1\0\1\31\1\0\1\47\2\50\1\52"+
    "\3\50\2\5\2\47\5\0\1\75\1\76\1\0\4\77"+
    "\1\0\2\77\1\76\2\77\2\76\1\0\1\77\1\0"+
    "\1\76\1\0\13\77\3\0\13\77\5\0\1\75\1\76"+
    "\1\0\4\77\1\0\1\77\1\101\3\102\1\112\1\76"+
    "\1\0\1\77\1\0\1\76\1\0\13\101\1\0\1\31"+
    "\1\0\1\102\6\101\2\77\2\102\5\0\1\75\1\76"+
    "\1\0\4\77\1\0\1\77\4\102\2\76\1\0\1\77"+
    "\1\0\1\76\1\0\13\102\1\0\1\31\1\0\7\102"+
    "\2\77\2\102\5\0\1\75\1\76\1\0\1\77\1\100"+
    "\1\77\1\100\1\0\1\77\3\102\1\103\1\55\1\76"+
    "\1\0\1\100\1\0\1\76\1\0\13\103\1\0\1\31"+
    "\1\0\7\103\2\100\2\103\5\0\1\113\1\76\1\60"+
    "\1\77\1\114\1\115\1\100\1\60\1\77\1\115\1\116"+
    "\1\115\1\114\1\117\1\116\1\0\1\114\1\0\1\76"+
    "\1\0\13\114\1\0\1\60\1\0\13\114\2\0\6\54"+
    "\1\0\1\54\1\106\44\54\1\120\4\54\7\0\1\5"+
    "\1\0\1\5\2\0\1\30\2\0\1\5\1\40\2\0"+
    "\1\5\3\0\11\16\1\121\1\16\3\0\1\5\6\16"+
    "\4\5\5\0\1\113\1\76\1\60\1\77\2\115\1\77"+
    "\1\60\1\77\1\115\1\116\2\115\2\116\1\0\1\115"+
    "\1\0\1\76\1\0\13\115\1\0\1\60\1\0\13\115"+
    "\11\0\1\5\1\0\1\5\2\0\1\30\2\0\1\5"+
    "\1\40\2\0\1\5\3\0\2\16\1\122\10\16\3\0"+
    "\1\5\6\16\4\5\2\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[2850];
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
    "\1\1\1\11\23\1\3\0\2\1\1\0\1\11\1\0"+
    "\6\1\1\0\1\11\5\1\2\0\5\1\1\0\12\1"+
    "\1\11\1\0\7\1\1\0\3\1\1\0\10\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[82];
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
            { return T_RT_DBL;
            }
          // fall through
          case 28: break;
          case 9:
            { return JSON_TEXT;
            }
          // fall through
          case 29: break;
          case 10:
            { return MULTIPART_SEPERATE;
            }
          // fall through
          case 30: break;
          case 11:
            { return HEADER_DESC;
            }
          // fall through
          case 31: break;
          case 12:
            { return LINE_COMMENT;
            }
          // fall through
          case 32: break;
          case 13:
            { return VARIABLE_DEFINE;
            }
          // fall through
          case 33: break;
          case 14:
            { return GET;
            }
          // fall through
          case 34: break;
          case 15:
            { return PUT;
            }
          // fall through
          case 35: break;
          case 16:
            { return URL_DESC;
            }
          // fall through
          case 36: break;
          case 17:
            { return XML_TEXT;
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
