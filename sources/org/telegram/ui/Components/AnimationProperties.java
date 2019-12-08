package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Property;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.PhotoViewer;

public class AnimationProperties {
    public static final Property<ClippingImageView, Float> CLIPPING_IMAGE_VIEW_PROGRESS = new FloatProperty<ClippingImageView>("animationProgress") {
        public void setValue(ClippingImageView clippingImageView, float f) {
            clippingImageView.setAnimationProgress(f);
        }

        public Float get(ClippingImageView clippingImageView) {
            return Float.valueOf(clippingImageView.getAnimationProgress());
        }
    };
    public static final Property<DialogCell, Float> CLIP_DIALOG_CELL_PROGRESS = new FloatProperty<DialogCell>("clipProgress") {
        public void setValue(DialogCell dialogCell, float f) {
            dialogCell.setClipProgress(f);
        }

        public Float get(DialogCell dialogCell) {
            return Float.valueOf(dialogCell.getClipProgress());
        }
    };
    public static final Property<ColorDrawable, Integer> COLOR_DRAWABLE_ALPHA;
    public static final Property<Paint, Integer> PAINT_ALPHA;
    public static final Property<PhotoViewer, Float> PHOTO_VIEWER_ANIMATION_VALUE = new FloatProperty<PhotoViewer>("animationValue") {
        public void setValue(PhotoViewer photoViewer, float f) {
            photoViewer.setAnimationValue(f);
        }

        public Float get(PhotoViewer photoViewer) {
            return Float.valueOf(photoViewer.getAnimationValue());
        }
    };
    public static final Property<ShapeDrawable, Integer> SHAPE_DRAWABLE_ALPHA;

    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public abstract void setValue(T t, float f);

        public FloatProperty(String str) {
            super(Float.class, str);
        }

        public final void set(T t, Float f) {
            setValue(t, f.floatValue());
        }
    }

    public static abstract class IntProperty<T> extends Property<T, Integer> {
        public abstract void setValue(T t, int i);

        public IntProperty(String str) {
            super(Integer.class, str);
        }

        public final void set(T t, Integer num) {
            setValue(t, num.intValue());
        }
    }

    static {
        String str = "alpha";
        PAINT_ALPHA = new IntProperty<Paint>(str) {
            public void setValue(Paint paint, int i) {
                paint.setAlpha(i);
            }

            public Integer get(Paint paint) {
                return Integer.valueOf(paint.getAlpha());
            }
        };
        COLOR_DRAWABLE_ALPHA = new IntProperty<ColorDrawable>(str) {
            public void setValue(ColorDrawable colorDrawable, int i) {
                colorDrawable.setAlpha(i);
            }

            public Integer get(ColorDrawable colorDrawable) {
                return Integer.valueOf(colorDrawable.getAlpha());
            }
        };
        SHAPE_DRAWABLE_ALPHA = new IntProperty<ShapeDrawable>(str) {
            public void setValue(ShapeDrawable shapeDrawable, int i) {
                shapeDrawable.getPaint().setAlpha(i);
            }

            public Integer get(ShapeDrawable shapeDrawable) {
                return Integer.valueOf(shapeDrawable.getPaint().getAlpha());
            }
        };
    }
}
