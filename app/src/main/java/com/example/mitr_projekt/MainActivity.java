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
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private LineChart chart;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = (LineChart) findViewById(R.id.chart);

        LineDataSet lineX = new LineDataSet(null, "axis X");
        lineX.setColors(ColorTemplate.rgb("#FF0000"));

        LineDataSet lineY = new LineDataSet(null, "axis Y");
        lineY.setColors(ColorTemplate.rgb("#00FF00"));

        LineDataSet lineZ = new LineDataSet(null, "axis Z");
        lineZ.setColors(ColorTemplate.rgb("#0000FF"));

        LineData lineData = new LineData(lineX,lineY,lineZ);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;

        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        // linear_acceleration[0] = event.values[0] - gravity[0];
        // linear_acceleration[1] = event.values[1] - gravity[1];
        // linear_acceleration[2] = event.values[2] - gravity[2];

        linear_acceleration[0] = event.values[0];
        linear_acceleration[1] = event.values[1];
        linear_acceleration[2] = event.values[2];

        String textstr = Float.toString(linear_acceleration[0]) + " " + Float.toString(linear_acceleration[1]) + " " +Float.toString(linear_acceleration[2]);

        this.addEntry(event.values[0], event.values[1], event.values[2]);
        EditText text = (EditText) findViewById(R.id.editTextTextPersonName2);
        text.setText(textstr);
        // Do something with this sensor value.
    }

    private void UpdateData(LineData data, int index, float value)
    {
        ILineDataSet set = data.getDataSetByIndex(0);

        data.addEntry(new Entry(set.getEntryCount(), value), index);
    }

    private void addEntry(float xval, float yval, float zval) {

        LineData data = chart.getData();

        if (data != null) {

            this.UpdateData(data, 0, xval);
            this.UpdateData(data, 1, yval);
            this.UpdateData(data, 2, zval);

            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(1000);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
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
