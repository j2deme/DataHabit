package com.rdpharr.DataHabit;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;

public class HistoryChart {
	private dbAdapter mDbHelper;
	private Tracker t;
	private String title;
	private int type;
	private double [] yValues;
	private Date [] xValues;
	private String intentTitle = "HistoryChart";
	
	public HistoryChart (Context ctx, int trackerId){
		t=new Tracker(ctx, trackerId);
		type = t.getType();
		title = t.getName();
		
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchAllData(trackerId);
		yValues = new double[c.getCount()];
		xValues = new Date[c.getCount()];
		for (int i=0;i<c.getCount();i++){
			Log.d("Loop", Integer.toString(i));
			c.moveToPosition(i);
			xValues[i]=new Date(c.getLong(2));
			yValues[i]=c.getDouble(3);
		}
		c.close();
		mDbHelper.close();
	}
	public Intent showChart(Context ctx){
		return ChartFactory.getTimeChartIntent(ctx, getDataset(ctx), getRenderer(), intentTitle);
	}
	private XYMultipleSeriesDataset getDataset(Context ctx){
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		TimeSeries series = new TimeSeries(title);
		for (int i=0;i<xValues.length;i++)	series.add(xValues[i], yValues[i]);
		dataset.addSeries(series);
		return dataset;
	}
	private XYMultipleSeriesRenderer getRenderer() {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(12);
	    renderer.setChartTitleTextSize(12);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(5);
	    renderer.setMargins(new int[] { 20, 30, 15, 0 });
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    r.setColor(Color.WHITE);
	    r.setPointStyle(PointStyle.CIRCLE);
	    r.setFillBelowLine(false);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    setChartSettings(renderer);
	    return renderer;
	}
	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
	    renderer.setChartTitle(title);
	    renderer.setXTitle("Date");//TODO string
	    renderer.setYTitle("Value/Rating");//TODO string
	    //renderer.setApplyBackgroundColor(false);
	    //renderer.setRange(new double[] {0,6,-70,40}); 
	    //renderer.setFitLegend(false);
	    //renderer.setAxesColor(Color.BLACK);
	    renderer.setShowGrid(true);
	    //renderer.setXAxisMin(0.5);
	    //renderer.setXAxisMax(10.5);
	    //renderer.setYAxisMin(0);
	    //renderer.setZoomEnabled(true);
	    //renderer.setYAxisMax(30);
	  }
}






