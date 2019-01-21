package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.Property;
import org.telegram.ui.PhotoViewer;

public class AnimationProperties {
    public static final Property<ClippingImageView, Float> CLIPPING_IMAGE_VIEW_PROGRESS = new FloatProperty<ClippingImageView>("animationProgress") {
        public void setValue(ClippingImageView object, float value) {
            object.setAnimationProgress(value);
        }

        public Float get(ClippingImageView object) {
            return Float.valueOf(object.getAnimationProgress());
        }
    };
    public static final Property<ColorDrawable, Integer> COLOR_DRAWABLE_ALPHA = new IntProperty<ColorDrawable>("alpha") {
        public void setValue(ColorDrawable object, int value) {
            object.setAlpha(value);
        }

        public Integer get(ColorDrawable object) {
            return Integer.valueOf(object.getAlpha());
        }
    };
    public static final Property<Paint, Integer> PAINT_ALPHA = new IntProperty<Paint>("alpha") {
        public void setValue(Paint object, int value) {
            object.setAlpha(value);
        }

        public Integer get(Paint object) {
            return Integer.valueOf(object.getAlpha());
        }
    };
    public static final Property<PhotoViewer, Float> PHOTO_VIEWER_ANIMATION_VALUE = new FloatProperty<PhotoViewer>("animationValue") {
        public void setValue(PhotoViewer object, float value) {
            object.setAnimationValue(value);
        }

        public Float get(PhotoViewer object) {
            return Float.valueOf(object.getAnimationValue());
        }
    };

    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public abstract void setValue(T t, float f);

        public FloatProperty(String name) {
            super(Float.class, name);
        }

        public final void set(T object, Float value) {
            setValue(object, value.floatValue());
        }
    }

    public static abstract class IntProperty<T> extends Property<T, Integer> {
        public abstract void setValue(T t, int i);

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        public final void set(T object, Integer value) {
            setValue(object, value.intValue());
        }
    }
}
