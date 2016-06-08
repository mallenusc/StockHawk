package com.sam_chordas.android.stockhawk.data;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.models.Quote;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 *
 */
public class WidgetListProvider implements RemoteViewsFactory{
    private ArrayList<Quote> listItemList = new ArrayList<Quote>();
    private Context mContext = null;
    private int appWidgetId;
    CursorLoader mCursorLoader;

    public WidgetListProvider(Context context, Intent intent) {
        this.mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

       // populateListItem();
    }

    public void populateListItem(Cursor cursor) {

//        mCursorLoader = new CursorLoader(mContext, QuoteProvider.Quotes.CONTENT_URI, null, null, null, null);
//        //  mCursorLoader.registerListener(LOADER_ID_NETWORK, this);
//        mCursorLoader.registerListener(0, this);
//        mCursorLoader.startLoading();
        // Bind data to UI, etc

        Log.e("WidgetService" , "**onLoadComplete** List Provider");


        if (cursor != null) {
            int startPos = cursor.getPosition();

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {

                if(cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISCURRENT)) == 1)
                {
                    Log.e("**WidgetService**", "Symbol - " + cursor.getString(cursor.getColumnIndex("symbol")));
                    Log.e("**WidgetService**", "Price - " + cursor.getString(cursor.getColumnIndex("bid_price")));
                    Log.e("**WidgetService**", "is up - " + cursor.getColumnIndex("is_up"));
                    Quote listItem = new Quote();
                    listItem.symbol = cursor.getString(cursor.getColumnIndex("symbol"));
                    listItem.bidPrice = cursor.getString(cursor.getColumnIndex("bid_price"));
                    listItem.isUp = cursor.getInt(cursor.getColumnIndex("is_up"));
                    listItem.change = cursor.getString(cursor.getColumnIndex("change"));
                    listItem.percentChange = cursor.getString(cursor.getColumnIndex("percent_change"));

                    listItemList.add(listItem);

                }


            }
            cursor.moveToPosition(startPos);
        }

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.list_item_quote);

 //       Log.e("ListProvider", "**getViewAt");
        Quote listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.stock_symbol, listItem.symbol);
        remoteView.setTextViewText(R.id.bid_price, listItem.bidPrice);

//        Log.e("**WidgetService**", "Symbol - " + listItem.symbol);
//        Log.e("**WidgetService**", "Price - " + listItem.bidPrice);
//        Log.e("**WidgetService**", "Percent Change - " + listItem.percentChange);
//        Log.e("**WidgetService**", "Change - " + listItem.change);
//        Log.e("**WidgetService**", "is up - " + listItem.isUp);

        int sdk = Build.VERSION.SDK_INT;
        if (listItem.isUp == 1){
            remoteView.setInt(R.id.change, "setBackgroundResource",
                    R.drawable.percent_change_pill_green);

        } else{
            remoteView.setInt(R.id.change, "setBackgroundResource",
                    R.drawable.percent_change_pill_red);
        }
        if (Utils.showPercent){
            remoteView.setTextViewText(R.id.change, listItem.percentChange);
        } else{
            remoteView.setTextViewText(R.id.change, listItem.change);
        }


        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

        // Stop the cursor loader
        if (mCursorLoader != null) {
            //mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }


//    @Override
//    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
//        // Bind data to UI, etc
//
//        Log.e("WidgetService" , "**onLoadComplete** List Provider");
//
//
//        if (cursor != null) {
//            int startPos = cursor.getPosition();
//
//            cursor.moveToPosition(-1);
//            while (cursor.moveToNext()) {
//
//                if(cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISCURRENT)) == 1)
//                {
////                    Log.e("**WidgetService**", "Symbol - " + cursor.getString(cursor.getColumnIndex("symbol")));
////                    Log.e("**WidgetService**", "Price - " + cursor.getString(cursor.getColumnIndex("bid_price")));
////                    Log.e("**WidgetService**", "is up - " + cursor.getColumnIndex("is_up"));
//                    Quote listItem = new Quote();
//                    listItem.symbol = cursor.getString(cursor.getColumnIndex("symbol"));
//                    listItem.bidPrice = cursor.getString(cursor.getColumnIndex("bid_price"));
//                    listItem.isUp = cursor.getInt(cursor.getColumnIndex("is_up"));
//                    listItem.change = cursor.getString(cursor.getColumnIndex("change"));
//                    listItem.percentChange = cursor.getString(cursor.getColumnIndex("percent_change"));
//
//                    listItemList.add(listItem);
//
//                }
//
//
//            }
//            cursor.moveToPosition(startPos);
//        }
//
//    }


}