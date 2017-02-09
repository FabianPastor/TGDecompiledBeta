package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
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
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;

public class IntroActivity extends Activity {
    private ViewGroup bottomPages;
    private int[] icons;
    private boolean justCreated = false;
    private int lastPage = 0;
    private int[] messages;
    private boolean startPressed = false;
    private int[] titles;
    private ImageView topImage1;
    private ImageView topImage2;
    private ViewPager viewPager;

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public int getCount() {
            return 7;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.intro_view_layout, null);
            TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
            TextView messageTextView = (TextView) view.findViewById(R.id.message_text);
            container.addView(view, 0);
            headerTextView.setText(IntroActivity.this.getString(IntroActivity.this.titles[position]));
            messageTextView.setText(AndroidUtilities.replaceTags(IntroActivity.this.getString(IntroActivity.this.messages[position])));
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            int count = IntroActivity.this.bottomPages.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = IntroActivity.this.bottomPages.getChildAt(a);
                if (a == position) {
                    child.setBackgroundColor(-13851168);
                } else {
                    child.setBackgroundColor(-4473925);
                }
            }
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
        Theme.loadResources(this);
        requestWindowFeature(1);
        if (AndroidUtilities.isTablet()) {
            setContentView(R.layout.intro_layout_tablet);
            View imageView = findViewById(R.id.background_image_intro);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            imageView.setBackgroundDrawable(drawable);
        } else {
            setRequestedOrientation(1);
            setContentView(R.layout.intro_layout);
        }
        if (LocaleController.isRTL) {
            this.icons = new int[]{R.drawable.intro7, R.drawable.intro6, R.drawable.intro5, R.drawable.intro4, R.drawable.intro3, R.drawable.intro2, R.drawable.intro1};
            this.titles = new int[]{R.string.Page7Title, R.string.Page6Title, R.string.Page5Title, R.string.Page4Title, R.string.Page3Title, R.string.Page2Title, R.string.Page1Title};
            this.messages = new int[]{R.string.Page7Message, R.string.Page6Message, R.string.Page5Message, R.string.Page4Message, R.string.Page3Message, R.string.Page2Message, R.string.Page1Message};
        } else {
            this.icons = new int[]{R.drawable.intro1, R.drawable.intro2, R.drawable.intro3, R.drawable.intro4, R.drawable.intro5, R.drawable.intro6, R.drawable.intro7};
            this.titles = new int[]{R.string.Page1Title, R.string.Page2Title, R.string.Page3Title, R.string.Page4Title, R.string.Page5Title, R.string.Page6Title, R.string.Page7Title};
            this.messages = new int[]{R.string.Page1Message, R.string.Page2Message, R.string.Page3Message, R.string.Page4Message, R.string.Page5Message, R.string.Page6Message, R.string.Page7Message};
        }
        this.viewPager = (ViewPager) findViewById(R.id.intro_view_pager);
        TextView startMessagingButton = (TextView) findViewById(R.id.start_messaging_button);
        startMessagingButton.setText(LocaleController.getString("StartMessaging", R.string.StartMessaging).toUpperCase());
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(startMessagingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(startMessagingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            startMessagingButton.setStateListAnimator(animator);
        }
        this.topImage1 = (ImageView) findViewById(R.id.icon_image1);
        this.topImage2 = (ImageView) findViewById(R.id.icon_image2);
        this.bottomPages = (ViewGroup) findViewById(R.id.bottom_pages);
        this.topImage2.setVisibility(8);
        this.viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
        startMessagingButton.setOnClickListener(new OnClickListener() {
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
            startMessagingButton.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ConnectionsManager.getInstance().switchBackend();
                    return true;
                }
            });
        }
        this.justCreated = true;
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
    }

    protected void onPause() {
        super.onPause();
        AndroidUtilities.unregisterUpdates();
    }
}
