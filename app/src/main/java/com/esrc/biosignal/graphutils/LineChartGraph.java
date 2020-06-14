package com.esrc.biosignal.graphutils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.RelativeLayout;


import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class LineChartGraph {
    private static final int GRAPH_MODE_REAL = 0;

    private Context mContext;
    private int mWindowSize;
    private int mIntervalSize;
    private int mGraphMode;

    private RelativeLayout mLayout;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private GraphicalView mView;
    private int mCount;
    private XYSeries mSeries;

    public LineChartGraph(Context context, RelativeLayout layout, int window_size, int interval_size)
    {
        // Set context variables
        mContext = context;
        mLayout = layout;
        mWindowSize = window_size;  //넘어가는 속도??
        mIntervalSize = interval_size;

        // Initialize variables
        mSeries = new XYSeries("PPG");

        // Initialize graph property
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(10);
        renderer.setColor(Color.argb(0xFF, 0xFF, 0xBB, 0x00));
        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setMargins(new int[]{30, 70, 0, 30});
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setMarginsColor(Color.argb(0x00, 0xFF, 0xFF, 0xFF));
        mRenderer.setBackgroundColor(Color.TRANSPARENT);
        mRenderer.setGridColor(Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
        mRenderer.setAxesColor(Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
        mRenderer.setLabelsColor(Color.argb(0xFF, 0x99, 0x99, 0x99));
        mRenderer.setXLabelsColor(Color.argb(0xFF, 0x99, 0x99, 0x99));
        mRenderer.setYLabelsColor(0, Color.argb(0xFF, 0x99, 0x99, 0x99));
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setAntialiasing(true);
        mRenderer.setShowGrid(false);
        mRenderer.setShowLegend(false);
        mRenderer.setShowGridX(true);
        mRenderer.setYAxisMax(1024);
        mRenderer.setYAxisMin(0);
        mRenderer.setYLabels(10);
        mRenderer.setChartTitleTextSize(50);
        mRenderer.setLabelsTextSize(40);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);

        // Set real graph initially
        setRealGraph();
    }

    public void add(long value)
    {
        if((mCount == 0) && (mSeries.getItemCount() > 0)) mSeries.clear();

        // Add
        mSeries.add(mCount++, value);
        if (mSeries.getItemCount() >= mWindowSize) {
            for (int i = 0; i < mIntervalSize; i++) {
                mSeries.remove(0);
            }
        }

        // Display
        if(mGraphMode == GRAPH_MODE_REAL) {
            mView.invalidate();
        }
    }

    public void setRealGraph()
    {
        // Set graph mode
        mGraphMode = GRAPH_MODE_REAL;

        // Set graph property
        if(mCount == 0) mSeries.add(0, 50);
        mDataset = new XYMultipleSeriesDataset();
        mDataset.addSeries(mSeries);
        mRenderer.setChartTitle("");
        mRenderer.setXLabelsColor(Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
        mRenderer.setXLabels(1);
        mView = new GraphicalView(mContext, new LineChart(mDataset, mRenderer));
        mLayout.addView(mView);
    }
}
