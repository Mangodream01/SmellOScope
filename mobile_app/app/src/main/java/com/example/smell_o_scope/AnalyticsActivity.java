package com.example.smell_o_scope;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.*;
import org.tensorflow.TensorFlow;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
//import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.util.Arrays;
import java.util.Collections;

public class AnalyticsActivity extends AppCompatActivity {

    int CLASS_SIZE = 6; // MobileNet has 1000 classes + 1 zero-index
    int IMAGE_SIZE = 224;
    //    String INPUT_NAME = "input_1";
//    String OUTPUT_NAME = "dense";
    String INPUT_NAME = "input_1";
    String OUTPUT_NAME = "dense_1/Softmax";
    String[] OUTPUT_NAMES = {OUTPUT_NAME};

    private float[] floatValues;

    private Bitmap resizedbitmap1;

    static float[] PREDICTIONS = new float[6];
    private int[] INPUT_SIZE = {224, 224, 3};

    TextView resultView;
    List<SliceValue> pieData = new ArrayList<>();
    PieChartView pieChartView;

    public static String labeltje = "";


    private TensorFlowInferenceInterface inferenceInterface;


    private static final int PIXEL_WIDTH = 28;

    //an array that stores the pixel values
    private int[] intArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAnalytics);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ImageView mImage = findViewById(R.id.imageToBeAnalyzed);
//        mImage.setImageBitmap(ShowCaptureActivity.bitmap);

        Bitmap blurredBitmap = blur(AnalyticsActivity.this, ShowCaptureActivity.bitmap);
        mImage.setImageBitmap(blurredBitmap);

       // resizedbitmap1 = Bitmap.createBitmap(ShowCaptureActivity.bitmap, 0, 0, 224, 224);
        resizedbitmap1 = Bitmap.createBitmap(ShowCaptureActivity.bitmap, (ShowCaptureActivity.bitmap.getWidth()-556)/2, (ShowCaptureActivity.bitmap.getHeight()-556)/2, 556, 556);


//        intArray = new int[resizedbitmap1.getWidth() * resizedbitmap1.getHeight()];
//        resizedbitmap1.getPixels(intArray, 0, resizedbitmap1.getWidth(), 0, 0, resizedbitmap1.getWidth(), resizedbitmap1.getHeight());

        Button infoButton = findViewById(R.id.analysisInfo);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyticsActivity.this, ExplanationActivity.class);
                startActivity(intent);

            }
        });

        //resultView = (TextView) findViewById(R.id.imageAnalysisText);
        pieChartView = findViewById(R.id.chart);

        predict(resizedbitmap1);

//        // load the model
//        TensorFlowInferenceInterface tfii = new TensorFlowInferenceInterface(getAssets(), "frozen_graph.pb");
//
//        // input a image for classification
//        tfii.feed(INPUT_NAME, intArray, 6, IMAGE_SIZE, IMAGE_SIZE, 3);
//
//        // run the classification as session.run() in python.
//        tfii.run(OUTPUT_NAMES, false);
//
//        // get output variables which are the probabilities of the classes.
//        float[] outputs = new float[CLASS_SIZE];
//        tfii.fetch(OUTPUT_NAME, outputs);


    }


