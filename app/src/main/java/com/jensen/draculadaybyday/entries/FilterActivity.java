package com.jensen.draculadaybyday.entries;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.presentation.MultiSpinner;
import com.jensen.draculadaybyday.sql_lite.Constraint;
import com.jensen.draculadaybyday.sql_lite.DateConstraint;
import com.jensen.draculadaybyday.sql_lite.SortValue;
import com.jensen.draculadaybyday.sql_lite.SqlConstraintFactory;
import com.jensen.draculadaybyday.sql_lite.SqlSortFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    public final static String CONSTRAINTS_INTENT_KEY = "constraints";
    public final static String SORTING_INTENT_KEY = "sorting";

    private final List<Integer> idList = new ArrayList<>();
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private void setSingleDay(final String stringDateRep) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Button dateButton = (Button) findViewById(R.id.single_button);

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                Calendar cal = getCalendarFromString(stringDateRep);
                dateButton.setTag(cal);

                int month = cal.get(Calendar.MONTH)-1;
                cal.set(Calendar.MONTH, month);

                dateButton.setText(dateFormat.format(cal.getTime()));
            }}, 100);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_filter);

            ActionBar mActionBar = getSupportActionBar();

            if (mActionBar != null) {
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
            }

            //region Filter layout
            final Spinner dateSpinner = (Spinner)findViewById(R.id.filter_date_spinner);
            AdapterView.OnItemSelectedListener dateSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
                private void removeOldView() {
                    RelativeLayout rl = (RelativeLayout)findViewById(R.id.set_date);
                    if (rl != null) {
                        RelativeLayout parentView = (RelativeLayout)findViewById(R.id.date_rl_view);
                        parentView.removeView(rl);
                    }
                }

                private void getDatePicker(final Calendar calendar, final Button dateButton) {
                    try {
                        DatePickerDialog.OnDateSetListener setListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                dateButton.setText(dateFormat.format(newDate.getTime()));
                                dateButton.setTag(newDate);
                            }
                        };

                        DatePickerDialog dlg = new DatePickerDialog(FilterActivity.this, setListener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)) {
                            @Override
                            protected void onCreate(Bundle savedInstanceState) {
                                super.onCreate(savedInstanceState);
                                DatePicker dp = this.getDatePicker();
                                dp.setMinDate(calendar.getTimeInMillis());
                            }
                        };

                        dlg.show();
                    }
                    catch (Exception e) {
                        Log.d("date-picker-dialog", e.getMessage());
                    }
                }

                private void addSingleDate(int labelTextId) {
                    LayoutInflater layoutInflator = LayoutInflater.from(getBaseContext());
                    RelativeLayout parentLayout = (RelativeLayout)findViewById(R.id.date_rl_view);

                    RelativeLayout dateLayout = (RelativeLayout) layoutInflator.inflate(R.layout.single_date, parentLayout, false);
                    TextView textView = (TextView)dateLayout.findViewById(R.id.single_label);
                    textView.setText(getResources().getString(labelTextId));

                    final Button dateButton = (Button)dateLayout.findViewById(R.id.single_button);
                    final Calendar initialDay = Calendar.getInstance();
                    initialDay.set(Calendar.DAY_OF_MONTH, 3);
                    initialDay.set(Calendar.MONTH, 2);
                    initialDay.set(Calendar.YEAR, 1893);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    dateButton.setText(dateFormat.format(initialDay.getTime()));
                    dateButton.setTag(initialDay);
                    dateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDatePicker(initialDay, dateButton);
                        }
                    });

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.filter_date_spinner);
                    parentLayout.addView(dateLayout, layoutParams);
                }

                private void addBetweenDates() {
                    LayoutInflater layoutInflator = LayoutInflater.from(getBaseContext());
                    RelativeLayout parentLayout = (RelativeLayout)findViewById(R.id.date_rl_view);

                    RelativeLayout dateLayout = (RelativeLayout) layoutInflator.inflate(R.layout.between_dates, parentLayout, false);

                    final Calendar initialDay = Calendar.getInstance();
                    initialDay.set(Calendar.DAY_OF_MONTH, 3);
                    initialDay.set(Calendar.MONTH, 2);
                    initialDay.set(Calendar.YEAR, 1893);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

                    final Button startDateButton = (Button) dateLayout.findViewById(R.id.between_dates_start_button);
                    startDateButton.setText(dateFormat.format(initialDay.getTime()));
                    startDateButton.setTag(initialDay);
                    startDateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDatePicker(initialDay, startDateButton);
                        }
                    });

                    final Button endDateButton = (Button) dateLayout.findViewById(R.id.between_dates_end_button);
                    endDateButton.setText(dateFormat.format(initialDay.getTime()));
                    endDateButton.setTag(initialDay);
                    endDateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDatePicker(initialDay, endDateButton);
                        }
                    });

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.filter_date_spinner);
                    parentLayout.addView(dateLayout, layoutParams);
                }

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        switch (dateSpinner.getSelectedItemPosition()) {
                            case 0:
                                removeOldView();
                                break;
                            case 1:
                                removeOldView();
                                addSingleDate(R.string.filter_on_date);
                                break;
                            case 2:
                                removeOldView();
                                addSingleDate(R.string.filter_before_date);
                                break;
                            case 3:
                                removeOldView();
                                addSingleDate(R.string.filter_after_date);
                                break;
                            case 4:
                                removeOldView();
                                addBetweenDates();
                                break;
                        }
                    } catch (Exception e) {
                        Log.d("Date", e.getMessage());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // This is required to be here
                }
            };

            dateSpinner.setOnItemSelectedListener(dateSpinnerSelectedListener);
            //endregion

            //region Sort layout
            RelativeLayout topRl = (RelativeLayout)findViewById(R.id.sort_spinner_1);
            int topRlId = topRl.getId();
            idList.add(topRlId);

            final ImageView sortOrderView = (ImageView)findViewById(R.id.sort_order_button) ;
            sortOrderView.setTag(true);
            final View.OnClickListener clickToggle = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView view = (ImageView)v;
                    boolean ascending = !(boolean)view.getTag();
                    if (ascending) {
                        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_ascending, getTheme()));
                        view.setContentDescription(getResources().getString(R.string.sort_asc_description));
                    } else {
                        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_descending, getTheme()));
                        view.setContentDescription(getResources().getString(R.string.sort_desc_description));
                    }
                    view.setTag(ascending);
                }
            };
            sortOrderView.setOnClickListener(clickToggle);

            // Remove button - As long as there is only one instance of the sort values, you cannot remove it
            final Button removeButton = (Button)topRl.findViewById(R.id.sort_spinner_button);
            removeButton.setTag(topRlId);
            removeButton.setEnabled(false);

            final View.OnClickListener clickRemoveListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Extra sanity check - we should not be here if there is only one sort element,
                    // but just in case
                    if (1 < idList.size()) {
                        // Find and remove the view
                        int layoutId = (Integer) v.getTag();

                        RelativeLayout rl = (RelativeLayout) findViewById(layoutId);
                        ((ViewGroup) rl.getParent()).removeView(rl);

                        // We need to ensure that the relative layout is not messed up
                        // If there are at least there sorting objects left a, b, and c,
                        // (let x > y indicate that y is directly below x) and a > b > c, a and we
                        // remove b, then we must have a > c
                        if (3 <= idList.size()) {
                            int removeIndex = idList.indexOf(layoutId);
                            // We should never do this for the first or the last element in the list
                            if (0 < removeIndex && removeIndex < idList.size() - 1 ) {

                                // The indexes
                                int idAboveIndex = removeIndex - 1;
                                int idBelowIndex = removeIndex + 1;

                                // The ids
                                int idAbove = idList.get(idAboveIndex);
                                int idBelow = idList.get(idBelowIndex);

                                RelativeLayout rlBelow = (RelativeLayout)findViewById(idBelow);

                                RelativeLayout.LayoutParams aboveLayoutParams
                                        = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                                aboveLayoutParams.addRule(RelativeLayout.BELOW, idAbove);
                                rlBelow.setLayoutParams(aboveLayoutParams);
                            }
                        }

                        // Remove the id from the list - I cast to object so it removes the integer, not the object at the position
                        //noinspection SuspiciousMethodCalls
                        idList.remove((Object)layoutId);

                        // if there is only one layout left, we disable its ability to remove itself
                        if (idList.size() == 1) {
                            getButton(idList.get(0)).setEnabled(false);
                        }
                    }
                }
            };
            removeButton.setOnClickListener(clickRemoveListener);

            // Add button
            Button addButton = (Button)findViewById(R.id.sort_add_button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get information about the last layout
                    int lastSortId = idList.get(idList.size() - 1);
                    int newSortId = lastSortId + 1;

                    // Get the viewgroup of the last item
                    RelativeLayout parentLayout = (RelativeLayout)findViewById(R.id.sort_top_rl);

                    LayoutInflater layoutInflator = LayoutInflater.from(getBaseContext());
                    RelativeLayout rlNew = (RelativeLayout) layoutInflator.inflate(R.layout.sort_item, parentLayout, false);

                    rlNew.setId(newSortId);
                    idList.add(newSortId);

                    // Add the view
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, lastSortId);
                    parentLayout.addView(rlNew, layoutParams);

                    //region Set the first remove button to be enabled
                    Button firstRemoveButton = getButton(idList.get(0));
                    firstRemoveButton.setEnabled(true);

                    // Set the tag and onClick value for the ImageView
                    ImageView orderView = getImageView(newSortId);
                    orderView.setTag(true);
                    orderView.setOnClickListener(clickToggle);

                    //Set the tag and onClick value for the remove button
                    Button removeButton = getButton(newSortId);
                    removeButton.setTag(newSortId);
                    removeButton.setOnClickListener(clickRemoveListener);
                    //endregion
                }
            });
            //endregion

            //region cancel/OK button
            final Button cancelButton = (Button)findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            });

            Button okButton = (Button)findViewById(R.id.ok_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();

                    //region Constraints
                    SqlConstraintFactory constraintFactory = new SqlConstraintFactory();

                    //region Date
                    Spinner dateSpinner = (Spinner)findViewById(R.id.filter_date_spinner);
                    switch (dateSpinner.getSelectedItemPosition()) {
                        case 1:
                            Button exactDate = (Button)findViewById(R.id.single_button);
                            Calendar exactDateCalendar = (Calendar) exactDate.getTag();
                            constraintFactory.exactDate(exactDateCalendar);
                            break;
                        case 2:
                            Button beforeDate = (Button)findViewById(R.id.single_button);
                            Calendar beforeDateCalendar = (Calendar)beforeDate.getTag();
                            constraintFactory.beforeDate(beforeDateCalendar);
                            break;

                        case 3:
                            Button afterDate = (Button)findViewById(R.id.single_button);
                            Calendar afterDateCalendar = (Calendar)afterDate.getTag();
                            constraintFactory.afterDate(afterDateCalendar);
                            break;

                        case 4:
                            Button startDateButton = (Button)findViewById(R.id.between_dates_start_button);
                            Calendar startDateCalendar = (Calendar)startDateButton.getTag();

                            Button endDateButton = (Button)findViewById(R.id.between_dates_end_button);
                            Calendar endDateCalendar = (Calendar)endDateButton.getTag();

                            constraintFactory.betweenDates(startDateCalendar, endDateCalendar);
                            break;
                    }
                    //endregion

                    //region Person
                    MultiSpinner personSpinner = (MultiSpinner)findViewById(R.id.filter_person_spinner);
                    List<String> persons = personSpinner.getSelected();

                    if(!persons.get(0).equals("All")) {
                        constraintFactory.multiplePersons(persons);
                    }
                    //endregion

                    //region Type
                    MultiSpinner typeSpinner = (MultiSpinner)findViewById(R.id.filter_type_spinner);
                    List<String> types = typeSpinner.getSelected();

                    if (!types.get(0).equals("All")) {
                        constraintFactory.multipleTypes(types);
                    }
                    //endregion

                    //region Chapters
                    MultiSpinner chapterSpinner = (MultiSpinner)findViewById(R.id.filter_chapter_spinner);
                    List<String> chapters = chapterSpinner.getSelected();

                    if (!chapters.get(0).equals("All")) {
                        constraintFactory.multipleChapters(chapters);
                    }
                    //endregion

                    //region Read status
                    Spinner readSpinner = (Spinner)findViewById(R.id.filter_read_spinner);
                    switch (readSpinner.getSelectedItemPosition()) {
                        // 0 is both read and unread
                        case 1:
                            // Only read
                            constraintFactory.readStatus(false);
                            break;
                        case 2:
                            // Only unread
                            constraintFactory.readStatus(true);
                            break;
                    }
                    //endregion

                    returnIntent.putExtra(CONSTRAINTS_INTENT_KEY, constraintFactory);
                    //endregion

                    //region Sort
                    SqlSortFactory sortFactory = new SqlSortFactory();
                    for(int idVal : idList) {
                        Spinner spinVal = getSpinner(idVal);
                        String spinnerValue = (String)spinVal.getSelectedItem();
                        boolean ascendingOrder = (boolean)getImageView(idVal).getTag();

                        switch (spinnerValue) {
                            case "Book order":
                                sortFactory.bookOrder(ascendingOrder);
                                break;
                            case "Chapter":
                                sortFactory.chapterOrder(ascendingOrder);
                                break;
                            case "Person":
                                sortFactory.personOrder(ascendingOrder);
                                break;
                            case "Date":
                                sortFactory.dateOrder(ascendingOrder);
                                break;
                            case "Type":
                                sortFactory.entryType(ascendingOrder);
                                break;
                            case "Read/Unread":
                                sortFactory.readType(ascendingOrder);
                                break;
                        }
                    }

                    returnIntent.putExtra(SORTING_INTENT_KEY, sortFactory);
                    //endregion

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
            //endregion
        } catch (Exception e) {
            Log.d("Filter exception", e.getMessage());
        }

        try {
            //region Change the GUI to fit the values given in the intent
            Intent intent = getIntent();

            //region Filter
            SqlConstraintFactory constraintFactory = (SqlConstraintFactory) intent.getExtras().get(CONSTRAINTS_INTENT_KEY);

            Spinner dateSpinner = (Spinner) findViewById(R.id.filter_date_spinner);

            //region Date
            final DateConstraint dateConstraint = (DateConstraint) constraintFactory.getConstraint(constraintFactory.DATE);
            if (dateConstraint.hasConstraints()) {
                switch (dateConstraint.getDateType()) {
                    case DateConstraint.BEFORE:
                        dateSpinner.setSelection(1, false);
                        setSingleDay(dateConstraint.getConstraintSqlValues().get(0));
                        break;
                    case DateConstraint.AFTER:
                        dateSpinner.setSelection(2, false);
                        setSingleDay(dateConstraint.getConstraintSqlValues().get(0));
                        break;
                    case DateConstraint.EXACT:
                        dateSpinner.setSelection(3, false);
                        setSingleDay(dateConstraint.getConstraintSqlValues().get(0));
                        break;
                    case DateConstraint.BETWEEN:
                        dateSpinner.setSelection(4, false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calStart = getCalendarFromString(dateConstraint.getConstraintSqlValues().get(0));
                                Calendar calEnd = getCalendarFromString(dateConstraint.getConstraintSqlValues().get(1));

                                int beforeMonth = calStart.get(Calendar.MONTH)-1;
                                int afterMonth = calEnd.get(Calendar.MONTH)-1;

                                calStart.set(Calendar.MONTH, beforeMonth);
                                calEnd.set(Calendar.MONTH, afterMonth);

                                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

                                Button dateButtonStart = (Button) findViewById(R.id.between_dates_start_button);
                                Button dateButtonEnd = (Button) findViewById(R.id.between_dates_end_button);

                                dateButtonStart.setText(dateFormat.format(calStart.getTime()));
                                dateButtonEnd.setText(dateFormat.format(calEnd.getTime()));

                                dateButtonStart.setTag(calStart);
                                dateButtonEnd.setTag(calEnd);
                            }}, 100);
                        break;
                }
            }
            //endregion


            //region Set the Multi spinners
            //region Type constraints
            Constraint typeConstraints = constraintFactory.getConstraint(SqlConstraintFactory.TYPE);
            if (typeConstraints.hasConstraints()) {
                MultiSpinner typeMultiSpin = (MultiSpinner) findViewById(R.id.filter_type_spinner);
                typeMultiSpin.setSelectedEntries(typeConstraints.getConstraintSqlValues());
            }
            //endregion
            //region Person constraints
            Constraint personConstraints = constraintFactory.getConstraint(SqlConstraintFactory.PERSON);
            if (personConstraints.hasConstraints()) {
                MultiSpinner personMultiSpin = (MultiSpinner)findViewById(R.id.filter_person_spinner);
                personMultiSpin.setSelectedEntries(personConstraints.getConstraintSqlValues());
            }
            //endregion
            //region Chapter constraints
            Constraint chapterConstraints = constraintFactory.getConstraint(SqlConstraintFactory.CHAPTER);
            if (chapterConstraints.hasConstraints()) {
                MultiSpinner chapterSpinner = (MultiSpinner)findViewById(R.id.filter_chapter_spinner);
                chapterSpinner.setSelectedEntries(chapterConstraints.getConstraintSqlValues());
            }
            //endregion
            //endregion

            //region Read or unread
            Constraint readConstraints = constraintFactory.getConstraint(SqlConstraintFactory.READ);
            if (readConstraints.hasConstraints()) {
                Spinner spinner = (Spinner) findViewById(R.id.filter_read_spinner);
                SpinnerAdapter typeSpinnerAdapter = spinner.getAdapter();
                for (int i = 0; i < typeSpinnerAdapter.getCount(); i++) {
                    if (readConstraints.getConstraintSqlValues().get(0).equals(typeSpinnerAdapter.getItem(i))) {
                        spinner.setSelection(i, false);
                        break;
                    }
                }
            }
            //endregion
            //endregion

            //region Sort
            SqlSortFactory sortFactory = (SqlSortFactory) intent.getExtras().get(SORTING_INTENT_KEY);
            List<SortValue> sortValues = sortFactory.getSortingOrderList();

            Button addButton = (Button)findViewById(R.id.sort_add_button);

            // Take account of the fact that we are sorting
            for(int i = 0; i < sortValues.size() - 1; i++) {
                addButton.performClick();
            }
            for(int i = 0; i < sortValues.size(); i++) {
                SortValue sortValue = sortValues.get(i);
                int idVal = idList.get(i);

                Spinner sortSpinner = getSpinner(idVal);
                String mSortValue = sortValues.get(i).getColumn();

                sortSpinner.setSelection(sortValues.get(i).getSpinnerIndex());

                ImageView sortImageView = getImageView(idVal);
                if (!sortValue.getAscending()) {
                    sortImageView.performClick();
                }
            }
            //endregion
            //endregion


        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //region Filter
        //region Date spinner
        Spinner dateSpinner = (Spinner)findViewById(R.id.filter_date_spinner);
        int dateSelectedPosition = dateSpinner.getSelectedItemPosition();

        outState.putInt("dateSelectedPos", dateSelectedPosition);
        if (1 <= dateSelectedPosition && dateSelectedPosition <= 3) {
            Button dateButton = (Button) findViewById(R.id.single_button);
            outState.putString("date_text", (String) dateButton.getText());

            Calendar cal = (Calendar)dateButton.getTag();
            outState.putSerializable("dateCal", cal);
        } else if (dateSelectedPosition == 4) {
            Button dateButtonStart = (Button) findViewById(R.id.between_dates_start_button);
            Button dateButtonEnd = (Button) findViewById(R.id.between_dates_end_button);

            outState.putString("date_start_text", (String)dateButtonStart.getText());
            outState.putString("date_end_text", (String)dateButtonEnd.getText());

            Calendar calStart = (Calendar)dateButtonStart.getTag();
            Calendar calEnd = (Calendar)dateButtonEnd.getTag();

            outState.putSerializable("dateCal_start", calStart);
            outState.putSerializable("dateCal_end", calEnd);
        }
        //endregion

        //region Persons
        MultiSpinner personSpinner = (MultiSpinner)findViewById(R.id.filter_person_spinner);
        List<String> persons = personSpinner.getSelected();
        outState.putStringArrayList("persons", new ArrayList<>(persons));
        //endregion

        //region Types
        MultiSpinner typeSpinner = (MultiSpinner)findViewById(R.id.filter_type_spinner);
        List<String> types = typeSpinner.getSelected();
        outState.putStringArrayList("types", new ArrayList<>(types));
        //endregion

        //region Chapters
        MultiSpinner chapterSpinner = (MultiSpinner)findViewById(R.id.filter_chapter_spinner);
        List<String> chapters = chapterSpinner.getSelected();
        outState.putStringArrayList("chapters", new ArrayList(chapters));
        //endregion

        //region Read/unread
        Spinner readSpinner = (Spinner)findViewById(R.id.filter_read_spinner);
        outState.putInt("readPos", readSpinner.getSelectedItemPosition());
        //endregion
        //endregion

        //region Sort
        int[] sortPositionArray = new int[idList.size()];
        boolean[] sortAscArray = new boolean[idList.size()];
        for(int i = 0; i < idList.size(); i++) {
            // int current
            int relativeViewId = idList.get(i);
            Spinner sortSpinner = getSpinner(relativeViewId);
            sortPositionArray[i] = sortSpinner.getSelectedItemPosition();

            ImageView imageView = getImageView(relativeViewId);
            sortAscArray[i] = (boolean)imageView.getTag();
        }

        outState.putIntArray("sortPosition", sortPositionArray);
        outState.putBooleanArray("sortAsc", sortAscArray);
        //endregion
    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try {
            //region Filter
            //region Date spinner
            final Spinner dateSpinner = (Spinner) findViewById(R.id.filter_date_spinner);
            final int dateSelectedPosition = savedInstanceState.getInt("dateSelectedPos");
            dateSpinner.setSelection(dateSelectedPosition);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (dateSelectedPosition) {
                        case 1:
                        case 2:
                        case 3:
                            String buttonString = savedInstanceState.getString("date_text");
                            Calendar cal = (Calendar) savedInstanceState.getSerializable("dateCal");

                            Button dateButton = (Button) findViewById(R.id.single_button);
                            dateButton.setText(buttonString);
                            dateButton.setTag(cal);
                            break;
                        case 4:
                            String buttonStartString = savedInstanceState.getString("date_start_text");
                            String buttonEndString = savedInstanceState.getString("date_end_text");

                            Calendar calStart = (Calendar)savedInstanceState.getSerializable("dateCal_start");
                            Calendar calEnd = (Calendar)savedInstanceState.getSerializable("dateCal_end");

                            Button dateButtonStart = (Button) findViewById(R.id.between_dates_start_button);
                            Button dateButtonEnd = (Button) findViewById(R.id.between_dates_end_button);

                            dateButtonStart.setText(buttonStartString);
                            dateButtonEnd.setText(buttonEndString);

                            dateButtonStart.setTag(calStart);
                            dateButtonEnd.setTag(calEnd);
                            break;
                    }
                }
            }, 250);
            //endregion

            //region Persons
            ArrayList<String> persons = savedInstanceState.getStringArrayList("persons");
            MultiSpinner personSpinner = (MultiSpinner)findViewById(R.id.filter_person_spinner);
            personSpinner.setSelectedEntries(persons);
            //endregion

            //region Types
            ArrayList<String> types = savedInstanceState.getStringArrayList("types");
            MultiSpinner typeSpinner = (MultiSpinner)findViewById(R.id.filter_type_spinner);
            typeSpinner.setSelectedEntries(types);
            //endregion

            //region Chapters
            ArrayList<String> chapters = savedInstanceState.getStringArrayList("chapters");
            MultiSpinner chapterSpinner = (MultiSpinner)findViewById(R.id.filter_chapter_spinner);
            chapterSpinner.setSelectedEntries(chapters);
            //endregion

            //region read/unread
            int position = savedInstanceState.getInt("readPos");
            Spinner readSpinner = (Spinner)findViewById(R.id.filter_read_spinner);
            readSpinner.setSelection(position, false);
            //endregion
            //endregion

            //region Sort
            int[] positionArray = (int[])savedInstanceState.get("sortPosition");
            boolean[] sortAscArray = (boolean[])savedInstanceState.get("sortAsc");

            // There will always be at least one sort
            Button addButton = (Button)findViewById(R.id.sort_add_button);
            for(int i = 0; i < positionArray.length-1; i++) {
                addButton.performClick();
            }
            for(int i = 0; i < positionArray.length; i++) {
                Spinner spinner = getSpinner(idList.get(i));
                spinner.setSelection(positionArray[i], false);

                ImageView ascView = getImageView(idList.get(i));
                ascView.setTag(true);
                if (!sortAscArray[i]) {
                    ascView.performClick();
                }
            }
            //endregion
        } catch (Exception e) {
            Log.d("RestoreInstance", e.getMessage());
        }
    }

    private Calendar getCalendarFromString(String dateRep) {
        String[] rep = dateRep.split("-");
        int year = Integer.valueOf(rep[0]);
        int month = Integer.valueOf(rep[1]);
        int day = Integer.valueOf(rep[2].substring(0, 2));

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    @SuppressWarnings("ConstantConditions")
    private Button getButton(int layoutId) {
        RelativeLayout rl = (RelativeLayout)findViewById(layoutId);
        return (Button)rl.findViewById(R.id.sort_spinner_button);
    }

    @SuppressWarnings("ConstantConditions")
    private ImageView getImageView(int layoutId) {
        RelativeLayout rl = (RelativeLayout)findViewById(layoutId);
        return (ImageView)rl.findViewById(R.id.sort_order_button);
    }

    @SuppressWarnings("ConstantConditions")
    private Spinner getSpinner(int layoutId) {
        RelativeLayout rl = (RelativeLayout)findViewById(layoutId);
        return (Spinner)rl.findViewById(R.id.sort_spinner);
    }


}
