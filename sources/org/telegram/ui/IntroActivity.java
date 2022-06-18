package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.Intro;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$LangPackString;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_langPackString;
import org.telegram.tgnet.TLRPC$TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class IntroActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public BottomPagesView bottomPages;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public long currentDate;
    /* access modifiers changed from: private */
    public int currentViewPagerPage;
    private RLottieDrawable darkThemeDrawable;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public boolean dragging;
    /* access modifiers changed from: private */
    public EGLThread eglThread;
    /* access modifiers changed from: private */
    public FrameLayout frameContainerView;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    private boolean isOnLogout;
    private boolean justCreated = false;
    /* access modifiers changed from: private */
    public boolean justEndDragging;
    /* access modifiers changed from: private */
    public int lastPage = 0;
    private LocaleController.LocaleInfo localeInfo;
    /* access modifiers changed from: private */
    public String[] messages;
    /* access modifiers changed from: private */
    public final Object pagerHeaderTag = new Object();
    /* access modifiers changed from: private */
    public final Object pagerMessageTag = new Object();
    /* access modifiers changed from: private */
    public int startDragX;
    /* access modifiers changed from: private */
    public TextView startMessagingButton;
    private boolean startPressed = false;
    /* access modifiers changed from: private */
    public TextView switchLanguageTextView;
    /* access modifiers changed from: private */
    public String[] titles;
    /* access modifiers changed from: private */
    public ViewPager viewPager;

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public boolean onFragmentCreate() {
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).apply();
        this.titles = new String[]{LocaleController.getString("Page1Title", NUM), LocaleController.getString("Page2Title", NUM), LocaleController.getString("Page3Title", NUM), LocaleController.getString("Page5Title", NUM), LocaleController.getString("Page4Title", NUM), LocaleController.getString("Page6Title", NUM)};
        this.messages = new String[]{LocaleController.getString("Page1Message", NUM), LocaleController.getString("Page2Message", NUM), LocaleController.getString("Page3Message", NUM), LocaleController.getString("Page5Message", NUM), LocaleController.getString("Page4Message", NUM), LocaleController.getString("Page6Message", NUM)};
        return true;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setAddToContainer(false);
        ScrollView scrollView = new ScrollView(context2);
        scrollView.setFillViewport(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        final FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(28, 28, 17));
        AnonymousClass1 r7 = new FrameLayout(context2, 4) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int i5 = (i4 - i2) / 4;
                int i6 = i5 * 3;
                int dp = (i6 - AndroidUtilities.dp(275.0f)) / 2;
                int i7 = 0;
                IntroActivity.this.frameLayout2.layout(0, dp, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + dp);
                int dp2 = dp + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(122.0f);
                int measuredWidth = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(measuredWidth, dp2, IntroActivity.this.bottomPages.getMeasuredWidth() + measuredWidth, IntroActivity.this.bottomPages.getMeasuredHeight() + dp2);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                int measuredHeight = i6 + ((i5 - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2);
                int measuredWidth2 = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(measuredWidth2, measuredHeight, IntroActivity.this.startMessagingButton.getMeasuredWidth() + measuredWidth2, IntroActivity.this.startMessagingButton.getMeasuredHeight() + measuredHeight);
                int dp3 = measuredHeight - AndroidUtilities.dp(30.0f);
                int measuredWidth3 = (getMeasuredWidth() - IntroActivity.this.switchLanguageTextView.getMeasuredWidth()) / 2;
                IntroActivity.this.switchLanguageTextView.layout(measuredWidth3, dp3 - IntroActivity.this.switchLanguageTextView.getMeasuredHeight(), IntroActivity.this.switchLanguageTextView.getMeasuredWidth() + measuredWidth3, dp3);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
                int dp4 = AndroidUtilities.dp((float) 4);
                if (!AndroidUtilities.isTablet()) {
                    i7 = AndroidUtilities.statusBarHeight;
                }
                int i8 = dp4 + i7;
                if (marginLayoutParams.topMargin != i8) {
                    marginLayoutParams.topMargin = i8;
                    frameLayout.requestLayout();
                }
            }
        };
        this.frameContainerView = r7;
        scrollView.addView(r7, LayoutHelper.createScroll(-1, -2, 51));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, String.valueOf(NUM), AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        this.darkThemeDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        this.darkThemeDrawable.beginApplyLayerColors();
        this.darkThemeDrawable.commitApplyLayerColors();
        this.darkThemeDrawable.setCustomEndFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0);
        this.darkThemeDrawable.setCurrentFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0, false);
        Theme.getCurrentTheme().isDark();
        rLottieImageView.setContentDescription(LocaleController.getString(NUM));
        rLottieImageView.setAnimation(this.darkThemeDrawable);
        frameLayout.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda2(this, rLottieImageView));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.frameLayout2 = frameLayout3;
        this.frameContainerView.addView(frameLayout3, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(context2);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (IntroActivity.this.eglThread == null && surfaceTexture != null) {
                    EGLThread unused = IntroActivity.this.eglThread = new EGLThread(surfaceTexture);
                    IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
                    IntroActivity.this.eglThread.postRunnable(new IntroActivity$2$$ExternalSyntheticLambda0(this));
                    IntroActivity.this.eglThread.postRunnable(IntroActivity.this.eglThread.drawRunnable);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSurfaceTextureAvailable$0() {
                Intro.setPage(IntroActivity.this.currentViewPagerPage);
                Intro.setDate(((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f);
                Intro.onDrawFrame(0);
                if (IntroActivity.this.eglThread != null && IntroActivity.this.eglThread.isAlive() && IntroActivity.this.eglThread.eglDisplay != null && IntroActivity.this.eglThread.eglSurface != null) {
                    try {
                        IntroActivity.this.eglThread.egl10.eglSwapBuffers(IntroActivity.this.eglThread.eglDisplay, IntroActivity.this.eglThread.eglSurface);
                    } catch (Exception unused) {
                    }
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                if (IntroActivity.this.eglThread != null) {
                    IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (IntroActivity.this.eglThread == null) {
                    return true;
                }
                IntroActivity.this.eglThread.shutdown();
                EGLThread unused = IntroActivity.this.eglThread = null;
                return true;
            }
        });
        ViewPager viewPager2 = new ViewPager(context2);
        this.viewPager = viewPager2;
        viewPager2.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.frameContainerView.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int i, float f, int i2) {
                IntroActivity.this.bottomPages.setPageOffset(i, f);
                float measuredWidth = (float) IntroActivity.this.viewPager.getMeasuredWidth();
                if (measuredWidth != 0.0f) {
                    Intro.setScrollOffset((((((float) i) * measuredWidth) + ((float) i2)) - (((float) IntroActivity.this.currentViewPagerPage) * measuredWidth)) / measuredWidth);
                }
            }

            public void onPageSelected(int i) {
                int unused = IntroActivity.this.currentViewPagerPage = i;
            }

            public void onPageScrollStateChanged(int i) {
                if (i == 1) {
                    boolean unused = IntroActivity.this.dragging = true;
                    IntroActivity introActivity = IntroActivity.this;
                    int unused2 = introActivity.startDragX = introActivity.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
                } else if (i == 0 || i == 2) {
                    if (IntroActivity.this.dragging) {
                        boolean unused3 = IntroActivity.this.justEndDragging = true;
                        boolean unused4 = IntroActivity.this.dragging = false;
                    }
                    if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                        IntroActivity introActivity2 = IntroActivity.this;
                        int unused5 = introActivity2.lastPage = introActivity2.viewPager.getCurrentItem();
                    }
                }
            }
        });
        AnonymousClass4 r5 = new TextView(this, context2) {
            CellFlickerDrawable cellFlickerDrawable;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (this.cellFlickerDrawable == null) {
                    CellFlickerDrawable cellFlickerDrawable2 = new CellFlickerDrawable();
                    this.cellFlickerDrawable = cellFlickerDrawable2;
                    cellFlickerDrawable2.drawFrame = false;
                    cellFlickerDrawable2.repeatProgress = 2.0f;
                }
                this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.cellFlickerDrawable.draw(canvas, rectF, (float) AndroidUtilities.dp(4.0f), (View) null);
                invalidate();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (View.MeasureSpec.getSize(i) > AndroidUtilities.dp(260.0f)) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), NUM), i2);
                } else {
                    super.onMeasure(i, i2);
                }
            }
        };
        this.startMessagingButton = r5;
        r5.setText(LocaleController.getString("StartMessaging", NUM));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.startMessagingButton.setTextSize(1, 15.0f);
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.frameContainerView.addView(this.startMessagingButton, LayoutHelper.createFrame(-1, 50.0f, 81, 16.0f, 0.0f, 16.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda1(this));
        BottomPagesView bottomPagesView = new BottomPagesView(context2, this.viewPager, 6);
        this.bottomPages = bottomPagesView;
        this.frameContainerView.addView(bottomPagesView, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.switchLanguageTextView = textView;
        textView.setGravity(17);
        this.switchLanguageTextView.setTextSize(1, 16.0f);
        this.frameContainerView.addView(this.switchLanguageTextView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.switchLanguageTextView.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda0(this));
        float f = (float) 4;
        this.frameContainerView.addView(frameLayout, LayoutHelper.createFrame(64, 64.0f, 53, 0.0f, f, f, 0.0f));
        this.fragmentView = scrollView;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.configLoaded);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        checkContinueText();
        this.justCreated = true;
        updateColors(false);
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$0(org.telegram.ui.Components.RLottieImageView r9, android.view.View r10) {
        /*
            r8 = this;
            boolean r10 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
            if (r10 == 0) goto L_0x0005
            return
        L_0x0005:
            r10 = 1
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r10
            boolean r0 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
            r0 = r0 ^ r10
            if (r0 == 0) goto L_0x0016
            java.lang.String r1 = "Night"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
            goto L_0x001c
        L_0x0016:
            java.lang.String r1 = "Blue"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
        L_0x001c:
            r2 = 0
            org.telegram.ui.ActionBar.Theme.selectedAutoNightType = r2
            org.telegram.ui.ActionBar.Theme.saveAutoNightThemeConfig()
            org.telegram.ui.ActionBar.Theme.cancelAutoNightThemeCallbacks()
            org.telegram.ui.Components.RLottieDrawable r3 = r8.darkThemeDrawable
            if (r0 == 0) goto L_0x002f
            int r4 = r3.getFramesCount()
            int r4 = r4 - r10
            goto L_0x0030
        L_0x002f:
            r4 = 0
        L_0x0030:
            r3.setCustomEndFrame(r4)
            r9.playAnimation()
            r3 = 2
            int[] r4 = new int[r3]
            r9.getLocationInWindow(r4)
            r5 = r4[r2]
            int r6 = r9.getMeasuredWidth()
            int r6 = r6 / r3
            int r5 = r5 + r6
            r4[r2] = r5
            r5 = r4[r10]
            int r6 = r9.getMeasuredHeight()
            int r6 = r6 / r3
            int r5 = r5 + r6
            r4[r10] = r5
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r7 = 6
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r7[r2] = r1
            java.lang.Boolean r1 = java.lang.Boolean.FALSE
            r7[r10] = r1
            r7[r3] = r4
            r10 = 3
            r1 = -1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r7[r10] = r1
            r10 = 4
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r7[r10] = r0
            r10 = 5
            r7[r10] = r9
            r5.postNotificationName(r6, r7)
            r10 = 2131624088(0x7f0e0098, float:1.8875346E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
            r9.setContentDescription(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.IntroActivity.lambda$createView$0(org.telegram.ui.Components.RLottieImageView, android.view.View):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        if (!this.startPressed) {
            this.startPressed = true;
            presentFragment(new LoginActivity().setIntroView(this.frameContainerView, this.startMessagingButton), true);
            this.destroyed = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        if (!this.startPressed && this.localeInfo != null) {
            this.startPressed = true;
            final AlertDialog alertDialog = new AlertDialog(view.getContext(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.showDelayed(1000);
            NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() {
                public void didReceivedNotification(int i, int i2, Object... objArr) {
                    if (i == NotificationCenter.reloadInterface) {
                        alertDialog.dismiss();
                        NotificationCenter.getGlobalInstance().removeObserver(this, i);
                        AndroidUtilities.runOnUIThread(new IntroActivity$5$$ExternalSyntheticLambda0(this), 100);
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$didReceivedNotification$0() {
                    IntroActivity.this.presentFragment(new LoginActivity().setIntroView(IntroActivity.this.frameContainerView, IntroActivity.this.startMessagingButton), true);
                    boolean unused = IntroActivity.this.destroyed = true;
                }
            }, NotificationCenter.reloadInterface);
            LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
        }
    }

    @SuppressLint({"SourceLockedOrientationActivity"})
    public void onResume() {
        Activity parentActivity;
        super.onResume();
        if (this.justCreated) {
            if (LocaleController.isRTL) {
                this.viewPager.setCurrentItem(6);
                this.lastPage = 6;
            } else {
                this.viewPager.setCurrentItem(0);
                this.lastPage = 0;
            }
            this.justCreated = false;
        }
        if (!AndroidUtilities.isTablet() && (parentActivity = getParentActivity()) != null) {
            parentActivity.setRequestedOrientation(1);
        }
    }

    public void onPause() {
        Activity parentActivity;
        super.onPause();
        if (!AndroidUtilities.isTablet() && (parentActivity = getParentActivity()) != null) {
            parentActivity.setRequestedOrientation(-1);
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.configLoaded);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0).apply();
    }

    private void checkContinueText() {
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String str = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
        if ((str == null || (str.equals("en") && LocaleController.getInstance().getSystemDefaultLocale().getLanguage() != null && !LocaleController.getInstance().getSystemDefaultLocale().getLanguage().equals("en"))) && (str = LocaleController.getInstance().getSystemDefaultLocale().getLanguage()) == null) {
            str = "en";
        }
        String str2 = str.contains("-") ? str.split("-")[0] : str;
        String localeAlias = LocaleController.getLocaleAlias(str2);
        LocaleController.LocaleInfo localeInfo2 = null;
        LocaleController.LocaleInfo localeInfo3 = null;
        for (int i = 0; i < LocaleController.getInstance().languages.size(); i++) {
            LocaleController.LocaleInfo localeInfo4 = LocaleController.getInstance().languages.get(i);
            if (localeInfo4.shortName.equals("en")) {
                localeInfo2 = localeInfo4;
            }
            if (localeInfo4.shortName.replace("_", "-").equals(str) || localeInfo4.shortName.equals(str2) || localeInfo4.shortName.equals(localeAlias)) {
                localeInfo3 = localeInfo4;
            }
            if (localeInfo2 != null && localeInfo3 != null) {
                break;
            }
        }
        if (localeInfo2 != null && localeInfo3 != null && localeInfo2 != localeInfo3) {
            TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings = new TLRPC$TL_langpack_getStrings();
            if (localeInfo3 != currentLocaleInfo) {
                tLRPC$TL_langpack_getStrings.lang_code = localeInfo3.getLangCode();
                this.localeInfo = localeInfo3;
            } else {
                tLRPC$TL_langpack_getStrings.lang_code = localeInfo2.getLangCode();
                this.localeInfo = localeInfo2;
            }
            tLRPC$TL_langpack_getStrings.keys.add("ContinueOnThisLanguage");
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new IntroActivity$$ExternalSyntheticLambda5(this, str), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkContinueText$4(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (!tLRPC$Vector.objects.isEmpty()) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(0);
                if (tLRPC$LangPackString instanceof TLRPC$TL_langPackString) {
                    AndroidUtilities.runOnUIThread(new IntroActivity$$ExternalSyntheticLambda4(this, tLRPC$LangPackString, str));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkContinueText$3(TLRPC$LangPackString tLRPC$LangPackString, String str) {
        if (!this.destroyed) {
            this.switchLanguageTextView.setText(tLRPC$LangPackString.value);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str.toLowerCase()).apply();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack || i == NotificationCenter.configLoaded) {
            checkContinueText();
        }
    }

    public IntroActivity setOnLogout() {
        this.isOnLogout = true;
        return this;
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        if (!this.isOnLogout) {
            return null;
        }
        AnimatorSet duration = new AnimatorSet().setDuration(50);
        duration.playTogether(new Animator[]{ValueAnimator.ofFloat(new float[0])});
        return duration;
    }

    private class IntroAdapter extends PagerAdapter {
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        private IntroAdapter() {
        }

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            final TextView textView = new TextView(viewGroup.getContext());
            textView.setTag(IntroActivity.this.pagerHeaderTag);
            final TextView textView2 = new TextView(viewGroup.getContext());
            textView2.setTag(IntroActivity.this.pagerMessageTag);
            AnonymousClass1 r2 = new FrameLayout(this, viewGroup.getContext()) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int dp = (((((i4 - i2) / 4) * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(16.0f);
                    int dp2 = AndroidUtilities.dp(18.0f);
                    TextView textView = textView;
                    textView.layout(dp2, dp, textView.getMeasuredWidth() + dp2, textView.getMeasuredHeight() + dp);
                    int textSize = ((int) (((float) dp) + textView.getTextSize())) + AndroidUtilities.dp(16.0f);
                    int dp3 = AndroidUtilities.dp(16.0f);
                    TextView textView2 = textView2;
                    textView2.layout(dp3, textSize, textView2.getMeasuredWidth() + dp3, textView2.getMeasuredHeight() + textSize);
                }
            };
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            textView.setTextSize(1, 26.0f);
            textView.setGravity(17);
            r2.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            textView2.setTextSize(1, 15.0f);
            textView2.setGravity(17);
            r2.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 286.0f, 16.0f, 0.0f));
            viewGroup.addView(r2, 0);
            textView.setText(IntroActivity.this.titles[i]);
            textView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[i]));
            return r2;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
            IntroActivity.this.bottomPages.setCurrentPage(i);
            int unused = IntroActivity.this.currentViewPagerPage = i;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }

    public class EGLThread extends DispatchQueue {
        /* access modifiers changed from: private */
        public Runnable drawRunnable = new Runnable() {
            public void run() {
                if (EGLThread.this.initied) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                        int min = (int) Math.min(currentTimeMillis - EGLThread.this.lastDrawFrame, 16);
                        Intro.setPage(IntroActivity.this.currentViewPagerPage);
                        Intro.setDate(((float) (currentTimeMillis - IntroActivity.this.currentDate)) / 1000.0f);
                        Intro.onDrawFrame(min);
                        EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                        long unused = EGLThread.this.lastDrawFrame = currentTimeMillis;
                        float f = 0.0f;
                        if (EGLThread.this.maxRefreshRate == 0.0f) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                for (float f2 : ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getSupportedRefreshRates()) {
                                    if (f2 > f) {
                                        f = f2;
                                    }
                                }
                                float unused2 = EGLThread.this.maxRefreshRate = f;
                            } else {
                                float unused3 = EGLThread.this.maxRefreshRate = 60.0f;
                            }
                        }
                        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                        EGLThread eGLThread = EGLThread.this;
                        eGLThread.postRunnable(eGLThread.drawRunnable, Math.max(((long) (1000.0f / EGLThread.this.maxRefreshRate)) - currentTimeMillis2, 0));
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public EGL10 egl10;
        private EGLConfig eglConfig;
        /* access modifiers changed from: private */
        public EGLContext eglContext;
        /* access modifiers changed from: private */
        public EGLDisplay eglDisplay;
        /* access modifiers changed from: private */
        public EGLSurface eglSurface;
        /* access modifiers changed from: private */
        public boolean initied;
        /* access modifiers changed from: private */
        public long lastDrawFrame;
        /* access modifiers changed from: private */
        public float maxRefreshRate;
        private SurfaceTexture surfaceTexture;
        /* access modifiers changed from: private */
        public GenericProvider<Void, Bitmap> telegramMaskProvider = IntroActivity$EGLThread$$ExternalSyntheticLambda2.INSTANCE;
        private int[] textures = new int[24];

        /* access modifiers changed from: private */
        public static /* synthetic */ Bitmap lambda$new$0(Void voidR) {
            int dp = AndroidUtilities.dp(150.0f);
            Bitmap createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), dp, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
            Paint paint = new Paint(1);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawCircle(((float) createBitmap.getWidth()) / 2.0f, ((float) createBitmap.getHeight()) / 2.0f, ((float) dp) / 2.0f, paint);
            return createBitmap;
        }

        public EGLThread(SurfaceTexture surfaceTexture2) {
            super("EGLThread");
            this.surfaceTexture = surfaceTexture2;
        }

        private boolean initGL() {
            int[] iArr;
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eglGetDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] iArr2 = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (EmuDetector.with(IntroActivity.this.getParentActivity()).detect()) {
                iArr = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12344};
            } else {
                iArr = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344};
            }
            if (!this.egl10.eglChooseConfig(this.eglDisplay, iArr, eGLConfigArr, 1, iArr2)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (iArr2[0] > 0) {
                EGLConfig eGLConfig = eGLConfigArr[0];
                this.eglConfig = eGLConfig;
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else {
                        GLES20.glGenTextures(23, this.textures, 0);
                        loadTexture(NUM, 0);
                        loadTexture(NUM, 1);
                        loadTexture(NUM, 2);
                        loadTexture(NUM, 3);
                        loadTexture(NUM, 4);
                        loadTexture(NUM, 5);
                        loadTexture(NUM, 6);
                        loadTexture(NUM, 7);
                        loadTexture(NUM, 8);
                        loadTexture(NUM, 9);
                        loadTexture(NUM, 10);
                        loadTexture(NUM, 11);
                        loadTexture(NUM, 12);
                        loadTexture(NUM, 13);
                        loadTexture(NUM, 14);
                        loadTexture(NUM, 15);
                        loadTexture(NUM, 16);
                        loadTexture(NUM, 17, Theme.getColor("windowBackgroundWhite"), false);
                        loadTexture(NUM, 18);
                        loadTexture(NUM, 19);
                        loadTexture(NUM, 20);
                        loadTexture(NUM, 21);
                        loadTexture((GenericProvider<Void, Bitmap>) IntroActivity$EGLThread$$ExternalSyntheticLambda1.INSTANCE, 22);
                        loadTexture(this.telegramMaskProvider, 23);
                        updateTelegramTextures();
                        updatePowerfulTextures();
                        int[] iArr3 = this.textures;
                        Intro.setPrivateTextures(iArr3[19], iArr3[20]);
                        int[] iArr4 = this.textures;
                        Intro.setFreeTextures(iArr4[14], iArr4[13]);
                        int[] iArr5 = this.textures;
                        Intro.setFastTextures(iArr5[2], iArr5[3], iArr5[1], iArr5[0]);
                        int[] iArr6 = this.textures;
                        Intro.setIcTextures(iArr6[4], iArr6[5], iArr6[6], iArr6[7], iArr6[8], iArr6[9], iArr6[10], iArr6[11], iArr6[12]);
                        Intro.onSurfaceCreated();
                        long unused = IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
                        return true;
                    }
                } else {
                    finish();
                    return false;
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ Bitmap lambda$initGL$1(Void voidR) {
            Paint paint = new Paint(1);
            paint.setColor(-13851168);
            int dp = AndroidUtilities.dp(150.0f);
            Bitmap createBitmap = Bitmap.createBitmap(dp, dp, Bitmap.Config.ARGB_8888);
            float f = ((float) dp) / 2.0f;
            new Canvas(createBitmap).drawCircle(f, f, f, paint);
            return createBitmap;
        }

        public void updateTelegramTextures() {
            int[] iArr = this.textures;
            Intro.setTelegramTextures(iArr[22], iArr[21], iArr[23]);
        }

        public void updatePowerfulTextures() {
            int[] iArr = this.textures;
            Intro.setPowerfulTextures(iArr[17], iArr[18], iArr[16], iArr[15]);
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl102 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay2 = this.eglDisplay;
            if (eGLDisplay2 != null) {
                this.egl10.eglTerminate(eGLDisplay2);
                this.eglDisplay = null;
            }
        }

        private void loadTexture(GenericProvider<Void, Bitmap> genericProvider, int i) {
            loadTexture(genericProvider, i, false);
        }

        /* access modifiers changed from: private */
        public void loadTexture(GenericProvider<Void, Bitmap> genericProvider, int i, boolean z) {
            if (z) {
                GLES20.glDeleteTextures(1, this.textures, i);
                GLES20.glGenTextures(1, this.textures, i);
            }
            Bitmap provide = genericProvider.provide(null);
            GLES20.glBindTexture(3553, this.textures[i]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, provide, 0);
            provide.recycle();
        }

        private void loadTexture(int i, int i2) {
            loadTexture(i, i2, 0, false);
        }

        /* access modifiers changed from: private */
        public void loadTexture(int i, int i2, int i3, boolean z) {
            Drawable drawable = IntroActivity.this.getParentActivity().getResources().getDrawable(i);
            if (drawable instanceof BitmapDrawable) {
                if (z) {
                    GLES20.glDeleteTextures(1, this.textures, i2);
                    GLES20.glGenTextures(1, this.textures, i2);
                }
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                GLES20.glBindTexture(3553, this.textures[i2]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                if (i3 != 0) {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    Paint paint = new Paint(5);
                    paint.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                    GLUtils.texImage2D(3553, 0, createBitmap, 0);
                    createBitmap.recycle();
                    return;
                }
                GLUtils.texImage2D(3553, 0, bitmap, 0);
            }
        }

        public void shutdown() {
            postRunnable(new IntroActivity$EGLThread$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$shutdown$2() {
            finish();
            Looper myLooper = Looper.myLooper();
            if (myLooper != null) {
                myLooper.quit();
            }
        }

        public void setSurfaceTextureSize(int i, int i2) {
            Intro.onSurfaceChanged(i, i2, Math.min(((float) i) / 150.0f, ((float) i2) / 150.0f), 0);
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$5() {
        updateColors(true);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new IntroActivity$$ExternalSyntheticLambda6(this), "windowBackgroundWhite", "windowBackgroundWhiteBlueText4", "chats_actionBackground", "chats_actionPressedBackground", "featuredStickers_buttonText", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText3");
    }

    private void updateColors(boolean z) {
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.switchLanguageTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.startMessagingButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.startMessagingButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2"), Theme.getColor("chats_actionPressedBackground")));
        this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image2"), PorterDuff.Mode.SRC_IN));
        this.bottomPages.invalidate();
        if (z) {
            EGLThread eGLThread = this.eglThread;
            if (eGLThread != null) {
                eGLThread.postRunnable(new IntroActivity$$ExternalSyntheticLambda3(this));
            }
            for (int i = 0; i < this.viewPager.getChildCount(); i++) {
                View childAt = this.viewPager.getChildAt(i);
                ((TextView) childAt.findViewWithTag(this.pagerHeaderTag)).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                ((TextView) childAt.findViewWithTag(this.pagerMessageTag)).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            }
            return;
        }
        Intro.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateColors$6() {
        this.eglThread.loadTexture(NUM, 17, Theme.getColor("windowBackgroundWhite"), true);
        this.eglThread.updatePowerfulTextures();
        EGLThread eGLThread = this.eglThread;
        eGLThread.loadTexture(eGLThread.telegramMaskProvider, 23, true);
        this.eglThread.updateTelegramTextures();
        Intro.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
