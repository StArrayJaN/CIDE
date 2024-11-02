package starray.android.cide;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class AsyncProcess {

	private String[] cmd;
	private ProcessBuilder processBuilder;
	private Process commandProcess;
	private ProcessCallbak listener;

	private AsyncProcess() {};

    public AsyncProcess(String... cmd) {
		this.cmd = cmd;
		processBuilder =  new ProcessBuilder();
	}

	public AsyncProcess(List<String> cmd) {
		this.cmd = cmd.toArray(new String[0]);
	}

	public interface ProcessCallbak {
		void onProcessExit(int value);
		void onCommandOutputUpdate(String output);
	}
	
	public void redirectErrorStream(boolean yes) {
		processBuilder.redirectErrorStream(yes);
	}

	public void setProcessCallbak(ProcessCallbak listener) {
		this.listener = listener;
	}
    
    public ProcessBuilder getBuilder() {
        return processBuilder;
    }

	public void start() throws IOException{
		processBuilder.command(cmd);
		commandProcess = processBuilder.start();
		Log.i("Process",commandProcess.toString());
		Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (listener != null) {
							readOutput();
							listener.onProcessExit(commandProcess.waitFor());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		thread.start();
	}

	public boolean isStarted() {
		return commandProcess != null;
	}

	public Process getProcess() {
		return commandProcess;
	}

	private void readOutput() {
		try {
			InputStream in = commandProcess.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String temp;
			while ((temp = br.readLine()) != null) {
				// 拼接换行符
				if (listener != null) {
					listener.onCommandOutputUpdate(temp);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


