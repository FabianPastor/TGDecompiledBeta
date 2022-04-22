package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class PathAnimator {
    private float durationScale;
    private ArrayList<KeyFrame> keyFrames = new ArrayList<>();
    private Path path = new Path();
    private float pathTime = -1.0f;
    private float scale;
    private float tx;
    private float ty;

    private static class KeyFrame {
        public ArrayList<Object> commands;
        public float time;

        private KeyFrame() {
            this.commands = new ArrayList<>();
        }
    }

    private static class MoveTo {
        public float x;
        public float y;

        private MoveTo() {
        }
    }

    private static class LineTo {
        public float x;
        public float y;

        private LineTo() {
        }
    }

    private static class CurveTo {
        public float x;
        public float x1;
        public float x2;
        public float y;
        public float y1;
        public float y2;

        private CurveTo() {
        }
    }

    public PathAnimator(float f, float f2, float f3, float f4) {
        this.scale = f;
        this.tx = f2;
        this.ty = f3;
        this.durationScale = f4;
    }

    public void addSvgKeyFrame(String str, float f) {
        if (str != null) {
            try {
                KeyFrame keyFrame = new KeyFrame();
                keyFrame.time = f * this.durationScale;
                String[] split = str.split(" ");
                int i = 0;
                while (i < split.length) {
                    char charAt = split[i].charAt(0);
                    if (charAt == 'C') {
                        CurveTo curveTo = new CurveTo();
                        curveTo.x1 = (Float.parseFloat(split[i + 1]) + this.tx) * this.scale;
                        curveTo.y1 = (Float.parseFloat(split[i + 2]) + this.ty) * this.scale;
                        curveTo.x2 = (Float.parseFloat(split[i + 3]) + this.tx) * this.scale;
                        curveTo.y2 = (Float.parseFloat(split[i + 4]) + this.ty) * this.scale;
                        curveTo.x = (Float.parseFloat(split[i + 5]) + this.tx) * this.scale;
                        i += 6;
                        curveTo.y = (Float.parseFloat(split[i]) + this.ty) * this.scale;
                        keyFrame.commands.add(curveTo);
                    } else if (charAt == 'L') {
                        LineTo lineTo = new LineTo();
                        lineTo.x = (Float.parseFloat(split[i + 1]) + this.tx) * this.scale;
                        i += 2;
                        lineTo.y = (Float.parseFloat(split[i]) + this.ty) * this.scale;
                        keyFrame.commands.add(lineTo);
                    } else if (charAt == 'M') {
                        MoveTo moveTo = new MoveTo();
                        moveTo.x = (Float.parseFloat(split[i + 1]) + this.tx) * this.scale;
                        i += 2;
                        moveTo.y = (Float.parseFloat(split[i]) + this.ty) * this.scale;
                        keyFrame.commands.add(moveTo);
                    }
                    i++;
                }
                this.keyFrames.add(keyFrame);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void draw(Canvas canvas, Paint paint, float f) {
        float f2;
        float f3 = f;
        if (this.pathTime != f3) {
            this.pathTime = f3;
            int size = this.keyFrames.size();
            int i = 0;
            KeyFrame keyFrame = null;
            KeyFrame keyFrame2 = null;
            for (int i2 = 0; i2 < size; i2++) {
                KeyFrame keyFrame3 = this.keyFrames.get(i2);
                if ((keyFrame2 == null || keyFrame2.time < keyFrame3.time) && keyFrame3.time <= f3) {
                    keyFrame2 = keyFrame3;
                }
                if ((keyFrame == null || keyFrame.time > keyFrame3.time) && keyFrame3.time >= f3) {
                    keyFrame = keyFrame3;
                }
            }
            if (keyFrame == keyFrame2) {
                keyFrame2 = null;
            }
            if (keyFrame2 != null && keyFrame == null) {
                keyFrame = keyFrame2;
                keyFrame2 = null;
            }
            if (keyFrame == null) {
                return;
            }
            if (keyFrame2 == null || keyFrame2.commands.size() == keyFrame.commands.size()) {
                this.path.reset();
                int size2 = keyFrame.commands.size();
                while (i < size2) {
                    Object obj = keyFrame2 != null ? keyFrame2.commands.get(i) : null;
                    Object obj2 = keyFrame.commands.get(i);
                    if (obj == null || obj.getClass() == obj2.getClass()) {
                        if (keyFrame2 != null) {
                            float f4 = keyFrame2.time;
                            f2 = (f3 - f4) / (keyFrame.time - f4);
                        } else {
                            f2 = 1.0f;
                        }
                        if (obj2 instanceof MoveTo) {
                            MoveTo moveTo = (MoveTo) obj2;
                            MoveTo moveTo2 = (MoveTo) obj;
                            if (moveTo2 != null) {
                                Path path2 = this.path;
                                float f5 = moveTo2.x;
                                float dpf2 = AndroidUtilities.dpf2(f5 + ((moveTo.x - f5) * f2));
                                float f6 = moveTo2.y;
                                path2.moveTo(dpf2, AndroidUtilities.dpf2(f6 + ((moveTo.y - f6) * f2)));
                            } else {
                                this.path.moveTo(AndroidUtilities.dpf2(moveTo.x), AndroidUtilities.dpf2(moveTo.y));
                            }
                        } else if (obj2 instanceof LineTo) {
                            LineTo lineTo = (LineTo) obj2;
                            LineTo lineTo2 = (LineTo) obj;
                            if (lineTo2 != null) {
                                Path path3 = this.path;
                                float f7 = lineTo2.x;
                                float dpvar_ = AndroidUtilities.dpf2(f7 + ((lineTo.x - f7) * f2));
                                float f8 = lineTo2.y;
                                path3.lineTo(dpvar_, AndroidUtilities.dpf2(f8 + ((lineTo.y - f8) * f2)));
                            } else {
                                this.path.lineTo(AndroidUtilities.dpf2(lineTo.x), AndroidUtilities.dpf2(lineTo.y));
                            }
                        } else if (obj2 instanceof CurveTo) {
                            CurveTo curveTo = (CurveTo) obj2;
                            CurveTo curveTo2 = (CurveTo) obj;
                            if (curveTo2 != null) {
                                Path path4 = this.path;
                                float f9 = curveTo2.x1;
                                float dpvar_ = AndroidUtilities.dpf2(f9 + ((curveTo.x1 - f9) * f2));
                                float var_ = curveTo2.y1;
                                float dpvar_ = AndroidUtilities.dpf2(var_ + ((curveTo.y1 - var_) * f2));
                                float var_ = curveTo2.x2;
                                float dpvar_ = AndroidUtilities.dpf2(var_ + ((curveTo.x2 - var_) * f2));
                                float var_ = curveTo2.y2;
                                float dpvar_ = AndroidUtilities.dpf2(var_ + ((curveTo.y2 - var_) * f2));
                                float var_ = curveTo2.x;
                                float dpvar_ = AndroidUtilities.dpf2(var_ + ((curveTo.x - var_) * f2));
                                float var_ = curveTo2.y;
                                path4.cubicTo(dpvar_, dpvar_, dpvar_, dpvar_, dpvar_, AndroidUtilities.dpf2(var_ + ((curveTo.y - var_) * f2)));
                            } else {
                                this.path.cubicTo(AndroidUtilities.dpf2(curveTo.x1), AndroidUtilities.dpf2(curveTo.y1), AndroidUtilities.dpf2(curveTo.x2), AndroidUtilities.dpf2(curveTo.y2), AndroidUtilities.dpf2(curveTo.x), AndroidUtilities.dpf2(curveTo.y));
                            }
                        }
                        i++;
                    } else {
                        return;
                    }
                }
                this.path.close();
            } else {
                return;
            }
        }
        canvas.drawPath(this.path, paint);
    }
}
