package org.telegram.ui.Components;

import android.content.Context;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;

public class RLottieImageView extends ImageView {
    private RLottieDrawable drawable;
    private HashMap<String, Integer> layerColors;

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

    public void setProgress(float f) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setProgress(f);
        }
    }

    public void playAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.start();
        }
    }
}
