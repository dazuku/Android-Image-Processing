#pragma version(1)

// Tell which java package name the reflected files should belong to
#pragma rs java_package_name(co.dazuku.androidimageprocessing)

int *R;
int *G;
int *B;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float3 pixel = convert_float4(in[0]).rgb;

    R[(int) pixel[0]]++;
    G[(int) pixel[1]]++;
    B[(int) pixel[2]]++;

    out->xyz = convert_uchar3(pixel);
}