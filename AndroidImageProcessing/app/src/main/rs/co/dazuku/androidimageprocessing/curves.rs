#pragma version(1)

// Tell which java package name the reflected files should belong to
#pragma rs java_package_name(co.dazuku.androidimageprocessing)

// Vector in x to Red compose
float *Rx;
// Vector in y to Red compose
float *Ry;
// Size of array in Red
float redSize;

// Vector in x to Green compose
float *Gx;
// Vector in y to Green compose
float *Gy;
// Size of array in Green
float greenSize;

// Vector in x to Blue compose
float *Bx;
// Vector in y to Blue compose
float *By;
// Size of array in Blue
float blueSize;

// Vector in x to Compose
float *Cx;
// Vector in y to Compose
float *Cy;
// Size of array in Compose
float composeSize;


void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float3 pixel = convert_float4(in[0]).rgb;

    int rs, i, next;
    // process to Red
    if(redSize > 0) {
        rs = redSize - 1;
        for(i = 0; i < rs; i++) {
            next = i + 1;
            if(pixel[0] >= Rx[i] && pixel[0] < Rx[next]) {
                pixel[0] = (Ry[next] - Ry[i]) * (pixel[0] - Rx[i]) / (Rx[next] - Rx[i]) + Ry[i];
                break;
            }
        }
    }

    if(greenSize > 0) {
        // process to Green
        rs = greenSize - 1;
        for(i = 0; i < rs; i++) {
            next = i + 1;
            if(pixel[1] >= Gx[i] && pixel[1] < Gx[next]) {
                pixel[1] = (Gy[next] - Gy[i]) * (pixel[1] - Gx[i]) / (Gx[next] - Gx[i]) + Gy[i];
                break;
            }
        }
    }

    if(blueSize > 0) {
        // process to Blue
        rs = blueSize - 1;
        for(i = 0; i < rs; i++) {
            next = i + 1;
            if(pixel[2] >= Bx[i] && pixel[2] < Bx[next]) {
                pixel[2] = (By[next] - By[i]) * (pixel[2] - Bx[i]) / (Bx[next] - Bx[i]) + By[i];
                break;
            }
        }
    }

    if(composeSize > 0) {
    // process to Compose Red
        rs = composeSize - 1;
        bool endRed = true, endBlue = true, endGreen = true;
        for(i = 0; i < rs; i++) {
            next = i + 1;
            if(endRed) {
                if(pixel[0] >= Cx[i] && pixel[0] < Cx[next]) {
                    pixel[0] = (Cy[next] - Cy[i]) * (pixel[0] - Cx[i]) / (Cx[next] - Cx[i]) + Cy[i];
                    endRed = false;
                }
            }

            if(endGreen) {
                if(pixel[1] >= Cx[i] && pixel[1] < Cx[next]) {
                    pixel[1] = (Cy[next] - Cy[i]) * (pixel[1] - Cx[i]) / (Cx[next] - Cx[i]) + Cy[i];
                    endGreen = false;
                }
            }

            if(endBlue) {
                if(pixel[2] >= Cx[i] && pixel[2] < Cx[next]) {
                    pixel[2] = (Cy[next] - Cy[i]) * (pixel[2] - Cx[i]) / (Cx[next] - Cx[i]) + Cy[i];
                    endBlue = false;
                }
            }

            if(!endBlue && !endRed && !endGreen) {
                break;
            }
        }
    }

    pixel = clamp(pixel, 0.f, 255.f);
    out->xyz = convert_uchar3(pixel);
}