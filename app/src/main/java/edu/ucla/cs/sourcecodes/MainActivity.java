package edu.ucla.cs.sourcecodes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidbelieve.drawerwithswipetabs.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends  Activity  {
    DrawerLayout mDrawerLayout;
    NavigationView mBottomView;
    android.app.FragmentManager mFragmentManager;
    android.app.FragmentTransaction mFragmentTransaction;

    private String TAG = "MainActivity.java";


    final int[] ICONS = new int[]{
            R.drawable.microphone,
            R.drawable.gallery,
            R.drawable.composea,
    };

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    private MyAdapter adapter;

    public static String _path;

/*    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";*/
    public Bundle getBundle() {


        Bundle args = new Bundle();
        args.putString("message", "takeAPicture");

        return args;
    }

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            /**
         *Inflate tab_layout and setup Views.
         */

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);


       // _path = DATA_PATH + "/ocr.jpg";
        /**
         *Set an Apater for the View Pager
         */
      //  viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        adapter = new MyAdapter(MainActivity.this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(3,true);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setIcon(ICONS[0]);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);


                //tabLayout.setOnTabSelectedListener(
                //        new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {


                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        // super.onTabSelected(tab);
                        int tabPosition  = tab.getPosition();
                        viewPager.setCurrentItem(tabPosition);

                        switch(tabPosition) {
                            case 0:
                                Log.d(TAG, "Position 0 selected");
                                break;
                            case 1:


                                final CameraFragment fragment = (CameraFragment)getFragmentManager().findFragmentById(R.id.camera_frag);
                                fragment.onInitiateCapture();



                              //  android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                              //  ft.add(new CameraFragment(), null);
                              //  ft.commit();

                           /*     mFragmentManager = getFragmentManager();
                                mFragmentTransaction = mFragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.camera_frag,new CameraFragment()).commit();*/
                                break;
                            case 2:
                                Log.d(TAG, "Position 2 selected");

                                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setAction(Intent.ACTION_VIEW);
                                startActivity(i);
                                break;
                        }

                        Log.d(TAG,"Tab Selected!!");


                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        int tabPosition  = tab.getPosition();
                        viewPager.setCurrentItem(tabPosition);


                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                        int tabPosition  = tab.getPosition();
                        viewPager.setCurrentItem(tabPosition);
                        switch(tabPosition) {
                            case 0:
                                Log.d(TAG, "Position 0 selected");
                                break;
                            case 1:


                                final CameraFragment fragment = (CameraFragment)getFragmentManager().findFragmentById(R.id.camera_frag);
                                fragment.onInitiateCapture();



                                //   startCameraActivity();


                                //  android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                                //  ft.add(new CameraFragment(), null);
                                //  ft.commit();

                           /*     mFragmentManager = getFragmentManager();
                                mFragmentTransaction = mFragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.camera_frag,new CameraFragment()).commit();*/
                                break;
                            case 2:
                                Log.d(TAG, "Position 2 selected");

                                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setAction(Intent.ACTION_VIEW);
                                startActivity(i);
                                break;
                        }


                        Log.d(TAG,"Re Tab Selected!!");
                    }


                });

            }
        });
       // return mContentView;

    }

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }


    private class  MyAdapter extends  PagerAdapter {

        private LayoutInflater mInflater;
        private int[] mLayouts = {R.layout.microphone_layout, R.layout.camera_layout, R.layout.note_layout};

        public MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup pageView = (ViewGroup) mInflater.inflate(mLayouts[position],
                    container, false);
            container.addView(pageView);
            getItemPosition(pageView);
            return pageView;
        }



        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

/*    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }


        //
        // Return fragment with respect to Position .
         ///

        @Override
        public  Fragment getItem(int position)
        {
            switch (position){
              //  case 0 : return new android.support.v4.app.Fragment();
              //  case 1 : return new android.support.v4.app.Fragment();
              //  case 2 : return new android.support.v4.app.Fragment();

                case 0: return new MicrophoneFragment();

                case 1: return new CameraFragment();


                case 2: return new  NoteFragment() ;


            }

            Log.d(TAG, "NULL");
            Log.d(TAG, "position in getItem: " + Integer.toString(position));

            return null;

        }

        @Override
        public int getCount() {

            return int_items;

        }

    }

*/

}