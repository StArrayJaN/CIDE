package starray.android.cide.cpp;

import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import io.github.rosemoe.sora.lang.analysis.AsyncIncrementalAnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.IncrementalAnalyzeManager;
import io.github.rosemoe.sora.lang.styling.CodeBlock;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.TextStyle;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.util.IntPair;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import kotlin.Pair;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

public abstract class BaseIncrementalAnalyzeManager extends AsyncIncrementalAnalyzeManager<LineState, IncrementalToken> {
    private final int[] blockTokens;
    protected final Lexer lexer;
    private final int[] multilineEndTypes;
    private final int[] multilineStartTypes;

    protected abstract List<Span> generateSpans(IncrementalAnalyzeManager.LineTokenizeResult<LineState, IncrementalToken> lineTokenizeResult);

    protected abstract int[] getCodeBlockTokens();

    protected abstract int[][] getMultilineTokenStartEndTypes();

    protected abstract void handleIncompleteToken(IncrementalToken incrementalToken);

    public BaseIncrementalAnalyzeManager(Class<? extends Lexer> lexer) {
        Objects.requireNonNull(lexer, "Cannot create analyzer manager for null lexer");
        this.lexer = createLexerInstance(lexer);
        int[][] multilineTokenTypes = getMultilineTokenStartEndTypes();
        verifyMultilineTypes(multilineTokenTypes);
        this.multilineStartTypes = multilineTokenTypes[0];
        this.multilineEndTypes = multilineTokenTypes[1];
        int[] tokens = getCodeBlockTokens();
        this.blockTokens = tokens == null ? new int[0] : tokens;
    }

    private Lexer createLexerInstance(Class<? extends Lexer> lexer) {
        try {
            Constructor<? extends Lexer> constructor = lexer.getConstructor(CharStream.class);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(createStream(""));
        } catch (Throwable err) {
            throw new RuntimeException("Unable to create Lexer instance", err);
        }
    }

    protected CharStream createStream(CharSequence source) {
        Objects.requireNonNull(source);
        try {
            return CharStreams.fromReader(new CharSequenceReader(source));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create CharStream for source", e);
        }
    }

    private void verifyMultilineTypes(int[][] types) {
        Preconditions.checkState(types.length == 2, "There must be exact two inner int[] in multiline token types");
        int[] start = types[0];
        int[] end = types[1];
        Preconditions.checkState(start.length > 0, "Invalid start token types");
        Preconditions.checkState(end.length > 0, "Invalid end token types");
    }

    public LineState getInitialState() {
        return new LineState();
    }

    public boolean stateEquals(LineState state, LineState another) {
        return state.equals(another);
    }

    public IncrementalAnalyzeManager.LineTokenizeResult<LineState, IncrementalToken> tokenizeLine(CharSequence lineText, LineState state, int line) {
        ArrayList<IncrementalToken> tokens = new ArrayList<>();
        int newState = 0;
        LineState stateObj = new LineState();
        if (state.state == 0) {
            newState = tokenizeNormal(lineText, 0, tokens, stateObj, state.lexerMode);
        } else if (state.state == 1) {
            long result = fillIncomplete(lineText, tokens, state.lexerMode);
            int newState2 = IntPair.getFirst(result);
            if (newState2 == 0) {
                newState = tokenizeNormal(lineText, IntPair.getSecond(result), tokens, stateObj, state.lexerMode);
            } else {
                newState = 1;
            }
        }
        stateObj.state = newState;
        stateObj.lexerMode = this.lexer._mode;
        return new IncrementalAnalyzeManager.LineTokenizeResult<>(stateObj, tokens);
    }

    public List<Span> generateSpansForLine(IncrementalAnalyzeManager.LineTokenizeResult<LineState, IncrementalToken> tokens) {
        List<Span> result = generateSpans(tokens);
        Objects.requireNonNull(result);
        if (result.isEmpty()) {
            result.add(Span.obtain(0, TextStyle.makeStyle(5)));
        }
        return result;
    }

