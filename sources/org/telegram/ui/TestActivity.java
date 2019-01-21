package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActionDrawable;

public class TestActivity extends BaseFragment {
    int num = 0;
    int p = 0;

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle("Test");
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TestActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-16777216);
        this.fragmentView = frameLayout;
        MediaActionDrawable actionDrawable2 = new MediaActionDrawable();
        actionDrawable2.setIcon(2, false);
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(actionDrawable2);
        imageView.getClass();
        actionDrawable2.setDelegate(TestActivity$$Lambda$0.get$Lambda(imageView));
        frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 17));
        frameLayout.setOnClickListener(new TestActivity$$Lambda$1(actionDrawable2));
        return this.fragmentView;
    }

    static final /* synthetic */ void lambda$createView$0$TestActivity(MediaActionDrawable actionDrawable2, View v) {
        int icon = actionDrawable2.getCurrentIcon();
        boolean animated = true;
        if (icon == 2) {
            icon = 3;
        } else if (icon == 3) {
            icon = 0;
        } else if (icon == 0) {
            icon = 2;
            animated = false;
        }
        actionDrawable2.setIcon(icon, animated);
    }
}
