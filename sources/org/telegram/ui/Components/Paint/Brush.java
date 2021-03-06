package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.telegram.messenger.ApplicationLoader;

public interface Brush {
    float getAlpha();

    float getAngle();

    float getScale();

    float getSpacing();

    Bitmap getStamp();

    boolean isLightSaber();

    public static class Radial implements Brush {
        public float getAlpha() {
            return 0.85f;
        }

        public float getAngle() {
            return 0.0f;
        }

        public float getScale() {
            return 1.0f;
        }

        public float getSpacing() {
            return 0.15f;
        }

        public boolean isLightSaber() {
            return false;
        }

        public Bitmap getStamp() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), NUM, options);
        }
    }

    public static class Elliptical implements Brush {
        public float getAlpha() {
            return 0.3f;
        }

        public float getScale() {
            return 1.5f;
        }

        public float getSpacing() {
            return 0.04f;
        }

        public boolean isLightSaber() {
            return false;
        }

        public float getAngle() {
            return (float) Math.toRadians(125.0d);
        }

        public Bitmap getStamp() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), NUM, options);
        }
    }

    public static class Neon implements Brush {
        public float getAlpha() {
            return 0.7f;
        }

        public float getAngle() {
            return 0.0f;
        }

        public float getScale() {
            return 1.45f;
        }

        public float getSpacing() {
            return 0.07f;
        }

        public boolean isLightSaber() {
            return true;
        }

        public Bitmap getStamp() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), NUM, options);
        }
    }

    public static class Arrow implements Brush {
        public float getAlpha() {
            return 0.85f;
        }

        public float getAngle() {
            return 0.0f;
        }

        public float getScale() {
            return 1.0f;
        }

        public float getSpacing() {
            return 0.15f;
        }

        public boolean isLightSaber() {
            return false;
        }

        public Bitmap getStamp() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), NUM, options);
        }
    }
}
