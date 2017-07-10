package com.jensen.draculadaybyday.sql_lite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.jensen.draculadaybyday.entry.Entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class FragmentEntryDatabaseHandler extends android.database.sqlite.SQLiteOpenHelper {

    // Contacts Table Columns names
    public static final String ENTRY_SEQ_NUM = "entrySeqNum";
    private static final String ENTRY_DATE_NUM = "entryDateNum";
    private static final String CHAPTER = "chapter";
    private static final String PERSON = "person";
    private static final String TEXT = "text";
    private static final String DATE = "date";
    private static final String TYPE = "type";
    private static final String UNLOCKED = "unlocked";
    private static final String UNREAD = "unread";

    // Time
    private static final String TIME_FORMAT = "yyyy-MM-dd";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "fragmentEntryManager";
    // Contacts table name
    private static final String TABLE_ENTRY = "mEntry";
    private static final String[] allEntries = new String[]{ENTRY_SEQ_NUM, ENTRY_DATE_NUM, CHAPTER, PERSON, TEXT, DATE, TYPE, UNLOCKED, UNREAD};
    private static final String[] allEntriesButText = new String[]{ENTRY_SEQ_NUM, ENTRY_DATE_NUM, CHAPTER, PERSON, DATE, TYPE, UNLOCKED, UNREAD};
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
                + TYPE + " TEXT,"
                + UNLOCKED + " INTEGER,"
                + UNREAD + " INTEGER"
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

    public synchronized void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    // Inserting an mEntry
    public void addEntry(Entry entry) {
        if (!db.isOpen()) {
            open();
        }

        try {
            // Check if the mEntry is already in the database
            if (!EntryAlreadyInDB(entry.getStoryEntryNum())) {

                ContentValues values = new ContentValues();

                // auto-generate the ENTRY_SEQ_NUM (primary key)

                values.put(ENTRY_DATE_NUM, entry.getDateEntryNum());
                values.put(CHAPTER, entry.getChapter());
                values.put(PERSON, entry.getPerson());
                values.put(TEXT, entry.getTextEntry());

                SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
                values.put(DATE, dateFormat.format(entry.getDate().getTime()));
                values.put(TYPE, entry.getType().description);
                values.put(UNLOCKED, entry.getUnlocked());
                values.put(UNREAD, entry.getUnread());

                // Inserting row
                db.insertOrThrow(TABLE_ENTRY, null, values);

            }
        } catch (Exception e) {
            Log.d("Database error", e.getMessage());
            throw e;
        }
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
        assert cursor != null;
        cursor.close();
        return false;
    }

    private int entryCount(String where) {
        if (!db.isOpen()) {
            this.open();
        }

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ENTRY + " WHERE " + where, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();

        return count;
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

    private boolean getBoolean(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column)) == 1;
    }

    private Entry getEntryCompleteEntry(Cursor cursor) {
        return new Entry(
                getShort(cursor, ENTRY_SEQ_NUM),
                getShort(cursor, ENTRY_DATE_NUM),
                getShort(cursor, CHAPTER),
                getString(cursor, PERSON),
                getString(cursor, TEXT),
                makeDate(getString(cursor, DATE)),
                getString(cursor, TYPE),
                getBoolean(cursor, UNLOCKED),
                getBoolean(cursor, UNREAD)
        );
    }

    private Entry getCheapDiaryEntry(Cursor cursor) {
        return new Entry(
                getShort(cursor, ENTRY_SEQ_NUM),
                getShort(cursor, ENTRY_DATE_NUM),
                getShort(cursor, CHAPTER),
                getString(cursor, PERSON),
                null,
                makeDate(getString(cursor, DATE)),
                getString(cursor, TYPE),
                getBoolean(cursor, UNLOCKED),
                getBoolean(cursor, UNREAD)
        );
    }

    private LinkedList<Entry> getCompleteEntryFromDatabase(String query, String[] value, String groupBy, String orderBy) {
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

        LinkedList<Entry> entries = new LinkedList<>();
        if (cursor != null && 0 < cursor.getCount()) {
            cursor.moveToFirst();

            do {
                entries.add(getEntryCompleteEntry(cursor));
            } while (cursor.moveToNext());
        }

        // Close curosor
        assert cursor != null;
        cursor.close();

        // Close the cursor and the database
        cursor.close();
        db.close();

        // return contact
        return entries;
    }

    private LinkedList<Entry> getCheapEntryFromDatabase(String query, String[] value, String groupBy, String orderBy) {
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

        LinkedList<Entry> entries = new LinkedList<>();

        if (cursor != null && 0 < cursor.getCount()) {
            cursor.moveToFirst();

            do {
                entries.add(getCheapDiaryEntry(cursor));
            } while (cursor.moveToNext());
        }

        // Close cursor and the database
        assert cursor != null;
        cursor.close();
        db.close();

        // return contact
        return entries;
    }

    private Entry getSingleEntry(String query, String[] value, String groupBy, String orderBy) {
        LinkedList<Entry> entryList = getCompleteEntryFromDatabase(query, value, groupBy, orderBy);

        /*
        if (1 < entryList.size()) {
            throw new Exception("More than one mEntry for the same sequence number");
        }
        */

        if (0 < entryList.size()) {
            return entryList.get(0);
        } else {
            return null;
        }
    }

    private String formatDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return dateFormat.format(date.getTime());
    }

    // Get the diary mEntry for the specific sequence number and calendar date
    public Entry getSpecificDiaryEntry(int entrySequenceNumber, Calendar date) {
        return getSingleEntry(ENTRY_SEQ_NUM + "=? AND " + DATE + "<= ?", new String[]{String.valueOf(entrySequenceNumber), formatDate(date)}, null, null);
    }

    // Get the diary mEntry based on a sequence number
    public Entry getSpecificDiaryEntry(int entrySequenceNumber) {
        return getSingleEntry(ENTRY_SEQ_NUM + "=?", new String[]{String.valueOf(entrySequenceNumber)}, null, null);
    }

    // Get a specific mEntry from a specific person
    public Entry getSpecificDiaryEntryByPerson(String person, int entryPersonNum, Calendar date) {
        return getSingleEntry(PERSON + "=? AND " + ENTRY_DATE_NUM + "=? AND " + DATE + "<=?", new String[]{person, String.valueOf(entryPersonNum), formatDate(date)}, null, null);
    }

    // Get a specific mEntry from a specific Chapter
    public LinkedList<Entry> getChapter(int chapter, Calendar date) {
        //  return get
        return null;
    }

    public List<Entry> getDiaryEntries(Calendar date) {
        return null;
        //    return getCompleteEntryFromDatabase(DATE + "=?", new String[] { dateValue }, DATE, ENTRY_SEQ_NUM);
    }

    public List<Entry> getDiaryEntriesBeforeDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String dateValue = dateFormat.format(date.getTime());

        return getCheapEntryFromDatabase("1=1", null, null, DATE + ", " + ENTRY_SEQ_NUM);
    }

    public int getNumUnlcokedDiaries(Calendar date) {
        return entryCount("1=1");
    }

    public List<Entry> getDiaryEntryByChapter(int chapter) {
        return getCheapEntryFromDatabase(CHAPTER + " = ?", new String[]{String.valueOf(chapter)}, DATE, ENTRY_SEQ_NUM);
    }

    public List<Entry> getDiaryEntryByPerson(String person) {
        return getCheapEntryFromDatabase(PERSON + " = ?", new String[]{person}, DATE, ENTRY_DATE_NUM);
    }

    public List<Entry> getDiaryEntryByType(String type) {
        return getCheapEntryFromDatabase(TYPE + "= ?", new String[]{type}, DATE, ENTRY_SEQ_NUM);
    }
}
