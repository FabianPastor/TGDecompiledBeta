package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
/* loaded from: classes3.dex */
public class SimpleFloatPropertyCompat<T> extends FloatPropertyCompat<T> {
    private Getter<T> getter;
    private float multiplier;
    private Setter<T> setter;

    /* loaded from: classes3.dex */
    public interface Getter<T> {
        float get(T t);
    }

    /* loaded from: classes3.dex */
    public interface Setter<T> {
        void set(T t, float f);
    }

    public SimpleFloatPropertyCompat(String str, Getter<T> getter, Setter<T> setter) {
        super(str);
        this.multiplier = 1.0f;
        this.getter = getter;
        this.setter = setter;
    }

    public SimpleFloatPropertyCompat<T> setMultiplier(float f) {
        this.multiplier = f;
        return this;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
    public float getValue(T t) {
        return this.getter.get(t) * this.multiplier;
    }

    @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
    public void setValue(T t, float f) {
        this.setter.set(t, f / this.multiplier);
    }
}
