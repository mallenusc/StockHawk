package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.models.Quotes;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyGraphActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_line_graph);

        mContext = this;

        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DAY_OF_WEEK, -1);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initStockChart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }

    public int initStockChart() {

        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol "
                    + "in (", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String symbol = getIntent().getExtras().getString(QuoteColumns.SYMBOL);


        String startDate = "2015-09-12";
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mmm-dd");
        String endDate = df.format(rightNow.getTime());

        try {

            urlStringBuilder.append(URLEncoder.encode("\"" + symbol + "\")", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode(" and startDate = " + "\"" + startDate + "\"", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode(" and endDate =" + "\"" + endDate + "\"", "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // finalize the URL for the API query.
        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                + "org%2Falltableswithkeys&callback=");

        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;

        if (urlStringBuilder != null) {
            urlString = urlStringBuilder.toString();
            try {

                getResponse = fetchData(urlString);

                Quotes prices = Utils.quoteChartJsonToContentVals(getResponse);

                if (prices != null) {
                    final String[] date;
                    if (prices.getDates() == null) {
                        date = new String[prices.getDates().length];
                    } else {
                        date = prices.getDates();
                    }

                    date[3] = "Jan";
                    date[(prices.getDates().length / 5)] = "Feb";
                    date[(prices.getDates().length / 5) * 2] = "Mar";
                    date[(prices.getDates().length / 5) * 3] = "Apr";
                    date[(prices.getDates().length / 5) * 4] = "May";

                    final float[] price = prices.getQuotes();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            (new StockChart((CardView) findViewById(R.id.card1), mContext, date, price)).init();
                        }
                    });


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_stocks, menu);
        restoreActionBar();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_change_units) {
            // this is for changing stock changes from percent value to dollar value
            Utils.showPercent = !Utils.showPercent;
            this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
        }

        return super.onOptionsItemSelected(item);
    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


}
