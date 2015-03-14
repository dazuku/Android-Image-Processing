package co.dazuku.androidimageprocessing;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import co.dazuku.androidimageprocessing.utils.CurveComposition;
import co.dazuku.androidimageprocessing.utils.KeyPoint;


/**
 * Use the {@link CurvesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurvesFragment extends Fragment {
    ImageView imageView;
    ImageView originalImageView;

    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    ImageView redOriginalView;
    ImageView greenOriginalView;
    ImageView blueOriginalView;

    ImageView redModifyView;
    ImageView greenModifyView;
    ImageView blueModifyView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CurvesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurvesFragment newInstance() {
        CurvesFragment fragment = new CurvesFragment();
        return fragment;
    }

    public CurvesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void applyCurve() {
        CurveComposition composition = new CurveComposition();

        composition.addRedKeyPoint(new KeyPoint(0, 0));
        composition.addRedKeyPoint(new KeyPoint(65, 100));
        composition.addRedKeyPoint(new KeyPoint(130, 130));
        composition.addRedKeyPoint(new KeyPoint(195, 155));
        composition.addRedKeyPoint(new KeyPoint(255, 255));

        composition.addGreenKeyPoint(new KeyPoint(0, 0));
        composition.addGreenKeyPoint(new KeyPoint(65, 25));
        composition.addGreenKeyPoint(new KeyPoint(130, 130));
        composition.addGreenKeyPoint(new KeyPoint(195, 235));
        composition.addGreenKeyPoint(new KeyPoint(255, 255));

        composition.addBlueKeyPoint(new KeyPoint(100, 100));
        composition.addBlueKeyPoint(new KeyPoint(255, 255));

        composition.addCompositionKeyPoint(new KeyPoint(0, 0));
        composition.addCompositionKeyPoint(new KeyPoint(200, 255));

        ImageProcessing.applyCurvesToBitmap(getActivity(), mBitmapIn, mBitmapOut, composition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_curves, container, false);

        imageView = (ImageView) view.findViewById(R.id.curvesImage);
        originalImageView = (ImageView) view.findViewById(R.id.originalImage);

        redOriginalView = (ImageView) view.findViewById(R.id.imageView);
        greenOriginalView = (ImageView) view.findViewById(R.id.imageView2);
        blueOriginalView = (ImageView) view.findViewById(R.id.imageView3);

        redModifyView = (ImageView) view.findViewById(R.id.imageView4);
        greenModifyView = (ImageView) view.findViewById(R.id.imageView5);
        blueModifyView = (ImageView) view.findViewById(R.id.imageView6);

        mBitmapIn = loadBitmap(R.drawable.city);
        mBitmapOut = loadBitmap(R.drawable.city);

        imageView.setImageBitmap(mBitmapOut);
        originalImageView.setImageBitmap(mBitmapIn);

        Bitmap[] histogram = ImageProcessing.getHistogramsBitmap(getActivity(), mBitmapIn);

        redOriginalView.setImageBitmap(histogram[0]);
        greenOriginalView.setImageBitmap(histogram[1]);
        blueOriginalView.setImageBitmap(histogram[2]);


        new ApplyCurveAsync().execute();

        return view;
    }

    private class ApplyCurveAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        Bitmap[] histogram;

        @Override
        protected Void doInBackground(Void... params) {
            applyCurve();

            histogram = ImageProcessing.getHistogramsBitmap(getActivity(), mBitmapOut);


            //ImageProcessing.getHistogram(getActivity(), mBitmapOut);
            //ImageProcessing.applyGaussianBlur(getActivity(), mBitmapOut, mBitmapOut, 25.f);
            //ImageProcessing.getHistogram(getActivity(), mBitmapOut);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            imageView.setImageBitmap(mBitmapOut);

            redModifyView.setImageBitmap(histogram[0]);
            greenModifyView.setImageBitmap(histogram[1]);
            blueModifyView.setImageBitmap(histogram[2]);
        }
    }

}
