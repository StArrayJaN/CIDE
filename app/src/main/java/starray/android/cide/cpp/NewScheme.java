package starray.android.cide.cpp;


import android.content.Context;
import io.github.rosemoe.sora.lang.styling.TextStyle;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class NewScheme extends EditorColorScheme {
    public static final int COMPLETION_WND_BG_CURRENT_ITEM;
    public static final int COMPLETION_WND_TEXT_API;
    public static final int COMPLETION_WND_TEXT_DETAIL;
    public static final int COMPLETION_WND_TEXT_LABEL;
    public static final int COMPLETION_WND_TEXT_TYPE;
    public static final int FIELD;
    public static final int FIXME_COMMENT;
    public static final int LOG_PRIORITY_BG_DEBUG;
    public static final int LOG_PRIORITY_BG_ERROR;
    public static final int LOG_PRIORITY_BG_INFO;
    public static final int LOG_PRIORITY_BG_VERBOSE;
    public static final int LOG_PRIORITY_BG_WARNING;
    public static final int LOG_PRIORITY_FG_DEBUG;
    public static final int LOG_PRIORITY_FG_ERROR;
    public static final int LOG_PRIORITY_FG_INFO;
    public static final int LOG_PRIORITY_FG_VERBOSE;
    public static final int LOG_PRIORITY_FG_WARNING;
    public static final int LOG_TEXT_DEBUG;
    public static final int LOG_TEXT_ERROR;
    public static final int LOG_TEXT_INFO;
    public static final int LOG_TEXT_VERBOSE;
    public static final int LOG_TEXT_WARNING;
    public static final int TODO_COMMENT;
    public static final int TYPE_NAME;
    public static final int XML_TAG;
    protected static int endColorId;

    protected NewScheme() {
    }

    static {
        endColorId = 64;
        int i = endColorId + 1;
        endColorId = i;
        COMPLETION_WND_BG_CURRENT_ITEM = i;
        int i2 = endColorId + 1;
        endColorId = i2;
        COMPLETION_WND_TEXT_LABEL = i2;
        int i3 = endColorId + 1;
        endColorId = i3;
        COMPLETION_WND_TEXT_TYPE = i3;
        int i4 = endColorId + 1;
        endColorId = i4;
        COMPLETION_WND_TEXT_API = i4;
        int i5 = endColorId + 1;
        endColorId = i5;
        COMPLETION_WND_TEXT_DETAIL = i5;
        int i6 = endColorId + 1;
        endColorId = i6;
        LOG_TEXT_INFO = i6;
        int i7 = endColorId + 1;
        endColorId = i7;
        LOG_TEXT_DEBUG = i7;
        int i8 = endColorId + 1;
        endColorId = i8;
        LOG_TEXT_VERBOSE = i8;
        int i9 = endColorId + 1;
        endColorId = i9;
        LOG_TEXT_ERROR = i9;
        int i10 = endColorId + 1;
        endColorId = i10;
        LOG_TEXT_WARNING = i10;
        int i11 = endColorId + 1;
        endColorId = i11;
        LOG_PRIORITY_FG_INFO = i11;
        int i12 = endColorId + 1;
        endColorId = i12;
        LOG_PRIORITY_FG_DEBUG = i12;
        int i13 = endColorId + 1;
        endColorId = i13;
        LOG_PRIORITY_FG_VERBOSE = i13;
        int i14 = endColorId + 1;
        endColorId = i14;
        LOG_PRIORITY_FG_ERROR = i14;
        int i15 = endColorId + 1;
        endColorId = i15;
        LOG_PRIORITY_FG_WARNING = i15;
        int i16 = endColorId + 1;
        endColorId = i16;
        LOG_PRIORITY_BG_INFO = i16;
        int i17 = endColorId + 1;
        endColorId = i17;
        LOG_PRIORITY_BG_DEBUG = i17;
        int i18 = endColorId + 1;
        endColorId = i18;
        LOG_PRIORITY_BG_VERBOSE = i18;
        int i19 = endColorId + 1;
        endColorId = i19;
        LOG_PRIORITY_BG_ERROR = i19;
        int i20 = endColorId + 1;
        endColorId = i20;
        LOG_PRIORITY_BG_WARNING = i20;
        int i21 = endColorId + 1;
        endColorId = i21;
        XML_TAG = i21;
        int i22 = endColorId + 1;
        endColorId = i22;
        FIELD = i22;
        int i23 = endColorId + 1;
        endColorId = i23;
        TYPE_NAME = i23;
        int i24 = endColorId + 1;
        endColorId = i24;
        TODO_COMMENT = i24;
        int i25 = endColorId + 1;
        endColorId = i25;
        FIXME_COMMENT = i25;
    }

    public static long get(int id) {
        return TextStyle.makeStyle(id);
    }

    public static long forKeyword() {
        return TextStyle.makeStyle(21, 0, true, false, false);
    }

    public static long forString() {
        return TextStyle.makeStyle(24, true);
    }

    public static long forComment() {
        return TextStyle.makeStyle(22, true);
    }

    public static long withoutCompletion(int id) {
        return TextStyle.makeStyle(id, true);
    }

    public void applyDefault() {
        super.applyDefault();
        setColor(4, -14606047);
        setColor(3, -14606047);
        setColor(29, -28928);
        setColor(6, -1074534);
        setColor(1, 0);
        setColor(2, -5592406);
        setColor(45, -657931);
        setColor(16, -16777216);
        setColor(17, -1);
        setColor(5, -657931);
        setColor(30, -14606047);
        setColor(7, -44462);
        setColor(8, -44462);
        setColor(9, -13553359);
        setColor(10, -1);
        setColor(11, -9079435);
        setColor(12, -44462);
        setColor(13, -4342339);
        setColor(14, -12434878);
        setColor(15, -1);
        setColor(19, -9079435);
        setColor(20, -6381922);
        setColor(31, -2236963);
        setColor(21, -40864);
        setColor(23, -11549705);
        setColor(24, -7617718);
        setColor(TYPE_NAME, -11549705);
        setColor(28, -11549705);
        setColor(FIELD, -999861);
        setColor(XML_TAG, -40864);
        setColor(LOG_TEXT_ERROR, -769226);
        setColor(LOG_TEXT_WARNING, -5317);
        setColor(LOG_TEXT_INFO, -11751600);
        setColor(LOG_TEXT_DEBUG, -657931);
        setColor(LOG_TEXT_VERBOSE, -11549705);
        setColor(LOG_PRIORITY_FG_ERROR, -16777216);
        setColor(LOG_PRIORITY_FG_WARNING, -16777216);
        setColor(LOG_PRIORITY_FG_INFO, -1);
        setColor(LOG_PRIORITY_FG_DEBUG, -1);
        setColor(LOG_PRIORITY_FG_VERBOSE, -16777216);
        setColor(LOG_PRIORITY_BG_ERROR, -769226);
        setColor(LOG_PRIORITY_BG_WARNING, -5317);
        setColor(LOG_PRIORITY_BG_INFO, -14719552);
        setColor(LOG_PRIORITY_BG_DEBUG, -6382300);
        setColor(LOG_PRIORITY_BG_VERBOSE, -1);
        setColor(TODO_COMMENT, -15360);
        setColor(FIXME_COMMENT, -21760);
        setColor(22, -4342339);
    }

    public boolean isDark() {
        return true;
    }
}
