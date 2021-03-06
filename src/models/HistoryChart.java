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

import com.rdpharr.DataHabit.R;

public class HistoryChart {
	private dbAdapter mDbHelper;
	private Tracker t;
	private String title, dateFormat;
	private int type, daysShownAtStartup;
	private double [] yValues;
	private double yMin, yMax; 
	private Date xMin, xMax;
	private Date [] xValues;
	private Context ctx; 
	
	public HistoryChart (Context ctx, int trackerId){
		this.ctx = ctx;
		t=new Tracker(ctx, trackerId);
		dateFormat="dd-MMM hhaa";
		daysShownAtStartup = 14; //days of data to show on initial chart view (unzoomed)
		populateValues();
	}
	private void populateValues(){
		type = t.getType();
		title = t.getName();
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchAllData(t.getId());
		yValues = new double[c.getCount()];
		xValues = new Date[c.getCount()];
		yMin = 0;
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
			if (type==5)yValues[i]=yValues[i]/(1000*60);//Stopwatch: convert to minutes from millis
			if(i==0)yMax=yValues[i];
			if (yValues[i]>yMax) yMax=yValues[i];
			if (yValues[i]<yMin) yMin=yValues[i];
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
	    renderer.setPanLimits(new double[] { xMin.getTime(), xMax.getTime(), yMin, yMax});
	    renderer.setZoomLimits(new double[] { xMin.getTime(), xMax.getTime(), yMin, yMax });
	    renderer.setRange(new double[]{
	    		xMax.getTime()-daysShownAtStartup*24*60*60*1000,
	    		xMax.getTime(), 
	    		yMin, 
	    		yMax});
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