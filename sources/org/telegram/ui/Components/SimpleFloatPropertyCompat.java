package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.FloatPropertyCompat;

public class SimpleFloatPropertyCompat<T> extends FloatPropertyCompat<T> {
    private Getter<T> getter;
    private float multiplier = 1.0f;
    private Setter<T> setter;

    public interface Getter<T> {
        float get(T t);
    }

    public interface Setter<T> {
        void set(T t, float f);
    }

    public SimpleFloatPropertyCompat(String str, Getter<T> getter2, Setter<T> setter2) {
        super(str);
        this.getter = getter2;
        this.setter = setter2;
    }

    public SimpleFloatPropertyCompat<T> setMultiplier(float f) {
        this.multiplier = f;
        return this;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public float getValue(T t) {
        return this.getter.get(t) * this.multiplier;
    }

    public void setValue(T t, float f) {
        this.setter.set(t, f / this.multiplier);
    }
}
