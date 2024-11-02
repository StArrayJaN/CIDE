package starray.android.cide;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public abstract class WOutputStream extends OutputStream {
    OutputStream father;
    private WOutputStream(OutputStream outputStream) {
        father = outputStream;
    }

    public static WOutputStream forOutput(OutputStream outputStream) {
        return new WOutputStream(outputStream) {
            @Override
            public void write(byte[] b) throws IOException {
                Log.i("out",new String(b));
                super.write(b);
            }

            @Override
            public void write(int b) throws IOException {
                father.write(b);
            }
        };
    }

}
