package org.telegram.ui.Components.Paint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ShaderSet {
    private static final String ATTRIBUTES = "attributes";
    private static final Map<String, Map<String, Object>> AVAILBALBE_SHADERS = createMap();
    private static final String FRAGMENT = "fragment";
    private static final String PAINT_BLITWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLITWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { vec4 tex = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }";
    private static final String PAINT_BRUSHLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { vec4 f = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = vec4(f.r * varIntensity, f.g, f.b, 0.0); }";
    private static final String PAINT_BRUSH_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { gl_FragColor = vec4(0, 0, 0, varIntensity * texture2D(texture, varTexcoord.st, 0.0).r); }";
    private static final String PAINT_BRUSH_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }";
    private static final String PAINT_COMPOSITEWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D mask; uniform vec4 color; void main (void) { vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float alpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); gl_FragColor.rgb = finalColor; gl_FragColor.a = alpha; }";
    private static final String PAINT_COMPOSITEWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D mask; uniform vec4 color; void main(void) { float alpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; gl_FragColor.rgb = color.rgb; gl_FragColor.a = alpha; }";
    private static final String PAINT_NONPREMULTIPLIEDBLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); }";
    private static final String UNIFORMS = "uniforms";
    private static final String VERTEX = "vertex";

    private static Map<String, Map<String, Object>> createMap() {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        String str = "vertex";
        hashMap2.put(str, "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }");
        String str2 = "fragment";
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { gl_FragColor = vec4(0, 0, 0, varIntensity * texture2D(texture, varTexcoord.st, 0.0).r); }");
        String[] strArr = new String[3];
        strArr[0] = "inPosition";
        strArr[1] = "inTexcoord";
        strArr[2] = "alpha";
        String str3 = "attributes";
        hashMap2.put(str3, strArr);
        strArr = new String[2];
        strArr[0] = "mvpMatrix";
        strArr[1] = "texture";
        String str4 = "uniforms";
        hashMap2.put(str4, strArr);
        hashMap.put("brush", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(str, "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }");
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { vec4 f = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = vec4(f.r * varIntensity, f.g, f.b, 0.0); }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord", "alpha"});
        hashMap2.put(str4, new String[]{"mvpMatrix", "texture"});
        hashMap.put("brushLight", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        String str5 = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }";
        hashMap2.put(str, str5);
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { vec4 tex = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor.rgb *= gl_FragColor.a; }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(str4, new String[]{"mvpMatrix", "texture"});
        hashMap.put("blit", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(str, str5);
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord"});
        r15 = new String[4];
        r15[2] = "mask";
        r15[3] = "color";
        hashMap2.put(str4, r15);
        hashMap.put("blitWithMaskLight", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(str, str5);
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(str4, new String[]{"mvpMatrix", "texture", "mask", "color"});
        hashMap.put("blitWithMask", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(str, str5);
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; uniform sampler2D mask; uniform vec4 color; void main(void) { float alpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; gl_FragColor.rgb = color.rgb; gl_FragColor.a = alpha; }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(str4, new String[]{"mvpMatrix", "mask", "color"});
        hashMap.put("compositeWithMask", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(str, str5);
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; uniform sampler2D mask; uniform vec4 color; void main (void) { vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float alpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); gl_FragColor.rgb = finalColor; gl_FragColor.a = alpha; }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(str4, new String[]{"mvpMatrix", "texture", "mask", "color"});
        hashMap.put("compositeWithMaskLight", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(str, str5);
        hashMap2.put(str2, "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); }");
        hashMap2.put(str3, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(str4, new String[]{"mvpMatrix", "texture"});
        hashMap.put("nonPremultipliedBlit", Collections.unmodifiableMap(hashMap2));
        return Collections.unmodifiableMap(hashMap);
    }

    public static Map<String, Shader> setup() {
        HashMap hashMap = new HashMap();
        for (Entry entry : AVAILBALBE_SHADERS.entrySet()) {
            Map map = (Map) entry.getValue();
            hashMap.put(entry.getKey(), new Shader((String) map.get("vertex"), (String) map.get("fragment"), (String[]) map.get("attributes"), (String[]) map.get("uniforms")));
        }
        return Collections.unmodifiableMap(hashMap);
    }
}
