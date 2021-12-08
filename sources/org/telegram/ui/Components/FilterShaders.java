package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;

public class FilterShaders {
    private static final int PGPhotoEnhanceHistogramBins = 256;
    private static final int PGPhotoEnhanceSegments = 4;
    private static final String YUCIHighPassSkinSmoothingMaskBoostFilterFragmentShaderCode = "precision lowp float;varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {vec4 color = texture2D(sourceImage, texCoord);float hardLightColor = color.b;for (int i = 0; i < 3; ++i){if (hardLightColor < 0.5) {hardLightColor = hardLightColor * hardLightColor * 2.0;} else {hardLightColor = 1.0 - (1.0 - hardLightColor) * (1.0 - hardLightColor) * 2.0;}}float k = 255.0 / (164.0 - 75.0);hardLightColor = (hardLightColor - 75.0 / 255.0) * k;gl_FragColor = vec4(vec3(hardLightColor), color.a);}";
    private static final String enhanceFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.NUM, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c2 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c3 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c4 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}";
    private static final String greenAndBlueChannelOverlayFragmentShaderCode = "%1$s\nprecision lowp float;varying highp vec2 texCoord;uniform %2$s sourceImage;void main() {vec4 inp = texture2D(sourceImage, texCoord);vec4 image = vec4(inp.rgb * pow(2.0, -1.0), inp.w);vec4 base = vec4(image.g, image.g, image.g, 1.0);vec4 overlay = vec4(image.b, image.b, image.b, 1.0);float ba = 2.0 * overlay.b * base.b + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);gl_FragColor = vec4(ba,ba,ba,image.a);}";
    private static final String highpassSkinSmoothingCompositingFilterFragmentShaderString = "%1$s\nprecision lowp float;varying highp vec2 texCoord;varying highp vec2 texCoord2;uniform %2$s sourceImage;uniform sampler2D toneCurveTexture;uniform sampler2D inputImageTexture3;uniform lowp float mixturePercent;void main() {vec4 image = texture2D(sourceImage, texCoord);vec4 mask = texture2D(inputImageTexture3, texCoord2);float redCurveValue = texture2D(toneCurveTexture, vec2(image.r, 0.0)).r;float greenCurveValue = texture2D(toneCurveTexture, vec2(image.g, 0.0)).g;float blueCurveValue = texture2D(toneCurveTexture, vec2(image.b, 0.0)).b;vec4 result = vec4(redCurveValue, greenCurveValue, blueCurveValue, image.a);vec4 tone = mix(image, result, mixturePercent);gl_FragColor = vec4(mix(image.rgb, tone.rgb, 1.0 - mask.b), 1.0);}";
    private static final String linearBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
    private static final String radialBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
    private static final String rgbToHsvFragmentShaderCode = "%1$s\nprecision highp float;varying vec2 texCoord;uniform %2$s sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}";
    private static final String sharpenFragmentShaderCode = "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}";
    private static final String sharpenVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}";
    public static final String simpleFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}";
    public static final String simpleTwoTextureVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;varying vec2 texCoord2;void main() {gl_Position = position;texCoord = inputTexCoord;texCoord2 = inputTexCoord;}";
    private static final String simpleTwoTextureVertexVideoShaderCode = "attribute vec4 position;uniform mat4 videoMatrix;attribute vec4 inputTexCoord;varying vec2 texCoord;varying vec2 texCoord2;void main() {gl_Position = position;texCoord = vec2(videoMatrix * inputTexCoord).xy;texCoord2 = inputTexCoord.xy;}";
    public static final String simpleVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}";
    private static final String simpleVertexVideoShaderCode = "attribute vec4 position;uniform mat4 videoMatrix;attribute vec4 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = vec2(videoMatrix * inputTexCoord).xy;}";
    private static final String stillImageHighPassFilterFragmentShaderCode = "precision lowp float;varying highp vec2 texCoord;varying highp vec2 texCoord2;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;void main() {vec4 image = texture2D(sourceImage, texCoord);vec4 blurredImage = texture2D(inputImageTexture2, texCoord2);gl_FragColor = vec4((image.rgb - blurredImage.rgb + vec3(0.5,0.5,0.5)), image.a);}";
    private static final String toolsFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;}return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}";
    private int[] bitmapTextre = new int[1];
    private BlurProgram blurProgram;
    private boolean blurTextureCreated;
    private int boostInputTexCoordHandle;
    private int boostPositionHandle;
    private int boostProgram;
    private int boostSourceImageHandle;
    private ByteBuffer calcBuffer;
    private ByteBuffer cdtBuffer;
    private int compositeCurveImageHandle;
    private int compositeInputImageHandle;
    private int compositeInputTexCoordHandle;
    private int compositeMatrixHandle;
    private int compositeMixtureHandle;
    private int compositePositionHandle;
    private int compositeProgram;
    private int compositeSourceImageHandle;
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
    private int greenAndBlueChannelOverlayInputTexCoordHandle;
    private int greenAndBlueChannelOverlayMatrixHandle;
    private int greenAndBlueChannelOverlayPositionHandle;
    private int greenAndBlueChannelOverlayProgram;
    private int greenAndBlueChannelOverlaySourceImageHandle;
    private int heightHandle;
    private int highPassInputImageHandle;
    private int highPassInputTexCoordHandle;
    private int highPassPositionHandle;
    private int highPassProgram;
    private int highPassSourceImageHandle;
    private int highlightsHandle;
    private int highlightsTintColorHandle;
    private int highlightsTintIntensityHandle;
    private ByteBuffer hsvBuffer;
    private boolean hsvGenerated;
    private int inputTexCoordHandle;
    private boolean isVideo;
    private float lastRadius;
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
    private boolean needUpdateSkinTexture = true;
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
    private int[] renderTexture = new int[4];
    private int[] rgbToHsvInputTexCoordHandle = new int[2];
    private int rgbToHsvMatrixHandle;
    private int[] rgbToHsvPositionHandle = new int[2];
    private int[] rgbToHsvShaderProgram = new int[2];
    private int[] rgbToHsvSourceImageHandle = new int[2];
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
    private BlurProgram skinBlurProgram;
    private boolean skinPassDrawn;
    private boolean skinTextureCreated;
    private int skipToneHandle;
    private int sourceImageHandle;
    private FloatBuffer textureBuffer;
    private ToneCurve toneCurve;
    private int toolsShaderProgram;
    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexInvertBuffer;
    private int videoFramesCount;
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

        float getSoftenSkinValue();

        int getTintHighlightsColor();

        float getTintHighlightsIntensityValue();

        int getTintShadowsColor();

        float getTintShadowsIntensityValue();

        float getVignetteValue();

        float getWarmthValue();

        boolean shouldDrawCurvesPass();

        boolean shouldShowOriginal();
    }

    /* access modifiers changed from: private */
    public static String vertexShaderForOptimizedBlurOfRadius(int blurRadius, float sigma) {
        float[] standardGaussianWeights = new float[((blurRadius * 2) + 1)];
        float sumOfWeights = 0.0f;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeights[currentGaussianWeightIndex] = (float) ((1.0d / Math.sqrt(Math.pow((double) sigma, 2.0d) * 6.283185307179586d)) * Math.exp((-Math.pow((double) currentGaussianWeightIndex, 2.0d)) / (Math.pow((double) sigma, 2.0d) * 2.0d)));
            if (currentGaussianWeightIndex == 0) {
                sumOfWeights += standardGaussianWeights[currentGaussianWeightIndex];
            } else {
                double d = (double) sumOfWeights;
                double d2 = (double) standardGaussianWeights[currentGaussianWeightIndex];
                Double.isNaN(d2);
                Double.isNaN(d);
                sumOfWeights = (float) (d + (d2 * 2.0d));
            }
        }
        for (int currentGaussianWeightIndex2 = 0; currentGaussianWeightIndex2 < blurRadius + 1; currentGaussianWeightIndex2++) {
            standardGaussianWeights[currentGaussianWeightIndex2] = standardGaussianWeights[currentGaussianWeightIndex2] / sumOfWeights;
        }
        int numberOfOptimizedOffsets = Math.min((blurRadius / 2) + (blurRadius % 2), 7);
        float[] optimizedGaussianOffsets = new float[numberOfOptimizedOffsets];
        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; currentOptimizedOffset++) {
            float firstWeight = standardGaussianWeights[(currentOptimizedOffset * 2) + 1];
            float secondWeight = standardGaussianWeights[(currentOptimizedOffset * 2) + 2];
            optimizedGaussianOffsets[currentOptimizedOffset] = ((((float) ((currentOptimizedOffset * 2) + 1)) * firstWeight) + (((float) ((currentOptimizedOffset * 2) + 2)) * secondWeight)) / (firstWeight + secondWeight);
        }
        StringBuilder shaderString = new StringBuilder();
        shaderString.append("attribute vec4 position;\n");
        shaderString.append("attribute vec4 inputTexCoord;\n");
        shaderString.append("uniform float texelWidthOffset;\n");
        shaderString.append("uniform float texelHeightOffset;\n");
        shaderString.append(String.format(Locale.US, "varying vec2 blurCoordinates[%d];\n", new Object[]{Integer.valueOf((numberOfOptimizedOffsets * 2) + 1)}));
        shaderString.append("void main()\n");
        shaderString.append("{\n");
        shaderString.append("gl_Position = position;\n");
        shaderString.append("vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");
        shaderString.append("blurCoordinates[0] = inputTexCoord.xy;\n");
        for (int currentOptimizedOffset2 = 0; currentOptimizedOffset2 < numberOfOptimizedOffsets; currentOptimizedOffset2++) {
            shaderString.append(String.format(Locale.US, "blurCoordinates[%d] = inputTexCoord.xy + singleStepOffset * %f;\nblurCoordinates[%d] = inputTexCoord.xy - singleStepOffset * %f;\n", new Object[]{Integer.valueOf((currentOptimizedOffset2 * 2) + 1), Float.valueOf(optimizedGaussianOffsets[currentOptimizedOffset2]), Integer.valueOf((currentOptimizedOffset2 * 2) + 2), Float.valueOf(optimizedGaussianOffsets[currentOptimizedOffset2])}));
        }
        shaderString.append("}");
        return shaderString.toString();
    }

    /* access modifiers changed from: private */
    public static String fragmentShaderForOptimizedBlurOfRadius(int blurRadius, float sigma) {
        float f = sigma;
        int i = 1;
        float[] standardGaussianWeights = new float[((blurRadius * 2) + 1)];
        float sumOfWeights = 0.0f;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeights[currentGaussianWeightIndex] = (float) ((1.0d / Math.sqrt(Math.pow((double) f, 2.0d) * 6.283185307179586d)) * Math.exp((-Math.pow((double) currentGaussianWeightIndex, 2.0d)) / (Math.pow((double) f, 2.0d) * 2.0d)));
            if (currentGaussianWeightIndex == 0) {
                sumOfWeights += standardGaussianWeights[currentGaussianWeightIndex];
            } else {
                double d = (double) sumOfWeights;
                double d2 = (double) standardGaussianWeights[currentGaussianWeightIndex];
                Double.isNaN(d2);
                Double.isNaN(d);
                sumOfWeights = (float) (d + (d2 * 2.0d));
            }
        }
        for (int currentGaussianWeightIndex2 = 0; currentGaussianWeightIndex2 < blurRadius + 1; currentGaussianWeightIndex2++) {
            standardGaussianWeights[currentGaussianWeightIndex2] = standardGaussianWeights[currentGaussianWeightIndex2] / sumOfWeights;
        }
        int numberOfOptimizedOffsets = Math.min((blurRadius / 2) + (blurRadius % 2), 7);
        int trueNumberOfOptimizedOffsets = (blurRadius / 2) + (blurRadius % 2);
        StringBuilder shaderString = new StringBuilder();
        shaderString.append("uniform sampler2D sourceImage;\n");
        shaderString.append("uniform highp float texelWidthOffset;\n");
        shaderString.append("uniform highp float texelHeightOffset;\n");
        shaderString.append(String.format(Locale.US, "varying highp vec2 blurCoordinates[%d];\n", new Object[]{Integer.valueOf((numberOfOptimizedOffsets * 2) + 1)}));
        shaderString.append("void main()\n");
        shaderString.append("{\n");
        shaderString.append("lowp vec4 sum = vec4(0.0);\n");
        shaderString.append(String.format(Locale.US, "sum += texture2D(sourceImage, blurCoordinates[0]) * %f;\n", new Object[]{Float.valueOf(standardGaussianWeights[0])}));
        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < numberOfOptimizedOffsets; currentBlurCoordinateIndex++) {
            float optimizedWeight = standardGaussianWeights[(currentBlurCoordinateIndex * 2) + 1] + standardGaussianWeights[(currentBlurCoordinateIndex * 2) + 2];
            shaderString.append(String.format(Locale.US, "sum += texture2D(sourceImage, blurCoordinates[%d]) * %f;\n", new Object[]{Integer.valueOf((currentBlurCoordinateIndex * 2) + 1), Float.valueOf(optimizedWeight)}));
            shaderString.append(String.format(Locale.US, "sum += texture2D(sourceImage, blurCoordinates[%d]) * %f;\n", new Object[]{Integer.valueOf((currentBlurCoordinateIndex * 2) + 2), Float.valueOf(optimizedWeight)}));
        }
        if (trueNumberOfOptimizedOffsets > numberOfOptimizedOffsets) {
            shaderString.append("highp vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");
            int currentOverlowTextureRead = numberOfOptimizedOffsets;
            while (currentOverlowTextureRead < trueNumberOfOptimizedOffsets) {
                float firstWeight = standardGaussianWeights[(currentOverlowTextureRead * 2) + i];
                float secondWeight = standardGaussianWeights[(currentOverlowTextureRead * 2) + 2];
                float optimizedWeight2 = firstWeight + secondWeight;
                float optimizedOffset = ((((float) ((currentOverlowTextureRead * 2) + i)) * firstWeight) + (((float) ((currentOverlowTextureRead * 2) + 2)) * secondWeight)) / optimizedWeight2;
                Locale locale = Locale.US;
                Object[] objArr = new Object[2];
                objArr[0] = Float.valueOf(optimizedOffset);
                objArr[i] = Float.valueOf(optimizedWeight2);
                shaderString.append(String.format(locale, "sum += texture2D(sourceImage, blurCoordinates[0] + singleStepOffset * %f) * %f;\n", objArr));
                shaderString.append(String.format(Locale.US, "sum += texture2D(sourceImage, blurCoordinates[0] - singleStepOffset * %f) * %f;\n", new Object[]{Float.valueOf(optimizedOffset), Float.valueOf(optimizedWeight2)}));
                currentOverlowTextureRead++;
                i = 1;
            }
        }
        shaderString.append("gl_FragColor = sum;\n");
        shaderString.append("}\n");
        return shaderString.toString();
    }

    private static class BlurProgram {
        public int blurHeightHandle;
        public int blurInputTexCoordHandle;
        public int blurPositionHandle;
        public int blurShaderProgram;
        public int blurSourceImageHandle;
        public int blurWidthHandle;
        private String fragmentShaderCode;
        private String vertexShaderCode;

        public BlurProgram(float radius, float sigma, boolean calc) {
            int calculatedSampleRadius;
            if (calc) {
                sigma = (float) Math.round(radius);
                calculatedSampleRadius = 0;
                if (sigma >= 1.0f) {
                    double d = (double) 0.00390625f;
                    double sqrt = Math.sqrt(Math.pow((double) sigma, 2.0d) * 6.283185307179586d);
                    Double.isNaN(d);
                    int calculatedSampleRadius2 = (int) Math.floor(Math.sqrt(Math.pow((double) sigma, 2.0d) * -2.0d * Math.log(d * sqrt)));
                    calculatedSampleRadius = calculatedSampleRadius2 + (calculatedSampleRadius2 % 2);
                }
            } else {
                calculatedSampleRadius = (int) radius;
            }
            this.fragmentShaderCode = FilterShaders.fragmentShaderForOptimizedBlurOfRadius(calculatedSampleRadius, sigma);
            this.vertexShaderCode = FilterShaders.vertexShaderForOptimizedBlurOfRadius(calculatedSampleRadius, sigma);
        }

        public void destroy() {
            int i = this.blurShaderProgram;
            if (i != 0) {
                GLES20.glDeleteProgram(i);
                this.blurShaderProgram = 0;
            }
        }

        public boolean create() {
            int vertexShader = FilterShaders.loadShader(35633, this.vertexShaderCode);
            int fragmentShader = FilterShaders.loadShader(35632, this.fragmentShaderCode);
            if (vertexShader == 0 || fragmentShader == 0) {
                return false;
            }
            int glCreateProgram = GLES20.glCreateProgram();
            this.blurShaderProgram = glCreateProgram;
            GLES20.glAttachShader(glCreateProgram, vertexShader);
            GLES20.glAttachShader(this.blurShaderProgram, fragmentShader);
            GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
            GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
            GLES20.glLinkProgram(this.blurShaderProgram);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(this.blurShaderProgram, 35714, linkStatus, 0);
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(this.blurShaderProgram);
                this.blurShaderProgram = 0;
            } else {
                this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
                this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
                this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
                this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
                this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
            }
            return true;
        }
    }

    private static class ToneCurve {
        private float[] blueCurve;
        private int[] curveTexture = new int[1];
        private float[] greenCurve;
        private float[] redCurve;
        private float[] rgbCompositeCurve;

        public ToneCurve() {
            ArrayList<PointF> defaultCurve = new ArrayList<>();
            defaultCurve.add(new PointF(0.0f, 0.0f));
            defaultCurve.add(new PointF(0.5f, 0.5f));
            defaultCurve.add(new PointF(1.0f, 1.0f));
            ArrayList<PointF> rgbDefaultCurve = new ArrayList<>();
            rgbDefaultCurve.add(new PointF(0.0f, 0.0f));
            rgbDefaultCurve.add(new PointF(0.47f, 0.57f));
            rgbDefaultCurve.add(new PointF(1.0f, 1.0f));
            this.rgbCompositeCurve = getPreparedSplineCurve(rgbDefaultCurve);
            float[] preparedSplineCurve = getPreparedSplineCurve(defaultCurve);
            this.blueCurve = preparedSplineCurve;
            this.greenCurve = preparedSplineCurve;
            this.redCurve = preparedSplineCurve;
            updateToneCurveTexture();
        }

        private float[] getPreparedSplineCurve(ArrayList<PointF> points) {
            int N = points.size();
            for (int i = 0; i < N; i++) {
                PointF point = points.get(i);
                point.x *= 255.0f;
                point.y *= 255.0f;
            }
            ArrayList<PointF> splinePoints = splineCurve(points);
            PointF firstSplinePoint = splinePoints.get(0);
            if (firstSplinePoint.x > 0.0f) {
                for (int i2 = (int) firstSplinePoint.x; i2 >= 0; i2--) {
                    splinePoints.add(0, new PointF((float) i2, 0.0f));
                }
            }
            PointF lastSplinePoint = splinePoints.get(splinePoints.size() - 1);
            if (lastSplinePoint.x < 255.0f) {
                int i3 = (int) lastSplinePoint.x;
                while (true) {
                    i3++;
                    if (i3 > 255) {
                        break;
                    }
                    splinePoints.add(new PointF((float) i3, 255.0f));
                }
            }
            float[] preparedSplinePoints = new float[splinePoints.size()];
            int N2 = splinePoints.size();
            for (int i4 = 0; i4 < N2; i4++) {
                PointF newPoint = splinePoints.get(i4);
                float distance = (float) Math.sqrt(Math.pow((double) (newPoint.x - newPoint.y), 2.0d));
                if (newPoint.x > newPoint.y) {
                    distance = -distance;
                }
                preparedSplinePoints[i4] = distance;
            }
            return preparedSplinePoints;
        }

        private ArrayList<PointF> splineCurve(ArrayList<PointF> points) {
            ArrayList<PointF> arrayList = points;
            double[] sd = secondDerivative(points);
            int n = sd.length;
            if (n < 1) {
                return null;
            }
            ArrayList<PointF> output = new ArrayList<>(n + 1);
            for (int i = 0; i < n - 1; i++) {
                PointF cur = arrayList.get(i);
                PointF next = arrayList.get(i + 1);
                int x = (int) cur.x;
                while (x < ((int) next.x)) {
                    double d = (double) (((float) x) - cur.x);
                    double d2 = (double) (next.x - cur.x);
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    double t = d / d2;
                    double a = 1.0d - t;
                    double h = (double) (next.x - cur.x);
                    ArrayList<PointF> output2 = output;
                    double d3 = (double) cur.y;
                    Double.isNaN(d3);
                    PointF cur2 = cur;
                    PointF next2 = next;
                    double d4 = (double) next.y;
                    Double.isNaN(d4);
                    Double.isNaN(h);
                    Double.isNaN(h);
                    float y = (float) ((d3 * a) + (d4 * t) + (((h * h) / 6.0d) * (((((a * a) * a) - a) * sd[i]) + ((((t * t) * t) - t) * sd[i + 1]))));
                    if (y > 255.0f) {
                        y = 255.0f;
                    } else if (y < 0.0f) {
                        y = 0.0f;
                    }
                    ArrayList<PointF> output3 = output2;
                    output3.add(new PointF((float) x, y));
                    x++;
                    output = output3;
                    cur = cur2;
                    next = next2;
                }
                PointF pointF = next;
                ArrayList<PointF> arrayList2 = output;
            }
            ArrayList<PointF> output4 = output;
            output4.add(arrayList.get(points.size() - 1));
            return output4;
        }

        private double[] secondDerivative(ArrayList<PointF> points) {
            ArrayList<PointF> arrayList = points;
            int n = points.size();
            if (n <= 0) {
                return null;
            }
            char c = 1;
            if (n == 1) {
                return null;
            }
            char c2 = 2;
            int[] iArr = new int[2];
            iArr[1] = 3;
            char c3 = 0;
            iArr[0] = n;
            double[][] matrix = (double[][]) Array.newInstance(double.class, iArr);
            double[] result = new double[n];
            matrix[0][1] = 1.0d;
            matrix[0][0] = 0.0d;
            matrix[0][2] = 0.0d;
            int i = 1;
            while (i < n - 1) {
                PointF P1 = arrayList.get(i - 1);
                PointF P2 = arrayList.get(i);
                PointF P3 = arrayList.get(i + 1);
                double[] dArr = matrix[i];
                double d = (double) (P2.x - P1.x);
                Double.isNaN(d);
                dArr[c3] = d / 6.0d;
                double[] dArr2 = matrix[i];
                double d2 = (double) (P3.x - P1.x);
                Double.isNaN(d2);
                dArr2[c] = d2 / 3.0d;
                double[] dArr3 = matrix[i];
                double d3 = (double) (P3.x - P2.x);
                Double.isNaN(d3);
                dArr3[c2] = d3 / 6.0d;
                double d4 = (double) (P3.y - P2.y);
                double d5 = (double) (P3.x - P2.x);
                Double.isNaN(d4);
                Double.isNaN(d5);
                double d6 = d4 / d5;
                double d7 = (double) (P2.y - P1.y);
                double d8 = (double) (P2.x - P1.x);
                Double.isNaN(d7);
                Double.isNaN(d8);
                result[i] = d6 - (d7 / d8);
                i++;
                c = 1;
                c3 = 0;
                c2 = 2;
            }
            result[0] = 0.0d;
            result[n - 1] = 0.0d;
            matrix[n - 1][1] = 1.0d;
            matrix[n - 1][0] = 0.0d;
            matrix[n - 1][2] = 0.0d;
            for (int i2 = 1; i2 < n; i2++) {
                double k = matrix[i2][0] / matrix[i2 - 1][1];
                double[] dArr4 = matrix[i2];
                dArr4[1] = dArr4[1] - (matrix[i2 - 1][2] * k);
                matrix[i2][0] = 0.0d;
                result[i2] = result[i2] - (result[i2 - 1] * k);
            }
            for (int i3 = n - 2; i3 >= 0; i3--) {
                double k2 = matrix[i3][2] / matrix[i3 + 1][1];
                double[] dArr5 = matrix[i3];
                dArr5[1] = dArr5[1] - (matrix[i3 + 1][0] * k2);
                matrix[i3][2] = 0.0d;
                result[i3] = result[i3] - (result[i3 + 1] * k2);
            }
            double[] output = new double[n];
            for (int i4 = 0; i4 < n; i4++) {
                output[i4] = result[i4] / matrix[i4][1];
            }
            return output;
        }

        private void updateToneCurveTexture() {
            GLES20.glGenTextures(1, this.curveTexture, 0);
            GLES20.glBindTexture(3553, this.curveTexture[0]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            ByteBuffer curveBuffer = ByteBuffer.allocateDirect(1024);
            curveBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (this.redCurve.length >= 256 && this.greenCurve.length >= 256 && this.blueCurve.length >= 256 && this.rgbCompositeCurve.length >= 256) {
                for (int currentCurveIndex = 0; currentCurveIndex < 256; currentCurveIndex++) {
                    int r = (int) Math.min(Math.max(((float) currentCurveIndex) + this.redCurve[currentCurveIndex], 0.0f), 255.0f);
                    int g = (int) Math.min(Math.max(((float) currentCurveIndex) + this.greenCurve[currentCurveIndex], 0.0f), 255.0f);
                    int b = (int) Math.min(Math.max(((float) currentCurveIndex) + this.blueCurve[currentCurveIndex], 0.0f), 255.0f);
                    curveBuffer.put((byte) ((int) Math.min(Math.max(((float) b) + this.rgbCompositeCurve[b], 0.0f), 255.0f)));
                    curveBuffer.put((byte) ((int) Math.min(Math.max(((float) g) + this.rgbCompositeCurve[g], 0.0f), 255.0f)));
                    curveBuffer.put((byte) ((int) Math.min(Math.max(((float) r) + this.rgbCompositeCurve[r], 0.0f), 255.0f)));
                    curveBuffer.put((byte) -1);
                }
                curveBuffer.position(0);
                GLES20.glTexImage2D(3553, 0, 6408, 256, 1, 0, 6408, 5121, curveBuffer);
            }
        }

        public int getCurveTexture() {
            return this.curveTexture[0];
        }
    }

    public FilterShaders(boolean video) {
        this.isVideo = video;
        float[] squareCoordinates = {-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = bb.asFloatBuffer();
        this.vertexBuffer = asFloatBuffer;
        asFloatBuffer.put(squareCoordinates);
        this.vertexBuffer.position(0);
        float[] squareCoordinates2 = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        ByteBuffer bb2 = ByteBuffer.allocateDirect(squareCoordinates2.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = bb2.asFloatBuffer();
        this.vertexInvertBuffer = asFloatBuffer2;
        asFloatBuffer2.put(squareCoordinates2);
        this.vertexInvertBuffer.position(0);
        float[] textureCoordinates = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        ByteBuffer bb3 = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        bb3.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer3 = bb3.asFloatBuffer();
        this.textureBuffer = asFloatBuffer3;
        asFloatBuffer3.put(textureCoordinates);
        this.textureBuffer.position(0);
    }

    public void setDelegate(FilterShadersDelegate filterShadersDelegate) {
        this.delegate = filterShadersDelegate;
    }

    public boolean create() {
        int vertexShader;
        int vertexShader2;
        int vertexShader3;
        int fragmentShader;
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
        int[] linkStatus = new int[1];
        int vertexShader4 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
        int fragmentShader2 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;}return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}");
        if (vertexShader4 == 0 || fragmentShader2 == 0) {
            return false;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        this.toolsShaderProgram = glCreateProgram;
        GLES20.glAttachShader(glCreateProgram, vertexShader4);
        GLES20.glAttachShader(this.toolsShaderProgram, fragmentShader2);
        GLES20.glBindAttribLocation(this.toolsShaderProgram, 0, "position");
        GLES20.glBindAttribLocation(this.toolsShaderProgram, 1, "inputTexCoord");
        GLES20.glLinkProgram(this.toolsShaderProgram);
        GLES20.glGetProgramiv(this.toolsShaderProgram, 35714, linkStatus, 0);
        if (linkStatus[0] == 0) {
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
        int vertexShader5 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}");
        int fragmentShader3 = loadShader(35632, "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}");
        if (vertexShader5 == 0 || fragmentShader3 == 0) {
            return false;
        }
        int glCreateProgram2 = GLES20.glCreateProgram();
        this.sharpenShaderProgram = glCreateProgram2;
        GLES20.glAttachShader(glCreateProgram2, vertexShader5);
        GLES20.glAttachShader(this.sharpenShaderProgram, fragmentShader3);
        GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
        GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
        GLES20.glLinkProgram(this.sharpenShaderProgram);
        GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, linkStatus, 0);
        if (linkStatus[0] == 0) {
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
        BlurProgram blurProgram2 = new BlurProgram(8.0f, 3.0f, false);
        this.blurProgram = blurProgram2;
        if (!blurProgram2.create()) {
            return false;
        }
        boolean z = this.isVideo;
        String extension = z ? "#extension GL_OES_EGL_image_external : require" : "";
        String sampler2D = z ? "samplerExternalOES" : "sampler2D";
        int vertexShader6 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
        int fragmentShader4 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
        if (vertexShader6 == 0 || fragmentShader4 == 0) {
            return false;
        }
        int glCreateProgram3 = GLES20.glCreateProgram();
        this.linearBlurShaderProgram = glCreateProgram3;
        GLES20.glAttachShader(glCreateProgram3, vertexShader6);
        GLES20.glAttachShader(this.linearBlurShaderProgram, fragmentShader4);
        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
        GLES20.glLinkProgram(this.linearBlurShaderProgram);
        GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, linkStatus, 0);
        if (linkStatus[0] == 0) {
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
        int vertexShader7 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
        int fragmentShader5 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
        if (vertexShader7 == 0 || fragmentShader5 == 0) {
            return false;
        }
        int glCreateProgram4 = GLES20.glCreateProgram();
        this.radialBlurShaderProgram = glCreateProgram4;
        GLES20.glAttachShader(glCreateProgram4, vertexShader7);
        GLES20.glAttachShader(this.radialBlurShaderProgram, fragmentShader5);
        GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
        GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
        GLES20.glLinkProgram(this.radialBlurShaderProgram);
        GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, linkStatus, 0);
        if (linkStatus[0] == 0) {
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
        int a = 0;
        while (true) {
            boolean z2 = this.isVideo;
            if (a < (z2 ? 2 : 1)) {
                if (a != 1 || !z2) {
                    int i = fragmentShader5;
                    vertexShader3 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                    fragmentShader = loadShader(35632, String.format(Locale.US, "%1$s\nprecision highp float;varying vec2 texCoord;uniform %2$s sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}", new Object[]{"", "sampler2D"}));
                } else {
                    vertexShader3 = loadShader(35633, "attribute vec4 position;uniform mat4 videoMatrix;attribute vec4 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = vec2(videoMatrix * inputTexCoord).xy;}");
                    int i2 = fragmentShader5;
                    fragmentShader = loadShader(35632, String.format(Locale.US, "%1$s\nprecision highp float;varying vec2 texCoord;uniform %2$s sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}", new Object[]{extension, sampler2D}));
                }
                if (vertexShader3 == 0 || fragmentShader == 0) {
                } else {
                    this.rgbToHsvShaderProgram[a] = GLES20.glCreateProgram();
                    GLES20.glAttachShader(this.rgbToHsvShaderProgram[a], vertexShader3);
                    GLES20.glAttachShader(this.rgbToHsvShaderProgram[a], fragmentShader);
                    GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram[a], 0, "position");
                    GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram[a], 1, "inputTexCoord");
                    GLES20.glLinkProgram(this.rgbToHsvShaderProgram[a]);
                    int fragmentShader6 = fragmentShader;
                    GLES20.glGetProgramiv(this.rgbToHsvShaderProgram[a], 35714, linkStatus, 0);
                    if (linkStatus[0] == 0) {
                        GLES20.glDeleteProgram(this.rgbToHsvShaderProgram[a]);
                        this.rgbToHsvShaderProgram[a] = 0;
                    } else {
                        this.rgbToHsvPositionHandle[a] = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram[a], "position");
                        this.rgbToHsvInputTexCoordHandle[a] = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram[a], "inputTexCoord");
                        this.rgbToHsvSourceImageHandle[a] = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram[a], "sourceImage");
                        if (a == 1) {
                            this.rgbToHsvMatrixHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram[a], "videoMatrix");
                        }
                    }
                    a++;
                    fragmentShader5 = fragmentShader6;
                }
            } else {
                int vertexShader8 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                int fragmentShader7 = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.NUM, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c2 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c3 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c4 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}");
                if (vertexShader8 == 0 || fragmentShader7 == 0) {
                    return false;
                }
                int glCreateProgram5 = GLES20.glCreateProgram();
                this.enhanceShaderProgram = glCreateProgram5;
                GLES20.glAttachShader(glCreateProgram5, vertexShader8);
                GLES20.glAttachShader(this.enhanceShaderProgram, fragmentShader7);
                GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
                GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.enhanceShaderProgram);
                GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(this.enhanceShaderProgram);
                    this.enhanceShaderProgram = 0;
                } else {
                    this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
                    this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
                    this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
                    this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
                    this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
                }
                if (this.isVideo) {
                    vertexShader = loadShader(35633, "attribute vec4 position;uniform mat4 videoMatrix;attribute vec4 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = vec2(videoMatrix * inputTexCoord).xy;}");
                } else {
                    vertexShader = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                }
                int fragmentShader8 = loadShader(35632, String.format(Locale.US, "%1$s\nprecision lowp float;varying highp vec2 texCoord;uniform %2$s sourceImage;void main() {vec4 inp = texture2D(sourceImage, texCoord);vec4 image = vec4(inp.rgb * pow(2.0, -1.0), inp.w);vec4 base = vec4(image.g, image.g, image.g, 1.0);vec4 overlay = vec4(image.b, image.b, image.b, 1.0);float ba = 2.0 * overlay.b * base.b + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);gl_FragColor = vec4(ba,ba,ba,image.a);}", new Object[]{extension, sampler2D}));
                if (vertexShader == 0 || fragmentShader8 == 0) {
                    return false;
                }
                int glCreateProgram6 = GLES20.glCreateProgram();
                this.greenAndBlueChannelOverlayProgram = glCreateProgram6;
                GLES20.glAttachShader(glCreateProgram6, vertexShader);
                GLES20.glAttachShader(this.greenAndBlueChannelOverlayProgram, fragmentShader8);
                GLES20.glBindAttribLocation(this.greenAndBlueChannelOverlayProgram, 0, "position");
                GLES20.glBindAttribLocation(this.greenAndBlueChannelOverlayProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.greenAndBlueChannelOverlayProgram);
                GLES20.glGetProgramiv(this.greenAndBlueChannelOverlayProgram, 35714, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(this.greenAndBlueChannelOverlayProgram);
                    this.greenAndBlueChannelOverlayProgram = 0;
                } else {
                    this.greenAndBlueChannelOverlayPositionHandle = GLES20.glGetAttribLocation(this.greenAndBlueChannelOverlayProgram, "position");
                    this.greenAndBlueChannelOverlayInputTexCoordHandle = GLES20.glGetAttribLocation(this.greenAndBlueChannelOverlayProgram, "inputTexCoord");
                    this.greenAndBlueChannelOverlaySourceImageHandle = GLES20.glGetUniformLocation(this.greenAndBlueChannelOverlayProgram, "sourceImage");
                    if (this.isVideo) {
                        this.greenAndBlueChannelOverlayMatrixHandle = GLES20.glGetUniformLocation(this.greenAndBlueChannelOverlayProgram, "videoMatrix");
                    }
                }
                int fragmentShader9 = loadShader(35632, "precision lowp float;varying highp vec2 texCoord;varying highp vec2 texCoord2;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;void main() {vec4 image = texture2D(sourceImage, texCoord);vec4 blurredImage = texture2D(inputImageTexture2, texCoord2);gl_FragColor = vec4((image.rgb - blurredImage.rgb + vec3(0.5,0.5,0.5)), image.a);}");
                int vertexShader9 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;varying vec2 texCoord2;void main() {gl_Position = position;texCoord = inputTexCoord;texCoord2 = inputTexCoord;}");
                if (vertexShader9 == 0 || fragmentShader9 == 0) {
                    return false;
                }
                int glCreateProgram7 = GLES20.glCreateProgram();
                this.highPassProgram = glCreateProgram7;
                GLES20.glAttachShader(glCreateProgram7, vertexShader9);
                GLES20.glAttachShader(this.highPassProgram, fragmentShader9);
                GLES20.glBindAttribLocation(this.highPassProgram, 0, "position");
                GLES20.glBindAttribLocation(this.highPassProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.highPassProgram);
                GLES20.glGetProgramiv(this.highPassProgram, 35714, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(this.highPassProgram);
                    this.highPassProgram = 0;
                } else {
                    this.highPassPositionHandle = GLES20.glGetAttribLocation(this.highPassProgram, "position");
                    this.highPassInputTexCoordHandle = GLES20.glGetAttribLocation(this.highPassProgram, "inputTexCoord");
                    this.highPassSourceImageHandle = GLES20.glGetUniformLocation(this.highPassProgram, "sourceImage");
                    this.highPassInputImageHandle = GLES20.glGetUniformLocation(this.highPassProgram, "inputImageTexture2");
                }
                int fragmentShader10 = loadShader(35632, "precision lowp float;varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {vec4 color = texture2D(sourceImage, texCoord);float hardLightColor = color.b;for (int i = 0; i < 3; ++i){if (hardLightColor < 0.5) {hardLightColor = hardLightColor * hardLightColor * 2.0;} else {hardLightColor = 1.0 - (1.0 - hardLightColor) * (1.0 - hardLightColor) * 2.0;}}float k = 255.0 / (164.0 - 75.0);hardLightColor = (hardLightColor - 75.0 / 255.0) * k;gl_FragColor = vec4(vec3(hardLightColor), color.a);}");
                int vertexShader10 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                if (vertexShader10 == 0 || fragmentShader10 == 0) {
                    return false;
                }
                int glCreateProgram8 = GLES20.glCreateProgram();
                this.boostProgram = glCreateProgram8;
                GLES20.glAttachShader(glCreateProgram8, vertexShader10);
                GLES20.glAttachShader(this.boostProgram, fragmentShader10);
                GLES20.glBindAttribLocation(this.boostProgram, 0, "position");
                GLES20.glBindAttribLocation(this.boostProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.boostProgram);
                GLES20.glGetProgramiv(this.boostProgram, 35714, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(this.boostProgram);
                    this.boostProgram = 0;
                } else {
                    this.boostPositionHandle = GLES20.glGetAttribLocation(this.boostProgram, "position");
                    this.boostInputTexCoordHandle = GLES20.glGetAttribLocation(this.boostProgram, "inputTexCoord");
                    this.boostSourceImageHandle = GLES20.glGetUniformLocation(this.boostProgram, "sourceImage");
                }
                if (this.isVideo) {
                    vertexShader2 = loadShader(35633, "attribute vec4 position;uniform mat4 videoMatrix;attribute vec4 inputTexCoord;varying vec2 texCoord;varying vec2 texCoord2;void main() {gl_Position = position;texCoord = vec2(videoMatrix * inputTexCoord).xy;texCoord2 = inputTexCoord.xy;}");
                } else {
                    vertexShader2 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;varying vec2 texCoord2;void main() {gl_Position = position;texCoord = inputTexCoord;texCoord2 = inputTexCoord;}");
                }
                int fragmentShader11 = loadShader(35632, String.format(Locale.US, "%1$s\nprecision lowp float;varying highp vec2 texCoord;varying highp vec2 texCoord2;uniform %2$s sourceImage;uniform sampler2D toneCurveTexture;uniform sampler2D inputImageTexture3;uniform lowp float mixturePercent;void main() {vec4 image = texture2D(sourceImage, texCoord);vec4 mask = texture2D(inputImageTexture3, texCoord2);float redCurveValue = texture2D(toneCurveTexture, vec2(image.r, 0.0)).r;float greenCurveValue = texture2D(toneCurveTexture, vec2(image.g, 0.0)).g;float blueCurveValue = texture2D(toneCurveTexture, vec2(image.b, 0.0)).b;vec4 result = vec4(redCurveValue, greenCurveValue, blueCurveValue, image.a);vec4 tone = mix(image, result, mixturePercent);gl_FragColor = vec4(mix(image.rgb, tone.rgb, 1.0 - mask.b), 1.0);}", new Object[]{extension, sampler2D}));
                if (vertexShader2 == 0 || fragmentShader11 == 0) {
                    return false;
                }
                int glCreateProgram9 = GLES20.glCreateProgram();
                this.compositeProgram = glCreateProgram9;
                GLES20.glAttachShader(glCreateProgram9, vertexShader2);
                GLES20.glAttachShader(this.compositeProgram, fragmentShader11);
                GLES20.glBindAttribLocation(this.compositeProgram, 0, "position");
                GLES20.glBindAttribLocation(this.compositeProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.compositeProgram);
                GLES20.glGetProgramiv(this.compositeProgram, 35714, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(this.compositeProgram);
                    this.compositeProgram = 0;
                } else {
                    this.compositePositionHandle = GLES20.glGetAttribLocation(this.compositeProgram, "position");
                    this.compositeInputTexCoordHandle = GLES20.glGetAttribLocation(this.compositeProgram, "inputTexCoord");
                    this.compositeSourceImageHandle = GLES20.glGetUniformLocation(this.compositeProgram, "sourceImage");
                    this.compositeInputImageHandle = GLES20.glGetUniformLocation(this.compositeProgram, "inputImageTexture3");
                    this.compositeCurveImageHandle = GLES20.glGetUniformLocation(this.compositeProgram, "toneCurveTexture");
                    this.compositeMixtureHandle = GLES20.glGetUniformLocation(this.compositeProgram, "mixturePercent");
                    if (this.isVideo) {
                        this.compositeMatrixHandle = GLES20.glGetUniformLocation(this.compositeProgram, "videoMatrix");
                    }
                }
                this.toneCurve = new ToneCurve();
                return true;
            }
        }
        return false;
    }

    public void setRenderData(Bitmap currentBitmap, int orientation, int videoTex, int w, int h) {
        loadTexture(currentBitmap, orientation, w, h);
        this.videoTexture = videoTex;
        GLES20.glBindTexture(3553, this.enhanceTextures[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] != 0) {
            return shader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(shader));
            FileLog.e("shader code:\n " + shaderCode);
        }
        GLES20.glDeleteShader(shader);
        return 0;
    }

    public void drawEnhancePass() {
        boolean updateFrame;
        int index;
        boolean z = this.isVideo;
        if (z) {
            updateFrame = true;
        } else {
            updateFrame = !this.hsvGenerated;
        }
        if (updateFrame) {
            if (!z || this.skinPassDrawn) {
                GLES20.glUseProgram(this.rgbToHsvShaderProgram[0]);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.skinPassDrawn ? this.renderTexture[1] : this.bitmapTextre[0]);
                index = 0;
            } else {
                GLES20.glUseProgram(this.rgbToHsvShaderProgram[1]);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, this.videoTexture);
                GLES20.glUniformMatrix4fv(this.rgbToHsvMatrixHandle, 1, false, this.videoMatrix, 0);
                index = 1;
            }
            GLES20.glUniform1i(this.rgbToHsvSourceImageHandle[index], 0);
            GLES20.glEnableVertexAttribArray(this.rgbToHsvInputTexCoordHandle[index]);
            GLES20.glVertexAttribPointer(this.rgbToHsvInputTexCoordHandle[index], 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.rgbToHsvPositionHandle[index]);
            GLES20.glVertexAttribPointer(this.rgbToHsvPositionHandle[index], 2, 5126, false, 8, this.isVideo ? this.vertexInvertBuffer : this.vertexBuffer);
            GLES20.glBindFramebuffer(36160, this.enhanceFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.enhanceTextures[0], 0);
            GLES20.glDrawArrays(5, 0, 4);
        }
        if (this.hsvGenerated == 0) {
            int newCapacity = this.renderBufferWidth * this.renderBufferHeight * 4;
            ByteBuffer byteBuffer = this.hsvBuffer;
            if (byteBuffer == null || newCapacity > byteBuffer.capacity()) {
                this.hsvBuffer = ByteBuffer.allocateDirect(newCapacity);
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
                ByteBuffer curveBuffer = this.delegate.fillAndGetCurveBuffer();
                GLES20.glActiveTexture(33985);
                GLES20.glBindTexture(3553, this.curveTextures[0]);
                GLES20.glTexImage2D(3553, 0, 6408, 200, 1, 0, 6408, 5121, curveBuffer);
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

    public boolean drawSkinSmoothPass() {
        int i;
        FilterShadersDelegate filterShadersDelegate = this.delegate;
        if (filterShadersDelegate == null || filterShadersDelegate.shouldShowOriginal() || this.delegate.getSoftenSkinValue() <= 0.0f || (i = this.renderBufferWidth) <= 0 || this.renderBufferHeight <= 0) {
            if (this.skinPassDrawn) {
                this.hsvGenerated = false;
                this.skinPassDrawn = false;
            }
            return false;
        }
        if (this.needUpdateSkinTexture || this.isVideo) {
            float rad = ((float) i) * 0.006f;
            if (this.skinBlurProgram == null || ((double) Math.abs(this.lastRadius - rad)) > 1.0E-4d) {
                BlurProgram blurProgram2 = this.skinBlurProgram;
                if (blurProgram2 != null) {
                    blurProgram2.destroy();
                }
                this.lastRadius = rad;
                BlurProgram blurProgram3 = new BlurProgram(this.lastRadius, 2.0f, true);
                this.skinBlurProgram = blurProgram3;
                blurProgram3.create();
            }
            if (!this.skinTextureCreated) {
                GLES20.glBindTexture(3553, this.renderTexture[3]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
                this.skinTextureCreated = true;
            }
            GLES20.glUseProgram(this.greenAndBlueChannelOverlayProgram);
            GLES20.glUniform1i(this.greenAndBlueChannelOverlaySourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.greenAndBlueChannelOverlayInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.greenAndBlueChannelOverlayInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.greenAndBlueChannelOverlayPositionHandle);
            if (this.isVideo) {
                GLES20.glUniformMatrix4fv(this.greenAndBlueChannelOverlayMatrixHandle, 1, false, this.videoMatrix, 0);
            }
            GLES20.glVertexAttribPointer(this.greenAndBlueChannelOverlayPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glActiveTexture(33984);
            if (this.isVideo) {
                GLES20.glBindTexture(36197, this.videoTexture);
            } else {
                GLES20.glBindTexture(3553, this.bitmapTextre[0]);
            }
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glUseProgram(this.skinBlurProgram.blurShaderProgram);
            GLES20.glUniform1i(this.skinBlurProgram.blurSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.skinBlurProgram.blurInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.skinBlurProgram.blurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.skinBlurProgram.blurPositionHandle);
            GLES20.glVertexAttribPointer(this.skinBlurProgram.blurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glUniform1f(this.skinBlurProgram.blurWidthHandle, 0.0f);
            GLES20.glUniform1f(this.skinBlurProgram.blurHeightHandle, 1.0f / ((float) this.renderBufferHeight));
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[3]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[3], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1f(this.skinBlurProgram.blurWidthHandle, 1.0f / ((float) this.renderBufferWidth));
            GLES20.glUniform1f(this.skinBlurProgram.blurHeightHandle, 0.0f);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glUseProgram(this.highPassProgram);
            GLES20.glUniform1i(this.highPassSourceImageHandle, 0);
            GLES20.glUniform1i(this.highPassInputImageHandle, 1);
            GLES20.glEnableVertexAttribArray(this.highPassInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.highPassInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.highPassPositionHandle);
            GLES20.glVertexAttribPointer(this.highPassPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, this.renderTexture[3]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glUseProgram(this.boostProgram);
            GLES20.glUniform1i(this.boostSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.boostInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.boostInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.boostPositionHandle);
            GLES20.glVertexAttribPointer(this.boostPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[3]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[3], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glDrawArrays(5, 0, 4);
            this.needUpdateSkinTexture = false;
        }
        this.skinPassDrawn = true;
        this.hsvGenerated = false;
        GLES20.glUseProgram(this.compositeProgram);
        GLES20.glUniform1i(this.compositeSourceImageHandle, 0);
        GLES20.glUniform1i(this.compositeInputImageHandle, 1);
        GLES20.glUniform1i(this.compositeCurveImageHandle, 2);
        GLES20.glUniform1f(this.compositeMixtureHandle, this.delegate.getSoftenSkinValue());
        GLES20.glEnableVertexAttribArray(this.compositeInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.compositeInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.compositePositionHandle);
        GLES20.glVertexAttribPointer(this.compositePositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        if (this.isVideo) {
            GLES20.glUniformMatrix4fv(this.compositeMatrixHandle, 1, false, this.videoMatrix, 0);
        }
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
        GLES20.glActiveTexture(33984);
        if (this.isVideo) {
            GLES20.glBindTexture(36197, this.videoTexture);
        } else {
            GLES20.glBindTexture(3553, this.bitmapTextre[0]);
        }
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.renderTexture[3]);
        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(3553, this.toneCurve.getCurveTexture());
        GLES20.glDrawArrays(5, 0, 4);
        return true;
    }

    public boolean drawBlurPass() {
        FilterShadersDelegate filterShadersDelegate;
        FilterShadersDelegate filterShadersDelegate2 = this.delegate;
        int blurType = filterShadersDelegate2 != null ? filterShadersDelegate2.getBlurType() : 0;
        if (this.isVideo || (filterShadersDelegate = this.delegate) == null || filterShadersDelegate.shouldShowOriginal() || blurType == 0) {
            return false;
        }
        if (this.needUpdateBlurTexture) {
            if (!this.blurTextureCreated) {
                GLES20.glBindTexture(3553, this.renderTexture[2]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
                this.blurTextureCreated = true;
            }
            GLES20.glUseProgram(this.blurProgram.blurShaderProgram);
            GLES20.glUniform1i(this.blurProgram.blurSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.blurProgram.blurInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.blurProgram.blurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.blurProgram.blurPositionHandle);
            GLES20.glVertexAttribPointer(this.blurProgram.blurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1f(this.blurProgram.blurWidthHandle, 0.0f);
            GLES20.glUniform1f(this.blurProgram.blurHeightHandle, 1.0f / ((float) this.renderBufferHeight));
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[2]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[2], 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glUniform1f(this.blurProgram.blurWidthHandle, 1.0f / ((float) this.renderBufferWidth));
            GLES20.glUniform1f(this.blurProgram.blurHeightHandle, 0.0f);
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

    public void onVideoFrameUpdate(float[] m) {
        this.videoMatrix = m;
        this.hsvGenerated = false;
    }

    private Bitmap createBitmap(Bitmap bitmap, int orientation, float scale) {
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postRotate((float) orientation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void loadTexture(Bitmap bitmap, int orientation, int w, int h) {
        Bitmap bitmap2 = bitmap;
        int i = orientation;
        this.renderBufferWidth = w;
        this.renderBufferHeight = h;
        if (this.renderFrameBuffer == null) {
            int[] iArr = new int[4];
            this.renderFrameBuffer = iArr;
            GLES20.glGenFramebuffers(4, iArr, 0);
            GLES20.glGenTextures(4, this.renderTexture, 0);
        }
        if (bitmap2 != null && !bitmap.isRecycled()) {
            GLES20.glGenTextures(1, this.bitmapTextre, 0);
            float maxSize = (float) AndroidUtilities.getPhotoSize();
            int i2 = this.renderBufferWidth;
            if (((float) i2) > maxSize || ((float) this.renderBufferHeight) > maxSize || i % 360 != 0) {
                float scale = 1.0f;
                if (((float) i2) > maxSize || ((float) this.renderBufferHeight) > maxSize) {
                    float scaleX = maxSize / ((float) bitmap.getWidth());
                    float scaleY = maxSize / ((float) bitmap.getHeight());
                    if (scaleX < scaleY) {
                        this.renderBufferWidth = (int) maxSize;
                        this.renderBufferHeight = (int) (((float) bitmap.getHeight()) * scaleX);
                        scale = scaleX;
                    } else {
                        this.renderBufferHeight = (int) maxSize;
                        this.renderBufferWidth = (int) (((float) bitmap.getWidth()) * scaleY);
                        scale = scaleY;
                    }
                }
                if (i % 360 == 90 || i % 360 == 270) {
                    int temp = this.renderBufferWidth;
                    this.renderBufferWidth = this.renderBufferHeight;
                    this.renderBufferHeight = temp;
                }
                bitmap2 = createBitmap(bitmap2, i, scale);
            }
            GLES20.glBindTexture(3553, this.bitmapTextre[0]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, bitmap2, 0);
        }
        for (int a = 0; a < 2; a++) {
            GLES20.glBindTexture(3553, this.renderTexture[a]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
        }
    }

    public FloatBuffer getTextureBuffer() {
        return this.textureBuffer;
    }

    public FloatBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    public FloatBuffer getVertexInvertBuffer() {
        return this.vertexInvertBuffer;
    }

    public int getRenderBufferWidth() {
        return this.renderBufferWidth;
    }

    public int getRenderBufferHeight() {
        return this.renderBufferHeight;
    }

    public int getRenderTexture(int index) {
        if (!this.isVideo) {
            return this.renderTexture[index];
        }
        return this.renderTexture[index == 0 ? (char) 1 : 0];
    }

    public int getRenderFrameBuffer() {
        int[] iArr = this.renderFrameBuffer;
        if (iArr != null) {
            return iArr[!this.isVideo];
        }
        return 0;
    }

    public void requestUpdateBlurTexture() {
        this.needUpdateBlurTexture = true;
        this.needUpdateSkinTexture = true;
    }

    public static FilterShadersDelegate getFilterShadersDelegate(final MediaController.SavedFilterState lastState) {
        return new FilterShadersDelegate() {
            public boolean shouldShowOriginal() {
                return false;
            }

            public float getSoftenSkinValue() {
                return MediaController.SavedFilterState.this.softenSkinValue / 100.0f;
            }

            public float getShadowsValue() {
                return ((MediaController.SavedFilterState.this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
            }

            public float getHighlightsValue() {
                return ((MediaController.SavedFilterState.this.highlightsValue * 0.75f) + 100.0f) / 100.0f;
            }

            public float getEnhanceValue() {
                return MediaController.SavedFilterState.this.enhanceValue / 100.0f;
            }

            public float getExposureValue() {
                return MediaController.SavedFilterState.this.exposureValue / 100.0f;
            }

            public float getContrastValue() {
                return ((MediaController.SavedFilterState.this.contrastValue / 100.0f) * 0.3f) + 1.0f;
            }

            public float getWarmthValue() {
                return MediaController.SavedFilterState.this.warmthValue / 100.0f;
            }

            public float getVignetteValue() {
                return MediaController.SavedFilterState.this.vignetteValue / 100.0f;
            }

            public float getSharpenValue() {
                return ((MediaController.SavedFilterState.this.sharpenValue / 100.0f) * 0.6f) + 0.11f;
            }

            public float getGrainValue() {
                return (MediaController.SavedFilterState.this.grainValue / 100.0f) * 0.04f;
            }

            public float getFadeValue() {
                return MediaController.SavedFilterState.this.fadeValue / 100.0f;
            }

            public float getTintHighlightsIntensityValue() {
                if (MediaController.SavedFilterState.this.tintHighlightsColor == 0) {
                    return 0.0f;
                }
                return 50.0f / 100.0f;
            }

            public float getTintShadowsIntensityValue() {
                if (MediaController.SavedFilterState.this.tintShadowsColor == 0) {
                    return 0.0f;
                }
                return 50.0f / 100.0f;
            }

            public float getSaturationValue() {
                float parameterValue = MediaController.SavedFilterState.this.saturationValue / 100.0f;
                if (parameterValue > 0.0f) {
                    parameterValue *= 1.05f;
                }
                return 1.0f + parameterValue;
            }

            public int getTintHighlightsColor() {
                return MediaController.SavedFilterState.this.tintHighlightsColor;
            }

            public int getTintShadowsColor() {
                return MediaController.SavedFilterState.this.tintShadowsColor;
            }

            public int getBlurType() {
                return MediaController.SavedFilterState.this.blurType;
            }

            public float getBlurExcludeSize() {
                return MediaController.SavedFilterState.this.blurExcludeSize;
            }

            public float getBlurExcludeBlurSize() {
                return MediaController.SavedFilterState.this.blurExcludeBlurSize;
            }

            public float getBlurAngle() {
                return MediaController.SavedFilterState.this.blurAngle;
            }

            public Point getBlurExcludePoint() {
                return MediaController.SavedFilterState.this.blurExcludePoint;
            }

            public boolean shouldDrawCurvesPass() {
                return !MediaController.SavedFilterState.this.curvesToolValue.shouldBeSkipped();
            }

            public ByteBuffer fillAndGetCurveBuffer() {
                MediaController.SavedFilterState.this.curvesToolValue.fillBuffer();
                return MediaController.SavedFilterState.this.curvesToolValue.curveBuffer;
            }
        };
    }
}
