package com.example.smell_o_scope;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import org.json.JSONObject;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;


public class ExplanationActivity extends AppCompatActivity  {

    String INPUT_NAME = "input_1";
    String OUTPUT_NAME = "conv_dw_13/depthwise_kernel";

    private float[] floatValues;

    private Bitmap resizedbitmap1;

    float[] PREDICTIONS = new float[150128];
    private int[] INPUT_SIZE = {224, 224, 3};

    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarExplanation);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        resizedbitmap1 = Bitmap.createBitmap(ShowCaptureActivity.bitmap, (ShowCaptureActivity.bitmap.getWidth()-556)/2, (ShowCaptureActivity.bitmap.getHeight()-556)/2, 556, 556);
        predict(resizedbitmap1);

        TextView step1 = findViewById(R.id.step1);
        step1.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                onInputImageShowPopupWindowClick(findViewById(R.id.step1));
            }
        });

        TextView step2 = findViewById(R.id.step2);
        step2.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                onInputImageShowPopupWindowClick2(findViewById(R.id.step2));
            }
        });

        TextView step3 = findViewById(R.id.step3);
        step3.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                onInputImageShowPopupWindowClick3(findViewById(R.id.step3));
            }
        });

        TextView step4 = findViewById(R.id.step4);
        step4.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                onInputImageShowPopupWindowClick4(findViewById(R.id.step4));
            }
        });


//        Button backButton = findViewById(R.id.backbutton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent (ExplanationActivity.this, MenuFragment.class);
//                startActivity(intent);
//            }
//        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static final float BITMAP_SCALE = 0.4f;

    public static Bitmap timesWeights(Context context, Bitmap image, int[] weights) {
        int width = Math.round(image.getWidth());
        int height = Math.round(image.getHeight());

//        int[] pixels = new int[250000];
//        image.getPixels(pixels,0,width, 0,0, width, height);

//        int[] combinedValues = new int[150128];
//        for (int i = 0;i < pixels.length;i++) {
//            combinedValues[i] = pixels[i]*weights[i];
//        }

        int amount = 0;
        for(int value:weights){
            amount++;
        }
        System.out.println("AMOUJNR IS: " + amount);




//        image.setPixels(weights,0, width, 0,0,width,height);

        return image;
    }

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



        StringBuilder str = new StringBuilder();
        for(int i=0;i<PREDICTIONS.length;i++){
            str.append(PREDICTIONS[i] + " \n");
        }



        resultView = findViewById(R.id.resultview);


//        if (! Python.isStarted()) {
//            Python.start(new AndroidPlatform(ExplanationActivity.this));
//        }
//        Python py = Python.getInstance();
//        PyObject myClass = py.getModule("helloworld");
//        PyObject textHello = myClass.callAttr("plot_image", intPredictions);
//        String helloworld = textHello.toString();
//
//        resultView.setText(helloworld);

//        resultView.setText("Nr of predictions: " + predictions + '\n' + str);

        resultView.setText("This dish is mainly " + AnalyticsActivity.labeltje + ".");

//        ArrayList<Float> newFloats = new ArrayList<>(); //= floatValues * PREDICTIONS;
        int[] intPredictions = new int[150128];
        for (int i = 0; i < PREDICTIONS.length; i++) {
            intPredictions[i] = (int) PREDICTIONS[i];
        }




        Bitmap newBitmap = timesWeights(ExplanationActivity.this, bitmap, intPredictions);

        ImageView imView = findViewById(R.id.weightvisualize);

        imView.setImageBitmap(newBitmap);


    }

    public void onInputImageShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.explain_popup1, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setElevation(20);
        }

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onInputImageShowPopupWindowClick2(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.explain_popup2, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setElevation(20);
        }

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onInputImageShowPopupWindowClick3(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.explain_popup3, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setElevation(20);
        }

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onInputImageShowPopupWindowClick4(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.explain_popup4, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setElevation(20);
        }

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
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
