package starray.android.cide.cpp;

import kotlin.jvm.internal.Intrinsics;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public final class IncrementalToken implements Token {
    public boolean incomplete;
    public int startIndex;
    private final Token token;
    public int type;

    public IncrementalToken(Token token) {
        Intrinsics.checkNotNullParameter(token, "token");
        this.token = token;
        this.type = this.token.getType();
        this.startIndex = this.token.getStartIndex();
    }

    public final Token getToken() {
        return this.token;
    }

    public String getText() {
        String text = this.token.getText();
        Intrinsics.checkNotNullExpressionValue(text, "getText(...)");
        return text;
    }

    public int getType() {
        return this.type;
    }

    public int getLine() {
        return this.token.getLine();
    }

    public int getCharPositionInLine() {
        return this.token.getCharPositionInLine();
    }

    public int getChannel() {
        return this.token.getChannel();
    }

    public int getTokenIndex() {
        return this.token.getTokenIndex();
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getStopIndex() {
        return this.token.getStopIndex();
    }

    public TokenSource getTokenSource() {
        TokenSource tokenSource = this.token.getTokenSource();
        Intrinsics.checkNotNullExpressionValue(tokenSource, "getTokenSource(...)");
        return tokenSource;
    }

    public CharStream getInputStream() {
        CharStream inputStream = this.token.getInputStream();
        Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
        return inputStream;
    }

    public boolean equals(Object other) {
        return Intrinsics.areEqual(this.token, other);
    }

    public int hashCode() {
        return this.token.hashCode();
    }

    public String toString() {
        return this.token.toString();
    }
}
