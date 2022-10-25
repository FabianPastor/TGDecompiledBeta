package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class OverlayActionBarLayoutDialog extends Dialog implements INavigationLayout.INavigationLayoutDelegate {
    private INavigationLayout actionBarLayout;
    private FrameLayout frameLayout;
    private Theme.ResourcesProvider resourcesProvider;

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needAddFragmentToStack(BaseFragment baseFragment, INavigationLayout iNavigationLayout) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, INavigationLayout iNavigationLayout) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ boolean needPresentFragment(INavigationLayout iNavigationLayout, INavigationLayout.NavigationParams navigationParams) {
        boolean needPresentFragment;
        needPresentFragment = needPresentFragment(navigationParams.fragment, navigationParams.removeLast, navigationParams.noAnimation, iNavigationLayout);
        return needPresentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean onPreIme() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public void onRebuildAllFragments(INavigationLayout iNavigationLayout, boolean z) {
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public /* synthetic */ void onThemeProgress(float f) {
        INavigationLayout.INavigationLayoutDelegate.CC.$default$onThemeProgress(this, f);
    }

    public OverlayActionBarLayoutDialog(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.resourcesProvider = resourcesProvider;
        INavigationLayout newLayout = INavigationLayout.CC.newLayout(context);
        this.actionBarLayout = newLayout;
        newLayout.setFragmentStack(new ArrayList());
        this.actionBarLayout.presentFragment(new INavigationLayout.NavigationParams(new EmptyFragment()).setNoAnimation(true));
        this.actionBarLayout.setDelegate(this);
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.frameLayout.addView(this.actionBarLayout.getView(), new FrameLayout.LayoutParams(-1, -1, 17));
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
            this.frameLayout.setBackgroundColor(-NUM);
            this.frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.OverlayActionBarLayoutDialog$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    OverlayActionBarLayoutDialog.this.lambda$new$0(view);
                }
            });
            this.actionBarLayout.setRemoveActionBarExtraHeight(true);
            VerticalPositionAutoAnimator.attach(this.actionBarLayout.getView());
        }
        setContentView(this.frameLayout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onBackPressed();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public void onMeasureOverride(int[] iArr) {
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isInMultiwindow || AndroidUtilities.isSmallTablet()) {
            return;
        }
        iArr[0] = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), View.MeasureSpec.getSize(iArr[0])), NUM);
        iArr[1] = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0f), View.MeasureSpec.getSize(iArr[1])), NUM);
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
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
            if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", null, true)) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ WindowInsets lambda$onCreate$1(View view, WindowInsets windowInsets) {
        view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
        return windowInsets;
    }

    public void addFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment, AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet());
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        this.actionBarLayout.onBackPressed();
        if (this.actionBarLayout.getFragmentStack().size() <= 1) {
            dismiss();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needCloseLastFragment(INavigationLayout iNavigationLayout) {
        if (iNavigationLayout.getFragmentStack().size() <= 1) {
            dismiss();
        }
        return true;
    }

    /* loaded from: classes3.dex */
    private static final class EmptyFragment extends BaseFragment {
        private EmptyFragment() {
        }

        @Override // org.telegram.ui.ActionBar.BaseFragment
        public View createView(Context context) {
            this.actionBar.setAddToContainer(false);
            View view = new View(context);
            view.setBackgroundColor(0);
            return view;
        }
    }
}
