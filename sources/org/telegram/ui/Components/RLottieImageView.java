package org.telegram.ui.Components;

import android.content.Context;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;

public class RLottieImageView extends ImageView {
    private boolean attachedToWindow;
    private boolean autoRepeat;
    private RLottieDrawable drawable;
    private HashMap<String, Integer> layerColors;
    private boolean playing;

    public RLottieImageView(Context context) {
        super(context);
    }

    public void setLayerColor(String str, int i) {
        if (this.layerColors == null) {
            this.layerColors = new HashMap();
        }
        this.layerColors.put(str, Integer.valueOf(i));
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setLayerColor(str, i);
        }
    }

    public void setAnimation(int i, int i2, int i3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(i);
        this.drawable = new RLottieDrawable(i, stringBuilder.toString(), AndroidUtilities.dp((float) i2), AndroidUtilities.dp((float) i3), false);
        if (this.autoRepeat) {
            this.drawable.setAutoRepeat(1);
        }
        this.drawable.beginApplyLayerColors();
        HashMap hashMap = this.layerColors;
        if (hashMap != null) {
            for (Entry entry : hashMap.entrySet()) {
                this.drawable.setLayerColor((String) entry.getKey(), ((Integer) entry.getValue()).intValue());
            }
        }
        this.drawable.commitApplyLayerColors();
        this.drawable.setAllowDecodeSingleFrame(true);
        setImageDrawable(this.drawable);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        if (this.playing) {
            this.drawable.start();
        }
    }

    /* Access modifiers changed, original: protected */
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
            }
        }
    }
}
