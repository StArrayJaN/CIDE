package starray.android.cide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.langs.java.JavaIncrementalAnalyzeManager;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class CPPLanguage extends JavaLanguage {

    String[] keywords = {
            "alignas",
            "alignof",
            "and",
            "and_eq",
            "asm",
            "atomic_cancel",
            "atomic_commit",
            "atomic_noexcept",
            "auto",
            "bitand",
            "bitor",
            "bool",
            "break",
            "case",
            "catch",
            "char",
            "char8_t",
            "char16_t",
            "char32_t",
            "class",
            "compl",
            "concept",
            "const",
            "consteval",
            "constexpr",
            "constinit",
            "const_cast",
            "continue",
            "co_await",
            "co_return",
            "co_yield",
            "decltype",
            "default",
            "delete",
            "do",
            "double",
            "dynamic_cast",
            "else",
            "enum",
            "explicit",
            "export",
            "extern",
            "false",
            "float",
            "for",
            "friend",
            "goto",
            "if",
            "inline",
            "int",
            "long",
            "mutable",
            "namespace",
            "new",
            "noexcept",
            "not",
            "not_eq",
            "nullptr",
            "operator",
            "or",
            "or_eq",
            "private",
            "protected",
            "public",
            "reflexpr",
            "register",
            "reinterpret_cast",
            "requires",
            "return",
            "short",
            "signed",
            "sizeof",
            "static",
            "static_assert",
            "static_cast",
            "struct",
            "switch",
            "synchronized",
            "template",
            "this",
            "thread_local",
            "throw",
            "true",
            "try",
            "typedef",
            "typeid",
            "typename",
            "union",
            "unsigned",
            "using",
            "virtual",
            "void",
            "volatile",
            "wchar_t",
            "while",
            "xor",
            "xor_eq"
    };
    IdentifierAutoComplete autoComplete;
    private final JavaIncrementalAnalyzeManager manager;
    CPPLanguage() {
        autoComplete = new IdentifierAutoComplete(keywords);
        manager = new JavaIncrementalAnalyzeManager();

    }

    public IdentifierAutoComplete.SyncIdentifiers getIdentifiers() throws Exception {
        Class<?> clazz = getClass().getSuperclass();
        Field manager = Objects.requireNonNull(clazz).getDeclaredField("manager");
        manager.setAccessible(true);
        Field identifiers = manager.getClass().getDeclaredField("identifiers");
        identifiers.setAccessible(true);
        return (IdentifierAutoComplete.SyncIdentifiers)identifiers.get(manager);
    }

    @SuppressLint("NewApi")
    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                                    @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        var prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);

        final IdentifierAutoComplete.SyncIdentifiers idt;
        try {
            idt = getIdentifiers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (idt != null) {
            Log.i("CPP",String.format("requireAutoComplete:content:%s,publisher:%s",prefix, Arrays.toString(publisher.getItems()
                    .stream()
                    .map(Object::toString)
                    .toList()
                    .toArray())));
            autoComplete.requireAutoComplete(content,position,prefix, publisher, idt);
        }
    }

}
