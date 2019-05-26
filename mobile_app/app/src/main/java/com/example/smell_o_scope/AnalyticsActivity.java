package com.example.smell_o_scope;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.*;
import org.tensorflow.TensorFlow;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
//import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class AnalyticsActivity extends AppCompatActivity {

    int CLASS_SIZE = 6; // MobileNet has 1000 classes + 1 zero-index
    int IMAGE_SIZE = 224;
//    String INPUT_NAME = "input_1";
//    String OUTPUT_NAME = "dense";
    String INPUT_NAME = "Input";
    String OUTPUT_NAME = "Output";
    String[] OUTPUT_NAMES = {OUTPUT_NAME};

    private static final String MODEL_FILE = "file:///android_asset/frozen_model_iris.pb";

    private static final String INPUT_NODE = "Input";
    private static final String OUTPUT_NODE = "Output";

    private static final int[] INPUT_SIZE = {1, 4};


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

        Bitmap blurredBitmap = blur( AnalyticsActivity.this, ShowCaptureActivity.bitmap );
        mImage.setImageBitmap(blurredBitmap);

        intArray = new int[ShowCaptureActivity.bitmap.getWidth()*ShowCaptureActivity.bitmap.getHeight()];
        ShowCaptureActivity.bitmap.getPixels(intArray, 0, ShowCaptureActivity.bitmap.getWidth(), 0, 0, ShowCaptureActivity.bitmap.getWidth(), ShowCaptureActivity.bitmap.getHeight());



        Button infoButton = findViewById(R.id.analysisInfo);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (AnalyticsActivity.this, ExplanationActivity.class);
                startActivity(intent);

            }
        });

        // *DIFFERENT METHODS FOR INCLUDING NEURAL NET*
//        inferenceInterface = new TensorFlowInferenceInterface(getAssets(),MODEL_FILE);
//        // We need to initialize our Tensorflow interface with the MODEL_FILE
//        System.out.println("Model Loaded Successfully");
//
//        // Input node values in the interface
//        inferenceInterface.feed(INPUT_NODE, intArray, 3, INPUT_SIZE[0], INPUT_SIZE[1], 3);
//
//        // Run the interface to get the output for input node value
//        inferenceInterface.run(new String[]{OUTPUT_NODE});
//
//        // We read the float and store it in variable result
//        float[] result = {0, 0, 0};
//        inferenceInterface.fetch(OUTPUT_NODE, result);
//
//        int class_id = argmax(result);
//
//        System.out.println(class_id);

//                  // load the model
//           TensorFlowInferenceInterface tfii = new TensorFlowInferenceInterface(getAssets(), "frozen_model_iris.pb");
//
//            // input a image for classification
//            tfii.feed(INPUT_NAME, intArray, 3, IMAGE_SIZE, IMAGE_SIZE, 3);
//
//            // run the classification as session.run() in python.
//            tfii.run(OUTPUT_NAMES, false);
//
//            // get output variables which are the probabilities of the classes.
//            float[] outputs = new float[CLASS_SIZE];
//            tfii.fetch(OUTPUT_NAME, outputs);
//
//            System.out.println(outputs);



//      *DOES NOT WORK*
//        SavedModelBundle savedModelBundle = SavedModelBundle.load("./assets", "flavour");
//        Graph graph = savedModelBundle.graph();
//        //printOperations(graph);
//        Tensor result = savedModelBundle.session().runner()
//                .feed("myInput", Tensor.create(intArray))
//                .fetch("myOutput")
//                .run().get(0);
//
//        System.out.println(result);


        }



    static {
        System.loadLibrary("tensorflow_inference");
    }



    public static int argmax(float[] elems) {
        int bestIdx = -1;
        float max = -1000;
        for (int i = 0; i < elems.length; i++) {
            float elem = elems[i];
            if (elem > max) {
                max = elem;
                bestIdx = i;
            }
        }
        return bestIdx;
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



}
