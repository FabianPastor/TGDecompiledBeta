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

    public SimpleFloatPropertyCompat(String name, Getter<T> getter2, Setter<T> setter2) {
        super(name);
        this.getter = getter2;
        this.setter = setter2;
    }

    public SimpleFloatPropertyCompat<T> setMultiplier(float multiplier2) {
        this.multiplier = multiplier2;
        return this;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public float getValue(T object) {
        return this.getter.get(object) * this.multiplier;
    }

    public void setValue(T object, float value) {
        this.setter.set(object, value / this.multiplier);
    }
}
