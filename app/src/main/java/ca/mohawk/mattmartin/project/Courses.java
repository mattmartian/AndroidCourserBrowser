package ca.mohawk.mattmartin.project;

/**
 * Created by Matt on 2017-04-15.
 */

/**
 * Class: Courses
 * Description: Object model with parameters for the courses, semesters and programs
 * @author Matthew Martin
 */

public class Courses {
    public Integer   _id;
    public Integer  program;
    public Integer  semesterNum;
    public String   courseCode;
    public String   courseTitle;
    public String   courseDescription;
    public String   courseOwner;
    public Integer   optional;
    public Integer   hours;
}
