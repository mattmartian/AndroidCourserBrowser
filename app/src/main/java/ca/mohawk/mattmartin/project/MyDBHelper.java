package ca.mohawk.mattmartin.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matt on 2017-04-15.
 */

/**
 * Class: MyDBHelper
 * Description: This class will create the database and the table for all the information that will be accessed by this application
 * This class will also be used as a helper to access the contents of the database
 * @author Matthew Martin
 */

public class MyDBHelper extends SQLiteOpenHelper {

    //Create a database with these columns
    private static final String SQL_CREATE = "CREATE TABLE mytable ( _id INTEGER PRIMARY KEY, program INTEGER, semesterNum INTEGER, courseCode TEXT, courseTitle TEXT, courseDescription TEXT, courseOwner TEXT, optional INTEGER, hours INTEGER)";

    private static final String DATABASE_NAME = "MyDatabase.db";
    private static final int DATABASE_VERSION = 1;


    //create the database helper tool
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
