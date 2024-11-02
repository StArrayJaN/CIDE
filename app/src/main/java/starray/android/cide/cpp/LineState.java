package starray.android.cide.cpp;

//
// Decompiled by Jadx - 886ms
// 
import org.antlr.v4.runtime.Lexer;
public class LineState {
    public static final int NORMAL = 0;
    public static final int INCOMPLETE = 1;

    public int state = NORMAL;
    public boolean hasBraces = false;
    public int lexerMode = Lexer.DEFAULT_MODE;

    public LineState() {
    }

    public LineState(int state, boolean hasBraces, int lexerMode) {
        this.state = state;
        this.hasBraces = hasBraces;
        this.lexerMode = lexerMode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isHasBraces() {
        return hasBraces;
    }

    public void setHasBraces(boolean hasBraces) {
        this.hasBraces = hasBraces;
    }

    public int getLexerMode() {
        return lexerMode;
    }

    public void setLexerMode(int lexerMode) {
        this.lexerMode = lexerMode;
    }
}