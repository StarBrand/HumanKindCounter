package cl.humankind.humankindcounter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * This code is adapted from Johann Pardanaud's (https://gist.github.com/nesk) tutorial, available:
 * https://medium.com/@johann.pardanaud/ship-an-android-app-with-a-pre-populated-database-cd2b3aa3311f
 *
 */
public class SanctuariesDatabaseHelper extends SQLiteOpenHelper {

    private static final String msg = "Sanctuaries Database : ";
    private static final int VERSION = 2;
    private static final String NAME = "sanctuaries.db";
    private static final String ASSETS = "databases";
    private Context context;

    SanctuariesDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Database already exist
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database already exist
    }

    private void installDatabaseFromAssets(){
        if ((!checkDatabaseExist()) || checkIfOutdated()) {
            try {
                if (context.deleteDatabase(NAME)){
                    Log.d(msg, "Database deleted");
                } else Log.d(msg, "Database not deleted");
                Log.d(msg, "Copying database from assets");
                InputStream inputStream = context.getAssets().open(
                        String.format("%s/%s", ASSETS, NAME)
                );
                File outputFile = new File(
                        context.getDatabasePath(NAME).getAbsolutePath()
                );
                OutputStream outputStream = new FileOutputStream(outputFile);
                int bytes = IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                Log.d(msg, "Bytes written " + bytes);
            } catch (Exception e) {
                Log.w(msg, "Sanctuaries database could not be installed");
                Log.w(msg, Arrays.toString(e.getStackTrace()));
            }
        } else if (checkIfOutdated()) {
            // TODO: do action to update just sanctuaries and version
            Log.d(msg, "Database exists but is outdated");
        } else {
            // do nothing
            Log.d(msg, "Database exists and it is up-to-date");
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        installDatabaseFromAssets();
        return super.getReadableDatabase();
    }

    /**
     * Checks if Database is updated by querying version table
     *
     * @return If Updated
     */
    private boolean checkIfOutdated(){
        SQLiteDatabase test = SQLiteDatabase.openDatabase(
                context.getDatabasePath(NAME).getAbsolutePath(),
                null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = test.rawQuery("SELECT * FROM version", null);
        cursor.moveToFirst();
        int version = cursor.getInt(0);
        cursor.close();
        Log.d(msg, "Database on version " + version);
        return version < VERSION;
    }

    /**
     * Check if database exists or version table exists
     *
     * @return If exist
     */
    private boolean checkDatabaseExist(){
        try {
            SQLiteDatabase test = SQLiteDatabase.openDatabase(
                    context.getDatabasePath(NAME).getAbsolutePath(),
                    null, SQLiteDatabase.OPEN_READONLY);
            try {
                Cursor cursor = test.rawQuery("SELECT * FROM version", null);
                cursor.close();
                return true;
            } catch (SQLiteException e){
                Log.w(msg, "No version table");
                return false;
            }
        } catch (SQLiteCantOpenDatabaseException e) {
            return false;
        }
    }

}
