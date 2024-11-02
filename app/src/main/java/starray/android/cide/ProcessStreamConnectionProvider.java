package starray.android.cide;
import android.util.Log;

import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProcessStreamConnectionProvider implements StreamConnectionProvider {
    private static final String TAG = "ProcessStreamConnectionProvider";
    Process process;
    AsyncProcess builder;
    public ProcessStreamConnectionProvider(AsyncProcess builder) {
       this.builder = builder;
    }

    @Override
    public InputStream getInputStream() {
        return process.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    @Override
    public void start() {
        try {
            builder.start();
            Log.i("","Start");
            //休眠5秒以等待语言服务器初始化完成
        } catch (Exception e) {}
        process = builder.getProcess();
        Log.i("",process.toString());
    }

    @Override
    public void close() {
        process.destroy();
    }
    
}
