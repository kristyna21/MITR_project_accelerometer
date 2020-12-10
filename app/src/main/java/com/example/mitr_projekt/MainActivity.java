package com.example.mitr_projekt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private LineChart chart;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ResetGraph();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * On click wrapper method for reset button
     */
    public void ResetGraphOnClick(View view)
    {
        ResetGraph();
    }

    /**
     * Reset graph to initial state
     */
    public void ResetGraph()
    {
        chart = (LineChart) findViewById(R.id.chart);

        LineDataSet lineX = new LineDataSet(null, "axis X");
        lineX.setColors(ColorTemplate.rgb("#FF0000"));
        lineX.setDrawCircles(false);

        LineDataSet lineY = new LineDataSet(null, "axis Y");
        lineY.setColors(ColorTemplate.rgb("#00FF00"));
        lineY.setDrawCircles(false);

        LineDataSet lineZ = new LineDataSet(null, "axis Z");
        lineZ.setColors(ColorTemplate.rgb("#0000FF"));
        lineZ.setDrawCircles(false);

        LineData lineData = new LineData(lineX,lineY,lineZ);
        chart.fitScreen();
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }

    /**
     * Method called on sensor value change
     * @param event
     */
    @Override
    public final void onSensorChanged(SensorEvent event) {

        float[] linear_acceleration = new float[3];

        Switch sw = (Switch) findViewById(R.id.switch1);
        if(sw.isChecked())
        {
            // Remove the gravity
            linear_acceleration[0] = event.values[0];
            linear_acceleration[1] = event.values[1] - 9.78f;
            linear_acceleration[2] = event.values[2] - 0.81f;
        }
        else
        {
            linear_acceleration[0] = event.values[0];
            linear_acceleration[1] = event.values[1];
            linear_acceleration[2] = event.values[2];
        }

        AddEntry(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
        UpdateTexts(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
    }

    /**
     * Method that updates all text entries on page
     * @param xval
     * @param yval
     * @param zval
     */
    private void UpdateTexts(float xval, float yval, float zval)
    {
        EditText axisXEntry = (EditText) findViewById(R.id.AxisXValue);
        axisXEntry.setText(String.format(Locale.UK,"%.2f", xval));

        EditText axisYEntry = (EditText) findViewById(R.id.AxisYValue);
        axisYEntry.setText(String.format(Locale.UK,"%.2f", yval));

        EditText axisZEntry = (EditText) findViewById(R.id.AxisZValue);
        axisZEntry.setText(String.format(Locale.UK,"%.2f", zval));
    }

    /**
     * Add value as new entry to axis of given index
     * @param data
     * @param axis
     * @param value
     */
    private void AddEntryToAxis(LineData data, int axis, float value)
    {
        ILineDataSet set = data.getDataSetByIndex(0);
        data.addEntry(new Entry(set.getEntryCount(), value), axis);
    }

    /**
     * Add entries for each axis and notify graph that data has been changed
     * @param xval
     * @param yval
     * @param zval
     */
    private void AddEntry(float xval, float yval, float zval) {

        LineData data = chart.getData();

        this.AddEntryToAxis(data, 0, xval);
        this.AddEntryToAxis(data, 1, yval);
        this.AddEntryToAxis(data, 2, zval);

        data.notifyDataChanged();

        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(1000);
        chart.moveViewToX(data.getEntryCount());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
