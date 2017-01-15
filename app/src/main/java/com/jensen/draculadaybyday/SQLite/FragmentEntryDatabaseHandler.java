package com.jensen.draculadaybyday.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jensen.draculadaybyday.Fragment.FragmentEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Spil_2 on 12-09-2016.
 */
public class FragmentEntryDatabaseHandler extends android.database.sqlite.SQLiteOpenHelper {

    // Contacts Table Columns names
    public static final String ENTRY_SEQ_NUM = "entrySeqNum";
    public static final String ENTRY_DATE_NUM = "entryDateNum";
    public static final String CHAPTER = "chapter";
    public static final String PERSON = "person";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String TYPE = "type";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "fragmentEntryManager";
    // Contacts table name
    private static final String TABLE_ENTRY = "entry";
    private static final String[] allEntries = new String[]{ENTRY_SEQ_NUM, ENTRY_DATE_NUM, CHAPTER, PERSON, TEXT, DATE, TYPE};
    private static final String[] allEntriesButText = new String[]{ENTRY_SEQ_NUM, ENTRY_DATE_NUM, CHAPTER, PERSON, DATE, TYPE};
    // The instance of the class
    private static FragmentEntryDatabaseHandler databaseHandler = null;
    // The database
    private SQLiteDatabase db;

    private FragmentEntryDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = this.getWritableDatabase();
        this.db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);
        this.onCreate(db);
    }

    public static FragmentEntryDatabaseHandler getInstance(Context context) {
        if (databaseHandler == null) {
            databaseHandler = new FragmentEntryDatabaseHandler(context);
        }

        return databaseHandler;
    }

    public static FragmentEntryDatabaseHandler getInstance() {
        return databaseHandler;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRY + "("
                + ENTRY_SEQ_NUM + " INTEGER PRIMARY KEY,"
                + ENTRY_DATE_NUM + " INTEGER,"
                + CHAPTER + " INTEGER,"
                + PERSON + " TEXT,"
                + TEXT + " TEXT,"
                + DATE + " TEXT,"
                + TYPE + " TEXT"
                + ")";
        db.execSQL(CREATE_ENTRY_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);

        // Create tables again
        onCreate(db);
    }

    public synchronized void open() throws SQLiteException {
        db = this.getWritableDatabase();
    }

    // Inserting an entry
    public void addEntry(FragmentEntry entry) {
        if (!db.isOpen()) {
            open();
        }

        // Check if the entry is already in the database
        if (!EntryAlreadyInDB(entry.getStoryEntryNum())) {

            ContentValues values = new ContentValues();
            values.put(ENTRY_SEQ_NUM, entry.getStoryEntryNum());
            values.put(ENTRY_DATE_NUM, entry.getDateEntryNum());
            values.put(CHAPTER, entry.getChapter());
            values.put(PERSON, entry.getPerson());
            values.put(TEXT, entry.getFragmentEntry());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            values.put(DATE, dateFormat.format(entry.getDate().getTime()));

            values.put(TYPE, entry.getType());


            // Inserting row
            db.insertOrThrow(TABLE_ENTRY, null, values);
        }

        // Closing database connection
        db.close();
    }

    private boolean EntryAlreadyInDB(short sequenceNumber) {
        if (!db.isOpen()) {
            this.open();
        }

        Cursor cursor = db.query(
                TABLE_ENTRY,
                new String[]{ENTRY_SEQ_NUM},
                ENTRY_SEQ_NUM + " = ?",
                new String[]{String.valueOf(sequenceNumber)},
                null,
                null,
                null);

        if (cursor != null && 0 < cursor.getCount()) {
            cursor.moveToFirst();
            return sequenceNumber == getShort(cursor, ENTRY_SEQ_NUM);
        }

        return false;
    }

    private Calendar makeDate(String date_rep) {
        int year = Integer.valueOf(date_rep.substring(0, 4));
        int month = Integer.valueOf(date_rep.substring(5, 7));
        int day = Integer.valueOf(date_rep.substring(8, 10));

        return new GregorianCalendar(year, month, day);
    }

    private short getShort(Cursor cursor, String column) {
        return cursor.getShort(cursor.getColumnIndex(column));
    }

    private String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    private FragmentEntry getEntryCompleteEntry(Cursor cursor) {
        return new FragmentEntry(
                getShort(cursor, ENTRY_SEQ_NUM),
                getShort(cursor, ENTRY_DATE_NUM),
                getShort(cursor, CHAPTER),
                getString(cursor, PERSON),
                getString(cursor, TEXT),
                makeDate(getString(cursor, DATE)),
                getString(cursor, TYPE)
        );
    }

    private FragmentEntry getCheapDiaryEntry(Cursor cursor) {
        return new FragmentEntry(
                getShort(cursor, ENTRY_SEQ_NUM),
                getShort(cursor, ENTRY_DATE_NUM),
                getShort(cursor, CHAPTER),
                getString(cursor, PERSON),
                null,
                makeDate(getString(cursor, DATE)),
                getString(cursor, TYPE)
        );
    }

    private LinkedList<FragmentEntry> getCompleteEntryFromDatabase(String query, String[] value, String groupBy, String orderBy) {
        if (!db.isOpen()) {
            this.open();
        }

        Cursor cursor = db.query(
                TABLE_ENTRY,
                allEntries,
                query,
                value,
                groupBy,
                null,
                orderBy);

        LinkedList<FragmentEntry> entries = new LinkedList<>();

        if (cursor != null && 0 < cursor.getCount()) {
            cursor.moveToFirst();

            do {
                entries.add(getEntryCompleteEntry(cursor));
            } while (cursor.moveToNext());
        }

        // Close curosor
        cursor.close();

        // Close the cursor and the database
        cursor.close();
        db.close();

        // return contact
        return entries;
    }

    private LinkedList<FragmentEntry> getCheapEntryFromDatabase(String query, String[] value, String groupBy, String orderBy) {
        if (!db.isOpen()) {
            this.open();
        }

        Cursor cursor = db.query(
                TABLE_ENTRY,
                allEntriesButText,
                query,
                value,
                groupBy,
                null,
                orderBy);

        LinkedList<FragmentEntry> entries = new LinkedList<>();

        int val = cursor.getCount();

        if (cursor != null && 0 < cursor.getCount()) {
            cursor.moveToFirst();

            do {
                entries.add(getCheapDiaryEntry(cursor));
            } while (cursor.moveToNext());
        }


        // Close cursor and the database
        cursor.close();
        db.close();

        // return contact
        return entries;
    }

    private FragmentEntry getSingleEntry(String query, String[] value, String groupBy, String orderBy) {
        LinkedList<FragmentEntry> entryList = getCompleteEntryFromDatabase(query, value, groupBy, orderBy);

        /*
        if (1 < entryList.size()) {
            throw new Exception("More than one entry for the same sequence number");
        }
        */

        if (0 < entryList.size()) {
            return entryList.get(0);
        } else {
            return null;
        }
    }

    private String formatDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date.getTime());
    }

    // Get the diary entry for the specific sequence number and calendar date
    public FragmentEntry getSpecificDiaryEntry(int entrySequenceNumber, Calendar date) {
        return getSingleEntry(ENTRY_SEQ_NUM + "=? AND " + DATE + "<= ?", new String[]{String.valueOf(entrySequenceNumber), formatDate(date)}, null, null);
    }

    // Get the diary entry based on a sequence number
    public FragmentEntry getSpecificDiaryEntry(int entrySequenceNumber) {
        return getSingleEntry(ENTRY_SEQ_NUM + "=?", new String[]{String.valueOf(entrySequenceNumber)}, null, null);
    }

    // Get a specific entry from a specific person
    public FragmentEntry getSpecificDiaryEntryByPerson(String person, int entryPersonNum, Calendar date) {
        return getSingleEntry(PERSON + "=? AND " + ENTRY_DATE_NUM + "=? AND " + DATE + "<=?", new String[]{person, String.valueOf(entryPersonNum), formatDate(date)}, null, null);
    }

    // Get a specific entry from a specific Chapter
    public LinkedList<FragmentEntry> getChapter(int chapter, Calendar date) {
        //  return get
        return null;
    }

    public List<FragmentEntry> getDiaryEntries(Calendar date) {
        return null;
        //    return getCompleteEntryFromDatabase(DATE + "=?", new String[] { dateValue }, DATE, ENTRY_SEQ_NUM);
    }

    public List<FragmentEntry> getDiaryEntriesBeforeDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateValue = dateFormat.format(date.getTime());

        return getCheapEntryFromDatabase("1=1", null, DATE, ENTRY_SEQ_NUM);
    }

    public List<FragmentEntry> getDiaryEntryByChapter(int chapter) {
        return getCheapEntryFromDatabase(CHAPTER + " = ?", new String[]{String.valueOf(chapter)}, DATE, ENTRY_SEQ_NUM);
    }

    public List<FragmentEntry> getDiaryEntryByPerson(String person) {
        return getCheapEntryFromDatabase(PERSON + " = ?", new String[]{person}, DATE, ENTRY_DATE_NUM);
    }

    public List<FragmentEntry> getDiaryEntryByType(String type) {
        return getCheapEntryFromDatabase(TYPE + "= ?", new String[]{type}, DATE, ENTRY_SEQ_NUM);
    }
}
