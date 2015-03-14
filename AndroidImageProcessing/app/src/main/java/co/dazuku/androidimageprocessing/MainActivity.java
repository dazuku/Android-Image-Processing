package co.dazuku.androidimageprocessing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.Matrix3f;
import android.support.v8.renderscript.RenderScript;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, CurvesFragment.newInstance())
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private final String TAG = "Img";
        private Bitmap mBitmapIn;
        private Bitmap mBitmapOut;
        private float mInBlack = 0.0f;
        private SeekBar mInBlackSeekBar;
        private float mOutBlack = 0.0f;
        private SeekBar mOutBlackSeekBar;
        private float mInWhite = 255.0f;
        private SeekBar mInWhiteSeekBar;
        private float mOutWhite = 255.0f;
        private SeekBar mOutWhiteSeekBar;
        private float mGamma = 1.0f;
        private SeekBar mGammaSeekBar;
        private float mSaturation = 1.0f;
        private SeekBar mSaturationSeekBar;
        private TextView mBenchmarkResult;
        private ImageView mDisplayView;

        Matrix3f satMatrix = new Matrix3f();
        float mInWMinInB;
        float mOutWMinOutB;
        float mOverInWMinInB;

        private RenderScript mRS;
        private Allocation mInPixelsAllocation;
        private Allocation mOutPixelsAllocation;
        private ScriptC_main mScript;

        private void setLevels() {
            mInWMinInB = mInWhite - mInBlack;
            mOutWMinOutB = mOutWhite - mOutBlack;
            mOverInWMinInB = 1.f / mInWMinInB;

            mScript.set_inBlack(mInBlack);
            mScript.set_outBlack(mOutBlack);
            mScript.set_inWMinInB(mInWMinInB);
            mScript.set_outWMinOutB(mOutWMinOutB);
            mScript.set_overInWMinInB(mOverInWMinInB);
        }

        private void setSaturation() {
            float rWeight = 0.299f;
            float gWeight = 0.587f;
            float bWeight = 0.114f;
            float oneMinusS = 1.0f - mSaturation;

            satMatrix.set(0, 0, oneMinusS * rWeight + mSaturation);
            satMatrix.set(0, 1, oneMinusS * rWeight);
            satMatrix.set(0, 2, oneMinusS * rWeight);
            satMatrix.set(1, 0, oneMinusS * gWeight);
            satMatrix.set(1, 1, oneMinusS * gWeight + mSaturation);
            satMatrix.set(1, 2, oneMinusS * gWeight);
            satMatrix.set(2, 0, oneMinusS * bWeight);
            satMatrix.set(2, 1, oneMinusS * bWeight);
            satMatrix.set(2, 2, oneMinusS * bWeight + mSaturation);
            mScript.set_colorMat(satMatrix);
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (seekBar == mInBlackSeekBar) {
                    mInBlack = (float)progress;
                    setLevels();
                } else if (seekBar == mOutBlackSeekBar) {
                    mOutBlack = (float)progress;
                    setLevels();
                } else if (seekBar == mInWhiteSeekBar) {
                    mInWhite = (float)progress + 127.0f;
                    setLevels();
                } else if (seekBar == mOutWhiteSeekBar) {
                    mOutWhite = (float)progress + 127.0f;
                    setLevels();
                } else if (seekBar == mGammaSeekBar) {
                    mGamma = (float)progress/100.0f;
                    mGamma = Math.max(mGamma, 0.1f);
                    mGamma = 1.0f / mGamma;
                    mScript.set_gamma(mGamma);
                } else if (seekBar == mSaturationSeekBar) {
                    mSaturation = (float)progress / 50.0f;
                    setSaturation();
                }

                filter();
                mDisplayView.invalidate();
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        private Bitmap loadBitmap(int resource) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap b = BitmapFactory.decodeResource(getResources(), resource, options);
            Bitmap b2 = Bitmap.createBitmap(b.getWidth(), b.getHeight(), b.getConfig());
            Canvas c = new Canvas(b2);
            c.drawBitmap(b, 0, 0, null);
            b.recycle();
            return b2;
        }

        private void filter() {
            mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
            mOutPixelsAllocation.copyTo(mBitmapOut);
        }

        public void benchmark(View v) {
            filter();
            long t = java.lang.System.currentTimeMillis();
            filter();
            t = java.lang.System.currentTimeMillis() - t;
            mDisplayView.invalidate();
            mBenchmarkResult.setText("Result: " + t + " ms");
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mBitmapIn = loadBitmap(R.drawable.city);
            mBitmapOut = loadBitmap(R.drawable.city);

            mDisplayView = (ImageView) rootView.findViewById(R.id.display);
            mDisplayView.setImageBitmap(mBitmapOut);

            mInBlackSeekBar = (SeekBar) rootView.findViewById(R.id.inBlack);
            mInBlackSeekBar.setOnSeekBarChangeListener(this);
            mInBlackSeekBar.setMax(128);
            mInBlackSeekBar.setProgress(0);
            mOutBlackSeekBar = (SeekBar) rootView.findViewById(R.id.outBlack);
            mOutBlackSeekBar.setOnSeekBarChangeListener(this);
            mOutBlackSeekBar.setMax(128);
            mOutBlackSeekBar.setProgress(0);

            mInWhiteSeekBar = (SeekBar) rootView.findViewById(R.id.inWhite);
            mInWhiteSeekBar.setOnSeekBarChangeListener(this);
            mInWhiteSeekBar.setMax(128);
            mInWhiteSeekBar.setProgress(128);
            mOutWhiteSeekBar = (SeekBar) rootView.findViewById(R.id.outWhite);
            mOutWhiteSeekBar.setOnSeekBarChangeListener(this);
            mOutWhiteSeekBar.setMax(128);
            mOutWhiteSeekBar.setProgress(128);

            mGammaSeekBar = (SeekBar) rootView.findViewById(R.id.inGamma);
            mGammaSeekBar.setOnSeekBarChangeListener(this);
            mGammaSeekBar.setMax(150);
            mGammaSeekBar.setProgress(100);

            mSaturationSeekBar = (SeekBar) rootView.findViewById(R.id.inSaturation);
            mSaturationSeekBar.setOnSeekBarChangeListener(this);
            mSaturationSeekBar.setProgress(50);

            mBenchmarkResult = (TextView) rootView.findViewById(R.id.benchmarkText);
            mBenchmarkResult.setText("Result: not run");

            mRS = RenderScript.create(getActivity());
            mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mScript = new ScriptC_main(mRS, getResources(), R.raw.main);
            mScript.set_gamma(mGamma);

            mScript.set_color(255.0f);

            int[] B = new int[2];
            int[] A = new int[2];

            A[0] = 0;
            A[1] = 255;

            B[0] = 0;
            B[1] = 255;

            Allocation a = Allocation.createSized(mRS, Element.F32(mRS), A.length);
            a.copyFrom(A);
            Allocation b = Allocation.createSized(mRS, Element.F32(mRS), B.length);
            b.copyFrom(B);

            mScript.bind_a(a);
            mScript.bind_b(b);

            setSaturation();
            setLevels();
            filter();

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