    public List<CodeBlock> computeBlocks(Content text, AsyncIncrementalAnalyzeManager<LineState, IncrementalToken>.CodeBlockAnalyzeDelegate delegate) {
        Stack<CodeBlock> stack = new Stack<>();
        ArrayList<CodeBlock> blocks = new ArrayList<>();
        int line = 0;
        int maxSwitch = 0;
        int currSwitch = 0;
        while (delegate.isNotCancelled() && line < text.getLineCount()) {
            IncrementalAnalyzeManager.LineTokenizeResult<LineState, IncrementalToken> tokens = getState(line);
            boolean checkForIdentifiers = ((LineState) tokens.state).state == 0 || (((LineState) tokens.state).state == 1 && tokens.tokens.size() > 1);
            if (!((LineState) tokens.state).hasBraces && !checkForIdentifiers) {
                line++;
            } else {
                for (int i = 0; i < tokens.tokens.size(); i++) {
                    IncrementalToken token = (IncrementalToken) tokens.tokens.get(i);
                    int offset = token.getStartIndex();
                    if (isCodeBlockStart(token)) {
                        if (stack.isEmpty()) {
                            if (currSwitch > maxSwitch) {
                                maxSwitch = currSwitch;
                            }
                            currSwitch = 0;
                        }
                        currSwitch++;
                        CodeBlock block = new CodeBlock();
                        block.startLine = line;
                        block.startColumn = offset;
                        stack.push(block);
                    } else if (isCodeBlockEnd(token) && !stack.isEmpty()) {
                        CodeBlock block2 = stack.pop();
                        block2.endLine = line;
                        block2.endColumn = offset;
                        if (block2.startLine != block2.endLine) {
                            blocks.add(block2);
                        }
                    }
                }
                line++;
            }
        }
        blocks.sort(CodeBlock.COMPARATOR_END);
        return blocks;
    }

    protected boolean isCodeBlockStart(IncrementalToken token) {
        return this.blockTokens.length == 2 && token.getType() == this.blockTokens[0];
    }

    protected boolean isCodeBlockEnd(IncrementalToken token) {
        return this.blockTokens.length == 2 && token.getType() == this.blockTokens[1];
    }

    protected boolean isIncompleteTokenStart(EvictingQueue<IncrementalToken> q) {
        return matchTokenTypes(this.multilineStartTypes, q);
    }

    protected boolean isIncompleteTokenEnd(EvictingQueue<IncrementalToken> q) {
        return matchTokenTypes(this.multilineEndTypes, q);
    }

    protected IncrementalToken nextToken() {
        return new IncrementalToken(this.lexer.nextToken());
    }

    protected void popTokensAfterIncomplete(IncrementalToken incompleteToken, List<IncrementalToken> tokens) {
        tokens.remove(tokens.size() - 1);
    }

    protected void handleLineCommentSpan(IncrementalToken token, List<Span> spans, int offset) {
        int commentType = 22;
        String commentText = token.getText();
        if (commentText.length() > 2) {
            String commentText2 = commentText.substring(2).trim();
            if ("todo".equalsIgnoreCase(commentText2.substring(0, 4))) {
                commentType = NewScheme.TODO_COMMENT;
            } else if ("fixme".equalsIgnoreCase(commentText2.substring(0, 5))) {
                commentType = NewScheme.FIXME_COMMENT;
            }
        }
        spans.add(Span.obtain(offset, NewScheme.withoutCompletion(commentType)));
    }

