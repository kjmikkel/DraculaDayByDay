package com.jensen.draculadaybyday.sql_lite;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SqlSortFactory {

    private UniqueValueList<String> sortingOrder;

    public SqlSortFactory() {
        sortingOrder = new UniqueValueList<>();
    }

    public void bookOrder() {
        sortingOrder.add(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM);
    }

    public void chapterOrder() {
        sortingOrder.add(FragmentEntryDatabaseHandler.CHAPTER);
    }

    public void personOrder() {
        sortingOrder.add(FragmentEntryDatabaseHandler.PERSON);
    }

    public void dateOrder() {
        sortingOrder.add(FragmentEntryDatabaseHandler.DATE);
    }

    public void entryType() {
        sortingOrder.add(FragmentEntryDatabaseHandler.TYPE);
    }

    public String getSortOrder() {
        return TextUtils.join(", ", sortingOrder);
    }

    private class UniqueValueList<E> extends LinkedList<E> {

        HashMap<E, Integer> recallValue;

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

            return addCorrectly && super.add(item);

        }

    }

}
