package models;

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

import com.rdpharr.DataHabit.R;

public class HistoryChart {
	private dbAdapter mDbHelper;
	private Tracker t;
	private String title, dateFormat;
	private int type;
	private double [] yValues;
	private double yMin; 
	private Date xMin, xMax;
	private Date [] xValues;
	private Context ctx; 
	
	public HistoryChart (Context ctx, int trackerId){
		this.ctx = ctx;
		t=new Tracker(ctx, trackerId);
		type = t.getType();
		title = t.getName();
		yMin = 0;
		dateFormat="dd-MMM-yy hhaa";
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchAllData(trackerId);
		yValues = new double[c.getCount()];
		xValues = new Date[c.getCount()];
		for (int i=0;i<c.getCount();i++){
			c.moveToPosition(i);
			xValues[i]=new Date(c.getLong(2));
			if (i==0){
				xMin=xValues[i];
				xMax=xValues[i];
			}
			if (xMin.compareTo(xValues[i])>0)xMin=xValues[i];
			if (xMax.compareTo(xValues[i])<0)xMax=xValues[i];
			
			yValues[i]=c.getDouble(3);
			if (yValues[i]<yMin) yMin=yValues[i];
			if (type==5)yValues[i]=yValues[i]/(1000*60);//convert to minutes
				
		}
		c.close();
		mDbHelper.close();
	}
	public Intent showChart(Context ctx){
		return ChartFactory.getTimeChartIntent(ctx, getDataset(ctx), getRenderer(), dateFormat);
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
	    renderer.setAxisTitleTextSize(20);
	    renderer.setChartTitleTextSize(30);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(5);
	    renderer.setYAxisMin(yMin);
	    renderer.setZoomButtonsVisible(true);
	    renderer.setPanLimits(new double[] { xMin.getTime(), xMax.getTime(), 0, 1000});
	    renderer.setZoomLimits(new double[] { xMin.getTime(), xMax.getTime(), 0, 1000 });
	    renderer.setMargins(new int[] { 20, 30, 15, 0 });
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    r.setColor(Color.WHITE);
	    r.setFillBelowLine(true);
	    r.setFillBelowLineColor(Color.GRAY);
	    r.setPointStyle(PointStyle.CIRCLE);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    setChartSettings(renderer);
	    return renderer;
	}
	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
	    renderer.setChartTitle(title);
	    renderer.setXTitle(ctx.getString(R.string.date));
	    renderer.setYTitle(ctx.getString(R.string.value));
	    if (type==5)renderer.setYTitle(ctx.getString(R.string.minutes));
	    renderer.setShowGrid(true);
	  }
}