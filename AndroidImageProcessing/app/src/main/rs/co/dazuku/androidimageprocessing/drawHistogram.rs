#pragma version(1)

// Tell which java package name the reflected files should belong to
#pragma rs java_package_name(co.dazuku.androidimageprocessing)

float3 hc;
float3 bg;

float *A;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float3 pixel = convert_float4(in[0]).rgb;

    if((255 - A[x]) <= y) {
        pixel[0] = hc[0];
        pixel[1] = hc[1];
        pixel[2] = hc[2];
    } else {
        pixel[0] = bg[0];
        pixel[1] = bg[1];
        pixel[2] = bg[2];
    }

    pixel = clamp(pixel, 0.f, 255.f);

    out->xyz = convert_uchar3(pixel);
}