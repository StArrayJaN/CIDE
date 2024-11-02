
package starray.android.cide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.system.Os;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;
import org.eclipse.lsp4j.services.LanguageServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.LanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.lsp.editor.LspProject;

@SuppressLint("All")
public class MainActivity extends AppCompatActivity {

    IDECodeEditor editor;
    LspEditor lspEditor;
    AsyncProcess process;
    ICodeEditor console;
    String clangdBinary;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            new Thread(() ->  {
            try {
                ExecutoTran.main(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}).start();
            initLayout();
            APPUtils.setMainActivity(this);
            clangdBinary = getDataDir() + "/ndk/android-ndk-r24/toolchains/llvm/prebuilt/linux-aarch64/bin/clangd";
            new File(getDataDir() +"/tmp").mkdir();
            Os.setenv("TMPDIR",getDataDir() +"/tmp",true);
            Os.setenv("HOME",getDataDir().toString(),true);
            if (new File(clangdBinary).exists()) {
                try {
                    Runtime.getRuntime().exec(new String[]{"chmod", "755", clangdBinary});
                AsyncProcess p = getProcess();
                p.start();
                p.getProcess().waitFor();
                } catch (Exception e) {
                    Log.e("", "", e);
                }
            }
            process = new AsyncProcess(clangdBinary);
            process.redirectErrorStream(true);
            process.setProcessCallbak(new AsyncProcess.ProcessCallbak() {
                @Override
                public void onCommandOutputUpdate(String output) {
                    APPUtils.APPLog.i(output);
                }

                @Override
                public void onProcessExit(int value) {

                }
            });

            new Thread(serverThread).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.run_command).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_command:
                runCommand();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runCommand() {
        View view = getLayoutInflater().inflate(R.layout.runcommand_layout, null);
        TextInputEditText textInputEditText = view.findViewById(R.id.input_command);
        TextInputLayout textInputLayout = view.findViewById(R.id.command_input_layout);
        TextView commandOutput = view.findViewById(R.id.command_output);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setTitle("Run Command")
                .setPositiveButton("运行",null)
                .show();
        Button BUTTON_POSITIVE = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        BUTTON_POSITIVE.setOnClickListener(v -> {
                    String command = Objects.requireNonNull(textInputEditText.getText()).toString();
                    if (!command.isEmpty()) {
                        textInputLayout.setVisibility(View.GONE);
                        commandOutput.setVisibility(View.VISIBLE);
                        String[] commands = command.split(" ");
                        AsyncProcess p = new AsyncProcess(commands);
                        p.redirectErrorStream(true);
                        p.setProcessCallbak(new AsyncProcess.ProcessCallbak() {
                            @Override
                            public void onCommandOutputUpdate(String output) {
                                runOnUiThread(() -> commandOutput.append(output + "\n"));
                            }
                            @Override
                            public void onProcessExit(int value) {
                                runOnUiThread(() -> {
                                    commandOutput.append("\n\n");
                                    if (value == 0) {
                                        commandOutput.append("Command executed successfully");
                                    } else
                                        commandOutput.append("Command executed with error code: " + value);
                                  BUTTON_POSITIVE.setText("关闭");
                                  BUTTON_POSITIVE.setOnClickListener((v) -> dialog.dismiss());
                                });

                            }
                        });
                        try {
                            p.start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }
    public void append(CharSequence sequence){
        runOnUiThread(() -> console.commitText(sequence));
    }
    private AsyncProcess getProcess() {
        AsyncProcess p = new AsyncProcess(getDataDir().toString() + "/ndk/android-ndk-r24/ndk-build","-C","/sdcard/MT2/jni","compile_commands.json");
        p.redirectErrorStream(true);
        p.setProcessCallbak(new AsyncProcess.ProcessCallbak() {
            @Override
            public void onProcessExit(int value) {
                Log.i("process", Objects.toString(value));
            }

            @Override
            public void onCommandOutputUpdate(String output) {
                Log.i("process",output);
            }
        });
        return p;
    }

    private EventHandler.EventListener listener = new EventHandler.EventListener() {

        @Override
        public void initialize(LanguageServer languageServer, InitializeResult initializeResult) {
            setText(new Gson().toJson(initializeResult), true);
        }

        @Override
        public void onShowMessage(MessageParams messageParams) {
            setText(new Gson().toJson(messageParams), true);
        }

        @Override
        public void onLogMessage(MessageParams messageParams) {
            setText(new Gson().toJson(messageParams), true);
            if (messageParams.getMessage() != null) {
                Log.e("message", messageParams.getMessage());
            }
        }

        @Override
        public void onHandlerException(Exception exception) {
            //setText(Log.getStackTraceString(exception), false);
        }
    };

    private void setText(String json, boolean b) {
        try {
            Log.i("",new JSONObject(json).toString(2));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void initLayout() throws IOException {
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, findViewById(R.id.drawer_layout), findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        editor = findViewById(R.id.code_editor);
        editor.setFile(new File(Environment.getExternalStorageDirectory() + "/MT2/jni/Source/EGL.cpp"));
        editor.setEditable(false);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(findViewById(R.id.ll_bottom_sheet));
        behavior.setPeekHeight(APPUtils.dip2px(this,50));
        console = findViewById(R.id.console);
        console.setEditable(false);
        console.setTextSize(5);
    }

    private final Runnable serverThread = new Runnable() {

        @Override
        public void run() {
            LanguageServerDefinition sd = new CustomLanguageServerDefinition("cpp", ext -> new SocketStreamConnectionProvider(2345,"127.0.0.1")) {
                @Override
                public EventHandler.EventListener getEventListener() {
                    return listener;
                }
            };
            final LspProject project = new LspProject(Environment.getExternalStorageDirectory()+"/MT2");
            project.addServerDefinition(sd);
            final Object lock = new Object();
            runOnUiThread(() -> {
                try {
                    lspEditor = project.createEditor(Environment.getExternalStorageDirectory() +"/MT2/jni/Source/EGL.cpp");
                    lspEditor.setWrapperLanguage(new JavaLanguage());
                    lspEditor.setEditor(editor);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                synchronized (lock) {
                    lock.notify();
                }
            });
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {}
            }

            boolean connected;
            try {
                lspEditor.connectWithTimeoutBlocking();
                var changeWorkspaceFoldersParams = new DidChangeWorkspaceFoldersParams();
                changeWorkspaceFoldersParams.setEvent(new WorkspaceFoldersChangeEvent());
                changeWorkspaceFoldersParams.getEvent().setAdded(List.of(new WorkspaceFolder("file://" +Environment.getExternalStorageDirectory() + "/MT2", "myProject")));
                Objects.requireNonNull(lspEditor.getRequestManager())
                        .didChangeWorkspaceFolders(
                                changeWorkspaceFoldersParams
                        );

                connected = true;
            } catch (Exception e) {
                connected = false;
                Log.wtf("", e);
            }
            final boolean finalConnected = connected;
            runOnUiThread(() -> {
                if (finalConnected) {
                    APPUtils.print("Initialized Language server");
                } else {
                    APPUtils.print("Unable to connect language server");
                }
                editor.setEditable(true);
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