//    @SuppressLint("StaticFieldLeak")
    public void predict(final Bitmap bitmap) {
//        //Runs inference in background thread
//        new AsyncTask<Integer, Integer, Integer>() {
//            @Override
//            protected Integer doInBackground(Integer... params) {

                //Normalize the pixels
                floatValues = normalizeBitmap(bitmap, 556, 127.5f, 1.0f);

                // load the model
                TensorFlowInferenceInterface tfii = new TensorFlowInferenceInterface(getAssets(), "frozen_graph.pb");

                //Pass input into the tensorflow
                tfii.feed(INPUT_NAME, floatValues, 1, 556, 556, 3);

                //compute predictions
                tfii.run(new String[]{OUTPUT_NAME});


                //copy the output into the PREDICTIONS array
                tfii.fetch(OUTPUT_NAME, PREDICTIONS);

                //Obtained highest prediction
                Object[] results = argmax(PREDICTIONS);

                int class_index = (Integer) results[0];
                float confidence = (Float) results[1];


//              https://www.codingdemos.com/android-pie-chart-tutorial/
//            -- NOT SURE ABOUT LABELS ---

                pieData.add(new SliceValue(PREDICTIONS[0], Color.parseColor("#f2c776")).setLabel("Bitter " + String.valueOf(PREDICTIONS[0] * 100).substring(0,5) + "%"));
                pieData.add(new SliceValue(PREDICTIONS[1], Color.parseColor("#63452d")).setLabel("Meaty " + String.valueOf(PREDICTIONS[1] * 100).substring(0,5) + "%"));
                pieData.add(new SliceValue(PREDICTIONS[2], Color.parseColor("#b53232")).setLabel("Piquant " + String.valueOf(PREDICTIONS[2] * 100).substring(0,5) + "%"));
                pieData.add(new SliceValue(PREDICTIONS[3], Color.parseColor("#38a595")).setLabel("Salty " + String.valueOf(PREDICTIONS[3] * 100).substring(0,5) + "%"));
                pieData.add(new SliceValue(PREDICTIONS[4], Color.parseColor("#f2e479")).setLabel("Sour " + String.valueOf(PREDICTIONS[4] * 100).substring(0,5) + "%"));
                pieData.add(new SliceValue(PREDICTIONS[5], Color.parseColor("#e89dcc")).setLabel("Sweet " + String.valueOf(PREDICTIONS[5] * 100).substring(0,5) + "%"));

//                pieData.add(new SliceValue(PREDICTIONS[0], Color.YELLOW).setLabel("Meaty"));
//                pieData.add(new SliceValue(PREDICTIONS[1], Color.MAGENTA).setLabel("Sweet"));
//                pieData.add(new SliceValue(PREDICTIONS[2], Color.RED).setLabel("Piquant"));
//                pieData.add(new SliceValue(PREDICTIONS[3], Color.BLUE).setLabel("Sour"));
//                pieData.add(new SliceValue(PREDICTIONS[4], Color.CYAN).setLabel("Salty"));
//                pieData.add(new SliceValue(PREDICTIONS[5], Color.GREEN).setLabel("Bitter"));

                int largest = getIndexOfLargest(PREDICTIONS);

                if(largest == 0){
                    labeltje = "bitter";
                }
                if(largest == 1){
                    labeltje = "meaty";
                }
                if(largest == 2){
                    labeltje = "piquant";
                 }
                if(largest == 3){
                    labeltje = "salty";
                 }
                 if(largest == 4) {
                     labeltje = "sour";
                 }
                 if(largest == 5){
                    labeltje = "sweet";
                 }


                PieChartData pieChartData = new PieChartData(pieData);
                pieChartData.setHasLabels(true);

                pieChartView.setPieChartData(pieChartData);

                String bestresult = Integer.toString(class_index) + " " +  Float.toString(confidence);

//                Toast.makeText(AnalyticsActivity.this, bestresult, Toast.LENGTH_LONG).show();
        try{

            final String conf = String.valueOf(confidence * 100).substring(0,5);

            //Convert predicted class index into actual label name
            final String label = getLabel(getAssets().open("labels.json"),class_index);

            StringBuilder str = new StringBuilder();
            for(int i=0;i<PREDICTIONS.length;i++){
                str.append(PREDICTIONS[i] + " \n");
            }

            resultView.setText(label + " : " + conf + "%" + " \n" + str + "\n " + bestresult);


        }

        catch (Exception e){


        }


            }

    public int getIndexOfLargest( float[] array )
    {
        if ( array == null || array.length == 0 ) return -1; // null or empty

        int largest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] ) largest = i;
        }
        return largest; // position of the first largest found
    }


//        }.execute(0);




    public static float[] normalizeBitmap(Bitmap source, int size, float mean, float std) {

        float[] output = new float[size * size * 3];

        int[] intValues = new int[source.getHeight() * source.getWidth()];

        source.getPixels(intValues, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            output[i * 3] = (((val >> 16) & 0xFF) - mean) / std;
            output[i * 3 + 1] = (((val >> 8) & 0xFF) - mean) / std;
            output[i * 3 + 2] = ((val & 0xFF) - mean) / std;
        }

        return output;

    }


    public static Object[] argmax(float[] array) {


        int best = -1;
        float best_confidence = 0.0f;

        for (int i = 0; i < array.length; i++) {

            float value = array[i];

            if (value > best_confidence) {

                best_confidence = value;
                best = i;
            }
        }

        return new Object[]{best,best_confidence};


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    public static Bitmap blur(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        }
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
        }

        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static String getLabel(InputStream jsonStream, int index){
        String label = "";
        try {

            byte[] jsonData = new byte[jsonStream.available()];
            jsonStream.read(jsonData);
            jsonStream.close();

            String jsonString = new String(jsonData,"utf-8");

            JSONObject object = new JSONObject(jsonString);

            label = object.getString(String.valueOf(index));



        }
        catch (Exception e){


        }
        return label;
    }
}




