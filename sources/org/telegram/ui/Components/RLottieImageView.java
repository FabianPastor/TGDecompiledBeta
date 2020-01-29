package org.telegram.ui.Components;

import android.content.Context;
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

    public void setLayerColor(String str, int i) {
        if (this.layerColors == null) {
            this.layerColors = new HashMap<>();
        }
        this.layerColors.put(str, Integer.valueOf(i));
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setLayerColor(str, i);
        }
    }

    public void replaceColors(int[] iArr) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.replaceColors(iArr);
        }
    }

    public void setAnimation(int i, int i2, int i3) {
        setAnimation(i, i2, i3, (int[]) null);
    }

    public void setAnimation(int i, int i2, int i3, int[] iArr) {
        this.drawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp((float) i2), AndroidUtilities.dp((float) i3), false, iArr);
        if (this.autoRepeat) {
            this.drawable.setAutoRepeat(1);
        }
        if (this.layerColors != null) {
            this.drawable.beginApplyLayerColors();
            for (Map.Entry next : this.layerColors.entrySet()) {
                this.drawable.setLayerColor((String) next.getKey(), ((Integer) next.getValue()).intValue());
            }
            this.drawable.commitApplyLayerColors();
        }
        this.drawable.setAllowDecodeSingleFrame(true);
        setImageDrawable(this.drawable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        RLottieDrawable rLottieDrawable;
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        if (this.playing && (rLottieDrawable = this.drawable) != null) {
            rLottieDrawable.start();
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

    public void setAutoRepeat(boolean z) {
        this.autoRepeat = z;
    }

    public void setProgress(float f) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setProgress(f);
        }
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
}
