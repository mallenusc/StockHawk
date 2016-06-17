package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.sam_chordas.android.stockhawk.R;

public class StockChart extends CardController {


    private final LineChartView mChart;
    private String[] mLabels;
    private float[] mValues;

    private Tooltip mTip;

    private Runnable mBaseAction;


    public StockChart(CardView card, Context context) {
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.stock_chart);

    }

    public StockChart(CardView card, Context context, String[] Labels, float[] values) {
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.stock_chart);
        mLabels = Labels;
        mValues = values;
    }


    @Override
    public void show(Runnable action) {
        super.show(action);



        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#b3b5bb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#ffc755"))
                .setDotsRadius(0.1f)
                .setThickness(1);
        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(1))
                .setAxisBorderValues(0, 300)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(false)
                .setYAxis(false);

        mBaseAction = action;
        Runnable chartAction = new Runnable() {
            @Override
            public void run() {
                mBaseAction.run();
            }
        };

        Animation anim = new Animation()
                .setEasing(new BounceEase())
                .setEndAction(chartAction);

        mChart.show(anim);
    }


    @Override
    public void update() {
        super.update();

        mChart.dismissAllTooltips();
        mChart.getChartAnimation().setEndAction(mBaseAction);
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation()
                .setEasing(new BounceEase())
                .setEndAction(action));
    }

}
