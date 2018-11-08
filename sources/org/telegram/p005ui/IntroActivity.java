package org.telegram.p005ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0541R;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Intro;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langPackString;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.Vector;

/* renamed from: org.telegram.ui.IntroActivity */
public class IntroActivity extends Activity implements NotificationCenterDelegate {
    private BottomPagesView bottomPages;
    private int currentAccount = UserConfig.selectedAccount;
    private long currentDate;
    private int currentViewPagerPage;
    private boolean destroyed;
    private boolean dragging;
    private EGLThread eglThread;
    private boolean justCreated = false;
    private boolean justEndDragging;
    private int lastPage = 0;
    private LocaleInfo localeInfo;
    private String[] messages;
    private int startDragX;
    private boolean startPressed = false;
    private TextView textView;
    private String[] titles;
    private ViewPager viewPager;

    /* renamed from: org.telegram.ui.IntroActivity$1 */
    class C17561 implements SurfaceTextureListener {

        /* renamed from: org.telegram.ui.IntroActivity$1$1 */
        class C17551 implements Runnable {
            C17551() {
            }

            public void run() {
                IntroActivity.this.eglThread.drawRunnable.run();
            }
        }

        C17561() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (IntroActivity.this.eglThread == null && surface != null) {
                IntroActivity.this.eglThread = new EGLThread(surface);
                IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
                IntroActivity.this.eglThread.postRunnable(new C17551());
            }
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (IntroActivity.this.eglThread != null) {
                IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (IntroActivity.this.eglThread != null) {
                IntroActivity.this.eglThread.shutdown();
                IntroActivity.this.eglThread = null;
            }
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$2 */
    class C17572 implements OnPageChangeListener {
        C17572() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            IntroActivity.this.bottomPages.setPageOffset(position, positionOffset);
            float width = (float) IntroActivity.this.viewPager.getMeasuredWidth();
            if (width != 0.0f) {
                Intro.setScrollOffset((((((float) position) * width) + ((float) positionOffsetPixels)) - (((float) IntroActivity.this.currentViewPagerPage) * width)) / width);
            }
        }

        public void onPageSelected(int i) {
            IntroActivity.this.currentViewPagerPage = i;
        }

        public void onPageScrollStateChanged(int i) {
            if (i == 1) {
                IntroActivity.this.dragging = true;
                IntroActivity.this.startDragX = IntroActivity.this.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
            } else if (i == 0 || i == 2) {
                if (IntroActivity.this.dragging) {
                    IntroActivity.this.justEndDragging = true;
                    IntroActivity.this.dragging = false;
                }
                if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                    IntroActivity.this.lastPage = IntroActivity.this.viewPager.getCurrentItem();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$3 */
    class C17583 implements OnClickListener {
        C17583() {
        }

        public void onClick(View view) {
            if (!IntroActivity.this.startPressed) {
                IntroActivity.this.startPressed = true;
                Intent intent2 = new Intent(IntroActivity.this, LaunchActivity.class);
                intent2.putExtra("fromIntro", true);
                IntroActivity.this.startActivity(intent2);
                IntroActivity.this.destroyed = true;
                IntroActivity.this.finish();
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$4 */
    class C17594 implements OnLongClickListener {
        C17594() {
        }

        public boolean onLongClick(View v) {
            ConnectionsManager.getInstance(IntroActivity.this.currentAccount).switchBackend();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$5 */
    class C17605 implements OnClickListener {
        C17605() {
        }

        public void onClick(View v) {
            if (!IntroActivity.this.startPressed && IntroActivity.this.localeInfo != null) {
                LocaleController.getInstance().applyLanguage(IntroActivity.this.localeInfo, true, false, IntroActivity.this.currentAccount);
                IntroActivity.this.startPressed = true;
                Intent intent2 = new Intent(IntroActivity.this, LaunchActivity.class);
                intent2.putExtra("fromIntro", true);
                IntroActivity.this.startActivity(intent2);
                IntroActivity.this.destroyed = true;
                IntroActivity.this.finish();
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$6 */
    class C17626 implements RequestDelegate {
        C17626() {
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                Vector vector = (Vector) response;
                if (!vector.objects.isEmpty()) {
                    final LangPackString string = (LangPackString) vector.objects.get(0);
                    if (string instanceof TL_langPackString) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (!IntroActivity.this.destroyed) {
                                    IntroActivity.this.textView.setText(string.value);
                                    MessagesController.getGlobalMainSettings().edit().putString("language_showed2", LocaleController.getSystemLocaleStringIso639().toLowerCase()).commit();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$BottomPagesView */
    private class BottomPagesView extends View {
        private float animatedProgress;
        private int currentPage;
        private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        private Paint paint = new Paint(1);
        private float progress;
        private RectF rect = new RectF();
        private int scrollPosition;

        public BottomPagesView(Context context) {
            super(context);
        }

        public void setPageOffset(int position, float offset) {
            this.progress = offset;
            this.scrollPosition = position;
            invalidate();
        }

        public void setCurrentPage(int page) {
            this.currentPage = page;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            int x;
            float d = (float) AndroidUtilities.m10dp(5.0f);
            this.paint.setColor(-4473925);
            this.currentPage = IntroActivity.this.viewPager.getCurrentItem();
            for (int a = 0; a < 6; a++) {
                if (a != this.currentPage) {
                    x = a * AndroidUtilities.m10dp(11.0f);
                    this.rect.set((float) x, 0.0f, (float) (AndroidUtilities.m10dp(5.0f) + x), (float) AndroidUtilities.m10dp(5.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m10dp(2.5f), (float) AndroidUtilities.m10dp(2.5f), this.paint);
                }
            }
            this.paint.setColor(-13851168);
            x = this.currentPage * AndroidUtilities.m10dp(11.0f);
            if (this.progress == 0.0f) {
                this.rect.set((float) x, 0.0f, (float) (AndroidUtilities.m10dp(5.0f) + x), (float) AndroidUtilities.m10dp(5.0f));
            } else if (this.scrollPosition >= this.currentPage) {
                this.rect.set((float) x, 0.0f, ((float) (AndroidUtilities.m10dp(5.0f) + x)) + (((float) AndroidUtilities.m10dp(11.0f)) * this.progress), (float) AndroidUtilities.m10dp(5.0f));
            } else {
                this.rect.set(((float) x) - (((float) AndroidUtilities.m10dp(11.0f)) * (1.0f - this.progress)), 0.0f, (float) (AndroidUtilities.m10dp(5.0f) + x), (float) AndroidUtilities.m10dp(5.0f));
            }
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m10dp(2.5f), (float) AndroidUtilities.m10dp(2.5f), this.paint);
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$EGLThread */
    public class EGLThread extends DispatchQueue {
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private Runnable drawRunnable = new C17641();
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        /* renamed from: gl */
        private GL f272gl;
        private boolean initied;
        private long lastRenderCallTime;
        private int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        private int surfaceWidth;
        private int[] textures = new int[23];

        /* renamed from: org.telegram.ui.IntroActivity$EGLThread$1 */
        class C17641 implements Runnable {

            /* renamed from: org.telegram.ui.IntroActivity$EGLThread$1$1 */
            class C17631 implements Runnable {
                C17631() {
                }

                public void run() {
                    EGLThread.this.drawRunnable.run();
                }
            }

            C17641() {
            }

            public void run() {
                if (!EGLThread.this.initied) {
                    return;
                }
                if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                    float time = ((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f;
                    Intro.setPage(IntroActivity.this.currentViewPagerPage);
                    Intro.setDate(time);
                    Intro.onDrawFrame();
                    EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    EGLThread.this.postRunnable(new C17631(), 16);
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.m12e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                }
            }
        }

        /* renamed from: org.telegram.ui.IntroActivity$EGLThread$2 */
        class C17652 implements Runnable {
            C17652() {
            }

            public void run() {
                EGLThread.this.finish();
                Looper looper = Looper.myLooper();
                if (looper != null) {
                    looper.quit();
                }
            }
        }

        public EGLThread(SurfaceTexture surface) {
            super("EGLThread");
            this.surfaceTexture = surface;
        }

        private boolean initGL() {
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m12e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                int[] configsCount = new int[1];
                EGLConfig[] configs = new EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344}, configs, 1, configsCount)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m12e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                } else if (configsCount[0] > 0) {
                    this.eglConfig = configs[0];
                    int[] iArr = new int[3];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m12e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (this.surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                        if (this.eglSurface == null || this.eglSurface == EGL10.EGL_NO_SURFACE) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m12e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            }
                            finish();
                            return false;
                        } else if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                            this.f272gl = this.eglContext.getGL();
                            GLES20.glGenTextures(23, this.textures, 0);
                            loadTexture(C0541R.drawable.intro_fast_arrow_shadow, 0);
                            loadTexture(C0541R.drawable.intro_fast_arrow, 1);
                            loadTexture(C0541R.drawable.intro_fast_body, 2);
                            loadTexture(C0541R.drawable.intro_fast_spiral, 3);
                            loadTexture(C0541R.drawable.intro_ic_bubble_dot, 4);
                            loadTexture(C0541R.drawable.intro_ic_bubble, 5);
                            loadTexture(C0541R.drawable.intro_ic_cam_lens, 6);
                            loadTexture(C0541R.drawable.intro_ic_cam, 7);
                            loadTexture(C0541R.drawable.intro_ic_pencil, 8);
                            loadTexture(C0541R.drawable.intro_ic_pin, 9);
                            loadTexture(C0541R.drawable.intro_ic_smile_eye, 10);
                            loadTexture(C0541R.drawable.intro_ic_smile, 11);
                            loadTexture(C0541R.drawable.intro_ic_videocam, 12);
                            loadTexture(C0541R.drawable.intro_knot_down, 13);
                            loadTexture(C0541R.drawable.intro_knot_up, 14);
                            loadTexture(C0541R.drawable.intro_powerful_infinity_white, 15);
                            loadTexture(C0541R.drawable.intro_powerful_infinity, 16);
                            loadTexture(C0541R.drawable.intro_powerful_mask, 17);
                            loadTexture(C0541R.drawable.intro_powerful_star, 18);
                            loadTexture(C0541R.drawable.intro_private_door, 19);
                            loadTexture(C0541R.drawable.intro_private_screw, 20);
                            loadTexture(C0541R.drawable.intro_tg_plane, 21);
                            loadTexture(C0541R.drawable.intro_tg_sphere, 22);
                            Intro.setTelegramTextures(this.textures[22], this.textures[21]);
                            Intro.setPowerfulTextures(this.textures[17], this.textures[18], this.textures[16], this.textures[15]);
                            Intro.setPrivateTextures(this.textures[19], this.textures[20]);
                            Intro.setFreeTextures(this.textures[14], this.textures[13]);
                            Intro.setFastTextures(this.textures[2], this.textures[3], this.textures[1], this.textures[0]);
                            Intro.setIcTextures(this.textures[4], this.textures[5], this.textures[6], this.textures[7], this.textures[8], this.textures[9], this.textures[10], this.textures[11], this.textures[12]);
                            Intro.onSurfaceCreated();
                            IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
                            return true;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m12e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            }
                            finish();
                            return false;
                        }
                    } else {
                        finish();
                        return false;
                    }
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m12e("eglConfig not initialized");
                    }
                    finish();
                    return false;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            }
            finish();
            return false;
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            if (this.eglContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
                this.eglContext = null;
            }
            if (this.eglDisplay != null) {
                this.egl10.eglTerminate(this.eglDisplay);
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
            postRunnable(new C17652());
        }

        public void setSurfaceTextureSize(int width, int height) {
            this.surfaceWidth = width;
            this.surfaceHeight = height;
            Intro.onSurfaceChanged(width, height, Math.min(((float) this.surfaceWidth) / 150.0f, ((float) this.surfaceHeight) / 150.0f), 0);
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$IntroAdapter */
    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        /* synthetic */ IntroAdapter(IntroActivity x0, C17561 x1) {
            this();
        }

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            FrameLayout frameLayout = new FrameLayout(container.getContext());
            TextView headerTextView = new TextView(container.getContext());
            headerTextView.setTextColor(-14606047);
            headerTextView.setTextSize(1, 26.0f);
            headerTextView.setGravity(17);
            frameLayout.addView(headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            TextView messageTextView = new TextView(container.getContext());
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
            IntroActivity.this.currentViewPagerPage = position;
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

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(C0541R.style.Theme.TMessages);
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
        this.titles = new String[]{LocaleController.getString("Page1Title", C0541R.string.Page1Title), LocaleController.getString("Page2Title", C0541R.string.Page2Title), LocaleController.getString("Page3Title", C0541R.string.Page3Title), LocaleController.getString("Page5Title", C0541R.string.Page5Title), LocaleController.getString("Page4Title", C0541R.string.Page4Title), LocaleController.getString("Page6Title", C0541R.string.Page6Title)};
        this.messages = new String[]{LocaleController.getString("Page1Message", C0541R.string.Page1Message), LocaleController.getString("Page2Message", C0541R.string.Page2Message), LocaleController.getString("Page3Message", C0541R.string.Page3Message), LocaleController.getString("Page5Message", C0541R.string.Page5Message), LocaleController.getString("Page4Message", C0541R.string.Page4Message), LocaleController.getString("Page6Message", C0541R.string.Page6Message)};
        View scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(-1);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        FrameLayout frameLayout2 = new FrameLayout(this);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        scrollView = new TextureView(this);
        frameLayout2.addView(scrollView, LayoutHelper.createFrame(Callback.DEFAULT_DRAG_ANIMATION_DURATION, 150, 17));
        scrollView.setSurfaceTextureListener(new C17561());
        this.viewPager = new ViewPager(this);
        this.viewPager.setAdapter(new IntroAdapter(this, null));
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new C17572());
        scrollView = new TextView(this);
        scrollView.setText(LocaleController.getString("StartMessaging", C0541R.string.StartMessaging).toUpperCase());
        scrollView.setGravity(17);
        scrollView.setTextColor(-1);
        scrollView.setTextSize(1, 16.0f);
        scrollView.setBackgroundResource(C0541R.drawable.regbtn_states);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            scrollView = scrollView;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(scrollView, "translationZ", new float[]{(float) AndroidUtilities.m10dp(2.0f), (float) AndroidUtilities.m10dp(4.0f)}).setDuration(200));
            scrollView = scrollView;
            animator.addState(new int[0], ObjectAnimator.ofFloat(scrollView, "translationZ", new float[]{(float) AndroidUtilities.m10dp(4.0f), (float) AndroidUtilities.m10dp(2.0f)}).setDuration(200));
            scrollView.setStateListAnimator(animator);
        }
        scrollView.setPadding(AndroidUtilities.m10dp(20.0f), AndroidUtilities.m10dp(10.0f), AndroidUtilities.m10dp(20.0f), AndroidUtilities.m10dp(10.0f));
        frameLayout.addView(scrollView, LayoutHelper.createFrame(-2, -2.0f, 81, 10.0f, 0.0f, 10.0f, 76.0f));
        scrollView.setOnClickListener(new C17583());
        if (BuildVars.DEBUG_VERSION) {
            scrollView.setOnLongClickListener(new C17594());
        }
        this.bottomPages = new BottomPagesView(this);
        frameLayout.addView(this.bottomPages, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        this.textView = new TextView(this);
        this.textView.setTextColor(-15494190);
        this.textView.setGravity(17);
        this.textView.setTextSize(1, 16.0f);
        frameLayout.addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.textView.setOnClickListener(new C17605());
        if (AndroidUtilities.isTablet()) {
            FrameLayout frameLayout3 = new FrameLayout(this);
            setContentView(frameLayout3);
            View imageView = new ImageView(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(C0541R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            imageView.setBackgroundDrawable(drawable);
            frameLayout3.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout4 = new FrameLayout(this);
            frameLayout4.setBackgroundResource(C0541R.drawable.btnshadow);
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
    }

    protected void onResume() {
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

    protected void onPause() {
        super.onPause();
        AndroidUtilities.unregisterUpdates();
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0).commit();
    }

    private void checkContinueText() {
        String arg;
        LocaleInfo englishInfo = null;
        LocaleInfo systemInfo = null;
        LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String systemLang = LocaleController.getSystemLocaleStringIso639().toLowerCase();
        if (systemLang.contains("-")) {
            arg = systemLang.split("-")[0];
        } else {
            arg = systemLang;
        }
        String alias = LocaleController.getLocaleAlias(arg);
        for (int a = 0; a < LocaleController.getInstance().languages.size(); a++) {
            LocaleInfo info = (LocaleInfo) LocaleController.getInstance().languages.get(a);
            if (info.shortName.equals("en")) {
                englishInfo = info;
            }
            if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || (alias != null && info.shortName.equals(alias))) {
                systemInfo = info;
            }
            if (englishInfo != null && systemInfo != null) {
                break;
            }
        }
        if (englishInfo != null && systemInfo != null && englishInfo != systemInfo) {
            TL_langpack_getStrings req = new TL_langpack_getStrings();
            if (systemInfo != currentLocaleInfo) {
                req.lang_code = systemInfo.shortName.replace("_", "-");
                this.localeInfo = systemInfo;
            } else {
                req.lang_code = englishInfo.shortName.replace("_", "-");
                this.localeInfo = englishInfo;
            }
            req.keys.add("ContinueOnThisLanguage");
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C17626(), 8);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack) {
            checkContinueText();
        }
    }
}
