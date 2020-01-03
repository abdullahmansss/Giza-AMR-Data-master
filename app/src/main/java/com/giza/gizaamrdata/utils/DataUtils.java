package com.giza.gizaamrdata.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import com.giza.gizaamrdata.models.Meter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author hossam.
 */
public class DataUtils {
    public static long convertToMillies(String strDate, String strFormat) {
        Calendar calendar = convertToCalendar(strDate, strFormat);
        if (calendar != null) {
            return calendar.getTime().getTime();
        } else {
            return 0;
        }
    }

    public static Calendar convertToCalendar(String strDate, String strFormat) {
        try {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            final DateFormat df = new SimpleDateFormat(strFormat, Locale.ENGLISH);
            calendar.setTime(Objects.requireNonNull(df.parse(strDate)));

            return calendar;
        } catch (Exception e) {
            return null;
        }
    }

    public static long getTimeInMillies(String timeCreated) {
        return convertToMillies(timeCreated, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String getDateFromMillies(String timeCreated) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeCreated));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
        return dateFormat.format(calendar.getTime());
    }

    public static String convertToString(Calendar calendar, String strFormat) {
        return convertToString(calendar, strFormat, Locale.ENGLISH);
    }

    public static String convertToString(Calendar calendar, String strFormat, Locale locale) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(strFormat, locale);
            String strDate = format.format(calendar.getTime());
            return strDate;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDate(String strDate, String originalFormat, String desiredFormat) {
        return formatDate(strDate, originalFormat, desiredFormat, Locale.ENGLISH);
    }

    public static String formatDate(String strDate, String originalFormat, String desiredFormat, Locale locale) {
        return convertToString(convertToCalendar(strDate, originalFormat), desiredFormat, locale);
    }


    public static void exportMeterFile(List<Meter> meters, Context context) throws IOException {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "MetersData.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer;
        FileWriter mFileWriter;

        // File exist
        if (f.exists() && !f.isDirectory()) {
            mFileWriter = new FileWriter(filePath, false);
            writer = new CSVWriter(mFileWriter);
        } else {
            writer = new CSVWriter(new FileWriter(filePath));
        }
        writer.writeNext(Meter.getHeadersNames());
        for (Meter m : meters  ) {
            writer.writeNext(m.toRow());
        }
        writer.close();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/csv");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        context.startActivity(Intent.createChooser(sendIntent, "SHARE"));
    }

    public static String formatPrice(int price) {
        return new DecimalFormat("#,##0.###").format(price);
    }
}
