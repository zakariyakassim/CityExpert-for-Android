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

public class PlaceDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "favourites2";

    Context context;

    private static final String TABLE_PLACES = "favourite_places";

    private static final String PLACENAME = "placename";
    private static final String IMAGE = "image";
    private static final String ADDRESS = "address";
    private static final String DISTANCE = "distance";
    private static final String TYPE = "type";
    private static final String LAT = "lat";
    private static final String LNG = "lng";

    public String PLACENAME(){
        return PLACENAME;
    }
    public String IMAGE(){
        return IMAGE;
    }
    public String ADDRESS(){
        return ADDRESS;
    }
    public String DISTANCE(){
        return DISTANCE;
    }
    public String TYPE(){
        return TYPE;
    }
    public String LAT(){
        return LAT;
    }
    public String LNG(){
        return LNG;
    }


    public PlaceDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //String name, String image, String address, String distance, String place_type

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PLACES + "(" + PLACENAME + " VARCHAR," + IMAGE + " VARCHAR," + ADDRESS + " VARCHAR," + DISTANCE + " VARCHAR," + TYPE + " VARCHAR," + LAT + " VARCHAR," + LNG + " VARCHAR);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }


    public Boolean ifExist(String address){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean ss;
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_PLACES+" WHERE TRIM("+ADDRESS+") = '" + address.trim() + "'", null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String getAddress = cursor.getString(cursor
                    .getColumnIndex(ADDRESS));
            ss = getAddress.equals(address);
        }else {
            ss = false;
        }
        return ss;

    }

    public void addPlaceToFav(String placename, String image, String address, String distance, String type, String lat, String lng) {
        if(ifExist(address)){

            CharSequence text = "Already in your favourites";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PLACENAME, placename);
            values.put(IMAGE, image);
            values.put(ADDRESS, address);
            values.put(DISTANCE, distance);
            values.put(TYPE, type);
            values.put(LAT, lat);
            values.put(LNG, lng);
            // Inserting Row
            db.insert(TABLE_PLACES, null, values);
            db.close(); // Closing database connection

            CharSequence text = "Added to favourites";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }


    public ArrayList<HashMap<String,String>> getAllPlaces() {
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_PLACES, null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {


                String name = cursor.getString(cursor
                        .getColumnIndex(PLACENAME));
                String image = cursor.getString(cursor
                        .getColumnIndex(IMAGE));
                String address = cursor.getString(cursor
                        .getColumnIndex(ADDRESS));
                String distance = cursor.getString(cursor
                        .getColumnIndex(DISTANCE));
                String type = cursor.getString(cursor
                        .getColumnIndex(TYPE));
                String lat = cursor.getString(cursor
                        .getColumnIndex(LAT));
                String lng = cursor.getString(cursor
                        .getColumnIndex(LNG));

                HashMap map = new HashMap();

                map.put(PLACENAME,name);
                map.put(IMAGE,image);
                map.put(ADDRESS,address);
                map.put(DISTANCE,distance);
                map.put(TYPE,type);
                map.put(LAT,lat);
                map.put(LNG,lng);


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

    public int deleteEach(String address) {

        CharSequence text = "Deleted from favourites";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PLACES,
                ADDRESS+" = ? ",
                new String[]{address});


    }

    public int deleteAll()   {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PLACES, null,null);
    }

/*
    public Boolean ifExist(String stopcode){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean ss;
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_PLACES+" WHERE TRIM("+STOPCODE+") = '" + stopcode.trim() + "'", null);
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
            db.insert(TABLE_PLACES, null, values);
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
        Cursor cursor = db.rawQuery("select * from "+TABLE_PLACES, null);

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
        return db.delete(TABLE_PLACES,
                STOPCODE+" = ? ",
                new String[]{stopcode});


    }

    public int deleteAll()   {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PLACES, null,null);
    }



    /*
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from appointments", null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                String getTitle = cursor.getString(cursor
                        .getColumnIndex("title"));
                String getTime = cursor.getString(cursor
                        .getColumnIndex("time"));
                String getDetails = cursor.getString(cursor
                        .getColumnIndex("details"));
                String getDate = cursor.getString(cursor
                        .getColumnIndex("date"));

                list.add(new Appointment(getTitle,getTime,getDetails,getDate));


                cursor.moveToNext();
            }
        }


        return list;
    }

    public void addAppointment(String title, String time, String details, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(TIME, time);
        values.put(DETAILS, details);
        values.put(DATE, date);
        // Inserting Row
        db.insert(TABLE_APPOINTMENTS, null, values);
        db.close(); // Closing database connection
    }


    public Boolean ifExist(String title, String date){
       SQLiteDatabase db = this.getWritableDatabase();
        Boolean ss;
       Cursor cursor = db.rawQuery("SELECT * FROM appointments WHERE TRIM(title) = '" + title.trim() + "'", null);
        if(cursor.getCount() > 0){
        cursor.moveToFirst();
            String getDate = cursor.getString(cursor
                    .getColumnIndex("date"));
            ss = date.equals(getDate);
        }else {
            ss = false;
        }
       return ss;

    }



    public int deleteEach(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("appointments",
                "title = ? ",
                new String[]{title});
    }


    public int delete(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("appointments",
                "date = ? ",
                new String[]{date});
    }


    public int getDateByTitle(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        return  0;
    }





    public ArrayList<Appointment> getByTitle(String title) {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM appointments WHERE TRIM(title) = '" + title.trim() + "'", null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                String getTitle = cursor.getString(cursor
                        .getColumnIndex("title"));
                String getTime = cursor.getString(cursor
                        .getColumnIndex("time"));
                String getDetails = cursor.getString(cursor
                        .getColumnIndex("details"));
                String getDate = cursor.getString(cursor
                        .getColumnIndex("date"));

                list.add(new Appointment(getTitle, getTime, getDetails, getDate));


                cursor.moveToNext();
            }
        }


        return list;


    }



    public ArrayList<Appointment> getByDate(String date) {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM appointments WHERE TRIM(date) = '" + date.trim() + "' ORDER BY time ASC", null);

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                String getTitle = cursor.getString(cursor
                        .getColumnIndex("title"));
                String getTime = cursor.getString(cursor
                        .getColumnIndex("time"));
                String getDetails = cursor.getString(cursor
                        .getColumnIndex("details"));
                String getDate = cursor.getString(cursor
                        .getColumnIndex("date"));

                list.add(new Appointment(getTitle, getTime, getDetails, getDate));


                cursor.moveToNext();
            }
        }


        return list;

    }

    public int updateDate(String newDate, String title){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("date", newDate);

        return db.update(TABLE_APPOINTMENTS, newValues, "title=?", new  String[]{title});
    }

    public int updateTitle(String newTitle, String oldTitle){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("title", newTitle);

        return db.update(TABLE_APPOINTMENTS, newValues, "title=?", new  String[]{oldTitle});

    }

    public int updateTime(String newTime, String oldTime){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("time", newTime);

        return db.update(TABLE_APPOINTMENTS, newValues, "time=?", new String[]{oldTime});

    }

    public int updateDetails(String newDetails, String oldDetails, String title){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("details", newDetails);

        return db.update(TABLE_APPOINTMENTS, newValues, "details=? AND title=?", new String[]{oldDetails,title});

    }*/

}