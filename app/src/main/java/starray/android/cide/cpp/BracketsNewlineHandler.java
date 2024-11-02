package starray.android.cide.cpp;

import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.text.TextUtils;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class BracketsNewlineHandler extends BaseNewLineHandler {
    private final Function1<String, Integer> getIndentAdvance;
    private final Function0<Boolean> useTab;

    public final Function1<String, Integer> getGetIndentAdvance() {
        return this.getIndentAdvance;
    }

    public final Function0<Boolean> getUseTab() {
        return this.useTab;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BracketsNewlineHandler(Function1<String, Integer> function1, Function0<Boolean> function0) {
        Intrinsics.checkNotNullParameter(function1, "getIndentAdvance");
        Intrinsics.checkNotNullParameter(function0, "useTab");
        this.getIndentAdvance = function1;
        this.useTab = function0;
    }

    public NewlineHandleResult handleNewline(Content text, CharPosition position, Styles style, int tabSize) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(position, "position");
        ContentLine line = text.getLine(position.line);
        int index = position.column;
        String beforeText = line.subSequence(0, index).toString();
        Intrinsics.checkNotNullExpressionValue(beforeText, "toString(...)");
        String afterText = line.subSequence(index, line.length()).toString();
        Intrinsics.checkNotNullExpressionValue(afterText, "toString(...)");
        return handleNewline(beforeText, afterText, tabSize);
    }

    private final NewlineHandleResult handleNewline(String beforeText, String afterText, int tabSize) {
        Intrinsics.checkNotNull(beforeText);
        int count = TextUtils.countLeadingSpaceCount(beforeText, tabSize);
        int advanceBefore = ((Number) this.getIndentAdvance.invoke(beforeText)).intValue();
        int advanceAfter = ((Number) this.getIndentAdvance.invoke(afterText)).intValue();
        StringBuilder append = new StringBuilder("\n").append(TextUtils.createIndent(count + advanceBefore, tabSize, ((Boolean) this.useTab.invoke()).booleanValue())).append('\n');
        String it = TextUtils.createIndent(count + advanceAfter, tabSize, ((Boolean) this.useTab.invoke()).booleanValue());
        Intrinsics.checkNotNull(it);
        StringBuilder sb = append.append(it);
        int shiftLeft = it.length() + 1;
        return new NewlineHandleResult(sb, shiftLeft);
    }
}
