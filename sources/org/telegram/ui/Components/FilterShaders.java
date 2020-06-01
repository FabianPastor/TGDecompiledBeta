package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;

public class FilterShaders {
    private int blurHeightHandle;
    private int blurInputTexCoordHandle;
    private int blurPositionHandle;
    private int blurShaderProgram;
    private int blurSourceImageHandle;
    private int blurWidthHandle;
    private ByteBuffer calcBuffer;
    private ByteBuffer cdtBuffer;
    private int contrastHandle;
    private int[] curveTextures = new int[1];
    private int curvesImageHandle;
    private FilterShadersDelegate delegate;
    private int[] enhanceFrameBuffer = new int[1];
    private int enhanceInputImageTexture2Handle;
    private int enhanceInputTexCoordHandle;
    private int enhanceIntensityHandle;
    private int enhancePositionHandle;
    private int enhanceShaderProgram;
    private int enhanceSourceImageHandle;
    private int[] enhanceTextures = new int[2];
    private int exposureHandle;
    private int fadeAmountHandle;
    private int grainHandle;
    private int heightHandle;
    private int highlightsHandle;
    private int highlightsTintColorHandle;
    private int highlightsTintIntensityHandle;
    private ByteBuffer hsvBuffer;
    private boolean hsvGenerated;
    private int inputTexCoordHandle;
    private boolean isVideo;
    private int linearBlurAngleHandle;
    private int linearBlurAspectRatioHandle;
    private int linearBlurExcludeBlurSizeHandle;
    private int linearBlurExcludePointHandle;
    private int linearBlurExcludeSizeHandle;
    private int linearBlurInputTexCoordHandle;
    private int linearBlurPositionHandle;
    private int linearBlurShaderProgram;
    private int linearBlurSourceImage2Handle;
    private int linearBlurSourceImageHandle;
    private boolean needUpdateBlurTexture = true;
    private int positionHandle;
    private int radialBlurAspectRatioHandle;
    private int radialBlurExcludeBlurSizeHandle;
    private int radialBlurExcludePointHandle;
    private int radialBlurExcludeSizeHandle;
    private int radialBlurInputTexCoordHandle;
    private int radialBlurPositionHandle;
    private int radialBlurShaderProgram;
    private int radialBlurSourceImage2Handle;
    private int radialBlurSourceImageHandle;
    private int renderBufferHeight;
    private int renderBufferWidth;
    private int[] renderFrameBuffer;
    private int[] renderTexture = new int[3];
    private int rgbToHsvInputTexCoordHandle;
    private int rgbToHsvMatrixHandle;
    private int rgbToHsvPositionHandle;
    private int rgbToHsvShaderProgram;
    private int rgbToHsvSourceImageHandle;
    private int saturationHandle;
    private int shadowsHandle;
    private int shadowsTintColorHandle;
    private int shadowsTintIntensityHandle;
    private int sharpenHandle;
    private int sharpenHeightHandle;
    private int sharpenInputTexCoordHandle;
    private int sharpenPositionHandle;
    private int sharpenShaderProgram;
    private int sharpenSourceImageHandle;
    private int sharpenWidthHandle;
    private int skipToneHandle;
    private int sourceImageHandle;
    private FloatBuffer textureBuffer;
    private int toolsShaderProgram;
    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexInvertBuffer;
    private float[] videoMatrix;
    private int videoTexture;
    private int vignetteHandle;
    private int warmthHandle;
    private int widthHandle;

    public interface FilterShadersDelegate {
        ByteBuffer fillAndGetCurveBuffer();

        float getBlurAngle();

        float getBlurExcludeBlurSize();

        Point getBlurExcludePoint();

        float getBlurExcludeSize();

        int getBlurType();

        float getContrastValue();

        float getEnhanceValue();

        float getExposureValue();

        float getFadeValue();

        float getGrainValue();

        float getHighlightsValue();

        float getSaturationValue();

        float getShadowsValue();

        float getSharpenValue();

        int getTintHighlightsColor();

