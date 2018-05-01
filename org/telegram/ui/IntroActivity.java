package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
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
import java.util.ArrayList;
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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_langPackString;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.Components.LayoutHelper;

public class IntroActivity
  extends Activity
  implements NotificationCenter.NotificationCenterDelegate
{
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
  private LocaleController.LocaleInfo localeInfo;
  private String[] messages;
  private int startDragX;
  private boolean startPressed = false;
  private TextView textView;
  private String[] titles;
  private ViewPager viewPager;
  
  private void checkContinueText()
  {
    Object localObject1 = null;
    Object localObject2 = null;
    LocaleController.LocaleInfo localLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
    String str1 = LocaleController.getSystemLocaleStringIso639().toLowerCase();
    String str2;
    String str3;
    int i;
    if (str1.contains("-"))
    {
      str2 = str1.split("-")[0];
      str3 = LocaleController.getLocaleAlias(str2);
      i = 0;
    }
    Object localObject3;
    Object localObject4;
    for (;;)
    {
      localObject3 = localObject1;
      localObject4 = localObject2;
      if (i < LocaleController.getInstance().languages.size())
      {
        localObject3 = (LocaleController.LocaleInfo)LocaleController.getInstance().languages.get(i);
        if (((LocaleController.LocaleInfo)localObject3).shortName.equals("en")) {
          localObject1 = localObject3;
        }
        if ((!((LocaleController.LocaleInfo)localObject3).shortName.replace("_", "-").equals(str1)) && (!((LocaleController.LocaleInfo)localObject3).shortName.equals(str2)))
        {
          localObject4 = localObject2;
          if (str3 != null)
          {
            localObject4 = localObject2;
            if (!((LocaleController.LocaleInfo)localObject3).shortName.equals(str3)) {}
          }
        }
        else
        {
          localObject4 = localObject3;
        }
        if ((localObject1 != null) && (localObject4 != null)) {
          localObject3 = localObject1;
        }
      }
      else
      {
        if ((localObject3 != null) && (localObject4 != null) && (localObject3 != localObject4)) {
          break label209;
        }
        return;
        str2 = str1;
        break;
      }
      i++;
      localObject2 = localObject4;
    }
    label209:
    localObject2 = new TLRPC.TL_langpack_getStrings();
    if (localObject4 != localLocaleInfo) {
      ((TLRPC.TL_langpack_getStrings)localObject2).lang_code = ((LocaleController.LocaleInfo)localObject4).shortName.replace("_", "-");
    }
    for (this.localeInfo = ((LocaleController.LocaleInfo)localObject4);; this.localeInfo = ((LocaleController.LocaleInfo)localObject3))
    {
      ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ContinueOnThisLanguage");
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject2, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            paramAnonymousTLObject = (TLRPC.Vector)paramAnonymousTLObject;
            if (!paramAnonymousTLObject.objects.isEmpty()) {
              break label20;
            }
          }
          for (;;)
          {
            return;
            label20:
            paramAnonymousTLObject = (TLRPC.LangPackString)paramAnonymousTLObject.objects.get(0);
            if ((paramAnonymousTLObject instanceof TLRPC.TL_langPackString)) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (!IntroActivity.this.destroyed)
                  {
                    IntroActivity.this.textView.setText(paramAnonymousTLObject.value);
                    MessagesController.getGlobalMainSettings().edit().putString("language_showed2", LocaleController.getSystemLocaleStringIso639().toLowerCase()).commit();
                  }
                }
              });
            }
          }
        }
      }, 8);
      break;
      ((TLRPC.TL_langpack_getStrings)localObject2).lang_code = ((LocaleController.LocaleInfo)localObject3).shortName.replace("_", "-");
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.suggestedLangpack) {
      checkContinueText();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    setTheme(NUM);
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).commit();
    this.titles = new String[] { LocaleController.getString("Page1Title", NUM), LocaleController.getString("Page2Title", NUM), LocaleController.getString("Page3Title", NUM), LocaleController.getString("Page5Title", NUM), LocaleController.getString("Page4Title", NUM), LocaleController.getString("Page6Title", NUM) };
    this.messages = new String[] { LocaleController.getString("Page1Message", NUM), LocaleController.getString("Page2Message", NUM), LocaleController.getString("Page3Message", NUM), LocaleController.getString("Page5Message", NUM), LocaleController.getString("Page4Message", NUM), LocaleController.getString("Page6Message", NUM) };
    paramBundle = new ScrollView(this);
    paramBundle.setFillViewport(true);
    FrameLayout localFrameLayout = new FrameLayout(this);
    localFrameLayout.setBackgroundColor(-1);
    paramBundle.addView(localFrameLayout, LayoutHelper.createScroll(-1, -2, 51));
    Object localObject1 = new FrameLayout(this);
    localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 78.0F, 0.0F, 0.0F));
    Object localObject2 = new TextureView(this);
    ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(200, 150, 17));
    ((TextureView)localObject2).setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
    {
      public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if ((IntroActivity.this.eglThread == null) && (paramAnonymousSurfaceTexture != null))
        {
          IntroActivity.access$102(IntroActivity.this, new IntroActivity.EGLThread(IntroActivity.this, paramAnonymousSurfaceTexture));
          IntroActivity.this.eglThread.setSurfaceTextureSize(paramAnonymousInt1, paramAnonymousInt2);
          IntroActivity.this.eglThread.postRunnable(new Runnable()
          {
            public void run()
            {
              IntroActivity.EGLThread.access$200(IntroActivity.this.eglThread).run();
            }
          });
        }
      }
      
      public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
      {
        if (IntroActivity.this.eglThread != null)
        {
          IntroActivity.this.eglThread.shutdown();
          IntroActivity.access$102(IntroActivity.this, null);
        }
        return true;
      }
      
      public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (IntroActivity.this.eglThread != null) {
          IntroActivity.this.eglThread.setSurfaceTextureSize(paramAnonymousInt1, paramAnonymousInt2);
        }
      }
      
      public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
    });
    this.viewPager = new ViewPager(this);
    this.viewPager.setAdapter(new IntroAdapter(null));
    this.viewPager.setPageMargin(0);
    this.viewPager.setOffscreenPageLimit(1);
    localFrameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0F));
    this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1)
        {
          IntroActivity.access$602(IntroActivity.this, true);
          IntroActivity.access$702(IntroActivity.this, IntroActivity.this.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth());
        }
        for (;;)
        {
          return;
          if ((paramAnonymousInt == 0) || (paramAnonymousInt == 2))
          {
            if (IntroActivity.this.dragging)
            {
              IntroActivity.access$802(IntroActivity.this, true);
              IntroActivity.access$602(IntroActivity.this, false);
            }
            if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
              IntroActivity.access$902(IntroActivity.this, IntroActivity.this.viewPager.getCurrentItem());
            }
          }
        }
      }
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2)
      {
        IntroActivity.this.bottomPages.setPageOffset(paramAnonymousInt1, paramAnonymousFloat);
        paramAnonymousFloat = IntroActivity.this.viewPager.getMeasuredWidth();
        if (paramAnonymousFloat == 0.0F) {}
        for (;;)
        {
          return;
          Intro.setScrollOffset((paramAnonymousInt1 * paramAnonymousFloat + paramAnonymousInt2 - IntroActivity.this.currentViewPagerPage * paramAnonymousFloat) / paramAnonymousFloat);
        }
      }
      
      public void onPageSelected(int paramAnonymousInt)
      {
        IntroActivity.access$502(IntroActivity.this, paramAnonymousInt);
      }
    });
    localObject1 = new TextView(this);
    ((TextView)localObject1).setText(LocaleController.getString("StartMessaging", NUM).toUpperCase());
    ((TextView)localObject1).setGravity(17);
    ((TextView)localObject1).setTextColor(-1);
    ((TextView)localObject1).setTextSize(1, 16.0F);
    ((TextView)localObject1).setBackgroundResource(NUM);
    if (Build.VERSION.SDK_INT >= 21)
    {
      localObject2 = new StateListAnimator();
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(localObject1, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
      ((StateListAnimator)localObject2).addState(new int[] { 16842919 }, localObjectAnimator);
      localObjectAnimator = ObjectAnimator.ofFloat(localObject1, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
      ((StateListAnimator)localObject2).addState(new int[0], localObjectAnimator);
      ((TextView)localObject1).setStateListAnimator((StateListAnimator)localObject2);
    }
    ((TextView)localObject1).setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(10.0F));
    localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 81, 10.0F, 0.0F, 10.0F, 76.0F));
    ((TextView)localObject1).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (IntroActivity.this.startPressed) {}
        for (;;)
        {
          return;
          IntroActivity.access$1002(IntroActivity.this, true);
          paramAnonymousView = new Intent(IntroActivity.this, LaunchActivity.class);
          paramAnonymousView.putExtra("fromIntro", true);
          IntroActivity.this.startActivity(paramAnonymousView);
          IntroActivity.access$1102(IntroActivity.this, true);
          IntroActivity.this.finish();
        }
      }
    });
    if (BuildVars.DEBUG_VERSION) {
      ((TextView)localObject1).setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramAnonymousView)
        {
          ConnectionsManager.getInstance(IntroActivity.this.currentAccount).switchBackend();
          return true;
        }
      });
    }
    this.bottomPages = new BottomPagesView(this);
    localFrameLayout.addView(this.bottomPages, LayoutHelper.createFrame(66, 5.0F, 49, 0.0F, 350.0F, 0.0F, 0.0F));
    this.textView = new TextView(this);
    this.textView.setTextColor(-15494190);
    this.textView.setGravity(17);
    this.textView.setTextSize(1, 16.0F);
    localFrameLayout.addView(this.textView, LayoutHelper.createFrame(-2, 30.0F, 81, 0.0F, 0.0F, 0.0F, 20.0F));
    this.textView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((IntroActivity.this.startPressed) || (IntroActivity.this.localeInfo == null)) {}
        for (;;)
        {
          return;
          LocaleController.getInstance().applyLanguage(IntroActivity.this.localeInfo, true, false, IntroActivity.this.currentAccount);
          IntroActivity.access$1002(IntroActivity.this, true);
          paramAnonymousView = new Intent(IntroActivity.this, LaunchActivity.class);
          paramAnonymousView.putExtra("fromIntro", true);
          IntroActivity.this.startActivity(paramAnonymousView);
          IntroActivity.access$1102(IntroActivity.this, true);
          IntroActivity.this.finish();
        }
      }
    });
    if (AndroidUtilities.isTablet())
    {
      localFrameLayout = new FrameLayout(this);
      setContentView(localFrameLayout);
      localObject2 = new ImageView(this);
      localObject1 = (BitmapDrawable)getResources().getDrawable(NUM);
      ((BitmapDrawable)localObject1).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
      ((View)localObject2).setBackgroundDrawable((Drawable)localObject1);
      localFrameLayout.addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F));
      localObject2 = new FrameLayout(this);
      ((FrameLayout)localObject2).setBackgroundResource(NUM);
      ((FrameLayout)localObject2).addView(paramBundle, LayoutHelper.createFrame(-1, -1.0F));
      localFrameLayout.addView((View)localObject2, LayoutHelper.createFrame(498, 528, 17));
    }
    for (;;)
    {
      LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
      checkContinueText();
      this.justCreated = true;
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
      AndroidUtilities.handleProxyIntent(this, getIntent());
      return;
      setRequestedOrientation(1);
      setContentView(paramBundle);
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    this.destroyed = true;
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0L).commit();
  }
  
  protected void onPause()
  {
    super.onPause();
    AndroidUtilities.unregisterUpdates();
    ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.justCreated)
    {
      if (!LocaleController.isRTL) {
        break label58;
      }
      this.viewPager.setCurrentItem(6);
    }
    for (this.lastPage = 6;; this.lastPage = 0)
    {
      this.justCreated = false;
      AndroidUtilities.checkForCrashes(this);
      AndroidUtilities.checkForUpdates(this);
      ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
      return;
      label58:
      this.viewPager.setCurrentItem(0);
    }
  }
  
  private class BottomPagesView
    extends View
  {
    private float animatedProgress;
    private int currentPage;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private Paint paint = new Paint(1);
    private float progress;
    private RectF rect = new RectF();
    private int scrollPosition;
    
    public BottomPagesView(Context paramContext)
    {
      super();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      float f = AndroidUtilities.dp(5.0F);
      this.paint.setColor(-4473925);
      this.currentPage = IntroActivity.this.viewPager.getCurrentItem();
      int i = 0;
      if (i < 6)
      {
        if (i == this.currentPage) {}
        for (;;)
        {
          i++;
          break;
          int j = i * AndroidUtilities.dp(11.0F);
          this.rect.set(j, 0.0F, AndroidUtilities.dp(5.0F) + j, AndroidUtilities.dp(5.0F));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.5F), AndroidUtilities.dp(2.5F), this.paint);
        }
      }
      this.paint.setColor(-13851168);
      i = this.currentPage * AndroidUtilities.dp(11.0F);
      if (this.progress != 0.0F) {
        if (this.scrollPosition >= this.currentPage) {
          this.rect.set(i, 0.0F, AndroidUtilities.dp(5.0F) + i + AndroidUtilities.dp(11.0F) * this.progress, AndroidUtilities.dp(5.0F));
        }
      }
      for (;;)
      {
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.5F), AndroidUtilities.dp(2.5F), this.paint);
        return;
        this.rect.set(i - AndroidUtilities.dp(11.0F) * (1.0F - this.progress), 0.0F, AndroidUtilities.dp(5.0F) + i, AndroidUtilities.dp(5.0F));
        continue;
        this.rect.set(i, 0.0F, AndroidUtilities.dp(5.0F) + i, AndroidUtilities.dp(5.0F));
      }
    }
    
    public void setCurrentPage(int paramInt)
    {
      this.currentPage = paramInt;
      invalidate();
    }
    
    public void setPageOffset(int paramInt, float paramFloat)
    {
      this.progress = paramFloat;
      this.scrollPosition = paramInt;
      invalidate();
    }
  }
  
  public class EGLThread
    extends DispatchQueue
  {
    private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private final int EGL_OPENGL_ES2_BIT = 4;
    private Runnable drawRunnable = new Runnable()
    {
      public void run()
      {
        if (!IntroActivity.EGLThread.this.initied) {}
        for (;;)
        {
          return;
          if (((!IntroActivity.EGLThread.this.eglContext.equals(IntroActivity.EGLThread.this.egl10.eglGetCurrentContext())) || (!IntroActivity.EGLThread.this.eglSurface.equals(IntroActivity.EGLThread.this.egl10.eglGetCurrentSurface(12377)))) && (!IntroActivity.EGLThread.this.egl10.eglMakeCurrent(IntroActivity.EGLThread.this.eglDisplay, IntroActivity.EGLThread.this.eglSurface, IntroActivity.EGLThread.this.eglSurface, IntroActivity.EGLThread.this.eglContext)))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(IntroActivity.EGLThread.this.egl10.eglGetError()));
            }
          }
          else
          {
            float f = (float)(System.currentTimeMillis() - IntroActivity.this.currentDate) / 1000.0F;
            Intro.setPage(IntroActivity.this.currentViewPagerPage);
            Intro.setDate(f);
            Intro.onDrawFrame();
            IntroActivity.EGLThread.this.egl10.eglSwapBuffers(IntroActivity.EGLThread.this.eglDisplay, IntroActivity.EGLThread.this.eglSurface);
            IntroActivity.EGLThread.this.postRunnable(new Runnable()
            {
              public void run()
              {
                IntroActivity.EGLThread.this.drawRunnable.run();
              }
            }, 16L);
          }
        }
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
    private int surfaceHeight;
    private SurfaceTexture surfaceTexture;
    private int surfaceWidth;
    private int[] textures = new int[23];
    
    public EGLThread(SurfaceTexture paramSurfaceTexture)
    {
      super();
      this.surfaceTexture = paramSurfaceTexture;
    }
    
    private boolean initGL()
    {
      this.egl10 = ((EGL10)EGLContext.getEGL());
      this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
      boolean bool;
      if (this.eglDisplay == EGL10.EGL_NO_DISPLAY)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        }
        finish();
        bool = false;
      }
      for (;;)
      {
        return bool;
        int[] arrayOfInt = new int[2];
        if (!this.egl10.eglInitialize(this.eglDisplay, arrayOfInt))
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          }
          finish();
          bool = false;
        }
        else
        {
          arrayOfInt = new int[1];
          EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
          if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344 }, arrayOfEGLConfig, 1, arrayOfInt))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            }
            finish();
            bool = false;
          }
          else
          {
            if (arrayOfInt[0] > 0)
            {
              this.eglConfig = arrayOfEGLConfig[0];
              this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
              if (this.eglContext == null)
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                bool = false;
              }
            }
            else
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.e("eglConfig not initialized");
              }
              finish();
              bool = false;
              continue;
            }
            if ((this.surfaceTexture instanceof SurfaceTexture))
            {
              this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
              if ((this.eglSurface == null) || (this.eglSurface == EGL10.EGL_NO_SURFACE))
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                bool = false;
              }
            }
            else
            {
              finish();
              bool = false;
              continue;
            }
            if (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
              }
              finish();
              bool = false;
            }
            else
            {
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
              Intro.setTelegramTextures(this.textures[22], this.textures[21]);
              Intro.setPowerfulTextures(this.textures[17], this.textures[18], this.textures[16], this.textures[15]);
              Intro.setPrivateTextures(this.textures[19], this.textures[20]);
              Intro.setFreeTextures(this.textures[14], this.textures[13]);
              Intro.setFastTextures(this.textures[2], this.textures[3], this.textures[1], this.textures[0]);
              Intro.setIcTextures(this.textures[4], this.textures[5], this.textures[6], this.textures[7], this.textures[8], this.textures[9], this.textures[10], this.textures[11], this.textures[12]);
              Intro.onSurfaceCreated();
              IntroActivity.access$1702(IntroActivity.this, System.currentTimeMillis() - 1000L);
              bool = true;
            }
          }
        }
      }
    }
    
    private void loadTexture(int paramInt1, int paramInt2)
    {
      Object localObject = IntroActivity.this.getResources().getDrawable(paramInt1);
      if ((localObject instanceof BitmapDrawable))
      {
        localObject = ((BitmapDrawable)localObject).getBitmap();
        GLES20.glBindTexture(3553, this.textures[paramInt2]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLUtils.texImage2D(3553, 0, (Bitmap)localObject, 0);
      }
    }
    
    public void finish()
    {
      if (this.eglSurface != null)
      {
        this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
        this.eglSurface = null;
      }
      if (this.eglContext != null)
      {
        this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.eglContext = null;
      }
      if (this.eglDisplay != null)
      {
        this.egl10.eglTerminate(this.eglDisplay);
        this.eglDisplay = null;
      }
    }
    
    public void run()
    {
      this.initied = initGL();
      super.run();
    }
    
    public void setSurfaceTextureSize(int paramInt1, int paramInt2)
    {
      this.surfaceWidth = paramInt1;
      this.surfaceHeight = paramInt2;
      Intro.onSurfaceChanged(paramInt1, paramInt2, Math.min(this.surfaceWidth / 150.0F, this.surfaceHeight / 150.0F), 0);
    }
    
    public void shutdown()
    {
      postRunnable(new Runnable()
      {
        public void run()
        {
          IntroActivity.EGLThread.this.finish();
          Looper localLooper = Looper.myLooper();
          if (localLooper != null) {
            localLooper.quit();
          }
        }
      });
    }
  }
  
  private class IntroAdapter
    extends PagerAdapter
  {
    private IntroAdapter() {}
    
    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      paramViewGroup.removeView((View)paramObject);
    }
    
    public int getCount()
    {
      return IntroActivity.this.titles.length;
    }
    
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      FrameLayout localFrameLayout = new FrameLayout(paramViewGroup.getContext());
      TextView localTextView1 = new TextView(paramViewGroup.getContext());
      localTextView1.setTextColor(-14606047);
      localTextView1.setTextSize(1, 26.0F);
      localTextView1.setGravity(17);
      localFrameLayout.addView(localTextView1, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 244.0F, 18.0F, 0.0F));
      TextView localTextView2 = new TextView(paramViewGroup.getContext());
      localTextView2.setTextColor(-8355712);
      localTextView2.setTextSize(1, 15.0F);
      localTextView2.setGravity(17);
      localFrameLayout.addView(localTextView2, LayoutHelper.createFrame(-1, -2.0F, 51, 16.0F, 286.0F, 16.0F, 0.0F));
      paramViewGroup.addView(localFrameLayout, 0);
      localTextView1.setText(IntroActivity.this.titles[paramInt]);
      localTextView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[paramInt]));
      return localFrameLayout;
    }
    
    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView.equals(paramObject);
    }
    
    public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {}
    
    public Parcelable saveState()
    {
      return null;
    }
    
    public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      super.setPrimaryItem(paramViewGroup, paramInt, paramObject);
      IntroActivity.this.bottomPages.setCurrentPage(paramInt);
      IntroActivity.access$502(IntroActivity.this, paramInt);
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null) {
        super.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/IntroActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */