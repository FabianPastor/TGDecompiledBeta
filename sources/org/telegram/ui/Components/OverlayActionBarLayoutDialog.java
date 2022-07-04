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
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class OverlayActionBarLayoutDialog extends Dialog implements ActionBarLayout.ActionBarLayoutDelegate {
    private ActionBarLayout actionBarLayout;
    private FrameLayout frameLayout;
    private Theme.ResourcesProvider resourcesProvider;

    public OverlayActionBarLayoutDialog(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context, NUM);
        this.resourcesProvider = resourcesProvider2;
        AnonymousClass1 r0 = new ActionBarLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (!AndroidUtilities.isTablet() || AndroidUtilities.isInMultiwindow || AndroidUtilities.isSmallTablet()) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                } else {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), View.MeasureSpec.getSize(widthMeasureSpec)), NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0f), View.MeasureSpec.getSize(heightMeasureSpec)), NUM));
                }
            }
        };
        this.actionBarLayout = r0;
        r0.init(new ArrayList());
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-OverlayActionBarLayoutDialog  reason: not valid java name */
    public /* synthetic */ void m1118x63034fab(View v) {
        onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            window.addFlags(-NUM);
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(-NUM);
        }
        window.setWindowAnimations(NUM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        params.softInputMode = 16;
        params.height = -1;
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(params);
        if (Build.VERSION.SDK_INT >= 23) {
            window.setStatusBarColor(0);
        }
        this.frameLayout.setSystemUiVisibility(1280);
        if (Build.VERSION.SDK_INT >= 21) {
            this.frameLayout.setOnApplyWindowInsetsListener(OverlayActionBarLayoutDialog$$ExternalSyntheticLambda0.INSTANCE);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
    }

    public void addFragment(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment, AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet());
    }

    public void onBackPressed() {
        this.actionBarLayout.onBackPressed();
        if (this.actionBarLayout.fragmentsStack.size() <= 1) {
            dismiss();
        }
    }

    public boolean onPreIme() {
        return false;
    }

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (layout.fragmentsStack.size() <= 1) {
            dismiss();
        }
        return true;
    }

    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
    }

    private static final class EmptyFragment extends BaseFragment {
        private EmptyFragment() {
        }

        public View createView(Context context) {
            this.actionBar.setAddToContainer(false);
            View v = new View(context);
            v.setBackgroundColor(0);
            return v;
        }
    }
}
