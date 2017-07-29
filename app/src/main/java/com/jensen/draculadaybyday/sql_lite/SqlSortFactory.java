package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SqlSortFactory implements Parcelable {

    private final UniqueValueList<SortValue> sortingOrder;

    public SqlSortFactory() {
        sortingOrder = new UniqueValueList<>();
    }

    private SqlSortFactory(Parcel in) {
        sortingOrder = new UniqueValueList<>();
        try {
            List<SortValue> list = new LinkedList<>();
            in.readList(list, SortValue.class.getClassLoader());

            sortingOrder.addAll(list);
        } catch (Exception e) {
            Log.d("Parcel prob", e.getMessage());
        }
    }

    public List<SortValue> getSortingOrderList() {
        List<SortValue> returnValue = new ArrayList<>();
        for(SortValue sortValue : sortingOrder) {
            returnValue.add(sortValue);
        }
        return returnValue;
    }

    public void bookOrder(boolean asc) {
        sortingOrder.add(new SortValue(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, asc, 0));
    }

    public void chapterOrder(boolean asc) {
        sortingOrder.add(new SortValue(FragmentEntryDatabaseHandler.CHAPTER, asc, 1));
    }

    public void personOrder(boolean asc) {
        sortingOrder.add(new SortValue(FragmentEntryDatabaseHandler.PERSON, asc, 2));
    }

    public void dateOrder(boolean asc) {
        sortingOrder.add(new SortValue(FragmentEntryDatabaseHandler.DATE, asc, 3));
    }

    public void entryType(boolean asc) {
        sortingOrder.add(new SortValue(FragmentEntryDatabaseHandler.TYPE, asc, 4));
    }

    public void readType(boolean asc) {
        sortingOrder.add(new SortValue(FragmentEntryDatabaseHandler.UNREAD, asc, 5));
    }

    public String getSortOrder() {
        return TextUtils.join(", ", sortingOrder);
    }

    public void writeToParcel(Parcel out, int flags) {
        LinkedList<SortValue> list = new LinkedList<>();
        for(SortValue item : sortingOrder) {
            list.add(item);
        }
        out.writeList(list);
    }

    public int describeContents () {
        return 0;
    }

    public static final Parcelable.Creator<SqlSortFactory> CREATOR
            = new Parcelable.Creator<SqlSortFactory>() {

        public SqlSortFactory createFromParcel(Parcel in) {
            return new SqlSortFactory(in);
        }

        public SqlSortFactory[] newArray(int size) {
            return new SqlSortFactory[size];
        }
    };

    private class UniqueValueList<E> extends LinkedList<E> {
        final HashMap<E, Integer> recallValue;

        public UniqueValueList() {
            super();
            recallValue = new HashMap<>();
        }

        @Override
        public boolean add(E item) {
            boolean addCorrectly = true;
            if (recallValue.containsKey(item)) {
                // remove the order object
                super.remove(item);
                recallValue.remove(item);
                addCorrectly = false;
            }
            // Add the item to the hashmap
            recallValue.put(item, 1);
            boolean addResult = super.add(item);
            return addResult && addCorrectly;
        }

        @Override
        public boolean addAll(Collection<? extends E> items) {
            boolean addCorrectly = true;
            for (E item : items) {
                addCorrectly &= add(item);
            }
            return addCorrectly;
        }
    }
}
