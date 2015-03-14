package co.dazuku.androidimageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

import java.util.List;

import co.dazuku.androidimageprocessing.utils.CurveComposition;
import co.dazuku.androidimageprocessing.utils.KeyPoint;

/**
 * Created by dazuku on 3/13/15.
 */
public class ImageProcessing {

    public static Bitmap applyCurvesToBitmap(Context context, Bitmap bitmap, Bitmap out, CurveComposition composition) {

        RenderScript mRS = RenderScript.create(context);
        Allocation mInPixelsAllocation = Allocation.createFromBitmap(mRS, bitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation mOutPixelsAllocation = Allocation.createFromBitmap(mRS, out,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        ScriptC_curves mScript = new ScriptC_curves(mRS, context.getResources(), R.raw.curves);

        float Rx[], Ry[], Gx[], Gy[], Bx[], By[], Cx[], Cy[];
        Allocation allocRx, allocRy, allocGx, allocGy, allocBx, allocBy, allocCx, allocCy;

        if(composition.getRed().size() > 0) {
            Rx = new float[composition.getRed().size()];
            Ry = new float[composition.getRed().size()];

            List<KeyPoint> keyPointList = composition.getRed();
            for(int i = 0; i < keyPointList.size(); i++) {
                Rx[i] = keyPointList.get(i).getX();
                Ry[i] = keyPointList.get(i).getY();
            }

            allocRx = Allocation.createSized(mRS, Element.F32(mRS), Rx.length);
            allocRy = Allocation.createSized(mRS, Element.F32(mRS), Ry.length);

            allocRx.copyFrom(Rx);
            allocRy.copyFrom(Ry);

            mScript.bind_Rx(allocRx);
            mScript.bind_Ry(allocRy);

            mScript.set_redSize(Rx.length);
        }

        if(composition.getGreen().size() > 0) {
            Gx = new float[composition.getGreen().size()];
            Gy = new float[composition.getGreen().size()];

            List<KeyPoint> keyPointList = composition.getGreen();
            for(int i = 0; i < keyPointList.size(); i++) {
                Gx[i] = keyPointList.get(i).getX();
                Gy[i] = keyPointList.get(i).getY();
            }

            allocGx = Allocation.createSized(mRS, Element.F32(mRS), Gx.length);
            allocGy = Allocation.createSized(mRS, Element.F32(mRS), Gy.length);

            allocGx.copyFrom(Gx);
            allocGy.copyFrom(Gy);

            mScript.bind_Gx(allocGx);
            mScript.bind_Gy(allocGy);

            mScript.set_greenSize(Gx.length);
        }

        if(composition.getBlue().size() > 0) {
            Bx = new float[composition.getBlue().size()];
            By = new float[composition.getBlue().size()];

            List<KeyPoint> keyPointList = composition.getBlue();
            for(int i = 0; i < keyPointList.size(); i++) {
                Bx[i] = keyPointList.get(i).getX();
                By[i] = keyPointList.get(i).getY();
            }

            allocBx = Allocation.createSized(mRS, Element.F32(mRS), Bx.length);
            allocBy = Allocation.createSized(mRS, Element.F32(mRS), By.length);

            allocBx.copyFrom(Bx);
            allocBy.copyFrom(By);

            mScript.bind_Bx(allocBx);
            mScript.bind_By(allocBy);

            mScript.set_blueSize(Bx.length);
        }

        if(composition.getComposition().size() > 0) {
            Cx = new float[composition.getComposition().size()];
            Cy = new float[composition.getComposition().size()];

            List<KeyPoint> keyPointList = composition.getComposition();
            for(int i = 0; i < keyPointList.size(); i++) {
                Cx[i] = keyPointList.get(i).getX();
                Cy[i] = keyPointList.get(i).getY();
            }

            allocCx = Allocation.createSized(mRS, Element.F32(mRS), Cx.length);
            allocCy = Allocation.createSized(mRS, Element.F32(mRS), Cy.length);

            allocCx.copyFrom(Cx);
            allocCy.copyFrom(Cy);

            mScript.bind_Cx(allocCx);
            mScript.bind_Cy(allocCy);

            mScript.set_composeSize(Cx.length);
        }

        mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
        mOutPixelsAllocation.copyTo(out);


        return out;
    }
}
