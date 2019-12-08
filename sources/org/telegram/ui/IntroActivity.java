package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
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
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langPackString;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;

public class IntroActivity extends Activity implements NotificationCenterDelegate {
    private BottomPagesView bottomPages;
    private int currentAccount = UserConfig.selectedAccount;
    private long currentDate;
    private int currentViewPagerPage;
    private boolean destroyed;
    private boolean dragging;
    private EGLThread eglThread;
    private FrameLayout frameLayout2;
    private boolean justCreated = false;
    private boolean justEndDragging;
    private int lastPage = 0;
    private LocaleInfo localeInfo;
    private String[] messages;
    private int startDragX;
    private TextView startMessagingButton;
    private boolean startPressed = false;
    private TextView textView;
    private String[] titles;
    private ViewPager viewPager;

    public class EGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private Runnable drawRunnable = new Runnable() {
            public void run() {
                if (!EGLThread.this.initied) {
                    return;
                }
                if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                    float currentTimeMillis = ((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f;
                    Intro.setPage(IntroActivity.this.currentViewPagerPage);
                    Intro.setDate(currentTimeMillis);
                    Intro.onDrawFrame();
                    EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    EGLThread.this.postRunnable(new -$$Lambda$IntroActivity$EGLThread$1$t5CDZ7wR8GgPufsAxg7RJ2ghGUs(this), 16);
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("eglMakeCurrent failed ");
                    stringBuilder.append(GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                    FileLog.e(stringBuilder.toString());
                }
            }

            public /* synthetic */ void lambda$run$0$IntroActivity$EGLThread$1() {
                EGLThread.this.drawRunnable.run();
            }
        };
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private GL gl;
        private boolean initied;
        private long lastRenderCallTime;
        private SurfaceTexture surfaceTexture;
        private int[] textures = new int[23];

        public EGLThread(SurfaceTexture surfaceTexture) {
            super("EGLThread");
            this.surfaceTexture = surfaceTexture;
        }

        private boolean initGL() {
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            EGLDisplay eGLDisplay = this.eglDisplay;
            StringBuilder stringBuilder;
            if (eGLDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("eglGetDisplay failed ");
                    stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    FileLog.e(stringBuilder.toString());
                }
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(eGLDisplay, new int[2])) {
                int[] iArr = new int[1];
                EGLConfig[] eGLConfigArr = new EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344}, eGLConfigArr, 1, iArr)) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("eglChooseConfig failed ");
                        stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        FileLog.e(stringBuilder.toString());
                    }
                    finish();
                    return false;
                } else if (iArr[0] > 0) {
                    this.eglConfig = eGLConfigArr[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("eglCreateContext failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            FileLog.e(stringBuilder.toString());
                        }
                        finish();
                        return false;
                    }
                    SurfaceTexture surfaceTexture = this.surfaceTexture;
                    if (surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture, null);
                        EGLSurface eGLSurface = this.eglSurface;
                        if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("createWindowSurface failed ");
                                stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                FileLog.e(stringBuilder.toString());
                            }
                            finish();
                            return false;
                        } else if (this.egl10.eglMakeCurrent(this.eglDisplay, eGLSurface, eGLSurface, this.eglContext)) {
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
                            int[] iArr2 = this.textures;
                            Intro.setTelegramTextures(iArr2[22], iArr2[21]);
                            iArr2 = this.textures;
                            Intro.setPowerfulTextures(iArr2[17], iArr2[18], iArr2[16], iArr2[15]);
                            iArr2 = this.textures;
                            Intro.setPrivateTextures(iArr2[19], iArr2[20]);
                            iArr2 = this.textures;
                            Intro.setFreeTextures(iArr2[14], iArr2[13]);
                            iArr2 = this.textures;
                            Intro.setFastTextures(iArr2[2], iArr2[3], iArr2[1], iArr2[0]);
                            int[] iArr3 = this.textures;
                            Intro.setIcTextures(iArr3[4], iArr3[5], iArr3[6], iArr3[7], iArr3[8], iArr3[9], iArr3[10], iArr3[11], iArr3[12]);
                            Intro.onSurfaceCreated();
                            IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
                            return true;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("eglMakeCurrent failed ");
                                stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                FileLog.e(stringBuilder.toString());
                            }
                            finish();
                            return false;
                        }
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
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("eglInitialize failed ");
                stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                FileLog.e(stringBuilder.toString());
            }
            finish();
            return false;
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl10 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
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
            postRunnable(new -$$Lambda$IntroActivity$EGLThread$AriDXNPGmTpsZXAD4AD83qoD230(this));
        }

        public /* synthetic */ void lambda$shutdown$0$IntroActivity$EGLThread() {
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

    private class IntroAdapter extends PagerAdapter {
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        private IntroAdapter() {
        }

        /* synthetic */ IntroAdapter(IntroActivity introActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            final TextView textView = new TextView(viewGroup.getContext());
            final TextView textView2 = new TextView(viewGroup.getContext());
            AnonymousClass1 anonymousClass1 = new FrameLayout(viewGroup.getContext()) {
                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    i4 = (((((i4 - i2) / 4) * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(166.0f);
                    int dp = AndroidUtilities.dp(18.0f);
                    TextView textView = textView;
                    textView.layout(dp, i4, textView.getMeasuredWidth() + dp, textView.getMeasuredHeight() + i4);
                    i4 += AndroidUtilities.dp(42.0f);
                    dp = AndroidUtilities.dp(16.0f);
                    textView = textView2;
                    textView.layout(dp, i4, textView.getMeasuredWidth() + dp, textView2.getMeasuredHeight() + i4);
                }
            };
            textView.setTextColor(-14606047);
            textView.setTextSize(1, 26.0f);
            textView.setGravity(17);
            anonymousClass1.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            textView2.setTextColor(-8355712);
            textView2.setTextSize(1, 15.0f);
            textView2.setGravity(17);
            anonymousClass1.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 286.0f, 16.0f, 0.0f));
            viewGroup.addView(anonymousClass1, 0);
            textView.setText(IntroActivity.this.titles[i]);
            textView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[i]));
            return anonymousClass1;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
            IntroActivity.this.bottomPages.setCurrentPage(i);
            IntroActivity.this.currentViewPagerPage = i;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        setTheme(NUM);
        super.onCreate(bundle);
        requestWindowFeature(1);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
        this.titles = new String[]{LocaleController.getString("Page1Title", NUM), LocaleController.getString("Page2Title", NUM), LocaleController.getString("Page3Title", NUM), LocaleController.getString("Page5Title", NUM), LocaleController.getString("Page4Title", NUM), LocaleController.getString("Page6Title", NUM)};
        this.messages = new String[]{LocaleController.getString("Page1Message", NUM), LocaleController.getString("Page2Message", NUM), LocaleController.getString("Page3Message", NUM), LocaleController.getString("Page5Message", NUM), LocaleController.getString("Page4Message", NUM), LocaleController.getString("Page6Message", NUM)};
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        AnonymousClass1 anonymousClass1 = new FrameLayout(this) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                i4 = (i4 - i2) / 4;
                int i5 = i4 * 3;
                i = (i5 - AndroidUtilities.dp(275.0f)) / 2;
                IntroActivity.this.frameLayout2.layout(0, i, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + i);
                i += AndroidUtilities.dp(272.0f);
                i2 = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(i2, i, IntroActivity.this.bottomPages.getMeasuredWidth() + i2, IntroActivity.this.bottomPages.getMeasuredHeight() + i);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                i5 += (i4 - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2;
                i = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(i, i5, IntroActivity.this.startMessagingButton.getMeasuredWidth() + i, IntroActivity.this.startMessagingButton.getMeasuredHeight() + i5);
                i5 -= AndroidUtilities.dp(30.0f);
                i = (getMeasuredWidth() - IntroActivity.this.textView.getMeasuredWidth()) / 2;
                IntroActivity.this.textView.layout(i, i5 - IntroActivity.this.textView.getMeasuredHeight(), IntroActivity.this.textView.getMeasuredWidth() + i, i5);
            }
        };
        anonymousClass1.setBackgroundColor(-1);
        scrollView.addView(anonymousClass1, LayoutHelper.createScroll(-1, -2, 51));
        this.frameLayout2 = new FrameLayout(this);
        anonymousClass1.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(this);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (IntroActivity.this.eglThread == null && surfaceTexture != null) {
                    IntroActivity introActivity = IntroActivity.this;
                    introActivity.eglThread = new EGLThread(surfaceTexture);
                    IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
                    IntroActivity.this.eglThread.postRunnable(new -$$Lambda$IntroActivity$2$URUK1fqEwJXQIUQEi_Jx6E7c8mo(this));
                }
            }

            public /* synthetic */ void lambda$onSurfaceTextureAvailable$0$IntroActivity$2() {
                IntroActivity.this.eglThread.drawRunnable.run();
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                if (IntroActivity.this.eglThread != null) {
                    IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (IntroActivity.this.eglThread != null) {
                    IntroActivity.this.eglThread.shutdown();
                    IntroActivity.this.eglThread = null;
                }
                return true;
            }
        });
        this.viewPager = new ViewPager(this);
        this.viewPager.setAdapter(new IntroAdapter(this, null));
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        anonymousClass1.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int i, float f, int i2) {
                IntroActivity.this.bottomPages.setPageOffset(i, f);
                f = (float) IntroActivity.this.viewPager.getMeasuredWidth();
                if (f != 0.0f) {
                    Intro.setScrollOffset((((((float) i) * f) + ((float) i2)) - (((float) IntroActivity.this.currentViewPagerPage) * f)) / f);
                }
            }

            public void onPageSelected(int i) {
                IntroActivity.this.currentViewPagerPage = i;
            }

            public void onPageScrollStateChanged(int i) {
                IntroActivity introActivity;
                if (i == 1) {
                    IntroActivity.this.dragging = true;
                    introActivity = IntroActivity.this;
                    introActivity.startDragX = introActivity.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
                } else if (i == 0 || i == 2) {
                    if (IntroActivity.this.dragging) {
                        IntroActivity.this.justEndDragging = true;
                        IntroActivity.this.dragging = false;
                    }
                    if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                        introActivity = IntroActivity.this;
                        introActivity.lastPage = introActivity.viewPager.getCurrentItem();
                    }
                }
            }
        });
        this.startMessagingButton = new TextView(this);
        this.startMessagingButton.setText(LocaleController.getString("StartMessaging", NUM));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTextColor(-1);
        this.startMessagingButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.startMessagingButton.setTextSize(1, 14.0f);
        this.startMessagingButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        anonymousClass1.addView(this.startMessagingButton, LayoutHelper.createFrame(-2, 42.0f, 81, 10.0f, 0.0f, 10.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new -$$Lambda$IntroActivity$Kg_leHKna32gZHR3CZT09b4whCI(this));
        if (BuildVars.DEBUG_VERSION) {
            this.startMessagingButton.setOnLongClickListener(new -$$Lambda$IntroActivity$iyZ2QRC4zfDIPF0e6T1a3cQJl5Y(this));
        }
        this.bottomPages = new BottomPagesView(this, this.viewPager, 6);
        anonymousClass1.addView(this.bottomPages, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        this.textView = new TextView(this);
        this.textView.setTextColor(-15494190);
        this.textView.setGravity(17);
        this.textView.setTextSize(1, 16.0f);
        anonymousClass1.addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.textView.setOnClickListener(new -$$Lambda$IntroActivity$3V-J2Y1xEuzoe0ERCQjOPYz9JM8(this));
        if (AndroidUtilities.isTablet()) {
            FrameLayout frameLayout = new FrameLayout(this);
            setContentView(frameLayout);
            ImageView imageView = new ImageView(this);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(NUM);
            TileMode tileMode = TileMode.REPEAT;
            bitmapDrawable.setTileModeXY(tileMode, tileMode);
            imageView.setBackgroundDrawable(bitmapDrawable);
            frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout2 = new FrameLayout(this);
            frameLayout2.setBackgroundResource(NUM);
            frameLayout2.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(498, 528, 17));
        } else {
            setRequestedOrientation(1);
            setContentView(scrollView);
        }
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        checkContinueText();
        this.justCreated = true;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        AndroidUtilities.handleProxyIntent(this, getIntent());
    }

    public /* synthetic */ void lambda$onCreate$0$IntroActivity(View view) {
        if (!this.startPressed) {
            this.startPressed = true;
            Intent intent = new Intent(this, LaunchActivity.class);
            intent.putExtra("fromIntro", true);
            startActivity(intent);
            this.destroyed = true;
            finish();
        }
    }

    public /* synthetic */ boolean lambda$onCreate$1$IntroActivity(View view) {
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
        return true;
    }

    public /* synthetic */ void lambda$onCreate$2$IntroActivity(View view) {
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

    /* Access modifiers changed, original: protected */
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
        AndroidUtilities.checkForCrashes(this);
        AndroidUtilities.checkForUpdates(this);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        super.onPause();
        AndroidUtilities.unregisterUpdates();
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
    }

    /* Access modifiers changed, original: protected */
    public void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0).commit();
    }

    private void checkContinueText() {
        LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String str = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
        String str2 = "-";
        int i = 0;
        String str3 = str.contains(str2) ? str.split(str2)[0] : str;
        String localeAlias = LocaleController.getLocaleAlias(str3);
        LocaleInfo localeInfo = null;
        LocaleInfo localeInfo2 = null;
        while (i < LocaleController.getInstance().languages.size()) {
            LocaleInfo localeInfo3 = (LocaleInfo) LocaleController.getInstance().languages.get(i);
            if (localeInfo3.shortName.equals("en")) {
                localeInfo = localeInfo3;
            }
            if (localeInfo3.shortName.replace("_", str2).equals(str) || localeInfo3.shortName.equals(str3) || localeInfo3.shortName.equals(localeAlias)) {
                localeInfo2 = localeInfo3;
            }
            if (localeInfo != null && r7 != null) {
                break;
            }
            i++;
        }
        if (localeInfo != null && localeInfo2 != null && localeInfo != localeInfo2) {
            TL_langpack_getStrings tL_langpack_getStrings = new TL_langpack_getStrings();
            if (localeInfo2 != currentLocaleInfo) {
                tL_langpack_getStrings.lang_code = localeInfo2.getLangCode();
                this.localeInfo = localeInfo2;
            } else {
                tL_langpack_getStrings.lang_code = localeInfo.getLangCode();
                this.localeInfo = localeInfo;
            }
            tL_langpack_getStrings.keys.add("ContinueOnThisLanguage");
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new -$$Lambda$IntroActivity$v08WUA33DIz9dwdLeC-Z0gtPf-4(this, str), 8);
        }
    }

    public /* synthetic */ void lambda$checkContinueText$4$IntroActivity(String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            if (!vector.objects.isEmpty()) {
                LangPackString langPackString = (LangPackString) vector.objects.get(0);
                if (langPackString instanceof TL_langPackString) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$IntroActivity$xVDraVCLASSNAMEXqfCcnZOtPiiDOx4JQ(this, langPackString, str));
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$3$IntroActivity(LangPackString langPackString, String str) {
        if (!this.destroyed) {
            this.textView.setText(langPackString.value);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str.toLowerCase()).commit();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack) {
            checkContinueText();
        }
    }
}
