package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActionDrawable;

public class TestActivity extends BaseFragment {
    int num = 0;
    int p = 0;

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle("Test");
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    TestActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-16777216);
        this.fragmentView = frameLayout;
        MediaActionDrawable mediaActionDrawable = new MediaActionDrawable();
        mediaActionDrawable.setIcon(2, false);
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(mediaActionDrawable);
        imageView.getClass();
        mediaActionDrawable.setDelegate(new -$$Lambda$pmzqDjiJ3K2EPQb0Rq1MYHdTzL0(imageView));
        frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 17));
        frameLayout.setOnClickListener(new -$$Lambda$TestActivity$8SA6jL3MHG2fMuwJNTKXrp0KhUw(mediaActionDrawable));
        return this.fragmentView;
    }

    static /* synthetic */ void lambda$createView$0(MediaActionDrawable mediaActionDrawable, View view) {
        int currentIcon = mediaActionDrawable.getCurrentIcon();
        boolean z = true;
        if (currentIcon == 2) {
            currentIcon = 3;
        } else if (currentIcon == 3) {
            currentIcon = 0;
        } else if (currentIcon == 0) {
            currentIcon = 2;
            z = false;
        }
        mediaActionDrawable.setIcon(currentIcon, z);
    }
}
