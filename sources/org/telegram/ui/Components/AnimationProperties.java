package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Property;
import android.view.animation.OvershootInterpolator;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes3.dex */
public class AnimationProperties {
    public static final Property<ClippingImageView, Float> CLIPPING_IMAGE_VIEW_PROGRESS;
    public static final Property<DialogCell, Float> CLIP_DIALOG_CELL_PROGRESS;
    public static final Property<ColorDrawable, Integer> COLOR_DRAWABLE_ALPHA;
    public static final Property<PhotoViewer, Float> PHOTO_VIEWER_ANIMATION_VALUE;
    public static final Property<ShapeDrawable, Integer> SHAPE_DRAWABLE_ALPHA;
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator(1.9f);
    public static final Property<Paint, Integer> PAINT_ALPHA = new IntProperty<Paint>("alpha") { // from class: org.telegram.ui.Components.AnimationProperties.1
        @Override // org.telegram.ui.Components.AnimationProperties.IntProperty
        public void setValue(Paint paint, int i) {
            paint.setAlpha(i);
        }

        @Override // android.util.Property
        public Integer get(Paint paint) {
            return Integer.valueOf(paint.getAlpha());
        }
    };

    static {
        new IntProperty<Paint>("color") { // from class: org.telegram.ui.Components.AnimationProperties.2
            @Override // org.telegram.ui.Components.AnimationProperties.IntProperty
            public void setValue(Paint paint, int i) {
                paint.setColor(i);
            }

            @Override // android.util.Property
            public Integer get(Paint paint) {
                return Integer.valueOf(paint.getColor());
            }
        };
        COLOR_DRAWABLE_ALPHA = new IntProperty<ColorDrawable>("alpha") { // from class: org.telegram.ui.Components.AnimationProperties.3
            @Override // org.telegram.ui.Components.AnimationProperties.IntProperty
            public void setValue(ColorDrawable colorDrawable, int i) {
                colorDrawable.setAlpha(i);
            }

            @Override // android.util.Property
            public Integer get(ColorDrawable colorDrawable) {
                return Integer.valueOf(colorDrawable.getAlpha());
            }
        };
        SHAPE_DRAWABLE_ALPHA = new IntProperty<ShapeDrawable>("alpha") { // from class: org.telegram.ui.Components.AnimationProperties.4
            @Override // org.telegram.ui.Components.AnimationProperties.IntProperty
            public void setValue(ShapeDrawable shapeDrawable, int i) {
                shapeDrawable.getPaint().setAlpha(i);
            }

            @Override // android.util.Property
            public Integer get(ShapeDrawable shapeDrawable) {
                return Integer.valueOf(shapeDrawable.getPaint().getAlpha());
            }
        };
        CLIPPING_IMAGE_VIEW_PROGRESS = new FloatProperty<ClippingImageView>("animationProgress") { // from class: org.telegram.ui.Components.AnimationProperties.5
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(ClippingImageView clippingImageView, float f) {
                clippingImageView.setAnimationProgress(f);
            }

            @Override // android.util.Property
            public Float get(ClippingImageView clippingImageView) {
                return Float.valueOf(clippingImageView.getAnimationProgress());
            }
        };
        PHOTO_VIEWER_ANIMATION_VALUE = new FloatProperty<PhotoViewer>("animationValue") { // from class: org.telegram.ui.Components.AnimationProperties.6
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(PhotoViewer photoViewer, float f) {
                photoViewer.setAnimationValue(f);
            }

            @Override // android.util.Property
            public Float get(PhotoViewer photoViewer) {
                return Float.valueOf(photoViewer.getAnimationValue());
            }
        };
        CLIP_DIALOG_CELL_PROGRESS = new FloatProperty<DialogCell>("clipProgress") { // from class: org.telegram.ui.Components.AnimationProperties.7
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(DialogCell dialogCell, float f) {
                dialogCell.setClipProgress(f);
            }

            @Override // android.util.Property
            public Float get(DialogCell dialogCell) {
                return Float.valueOf(dialogCell.getClipProgress());
            }
        };
    }

    /* loaded from: classes3.dex */
    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public abstract void setValue(T t, float f);

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.util.Property
        public /* bridge */ /* synthetic */ void set(Object obj, Float f) {
            set2((FloatProperty<T>) obj, f);
        }

        public FloatProperty(String str) {
            super(Float.class, str);
        }

        /* renamed from: set  reason: avoid collision after fix types in other method */
        public final void set2(T t, Float f) {
            setValue(t, f.floatValue());
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class IntProperty<T> extends Property<T, Integer> {
        public abstract void setValue(T t, int i);

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.util.Property
        public /* bridge */ /* synthetic */ void set(Object obj, Integer num) {
            set2((IntProperty<T>) obj, num);
        }

        public IntProperty(String str) {
            super(Integer.class, str);
        }

        /* renamed from: set  reason: avoid collision after fix types in other method */
        public final void set2(T t, Integer num) {
            setValue(t, num.intValue());
        }
    }
}
