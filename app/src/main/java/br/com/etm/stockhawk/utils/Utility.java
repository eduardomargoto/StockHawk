package br.com.etm.stockhawk.utils;

import android.support.v4.util.Pair;

import com.github.mikephil.charting.data.Entry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by EDUARDO_MARGOTO on 2/18/2017.
 */

public class Utility {

    private static DecimalFormat dollarFormatWithPlus = null;
    private static DecimalFormat dollarFormat = null;
    private static DecimalFormat percentageFormat = null;


    public static DecimalFormat getDollarFormatWithPlus() {
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        return dollarFormatWithPlus;
    }

    public static DecimalFormat getDollarFormat() {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        return dollarFormat;
    }

    public static DecimalFormat getPercentageFormat() {
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
        return percentageFormat;
    }

    public static Pair<Float, List<Entry>> getFormattedHistory(String history) {
        List<Entry> entries = new ArrayList<>();
        List<Float> timeData = new ArrayList<>();
        List<Float> stockPrice = new ArrayList<>();
        String[] dataPairs = history.split("\\n");

        for (String pair : dataPairs) {
            String[] entry = pair.split(",");
            timeData.add(Float.valueOf(entry[0]));
            stockPrice.add(Float.valueOf(entry[1]));
        }
        Collections.reverse(timeData);
        Collections.reverse(stockPrice);
        Float referenceTime = timeData.get(0);
        for (int i = 0; i < timeData.size(); i++) {
            entries.add(new Entry(timeData.get(i) - referenceTime, stockPrice.get(i)));
        }
        return new Pair<>(referenceTime, entries);
    }
}
