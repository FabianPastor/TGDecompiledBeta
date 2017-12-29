package android.support.v4.app;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;

public class ActivityOptionsCompat {

    private static class ActivityOptionsCompatApi16Impl extends ActivityOptionsCompat {
        protected final ActivityOptions mActivityOptions;

        ActivityOptionsCompatApi16Impl(ActivityOptions activityOptions) {
            this.mActivityOptions = activityOptions;
        }

        public Bundle toBundle() {
            return this.mActivityOptions.toBundle();
        }
    }

    private static class ActivityOptionsCompatApi23Impl extends ActivityOptionsCompatApi16Impl {
        ActivityOptionsCompatApi23Impl(ActivityOptions activityOptions) {
            super(activityOptions);
        }
    }

    private static class ActivityOptionsCompatApi24Impl extends ActivityOptionsCompatApi23Impl {
        ActivityOptionsCompatApi24Impl(ActivityOptions activityOptions) {
            super(activityOptions);
        }
    }

    public static ActivityOptionsCompat makeCustomAnimation(Context context, int enterResId, int exitResId) {
        if (VERSION.SDK_INT >= 16) {
            return createImpl(ActivityOptions.makeCustomAnimation(context, enterResId, exitResId));
        }
        return new ActivityOptionsCompat();
    }

    private static ActivityOptionsCompat createImpl(ActivityOptions options) {
        if (VERSION.SDK_INT >= 24) {
            return new ActivityOptionsCompatApi24Impl(options);
        }
        if (VERSION.SDK_INT >= 23) {
            return new ActivityOptionsCompatApi23Impl(options);
        }
        return new ActivityOptionsCompatApi16Impl(options);
    }

    protected ActivityOptionsCompat() {
    }

    public Bundle toBundle() {
        return null;
    }
}
