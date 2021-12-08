package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import javax.microedition.khronos.opengles.GL;
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
import org.telegram.tgnet.TLRPC;
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
    public void onCreate(Bundle savedInstanceState) {
        setTheme(NUM);
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
        this.titles = new String[]{LocaleController.getString("Page1Title", NUM), LocaleController.getString("Page2Title", NUM), LocaleController.getString("Page3Title", NUM), LocaleController.getString("Page5Title", NUM), LocaleController.getString("Page4Title", NUM), LocaleController.getString("Page6Title", NUM)};
        this.messages = new String[]{LocaleController.getString("Page1Message", NUM), LocaleController.getString("Page2Message", NUM), LocaleController.getString("Page3Message", NUM), LocaleController.getString("Page5Message", NUM), LocaleController.getString("Page4Message", NUM), LocaleController.getString("Page6Message", NUM)};
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(this) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int oneFourth = (bottom - top) / 4;
                int y = ((oneFourth * 3) - AndroidUtilities.dp(275.0f)) / 2;
                IntroActivity.this.frameLayout2.layout(0, y, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + y);
                int y2 = y + AndroidUtilities.dp(272.0f);
                int x = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(x, y2, IntroActivity.this.bottomPages.getMeasuredWidth() + x, IntroActivity.this.bottomPages.getMeasuredHeight() + y2);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                int y3 = (oneFourth * 3) + ((oneFourth - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2);
                int x2 = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(x2, y3, IntroActivity.this.startMessagingButton.getMeasuredWidth() + x2, IntroActivity.this.startMessagingButton.getMeasuredHeight() + y3);
                int y4 = y3 - AndroidUtilities.dp(30.0f);
                int x3 = (getMeasuredWidth() - IntroActivity.this.textView.getMeasuredWidth()) / 2;
                IntroActivity.this.textView.layout(x3, y4 - IntroActivity.this.textView.getMeasuredHeight(), IntroActivity.this.textView.getMeasuredWidth() + x3, y4);
            }
        };
        frameLayout.setBackgroundColor(-1);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        FrameLayout frameLayout3 = new FrameLayout(this);
        this.frameLayout2 = frameLayout3;
        frameLayout.addView(frameLayout3, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(this);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (IntroActivity.this.eglThread == null && surface != null) {
                    EGLThread unused = IntroActivity.this.eglThread = new EGLThread(surface);
                    IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
                    IntroActivity.this.eglThread.postRunnable(new IntroActivity$2$$ExternalSyntheticLambda0(this));
                }
            }

            /* renamed from: lambda$onSurfaceTextureAvailable$0$org-telegram-ui-IntroActivity$2  reason: not valid java name */
            public /* synthetic */ void m3049x770CLASSNAMEa() {
                IntroActivity.this.eglThread.drawRunnable.run();
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
        ViewPager viewPager2 = new ViewPager(this);
        this.viewPager = viewPager2;
        viewPager2.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
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
        AnonymousClass4 r9 = new TextView(this) {
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
        this.startMessagingButton = r9;
        r9.setText(LocaleController.getString("StartMessaging", NUM));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTextColor(-1);
        this.startMessagingButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.startMessagingButton.setTextSize(1, 15.0f);
        this.startMessagingButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        frameLayout.addView(this.startMessagingButton, LayoutHelper.createFrame(-1, 42.0f, 81, 36.0f, 0.0f, 36.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda0(this));
        BottomPagesView bottomPagesView = new BottomPagesView(this, this.viewPager, 6);
        this.bottomPages = bottomPagesView;
        frameLayout.addView(bottomPagesView, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(this);
        this.textView = textView2;
        textView2.setTextColor(-15494190);
        this.textView.setGravity(17);
        this.textView.setTextSize(1, 16.0f);
        frameLayout.addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.textView.setOnClickListener(new IntroActivity$$ExternalSyntheticLambda1(this));
        if (AndroidUtilities.isTablet()) {
            FrameLayout frameLayout32 = new FrameLayout(this);
            setContentView(frameLayout32);
            SizeNotifierFrameLayout backgroundTablet = new SizeNotifierFrameLayout(this) {
                /* access modifiers changed from: protected */
                public boolean isActionBarVisible() {
                    return false;
                }
            };
            backgroundTablet.setOccupyStatusBar(false);
            backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            frameLayout32.addView(backgroundTablet, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout4 = new FrameLayout(this);
            frameLayout4.setBackgroundResource(NUM);
            frameLayout4.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout32.addView(frameLayout4, LayoutHelper.createFrame(498, 528, 17));
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

    /* renamed from: lambda$onCreate$0$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m3047lambda$onCreate$0$orgtelegramuiIntroActivity(View view) {
        if (!this.startPressed) {
            this.startPressed = true;
            Intent intent2 = new Intent(this, LaunchActivity.class);
            intent2.putExtra("fromIntro", true);
            startActivity(intent2);
            this.destroyed = true;
            finish();
        }
    }

    /* renamed from: lambda$onCreate$1$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m3048lambda$onCreate$1$orgtelegramuiIntroActivity(View v) {
        if (!this.startPressed && this.localeInfo != null) {
            LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
            this.startPressed = true;
            Intent intent2 = new Intent(this, LaunchActivity.class);
            intent2.putExtra("fromIntro", true);
            startActivity(intent2);
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
        LocaleController.LocaleInfo englishInfo = null;
        LocaleController.LocaleInfo systemInfo = null;
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String systemLang = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new IntroActivity$$ExternalSyntheticLambda3(this, systemLang), 8);
        }
    }

    /* renamed from: lambda$checkContinueText$3$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m3046lambda$checkContinueText$3$orgtelegramuiIntroActivity(String systemLang, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (!vector.objects.isEmpty()) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(0);
                if (string instanceof TLRPC.TL_langPackString) {
                    AndroidUtilities.runOnUIThread(new IntroActivity$$ExternalSyntheticLambda2(this, string, systemLang));
                }
            }
        }
    }

    /* renamed from: lambda$checkContinueText$2$org-telegram-ui-IntroActivity  reason: not valid java name */
    public /* synthetic */ void m3045lambda$checkContinueText$2$orgtelegramuiIntroActivity(TLRPC.LangPackString string, String systemLang) {
        if (!this.destroyed) {
            this.textView.setText(string.value);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang.toLowerCase()).commit();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack) {
            checkContinueText();
        }
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            final TextView headerTextView = new TextView(container.getContext());
            final TextView messageTextView = new TextView(container.getContext());
            FrameLayout frameLayout = new FrameLayout(container.getContext()) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int y = (((((bottom - top) / 4) * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(166.0f);
                    int x = AndroidUtilities.dp(18.0f);
                    TextView textView = headerTextView;
                    textView.layout(x, y, textView.getMeasuredWidth() + x, headerTextView.getMeasuredHeight() + y);
                    int y2 = y + AndroidUtilities.dp(42.0f);
                    int x2 = AndroidUtilities.dp(16.0f);
                    TextView textView2 = messageTextView;
                    textView2.layout(x2, y2, textView2.getMeasuredWidth() + x2, messageTextView.getMeasuredHeight() + y2);
                }
            };
            headerTextView.setTextColor(-14606047);
            headerTextView.setTextSize(1, 26.0f);
            headerTextView.setGravity(17);
            frameLayout.addView(headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            messageTextView.setTextColor(-8355712);
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

            /* renamed from: lambda$run$0$org-telegram-ui-IntroActivity$EGLThread$1  reason: not valid java name */
            public /* synthetic */ void m3051lambda$run$0$orgtelegramuiIntroActivity$EGLThread$1() {
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
        private GL gl;
        /* access modifiers changed from: private */
        public boolean initied;
        private long lastRenderCallTime;
        private SurfaceTexture surfaceTexture;
        private int[] textures = new int[23];

        public EGLThread(SurfaceTexture surface) {
            super("EGLThread");
            this.surfaceTexture = surface;
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
            if (!this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344}, configs, 1, configsCount)) {
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
                    this.gl = this.eglContext.getGL();
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
                    int[] iArr = this.textures;
                    Intro.setTelegramTextures(iArr[22], iArr[21]);
                    int[] iArr2 = this.textures;
                    Intro.setPowerfulTextures(iArr2[17], iArr2[18], iArr2[16], iArr2[15]);
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

        private void loadTexture(int resId, int index) {
            Drawable drawable = IntroActivity.this.getResources().getDrawable(resId);
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                GLES20.glBindTexture(3553, this.textures[index]);
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

        /* renamed from: lambda$shutdown$0$org-telegram-ui-IntroActivity$EGLThread  reason: not valid java name */
        public /* synthetic */ void m3050lambda$shutdown$0$orgtelegramuiIntroActivity$EGLThread() {
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
}
