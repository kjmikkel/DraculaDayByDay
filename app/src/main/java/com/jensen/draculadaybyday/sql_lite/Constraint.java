package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Constraint implements Parcelable {

    private String constraintSqlText;
    final private List<String> constraintSqlValues;

    public Constraint() {
        constraintSqlText = "";
        constraintSqlValues = new ArrayList<>();
    }

    public Constraint(Parcel in) {
        constraintSqlText = in.readString();

        constraintSqlValues = new ArrayList<>();
        in.readStringList(constraintSqlValues);
    }

    //region ConstraintSqlText
    public void setConstraintSqlText(String constraintSqlText) {
        this.constraintSqlText = constraintSqlText;
    }

    public String getConstraintSqlText() {
        return constraintSqlText;
    }
    //endregion

    //region ConstraintValue
    public void addConstraintSqlValue(String constraintValue) {
        constraintSqlValues.add(constraintValue);
    }

    public List<String> getConstraintSqlValues() {
        return constraintSqlValues;
    }
    //endregion

    public boolean hasConstraints() {
        return 0 < constraintSqlText.length();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeString(constraintSqlText);
            dest.writeStringList(constraintSqlValues);
        } catch (Exception e) {
            Log.d("Constraint write", e.getMessage());
        }
    }

    public static final Parcelable.Creator<Constraint> CREATOR
            = new Parcelable.Creator<Constraint>() {

        public Constraint createFromParcel(Parcel in) {
            return new Constraint(in);
        }

        public Constraint[] newArray(int size) {
            return new Constraint[size];
        }
    };
}
