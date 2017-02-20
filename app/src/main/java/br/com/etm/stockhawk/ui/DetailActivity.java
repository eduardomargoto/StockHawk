package br.com.etm.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import java.util.List;

import br.com.etm.stockhawk.R;
import br.com.etm.stockhawk.data.Contract;
import br.com.etm.stockhawk.data.PrefUtils;
import br.com.etm.stockhawk.utils.CustomMarkerView;
import br.com.etm.stockhawk.utils.Utility;
import br.com.etm.stockhawk.utils.XAxisDateFormatter;
import br.com.etm.stockhawk.utils.YAxisPriceFormatter;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int LOADER_ID = 0;

    @BindView(R.id.symbol)
    TextView symbol;

    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.change)
    TextView change;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.chart)
    LineChart chart;

    @BindColor(R.color.white)
    int white;

    @BindColor(R.color.colorPrimary)
    int colorPrimary;

    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;

    private Uri uriSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        if (getIntent().getData() != null) {
            uriSymbol = getIntent().getData();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }



    private void setLineChart(String history) {
        Pair<Float, List<Entry>> result = Utility.getFormattedHistory(history);
        List<Entry> dataPairs = result.second;
        Float referenceTime = result.first;
        LineDataSet dataSet = new LineDataSet(dataPairs, "");
        dataSet.setColor(colorPrimary);
        dataSet.setLineWidth(1.5f);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setCircleColor(colorPrimaryDark);
        dataSet.setHighLightColor(colorPrimaryDark);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new XAxisDateFormatter("MMM", referenceTime));
        xAxis.setTextColor(white);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setValueFormatter(new YAxisPriceFormatter());
        yAxis.setTextColor(white);

        CustomMarkerView customMarkerView = new CustomMarkerView(this,
                R.layout.marker_view, dataPairs.get(dataPairs.size()-1), referenceTime);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        chart.setMarker(customMarkerView);
        //disable all interactions with the graph
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDragDecelerationEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        Description description = new Description();
        description.setText(" ");
        chart.setDescription(description);
        chart.setExtraOffsets(10, 0, 0, 10);
        chart.animateX(1500, Easing.EasingOption.Linear);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (uriSymbol != null) {
            return new CursorLoader(
                    this,
                    uriSymbol,
                    Contract.Quote.FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            name.setText(data.getString(Contract.Quote.POSITION_NAME));
            symbol.setText(data.getString(Contract.Quote.POSITION_SYMBOL));
            price.setText(Utility.getDollarFormat().format(data.getFloat(Contract.Quote.POSITION_PRICE)));

            float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            if (rawAbsoluteChange > 0) {
                change.setBackgroundResource(R.drawable.percent_change_pill_green);
            } else {
                change.setBackgroundResource(R.drawable.percent_change_pill_red);
            }

            String change_value = Utility.getDollarFormatWithPlus().format(rawAbsoluteChange);
            String percentage = Utility.getPercentageFormat().format(percentageChange / 100);

            if (PrefUtils.getDisplayMode(this)
                    .equals(getString(R.string.pref_display_mode_absolute_key))) {
                change.setText(change_value);
            } else {
                change.setText(percentage);
            }

            setLineChart(data.getString(Contract.Quote.POSITION_HISTORY));

            chart.invalidate();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
