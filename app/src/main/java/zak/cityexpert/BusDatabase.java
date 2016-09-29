package zak.cityexpert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BusDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "favourites";

    Context context;

    private static final String TABLE_STOPS = "favourite_stops";

    private static final String STOPNAME = "stopname";
    private static final String STOPCODE = "stopcode";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String DISTANCE = "distance";
    private static final String TOWARDS = "towards";

    public String STOPNAME(){
        return STOPNAME;
    }
    public String STOPCODE(){
        return STOPCODE;
    }
    public String LATITUDE(){
        return LATITUDE;
    }
    public String LONGITUDE(){
        return LONGITUDE;
    }
    public String DISTANCE(){
        return DISTANCE;
    }
    public String TOWARDS(){
        return TOWARDS;
    }


    public BusDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STOPS + "(" + STOPNAME
                + " VARCHAR," + STOPCODE + " VARCHAR," + LATITUDE + " VARCHAR," + LONGITUDE
                + " VARCHAR," + DISTANCE + " VARCHAR," + TOWARDS + " VARCHAR);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
        onCreate(db);
    }


    public Boolean ifExist(String stopcode){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean ss;
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_STOPS+" WHERE TRIM("+STOPCODE+") = '" + stopcode.trim() + "'", null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String getCode = cursor.getString(cursor
                    .getColumnIndex(STOPCODE));
            ss = getCode.equals(stopcode);
        }else {
            ss = false;
        }
        return ss;

    }


    public void addStopToFav(String stopname, String stopcode, String latitude, String longitude, String distance, String towards) {
        if(ifExist(stopcode)){

            CharSequence text = "Already in your favourites";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(STOPNAME, stopname);
            values.put(STOPCODE, stopcode);
            values.put(LATITUDE, latitude);
            values.put(LONGITUDE, longitude);
            values.put(DISTANCE, distance);
            values.put(TOWARDS, towards);
            // Inserting Row
            db.insert(TABLE_STOPS, null, values);
            db.close(); // Closing database connection

            CharSequence text = "Added to favourites";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }


    public ArrayList<HashMap<String,String>> getAllStops() {
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_STOPS, null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {

                String name = cursor.getString(cursor
                        .getColumnIndex(STOPNAME));
                String code = cursor.getString(cursor
                        .getColumnIndex(STOPCODE));
                String lat = cursor.getString(cursor
                        .getColumnIndex(LATITUDE));
                String lng = cursor.getString(cursor
                        .getColumnIndex(LONGITUDE));
                String distance = cursor.getString(cursor
                        .getColumnIndex(DISTANCE));
                String towards = cursor.getString(cursor
                        .getColumnIndex(TOWARDS));

                HashMap map = new HashMap();

                map.put(STOPNAME,name);
                map.put(STOPCODE,code);
                map.put(LATITUDE,lat);
                map.put(LONGITUDE,lng);
                map.put(DISTANCE,distance);
                map.put(TOWARDS,towards);


                list.add(map);


                cursor.moveToNext();
            }
        }

        Collections.sort(list, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1,
                               HashMap<String, String> o2) {
                return Double.compare(Double.parseDouble(o1.get(DISTANCE)), Double.parseDouble(o2.get(DISTANCE)));
            }
        });

        return list;
    }

    public int deleteEach(String stopcode) {

        CharSequence text = "Deleted from favourites";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STOPS,
                STOPCODE+" = ? ",
                new String[]{stopcode});


    }

    public int deleteAll()   {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STOPS, null,null);
    }



}