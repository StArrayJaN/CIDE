package starray.android.cide;
import android.content.Context;

import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import android.util.AttributeSet;

public class ICodeEditor extends CodeEditor{
    
    public static final String TAG = ICodeEditor.class.getSimpleName();
    private File file;
    public ICodeEditor(Context context) {
		super(context);
	}
    
	public ICodeEditor(Context context, AttributeSet attr) {
		super(context,attr);
	}
	
	public void setFile(File file) throws IOException {
		setText(new String(Files.readAllBytes(file.toPath())));
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}

	/*@NonNull
	@Override
	public Content getText() {
		Content text = super.getText();
		APPUtils.APPLog.i("getText return:%S",text.toString());
		return text;
	}*/

	public void saveFile() throws IOException{
		Files.write(file.toPath(),getText().toString().getBytes());
	}
}
