package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.EditTextEmoji;
import org.telegram.p005ui.Components.ImageUpdater;
import org.telegram.p005ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RadialProgressView;
import org.telegram.p005ui.Components.SizeNotifierFrameLayout;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;

/* renamed from: org.telegram.ui.ChangeChatNameActivity */
public class ChangeChatNameActivity extends BaseFragment implements ImageUpdaterDelegate {
    private static final int done_button = 1;
    private FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    private ImageView avatarEditor;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private int chatId;
    private boolean createAfterUpload;
    private Chat currentChat;
    private View doneButton;
    private boolean donePressed;
    private EditTextEmoji editText;
    private View headerLabelView;
    private ImageUpdater imageUpdater;
    private AlertDialog progressDialog;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeChatNameActivity.this.finishFragment();
            } else if (id == 1 && !ChangeChatNameActivity.this.donePressed) {
                if (ChangeChatNameActivity.this.editText.length() == 0) {
                    Vibrator v = (Vibrator) ChangeChatNameActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChangeChatNameActivity.this.editText, 2.0f, 0);
                    return;
                }
                ChangeChatNameActivity.this.donePressed = true;
                if (ChangeChatNameActivity.this.imageUpdater.uploadingImage != null) {
                    ChangeChatNameActivity.this.createAfterUpload = true;
                    ChangeChatNameActivity.this.progressDialog = new AlertDialog(ChangeChatNameActivity.this.getParentActivity(), 3);
                    ChangeChatNameActivity.this.progressDialog.setOnCancelListener(new ChangeChatNameActivity$1$$Lambda$0(this));
                    ChangeChatNameActivity.this.progressDialog.show();
                    return;
                }
                if (ChangeChatNameActivity.this.uploadedAvatar != null) {
                    MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, ChangeChatNameActivity.this.uploadedAvatar, ChangeChatNameActivity.this.avatar, ChangeChatNameActivity.this.avatarBig);
                } else if (ChangeChatNameActivity.this.avatar == null && (ChangeChatNameActivity.this.currentChat.photo instanceof TL_chatPhoto)) {
                    MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, null, null, null);
                }
                ChangeChatNameActivity.this.finishFragment();
                if (ChangeChatNameActivity.this.editText.length() != 0) {
                    ChangeChatNameActivity.this.saveName();
                    ChangeChatNameActivity.this.finishFragment();
                }
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$ChangeChatNameActivity$1(DialogInterface dialog) {
            ChangeChatNameActivity.this.createAfterUpload = false;
            ChangeChatNameActivity.this.progressDialog = null;
            ChangeChatNameActivity.this.donePressed = false;
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$6 */
    class CLASSNAME implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$6$1 */
        class CLASSNAME implements DialogInterface.OnClickListener {
            CLASSNAME() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) ChangeChatNameActivity.this.chatId)));
                } else {
                    NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).deleteUserFromChat(ChangeChatNameActivity.this.chatId, MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChangeChatNameActivity.this.currentAccount).getClientUserId())), null, true);
                ChangeChatNameActivity.this.finishFragment();
            }
        }

        CLASSNAME() {
        }

        public void onClick(View v) {
            Builder builder = new Builder(ChangeChatNameActivity.this.getParentActivity());
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", CLASSNAMER.string.AreYouSureDeleteAndExit));
            builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new CLASSNAME());
            builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
            ChangeChatNameActivity.this.showDialog(builder.create());
        }
    }

    public ChangeChatNameActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.avatarDrawable = new AvatarDrawable();
        this.chatId = getArguments().getInt("chat_id", 0);
        this.imageUpdater = new ImageUpdater();
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.delegate = this;
        return true;
    }

    public View createView(Context context) {
        float f;
        float f2;
        if (this.editText != null) {
            this.editText.onDestroy();
        }
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", CLASSNAMER.string.ChannelEdit));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, CLASSNAMER.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        View CLASSNAME = new SizeNotifierFrameLayout(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(ChangeChatNameActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChangeChatNameActivity.this.actionBar)) {
                        if (ChangeChatNameActivity.this.editText == null || !ChangeChatNameActivity.this.editText.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.m9dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.m9dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChangeChatNameActivity.this.editText.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch ((gravity & 7) & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (ChangeChatNameActivity.this.editText != null && ChangeChatNameActivity.this.editText.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + getKeyboardHeight()) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }
        };
        this.fragmentView = CLASSNAME;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.fragmentView.setOnTouchListener(ChangeChatNameActivity$$Lambda$0.$instance);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        CLASSNAME.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context) {
            public void invalidate() {
                if (ChangeChatNameActivity.this.avatarOverlay != null) {
                    ChangeChatNameActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int l, int t, int r, int b) {
                if (ChangeChatNameActivity.this.avatarOverlay != null) {
                    ChangeChatNameActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(l, t, r, b);
            }
        };
        this.avatarImage.setRoundRadius(AndroidUtilities.m9dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
        View view = this.avatarImage;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 16.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 16.0f;
        } else {
            f2 = 0.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
        Paint paint = new Paint(1);
        paint.setColor(NUM);
        final Paint paint2 = paint;
        this.avatarOverlay = new View(context) {
            protected void onDraw(Canvas canvas) {
                if (ChangeChatNameActivity.this.avatarImage != null && ChangeChatNameActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint2.setAlpha((int) (85.0f * ChangeChatNameActivity.this.avatarImage.getImageReceiver().getCurrentAlpha()));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.m9dp(32.0f), paint2);
                }
            }
        };
        view = this.avatarOverlay;
        i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 16.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 16.0f;
        } else {
            f2 = 0.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
        this.avatarOverlay.setOnClickListener(new ChangeChatNameActivity$$Lambda$1(this));
        this.avatarEditor = new ImageView(context) {
            public void invalidate(int l, int t, int r, int b) {
                super.invalidate(l, t, r, b);
                ChangeChatNameActivity.this.avatarOverlay.invalidate();
            }

            public void invalidate() {
                super.invalidate();
                ChangeChatNameActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarEditor.setScaleType(ScaleType.CENTER);
        this.avatarEditor.setImageResource(CLASSNAMER.drawable.menu_camera_av);
        this.avatarEditor.setEnabled(false);
        this.avatarEditor.setClickable(false);
        frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        this.avatarProgressView = new RadialProgressView(context);
        this.avatarProgressView.setSize(AndroidUtilities.m9dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        showAvatarProgress(false, false);
        this.editText = new EditTextEmoji((Activity) context, CLASSNAME, this);
        this.editText.setHint(LocaleController.getString("GroupName", CLASSNAMER.string.GroupName));
        this.editText.setFilters(new InputFilter[]{new LengthFilter(100)});
        view = this.editText;
        f = LocaleController.isRTL ? 5.0f : 96.0f;
        if (LocaleController.isRTL) {
            f2 = 96.0f;
        } else {
            f2 = 5.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 16, f, 0.0f, f2, 0.0f));
        CLASSNAME = new ShadowSectionCell(context, 20);
        linearLayout.addView(CLASSNAME, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.creator) {
            FrameLayout container3 = new FrameLayout(context);
            container3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout.addView(container3, LayoutHelper.createLinear(-1, -2));
            CLASSNAME = new TextSettingsCell(context);
            CLASSNAME.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            CLASSNAME.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            CLASSNAME.setText(LocaleController.getString("DeleteMega", CLASSNAMER.string.DeleteMega), false);
            container3.addView(CLASSNAME, LayoutHelper.createFrame(-1, -2.0f));
            CLASSNAME.setOnClickListener(new CLASSNAME());
            TextInfoPrivacyCell infoCell2 = new TextInfoPrivacyCell(context);
            infoCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            infoCell2.setText(LocaleController.getString("MegaDeleteInfo", CLASSNAMER.string.MegaDeleteInfo));
            linearLayout.addView(infoCell2, LayoutHelper.createLinear(-1, -2));
        } else {
            CLASSNAME.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
        this.editText.setText(this.currentChat.title);
        this.editText.setSelection(this.editText.length());
        if (this.currentChat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$2$ChangeChatNameActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new ChangeChatNameActivity$$Lambda$4(this));
    }

    final /* synthetic */ void lambda$null$1$ChangeChatNameActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.editText != null) {
            this.editText.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.onResume();
        }
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.editText.openKeyboard();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.editText != null) {
            this.editText.onPause();
        }
    }

    public boolean onBackPressed() {
        if (this.editText == null || !this.editText.isPopupShowing()) {
            return true;
        }
        this.editText.hidePopup(true);
        return false;
    }

    public void didUploadPhoto(InputFile file, PhotoSize bigSize, PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new ChangeChatNameActivity$$Lambda$2(this, file, smallSize, bigSize));
    }

    final /* synthetic */ void lambda$didUploadPhoto$3$ChangeChatNameActivity(InputFile file, PhotoSize smallSize, PhotoSize bigSize) {
        if (file != null) {
            this.uploadedAvatar = file;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
        showAvatarProgress(true, false);
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor != null) {
            if (this.avatarAnimation != null) {
                this.avatarAnimation.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.avatarEditor.setVisibility(0);
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChangeChatNameActivity.this.avatarAnimation != null && ChangeChatNameActivity.this.avatarEditor != null) {
                            if (show) {
                                ChangeChatNameActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChangeChatNameActivity.this.avatarProgressView.setVisibility(4);
                            }
                            ChangeChatNameActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        ChangeChatNameActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (!(this.imageUpdater == null || this.imageUpdater.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        if (this.editText != null) {
            String text = this.editText.getText().toString();
            if (text != null && text.length() != 0) {
                args.putString("editText", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.imageUpdater != null) {
            this.imageUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new ChangeChatNameActivity$$Lambda$3(this), 100);
        }
    }

    final /* synthetic */ void lambda$onTransitionAnimationEnd$4$ChangeChatNameActivity() {
        if (this.editText != null) {
            this.editText.openKeyboard();
        }
    }

    private void saveName() {
        MessagesController.getInstance(this.currentAccount).changeChatTitle(this.chatId, this.editText.getText().toString());
    }
}
