package tw.org.iii.a08_2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView content;
    private File sdroot, approot;

    private void init() {
        content = findViewById(R.id.content);
        sp = getSharedPreferences("az", MODE_PRIVATE); //曾對檔名為"az"的檔案進行讀資料的動作
        editor = sp.edit();

        sdroot = Environment.getExternalStorageDirectory();
        Log.v("az", sdroot.getAbsolutePath());

        approot = new File(sdroot, "Android/data/" + getPackageName());
        if (approot.exists()) {
            approot.mkdirs(); //mkdir建出此路徑，"+s"副目錄不在建立副目錄
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 9487);
        } else {
            init();
        }
    }

    @Override //詳見AndroidDevelopers-AppPermissions-RequestAppPermissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9487) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                finish(); //沒給權限則關閉
            }
        }
    }

    public void test1(View view) {
        editor.putString("username", "brad");
        editor.putBoolean("sound", false);
        editor.putInt("stage", 4);
        editor.commit(); //真正把值給寫下去
        Toast.makeText(this, "save ok ", Toast.LENGTH_SHORT).show();
    }

    public void test2(View view) {
        boolean isSound = sp.getBoolean("sound", true);
        sp.getBoolean("sound", true);
        String username = sp.getString("username", "nobody");
        int stage = sp.getInt("stage", 1);
        Log.v("az", username + ":" + stage + ":" + isSound);
    }

    public void test3(View view) {   ///寫個按鈕能夠寫資料至檔案 FEAT.字串資料轉Bytes
        try {
            FileOutputStream fout = openFileOutput("az", MODE_PRIVATE);//output輸出串流:第一參數:檔名(不一定要有副檔名(作業系統使用))
            //MODE_APPEND主要寫LOG模式，會有複數資料產生
            fout.write("Hello,World".getBytes()); //寫入串流內容 //利用.getBytes()將字串資料轉為Bytes格式
            fout.flush(); //當輸出為檔案前，先flush出來
            fout.close();
            Toast.makeText(this, "Save ok ", Toast.LENGTH_SHORT).show();//要show才看得到
        } catch (IOException e) {
            Log.v("az", e.toString());
        }
    }  //拿到的是String，單位皆為Byte...
    //OpenFile將檔案存在內存，資料與app共存亡
    //AutoCloseable:含此interface的API會自動結束，不須跳出

    public void test4(View view) {  //讀資料，觀念:讀取串流
        try (FileInputStream fin = openFileInput("az")) { //讀取檔案az來取得串流
            StringBuffer sb = new StringBuffer();
            //int c; 讀一個
            byte[] buf = new byte[1024];
            int len;//宣告長度來知道長度有多少
            while ((len = fin.read(buf)) != -1) {    //當還有資料，則持續讀取直到-1表示沒有資料
                //Log.v("az","=>"+ c);
                sb.append(new String(buf, 0, len)); //從0開始由buf中取出長度為len的字串
                Log.v("az", "=>" + (char) len);
            }//fin.read();  //讀資料以byte為單位
            content.setText(sb.toString());
        } catch (Exception e) {
            Log.v("az", e.toString());
        }
    }

    public void test5(View view) {
        File file1 = new File(sdroot, "az.ok");
        try {
            FileOutputStream fout =
                    new FileOutputStream(file1);
            fout.write("Hello, Brad".getBytes());
            fout.flush();
            fout.close();
            Toast.makeText(this, "Save OK1",
                    Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.v("az", e.toString());
        }
    }
    public void test6(View view) {
        File file1 = new File(approot, "az.ok");
        try {
            FileOutputStream fout =
                    new FileOutputStream(file1);
            fout.write("Hello, Brad".getBytes());
            fout.flush();
            fout.close();
            Toast.makeText(this, "Save OK1",
                    Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.v("az", e.toString());
        }

    }
}
//android/os資料庫皆採用SQLite
