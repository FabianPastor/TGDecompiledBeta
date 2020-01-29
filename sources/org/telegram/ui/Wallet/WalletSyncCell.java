package org.telegram.ui.Wallet;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class WalletSyncCell extends FrameLayout {
    public WalletSyncCell(Context context) {
        super(context);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAutoRepeat(true);
        rLottieImageView.setAnimation(NUM, 112, 112);
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.playAnimation();
        addView(rLottieImageView, LayoutHelper.createFrame(-2, -2, 17));
    }
}
