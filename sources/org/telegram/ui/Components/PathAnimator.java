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

    public PathAnimator(float sc, float x, float y, float dsc) {
        this.scale = sc;
        this.tx = x;
        this.ty = y;
        this.durationScale = dsc;
    }

    public float getDurationScale() {
        return this.durationScale;
    }

    public void addSvgKeyFrame(String svg, float ms) {
        if (svg != null) {
            try {
                KeyFrame keyFrame = new KeyFrame();
                keyFrame.time = this.durationScale * ms;
                String[] args = svg.split(" ");
                int a = 0;
                while (a < args.length) {
                    switch (args[a].charAt(0)) {
                        case 'C':
                            CurveTo curveTo = new CurveTo();
                            curveTo.x1 = (Float.parseFloat(args[a + 1]) + this.tx) * this.scale;
                            curveTo.y1 = (Float.parseFloat(args[a + 2]) + this.ty) * this.scale;
                            curveTo.x2 = (Float.parseFloat(args[a + 3]) + this.tx) * this.scale;
                            curveTo.y2 = (Float.parseFloat(args[a + 4]) + this.ty) * this.scale;
                            curveTo.x = (Float.parseFloat(args[a + 5]) + this.tx) * this.scale;
                            curveTo.y = (Float.parseFloat(args[a + 6]) + this.ty) * this.scale;
                            keyFrame.commands.add(curveTo);
                            a += 6;
                            break;
                        case 'L':
                            LineTo lineTo = new LineTo();
                            lineTo.x = (Float.parseFloat(args[a + 1]) + this.tx) * this.scale;
                            lineTo.y = (Float.parseFloat(args[a + 2]) + this.ty) * this.scale;
                            keyFrame.commands.add(lineTo);
                            a += 2;
                            break;
                        case 'M':
                            MoveTo moveTo = new MoveTo();
                            moveTo.x = (Float.parseFloat(args[a + 1]) + this.tx) * this.scale;
                            moveTo.y = (Float.parseFloat(args[a + 2]) + this.ty) * this.scale;
                            keyFrame.commands.add(moveTo);
                            a += 2;
                            break;
                    }
                    a++;
                }
                this.keyFrames.add(keyFrame);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void draw(Canvas canvas, Paint paint, float time) {
        float progress;
        KeyFrame endKeyFrame;
        KeyFrame startKeyFrame;
        float f = time;
        if (this.pathTime != f) {
            this.pathTime = f;
            KeyFrame startKeyFrame2 = null;
            KeyFrame endKeyFrame2 = null;
            int N = this.keyFrames.size();
            for (int a = 0; a < N; a++) {
                KeyFrame keyFrame = this.keyFrames.get(a);
                if ((startKeyFrame2 == null || startKeyFrame2.time < keyFrame.time) && keyFrame.time <= f) {
                    startKeyFrame2 = keyFrame;
                }
                if ((endKeyFrame2 == null || endKeyFrame2.time > keyFrame.time) && keyFrame.time >= f) {
                    endKeyFrame2 = keyFrame;
                }
            }
            if (endKeyFrame2 == startKeyFrame2) {
                startKeyFrame2 = null;
            }
            if (startKeyFrame2 != null && endKeyFrame2 == null) {
                endKeyFrame2 = startKeyFrame2;
                startKeyFrame2 = null;
            }
            if (endKeyFrame2 == null) {
                KeyFrame keyFrame2 = endKeyFrame2;
                return;
            } else if (startKeyFrame2 == null || startKeyFrame2.commands.size() == endKeyFrame2.commands.size()) {
                this.path.reset();
                int a2 = 0;
                int N2 = endKeyFrame2.commands.size();
                while (a2 < N2) {
                    Object startCommand = startKeyFrame2 != null ? startKeyFrame2.commands.get(a2) : null;
                    Object endCommand = endKeyFrame2.commands.get(a2);
                    if (startCommand == null || startCommand.getClass() == endCommand.getClass()) {
                        if (startKeyFrame2 != null) {
                            progress = (f - startKeyFrame2.time) / (endKeyFrame2.time - startKeyFrame2.time);
                        } else {
                            progress = 1.0f;
                        }
                        if (endCommand instanceof MoveTo) {
                            MoveTo end = (MoveTo) endCommand;
                            MoveTo start = (MoveTo) startCommand;
                            if (start != null) {
                                this.path.moveTo(AndroidUtilities.dpf2(start.x + ((end.x - start.x) * progress)), AndroidUtilities.dpf2(start.y + ((end.y - start.y) * progress)));
                            } else {
                                this.path.moveTo(AndroidUtilities.dpf2(end.x), AndroidUtilities.dpf2(end.y));
                            }
                            startKeyFrame = startKeyFrame2;
                            endKeyFrame = endKeyFrame2;
                        } else if (endCommand instanceof LineTo) {
                            LineTo end2 = (LineTo) endCommand;
                            LineTo start2 = (LineTo) startCommand;
                            if (start2 != null) {
                                this.path.lineTo(AndroidUtilities.dpf2(start2.x + ((end2.x - start2.x) * progress)), AndroidUtilities.dpf2(start2.y + ((end2.y - start2.y) * progress)));
                            } else {
                                this.path.lineTo(AndroidUtilities.dpf2(end2.x), AndroidUtilities.dpf2(end2.y));
                            }
                            startKeyFrame = startKeyFrame2;
                            endKeyFrame = endKeyFrame2;
                        } else if (endCommand instanceof CurveTo) {
                            CurveTo end3 = (CurveTo) endCommand;
                            CurveTo start3 = (CurveTo) startCommand;
                            if (start3 != null) {
                                startKeyFrame = startKeyFrame2;
                                endKeyFrame = endKeyFrame2;
                                this.path.cubicTo(AndroidUtilities.dpf2(start3.x1 + ((end3.x1 - start3.x1) * progress)), AndroidUtilities.dpf2(start3.y1 + ((end3.y1 - start3.y1) * progress)), AndroidUtilities.dpf2(start3.x2 + ((end3.x2 - start3.x2) * progress)), AndroidUtilities.dpf2(start3.y2 + ((end3.y2 - start3.y2) * progress)), AndroidUtilities.dpf2(start3.x + ((end3.x - start3.x) * progress)), AndroidUtilities.dpf2(start3.y + ((end3.y - start3.y) * progress)));
                            } else {
                                startKeyFrame = startKeyFrame2;
                                endKeyFrame = endKeyFrame2;
                                this.path.cubicTo(AndroidUtilities.dpf2(end3.x1), AndroidUtilities.dpf2(end3.y1), AndroidUtilities.dpf2(end3.x2), AndroidUtilities.dpf2(end3.y2), AndroidUtilities.dpf2(end3.x), AndroidUtilities.dpf2(end3.y));
                            }
                        } else {
                            startKeyFrame = startKeyFrame2;
                            endKeyFrame = endKeyFrame2;
                        }
                        a2++;
                        f = time;
                        startKeyFrame2 = startKeyFrame;
                        endKeyFrame2 = endKeyFrame;
                    } else {
                        return;
                    }
                }
                KeyFrame keyFrame3 = endKeyFrame2;
                this.path.close();
            } else {
                KeyFrame keyFrame4 = startKeyFrame2;
                KeyFrame keyFrame5 = endKeyFrame2;
                return;
            }
        }
        canvas.drawPath(this.path, paint);
    }
}
