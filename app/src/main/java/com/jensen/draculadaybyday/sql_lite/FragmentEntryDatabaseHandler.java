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
    static final String CHAPTER = "chapter";
    static final String PERSON = "person";
    private static final String TEXT = "text";
    static final String DATE = "date";
    static final String TYPE = "type";
    static final String UNLOCKED = "unlocked";
    static final String UNREAD = "unread";

    // Time
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "fragmentEntryManager";
    // Contacts table name
    private static final String TABLE_ENTRY = "Entry";
    private static final String[] allEntries = new String[]{ENTRY_SEQ_NUM, CHAPTER, PERSON, TEXT, DATE, TYPE, UNLOCKED, UNREAD};
    private static final String[] allEntriesButText = new String[]{ENTRY_SEQ_NUM, CHAPTER, PERSON, DATE, TYPE, UNLOCKED, UNREAD};
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
            open();
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
            open();
        }

        Cursor cursor = db.rawQuery(String.format("SELECT COUNT(*) FROM %s WHERE %s", TABLE_ENTRY, where), null);
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
            open();
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

    private LinkedList<Entry> getCheapEntryFromDatabase(String query, String[] valueOfQueries, String orderBy) {
        LinkedList<Entry> entries = new LinkedList<>();

        try {
            if (!db.isOpen()) {
                open();
            }

            Cursor cursor = db.query(
                    TABLE_ENTRY,
                    allEntriesButText,
                    query,
                    valueOfQueries,
                    null,
                    null,
                    orderBy);

            if (cursor != null && 0 < cursor.getCount()) {
                cursor.moveToFirst();

                do {
                    entries.add(getCheapDiaryEntry(cursor));
                } while (cursor.moveToNext());

                // Close cursor
                cursor.close();
            }
        } catch (Exception e) {
            Log.d("DB error", e.getMessage());
        } finally {
            // Close database
            db.close();
        }

        // return contact
        return entries;
    }

    // Get the diary mEntry based on a sequence number
    public Entry getSpecificDiaryEntry(int entrySequenceNumber) {
        LinkedList<Entry> entryList = getCompleteEntryFromDatabase(ENTRY_SEQ_NUM + "=?", new String[]{String.valueOf(entrySequenceNumber)}, null, null);
        Entry entryToReturn = null;

        if (0 < entryList.size()) {
            entryToReturn = entryList.get(0);
        }

        return entryToReturn;
    }

    public List<Entry> getEntries(SqlConstraintFactory constraintFactory, SqlSortFactory sortFactory) {
        List<Entry> entries = null;
        try {
            entries = getCheapEntryFromDatabase(constraintFactory.getConstraint(), constraintFactory.getValues(), sortFactory.getSortOrder());
        } catch (Exception e) {
            Log.d("DB error", e.getMessage());
        }

        return entries;
    }

    public List<Entry> getDiaryEntriesBeforeDate(Calendar date) {
        List<Entry> entries = null;
        try {
            date.set(Calendar.YEAR, 1893);

            SqlConstraintFactory constraintFactory = new SqlConstraintFactory();
            constraintFactory.unlocked(true);

            SqlSortFactory sortFactory = new SqlSortFactory();
            sortFactory.dateOrder();
            sortFactory.bookOrder();
            entries = getCheapEntryFromDatabase(constraintFactory.getConstraint(), constraintFactory.getValues(), sortFactory.getSortOrder());
        } catch (Exception e) {
            Log.d("DB error", e.getMessage());
        }

        return entries;
    }

    // Update the correct
    public void unlockEntriesBeforeDate(Calendar date) {
        try {
            date.set(Calendar.YEAR, 1893);

            SqlConstraintFactory constraintFactory = new SqlConstraintFactory();
            constraintFactory.beforeDate(date);
            constraintFactory.unlocked(false);

            ContentValues cv = new ContentValues();
            cv.put(UNLOCKED, true);
            cv.put(UNREAD, true);

            if (!db.isOpen()) {
                open();
            }
            db.update(TABLE_ENTRY, cv, constraintFactory.getConstraint(), constraintFactory.getValues());
            db.close();
        } catch (Exception e) {
            Log.d("DB error", e.getMessage());
        }
    }

    public int getNumUnlcokedDiaries(Calendar date) {
        // Set the correct year
        date.set(Calendar.YEAR, 1893);

        // Make the constraints
        SqlConstraintFactory constraintFactory = new SqlConstraintFactory();
        constraintFactory.beforeDate(date);

        return entryCount(constraintFactory.getConstraint());
    }
}
