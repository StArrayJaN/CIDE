package starray.android.cide;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SetupActivity extends AppCompatActivity {

    private File ndkDIr;
    private Button downloadNDKButton;
    private Button importNDKButton;
    private Button requestPermissionButton;
    private TextInputEditText importNDKFilePath;
    private TextInputLayout inputLayout;
    private TextView NDKStatus;
    private TextView importNDKprogress;
    private Toolbar toolbar;
    private Button launchIDEButton;
    private TextInputEditText projectPath;
    private TextInputEditText sourcePath;
    private TextInputEditText compileDBPath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ndkDIr = new File(getDataDir(),"ndk");
        if (getSharedPreferences("cide",MODE_PRIVATE).getBoolean("inited",false)) {
            startActivity(new Intent(SetupActivity.this,MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_setup);
        APPUtils.setMainActivity(this);
        init();
    }

    public void init(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        downloadNDKButton = findViewById(R.id.download_android_ndk);
        importNDKButton = findViewById(R.id.import_android_ndk);
        requestPermissionButton = findViewById(R.id.request_permission);
        importNDKFilePath = findViewById(R.id.import_ndk_path);
        inputLayout = findViewById(R.id.import_ndk_path_layout);
        NDKStatus = findViewById(R.id.ndk_status);
        importNDKprogress = findViewById(R.id.import_ndk_progress);
        launchIDEButton = findViewById(R.id.launch_ide);
        projectPath = findViewById(R.id.project_path);
        sourcePath = findViewById(R.id.source_path);
        compileDBPath = findViewById(R.id.compile_db_path);

        projectPath.setTag("项目路径");
        sourcePath.setTag("源码路径");
        if (Permission.isPermissionGranted(this)) {
            requestPermissionButton.setEnabled(false);
            requestPermissionButton.setText("Permission Granted");
        }
        downloadNDKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Download NDK
                startActivity(new Intent(SetupActivity.this,MainActivity.class));
            }
        });
        importNDKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputLayout.getVisibility() != View.VISIBLE) {
                    inputLayout.setVisibility(View.VISIBLE);
                    importNDKButton.setText("导入");
                    return;
                }
                importNDK();
            }
        });
        requestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Permission.checkPermission(SetupActivity.this);
            }
        });
        launchIDEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyPath(projectPath,false) && verifyPath(sourcePath,true)) {
                    SharedPreferences.Editor editor = getSharedPreferences("cide",MODE_PRIVATE).edit();
                    editor.putString("projectPath",projectPath.getText().toString());
                    editor.putString("sourcePath",sourcePath.getText().toString());
                    editor.putString("compileDBPath",compileDBPath.getText().toString());
                    editor.putBoolean("inited",true);
                    editor.apply();
                    startActivity(new Intent(SetupActivity.this,MainActivity.class));
                }
            }
        });
    }

    private boolean verifyPath(TextInputEditText input,boolean isSourcePath){
        if (TextUtils.isEmpty(input.getText())) {
            APPUtils.print(input.getTag()+"不能为空!");
            return false;
        }
        if (isSourcePath) {
            if (!new File(projectPath.getText().toString(), input.getText().toString()).exists()) {
                APPUtils.print(input.getTag() + ":" + projectPath.getText().toString() + input.getText().toString() + ":无效!");
                return false;
            }
        } else if (!new File(input.getText().toString()).exists()) {
            APPUtils.print(input.getTag() + ":" + input.getText().toString() + ":无效!");
            return false;
        }
        return true;
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialogInterface, i) -> finish()).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        APPUtils.APPLog.i("Permission:onRequestPermissionsResult");
        if (requestCode == Permission.REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestPermissionButton.setEnabled(false);
                requestPermissionButton.setText("Permission Granted");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void importNDK(){
        String NDKPath = Objects.requireNonNull(importNDKFilePath.getText()).toString();
        if (TextUtils.isEmpty(NDKPath) || !new File(NDKPath).exists()) {
            Toast.makeText(this, "无效路径:"+NDKPath, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Permission.isPermissionGranted(this)) {
            Toast.makeText(this, "请先申请权限!", Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncProcess process = new AsyncProcess("tar","xvf",NDKPath,"-C",ndkDIr.getAbsolutePath());
        process.redirectErrorStream(true);
        importNDKprogress.setVisibility(View.VISIBLE);
        process.setProcessCallbak(new AsyncProcess.ProcessCallbak() {
            @Override
            public void onProcessExit(int value) {
                runOnUiThread(()-> {
                    if (value != 0) {
                        importNDKprogress.setText("导入失败,tar返回:" + value);
                        return;
                    }
                    importNDKprogress.setText("导入完成,tar返回:" + value);
                    downloadNDKButton.setVisibility(View.GONE);
                    NDKStatus.setText("必要操作完成，请启动");
                    importNDKButton.setText("已导入");
                    importNDKButton.setEnabled(false);
                    inputLayout.setVisibility(View.GONE);
                    findViewById(R.id.inflate_layout).setVisibility(View.GONE);
                    launchIDEButton.setVisibility(View.VISIBLE);
                });
            }
            @Override
            public void onCommandOutputUpdate(String output) {
                ndkDIr.mkdir();
                runOnUiThread(()-> importNDKprogress.setText(output));
            }
        });
        try {
            process.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
