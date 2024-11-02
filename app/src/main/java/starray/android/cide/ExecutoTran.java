package starray.android.cide;

import android.system.ErrnoException;
import android.system.Os;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
public class ExecutoTran {

    public static void main(String[] args) throws IOException {
        int port = 2345; // 服务器端口
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        // 等待客户端连接
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected");
        try {
            Os.setenv("LANG", "en_US.UTF-8", true);
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
        FileOutputStream logFile = new FileOutputStream("/sdcard/clangd-log.txt");
        // 创建进程并获取其输入输出流
        ProcessBuilder processBuilder = new ProcessBuilder( APPUtils.getMainActivity().getDataDir() + "/ndk/android-ndk-r24/toolchains/llvm/prebuilt/linux-aarch64/bin/clangd","--offset-encoding=utf-8","--enable-config"); // 替换为实际可执行文件的路径
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
        OutputStream processInput = process.getOutputStream();
        InputStream processOutput = process.getInputStream();
        //InputStream processError = process.getErrorStream();

        // 将客户端Socket的输出流连接到进程的输入流

        new Thread(new Runnable() {
				public void run() {
					try {
						byte[] buffer = new byte[65536];
						int bytesRead;
						while ((bytesRead = clientSocket.getInputStream().read(buffer)) != -1) {
							((MainActivity)APPUtils.getMainActivity()).append(new String(buffer,0,bytesRead));
							processInput.write(buffer, 0, bytesRead);
							processInput.flush();
							logFile.write(buffer, 0, bytesRead);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							logFile.close();
							processInput.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();

        // 将进程的输出流和错误流连接到客户端Socket的输出流
        new Thread(new Runnable() {
				public void run() {
					try {
						byte[] buffer = new byte[65536];
						int bytesRead;
						// 处理标准输出
						while ((bytesRead = processOutput.read(buffer)) != -1) {
							//APPUtils.APPLog.i("O:" + new String(buffer, 0, bytesRead));
							((MainActivity)APPUtils.getMainActivity()).append(new String(buffer,0,bytesRead));
							clientSocket.getOutputStream().write(buffer, 0, bytesRead);
							logFile.write(buffer, 0, bytesRead);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							logFile.close();
							clientSocket.getOutputStream().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
	}
}
