package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
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
import org.telegram.tgnet.TLRPC;
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
    private static final int ICON_HEIGHT_DP = 150;
    private static final int ICON_WIDTH_DP = 200;
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
        RLottieImageView themeIconView = new RLottieImageView(context2);
        final FrameLayout themeFrameLayout = new FrameLayout(context2);
        themeFrameLayout.addView(themeIconView, LayoutHelper.createFrame(28, 28, 17));
        AnonymousClass1 r9 = new FrameLayout(context2, 4) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int oneFourth = (bottom - top) / 4;
                int y = ((oneFourth * 3) - AndroidUtilities.dp(275.0f)) / 2;
                int i = 0;
                IntroActivity.this.frameLayout2.layout(0, y, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + y);
                int y2 = y + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(122.0f);
                int x = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(x, y2, IntroActivity.this.bottomPages.getMeasuredWidth() + x, IntroActivity.this.bottomPages.getMeasuredHeight() + y2);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                int y3 = (oneFourth * 3) + ((oneFourth - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2);
                int x2 = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(x2, y3, IntroActivity.this.startMessagingButton.getMeasuredWidth() + x2, IntroActivity.this.startMessagingButton.getMeasuredHeight() + y3);
                int y4 = y3 - AndroidUtilities.dp(30.0f);
                int x3 = (getMeasuredWidth() - IntroActivity.this.switchLanguageTextView.getMeasuredWidth()) / 2;
                IntroActivity.this.switchLanguageTextView.layout(x3, y4 - IntroActivity.this.switchLanguageTextView.getMeasuredHeight(), IntroActivity.this.switchLanguageTextView.getMeasuredWidth() + x3, y4);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) themeFrameLayout.getLayoutParams();
                int dp = AndroidUtilities.dp((float) 4);
                if (!AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.statusBarHeight;
                }
                int newTopMargin = dp + i;
                if (marginLayoutParams.topMargin != newTopMargin) {
                    marginLayoutParams.topMargin = newTopMargin;
                    themeFrameLayout.requestLayout();
                }
            }
        };
        this.frameContainerView = r9;
        scrollView.addView(r9, LayoutHelper.createScroll(-1, -2, 51));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, String.valueOf(NUM), AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        this.darkThemeDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        this.darkThemeDrawable.beginApplyLayerColors();
        this.darkThemeDrawable.commitApplyLayerColors();
        this.darkThemeDrawable.setCustomEndFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0);
        this.darkThemeDrawable.setCurrentFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0, false);
        Theme.getCurrentTheme().isDark();
        themeIconView.setContentDescription(LocaleController.getString(NUM));
        themeIconView.setAnimation(this.darkThemeDrawable);
        themeFrameLayout.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda2(this, themeIconView));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.frameLayout2 = frameLayout;
        this.frameContainerView.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(context2);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (IntroActivity.this.eglThread == null && surface != null) {
                    EGLThread unused = IntroActivity.this.eglThread = new EGLThread(surface);
                    IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
                    IntroActivity.this.eglThread.postRunnable(new IntroActivity$2$$ExternalSyntheticLambda0(this));
                    IntroActivity.this.eglThread.postRunnable(IntroActivity.this.eglThread.drawRunnable);
                }
            }

            /* renamed from: lambda$onSurfaceTextureAvailable$0$org-telegram-ui-IntroActivity$2  reason: not valid java name */
            public /* synthetic */ void m2295x770CLASSNAMEa() {
                Intro.setPage(IntroActivity.this.currentViewPagerPage);
                Intro.setDate(((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f);
                Intro.onDrawFrame(0);
                if (IntroActivity.this.eglThread != null && IntroActivity.this.eglThread.isAlive() && IntroActivity.this.eglThread.eglDisplay != null && IntroActivity.this.eglThread.eglSurface != null) {
                    try {
                        IntroActivity.this.eglThread.egl10.eglSwapBuffers(IntroActivity.this.eglThread.eglDisplay, IntroActivity.this.eglThread.eglSurface);
                    } catch (Exception e) {
                    }
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (IntroActivity.this.eglThread != null) {
                    IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (IntroActivity.this.eglThread == null) {
                    return true;
                }
                IntroActivity.this.eglThread.shutdown();
                EGLThread unused = IntroActivity.this.eglThread = null;
                return true;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        ViewPager viewPager2 = new ViewPager(context2);
        this.viewPager = viewPager2;
        viewPager2.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.frameContainerView.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                IntroActivity.this.bottomPages.setPageOffset(position, positionOffset);
                float width = (float) IntroActivity.this.viewPager.getMeasuredWidth();
                if (width != 0.0f) {
                    Intro.setScrollOffset((((((float) position) * width) + ((float) positionOffsetPixels)) - (((float) IntroActivity.this.currentViewPagerPage) * width)) / width);
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
        AnonymousClass4 r10 = new TextView(context2) {
            CellFlickerDrawable cellFlickerDrawable;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (this.cellFlickerDrawable == null) {
                    CellFlickerDrawable cellFlickerDrawable2 = new CellFlickerDrawable();
                    this.cellFlickerDrawable = cellFlickerDrawable2;
                    cellFlickerDrawable2.drawFrame = false;
                    this.cellFlickerDrawable.repeatProgress = 2.0f;
                }
                this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f));
                invalidate();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (View.MeasureSpec.getSize(widthMeasureSpec) > AndroidUtilities.dp(260.0f)) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), NUM), heightMeasureSpec);
                } else {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        };
        this.startMessagingButton = r10;
        r10.setText(LocaleController.getString("StartMessaging", NUM));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.startMessagingButton.setTextSize(1, 15.0f);
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.frameContainerView.addView(this.startMessagingButton, LayoutHelper.createFrame(-1, 50.0f, 81, 16.0f, 0.0f, 16.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda0(this));
        BottomPagesView bottomPagesView = new BottomPagesView(context2, this.viewPager, 6);
        this.bottomPages = bottomPagesView;
        this.frameContainerView.addView(bottomPagesView, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.switchLanguageTextView = textView;
        textView.setGravity(17);
        this.switchLanguageTextView.setTextSize(1, 16.0f);
        this.frameContainerView.addView(this.switchLanguageTextView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.switchLanguageTextView.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda1(this));
        this.frameContainerView.addView(themeFrameLayout, LayoutHelper.createFrame(64, 64.0f, 53, 0.0f, (float) 4, (float) 4, 0.0f));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$createView$0$org-telegram-ui-IntroActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m2290lambda$createView$0$orgtelegramuiIntroActivity(org.telegram.ui.Components.RLottieImageView r12, android.view.View r13) {
        /*
            r11 = this;
            boolean r0 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            r0 = 1
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r0
            java.lang.String r1 = "Blue"
            java.lang.String r2 = "Night"
            boolean r3 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
            r3 = r3 ^ r0
            r4 = r3
            if (r3 == 0) goto L_0x0019
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
            goto L_0x001d
        L_0x0019:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
        L_0x001d:
            r5 = 0
            org.telegram.ui.ActionBar.Theme.selectedAutoNightType = r5
            org.telegram.ui.ActionBar.Theme.saveAutoNightThemeConfig()
            org.telegram.ui.ActionBar.Theme.cancelAutoNightThemeCallbacks()
            org.telegram.ui.Components.RLottieDrawable r6 = r11.darkThemeDrawable
            if (r4 == 0) goto L_0x0030
            int r7 = r6.getFramesCount()
            int r7 = r7 - r0
            goto L_0x0031
        L_0x0030:
            r7 = 0
        L_0x0031:
            r6.setCustomEndFrame(r7)
            r12.playAnimation()
            r6 = 2
            int[] r7 = new int[r6]
            r12.getLocationInWindow(r7)
            r8 = r7[r5]
            int r9 = r12.getMeasuredWidth()
            int r9 = r9 / r6
            int r8 = r8 + r9
            r7[r5] = r8
            r8 = r7[r0]
            int r9 = r12.getMeasuredHeight()
            int r9 = r9 / r6
            int r8 = r8 + r9
            r7[r0] = r8
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r10 = 6
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r5] = r3
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r10[r0] = r5
            r10[r6] = r7
            r0 = 3
            r5 = -1
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r10[r0] = r5
            r0 = 4
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r4)
            r10[r0] = r5
            r0 = 5
            r10[r0] = r12
            r8.postNotificationName(r9, r10)
            r0 = 2131624058(0x7f0e007a, float:1.8875285E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
            r12.setContentDescription(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.IntroActivity.m2290lambda$createView$0$orgtelegramuiIntroActivity(org.telegram.ui.Components.RLottieImageView, android.view.View):void");
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m2291lambda$createView$1$orgtelegramuiIntroActivity(View view) {
        if (!this.startPressed) {
            this.startPressed = true;
            presentFragment(new LoginActivity().setIntroView(this.frameContainerView, this.startMessagingButton), true);
            this.destroyed = true;
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m2292lambda$createView$2$orgtelegramuiIntroActivity(View v) {
        if (!this.startPressed && this.localeInfo != null) {
            this.startPressed = true;
            final AlertDialog loaderDialog = new AlertDialog(v.getContext(), 3);
            loaderDialog.setCanCancel(false);
            loaderDialog.showDelayed(1000);
            NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() {
                public void didReceivedNotification(int id, int account, Object... args) {
                    if (id == NotificationCenter.reloadInterface) {
                        loaderDialog.dismiss();
                        NotificationCenter.getGlobalInstance().removeObserver(this, id);
                        AndroidUtilities.runOnUIThread(new IntroActivity$5$$ExternalSyntheticLambda0(this), 100);
                    }
                }

                /* renamed from: lambda$didReceivedNotification$0$org-telegram-ui-IntroActivity$5  reason: not valid java name */
                public /* synthetic */ void m2296lambda$didReceivedNotification$0$orgtelegramuiIntroActivity$5() {
                    IntroActivity.this.presentFragment(new LoginActivity().setIntroView(IntroActivity.this.frameContainerView, IntroActivity.this.startMessagingButton), true);
                    boolean unused = IntroActivity.this.destroyed = true;
                }
            }, NotificationCenter.reloadInterface);
            LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
        }
    }

    public void onResume() {
        Activity activity;
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
        if (!AndroidUtilities.isTablet() && (activity = getParentActivity()) != null) {
            activity.setRequestedOrientation(1);
        }
    }

    public void onPause() {
        Activity activity;
        super.onPause();
        if (!AndroidUtilities.isTablet() && (activity = getParentActivity()) != null) {
            activity.setRequestedOrientation(-1);
        }
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.configLoaded);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0).apply();
    }

    private void checkContinueText() {
        LocaleController.LocaleInfo englishInfo = null;
        LocaleController.LocaleInfo systemInfo = null;
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String systemLang = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
        if ((systemLang == null || (systemLang.equals("en") && LocaleController.getInstance().getSystemDefaultLocale().getLanguage() != null && !LocaleController.getInstance().getSystemDefaultLocale().getLanguage().equals("en"))) && (systemLang = LocaleController.getInstance().getSystemDefaultLocale().getLanguage()) == null) {
            systemLang = "en";
        }
        String arg = systemLang.contains("-") ? systemLang.split("-")[0] : systemLang;
        String alias = LocaleController.getLocaleAlias(arg);
        for (int a = 0; a < LocaleController.getInstance().languages.size(); a++) {
            LocaleController.LocaleInfo info = LocaleController.getInstance().languages.get(a);
            if (info.shortName.equals("en")) {
                englishInfo = info;
            }
            if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || info.shortName.equals(alias)) {
                systemInfo = info;
            }
            if (englishInfo != null && systemInfo != null) {
                break;
            }
        }
        if (englishInfo != null && systemInfo != null && englishInfo != systemInfo) {
            TLRPC.TL_langpack_getStrings req = new TLRPC.TL_langpack_getStrings();
            if (systemInfo != currentLocaleInfo) {
                req.lang_code = systemInfo.getLangCode();
                this.localeInfo = systemInfo;
            } else {
                req.lang_code = englishInfo.getLangCode();
                this.localeInfo = englishInfo;
            }
            req.keys.add("ContinueOnThisLanguage");
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new IntroActivity$$ExternalSyntheticLambda5(this, systemLang), 8);
        }
    }

    /* renamed from: lambda$checkContinueText$4$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m2289lambda$checkContinueText$4$orgtelegramuiIntroActivity(String finalSystemLang, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (!vector.objects.isEmpty()) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(0);
                if (string instanceof TLRPC.TL_langPackString) {
                    AndroidUtilities.runOnUIThread(new IntroActivity$$ExternalSyntheticLambda4(this, string, finalSystemLang));
                }
            }
        }
    }

    /* renamed from: lambda$checkContinueText$3$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m2288lambda$checkContinueText$3$orgtelegramuiIntroActivity(TLRPC.LangPackString string, String finalSystemLang) {
        if (!this.destroyed) {
            this.switchLanguageTextView.setText(string.value);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", finalSystemLang.toLowerCase()).apply();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack || id == NotificationCenter.configLoaded) {
            checkContinueText();
        }
    }

    public IntroActivity setOnLogout() {
        this.isOnLogout = true;
        return this;
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (!this.isOnLogout) {
            return null;
        }
        AnimatorSet set = new AnimatorSet().setDuration(50);
        set.playTogether(new Animator[]{ValueAnimator.ofFloat(new float[0])});
        return set;
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            final TextView headerTextView = new TextView(container.getContext());
            headerTextView.setTag(IntroActivity.this.pagerHeaderTag);
            final TextView messageTextView = new TextView(container.getContext());
            messageTextView.setTag(IntroActivity.this.pagerMessageTag);
            FrameLayout frameLayout = new FrameLayout(container.getContext()) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int y = (((((bottom - top) / 4) * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(16.0f);
                    int x = AndroidUtilities.dp(18.0f);
                    TextView textView = headerTextView;
                    textView.layout(x, y, textView.getMeasuredWidth() + x, headerTextView.getMeasuredHeight() + y);
                    int y2 = ((int) (((float) y) + headerTextView.getTextSize())) + AndroidUtilities.dp(16.0f);
                    int x2 = AndroidUtilities.dp(16.0f);
                    TextView textView2 = messageTextView;
                    textView2.layout(x2, y2, textView2.getMeasuredWidth() + x2, messageTextView.getMeasuredHeight() + y2);
                }
            };
            headerTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            headerTextView.setTextSize(1, 26.0f);
            headerTextView.setGravity(17);
            frameLayout.addView(headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            messageTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            messageTextView.setTextSize(1, 15.0f);
            messageTextView.setGravity(17);
            frameLayout.addView(messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 286.0f, 16.0f, 0.0f));
            container.addView(frameLayout, 0);
            headerTextView.setText(IntroActivity.this.titles[position]);
            messageTextView.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[position]));
            return frameLayout;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            IntroActivity.this.bottomPages.setCurrentPage(position);
            int unused = IntroActivity.this.currentViewPagerPage = position;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    public class EGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        /* access modifiers changed from: private */
        public Runnable drawRunnable = new Runnable() {
            public void run() {
                if (EGLThread.this.initied) {
                    long current = System.currentTimeMillis();
                    if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                        int deltaDrawMs = (int) Math.min(current - EGLThread.this.lastDrawFrame, 16);
                        Intro.setPage(IntroActivity.this.currentViewPagerPage);
                        Intro.setDate(((float) (current - IntroActivity.this.currentDate)) / 1000.0f);
                        Intro.onDrawFrame(deltaDrawMs);
                        EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                        long unused = EGLThread.this.lastDrawFrame = current;
                        if (EGLThread.this.maxRefreshRate == 0.0f) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                float maxRate = 0.0f;
                                for (float rate : ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getSupportedRefreshRates()) {
                                    if (rate > maxRate) {
                                        maxRate = rate;
                                    }
                                }
                                float unused2 = EGLThread.this.maxRefreshRate = maxRate;
                            } else {
                                float unused3 = EGLThread.this.maxRefreshRate = 60.0f;
                            }
                        }
                        EGLThread eGLThread = EGLThread.this;
                        eGLThread.postRunnable(eGLThread.drawRunnable, Math.max(((long) (1000.0f / EGLThread.this.maxRefreshRate)) - (System.currentTimeMillis() - current), 0));
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

        static /* synthetic */ Bitmap lambda$new$0(Void v) {
            int size = AndroidUtilities.dp(150.0f);
            Bitmap bm = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), size, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            c.drawColor(Theme.getColor("windowBackgroundWhite"));
            Paint paint = new Paint(1);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            c.drawCircle(((float) bm.getWidth()) / 2.0f, ((float) bm.getHeight()) / 2.0f, ((float) size) / 2.0f, paint);
            return bm;
        }

        public EGLThread(SurfaceTexture surface) {
            super("EGLThread");
            this.surfaceTexture = surface;
        }

        private boolean initGL() {
            int[] configSpec;
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
            if (!this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (EmuDetector.with(IntroActivity.this.getParentActivity()).detect()) {
                configSpec = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12344};
            } else {
                configSpec = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344};
            }
            if (!this.egl10.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
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
                    }
                    EGL10 egl103 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl103.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
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
                    int[] iArr = this.textures;
                    Intro.setPrivateTextures(iArr[19], iArr[20]);
                    int[] iArr2 = this.textures;
                    Intro.setFreeTextures(iArr2[14], iArr2[13]);
                    int[] iArr3 = this.textures;
                    Intro.setFastTextures(iArr3[2], iArr3[3], iArr3[1], iArr3[0]);
                    int[] iArr4 = this.textures;
                    Intro.setIcTextures(iArr4[4], iArr4[5], iArr4[6], iArr4[7], iArr4[8], iArr4[9], iArr4[10], iArr4[11], iArr4[12]);
                    Intro.onSurfaceCreated();
                    long unused = IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
                    return true;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        static /* synthetic */ Bitmap lambda$initGL$1(Void v) {
            Paint paint = new Paint(1);
            paint.setColor(-13851168);
            int size = AndroidUtilities.dp(150.0f);
            Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            new Canvas(bm).drawCircle(((float) size) / 2.0f, ((float) size) / 2.0f, ((float) size) / 2.0f, paint);
            return bm;
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
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        private void loadTexture(GenericProvider<Void, Bitmap> bitmapProvider, int index) {
            loadTexture(bitmapProvider, index, false);
        }

        /* access modifiers changed from: private */
        public void loadTexture(GenericProvider<Void, Bitmap> bitmapProvider, int index, boolean rebind) {
            if (rebind) {
                GLES20.glDeleteTextures(1, this.textures, index);
                GLES20.glGenTextures(1, this.textures, index);
            }
            Bitmap bm = bitmapProvider.provide(null);
            GLES20.glBindTexture(3553, this.textures[index]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, bm, 0);
            bm.recycle();
        }

        private void loadTexture(int resId, int index) {
            loadTexture(resId, index, 0, false);
        }

        /* access modifiers changed from: private */
        public void loadTexture(int resId, int index, int tintColor, boolean rebind) {
            Drawable drawable = IntroActivity.this.getParentActivity().getResources().getDrawable(resId);
            if (drawable instanceof BitmapDrawable) {
                if (rebind) {
                    GLES20.glDeleteTextures(1, this.textures, index);
                    GLES20.glGenTextures(1, this.textures, index);
                }
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                GLES20.glBindTexture(3553, this.textures[index]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                if (tintColor != 0) {
                    Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(tempBitmap);
                    Paint tempPaint = new Paint(5);
                    tempPaint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, tempPaint);
                    GLUtils.texImage2D(3553, 0, tempBitmap, 0);
                    tempBitmap.recycle();
                    return;
                }
                GLUtils.texImage2D(3553, 0, bitmap, 0);
            }
        }

        public void shutdown() {
            postRunnable(new IntroActivity$EGLThread$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$shutdown$2$org-telegram-ui-IntroActivity$EGLThread  reason: not valid java name */
        public /* synthetic */ void m2297lambda$shutdown$2$orgtelegramuiIntroActivity$EGLThread() {
            finish();
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        }

        public void setSurfaceTextureSize(int width, int height) {
            Intro.onSurfaceChanged(width, height, Math.min(((float) width) / 150.0f, ((float) height) / 150.0f), 0);
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new IntroActivity$$ExternalSyntheticLambda6(this), "windowBackgroundWhite", "windowBackgroundWhiteBlueText4", "chats_actionBackground", "chats_actionPressedBackground", "featuredStickers_buttonText", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText3");
    }

    /* renamed from: lambda$getThemeDescriptions$5$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m2293lambda$getThemeDescriptions$5$orgtelegramuiIntroActivity() {
        updateColors(true);
    }

    private void updateColors(boolean fromTheme) {
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.switchLanguageTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.startMessagingButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.startMessagingButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2"), Theme.getColor("chats_actionPressedBackground")));
        this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image2"), PorterDuff.Mode.SRC_IN));
        this.bottomPages.invalidate();
        if (fromTheme) {
            EGLThread eGLThread = this.eglThread;
            if (eGLThread != null) {
                eGLThread.postRunnable(new IntroActivity$$ExternalSyntheticLambda3(this));
            }
            for (int i = 0; i < this.viewPager.getChildCount(); i++) {
                View ch = this.viewPager.getChildAt(i);
                ((TextView) ch.findViewWithTag(this.pagerHeaderTag)).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                ((TextView) ch.findViewWithTag(this.pagerMessageTag)).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            }
            return;
        }
        Intro.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    }

    /* renamed from: lambda$updateColors$6$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m2294lambda$updateColors$6$orgtelegramuiIntroActivity() {
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
