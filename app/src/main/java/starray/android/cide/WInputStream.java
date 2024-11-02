package starray.android.cide;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WInputStream extends InputStream {
    InputStream father;
    public WInputStream(InputStream ori) {
        father = ori;
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        Log.i("input",new String(b,off,len));
        return super.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        Log.i("input",new String(b));
        return super.read(b);
    }

    @Override
    public int read() throws IOException {
        return father.read();
    }
}
