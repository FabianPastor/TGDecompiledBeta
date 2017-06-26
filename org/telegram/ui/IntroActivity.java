package org.telegram.ui;

import android.animation.AnimatorSet;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
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
    private int[] icons;
    private boolean justCreated = false;
    private int lastPage = 0;
    private LocaleInfo localeInfo;
    private int[] messages;
    private String[] messagesString;
    private boolean startPressed = false;
    private TextView textView;
    private int[] titles;
    private String[] titlesString;
    private ImageView topImage1;
    private ImageView topImage2;
    private ViewPager viewPager;

    private class BottomPagesView extends View {
        private float animatedProgress;
        private AnimatorSet animatorSet;
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
            if (this.animatorSet != null) {
                this.animatorSet.cancel();
                this.animatorSet = null;
            }
            this.scrollPosition = position;
            invalidate();
        }

        public float getAnimatedProgress() {
            return this.animatedProgress;
        }

        public void setAnimatedProgress(float value) {
            this.animatedProgress = value;
            invalidate();
        }

        public void setCurrentPage(int page) {
            this.currentPage = page;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            int x;
            float d = (float) AndroidUtilities.dp(5.0f);
            this.paint.setColor(-4473925);
            this.currentPage = IntroActivity.this.viewPager.getCurrentItem();
            for (int a = 0; a < 7; a++) {
                if (a != this.currentPage) {
                    x = a * AndroidUtilities.dp(11.0f);
                    this.rect.set((float) x, 0.0f, (float) (AndroidUtilities.dp(5.0f) + x), (float) AndroidUtilities.dp(5.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
                }
            }
            this.paint.setColor(-13851168);
            x = this.currentPage * AndroidUtilities.dp(11.0f);
            if (this.animatorSet != null) {
                this.progress = this.animatedProgress;
            }
            if (this.progress == 0.0f) {
                this.rect.set((float) x, 0.0f, (float) (AndroidUtilities.dp(5.0f) + x), (float) AndroidUtilities.dp(5.0f));
            } else if (this.scrollPosition >= this.currentPage) {
                this.rect.set((float) x, 0.0f, ((float) (AndroidUtilities.dp(5.0f) + x)) + (((float) AndroidUtilities.dp(11.0f)) * this.progress), (float) AndroidUtilities.dp(5.0f));
            } else {
                this.rect.set(((float) x) - (((float) AndroidUtilities.dp(11.0f)) * (1.0f - this.progress)), 0.0f, (float) (AndroidUtilities.dp(5.0f) + x), (float) AndroidUtilities.dp(5.0f));
            }
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
        }
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public int getCount() {
            return 7;
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
            headerTextView.setText(LocaleController.getString(IntroActivity.this.titlesString[position], IntroActivity.this.titles[position]));
            messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString(IntroActivity.this.messagesString[position], IntroActivity.this.messages[position])));
            return frameLayout;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            IntroActivity.this.bottomPages.setCurrentPage(position);
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
        setTheme(R.style.Theme.TMessages);
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if (LocaleController.isRTL) {
            this.icons = new int[]{R.drawable.intro7, R.drawable.intro6, R.drawable.intro5, R.drawable.intro4, R.drawable.intro3, R.drawable.intro2, R.drawable.intro1};
            this.titles = new int[]{R.string.Page7Title, R.string.Page6Title, R.string.Page5Title, R.string.Page4Title, R.string.Page3Title, R.string.Page2Title, R.string.Page1Title};
            this.messages = new int[]{R.string.Page7Message, R.string.Page6Message, R.string.Page5Message, R.string.Page4Message, R.string.Page3Message, R.string.Page2Message, R.string.Page1Message};
        } else {
            this.icons = new int[]{R.drawable.intro1, R.drawable.intro2, R.drawable.intro3, R.drawable.intro4, R.drawable.intro5, R.drawable.intro6, R.drawable.intro7};
            this.titles = new int[]{R.string.Page1Title, R.string.Page2Title, R.string.Page3Title, R.string.Page4Title, R.string.Page5Title, R.string.Page6Title, R.string.Page7Title};
            this.titlesString = new String[]{"Page1Title", "Page2Title", "Page3Title", "Page4Title", "Page5Title", "Page6Title", "Page7Title"};
            this.messages = new int[]{R.string.Page1Message, R.string.Page2Message, R.string.Page3Message, R.string.Page4Message, R.string.Page5Message, R.string.Page6Message, R.string.Page7Message};
            this.messagesString = new String[]{"Page1Message", "Page2Message", "Page3Message", "Page4Message", "Page5Message", "Page6Message", "Page7Message"};
        }
        View scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(-328966);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        FrameLayout frameLayout2 = new FrameLayout(this);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
        this.topImage1 = new ImageView(this);
        this.topImage1.setImageResource(R.drawable.intro1);
        frameLayout2.addView(this.topImage1, LayoutHelper.createFrame(-2, -2, 17));
        this.topImage2 = new ImageView(this);
        this.topImage2.setVisibility(8);
        frameLayout2.addView(this.topImage2, LayoutHelper.createFrame(-2, -2, 17));
        this.viewPager = new ViewPager(this);
        IntroActivity introActivity = this;
        this.viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                IntroActivity.this.bottomPages.setPageOffset(position, positionOffset);
            }

            public void onPageSelected(int i) {
            }

            public void onPageScrollStateChanged(int i) {
                if ((i == 0 || i == 2) && IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                    ImageView fadeoutImage;
                    ImageView fadeinImage;
                    IntroActivity.this.lastPage = IntroActivity.this.viewPager.getCurrentItem();
                    if (IntroActivity.this.topImage1.getVisibility() == 0) {
                        fadeoutImage = IntroActivity.this.topImage1;
                        fadeinImage = IntroActivity.this.topImage2;
                    } else {
                        fadeoutImage = IntroActivity.this.topImage2;
                        fadeinImage = IntroActivity.this.topImage1;
                    }
                    fadeinImage.bringToFront();
                    fadeinImage.setImageResource(IntroActivity.this.icons[IntroActivity.this.lastPage]);
                    fadeinImage.clearAnimation();
                    fadeoutImage.clearAnimation();
                    Animation outAnimation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.icon_anim_fade_out);
                    outAnimation.setAnimationListener(new AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            fadeoutImage.setVisibility(8);
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    Animation inAnimation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.icon_anim_fade_in);
                    inAnimation.setAnimationListener(new AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                            fadeinImage.setVisibility(0);
                        }

                        public void onAnimationEnd(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    fadeoutImage.startAnimation(outAnimation);
                    fadeinImage.startAnimation(inAnimation);
                }
            }
        });
        scrollView = new TextView(this);
        scrollView.setText(LocaleController.getString("StartMessaging", R.string.StartMessaging).toUpperCase());
        scrollView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        scrollView.setGravity(17);
        scrollView.setTextColor(-1);
        scrollView.setTextSize(1, 16.0f);
        scrollView.setBackgroundResource(R.drawable.regbtn_states);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            scrollView = scrollView;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(scrollView, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            scrollView = scrollView;
            animator.addState(new int[0], ObjectAnimator.ofFloat(scrollView, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            scrollView.setStateListAnimator(animator);
        }
        frameLayout.addView(scrollView, LayoutHelper.createFrame(-2, -2.0f, 81, 10.0f, 0.0f, 10.0f, 76.0f));
        scrollView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!IntroActivity.this.startPressed) {
                    IntroActivity.this.startPressed = true;
                    Intent intent2 = new Intent(IntroActivity.this, LaunchActivity.class);
                    intent2.putExtra("fromIntro", true);
                    IntroActivity.this.startActivity(intent2);
                    IntroActivity.this.finish();
                }
            }
        });
        if (BuildVars.DEBUG_VERSION) {
            scrollView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ConnectionsManager.getInstance().switchBackend();
                    return true;
                }
            });
        }
        this.bottomPages = new BottomPagesView(this);
        frameLayout.addView(this.bottomPages, LayoutHelper.createFrame(77, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        this.textView = new TextView(this);
        this.textView.setTextColor(-15494190);
        this.textView.setGravity(17);
        this.textView.setTextSize(1, 16.0f);
        frameLayout.addView(this.textView, LayoutHelper.createFrame(-2, BitmapDescriptorFactory.HUE_ORANGE, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.textView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!IntroActivity.this.startPressed && IntroActivity.this.localeInfo != null) {
                    LocaleController.getInstance().applyLanguage(IntroActivity.this.localeInfo, true);
                    IntroActivity.this.startPressed = true;
                    Intent intent2 = new Intent(IntroActivity.this, LaunchActivity.class);
                    intent2.putExtra("fromIntro", true);
                    IntroActivity.this.startActivity(intent2);
                    IntroActivity.this.finish();
                }
            }
        });
        if (AndroidUtilities.isTablet()) {
            FrameLayout frameLayout3 = new FrameLayout(this);
            setContentView(frameLayout3);
            View imageView = new ImageView(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            imageView.setBackgroundDrawable(drawable);
            frameLayout3.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout4 = new FrameLayout(this);
            frameLayout4.setBackgroundResource(R.drawable.btnshadow);
            frameLayout4.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout3.addView(frameLayout4, LayoutHelper.createFrame(498, 528, 17));
        } else {
            setRequestedOrientation(1);
            setContentView(scrollView);
        }
        checkContinueText();
        this.justCreated = true;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.suggestedLangpack);
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
        ConnectionsManager.getInstance().setAppPaused(false, false);
    }

    protected void onPause() {
        super.onPause();
        AndroidUtilities.unregisterUpdates();
        ConnectionsManager.getInstance().setAppPaused(true, false);
    }

    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    private void checkContinueText() {
        LocaleInfo englishInfo = null;
        LocaleInfo systemInfo = null;
        String systemLang = LocaleController.getSystemLocaleStringIso639();
        String arg;
        if (systemLang.contains("-")) {
            arg = systemLang.split("-")[0];
        } else {
            arg = systemLang;
        }
        for (int a = 0; a < LocaleController.getInstance().remoteLanguages.size(); a++) {
            LocaleInfo info = (LocaleInfo) LocaleController.getInstance().remoteLanguages.get(a);
            if (info.shortName.equals("en")) {
                englishInfo = info;
            }
            if (info.shortName.equals(systemLang) || info.shortName.equals(arg)) {
                systemInfo = info;
            }
            if (englishInfo != null && systemInfo != null) {
                break;
            }
        }
        if (englishInfo != null && systemInfo != null && englishInfo != systemInfo) {
            this.localeInfo = systemInfo;
            TL_langpack_getStrings req = new TL_langpack_getStrings();
            req.lang_code = systemInfo.shortName;
            req.keys.add("ContinueOnThisLanguage");
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (response != null) {
                        Vector vector = (Vector) response;
                        if (vector.objects.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    IntroActivity.this.textView.setText(LocaleController.getString("ContinueOnThisLanguage", R.string.ContinueOnThisLanguage));
                                }
                            });
                            return;
                        }
                        final LangPackString string = (LangPackString) vector.objects.get(0);
                        if (string instanceof TL_langPackString) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    IntroActivity.this.textView.setText(string.value);
                                }
                            });
                        }
                    }
                }
            }, 8);
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.suggestedLangpack) {
            checkContinueText();
        }
    }
}
