#pragma version(1)

// Tell which java package name the reflected files should belong to
#pragma rs java_package_name(co.dazuku.androidimageprocessing)

float inBlack;
float outBlack;
float inWMinInB;
float outWMinOutB;
float overInWMinInB;
float gamma;
rs_matrix3x3 colorMat;

float color;

float3 *a;
float3 *b;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float3 pixel = convert_float4(in[0]).rgb;
    if(pixel[0] + pixel[1] + pixel[2] > 510.0f) {
        pixel[0] = pixel[2] + pixel[1]/2;
        pixel[1] = pixel[2] + pixel[0]/2;
        pixel[2] = pixel[2];
    } else if(pixel[0] + pixel[1] + pixel[2] > 255.0f){
        pixel[0] = pixel[0];
        pixel[1] = pixel[0] + pixel[1] / 2;
        pixel[2] = pixel[0] + pixel[2] / 2;
    } else {
        pixel[0] = pixel[1] + pixel[0] / 2;
        pixel[1] = pixel[1];
        pixel[2] = pixel[1] + pixel[2] / 2;
    }


    pixel = clamp(pixel, 0.f, 255.f);
    out->xyz = convert_uchar3(pixel);
}