        float getTintHighlightsIntensityValue();

        int getTintShadowsColor();

        float getTintShadowsIntensityValue();

        float getVignetteValue();

        float getWarmthValue();

        boolean shouldDrawCurvesPass();

        boolean shouldShowOriginal();
    }

    public FilterShaders(boolean z) {
        this.isVideo = z;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        this.vertexBuffer = asFloatBuffer;
        asFloatBuffer.put(new float[]{-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f});
        this.vertexBuffer.position(0);
        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
        allocateDirect2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
        this.vertexInvertBuffer = asFloatBuffer2;
        asFloatBuffer2.put(new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f});
        this.vertexInvertBuffer.position(0);
        ByteBuffer allocateDirect3 = ByteBuffer.allocateDirect(32);
        allocateDirect3.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer3 = allocateDirect3.asFloatBuffer();
        this.textureBuffer = asFloatBuffer3;
        asFloatBuffer3.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.textureBuffer.position(0);
    }

    public void setDelegate(FilterShadersDelegate filterShadersDelegate) {
        this.delegate = filterShadersDelegate;
    }

    public boolean create() {
        int i;
        int i2;
        GLES20.glGenTextures(1, this.curveTextures, 0);
        GLES20.glGenTextures(2, this.enhanceTextures, 0);
        GLES20.glGenFramebuffers(1, this.enhanceFrameBuffer, 0);
        GLES20.glBindTexture(3553, this.enhanceTextures[1]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glBindTexture(3553, this.curveTextures[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        int loadShader = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
        int loadShader2 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}");
        if (!(loadShader == 0 || loadShader2 == 0)) {
            int glCreateProgram = GLES20.glCreateProgram();
            this.toolsShaderProgram = glCreateProgram;
            GLES20.glAttachShader(glCreateProgram, loadShader);
            GLES20.glAttachShader(this.toolsShaderProgram, loadShader2);
            GLES20.glBindAttribLocation(this.toolsShaderProgram, 0, "position");
            GLES20.glBindAttribLocation(this.toolsShaderProgram, 1, "inputTexCoord");
            GLES20.glLinkProgram(this.toolsShaderProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(this.toolsShaderProgram, 35714, iArr, 0);
            if (iArr[0] == 0) {
                GLES20.glDeleteProgram(this.toolsShaderProgram);
                this.toolsShaderProgram = 0;
            } else {
                this.positionHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "position");
                this.inputTexCoordHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "inputTexCoord");
                this.sourceImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "sourceImage");
                this.shadowsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadows");
                this.highlightsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlights");
                this.exposureHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "exposure");
                this.contrastHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "contrast");
                this.saturationHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "saturation");
                this.warmthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "warmth");
                this.vignetteHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "vignette");
                this.grainHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "grain");
                this.widthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "width");
                this.heightHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "height");
                this.curvesImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "curvesImage");
                this.skipToneHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "skipTone");
                this.fadeAmountHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "fadeAmount");
                this.shadowsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintIntensity");
                this.highlightsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintIntensity");
                this.shadowsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintColor");
                this.highlightsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintColor");
            }
            int loadShader3 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}");
            int loadShader4 = loadShader(35632, "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}");
            if (!(loadShader3 == 0 || loadShader4 == 0)) {
                int glCreateProgram2 = GLES20.glCreateProgram();
                this.sharpenShaderProgram = glCreateProgram2;
                GLES20.glAttachShader(glCreateProgram2, loadShader3);
                GLES20.glAttachShader(this.sharpenShaderProgram, loadShader4);
                GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
                GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.sharpenShaderProgram);
                int[] iArr2 = new int[1];
                GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, iArr2, 0);
                if (iArr2[0] == 0) {
                    GLES20.glDeleteProgram(this.sharpenShaderProgram);
                    this.sharpenShaderProgram = 0;
                } else {
                    this.sharpenPositionHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "position");
                    this.sharpenInputTexCoordHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "inputTexCoord");
                    this.sharpenSourceImageHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sourceImage");
                    this.sharpenWidthHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputWidth");
                    this.sharpenHeightHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputHeight");
                    this.sharpenHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sharpen");
                }
                int loadShader5 = loadShader(35633, "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}");
                int loadShader6 = loadShader(35632, "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}");
                if (!(loadShader5 == 0 || loadShader6 == 0)) {
                    int glCreateProgram3 = GLES20.glCreateProgram();
                    this.blurShaderProgram = glCreateProgram3;
                    GLES20.glAttachShader(glCreateProgram3, loadShader5);
                    GLES20.glAttachShader(this.blurShaderProgram, loadShader6);
                    GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
                    GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
                    GLES20.glLinkProgram(this.blurShaderProgram);
                    int[] iArr3 = new int[1];
                    GLES20.glGetProgramiv(this.blurShaderProgram, 35714, iArr3, 0);
                    if (iArr3[0] == 0) {
                        GLES20.glDeleteProgram(this.blurShaderProgram);
                        this.blurShaderProgram = 0;
                    } else {
                        this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
                        this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
                        this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
                        this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
                        this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
                    }
                    int loadShader7 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                    int loadShader8 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
                    if (!(loadShader7 == 0 || loadShader8 == 0)) {
                        int glCreateProgram4 = GLES20.glCreateProgram();
                        this.linearBlurShaderProgram = glCreateProgram4;
                        GLES20.glAttachShader(glCreateProgram4, loadShader7);
                        GLES20.glAttachShader(this.linearBlurShaderProgram, loadShader8);
                        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.linearBlurShaderProgram);
                        int[] iArr4 = new int[1];
                        GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, iArr4, 0);
                        if (iArr4[0] == 0) {
                            GLES20.glDeleteProgram(this.linearBlurShaderProgram);
                            this.linearBlurShaderProgram = 0;
                        } else {
                            this.linearBlurPositionHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "position");
                            this.linearBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "inputTexCoord");
                            this.linearBlurSourceImageHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "sourceImage");
                            this.linearBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "inputImageTexture2");
                            this.linearBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeSize");
                            this.linearBlurExcludePointHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludePoint");
                            this.linearBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeBlurSize");
                            this.linearBlurAngleHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "angle");
                            this.linearBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "aspectRatio");
                        }
                        int loadShader9 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader10 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
                        if (!(loadShader9 == 0 || loadShader10 == 0)) {
                            int glCreateProgram5 = GLES20.glCreateProgram();
                            this.radialBlurShaderProgram = glCreateProgram5;
                            GLES20.glAttachShader(glCreateProgram5, loadShader9);
                            GLES20.glAttachShader(this.radialBlurShaderProgram, loadShader10);
                            GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.radialBlurShaderProgram);
                            int[] iArr5 = new int[1];
                            GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, iArr5, 0);
                            if (iArr5[0] == 0) {
                                GLES20.glDeleteProgram(this.radialBlurShaderProgram);
                                this.radialBlurShaderProgram = 0;
                            } else {
                                this.radialBlurPositionHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "position");
                                this.radialBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "inputTexCoord");
                                this.radialBlurSourceImageHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "sourceImage");
                                this.radialBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "inputImageTexture2");
                                this.radialBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeSize");
                                this.radialBlurExcludePointHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludePoint");
                                this.radialBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeBlurSize");
                                this.radialBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "aspectRatio");
                            }
                            if (this.isVideo) {
                                i2 = loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision highp float;varying vec2 texCoord;uniform samplerExternalOES sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}");
                                i = loadShader(35633, "attribute vec4 position;uniform mat4 videoMatrix;attribute vec4 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = vec2(videoMatrix * inputTexCoord).xy;}");
                            } else {
                                i2 = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}");
                                i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                            }
                            if (!(i == 0 || i2 == 0)) {
                                int glCreateProgram6 = GLES20.glCreateProgram();
                                this.rgbToHsvShaderProgram = glCreateProgram6;
                                GLES20.glAttachShader(glCreateProgram6, i);
                                GLES20.glAttachShader(this.rgbToHsvShaderProgram, i2);
                                GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 0, "position");
                                GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 1, "inputTexCoord");
                                GLES20.glLinkProgram(this.rgbToHsvShaderProgram);
                                int[] iArr6 = new int[1];
                                GLES20.glGetProgramiv(this.rgbToHsvShaderProgram, 35714, iArr6, 0);
                                if (iArr6[0] == 0) {
                                    GLES20.glDeleteProgram(this.rgbToHsvShaderProgram);
                                    this.rgbToHsvShaderProgram = 0;
                                } else {
                                    this.rgbToHsvPositionHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "position");
                                    this.rgbToHsvInputTexCoordHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "inputTexCoord");
                                    this.rgbToHsvSourceImageHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "sourceImage");
                                    if (this.isVideo) {
                                        this.rgbToHsvMatrixHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "videoMatrix");
                                    }
                                }
                                int loadShader11 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                                int loadShader12 = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.NUM, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c2 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c3 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c4 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}");
                                if (!(loadShader11 == 0 || loadShader12 == 0)) {
                                    int glCreateProgram7 = GLES20.glCreateProgram();
                                    this.enhanceShaderProgram = glCreateProgram7;
                                    GLES20.glAttachShader(glCreateProgram7, loadShader11);
                                    GLES20.glAttachShader(this.enhanceShaderProgram, loadShader12);
                                    GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
                                    GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
                                    GLES20.glLinkProgram(this.enhanceShaderProgram);
                                    int[] iArr7 = new int[1];
                                    GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, iArr7, 0);
                                    if (iArr7[0] == 0) {
                                        GLES20.glDeleteProgram(this.enhanceShaderProgram);
                                        this.enhanceShaderProgram = 0;
                                    } else {
                                        this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
                                        this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
                                        this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
                                        this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
                                        this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setRenderData(Bitmap bitmap, int i, int i2, int i3, int i4) {
        loadTexture(bitmap, i, i3, i4);
        this.videoTexture = i2;
        GLES20.glBindTexture(3553, this.enhanceTextures[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, this.hsvBuffer);
    }

    public static int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(glCreateShader));
        }
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }

    public void drawEnhancePass() {
        boolean z;
        if (this.isVideo) {
            z = true;
        } else {
            z = !this.hsvGenerated;
        }
        if (z) {
            GLES20.glUseProgram(this.rgbToHsvShaderProgram);
            GLES20.glActiveTexture(33984);
            if (this.isVideo) {
                GLES20.glBindTexture(36197, this.videoTexture);
                GLES20.glUniformMatrix4fv(this.rgbToHsvMatrixHandle, 1, false, this.videoMatrix, 0);
            } else {
                GLES20.glBindTexture(3553, this.renderTexture[1]);
            }
            GLES20.glUniform1i(this.rgbToHsvSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.rgbToHsvInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.rgbToHsvInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.rgbToHsvPositionHandle);
            GLES20.glVertexAttribPointer(this.rgbToHsvPositionHandle, 2, 5126, false, 8, this.isVideo ? this.vertexInvertBuffer : this.vertexBuffer);
            GLES20.glBindFramebuffer(36160, this.enhanceFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.enhanceTextures[0], 0);
            GLES20.glDrawArrays(5, 0, 4);
        }
        if (!this.hsvGenerated) {
            int i = this.renderBufferWidth * this.renderBufferHeight * 4;
            ByteBuffer byteBuffer = this.hsvBuffer;
            if (byteBuffer == null || i > byteBuffer.capacity()) {
                this.hsvBuffer = ByteBuffer.allocateDirect(i);
            }
            if (this.cdtBuffer == null) {
                this.cdtBuffer = ByteBuffer.allocateDirect(16384);
            }
            if (this.calcBuffer == null) {
                this.calcBuffer = ByteBuffer.allocateDirect(32896);
            }
            GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, this.hsvBuffer);
            Utilities.calcCDT(this.hsvBuffer, this.renderBufferWidth, this.renderBufferHeight, this.cdtBuffer, this.calcBuffer);
            GLES20.glBindTexture(3553, this.enhanceTextures[1]);
            GLES20.glTexImage2D(3553, 0, 6408, 256, 16, 0, 6408, 5121, this.cdtBuffer);
            if (!this.isVideo) {
                this.hsvBuffer = null;
                this.cdtBuffer = null;
                this.calcBuffer = null;
            }
            this.hsvGenerated = true;
        }
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
        GLES20.glUseProgram(this.enhanceShaderProgram);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.enhanceTextures[0]);
        GLES20.glUniform1i(this.enhanceSourceImageHandle, 0);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.enhanceTextures[1]);
        GLES20.glUniform1i(this.enhanceInputImageTexture2Handle, 1);
        FilterShadersDelegate filterShadersDelegate = this.delegate;
        if (filterShadersDelegate == null || filterShadersDelegate.shouldShowOriginal()) {
            GLES20.glUniform1f(this.enhanceIntensityHandle, 0.0f);
        } else {
            GLES20.glUniform1f(this.enhanceIntensityHandle, this.delegate.getEnhanceValue());
        }
        GLES20.glEnableVertexAttribArray(this.enhanceInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.enhanceInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.enhancePositionHandle);
        GLES20.glVertexAttribPointer(this.enhancePositionHandle, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glDrawArrays(5, 0, 4);
    }

    public void drawSharpenPass() {
        if (!this.isVideo) {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glUseProgram(this.sharpenShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1i(this.sharpenSourceImageHandle, 0);
            FilterShadersDelegate filterShadersDelegate = this.delegate;
            if (filterShadersDelegate == null || filterShadersDelegate.shouldShowOriginal()) {
                GLES20.glUniform1f(this.sharpenHandle, 0.0f);
            } else {
                GLES20.glUniform1f(this.sharpenHandle, this.delegate.getSharpenValue());
            }
            GLES20.glUniform1f(this.sharpenWidthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.sharpenHeightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.sharpenInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.sharpenInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.sharpenPositionHandle);
            GLES20.glVertexAttribPointer(this.sharpenPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, 4);
        }
    }

    public void drawCustomParamsPass() {
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[!this.isVideo]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[!this.isVideo], 0);
        GLES20.glUseProgram(this.toolsShaderProgram);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[this.isVideo]);
        GLES20.glUniform1i(this.sourceImageHandle, 0);
        FilterShadersDelegate filterShadersDelegate = this.delegate;
        float f = 1.0f;
        if (filterShadersDelegate == null || filterShadersDelegate.shouldShowOriginal()) {
            GLES20.glUniform1f(this.shadowsHandle, 1.0f);
            GLES20.glUniform1f(this.highlightsHandle, 1.0f);
            GLES20.glUniform1f(this.exposureHandle, 0.0f);
            GLES20.glUniform1f(this.contrastHandle, 1.0f);
            GLES20.glUniform1f(this.saturationHandle, 1.0f);
            GLES20.glUniform1f(this.warmthHandle, 0.0f);
            GLES20.glUniform1f(this.vignetteHandle, 0.0f);
            GLES20.glUniform1f(this.grainHandle, 0.0f);
            GLES20.glUniform1f(this.fadeAmountHandle, 0.0f);
            GLES20.glUniform3f(this.highlightsTintColorHandle, 0.0f, 0.0f, 0.0f);
            GLES20.glUniform1f(this.highlightsTintIntensityHandle, 0.0f);
            GLES20.glUniform3f(this.shadowsTintColorHandle, 0.0f, 0.0f, 0.0f);
            GLES20.glUniform1f(this.shadowsTintIntensityHandle, 0.0f);
            GLES20.glUniform1f(this.skipToneHandle, 1.0f);
        } else {
            GLES20.glUniform1f(this.shadowsHandle, this.delegate.getShadowsValue());
            GLES20.glUniform1f(this.highlightsHandle, this.delegate.getHighlightsValue());
            GLES20.glUniform1f(this.exposureHandle, this.delegate.getExposureValue());
            GLES20.glUniform1f(this.contrastHandle, this.delegate.getContrastValue());
            GLES20.glUniform1f(this.saturationHandle, this.delegate.getSaturationValue());
            GLES20.glUniform1f(this.warmthHandle, this.delegate.getWarmthValue());
            GLES20.glUniform1f(this.vignetteHandle, this.delegate.getVignetteValue());
            GLES20.glUniform1f(this.grainHandle, this.delegate.getGrainValue());
            GLES20.glUniform1f(this.fadeAmountHandle, this.delegate.getFadeValue());
            int tintHighlightsColor = this.delegate.getTintHighlightsColor();
            int tintShadowsColor = this.delegate.getTintShadowsColor();
            GLES20.glUniform3f(this.highlightsTintColorHandle, ((float) ((tintHighlightsColor >> 16) & 255)) / 255.0f, ((float) ((tintHighlightsColor >> 8) & 255)) / 255.0f, ((float) (tintHighlightsColor & 255)) / 255.0f);
            GLES20.glUniform1f(this.highlightsTintIntensityHandle, this.delegate.getTintHighlightsIntensityValue());
            GLES20.glUniform3f(this.shadowsTintColorHandle, ((float) ((tintShadowsColor >> 16) & 255)) / 255.0f, ((float) ((tintShadowsColor >> 8) & 255)) / 255.0f, ((float) (tintShadowsColor & 255)) / 255.0f);
            GLES20.glUniform1f(this.shadowsTintIntensityHandle, this.delegate.getTintShadowsIntensityValue());
            boolean shouldDrawCurvesPass = this.delegate.shouldDrawCurvesPass();
            int i = this.skipToneHandle;
            if (shouldDrawCurvesPass) {
                f = 0.0f;
            }
            GLES20.glUniform1f(i, f);
            if (shouldDrawCurvesPass) {
                ByteBuffer fillAndGetCurveBuffer = this.delegate.fillAndGetCurveBuffer();
                GLES20.glActiveTexture(33985);
                GLES20.glBindTexture(3553, this.curveTextures[0]);
                GLES20.glTexImage2D(3553, 0, 6408, 200, 1, 0, 6408, 5121, fillAndGetCurveBuffer);
                GLES20.glUniform1i(this.curvesImageHandle, 1);
            }
        }
        GLES20.glUniform1f(this.widthHandle, (float) this.renderBufferWidth);
        GLES20.glUniform1f(this.heightHandle, (float) this.renderBufferHeight);
        GLES20.glEnableVertexAttribArray(this.inputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.inputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.positionHandle);
        GLES20.glVertexAttribPointer(this.positionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        GLES20.glDrawArrays(5, 0, 4);
    }

    public boolean drawBlurPass() {
        FilterShadersDelegate filterShadersDelegate;
        FilterShadersDelegate filterShadersDelegate2 = this.delegate;
        int blurType = filterShadersDelegate2 != null ? filterShadersDelegate2.getBlurType() : 0;
        if (this.isVideo || (filterShadersDelegate = this.delegate) == null || filterShadersDelegate.shouldShowOriginal() || blurType == 0) {
            return false;
        }
        if (this.needUpdateBlurTexture) {
            GLES20.glUseProgram(this.blurShaderProgram);
            GLES20.glUniform1i(this.blurSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.blurInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.blurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.blurPositionHandle);
            GLES20.glVertexAttribPointer(this.blurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1f(this.blurWidthHandle, 0.0f);
            GLES20.glUniform1f(this.blurHeightHandle, 1.0f / ((float) this.renderBufferHeight));
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[2]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[2], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glUniform1f(this.blurWidthHandle, 1.0f / ((float) this.renderBufferWidth));
            GLES20.glUniform1f(this.blurHeightHandle, 0.0f);
            GLES20.glDrawArrays(5, 0, 4);
            this.needUpdateBlurTexture = false;
        }
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
        if (blurType == 1) {
            GLES20.glUseProgram(this.radialBlurShaderProgram);
            GLES20.glUniform1i(this.radialBlurSourceImageHandle, 0);
            GLES20.glUniform1i(this.radialBlurSourceImage2Handle, 1);
            GLES20.glUniform1f(this.radialBlurExcludeSizeHandle, this.delegate.getBlurExcludeSize());
            GLES20.glUniform1f(this.radialBlurExcludeBlurSizeHandle, this.delegate.getBlurExcludeBlurSize());
            Point blurExcludePoint = this.delegate.getBlurExcludePoint();
            GLES20.glUniform2f(this.radialBlurExcludePointHandle, blurExcludePoint.x, blurExcludePoint.y);
            GLES20.glUniform1f(this.radialBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
            GLES20.glEnableVertexAttribArray(this.radialBlurInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.radialBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.radialBlurPositionHandle);
            GLES20.glVertexAttribPointer(this.radialBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        } else if (blurType == 2) {
            GLES20.glUseProgram(this.linearBlurShaderProgram);
            GLES20.glUniform1i(this.linearBlurSourceImageHandle, 0);
            GLES20.glUniform1i(this.linearBlurSourceImage2Handle, 1);
            GLES20.glUniform1f(this.linearBlurExcludeSizeHandle, this.delegate.getBlurExcludeSize());
            GLES20.glUniform1f(this.linearBlurExcludeBlurSizeHandle, this.delegate.getBlurExcludeBlurSize());
            GLES20.glUniform1f(this.linearBlurAngleHandle, this.delegate.getBlurAngle());
            Point blurExcludePoint2 = this.delegate.getBlurExcludePoint();
            GLES20.glUniform2f(this.linearBlurExcludePointHandle, blurExcludePoint2.x, blurExcludePoint2.y);
            GLES20.glUniform1f(this.linearBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
            GLES20.glEnableVertexAttribArray(this.linearBlurInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.linearBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.linearBlurPositionHandle);
            GLES20.glVertexAttribPointer(this.linearBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        }
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[1]);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.renderTexture[2]);
        GLES20.glDrawArrays(5, 0, 4);
        return true;
    }

    public void onVideoFrameUpdate(float[] fArr) {
        this.videoMatrix = fArr;
        this.hsvGenerated = false;
    }

    private Bitmap createBitmap(Bitmap bitmap, int i, int i2, int i3, float f) {
        Matrix matrix = new Matrix();
        matrix.setScale(f, f);
        matrix.postRotate((float) i);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void loadTexture(Bitmap bitmap, int i, int i2, int i3) {
        Bitmap bitmap2;
        float f;
        int i4 = i;
        this.renderBufferWidth = i2;
        this.renderBufferHeight = i3;
        if (this.renderFrameBuffer == null) {
            int[] iArr = new int[3];
            this.renderFrameBuffer = iArr;
            GLES20.glGenFramebuffers(3, iArr, 0);
            GLES20.glGenTextures(3, this.renderTexture, 0);
        }
        if (bitmap == null || bitmap.isRecycled()) {
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
        } else {
            float photoSize = (float) AndroidUtilities.getPhotoSize();
            if (((float) this.renderBufferWidth) > photoSize || ((float) this.renderBufferHeight) > photoSize || i4 % 360 != 0) {
                if (((float) this.renderBufferWidth) > photoSize || ((float) this.renderBufferHeight) > photoSize) {
                    float width = photoSize / ((float) bitmap.getWidth());
                    float height = photoSize / ((float) bitmap.getHeight());
                    if (width < height) {
                        this.renderBufferWidth = (int) photoSize;
                        this.renderBufferHeight = (int) (((float) bitmap.getHeight()) * width);
                        f = width;
                    } else {
                        this.renderBufferHeight = (int) photoSize;
                        this.renderBufferWidth = (int) (((float) bitmap.getWidth()) * height);
                        f = height;
                    }
                } else {
                    f = 1.0f;
                }
                int i5 = i4 % 360;
                if (i5 == 90 || i5 == 270) {
                    int i6 = this.renderBufferWidth;
                    this.renderBufferWidth = this.renderBufferHeight;
                    this.renderBufferHeight = i6;
                }
                bitmap2 = createBitmap(bitmap, i, this.renderBufferWidth, this.renderBufferHeight, f);
            } else {
                bitmap2 = bitmap;
            }
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, bitmap2, 0);
        }
        GLES20.glBindTexture(3553, this.renderTexture[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
        GLES20.glBindTexture(3553, this.renderTexture[2]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
    }

    public FloatBuffer getTextureBuffer() {
        return this.textureBuffer;
    }

    public FloatBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    public int getRenderBufferWidth() {
        return this.renderBufferWidth;
    }

    public int getRenderBufferHeight() {
        return this.renderBufferHeight;
    }

    public int getRenderTexture(int i) {
        if (!this.isVideo) {
            return this.renderTexture[i];
        }
        return this.renderTexture[i == 0 ? (char) 1 : 0];
    }

    public int getRenderFrameBuffer() {
        return this.renderFrameBuffer[!this.isVideo];
    }

    public void requestUpdateBlurTexture() {
        this.needUpdateBlurTexture = true;
    }

    public static FilterShadersDelegate getFilterShadersDelegate(final MediaController.SavedFilterState savedFilterState) {
        return new FilterShadersDelegate() {
            public boolean shouldShowOriginal() {
                return false;
            }

            public float getShadowsValue() {
                return ((savedFilterState.shadowsValue * 0.55f) + 100.0f) / 100.0f;
            }

            public float getHighlightsValue() {
                return ((savedFilterState.highlightsValue * 0.75f) + 100.0f) / 100.0f;
            }

            public float getEnhanceValue() {
                return savedFilterState.enhanceValue / 100.0f;
            }

            public float getExposureValue() {
                return savedFilterState.exposureValue / 100.0f;
            }

            public float getContrastValue() {
                return ((savedFilterState.contrastValue / 100.0f) * 0.3f) + 1.0f;
            }

            public float getWarmthValue() {
                return savedFilterState.warmthValue / 100.0f;
            }

            public float getVignetteValue() {
                return savedFilterState.vignetteValue / 100.0f;
            }

            public float getSharpenValue() {
                return ((savedFilterState.sharpenValue / 100.0f) * 0.6f) + 0.11f;
            }

            public float getGrainValue() {
                return (savedFilterState.grainValue / 100.0f) * 0.04f;
            }

            public float getFadeValue() {
                return savedFilterState.fadeValue / 100.0f;
            }

            public float getTintHighlightsIntensityValue() {
                return savedFilterState.tintHighlightsColor == 0 ? 0.0f : 0.5f;
            }

            public float getTintShadowsIntensityValue() {
                return savedFilterState.tintShadowsColor == 0 ? 0.0f : 0.5f;
            }

            public float getSaturationValue() {
                float f = savedFilterState.saturationValue / 100.0f;
                if (f > 0.0f) {
                    f *= 1.05f;
                }
                return f + 1.0f;
            }

            public int getTintHighlightsColor() {
                return savedFilterState.tintHighlightsColor;
            }

            public int getTintShadowsColor() {
                return savedFilterState.tintShadowsColor;
            }

            public int getBlurType() {
                return savedFilterState.blurType;
            }

            public float getBlurExcludeSize() {
                return savedFilterState.blurExcludeSize;
            }

            public float getBlurExcludeBlurSize() {
                return savedFilterState.blurExcludeBlurSize;
            }

            public float getBlurAngle() {
                return savedFilterState.blurAngle;
            }

            public Point getBlurExcludePoint() {
                return savedFilterState.blurExcludePoint;
            }

            public boolean shouldDrawCurvesPass() {
                return !savedFilterState.curvesToolValue.shouldBeSkipped();
            }

            public ByteBuffer fillAndGetCurveBuffer() {
                savedFilterState.curvesToolValue.fillBuffer();
                return savedFilterState.curvesToolValue.curveBuffer;
            }
        };
    }
}
