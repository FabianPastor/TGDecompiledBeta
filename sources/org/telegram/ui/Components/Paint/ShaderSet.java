package org.telegram.ui.Components.Paint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShaderSet {
    private static final String ATTRIBUTES = "attributes";
    private static final Map<String, Map<String, Object>> AVAILBALBE_SHADERS = createMap();
    private static final String FRAGMENT = "fragment";
    private static final String PAINT_BLITWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLITWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }";
    private static final String PAINT_BRUSHLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { vec4 f = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = vec4(f.r * varIntensity, f.g, f.b, 0.0); }";
    private static final String PAINT_BRUSH_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { gl_FragColor = vec4(0, 0, 0, varIntensity * texture2D(texture, varTexcoord.st, 0.0).r); }";
    private static final String PAINT_BRUSH_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }";
    private static final String PAINT_COMPOSITEWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main(void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }";
    private static final String PAINT_COMPOSITEWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main(void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }";
    private static final String PAINT_NONPREMULTIPLIEDBLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); }";
    private static final String UNIFORMS = "uniforms";
    private static final String VERTEX = "vertex";

    private static Map<String, Map<String, Object>> createMap() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        Map<String, Object> shader = new HashMap<>();
        shader.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }");
        shader.put("fragment", "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { gl_FragColor = vec4(0, 0, 0, varIntensity * texture2D(texture, varTexcoord.st, 0.0).r); }");
        shader.put("attributes", new String[]{"inPosition", "inTexcoord", "alpha"});
        shader.put("uniforms", new String[]{"mvpMatrix", "texture"});
        result.put("brush", Collections.unmodifiableMap(shader));
        Map<String, Object> shader2 = new HashMap<>();
        shader2.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }");
        shader2.put("fragment", "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { vec4 f = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = vec4(f.r * varIntensity, f.g, f.b, 0.0); }");
        shader2.put("attributes", new String[]{"inPosition", "inTexcoord", "alpha"});
        shader2.put("uniforms", new String[]{"mvpMatrix", "texture"});
        result.put("brushLight", Collections.unmodifiableMap(shader2));
        Map<String, Object> shader3 = new HashMap<>();
        shader3.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }");
        shader3.put("fragment", "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor.rgb *= gl_FragColor.a; }");
        shader3.put("attributes", new String[]{"inPosition", "inTexcoord"});
        shader3.put("uniforms", new String[]{"mvpMatrix", "texture"});
        result.put("blit", Collections.unmodifiableMap(shader3));
        Map<String, Object> shader4 = new HashMap<>();
        shader4.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }");
        shader4.put("fragment", "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }");
        shader4.put("attributes", new String[]{"inPosition", "inTexcoord"});
        shader4.put("uniforms", new String[]{"mvpMatrix", "texture", "mask", "color"});
        result.put("blitWithMaskLight", Collections.unmodifiableMap(shader4));
        Map<String, Object> shader5 = new HashMap<>();
        shader5.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }");
        shader5.put("fragment", "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }");
        shader5.put("attributes", new String[]{"inPosition", "inTexcoord"});
        shader5.put("uniforms", new String[]{"mvpMatrix", "texture", "mask", "color"});
        result.put("blitWithMask", Collections.unmodifiableMap(shader5));
        Map<String, Object> shader6 = new HashMap<>();
        shader6.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }");
        shader6.put("fragment", "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main(void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }");
        shader6.put("attributes", new String[]{"inPosition", "inTexcoord"});
        shader6.put("uniforms", new String[]{"mvpMatrix", "texture", "mask", "color"});
        result.put("compositeWithMask", Collections.unmodifiableMap(shader6));
        Map<String, Object> shader7 = new HashMap<>();
        shader7.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }");
        shader7.put("fragment", "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main(void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }");
        shader7.put("attributes", new String[]{"inPosition", "inTexcoord"});
        shader7.put("uniforms", new String[]{"mvpMatrix", "texture", "mask", "color"});
        result.put("compositeWithMaskLight", Collections.unmodifiableMap(shader7));
        Map<String, Object> shader8 = new HashMap<>();
        shader8.put("vertex", "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }");
        shader8.put("fragment", "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); }");
        shader8.put("attributes", new String[]{"inPosition", "inTexcoord"});
        shader8.put("uniforms", new String[]{"mvpMatrix", "texture"});
        result.put("nonPremultipliedBlit", Collections.unmodifiableMap(shader8));
        return Collections.unmodifiableMap(result);
    }

    public static Map<String, Shader> setup() {
        Map<String, Shader> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : AVAILBALBE_SHADERS.entrySet()) {
            Map<String, Object> value = entry.getValue();
            result.put(entry.getKey(), new Shader((String) value.get("vertex"), (String) value.get("fragment"), (String[]) value.get("attributes"), (String[]) value.get("uniforms")));
        }
        return Collections.unmodifiableMap(result);
    }
}
