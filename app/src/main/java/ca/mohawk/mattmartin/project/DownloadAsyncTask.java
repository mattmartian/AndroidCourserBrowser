package ca.mohawk.mattmartin.project;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.TransactionTooLargeException;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Matt on 2017-04-15.
 */

/**
 * Class: DownloadAsyncTask
 * Description: This class will download all of the data from the URI webservice string that has all of the data for the course browser and put it into the database
 * @author Matthew Martin
 */
public class DownloadAsyncTask extends AsyncTask<String, Void, String>{

    private Activity myActivity;




    public DownloadAsyncTask(Activity inActivity) {
        myActivity = inActivity;
    }


    /**
     * This doInBackground method will download all of the data from the webservice behind the scenes the download
     * process will not be visible by the user
      * @param params the location of the URI/Webservice
     * @return the data from the webservice
     */
    @Override
    protected String doInBackground(String... params) {

        Log.d("log", "Starting Background Task");
        String results = "";

       //download the dat afrom the URI
        try {
            URL url = new URL(params[0]);           // was HttpGet

            // Open the Connection - GET is the default setRequestMethod
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Read the response
            int statusCode = conn.getResponseCode();
            Log.d("log", "Response Code: " + statusCode);


           // if the response code is 200 the download was sucessful
            if (statusCode == 200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = null;
                //insert each line of data into the results
                while ((line = bufferedReader.readLine()) != null) {
                    results += line;                // Use a StringBuilder class if expecting many lines!!
                }
            }

            //if unsuccessful, catch and display the error to the user
        } catch (IOException ex) {
            Toast.makeText(myActivity,myActivity.getResources().getText(R.string.dlUnuccessful),Toast.LENGTH_SHORT).show();
        }

        //return the data
        return results;
    }

    /**
     *The onPostExecute method will read each line of data, convert the json to Gson, and insert each record into the corresponding column of the database
     * @param result one line of data
     */

    protected void onPostExecute(String result) {
        Gson gson = new Gson();

        MyDBHelper dbhelper = new MyDBHelper(myActivity);

        //convert the data from json to Gson
        CourseList courselist = gson.fromJson(result, CourseList.class);


        SQLiteDatabase db = dbhelper.getWritableDatabase();

        //the values of the database
        ContentValues values = new ContentValues();


        //start the transaction
        db.beginTransaction();

        //for every course that is in the data, insert the corresponding records into each column of the databse
        try {
            for (Courses course : courselist) {  // loop through your records
                values.put("program", course.program);
                values.put("semesterNum", course.semesterNum);
                values.put("courseCode", course.courseCode);
                values.put("courseTitle", course.courseTitle);
                values.put("courseDescription", course.courseDescription);
                values.put("courseOwner", course.courseOwner);
                values.put("optional", course.optional);
                values.put("hours", course.hours);

                //create a new ID for each record
                long newRowId = db.insert("mytable", null, values);

                Log.d("log", "New ID " + newRowId);
            }

            //if the data is inserted properly, make the transaction successful
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            //if there was a problem inserting a record, let the user know
            Toast error = Toast.makeText(myActivity,myActivity.getResources().getText(R.string.recorderror), Toast.LENGTH_SHORT);
            error.show();
        } finally {
            //end the transaction
            db.endTransaction();
        }

        //close the databse
        db.close();

        Log.d("log", "dbclose");
    }
}
