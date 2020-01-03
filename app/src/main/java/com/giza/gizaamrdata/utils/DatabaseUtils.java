package com.giza.gizaamrdata.utils;

import android.content.Context;
import com.giza.gizaamrdata.data.C;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hossam.
 */
public class DatabaseUtils {
    /**
     * *******************************************
     * Copies your database from your local
     * assets-folder to the just created empty
     * database in the system folder, from
     * where it can be accessed and handled.
     * This is done by transferring bytestream.
     * *******************************************
     */
    private static void copyDataBase(Context myContext) throws IOException {

        String DB_NAME = "MetersDB.db";
        String DB_PATH = myContext.getDatabasePath(C.Database.NAME).getPath();

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);


        // Path to the just created empty db
        String outFileName = DB_PATH;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}
