package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class IntroActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public BottomPagesView bottomPages;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public long currentDate;
    /* access modifiers changed from: private */
    public int currentViewPagerPage;
    private boolean destroyed;
    /* access modifiers changed from: private */
    public boolean dragging;
    /* access modifiers changed from: private */
    public EGLThread eglThread;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    private boolean justCreated = false;
    /* access modifiers changed from: private */
    public boolean justEndDragging;
    /* access modifiers changed from: private */
    public int lastPage = 0;
    private LocaleController.LocaleInfo localeInfo;
    /* access modifiers changed from: private */
    public String[] messages;
    /* access modifiers changed from: private */
    public int startDragX;
    /* access modifiers changed from: private */
    public TextView startMessagingButton;
    private boolean startPressed = false;
    /* access modifiers changed from: private */
    public TextView textView;
    /* access modifiers changed from: private */
    public String[] titles;
    /* access modifiers changed from: private */
    public ViewPager viewPager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(NUM);
        super.onCreate(bundle);
        requestWindowFeature(1);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
        this.titles = new String[]{LocaleController.getString("Page1Title", NUM), LocaleController.getString("Page2Title", NUM), LocaleController.getString("Page3Title", NUM), LocaleController.getString("Page5Title", NUM), LocaleController.getString("Page4Title", NUM), LocaleController.getString("Page6Title", NUM)};
        this.messages = new String[]{LocaleController.getString("Page1Message", NUM), LocaleController.getString("Page2Message", NUM), LocaleController.getString("Page3Message", NUM), LocaleController.getString("Page5Message", NUM), LocaleController.getString("Page4Message", NUM), LocaleController.getString("Page6Message", NUM)};
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        AnonymousClass1 r4 = new FrameLayout(this) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int i5 = (i4 - i2) / 4;
                int i6 = i5 * 3;
                int dp = (i6 - AndroidUtilities.dp(275.0f)) / 2;
                IntroActivity.this.frameLayout2.layout(0, dp, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + dp);
                int dp2 = dp + AndroidUtilities.dp(272.0f);
                int measuredWidth = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(measuredWidth, dp2, IntroActivity.this.bottomPages.getMeasuredWidth() + measuredWidth, IntroActivity.this.bottomPages.getMeasuredHeight() + dp2);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                int measuredHeight = i6 + ((i5 - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2);
                int measuredWidth2 = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(measuredWidth2, measuredHeight, IntroActivity.this.startMessagingButton.getMeasuredWidth() + measuredWidth2, IntroActivity.this.startMessagingButton.getMeasuredHeight() + measuredHeight);
                int dp3 = measuredHeight - AndroidUtilities.dp(30.0f);
                int measuredWidth3 = (getMeasuredWidth() - IntroActivity.this.textView.getMeasuredWidth()) / 2;
                IntroActivity.this.textView.layout(measuredWidth3, dp3 - IntroActivity.this.textView.getMeasuredHeight(), IntroActivity.this.textView.getMeasuredWidth() + measuredWidth3, dp3);
            }
        };
        r4.setBackgroundColor(-1);
        scrollView.addView(r4, LayoutHelper.createScroll(-1, -2, 51));
        FrameLayout frameLayout = new FrameLayout(this);
        this.frameLayout2 = frameLayout;
        r4.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(this);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (IntroActivity.this.eglThread == null && surfaceTexture != null) {
                    EGLThread unused = IntroActivity.this.eglThread = new EGLThread(surfaceTexture);
                    IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
                    IntroActivity.this.eglThread.postRunnable(new IntroActivity$2$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSurfaceTextureAvailable$0() {
                IntroActivity.this.eglThread.drawRunnable.run();
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
        ViewPager viewPager2 = new ViewPager(this);
        this.viewPager = viewPager2;
        viewPager2.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        r4.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
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
        AnonymousClass4 r7 = new TextView(this, this) {
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
                this.cellFlickerDrawable.draw(canvas, rectF, (float) AndroidUtilities.dp(4.0f));
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
        this.startMessagingButton = r7;
        r7.setText(LocaleController.getString("StartMessaging", NUM));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTextColor(-1);
        this.startMessagingButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.startMessagingButton.setTextSize(1, 15.0f);
        this.startMessagingButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r4.addView(this.startMessagingButton, LayoutHelper.createFrame(-1, 42.0f, 81, 36.0f, 0.0f, 36.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda1(this));
        BottomPagesView bottomPagesView = new BottomPagesView(this, this.viewPager, 6);
        this.bottomPages = bottomPagesView;
        r4.addView(bottomPagesView, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(this);
        this.textView = textView2;
        textView2.setTextColor(-15494190);
        this.textView.setGravity(17);
        this.textView.setTextSize(1, 16.0f);
        r4.addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.textView.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda0(this));
        if (AndroidUtilities.isTablet()) {
            FrameLayout frameLayout3 = new FrameLayout(this);
            setContentView(frameLayout3);
            AnonymousClass5 r42 = new SizeNotifierFrameLayout(this, this) {
                /* access modifiers changed from: protected */
                public boolean isActionBarVisible() {
                    return false;
                }
            };
            r42.setOccupyStatusBar(false);
            r42.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            frameLayout3.addView(r42, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout4 = new FrameLayout(this);
            frameLayout4.setBackgroundResource(NUM);
            frameLayout4.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout3.addView(frameLayout4, LayoutHelper.createFrame(498, 528, 17));
        } else {
            setRequestedOrientation(1);
            setContentView(scrollView);
        }
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        checkContinueText();
        this.justCreated = true;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        AndroidUtilities.handleProxyIntent(this, getIntent());
        AndroidUtilities.startAppCenter(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        if (!this.startPressed) {
            this.startPressed = true;
            Intent intent = new Intent(this, LaunchActivity.class);
            intent.putExtra("fromIntro", true);
            startActivity(intent);
            this.destroyed = true;
            finish();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        if (!this.startPressed && this.localeInfo != null) {
            LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
            this.startPressed = true;
            Intent intent = new Intent(this, LaunchActivity.class);
            intent.putExtra("fromIntro", true);
            startActivity(intent);
            this.destroyed = true;
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
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
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0).commit();
    }

    private void checkContinueText() {
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String str = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new IntroActivity$$ExternalSyntheticLambda3(this, str), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkContinueText$3(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (!tLRPC$Vector.objects.isEmpty()) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(0);
                if (tLRPC$LangPackString instanceof TLRPC$TL_langPackString) {
                    AndroidUtilities.runOnUIThread(new IntroActivity$$ExternalSyntheticLambda2(this, tLRPC$LangPackString, str));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkContinueText$2(TLRPC$LangPackString tLRPC$LangPackString, String str) {
        if (!this.destroyed) {
            this.textView.setText(tLRPC$LangPackString.value);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str.toLowerCase()).commit();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack) {
            checkContinueText();
        }
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
            final TextView textView2 = new TextView(viewGroup.getContext());
            AnonymousClass1 r2 = new FrameLayout(this, viewGroup.getContext()) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int dp = (((((i4 - i2) / 4) * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(166.0f);
                    int dp2 = AndroidUtilities.dp(18.0f);
                    TextView textView = textView;
                    textView.layout(dp2, dp, textView.getMeasuredWidth() + dp2, textView.getMeasuredHeight() + dp);
                    int dp3 = dp + AndroidUtilities.dp(42.0f);
                    int dp4 = AndroidUtilities.dp(16.0f);
                    TextView textView2 = textView2;
                    textView2.layout(dp4, dp3, textView2.getMeasuredWidth() + dp4, textView2.getMeasuredHeight() + dp3);
                }
            };
            textView.setTextColor(-14606047);
            textView.setTextSize(1, 26.0f);
            textView.setGravity(17);
            r2.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            textView2.setTextColor(-8355712);
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
                    if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                        Intro.setPage(IntroActivity.this.currentViewPagerPage);
                        Intro.setDate(((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f);
                        Intro.onDrawFrame();
                        EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                        EGLThread.this.postRunnable(new IntroActivity$EGLThread$1$$ExternalSyntheticLambda0(this), 16);
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                EGLThread.this.drawRunnable.run();
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
        private SurfaceTexture surfaceTexture;
        private int[] textures = new int[23];

        public EGLThread(SurfaceTexture surfaceTexture2) {
            super("EGLThread");
            this.surfaceTexture = surfaceTexture2;
        }

        private boolean initGL() {
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
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344}, eGLConfigArr, 1, iArr)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (iArr[0] > 0) {
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
                        this.eglContext.getGL();
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
                        loadTexture(NUM, 17);
                        loadTexture(NUM, 18);
                        loadTexture(NUM, 19);
                        loadTexture(NUM, 20);
                        loadTexture(NUM, 21);
                        loadTexture(NUM, 22);
                        int[] iArr2 = this.textures;
                        Intro.setTelegramTextures(iArr2[22], iArr2[21]);
                        int[] iArr3 = this.textures;
                        Intro.setPowerfulTextures(iArr3[17], iArr3[18], iArr3[16], iArr3[15]);
                        int[] iArr4 = this.textures;
                        Intro.setPrivateTextures(iArr4[19], iArr4[20]);
                        int[] iArr5 = this.textures;
                        Intro.setFreeTextures(iArr5[14], iArr5[13]);
                        int[] iArr6 = this.textures;
                        Intro.setFastTextures(iArr6[2], iArr6[3], iArr6[1], iArr6[0]);
                        int[] iArr7 = this.textures;
                        Intro.setIcTextures(iArr7[4], iArr7[5], iArr7[6], iArr7[7], iArr7[8], iArr7[9], iArr7[10], iArr7[11], iArr7[12]);
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

        private void loadTexture(int i, int i2) {
            Drawable drawable = IntroActivity.this.getResources().getDrawable(i);
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                GLES20.glBindTexture(3553, this.textures[i2]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLUtils.texImage2D(3553, 0, bitmap, 0);
            }
        }

        public void shutdown() {
            postRunnable(new IntroActivity$EGLThread$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$shutdown$0() {
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
}
