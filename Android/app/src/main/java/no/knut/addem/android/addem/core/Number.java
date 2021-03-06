package no.knut.addem.android.addem.core;

import java.io.Serializable;

public class Number implements Serializable{

    private int row, column, value;

    public Number(int row, int column, int value){
        this.row = row;
        this.column = column;
        this.value = value;

    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        Number other = (Number) o;

        if (other.row == row && other.column == column)
            return true;

        else return false;
    }

    @Override
    public String toString() {
        return "{ row:"+row + ", col:"+ column + ", val:" + value+ " }";
    }
}
