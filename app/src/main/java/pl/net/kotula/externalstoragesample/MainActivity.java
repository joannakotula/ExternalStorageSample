package pl.net.kotula.externalstoragesample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String FILENAME_TO_READ = Environment.getExternalStorageDirectory() + "/sample/test.txt";
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else {
            fillTextViewWithContent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            fillTextViewWithContent();
        }
    }

    private void fillTextViewWithContent() {
        String content = getFileContent();
        TextView textView = findViewById(R.id.textView);
        textView.setText(content, TextView.BufferType.NORMAL);
    }

    private String getFileContent() {
        String content;
        try {
            content = Files.toString(new File(FILENAME_TO_READ), Charsets.UTF_8);
        } catch (IOException e) {
            Log.e(TAG, "Cannot read file: " + FILENAME_TO_READ, e);
            content = "If you want to use this - create file " + FILENAME_TO_READ;
        }
        return content;
    }
}
