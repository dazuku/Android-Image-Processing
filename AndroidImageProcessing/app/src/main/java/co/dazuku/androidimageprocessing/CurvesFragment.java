package co.dazuku.androidimageprocessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Use the {@link CurvesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurvesFragment extends Fragment {
    ImageView imageView;
    ImageView originalImageView;

    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    private RenderScript mRS;
    private ScriptC_curves mScript;
    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curves, container, false);

        imageView = (ImageView) view.findViewById(R.id.curvesImage);
        originalImageView = (ImageView) view.findViewById(R.id.originalImage);

        mBitmapIn = loadBitmap(R.drawable.city);
        mBitmapOut = loadBitmap(R.drawable.city);

        imageView.setImageBitmap(mBitmapOut);
        originalImageView.setImageBitmap(mBitmapIn);

        mRS = RenderScript.create(getActivity());
        mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mScript = new ScriptC_curves(mRS, getResources(), R.raw.curves);

        float[] Rx = new float[5];
        float[] Ry = new float[5];

        Rx[0] = 0;
        Ry[0] = 0;

        Rx[1] = 65;
        Ry[1] = 100;

        Rx[2] = 130;
        Ry[2] = 130;

        Rx[3] = 195;
        Ry[3] = 155;

        Rx[4] = 255;
        Ry[4] = 255;

        float[] Gx = new float[5];
        float[] Gy = new float[5];

        Gx[0] = 0;
        Gy[0] = 0;

        Gx[1] = 65;
        Gy[1] = 25;

        Gx[2] = 130;
        Gy[2] = 130;

        Gx[3] = 195;
        Gy[3] = 235;

        Gx[4] = 255;
        Gy[4] = 255;

        float[] Bx = new float[2];
        float[] By = new float[2];

        Bx[0] = 100;
        By[0] = 100;

        Bx[1] = 255;
        By[1] = 255;

        float[] Cx = new float[2];
        float[] Cy = new float[2];

        Cx[0] = 0;
        Cy[0] = 0;

        Cx[1] = 200;
        Cy[1] = 255;


        Allocation a = Allocation.createSized(mRS, Element.F32(mRS), Rx.length);
        a.copyFrom(Rx);
        Allocation b = Allocation.createSized(mRS, Element.F32(mRS), Ry.length);
        b.copyFrom(Ry);

        Allocation aGx = Allocation.createSized(mRS, Element.F32(mRS), Gx.length);
        aGx.copyFrom(Gx);
        Allocation aGy = Allocation.createSized(mRS, Element.F32(mRS), Gy.length);
        aGy.copyFrom(Gy);

        Allocation aBx = Allocation.createSized(mRS, Element.F32(mRS), Bx.length);
        aBx.copyFrom(Bx);
        Allocation aBy = Allocation.createSized(mRS, Element.F32(mRS), By.length);
        aBy.copyFrom(By);

        Allocation aCx = Allocation.createSized(mRS, Element.F32(mRS), Cx.length);
        aCx.copyFrom(Cx);
        Allocation aCy = Allocation.createSized(mRS, Element.F32(mRS), Cy.length);
        aCy.copyFrom(Cy);

        mScript.bind_Rx(a);
        mScript.bind_Ry(b);

        mScript.bind_Gx(aGx);
        mScript.bind_Gy(aGy);

        mScript.bind_Bx(aBx);
        mScript.bind_By(aBy);

        mScript.bind_Cx(aCx);
        mScript.bind_Cy(aCy);

        mScript.set_redSize(Rx.length);
        mScript.set_greenSize(Gx.length);
        mScript.set_blueSize(Bx.length);
        mScript.set_composeSize(Cx.length);

        mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
        mOutPixelsAllocation.copyTo(mBitmapOut);

        // Inflate the layout for this fragment
        return view;
    }

}
