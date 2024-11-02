package starray.android.cide.cpp;

import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.lang.styling.StylesUtils;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
public abstract class BaseNewLineHandler implements NewlineHandler {
    private final List<String> openingBrackets = new ArrayList<>();
    private final List<String> closingBrackets = new ArrayList<>();
    
    protected final List<String> getOpeningBrackets() {
        return this.openingBrackets;
    }

    protected final List<String> getClosingBrackets() {
        return this.closingBrackets;
    }

    public boolean matchesRequirement(Content text, CharPosition position, Styles style) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(position, "position");
        ContentLine line = text.getLine(position.line);
        if (!StylesUtils.checkNoCompletion(style, position)) {
            List<String> list = this.openingBrackets;
            Intrinsics.checkNotNull(line);
            if (list.contains(getNonEmptyTextBefore((CharSequence) line, position.column, 1)) && this.closingBrackets.contains(getNonEmptyTextAfter((CharSequence) line, position.column, 1))) {
                return true;
            }
        }
        return false;
    }

    protected String getNonEmptyTextBefore(CharSequence text, int index, int length) {
        Intrinsics.checkNotNullParameter(text, "text");
        int idx = index;
        while (idx > 0 && Character.isWhitespace(text.charAt(idx - 1))) {
            idx--;
        }
        return text.subSequence(Math.max(0, idx - length), idx).toString();
    }

    protected String getNonEmptyTextAfter(CharSequence text, int index, int length) {
        Intrinsics.checkNotNullParameter(text, "text");
        int idx = index;
        while (idx < text.length() && Character.isWhitespace(text.charAt(idx))) {
            idx++;
        }
        return text.subSequence(idx, Math.min(idx + length, text.length())).toString();
    }
}
