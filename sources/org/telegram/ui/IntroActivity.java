package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import org.telegram.messenger.C0446R;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langPackString;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.Components.LayoutHelper;

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
    class C14261 implements SurfaceTextureListener {

        /* renamed from: org.telegram.ui.IntroActivity$1$1 */
        class C14251 implements Runnable {
            C14251() {
            }

            public void run() {
                IntroActivity.this.eglThread.drawRunnable.run();
            }
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        C14261() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (IntroActivity.this.eglThread == null && surfaceTexture != null) {
                IntroActivity.this.eglThread = new EGLThread(surfaceTexture);
                IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
                IntroActivity.this.eglThread.postRunnable(new C14251());
            }
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
    }

    /* renamed from: org.telegram.ui.IntroActivity$3 */
    class C14273 implements OnClickListener {
        C14273() {
        }

        public void onClick(View view) {
            if (IntroActivity.this.startPressed == null) {
                IntroActivity.this.startPressed = true;
                view = new Intent(IntroActivity.this, LaunchActivity.class);
                view.putExtra("fromIntro", true);
                IntroActivity.this.startActivity(view);
                IntroActivity.this.destroyed = true;
                IntroActivity.this.finish();
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$4 */
    class C14284 implements OnLongClickListener {
        C14284() {
        }

        public boolean onLongClick(View view) {
            ConnectionsManager.getInstance(IntroActivity.this.currentAccount).switchBackend();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$5 */
    class C14295 implements OnClickListener {
        C14295() {
        }

        public void onClick(View view) {
            if (IntroActivity.this.startPressed == null) {
                if (IntroActivity.this.localeInfo != null) {
                    LocaleController.getInstance().applyLanguage(IntroActivity.this.localeInfo, true, false, IntroActivity.this.currentAccount);
                    IntroActivity.this.startPressed = true;
                    view = new Intent(IntroActivity.this, LaunchActivity.class);
                    view.putExtra("fromIntro", true);
                    IntroActivity.this.startActivity(view);
                    IntroActivity.this.destroyed = true;
                    IntroActivity.this.finish();
                }
            }
        }
    }

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

        public void setPageOffset(int i, float f) {
            this.progress = f;
            this.scrollPosition = i;
            invalidate();
        }

        public void setCurrentPage(int i) {
            this.currentPage = i;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            int i;
            AndroidUtilities.dp(5.0f);
            this.paint.setColor(-4473925);
            this.currentPage = IntroActivity.this.viewPager.getCurrentItem();
            for (i = 0; i < 6; i++) {
                if (i != this.currentPage) {
                    int dp = AndroidUtilities.dp(11.0f) * i;
                    this.rect.set((float) dp, 0.0f, (float) (dp + AndroidUtilities.dp(5.0f)), (float) AndroidUtilities.dp(5.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
                }
            }
            this.paint.setColor(-13851168);
            i = this.currentPage * AndroidUtilities.dp(11.0f);
            if (this.progress == 0.0f) {
                this.rect.set((float) i, 0.0f, (float) (i + AndroidUtilities.dp(5.0f)), (float) AndroidUtilities.dp(5.0f));
            } else if (this.scrollPosition >= this.currentPage) {
                this.rect.set((float) i, 0.0f, ((float) (i + AndroidUtilities.dp(5.0f))) + (((float) AndroidUtilities.dp(11.0f)) * this.progress), (float) AndroidUtilities.dp(5.0f));
            } else {
                this.rect.set(((float) i) - (((float) AndroidUtilities.dp(11.0f)) * (1.0f - this.progress)), 0.0f, (float) (i + AndroidUtilities.dp(5.0f)), (float) AndroidUtilities.dp(5.0f));
            }
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$2 */
    class C21552 implements OnPageChangeListener {
        C21552() {
        }

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
            if (i == 1) {
                IntroActivity.this.dragging = true;
                IntroActivity.this.startDragX = IntroActivity.this.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
            } else if (i == 0 || i == 2) {
                if (IntroActivity.this.dragging != 0) {
                    IntroActivity.this.justEndDragging = true;
                    IntroActivity.this.dragging = false;
                }
                if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                    IntroActivity.this.lastPage = IntroActivity.this.viewPager.getCurrentItem();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity$6 */
    class C21566 implements RequestDelegate {
        C21566() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                Vector vector = (Vector) tLObject;
                if (vector.objects.isEmpty() == null) {
                    final LangPackString langPackString = (LangPackString) vector.objects.get(null);
                    if ((langPackString instanceof TL_langPackString) != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (!IntroActivity.this.destroyed) {
                                    IntroActivity.this.textView.setText(langPackString.value);
                                    MessagesController.getGlobalMainSettings().edit().putString("language_showed2", LocaleController.getSystemLocaleStringIso639().toLowerCase()).commit();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public class EGLThread extends DispatchQueue {
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private Runnable drawRunnable = new C14321();
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private GL gl;
        private boolean initied;
        private long lastRenderCallTime;
        private int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        private int surfaceWidth;
        private int[] textures = new int[23];

        /* renamed from: org.telegram.ui.IntroActivity$EGLThread$1 */
        class C14321 implements Runnable {

            /* renamed from: org.telegram.ui.IntroActivity$EGLThread$1$1 */
            class C14311 implements Runnable {
                C14311() {
                }

                public void run() {
                    EGLThread.this.drawRunnable.run();
                }
            }

            C14321() {
            }

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
                    EGLThread.this.postRunnable(new C14311(), 16);
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("eglMakeCurrent failed ");
                    stringBuilder.append(GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                    FileLog.m1e(stringBuilder.toString());
                }
            }
        }

        /* renamed from: org.telegram.ui.IntroActivity$EGLThread$2 */
        class C14332 implements Runnable {
            C14332() {
            }

            public void run() {
                EGLThread.this.finish();
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            }
        }

        public EGLThread(SurfaceTexture surfaceTexture) {
            super("EGLThread");
            this.surfaceTexture = surfaceTexture;
        }

        private boolean initGL() {
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("eglGetDisplay failed ");
                    stringBuilder.append(GLUtils.getEGLErrorString(r0.egl10.eglGetError()));
                    FileLog.m1e(stringBuilder.toString());
                }
                finish();
                return false;
            }
            if (r0.egl10.eglInitialize(r0.eglDisplay, new int[2])) {
                int[] iArr = new int[1];
                EGLConfig[] eGLConfigArr = new EGLConfig[1];
                if (!r0.egl10.eglChooseConfig(r0.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344}, eGLConfigArr, 1, iArr)) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("eglChooseConfig failed ");
                        stringBuilder.append(GLUtils.getEGLErrorString(r0.egl10.eglGetError()));
                        FileLog.m1e(stringBuilder.toString());
                    }
                    finish();
                    return false;
                } else if (iArr[0] > 0) {
                    r0.eglConfig = eGLConfigArr[0];
                    r0.eglContext = r0.egl10.eglCreateContext(r0.eglDisplay, r0.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (r0.eglContext == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("eglCreateContext failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(r0.egl10.eglGetError()));
                            FileLog.m1e(stringBuilder.toString());
                        }
                        finish();
                        return false;
                    } else if (r0.surfaceTexture instanceof SurfaceTexture) {
                        r0.eglSurface = r0.egl10.eglCreateWindowSurface(r0.eglDisplay, r0.eglConfig, r0.surfaceTexture, null);
                        if (r0.eglSurface != null) {
                            if (r0.eglSurface != EGL10.EGL_NO_SURFACE) {
                                if (r0.egl10.eglMakeCurrent(r0.eglDisplay, r0.eglSurface, r0.eglSurface, r0.eglContext)) {
                                    r0.gl = r0.eglContext.getGL();
                                    GLES20.glGenTextures(23, r0.textures, 0);
                                    loadTexture(C0446R.drawable.intro_fast_arrow_shadow, 0);
                                    loadTexture(C0446R.drawable.intro_fast_arrow, 1);
                                    loadTexture(C0446R.drawable.intro_fast_body, 2);
                                    loadTexture(C0446R.drawable.intro_fast_spiral, 3);
                                    loadTexture(C0446R.drawable.intro_ic_bubble_dot, 4);
                                    loadTexture(C0446R.drawable.intro_ic_bubble, 5);
                                    loadTexture(C0446R.drawable.intro_ic_cam_lens, 6);
                                    loadTexture(C0446R.drawable.intro_ic_cam, 7);
                                    loadTexture(C0446R.drawable.intro_ic_pencil, 8);
                                    loadTexture(C0446R.drawable.intro_ic_pin, 9);
                                    loadTexture(C0446R.drawable.intro_ic_smile_eye, 10);
                                    loadTexture(C0446R.drawable.intro_ic_smile, 11);
                                    loadTexture(C0446R.drawable.intro_ic_videocam, 12);
                                    loadTexture(C0446R.drawable.intro_knot_down, 13);
                                    loadTexture(C0446R.drawable.intro_knot_up, 14);
                                    loadTexture(C0446R.drawable.intro_powerful_infinity_white, 15);
                                    loadTexture(C0446R.drawable.intro_powerful_infinity, 16);
                                    loadTexture(C0446R.drawable.intro_powerful_mask, 17);
                                    loadTexture(C0446R.drawable.intro_powerful_star, 18);
                                    loadTexture(C0446R.drawable.intro_private_door, 19);
                                    loadTexture(C0446R.drawable.intro_private_screw, 20);
                                    loadTexture(C0446R.drawable.intro_tg_plane, 21);
                                    loadTexture(C0446R.drawable.intro_tg_sphere, 22);
                                    Intro.setTelegramTextures(r0.textures[22], r0.textures[21]);
                                    Intro.setPowerfulTextures(r0.textures[17], r0.textures[18], r0.textures[16], r0.textures[15]);
                                    Intro.setPrivateTextures(r0.textures[19], r0.textures[20]);
                                    Intro.setFreeTextures(r0.textures[14], r0.textures[13]);
                                    Intro.setFastTextures(r0.textures[2], r0.textures[3], r0.textures[1], r0.textures[0]);
                                    Intro.setIcTextures(r0.textures[4], r0.textures[5], r0.textures[6], r0.textures[7], r0.textures[8], r0.textures[9], r0.textures[10], r0.textures[11], r0.textures[12]);
                                    Intro.onSurfaceCreated();
                                    IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
                                    return true;
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("eglMakeCurrent failed ");
                                    stringBuilder.append(GLUtils.getEGLErrorString(r0.egl10.eglGetError()));
                                    FileLog.m1e(stringBuilder.toString());
                                }
                                finish();
                                return false;
                            }
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("createWindowSurface failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(r0.egl10.eglGetError()));
                            FileLog.m1e(stringBuilder.toString());
                        }
                        finish();
                        return false;
                    } else {
                        finish();
                        return false;
                    }
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m1e("eglConfig not initialized");
                    }
                    finish();
                    return false;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("eglInitialize failed ");
                stringBuilder.append(GLUtils.getEGLErrorString(r0.egl10.eglGetError()));
                FileLog.m1e(stringBuilder.toString());
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

        private void loadTexture(int i, int i2) {
            i = IntroActivity.this.getResources().getDrawable(i);
            if (i instanceof BitmapDrawable) {
                i = ((BitmapDrawable) i).getBitmap();
                GLES20.glBindTexture(3553, this.textures[i2]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLUtils.texImage2D(3553, 0, i, 0);
            }
        }

        public void shutdown() {
            postRunnable(new C14332());
        }

        public void setSurfaceTextureSize(int i, int i2) {
            this.surfaceWidth = i;
            this.surfaceHeight = i2;
            Intro.onSurfaceChanged(i, i2, Math.min(((float) this.surfaceWidth) / 150.0f, ((float) this.surfaceHeight) / 150.0f), 0);
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

        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View frameLayout = new FrameLayout(viewGroup.getContext());
            View textView = new TextView(viewGroup.getContext());
            textView.setTextColor(-14606047);
            textView.setTextSize(1, 26.0f);
            textView.setGravity(17);
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            View textView2 = new TextView(viewGroup.getContext());
            textView2.setTextColor(-8355712);
            textView2.setTextSize(1, 15.0f);
            textView2.setGravity(17);
            frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 286.0f, 16.0f, 0.0f));
            viewGroup.addView(frameLayout, 0);
            textView.setText(IntroActivity.this.titles[i]);
            textView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[i]));
            return frameLayout;
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

    protected void onCreate(Bundle bundle) {
        setTheme(C0446R.style.Theme.TMessages);
        super.onCreate(bundle);
        requestWindowFeature(1);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
        this.titles = new String[]{LocaleController.getString("Page1Title", C0446R.string.Page1Title), LocaleController.getString("Page2Title", C0446R.string.Page2Title), LocaleController.getString("Page3Title", C0446R.string.Page3Title), LocaleController.getString("Page5Title", C0446R.string.Page5Title), LocaleController.getString("Page4Title", C0446R.string.Page4Title), LocaleController.getString("Page6Title", C0446R.string.Page6Title)};
        this.messages = new String[]{LocaleController.getString("Page1Message", C0446R.string.Page1Message), LocaleController.getString("Page2Message", C0446R.string.Page2Message), LocaleController.getString("Page3Message", C0446R.string.Page3Message), LocaleController.getString("Page5Message", C0446R.string.Page5Message), LocaleController.getString("Page4Message", C0446R.string.Page4Message), LocaleController.getString("Page6Message", C0446R.string.Page6Message)};
        View scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        View frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(-1);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        View frameLayout2 = new FrameLayout(this);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        View textureView = new TextureView(this);
        frameLayout2.addView(textureView, LayoutHelper.createFrame(Callback.DEFAULT_DRAG_ANIMATION_DURATION, 150, 17));
        textureView.setSurfaceTextureListener(new C14261());
        this.viewPager = new ViewPager(this);
        this.viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new C21552());
        frameLayout2 = new TextView(this);
        frameLayout2.setText(LocaleController.getString("StartMessaging", C0446R.string.StartMessaging).toUpperCase());
        frameLayout2.setGravity(17);
        frameLayout2.setTextColor(-1);
        frameLayout2.setTextSize(1, 16.0f);
        frameLayout2.setBackgroundResource(C0446R.drawable.regbtn_states);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(frameLayout2, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(frameLayout2, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            frameLayout2.setStateListAnimator(stateListAnimator);
        }
        frameLayout2.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, -2.0f, 81, 10.0f, 0.0f, 10.0f, 76.0f));
        frameLayout2.setOnClickListener(new C14273());
        if (BuildVars.DEBUG_VERSION) {
            frameLayout2.setOnLongClickListener(new C14284());
        }
        r0.bottomPages = new BottomPagesView(r0);
        frameLayout.addView(r0.bottomPages, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        r0.textView = new TextView(r0);
        r0.textView.setTextColor(-15494190);
        r0.textView.setGravity(17);
        r0.textView.setTextSize(1, 16.0f);
        frameLayout.addView(r0.textView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        r0.textView.setOnClickListener(new C14295());
        if (AndroidUtilities.isTablet()) {
            frameLayout = new FrameLayout(r0);
            setContentView(frameLayout);
            View imageView = new ImageView(r0);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(C0446R.drawable.catstile);
            bitmapDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            imageView.setBackgroundDrawable(bitmapDrawable);
            frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            imageView = new FrameLayout(r0);
            imageView.setBackgroundResource(C0446R.drawable.btnshadow);
            imageView.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout.addView(imageView, LayoutHelper.createFrame(498, 528, 17));
        } else {
            setRequestedOrientation(1);
            setContentView(scrollView);
        }
        LocaleController.getInstance().loadRemoteLanguages(r0.currentAccount);
        checkContinueText();
        r0.justCreated = true;
        NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.suggestedLangpack);
        AndroidUtilities.handleProxyIntent(r0, getIntent());
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
        LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String toLowerCase = LocaleController.getSystemLocaleStringIso639().toLowerCase();
        int i = 0;
        String str = toLowerCase.contains("-") ? toLowerCase.split("-")[0] : toLowerCase;
        String localeAlias = LocaleController.getLocaleAlias(str);
        LocaleInfo localeInfo = null;
        LocaleInfo localeInfo2 = null;
        while (i < LocaleController.getInstance().languages.size()) {
            LocaleInfo localeInfo3 = (LocaleInfo) LocaleController.getInstance().languages.get(i);
            if (localeInfo3.shortName.equals("en")) {
                localeInfo = localeInfo3;
            }
            if (localeInfo3.shortName.replace("_", "-").equals(toLowerCase) || localeInfo3.shortName.equals(str) || (localeAlias != null && localeInfo3.shortName.equals(localeAlias))) {
                localeInfo2 = localeInfo3;
            }
            if (localeInfo != null && r6 != null) {
                break;
            }
            i++;
        }
        if (!(localeInfo == null || localeInfo2 == null)) {
            if (localeInfo != localeInfo2) {
                TLObject tL_langpack_getStrings = new TL_langpack_getStrings();
                if (localeInfo2 != currentLocaleInfo) {
                    tL_langpack_getStrings.lang_code = localeInfo2.shortName.replace("_", "-");
                    this.localeInfo = localeInfo2;
                } else {
                    tL_langpack_getStrings.lang_code = localeInfo.shortName.replace("_", "-");
                    this.localeInfo = localeInfo;
                }
                tL_langpack_getStrings.keys.add("ContinueOnThisLanguage");
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new C21566(), 8);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack) {
            checkContinueText();
        }
    }
}
