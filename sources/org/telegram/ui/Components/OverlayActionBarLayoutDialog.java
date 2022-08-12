package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class OverlayActionBarLayoutDialog extends Dialog implements ActionBarLayout.ActionBarLayoutDelegate {
    private ActionBarLayout actionBarLayout;
    private FrameLayout frameLayout;
    private Theme.ResourcesProvider resourcesProvider;

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout2) {
        return true;
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout2) {
        return true;
    }

    public boolean onPreIme() {
        return false;
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout2, boolean z) {
    }

    public OverlayActionBarLayoutDialog(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context, R.style.TransparentDialog);
        this.resourcesProvider = resourcesProvider2;
        AnonymousClass1 r9 = new ActionBarLayout(this, context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (!AndroidUtilities.isTablet() || AndroidUtilities.isInMultiwindow || AndroidUtilities.isSmallTablet()) {
                    super.onMeasure(i, i2);
                } else {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), View.MeasureSpec.getSize(i)), NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0f), View.MeasureSpec.getSize(i2)), NUM));
                }
            }
        };
        this.actionBarLayout = r9;
        r9.init(new ArrayList());
        this.actionBarLayout.presentFragment(new EmptyFragment(), false, true, false, false);
        this.actionBarLayout.setDelegate(this);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.frameLayout.addView(this.actionBarLayout, new FrameLayout.LayoutParams(-1, -1, 17));
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
            this.frameLayout.setBackgroundColor(-NUM);
            this.frameLayout.setOnClickListener(new OverlayActionBarLayoutDialog$$ExternalSyntheticLambda1(this));
            this.actionBarLayout.setRemoveActionBarExtraHeight(true);
            VerticalPositionAutoAnimator.attach(this.actionBarLayout);
        }
        setContentView(this.frameLayout);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            window.addFlags(-NUM);
        } else if (i >= 21) {
            window.addFlags(-NUM);
        }
        window.setWindowAnimations(R.style.DialogNoAnimation);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        attributes.flags &= -3;
        attributes.softInputMode = 16;
        attributes.height = -1;
        boolean z = true;
        if (i >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
        if (i >= 23) {
            window.setStatusBarColor(0);
        }
        this.frameLayout.setSystemUiVisibility(1280);
        if (i >= 21) {
            this.frameLayout.setOnApplyWindowInsetsListener(OverlayActionBarLayoutDialog$$ExternalSyntheticLambda0.INSTANCE);
        }
        if (i >= 26) {
            if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
    }

    public void addFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment, AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet());
    }

    public void onBackPressed() {
        this.actionBarLayout.onBackPressed();
        if (this.actionBarLayout.fragmentsStack.size() <= 1) {
            dismiss();
        }
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout2) {
        if (actionBarLayout2.fragmentsStack.size() <= 1) {
            dismiss();
        }
        return true;
    }

    private static final class EmptyFragment extends BaseFragment {
        private EmptyFragment() {
        }

        public View createView(Context context) {
            this.actionBar.setAddToContainer(false);
            View view = new View(context);
            view.setBackgroundColor(0);
            return view;
        }
    }
}
