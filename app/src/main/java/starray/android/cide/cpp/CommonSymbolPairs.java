package starray.android.cide.cpp;

import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.github.rosemoe.sora.widget.SymbolPairMatch.SymbolPair;
import io.github.rosemoe.sora.widget.SymbolPairMatch.SymbolPair.SymbolPairEx;

public class CommonSymbolPairs extends SymbolPairMatch {

    private final SymbolPairEx isSelected = new SymbolPairEx() {
        @Override
        public boolean shouldDoAutoSurround(Content content) {
            return content != null && content.getCursor().isSelected();
        }
    };

    public CommonSymbolPairs() {
        super.putPair('{', new SymbolPair("{", "}"));
        super.putPair('(', new SymbolPair("(", ")"));
        super.putPair('[', new SymbolPair("[", "]"));
        super.putPair('"', new SymbolPair("\"", "\"", isSelected));
        super.putPair('\'', new SymbolPair("'", "'", isSelected));
    }

}