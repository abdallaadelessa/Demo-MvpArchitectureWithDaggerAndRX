package com.abdallaadelessa.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy";
    public static final String SHORT_DATE_FORMAT = "dd-MMM";
    public static final String FULL_DATE_FORMAT = "dd-MM-yyyy hh:mm";

    //---------------------> Time Units Conversion

    public static int convertMilliSecondsToMonths(long milliSeconds) {
        return (int) ((((((milliSeconds / 1000) / 60) / 60) / 24) / 30)) + 1;
    }

    public static long convertMilliSecondsToSeconds(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        return seconds;
    }

    public static long convertSecondsToMilliSeconds(long seconds) {
        long milli = seconds * 1000;
        return milli;
    }

    //---------------------> Epoch

    public static String convertEpochMilliSecondsToDefaultFormatedDate(long epochMilliSeconds, String format) {
        Date date = new Date(epochMilliSeconds);
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        String StringDate = sf.format(date);
        return StringDate;
    }

    public static long convertStringDateToEpochMilliSeconds(String date, String format) throws ParseException {
        long epochMilli = 0;
        if (date != null) {
            SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
            Date dateObject = sf.parse(date);
            epochMilli = dateObject.getTime();
        }
        return epochMilli;
    }

    //--------------------->

    public static boolean isToday(String dateInDateFormat, String format) {
        boolean isToday = false;
        if (!ValidationUtils.isStringEmpty(dateInDateFormat)) {
            String todayDate = convertEpochMilliSecondsToDefaultFormatedDate(System.currentTimeMillis(), format);
            if (!ValidationUtils.isStringEmpty(todayDate) && todayDate.equals(dateInDateFormat)) {
                isToday = true;
            }
        }
        return isToday;
    }

    public static long getEndMillisOfTheGivenDayMilliseconds(long miliiseconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(miliiseconds));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime().getTime();
    }

    public static int getAge(long userBirthDateInMillis) {
        int age = 0;
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTimeInMillis(userBirthDateInMillis);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) && (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    //--------------------->
}
