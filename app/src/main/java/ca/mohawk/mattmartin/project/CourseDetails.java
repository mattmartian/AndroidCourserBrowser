package ca.mohawk.mattmartin.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Activity: Course Details
 * Description: activity to display a specific courses details based on the program that was chosen in the BrowseCourses activity
 * @author Matthew Martin
 */

public class CourseDetails extends AppCompatActivity {

    //db helper initialization
    MyDBHelper dbhelper = new MyDBHelper(this);
    ListView details;

    /**
     * The on create class will call the getDetails method to show the details of the course chosen
     * @param savedInstanceState the saved state of the application
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

         getDetails();
    }

    /**
     *Description: the getDetails method will recieve the course choice from the BrowseCourses activity and wil populate an array list with the specific
     * courses details which will be displayed to the user
     */

    public void getDetails(){

        //recieve data from BrowseCourses activity
        String requirement;
        Intent recieveChoice = getIntent();
        String courseChosen = recieveChoice.getStringExtra("courseChoice");



        //Select the details from the database based on the course chosen
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor detailGather = db.rawQuery("SELECT DISTINCT courseDescription, courseOwner, optional, hours from mytable WHERE _id = ?", new String[]{courseChosen});


        //iterate through the database and populate an array list with all of the details of the course
        ArrayList<String> coursedetail = new ArrayList<String>();
        coursedetail.add("                    " + "COURSE DETAILS" + "\n"+ "\n");
        while(detailGather.moveToNext()){
            String coursedesc  = detailGather.getString(detailGather.getColumnIndex("courseDescription"));
            String courseowner  = detailGather.getString(detailGather.getColumnIndex("courseOwner"));


            //if optional is 1, the course is an elective, if 0 it is required
            int optional  = detailGather.getInt(detailGather.getColumnIndex("optional"));
            if(optional == 1){
                requirement = "Elective";
            } else {
                requirement = "Required";
            }
            int hours  = detailGather.getInt(detailGather.getColumnIndex("hours"));

            //add to the array list with some styling
            coursedetail.add("Description: " + "\n" + coursedesc + "\n" + "\n" + "Owner: " + courseowner +  "\n" + "\n" + "Optional/Required: " + requirement +  "\n"  + "\n" + "Hours: " + hours + "hr/ Week" + "\n"+ "\n");
        }
        //close the cursor
        detailGather.close();

        //populate the listview and the adapter with data, then display to the user
        details = (ListView) findViewById(R.id.detailView);
        ArrayAdapter<String> detailslist  =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, coursedetail);
        details.setAdapter(detailslist);
    }
}