    protected int tokenizeNormal(CharSequence line, int column, List<IncrementalToken> tokens, LineState st, int lexerMode) {
        this.lexer.setInputStream(createStream(line));
        if (this.lexer._mode != lexerMode) {
            this.lexer.pushMode(lexerMode);
        }
        Pair<EvictingQueue<IncrementalToken>, EvictingQueue<IncrementalToken>> queues = createEvictingQueueForTokens();
        EvictingQueue<IncrementalToken> start = queues.getFirst();
        EvictingQueue<IncrementalToken> end = queues.getSecond();
        boolean isInIncompleteToken = false;
        int state = 0;
        IncrementalToken incompleteToken = null;
        while (true) {
            IncrementalToken token = nextToken();
            if (token == null || token.getType() == -1) {
                break;
            }
            if (token.getStartIndex() >= column) {
                if (!isInIncompleteToken) {
                    if (token.getStartIndex() == column && !tokens.isEmpty()) {
                        token.type = tokens.get(tokens.size() - 1).getType();
                    }
                    tokens.add(token);
                }
                start.add(token);
                end.add(token);
                int type = token.getType();
                if (contains(getCodeBlockTokens(), type)) {
                    st.hasBraces = true;
                }
                if (start.remainingCapacity() == 0 && isIncompleteTokenStart(start)) {
                    isInIncompleteToken = true;
                    incompleteToken = (IncrementalToken) start.poll();
                    popTokensAfterIncomplete((IncrementalToken) Objects.requireNonNull(incompleteToken), tokens);
                } else if (end.remainingCapacity() == 0 && isIncompleteTokenEnd(end)) {
                    isInIncompleteToken = false;
                    incompleteToken = null;
                }
                if (isInIncompleteToken) {
                    state = 1;
                }
            }
        }
        if (incompleteToken != null) {
            incompleteToken.incomplete = true;
            handleIncompleteToken(incompleteToken);
        }
        return state;
    }

    protected long fillIncomplete(CharSequence line, List<IncrementalToken> tokens, int lexerMode) {
        this.lexer.setInputStream(createStream(line));
        if (this.lexer._mode != lexerMode) {
            this.lexer.pushMode(lexerMode);
        }
        Pair<EvictingQueue<IncrementalToken>, EvictingQueue<IncrementalToken>> queue = createEvictingQueueForTokens();
        EvictingQueue<IncrementalToken> end = queue.getSecond();
        List<IncrementalToken> allTokens = this.lexer.getAllTokens().stream().map(obj ->(new IncrementalToken(obj))).collect(Collectors.toList());
        if (allTokens.isEmpty()) {
            return IntPair.pack(1, 0);
        }
        boolean completed = false;
        int index = 0;
        while (true) {
            if (index >= allTokens.size()) {
                break;
            }
            IncrementalToken token = allTokens.get(index);
            if (token.getType() != -1) {
                end.add(token);
                if (end.remainingCapacity() != 0 || !isIncompleteTokenEnd(end)) {
                    index++;
                } else {
                    completed = true;
                    break;
                }
            } else {
                break;
            }
        }
        IncrementalToken first = allTokens.get(0);
        int offset = allTokens.get(completed ? index : index - 1).getStartIndex();
        first.startIndex = 0;
        handleIncompleteToken(first);
        tokens.add(first);
        if (completed) {
            return IntPair.pack(0, offset);
        }
        return IntPair.pack(1, offset);
    }
    public static boolean contains(int[] array, int valueToFind) {
        return lastIndexOf(array, valueToFind,0) != -1;
    }
    
    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    private Pair<EvictingQueue<IncrementalToken>, EvictingQueue<IncrementalToken>> createEvictingQueueForTokens() {
        return new Pair<>(EvictingQueue.create(this.multilineStartTypes.length), EvictingQueue.create(this.multilineEndTypes.length));
    }

    private boolean matchTokenTypes(int[] types, EvictingQueue<IncrementalToken> tokens) {
        IncrementalToken[] arr = (IncrementalToken[]) tokens.toArray(new IncrementalToken[0]);
        for (int i = 0; i < types.length; i++) {
            if (types[i] != arr[i].getType()) {
                return false;
            }
        }
        return true;
    }
}
