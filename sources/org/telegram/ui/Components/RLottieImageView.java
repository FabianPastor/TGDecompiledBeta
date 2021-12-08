package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;

public class RLottieImageView extends ImageView {
    private boolean attachedToWindow;
    private boolean autoRepeat;
    private RLottieDrawable drawable;
    private HashMap<String, Integer> layerColors;
    private boolean playing;
    private boolean startOnAttach;

    public RLottieImageView(Context context) {
        super(context);
    }

    public void clearLayerColors() {
        this.layerColors.clear();
    }

    public void setLayerColor(String layer, int color) {
        if (this.layerColors == null) {
            this.layerColors = new HashMap<>();
        }
        this.layerColors.put(layer, Integer.valueOf(color));
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setLayerColor(layer, color);
        }
    }

    public void replaceColors(int[] colors) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.replaceColors(colors);
        }
    }

    public void setAnimation(int resId, int w, int h) {
        setAnimation(resId, w, h, (int[]) null);
    }

    public void setAnimation(int resId, int w, int h, int[] colorReplacement) {
        setAnimation(new RLottieDrawable(resId, "" + resId, AndroidUtilities.dp((float) w), AndroidUtilities.dp((float) h), false, colorReplacement));
    }

    public void setAnimation(RLottieDrawable lottieDrawable) {
        this.drawable = lottieDrawable;
        if (this.autoRepeat) {
            lottieDrawable.setAutoRepeat(1);
        }
        if (this.layerColors != null) {
            this.drawable.beginApplyLayerColors();
            for (Map.Entry<String, Integer> entry : this.layerColors.entrySet()) {
                this.drawable.setLayerColor(entry.getKey(), entry.getValue().intValue());
            }
            this.drawable.commitApplyLayerColors();
        }
        this.drawable.setAllowDecodeSingleFrame(true);
        setImageDrawable(this.drawable);
    }

    public void clearAnimationDrawable() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
        }
        this.drawable = null;
        setImageDrawable((Drawable) null);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setCallback(this);
            if (this.playing) {
                this.drawable.start();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
        }
    }

    public boolean isPlaying() {
        RLottieDrawable rLottieDrawable = this.drawable;
        return rLottieDrawable != null && rLottieDrawable.isRunning();
    }

    public void setAutoRepeat(boolean repeat) {
        this.autoRepeat = repeat;
    }

    public void setProgress(float progress) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setProgress(progress);
        }
    }

    public void setImageResource(int resId) {
        super.setImageResource(resId);
        this.drawable = null;
    }

    public void playAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            this.playing = true;
            if (this.attachedToWindow) {
                rLottieDrawable.start();
            } else {
                this.startOnAttach = true;
            }
        }
    }

    public void stopAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            this.playing = false;
            if (this.attachedToWindow) {
                rLottieDrawable.stop();
            } else {
                this.startOnAttach = false;
            }
        }
    }

    public RLottieDrawable getAnimatedDrawable() {
        return this.drawable;
    }
}
