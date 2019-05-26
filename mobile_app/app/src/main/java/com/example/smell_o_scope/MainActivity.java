package com.example.smell_o_scope;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.IDNA;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;

    public static int currentItem = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(currentItem);

        showAddItemDialog(this);

//        if(CameraFragment.showMenu == true){
//            viewPager.setCurrentItem(0,true);
//        }


    }

    private void showAddItemDialog(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("How to use")
                .setMessage("Swipe right to view the menu or take a picture straight away!")
                .setNegativeButton("Understood", null)
                .create();
        dialog.show();
    }

    public void goToInfoActivity (View view){
        Intent intent = new Intent (this, InfoActivity.class);
        startActivity(intent);
    }

    public void goToGallery (View view){
        Intent intent = new Intent (this, GalleryActivity.class);
        startActivity(intent);
    }

    public void onLogOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }



    public static class MyPagerAdapter extends FragmentPagerAdapter{
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            switch (position){
                case 0:
                    // return fragment menu
                    return MenuFragment.newInstance();
                case 1:
                    // return fragment camera
                    return CameraFragment.newInstance();
//                case 2:
//                    return GalleryFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount(){
            return 2;
        }
    }

}