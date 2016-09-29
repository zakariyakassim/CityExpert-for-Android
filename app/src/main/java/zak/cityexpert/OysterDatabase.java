package zak.cityexpert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class OysterDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "OYSTER";

    private static final String TABLE_USERS = "USERS";

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String PIN = "PIN";
    private static final String NAME = "NAME";

    Context context;

    public OysterDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" + NAME + " VARCHAR," + USERNAME + " VARCHAR," + PASSWORD + " VARCHAR," + PIN + " VARCHAR);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    public Boolean ifExist(String address){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean ss;
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_USERS+" WHERE TRIM("+USERNAME+") = '" + address.trim() + "'", null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String getAddress = cursor.getString(cursor
                    .getColumnIndex(USERNAME));
            ss = getAddress.equals(address);
        }else {
            ss = false;
        }
        return ss;
    }

    public void addUser(String name, String username, String password, String pin){

        if(ifExist(name)){

            CharSequence text = "Name already exists.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NAME, WordUtils.capitalize(name.toLowerCase()));
            values.put(USERNAME, username);
            values.put(PASSWORD, password);
            values.put(PIN, pin);
            // Inserting Row
            db.insert(TABLE_USERS, null, values);
            db.close(); // Closing database connection

            CharSequence text = "done";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }

    }

    public ArrayList<HashMap<String,String>> getByName(String name){

        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_USERS+" WHERE TRIM("+NAME+") = '"+name.trim()+"'", null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {

                HashMap map = new HashMap();

                map.put(NAME,cursor.getString(cursor
                        .getColumnIndex(NAME)));
                map.put(USERNAME,cursor.getString(cursor
                        .getColumnIndex(USERNAME)));
                map.put(PASSWORD,cursor.getString(cursor
                        .getColumnIndex(PASSWORD)));
                map.put(PIN,cursor.getString(cursor
                        .getColumnIndex(PIN)));

                list.add(map);

                cursor.moveToNext();
            }
        }

        return list;

    }

    public ArrayList<HashMap<String,String>> getAll(){

        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_USERS, null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {

                HashMap map = new HashMap();

                map.put(NAME,cursor.getString(cursor
                        .getColumnIndex(NAME)));
                map.put(USERNAME,cursor.getString(cursor
                        .getColumnIndex(USERNAME)));
                map.put(PASSWORD,cursor.getString(cursor
                        .getColumnIndex(PASSWORD)));
                map.put(PIN,cursor.getString(cursor
                        .getColumnIndex(PIN)));

                list.add(map);

                cursor.moveToNext();
            }
        }

        return list;

    }

    public int deleteUser(String name) {

        CharSequence text = "deleted";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS,
                NAME+" = ? ",
                new String[]{name});


    }


    public boolean dataIsAvailable()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_USERS, null);
        cursor.moveToFirst();
        int total = cursor.getCount();
        cursor.close();

        return total > 0;
    }

}
