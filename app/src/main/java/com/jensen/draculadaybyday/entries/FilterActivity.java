package com.jensen.draculadaybyday.entries;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.presentation.MultiSpinner;
import com.jensen.draculadaybyday.sql_lite.SqlConstraintFactory;
import com.jensen.draculadaybyday.sql_lite.SqlSortFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    private List<Integer> idList = new ArrayList<>();
    private static final String DATE_FORMAT = "dd/MM/yyyy";


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
            dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                    RelativeLayout dateLayout = (RelativeLayout) layoutInflator.inflate(R.layout.single_date, null, false);
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

                    RelativeLayout dateLayout = (RelativeLayout) layoutInflator.inflate(R.layout.between_dates, null, false);

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

                }
            });
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
                        int layoutId = (int) v.getTag();

                        RelativeLayout rl = (RelativeLayout) findViewById(layoutId);
                        ((ViewGroup) rl.getParent()).removeView(rl);

                        // Remove the id from the list
                        try {
                            idList.remove((Object) layoutId);
                        } catch (Exception e) {
                            Log.d("UI error", e.getMessage());
                        }
                        // if there is only one layout left, you cannot remove it
                        if (idList.size() == 1) {
                            getButton(idList.get(0), R.id.sort_spinner_button).setEnabled(false);
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
                    RelativeLayout rlNew = (RelativeLayout) layoutInflator.inflate(R.layout.sort_item, null, false);

                    rlNew.setId(newSortId);
                    idList.add(newSortId);

                    // Add the view
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, lastSortId);
                    parentLayout.addView(rlNew, layoutParams);

                    //region Set the first remove button to be enabled
                    RelativeLayout firstLayout = (RelativeLayout)findViewById(idList.get(0));
                    Button firstRemoveButton = getButton(newSortId, R.id.sort_spinner_button);
                    firstRemoveButton.setEnabled(true);

                    // Set the tag and onClick value for the ImageView
                    ImageView orderView = getImageView(newSortId, R.id.sort_order_button);
                    orderView.setTag(true);
                    orderView.setOnClickListener(clickToggle);

                    //Set the tag and onClick value for the remove button
                    Button removeButton = getButton(newSortId, R.id.sort_spinner_button);
                    removeButton.setTag(newSortId);
                    removeButton.setOnClickListener(clickRemoveListener);
                    //endregion
                }
            });
            //endregion

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

                    returnIntent.putExtra("constraints", constraintFactory);
                    //endregion

                    //region Sort
                    SqlSortFactory sortFactory = new SqlSortFactory();
                    for(int idVal : idList) {
                        Spinner spinVal = getSpinner(idVal, R.id.sort_spinner);
                        String spinnerValue = (String)spinVal.getSelectedItem();
                        boolean ascendingOrder = (boolean)getImageView(idVal, R.id.sort_order_button).getTag();

                        if (spinnerValue.equals("Book order")) {
                            sortFactory.bookOrder(ascendingOrder);
                        } else if (spinnerValue.equals("Chapter")) {
                            sortFactory.chapterOrder(ascendingOrder);
                        } else if (spinnerValue.equals("Person")) {
                            sortFactory.personOrder(ascendingOrder);
                        } else if (spinnerValue.equals("Date")) {
                            sortFactory.dateOrder(ascendingOrder);
                        } else if (spinnerValue.equals("Type")) {
                            sortFactory.entryType(ascendingOrder);
                        } else if (spinnerValue.equals("Read/unread")) {
                            sortFactory.readType(ascendingOrder);
                        }
                    }

                    returnIntent.putExtra("sort", sortFactory);
                    //endregion

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });

        } catch (Exception e) {
            Log.d("Filter exception", e.getMessage());
        }
    }

    private Button getButton(int layoutId, int buttonId) {
        RelativeLayout rl = (RelativeLayout)findViewById(layoutId);
        return (Button)rl.findViewById(buttonId);
    }

    private ImageView getImageView(int layoutId, int imageViewId) {
        RelativeLayout rl = (RelativeLayout)findViewById(layoutId);
        return (ImageView)rl.findViewById(imageViewId);
    }

    private Spinner getSpinner(int layoutId, int spinnerId) {
        RelativeLayout rl = (RelativeLayout)findViewById(layoutId);
        return (Spinner)rl.findViewById(spinnerId);
    }


}
