package ca.mohawk.mattmartin.project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity: Browse Courses
 * Description: activity to display a list of courses to the user based on the program and semester that they had chosen in the main activity
 * @author Matthew Martin
 */

public class BrowseCourses extends AppCompatActivity implements ListView.OnItemClickListener{


    //initialization of object variables
    MyDBHelper dbhelper = new MyDBHelper(this);

    SimpleCursorAdapter adapter;
    ListView courseSelection;

    /**
     * The onCreate class will initialize some object variables and call the setCourses method
     * @param savedInstanceState the saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_courses);

        setCourses();
    }


    /**
     * This onclick method will send the contents of the item inisde the listview that is clicked by the user to the next
     * activity
     * @param parent the listview
     * @param view the activity's view
     * @param position the position of the item inside the listview
     * @param id the ID of the item inside the listview
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Intent sendInput = new Intent(BrowseCourses.this,CourseDetails.class);
        Cursor cursor = (Cursor) adapter.getItem(position);
        //match the id of the item inside the listview to the id inside the database
        int ID = cursor.getInt(cursor.getColumnIndex("_id"));

        //store the value and start the next activity
        sendInput.putExtra("courseChoice", String.valueOf(ID));
        startActivity(sendInput);

    }

    /**
     * Description: This set courses method will recieve both the semester and the program number that was chosen by the user in the MainActivity
     *  and will display a list of courses in that specific semester of the program they have chosen
     */
    public void setCourses(){
        //object variable initialization
        courseSelection = (ListView) findViewById(R.id.courseView);

        //recieve the data from the MainActivity class
        Intent recieveChoice = getIntent();
        SharedPreferences settings = getPreferences(0);

        //store the data into strings
        String semChoice = recieveChoice.getStringExtra("semChoice");
        String programchoice = settings.getString("programChoice", "559");

        //initilize a readable version of the database
        SQLiteDatabase db = dbhelper.getReadableDatabase();

       //Gather the course title's, and code's based on the program and semester number indicated previously by the user
        Cursor course =db.rawQuery("SELECT _id, courseCode , courseTitle from mytable WHERE semesterNum = ?  and program = ? GROUP BY courseCode", new String[]{semChoice,programchoice});

        //grab the data from these columns..
        String[] fromColumns = {"courseCode", "courseTitle"};

        //and set them to these textviews
        int[] toViews = {R.id.txtcoursecode, R.id.txtcoursetitle};

        //make a custom adapter to hold all of the data
        adapter = new SimpleCursorAdapter(this,
                R.layout.row, course, fromColumns, toViews, 0);

        // Fill the ListView with the Adapter
        courseSelection.setAdapter(adapter);

        Toast coursePrompt = Toast.makeText(getApplicationContext(),getResources().getText(R.string.courseprompt),Toast.LENGTH_SHORT);
        coursePrompt.show();

        courseSelection.setOnItemClickListener(this);
    }
}
