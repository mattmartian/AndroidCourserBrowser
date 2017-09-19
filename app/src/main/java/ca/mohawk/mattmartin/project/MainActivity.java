/**
 * I, Matthew Martin, 000338807 certify that this material is my original work.
 * No other person's work has been used without due acknowledgement.
 */

package ca.mohawk.mattmartin.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * Activity: MainActivity
 * Description: This Main Activity will be the starting point of the program. The user will have functionality to refresh the database to check for new changes. They will also
 * be able to select a program number and a semester numbeer of the program chosen.
 * @author Matthew Martin
 */
public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener{



    //Declaration of object variables
    MyDBHelper dbhelper = new MyDBHelper(this);
    Spinner semSpinner;
    Button viewCourse;
    String semesterSelected;
    TextView semPrompt;
    Button refresh;
    ListView progView;

    /**
     * This onCreate method will call upon several other methods in order to allow the user to select a program and semester number that they choose
     * once a semester is chosen they can start another activity where courses swill be shown to select in order to see details
     * @param savedInstanceState the saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(),getResources().getText(R.string.programprompt),Toast.LENGTH_LONG).show();

        initialize();

        getData();

        setupDrawer();

        setPrograms();

        //When a semester is chosen in the spinner alert the user what semester they have chosen

        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semesterSelected = parent.getItemAtPosition(position).toString();
                Snackbar.make(view, getResources().getString(R.string.semesterchosen)+ "" + semesterSelected, Snackbar.LENGTH_SHORT).show();
            }

            //if nothing is selected prompt the user to select a semester
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast error = Toast.makeText(getApplicationContext(),getResources().getString(R.string.semesterprompt), Toast.LENGTH_SHORT);
                error.show();
            }
        });

    }

    /**
     * This method will initialize all of the object variables
     */

    public void initialize(){
        semSpinner = (Spinner) findViewById(R.id.semSpinner);
        viewCourse = (Button) findViewById(R.id.btnCourseList);
        semPrompt = (TextView) findViewById(R.id.txtSelect);
        refresh = (Button) findViewById(R.id.btnRefresh);

        //at the start of the program make these objects invisible
        semSpinner.setVisibility(View.INVISIBLE);
        viewCourse.setVisibility(View.INVISIBLE);
        semPrompt.setVisibility(View.INVISIBLE);
    }

    /**
     *This setPrograms method will go through the database and gather all the program numbers to display to the user in a listview
     */

    public void setPrograms(){

        SQLiteDatabase db = dbhelper.getReadableDatabase();


        //gather all the program numbers in the database and add them to an arraylist
        Cursor programCursor = db.query(true, "mytable", new String[] { "program"}, null, null, "program", null, null, null);

        ArrayList<Integer> programs = new ArrayList<Integer>();
        while(programCursor.moveToNext()){
            int program  = programCursor.getInt(programCursor.getColumnIndex("program"));
            programs.add(program);
        }

        programCursor.close();

        //set the arraylist into an adapter and set the adapter to the listview
        ArrayAdapter<Integer> programlist  =
                new ArrayAdapter<Integer>(this,android.R.layout.simple_list_item_1, programs);
        progView = (ListView) findViewById(R.id.programView);
        progView.setAdapter(programlist);

        progView.setOnItemClickListener(this);


    }

    /**
     * This setup drawer method will create the functionality for the navdrawer homebutton
     */

    public void setupDrawer(){
        ActionBar myToolbar = getSupportActionBar();
        myToolbar.setDisplayHomeAsUpEnabled(true);

        DrawerLayout mydrawerlayout = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle myactionbartiggle = new ActionBarDrawerToggle(
                this, mydrawerlayout, (R.string.open), (R.string.close));
        mydrawerlayout.addDrawerListener(myactionbartiggle);
        myactionbartiggle.syncState();
    }


    /**
     *This getData method will gather all of the data inside the webservice using the DownloadAsyncTask class
     */
    public void getData(){
        DownloadAsyncTask dl = new DownloadAsyncTask(this);
        // Build call to Webservice
        String uri = "https://csunix.mohawkcollege.ca/~geczy/mohawkprograms.php";

        dl.execute(uri);

    }


    /**
     *This onItemClick method will record the program number clicked in the listview and load the amount of semesters in that program and set it to
     * the spinner
     * @param parent the listview
     * @param view the activity
     * @param position the position of the item in the listview
     * @param id the id of the listview item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = dbhelper.getReadableDatabase();
                progView = (ListView) findViewById(R.id.programView);
                String progChoice = progView.getItemAtPosition(position).toString();

                    //store the program choice inside of a shared preference to be accessed later
                 SharedPreferences settings = getPreferences(0);
                 SharedPreferences.Editor editor = settings.edit();
                 editor.putString("programChoice", progChoice);
                 editor.commit();


                //get all of the semesters that are in the program specified by the user
                Cursor semester =db.rawQuery("SELECT DISTINCT semesterNum from mytable WHERE program = ? ORDER BY semesterNum ASC", new String[]{progChoice});

                ArrayList<Integer> semesters = new ArrayList<>();

                //add those semesters into an arraylist
                while(semester.moveToNext()){
                    int sem  = semester.getInt(semester.getColumnIndex("semesterNum"));
                    semesters.add(sem);
                }
                semester.close();

                //put that data into an array adapter and apply that adapter to the spinner
                ArrayAdapter<Integer> semesterlist  =
                        new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, semesters);
                //set the custom layout to the spinner
                semesterlist.setDropDownViewResource(R.layout.spinner_row);
                Spinner semSpinner = (Spinner) findViewById(R.id.semSpinner);
                semSpinner.setAdapter(semesterlist);

                //set a default selection
                semSpinner.setSelection(0, false);

                //make the objects involving choosing a semester visible
                semSpinner.setVisibility(View.VISIBLE);
                viewCourse.setVisibility(View.VISIBLE);
                 semPrompt.setVisibility(View.VISIBLE);

                //hide the functionality to refresh the database once a program is chosen
                refresh.setVisibility(View.INVISIBLE);

                //close the navdrawer
                DrawerLayout mydrawerlayout = (DrawerLayout) findViewById(R.id.activity_main);
                mydrawerlayout.closeDrawer(GravityCompat.START);

    }

    /**
     *This method will set up the efunctionality of the opening and closing of the navdrawer
     * @param item the option of open or close
     * @return the state of the navdrawer
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Find out the current state of the drawer (open or closed)
        DrawerLayout mydrawerlayout = (DrawerLayout) findViewById(R.id.activity_main);
        boolean isOpen = mydrawerlayout.isDrawerOpen(GravityCompat.START);

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // Home button - open or close the drawer
                if (isOpen == true) {
                    mydrawerlayout.closeDrawer(GravityCompat.START);
                } else {
                    mydrawerlayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     *This method will execute when the refresh database button is clicked. It will delete all the records currently existing, and reinsert records downloaded from the webservice
     * @param view the activity
     */

    public void refreshData(View view) {

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Log.d("log","Deleting Database...");
        db.delete("mytable", null, null);
        Log.d("log","Database Deleted");
        getData();
        Log.d("log","Database Restored");

        Toast.makeText(getApplicationContext(),getResources().getString(R.string.databaserefresh),Toast.LENGTH_SHORT).show();
    }

    /**
     *This method will execute when the view courses button is clicked, it will start the next activity and pass the users semester choice to the next activity
     * to be used
     * @param view the activity
     */

    public void courseList(View view) {
        Intent sendInput = new Intent(MainActivity.this,BrowseCourses.class);
        sendInput.putExtra("semChoice",semesterSelected);
        startActivity(sendInput);
    }
}
