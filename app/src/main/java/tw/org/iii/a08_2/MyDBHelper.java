package tw.org.iii.a08_2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final  String createTable =
            "CREATE TABLE user(id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",username TEXT,tel TEXT,birthday DATE)";//sql不分大小寫，但自訂的庫名/表名/欄位名有大小寫嚴格區分
    public MyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version); //版本數用整數處理
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
