package tw.org.iii.a08_2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;
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

        myDBHelper = new MyDBHelper(this,"mydb",null,1);
        db = myDBHelper.getReadableDatabase();
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

    // Query
    public void test7(View view) {
        Cursor c = db.query("user", null,null,null,null,null,null);
        //要全部資料都填入才行
        while (c.moveToNext()){
            String id = c.getString(0);//如何決定getXX的型別?有關於sql語法的運算/字串的function/年月日的計算，與程式無關
            String  username= c.getString(1);
            String tel = c.getString(2);
            String birthday = c.getString(3);
            Log.v("az",id + username + tel + birthday);

        }
    }

    public void test8(View view) {
        //String sql = insert into user(username,tel,birthday) values("aa,"bb,"cc);
        // db.execute(sql)
        ContentValues values = new ContentValues(); //用ContentValues的方式避免解碼攻擊
        values.put("username","aa");
        values.put("tel","1234567");
        values.put("birthday","2000-01-02");
        db.insert("user",null,values);
        test7(null);
    }


    public void test9(View view) {
        //delete from user where id = 2 and username ="brad"
        db.delete("user","id =? and username =?}",new String[]{"2","brad"});
        test7(null);            //避開風險 //透過sql字法來處理資料
    }

    public void test10(View view) {
        //update user set username = 'peter',tel='0912-123456' where id =4;
        ContentValues values = new ContentValues();
        values.put("username","peter");
        values.put("tel","0912-123456");
        db.update("user",null,"id=?",new String[]{"4"});
        test7(null);
    }
}
//android/os資料庫皆採用SQLite
//不可以使用程式來處理資料庫，當資料過多時，程式越跑越慢
//SQL可以做的事要交給SQL做
