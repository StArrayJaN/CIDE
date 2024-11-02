package io.github.rosemoe.sora.lsp.utils;

import android.annotation.SuppressLint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.lsp4j.CompletionItem;

public class OtherUtils {
    @SuppressLint("NewApi")
    public static String toString(List<?> list) {
        return Arrays
                .toString(list
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList())
                .toArray(new String[0]));
    }
    @SuppressLint("NewApi")
    public static String compeltionItemListToString(List<CompletionItem> list) {
            return Arrays
                    .toString(list
                            .stream()
                            .map(new Function<CompletionItem, String>() {
                                @Override
                                public String apply(CompletionItem completionItem) {
                                    StringBuilder str = new StringBuilder("detail:" + completionItem.getDetail() + "\n");
                                    str.append("label:" +completionItem.getLabel() + '\n');
                                    str.append("insertText:" + completionItem.getInsertText() + "\n");
                                    str.append("data:" + completionItem.getData() + "\n");
                                    return str.toString();
                                }
                            })
                            .collect(Collectors.toList())
                            .toArray(new String[0]));
    }
    public static int[] calculateTextViewSize(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) return new int[]{0, 0};
        // 获取TextView的Paint对象
        TextPaint textPaint = textView.getPaint();

        // 设置测量规格，宽度为AT_MOST，高度为UNSPECIFIED
        View.MeasureSpec.makeMeasureSpec(textView.getWidth(), View.MeasureSpec.AT_MOST);
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        // 计算文本宽度
        float textWidth = 0;
        int lineCount = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            float lineWidth = textPaint.measureText(line) +100;
            if (lineWidth > textWidth) {
                textWidth = lineWidth;
            }
            lineCount++;
        }
        /*Log.i("SignatureHelpWindow", Objects.toString(textHeight));*/
        // 如果行数超过了最大行数，则使用最大行数计算高度
        float totalHeight = 100 * lineCount ;
        return new int[]{(int) Math.ceil(textWidth), (int) Math.ceil(totalHeight)};
    }
}
