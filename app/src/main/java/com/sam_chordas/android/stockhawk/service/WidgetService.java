package com.sam_chordas.android.stockhawk.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.WidgetListProvider;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.WidgetQuoteProvider;


public class WidgetService extends RemoteViewsService implements Loader.OnLoadCompleteListener<Cursor> {
	/*
	 * So pretty simple just defining the Adapter of the listview
	 * here Adapter is ListProvider
	 * */
private CursorLoader mCursorLoader;
private WidgetListProvider mListProvider;
  private Intent mIntent;

  @Override
  public void onCreate() {
    mCursorLoader = new CursorLoader(getApplicationContext(), QuoteProvider.Quotes.CONTENT_URI, null, null, null, null);
  //  mCursorLoader.registerListener(LOADER_ID_NETWORK, this);
    mCursorLoader.registerListener(0, this);
    mCursorLoader.startLoading();

  }

  @Override
  public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
    // Bind data to UI, etc

    Log.e("WidgetService" , "**onLoadComplete**");
    if(mListProvider != null)
    {

      if (data != null) {
        int startPos = data.getPosition();

        data.moveToPosition(-1);
        while (data.moveToNext()) {

          if(data.getInt(data.getColumnIndex(QuoteColumns.ISCURRENT)) == 1)
          {
            Log.e("**WidgetService**", "Symbol - " + data.getString(data.getColumnIndex("symbol")));
            Log.e("**WidgetService**", "Price - " + data.getString(data.getColumnIndex("bid_price")));
          }


        }
        data.moveToPosition(startPos);

        mListProvider.populateListItem(data);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, WidgetQuoteProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
      }


//      final int count = data.getCount();
//
//      // We disable the data changed observer temporarily since each of the updates
//      // will trigger an onChange() in our data observer.
//      for (int i = 0; i < count; i++) {
//          Log.e("WidgetService" , "stock cursor count i: " + i);
////        Log.e("**WidgetService**", "Symbol - " + data.getString(data.getColumnIndex("symbol")));
//       // Log.e("**WidgetService**", "Symbol - " + data.getString(0));
//
//        data.moveToNext();
//      }

//      viewHolder.symbol.setText(cursor.getString(cursor.getColumnIndex("symbol")));
//      viewHolder.bidPrice.setText(cursor.getString(cursor.getColumnIndex("bid_price")));

//
//      for (int i = 0; i < initQueryCursor.getCount(); i++){
//        mStoredSymbols.append("\""+
//                initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"))+"\",");
//        initQueryCursor.moveToNext();
//      }

    //  DatabaseUtils.dumpCursor(data);



    }
    //mListProvider
  }

  @Override
  public void onDestroy() {

    // Stop the cursor loader
    if (mCursorLoader != null) {
      mCursorLoader.unregisterListener(this);
      mCursorLoader.cancelLoad();
      mCursorLoader.stopLoading();
    }
  }

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    mIntent = intent;
    int appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID);

    mListProvider = new WidgetListProvider(this.getApplicationContext(), intent);
Log.e("WidgetService" , "**onGetViewFactory**");

    return mListProvider;
  }

}