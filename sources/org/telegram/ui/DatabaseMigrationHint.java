package org.telegram.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class DatabaseMigrationHint extends FrameLayout {
    LinearLayout container;
    private final int currentAccount;
    TextView description1;
    TextView description2;
    RLottieImageView stickerView;
    TextView title;

    public DatabaseMigrationHint(Context context, int currentAccount2) {
        super(context);
        this.currentAccount = currentAccount2;
        LinearLayout linearLayout = new LinearLayout(context);
        this.container = linearLayout;
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.stickerView = rLottieImageView;
        rLottieImageView.setAnimation(NUM, 150, 150);
        this.stickerView.getAnimatedDrawable().setAutoRepeat(1);
        this.stickerView.playAnimation();
        this.container.addView(this.stickerView, LayoutHelper.createLinear(150, 150, 1));
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTextSize(1, 24.0f);
        this.title.setText(LocaleController.getString("OptimizingTelegram", NUM));
        this.title.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.title.setGravity(1);
        this.container.addView(this.title, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 50, 32, 50, 0));
        TextView textView2 = new TextView(context);
        this.description1 = textView2;
        textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.description1.setTextSize(1, 14.0f);
        this.description1.setText(LocaleController.getString("OptimizingTelegramDescription1", NUM));
        this.description1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.description1.setGravity(1);
        this.container.addView(this.description1, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 36, 20, 36, 0));
        TextView textView3 = new TextView(context);
        this.description2 = textView3;
        textView3.setTextSize(1, 14.0f);
        this.description2.setText(LocaleController.getString("OptimizingTelegramDescription2", NUM));
        this.description2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.description2.setGravity(1);
        this.container.addView(this.description2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 36, 24, 36, 0));
        addView(this.container, LayoutHelper.createFrame(-1, -2, 16));
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }
}
