package starray.android.cide;

import android.content.Context;
import android.util.AttributeSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import io.github.rosemoe.sora.widget.CodeEditor;

public class IDECodeEditor extends CodeEditor{
    
    public static final String TAG = IDECodeEditor.class.getSimpleName();
    private File file;
    public IDECodeEditor(Context context) {
		super(context);
	}
    
	public IDECodeEditor(Context context, AttributeSet attr) {
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
