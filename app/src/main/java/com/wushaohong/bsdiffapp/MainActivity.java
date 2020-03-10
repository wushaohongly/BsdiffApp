package com.wushaohong.bsdiffapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 权限申请
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions,0);
    }

    public void onClickUpdate(View view) {

        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... voids) {

                // 差分包路径
                String patchPath = new File(Environment.getExternalStorageDirectory(), "patch").getAbsolutePath();
                String sourcePath = getApplicationInfo().sourceDir;
                String outputPath = createNewApk().getAbsolutePath();

                Log.e("wush", outputPath);

                if (!new File(patchPath).exists()) {
                    return null;
                }
                bsPatch(sourcePath, outputPath, patchPath);
                return new File(outputPath);
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);

                if (file != null) {
                    if (file.exists()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Uri fileUri = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationInfo().packageName + ".fileprovider", file);
                            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        }
                        MainActivity.this.startActivity(intent);
                    }
                }

            }
        }.execute();

    }

    private File createNewApk() {
        File newApk = new File(Environment.getExternalStorageDirectory(), "bsdiff.apk");
        if (!newApk.exists()) {
            try {
                newApk.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newApk;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void bsPatch(String sourceDir, String outputFilePath, String patchFilePath);
}
