package android.support.v4.animation;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo({Scope.GROUP_ID})
public interface AnimatorUpdateListenerCompat {
    void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat);
}
