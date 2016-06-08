package com.sam_chordas.android.stockhawk.models;

/**
 * Created by Marques Allen on 5/12/16.
 */

public class Quotes {

    private float[] quotes;

    private String[] dates;

    public Quotes() {

    }


    public Quotes(float[] prices, String[] dates) {
        this.quotes = prices;
        this.dates = dates;
    }

    public float[] getQuotes() {
        return quotes;
    }

    public String[] getDates() {
        return dates;
    }

    public void setQuotes(float[] quotes) {
        this.quotes = quotes;
    }

    public void setDates( String[] dates) {
        this.dates = dates;
    }

}

