package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanReplacement;

public class IdenticonActivity extends BaseFragment implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private int chat_id;
    private TextView codeTextView;
    private FrameLayout container;
    private ImageView emojiButton;
    private boolean emojiSelected;
    private TextView emojiTextView;
    private LinearLayout linearLayout1;
    private TextView textView;
    private int textWidth;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public IdenticonActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id");
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EncryptionKey", R.string.EncryptionKey));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    IdenticonActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(1);
        linearLayout.setWeightSum(100.0f);
        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        ImageView identiconView = new ImageView(context);
        identiconView.setScaleType(ScaleType.FIT_XY);
        frameLayout.addView(identiconView, LayoutHelper.createFrame(-1, -1.0f));
        this.container = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (IdenticonActivity.this.codeTextView != null && IdenticonActivity.this.emojiButton != null) {
                    int x = ((right - left) - IdenticonActivity.this.emojiButton.getMeasuredWidth()) - AndroidUtilities.dp(5.0f);
                    int y = ((IdenticonActivity.this.codeTextView.getMeasuredHeight() - AndroidUtilities.dp(68.0f)) / 2) + IdenticonActivity.this.linearLayout1.getTop();
                    IdenticonActivity.this.emojiButton.layout(x, y, IdenticonActivity.this.emojiButton.getMeasuredWidth() + x, IdenticonActivity.this.emojiButton.getMeasuredHeight() + y);
                    x = (IdenticonActivity.this.codeTextView.getLeft() + (IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2)) - (IdenticonActivity.this.emojiTextView.getMeasuredWidth() / 2);
                    y = (((IdenticonActivity.this.codeTextView.getMeasuredHeight() - IdenticonActivity.this.emojiTextView.getMeasuredHeight()) / 2) + IdenticonActivity.this.linearLayout1.getTop()) - AndroidUtilities.dp(16.0f);
                    IdenticonActivity.this.emojiTextView.layout(x, y, IdenticonActivity.this.emojiTextView.getMeasuredWidth() + x, IdenticonActivity.this.emojiTextView.getMeasuredHeight() + y);
                }
            }
        };
        this.container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(this.container, LayoutHelper.createLinear(-1, -1, 50.0f));
        this.linearLayout1 = new LinearLayout(context);
        this.linearLayout1.setOrientation(1);
        this.linearLayout1.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.container.addView(this.linearLayout1, LayoutHelper.createFrame(-2, -2, 17));
        this.codeTextView = new TextView(context);
        this.codeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.codeTextView.setGravity(17);
        this.codeTextView.setTypeface(Typeface.MONOSPACE);
        this.codeTextView.setTextSize(1, 16.0f);
        this.linearLayout1.addView(this.codeTextView, LayoutHelper.createLinear(-2, -2, 1));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLinksClickable(true);
        this.textView.setClickable(true);
        this.textView.setGravity(17);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.linearLayout1.addView(this.textView, LayoutHelper.createFrame(-2, -2, 1));
        this.emojiButton = new ImageView(context);
        this.emojiButton.setImageResource(R.drawable.ic_smiles2_smile);
        this.emojiButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            this.emojiButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        }
        this.container.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f));
        this.emojiButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                IdenticonActivity.this.emojiSelected = !IdenticonActivity.this.emojiSelected;
                IdenticonActivity.this.updateEmojiButton(true);
            }
        });
        this.emojiTextView = new TextView(context);
        this.emojiTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.emojiTextView.setGravity(17);
        this.emojiTextView.setTextSize(1, 32.0f);
        this.container.addView(this.emojiTextView, LayoutHelper.createFrame(-2, -2.0f));
        EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            IdenticonDrawable drawable = new IdenticonDrawable();
            identiconView.setImageDrawable(drawable);
            drawable.setEncryptedChat(encryptedChat);
            User user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
            SpannableStringBuilder hash = new SpannableStringBuilder();
            StringBuilder emojis = new StringBuilder();
            if (encryptedChat.key_hash.length > 16) {
                int a;
                String hex = Utilities.bytesToHex(encryptedChat.key_hash);
                for (a = 0; a < 32; a++) {
                    if (a != 0) {
                        if (a % 8 == 0) {
                            hash.append('\n');
                        } else if (a % 4 == 0) {
                            hash.append(' ');
                        }
                    }
                    hash.append(hex.substring(a * 2, (a * 2) + 2));
                    hash.append(' ');
                }
                hash.append("\n");
                for (a = 0; a < 5; a++) {
                    int num = (((encryptedChat.key_hash[(a * 4) + 16] << 24) | (encryptedChat.key_hash[((a * 4) + 16) + 1] << 16)) | (encryptedChat.key_hash[((a * 4) + 16) + 2] << 8)) | encryptedChat.key_hash[((a * 4) + 16) + 3];
                    if (a != 0) {
                        emojis.append(" ");
                    }
                    emojis.append(EmojiData.emojiSecret[(ConnectionsManager.DEFAULT_DATACENTER_ID & num) % EmojiData.emojiSecret.length]);
                }
                this.emojiTextView.setText(Emoji.replaceEmoji(emojis, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0f), false));
                this.emojiButton.setVisibility(0);
            } else {
                this.emojiButton.setVisibility(8);
            }
            this.codeTextView.setText(hash.toString());
            hash.clear();
            hash.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", R.string.EncryptionKeyDescription, user.first_name, user.first_name)));
            String url = "telegram.org";
            int index = hash.toString().indexOf("telegram.org");
            if (index != -1) {
                hash.setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", R.string.EncryptionKeyLink)), index, "telegram.org".length() + index, 33);
            }
            this.textView.setText(hash);
        }
        updateEmojiButton(false);
        return this.fragmentView;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded && this.emojiTextView != null) {
            this.emojiTextView.invalidate();
        }
    }

    private void updateEmojiButton(boolean animated) {
        float f = 0.0f;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
        float f2;
        if (animated) {
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[6];
            TextView textView = this.emojiTextView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(textView, str, fArr);
            textView = this.codeTextView;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(textView, str, fArr);
            TextView textView2 = this.emojiTextView;
            String str2 = "scaleX";
            float[] fArr2 = new float[1];
            if (this.emojiSelected) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr2[0] = f2;
            animatorArr[2] = ObjectAnimator.ofFloat(textView2, str2, fArr2);
            textView2 = this.emojiTextView;
            str2 = "scaleY";
            fArr2 = new float[1];
            if (this.emojiSelected) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr2[0] = f2;
            animatorArr[3] = ObjectAnimator.ofFloat(textView2, str2, fArr2);
            textView2 = this.codeTextView;
            str2 = "scaleX";
            fArr2 = new float[1];
            if (this.emojiSelected) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            fArr2[0] = f2;
            animatorArr[4] = ObjectAnimator.ofFloat(textView2, str2, fArr2);
            textView = this.codeTextView;
            str = "scaleY";
            fArr = new float[1];
            if (!this.emojiSelected) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[5] = ObjectAnimator.ofFloat(textView, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(IdenticonActivity.this.animatorSet)) {
                        IdenticonActivity.this.animatorSet = null;
                        if (!IdenticonActivity.this.emojiSelected) {
                        }
                    }
                }
            });
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(150);
            this.animatorSet.start();
        } else {
            this.emojiTextView.setAlpha(this.emojiSelected ? 1.0f : 0.0f);
            TextView textView3 = this.codeTextView;
            if (this.emojiSelected) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            textView3.setAlpha(f2);
            textView3 = this.emojiTextView;
            if (this.emojiSelected) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            textView3.setScaleX(f2);
            textView3 = this.emojiTextView;
            if (this.emojiSelected) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            textView3.setScaleY(f2);
            textView3 = this.codeTextView;
            if (this.emojiSelected) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            textView3.setScaleX(f2);
            TextView textView4 = this.codeTextView;
            if (!this.emojiSelected) {
                f = 1.0f;
            }
            textView4.setScaleY(f);
        }
        this.emojiButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(!this.emojiSelected ? Theme.key_chat_emojiPanelIcon : Theme.key_chat_emojiPanelIconSelected), Mode.MULTIPLY));
    }

    private void fixLayout() {
        this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                if (IdenticonActivity.this.fragmentView != null) {
                    IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    LinearLayout layout = (LinearLayout) IdenticonActivity.this.fragmentView;
                    int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    if (rotation == 3 || rotation == 1) {
                        layout.setOrientation(0);
                    } else {
                        layout.setOrientation(1);
                    }
                    IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
                }
                return true;
            }
        });
    }

    public ThemeDescription[] getThemeDescriptions() {
        r8 = new ThemeDescription[8];
        r8[0] = new ThemeDescription(this.container, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r8[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r8[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r8[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r8[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r8[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r8[6] = new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r8[7] = new ThemeDescription(this.textView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        return r8;
    }
}
