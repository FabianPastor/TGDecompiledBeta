package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ManageSpaceActivity extends Activity implements ActionBarLayoutDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
    private ActionBarLayout actionBarLayout;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    private ActionBarLayout layersActionBarLayout;

    /* renamed from: org.telegram.ui.ManageSpaceActivity$1 */
    class C15191 implements OnTouchListener {
        C15191() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (ManageSpaceActivity.this.actionBarLayout.fragmentsStack.isEmpty() != null || motionEvent.getAction() != 1) {
                return false;
            }
            view = motionEvent.getX();
            motionEvent = motionEvent.getY();
            int[] iArr = new int[2];
            ManageSpaceActivity.this.layersActionBarLayout.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (!ManageSpaceActivity.this.layersActionBarLayout.checkTransitionAnimation()) {
                if (view <= ((float) i) || view >= ((float) (i + ManageSpaceActivity.this.layersActionBarLayout.getWidth())) || motionEvent <= ((float) i2) || motionEvent >= ((float) (i2 + ManageSpaceActivity.this.layersActionBarLayout.getHeight()))) {
                    if (ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.isEmpty() == null) {
                        while (ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > null) {
                            ManageSpaceActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        ManageSpaceActivity.this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.ManageSpaceActivity$2 */
    class C15202 implements OnClickListener {
        public void onClick(View view) {
        }

        C15202() {
        }
    }

    /* renamed from: org.telegram.ui.ManageSpaceActivity$3 */
    class C15213 implements OnGlobalLayoutListener {
        C15213() {
        }

        public void onGlobalLayout() {
            ManageSpaceActivity.this.needLayout();
            if (ManageSpaceActivity.this.actionBarLayout != null) {
                ManageSpaceActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout) {
        return true;
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout) {
        return true;
    }

    public boolean onPreIme() {
        return false;
    }

    protected void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        boolean z = true;
        requestWindowFeature(1);
        setTheme(C0446R.style.Theme.TMessages);
        getWindow().setBackgroundDrawableResource(C0446R.drawable.transparent);
        super.onCreate(bundle);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(identifier);
        }
        Theme.createDialogsResources(this);
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            View relativeLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(relativeLayout);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            relativeLayout.setLayoutParams(layoutParams);
            View view = new View(this);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(C0446R.drawable.catstile);
            bitmapDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            view.setBackgroundDrawable(bitmapDrawable);
            relativeLayout.addView(view, LayoutHelper.createRelative(-1, -1));
            relativeLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
            view = new FrameLayout(this);
            view.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            relativeLayout.addView(view, LayoutHelper.createRelative(-1, -1));
            view.setOnTouchListener(new C15191());
            view.setOnClickListener(new C15202());
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(view);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(C0446R.drawable.boxshadow);
            relativeLayout.addView(this.layersActionBarLayout, LayoutHelper.createRelative(530, 528));
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        } else {
            this.drawerLayoutContainer.addView(this.actionBarLayout, new LayoutParams(-1, -1));
        }
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        Intent intent = getIntent();
        if (bundle == null) {
            z = false;
        }
        handleIntent(intent, false, z, false);
        needLayout();
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3) {
        if (AndroidUtilities.isTablet()) {
            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                this.layersActionBarLayout.addFragmentToStack(new CacheControlActivity());
            }
        } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.addFragmentToStack(new CacheControlActivity());
        }
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
        }
        intent.setAction(false);
        return null;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
        }
    }

    public void presentFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.actionBarLayout.presentFragment(baseFragment, z, z2, true);
    }

    public void needLayout() {
        if (AndroidUtilities.isTablet()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.layersActionBarLayout.getLayoutParams();
            layoutParams.leftMargin = (AndroidUtilities.displaySize.x - layoutParams.width) / 2;
            int i = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            layoutParams.topMargin = i + (((AndroidUtilities.displaySize.y - layoutParams.height) - i) / 2);
            this.layersActionBarLayout.setLayoutParams(layoutParams);
            if (AndroidUtilities.isSmallTablet()) {
                if (getResources().getConfiguration().orientation != 2) {
                    layoutParams = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    this.actionBarLayout.setLayoutParams(layoutParams);
                    return;
                }
            }
            int i2 = (AndroidUtilities.displaySize.x / 100) * 35;
            if (i2 < AndroidUtilities.dp(320.0f)) {
                i2 = AndroidUtilities.dp(320.0f);
            }
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
            layoutParams2.width = i2;
            layoutParams2.height = -1;
            this.actionBarLayout.setLayoutParams(layoutParams2);
            if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() == 2) {
                ((BaseFragment) this.actionBarLayout.fragmentsStack.get(1)).onPause();
                this.actionBarLayout.fragmentsStack.remove(1);
                this.actionBarLayout.showLastFragment();
            }
        }
    }

    public void fixLayout() {
        if (AndroidUtilities.isTablet() && this.actionBarLayout != null) {
            this.actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new C15213());
        }
    }

    protected void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onPause();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        onFinish();
    }

    protected void onResume() {
        super.onResume();
        this.actionBarLayout.onResume();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onResume();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void onBackPressed() {
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            this.actionBarLayout.onBackPressed();
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onLowMemory();
        }
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout == this.actionBarLayout && actionBarLayout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() != null && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout, boolean z) {
        if (AndroidUtilities.isTablet() && actionBarLayout == this.layersActionBarLayout) {
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }
}
