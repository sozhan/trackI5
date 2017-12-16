package tmm.tracki5.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Arun on 19/02/16.
 */
public class CalendarUtil {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    //static String eventUriString;
    static Uri eventUriString;

    public static ArrayList<String> readCalendarEvent(Context context) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8){
            eventUriString = Uri.parse("content://com.android.calendar/events");
        } else  {
            eventUriString = Uri.parse("content://calendar/events");
        }

        Cursor cursor = context.getContentResolver()
                .query(eventUriString,
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);

        if(cursor!=null) {
            cursor.moveToFirst();
            // fetching calendars name
            String CNames[] = new String[cursor.getCount()];

            // fetching calendars id
            nameOfEvent.clear();
            startDates.clear();
            endDates.clear();
            descriptions.clear();
            for (int i = 0; i < CNames.length; i++) {

                nameOfEvent.add(cursor.getString(1));
//			startDates.add(getDate(Long.parseLong(cursor.getString(3).trim())));
//			endDates.add(getDate(Long.parseLong(cursor.getString(4).trim())));
                startDates.add(getDate(cursor.getLong(3)));
                endDates.add(getDate(cursor.getLong(4)));
                descriptions.add(cursor.getString(2));
                CNames[i] = cursor.getString(1);
                cursor.moveToNext();

            }
            return nameOfEvent;
        }else{
            return null;
        }
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

