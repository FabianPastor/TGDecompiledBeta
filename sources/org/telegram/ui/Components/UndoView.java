package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;

public class UndoView extends FrameLayout {
    private float additionalTranslationY;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private Object currentInfoObject;
    private boolean fromTop;
    private CharSequence infoText;
    private TextView infoTextView;
    private boolean isShown;
    private long lastUpdateTime;
    private RLottieImageView leftImageView;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    private long timeLeft;
    private String timeLeftString;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canUndo() {
        return true;
    }

    public void didPressUrl(CharacterStyle characterStyle) {
    }

    public class LinkMovementMethodMy extends LinkMovementMethod {
        public LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                if (motionEvent.getAction() != 1) {
                    return super.onTouchEvent(textView, spannable, motionEvent);
                }
                CharacterStyle[] characterStyleArr = (CharacterStyle[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), CharacterStyle.class);
                if (characterStyleArr != null && characterStyleArr.length > 0) {
                    UndoView.this.didPressUrl(characterStyleArr[0]);
                }
                Selection.removeSelection(spannable);
                return true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public UndoView(Context context) {
        this(context, false);
    }

    public UndoView(Context context, boolean z) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.fromTop = z;
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        this.infoTextView.setLinkTextColor(Theme.getColor("undo_cancelColor"));
        this.infoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        this.subinfoTextView.setLinkTextColor(Theme.getColor("undo_cancelColor"));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.leftImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.leftImageView.setLayerColor("info1.**", Theme.getColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("info2.**", Theme.getColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc9.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc8.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc7.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc6.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc5.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc4.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc3.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc2.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc1.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("Oval.**", Theme.getColor("undo_infoColor"));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(30, 30.0f, 19, 15.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                UndoView.this.lambda$new$0$UndoView(view);
            }
        });
        ImageView imageView = new ImageView(context);
        this.undoImageView = imageView;
        imageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        TextView textView3 = new TextView(context);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(Theme.getColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor("undo_infoColor"));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setColor(Theme.getColor("undo_infoColor"));
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("undo_background")));
        setOnTouchListener($$Lambda$UndoView$5pDYUsngdsAjUAfTf6WlgMO5mBI.INSTANCE);
        setVisibility(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$UndoView(View view) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    public void setColors(int i, int i2) {
        Theme.setDrawableColor(getBackground(), i);
        this.infoTextView.setTextColor(i2);
        this.subinfoTextView.setTextColor(i2);
        int i3 = i | -16777216;
        this.leftImageView.setLayerColor("info1.**", i3);
        this.leftImageView.setLayerColor("info2.**", i3);
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty());
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24;
    }

    public void setAdditionalTranslationY(float f) {
        this.additionalTranslationY = f;
    }

    public Object getCurrentInfoObject() {
        return this.currentInfoObject;
    }

    public void hide(boolean z, int i) {
        if (getVisibility() == 0 && this.isShown) {
            this.currentInfoObject = null;
            this.isShown = false;
            Runnable runnable = this.currentActionRunnable;
            if (runnable != null) {
                if (z) {
                    runnable.run();
                }
                this.currentActionRunnable = null;
            }
            Runnable runnable2 = this.currentCancelRunnable;
            if (runnable2 != null) {
                if (!z) {
                    runnable2.run();
                }
                this.currentCancelRunnable = null;
            }
            int i2 = this.currentAction;
            if (i2 == 0 || i2 == 1) {
                MessagesController.getInstance(this.currentAccount).removeDialogAction(this.currentDialogId, this.currentAction == 0, z);
            }
            float f = -1.0f;
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    Animator[] animatorArr = new Animator[1];
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (!this.fromTop) {
                        f = 1.0f;
                    }
                    fArr[0] = f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(250);
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        UndoView.this.setVisibility(4);
                        UndoView.this.setScaleX(1.0f);
                        UndoView.this.setScaleY(1.0f);
                        UndoView.this.setAlpha(1.0f);
                    }
                });
                animatorSet.start();
                return;
            }
            if (!this.fromTop) {
                f = 1.0f;
            }
            setTranslationY(f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)));
            setVisibility(4);
        }
    }

    public void showWithAction(long j, int i, Runnable runnable) {
        showWithAction(j, i, (Object) null, (Object) null, runnable, (Runnable) null);
    }

    public void showWithAction(long j, int i, Object obj) {
        showWithAction(j, i, obj, (Object) null, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long j, int i, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, (Object) null, (Object) null, runnable, runnable2);
    }

    public void showWithAction(long j, int i, Object obj, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, obj, (Object) null, runnable, runnable2);
    }

    public void showWithAction(long j, int i, Object obj, Object obj2, Runnable runnable, Runnable runnable2) {
        String str;
        String str2;
        int i2;
        int i3;
        String str3;
        CharSequence charSequence;
        int i4;
        CharSequence replaceTags;
        CharSequence charSequence2;
        String str4;
        CharSequence charSequence3;
        String string;
        String string2;
        long j2 = j;
        int i5 = i;
        Object obj3 = obj;
        Runnable runnable3 = this.currentActionRunnable;
        if (runnable3 != null) {
            runnable3.run();
        }
        this.isShown = true;
        this.currentActionRunnable = runnable;
        this.currentCancelRunnable = runnable2;
        this.currentDialogId = j2;
        this.currentAction = i5;
        this.timeLeft = 5000;
        this.currentInfoObject = obj3;
        this.lastUpdateTime = SystemClock.elapsedRealtime();
        this.undoTextView.setText(LocaleController.getString("Undo", NUM).toUpperCase());
        this.undoImageView.setVisibility(0);
        this.infoTextView.setTextSize(1, 15.0f);
        this.avatarImageView.setVisibility(8);
        this.infoTextView.setGravity(51);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.infoTextView.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.bottomMargin = 0;
        this.leftImageView.setScaleType(ImageView.ScaleType.CENTER);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.leftImageView.getLayoutParams();
        layoutParams2.gravity = 19;
        layoutParams2.bottomMargin = 0;
        layoutParams2.topMargin = 0;
        layoutParams2.leftMargin = AndroidUtilities.dp(3.0f);
        layoutParams2.width = AndroidUtilities.dp(54.0f);
        layoutParams2.height = -2;
        this.infoTextView.setMinHeight(0);
        float f = 14.0f;
        if (isTooltipAction()) {
            str = "";
            String str5 = null;
            if (i5 == 34) {
                TLRPC$User tLRPC$User = (TLRPC$User) obj3;
                charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupInvitedUser", NUM, UserObject.getFirstName(tLRPC$User)));
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                avatarDrawable.setInfo(tLRPC$User);
                this.avatarImageView.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                this.avatarImageView.setVisibility(0);
                this.timeLeft = 3000;
                str3 = null;
                i4 = 36;
                i3 = 0;
            } else if (i5 == 33) {
                charSequence = LocaleController.getString("VoipGroupCopyInviteLinkCopied", NUM);
                this.timeLeft = 3000;
                str3 = null;
                i4 = 36;
                i3 = NUM;
            } else if (i5 == 30) {
                charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupUserCantNowSpeak", NUM, UserObject.getFirstName((TLRPC$User) obj3)));
                this.timeLeft = 3000;
                str3 = null;
                i4 = 36;
                i3 = NUM;
            } else if (i5 == 31) {
                charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupUserCanNowSpeak", NUM, UserObject.getFirstName((TLRPC$User) obj3)));
                this.timeLeft = 3000;
                str3 = null;
                i4 = 36;
                i3 = NUM;
            } else if (i5 == 32) {
                charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupRemovedFromGroup", NUM, UserObject.getFirstName((TLRPC$User) obj3)));
                this.timeLeft = 3000;
                str3 = null;
                i4 = 36;
                i3 = NUM;
            } else {
                if (i5 == 9 || i5 == 10) {
                    TLRPC$User tLRPC$User2 = (TLRPC$User) obj3;
                    if (i5 == 9) {
                        replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferChannelToast", NUM, UserObject.getFirstName(tLRPC$User2)));
                    } else {
                        replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferGroupToast", NUM, UserObject.getFirstName(tLRPC$User2)));
                    }
                } else {
                    if (i5 == 8) {
                        charSequence2 = LocaleController.formatString("NowInContacts", NUM, UserObject.getFirstName((TLRPC$User) obj3));
                    } else if (i5 == 22) {
                        if (j2 <= 0) {
                            TLRPC$Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf((int) (-j2)));
                            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                if (obj3 == null) {
                                    replaceTags = LocaleController.getString("MainGroupProfilePhotoSetHint", NUM);
                                } else {
                                    replaceTags = LocaleController.getString("MainGroupProfileVideoSetHint", NUM);
                                }
                            } else if (obj3 == null) {
                                replaceTags = LocaleController.getString("MainChannelProfilePhotoSetHint", NUM);
                            } else {
                                replaceTags = LocaleController.getString("MainChannelProfileVideoSetHint", NUM);
                            }
                        } else if (obj3 == null) {
                            replaceTags = LocaleController.getString("MainProfilePhotoSetHint", NUM);
                        } else {
                            replaceTags = LocaleController.getString("MainProfileVideoSetHint", NUM);
                        }
                    } else if (i5 == 23) {
                        charSequence2 = LocaleController.getString("ChatWasMovedToMainList", NUM);
                    } else {
                        if (i5 == 6) {
                            charSequence = LocaleController.getString("ArchiveHidden", NUM);
                            string2 = LocaleController.getString("ArchiveHiddenInfo", NUM);
                            i3 = NUM;
                            i4 = 48;
                        } else {
                            int i6 = this.currentAction;
                            if (i6 == 13) {
                                charSequence = LocaleController.getString("QuizWellDone", NUM);
                                string2 = LocaleController.getString("QuizWellDoneInfo", NUM);
                                i3 = NUM;
                            } else if (i6 == 14) {
                                charSequence = LocaleController.getString("QuizWrongAnswer", NUM);
                                string2 = LocaleController.getString("QuizWrongAnswerInfo", NUM);
                                i3 = NUM;
                            } else {
                                if (i5 == 7) {
                                    charSequence3 = LocaleController.getString("ArchivePinned", NUM);
                                    if (MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty()) {
                                        string = LocaleController.getString("ArchivePinnedInfo", NUM);
                                    }
                                    str3 = str5;
                                    i4 = 36;
                                    i3 = NUM;
                                } else if (i5 == 20 || i5 == 21) {
                                    MessagesController.DialogFilter dialogFilter = (MessagesController.DialogFilter) obj2;
                                    if (j2 != 0) {
                                        int i7 = (int) j2;
                                        if (i7 == 0) {
                                            i7 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (j2 >> 32))).user_id;
                                        }
                                        if (i7 > 0) {
                                            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i7));
                                            if (i5 == 20) {
                                                replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("FilterUserAddedToExisting", NUM, UserObject.getFirstName(user), dialogFilter.name));
                                            } else {
                                                replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("FilterUserRemovedFrom", NUM, UserObject.getFirstName(user), dialogFilter.name));
                                            }
                                        } else {
                                            TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i7));
                                            if (i5 == 20) {
                                                replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("FilterChatAddedToExisting", NUM, chat2.title, dialogFilter.name));
                                            } else {
                                                replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("FilterChatRemovedFrom", NUM, chat2.title, dialogFilter.name));
                                            }
                                        }
                                    } else if (i5 == 20) {
                                        replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("FilterChatsAddedToExisting", NUM, LocaleController.formatPluralString("Chats", ((Integer) obj3).intValue()), dialogFilter.name));
                                    } else {
                                        replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("FilterChatsRemovedFrom", NUM, LocaleController.formatPluralString("Chats", ((Integer) obj3).intValue()), dialogFilter.name));
                                    }
                                } else {
                                    if (i5 == 19) {
                                        charSequence3 = this.infoText;
                                    } else {
                                        if (i5 == 3) {
                                            str4 = LocaleController.getString("ChatArchived", NUM);
                                        } else {
                                            str4 = LocaleController.getString("ChatsArchived", NUM);
                                        }
                                        charSequence3 = str4;
                                        if (MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty()) {
                                            string = LocaleController.getString("ChatArchivedInfo", NUM);
                                        }
                                    }
                                    str3 = str5;
                                    i4 = 36;
                                    i3 = NUM;
                                }
                                str5 = string;
                                str3 = str5;
                                i4 = 36;
                                i3 = NUM;
                            }
                            i4 = 44;
                        }
                        str3 = string2;
                    }
                    str3 = null;
                    i4 = 36;
                    i3 = NUM;
                }
                charSequence2 = replaceTags;
                str3 = null;
                i4 = 36;
                i3 = NUM;
            }
            this.infoTextView.setText(charSequence);
            if (i3 != 0) {
                this.leftImageView.setAnimation(i3, i4, i4);
                this.leftImageView.setVisibility(0);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else {
                this.leftImageView.setVisibility(8);
            }
            if (str3 != null) {
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(8.0f);
                ((FrameLayout.LayoutParams) this.subinfoTextView.getLayoutParams()).rightMargin = AndroidUtilities.dp(8.0f);
                this.subinfoTextView.setText(str3);
                this.subinfoTextView.setVisibility(0);
                this.infoTextView.setTextSize(1, 14.0f);
                this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else {
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.topMargin = AndroidUtilities.dp(13.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(8.0f);
                this.subinfoTextView.setVisibility(8);
                this.infoTextView.setTextSize(1, 15.0f);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
            }
            this.undoButton.setVisibility(8);
        } else {
            str = "";
            int i8 = this.currentAction;
            if (i8 == 24 || i8 == 25) {
                int intValue = ((Integer) obj3).intValue();
                TLRPC$User tLRPC$User3 = (TLRPC$User) obj2;
                this.undoImageView.setVisibility(8);
                this.leftImageView.setVisibility(0);
                if (intValue != 0) {
                    this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    this.infoTextView.setTextSize(1, 14.0f);
                    this.leftImageView.clearLayerColors();
                    this.leftImageView.setLayerColor("BODY.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Wibe Big.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Wibe Big 3.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Wibe Small.**", Theme.getColor("undo_infoColor"));
                    this.infoTextView.setText(LocaleController.getString("ProximityAlertSet", NUM));
                    this.leftImageView.setAnimation(NUM, 28, 28);
                    this.subinfoTextView.setVisibility(0);
                    this.subinfoTextView.setSingleLine(false);
                    this.subinfoTextView.setMaxLines(3);
                    if (tLRPC$User3 != null) {
                        this.subinfoTextView.setText(LocaleController.formatString("ProximityAlertSetInfoUser", NUM, UserObject.getFirstName(tLRPC$User3), LocaleController.formatDistance((float) intValue, 2)));
                    } else {
                        this.subinfoTextView.setText(LocaleController.formatString("ProximityAlertSetInfoGroup2", NUM, LocaleController.formatDistance((float) intValue, 2)));
                    }
                    this.undoButton.setVisibility(8);
                    layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                } else {
                    this.infoTextView.setTypeface(Typeface.DEFAULT);
                    this.infoTextView.setTextSize(1, 15.0f);
                    this.leftImageView.clearLayerColors();
                    this.leftImageView.setLayerColor("Body Main.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Body Top.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Line.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Curve Big.**", Theme.getColor("undo_infoColor"));
                    this.leftImageView.setLayerColor("Curve Small.**", Theme.getColor("undo_infoColor"));
                    layoutParams.topMargin = AndroidUtilities.dp(14.0f);
                    this.infoTextView.setText(LocaleController.getString("ProximityAlertCancelled", NUM));
                    this.leftImageView.setAnimation(NUM, 28, 28);
                    this.subinfoTextView.setVisibility(8);
                    this.undoTextView.setTextColor(Theme.getColor("undo_cancelColor"));
                    this.undoButton.setVisibility(0);
                }
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else if (i8 == 11) {
                this.infoTextView.setText(LocaleController.getString("AuthAnotherClientOk", NUM));
                this.leftImageView.setAnimation(NUM, 36, 36);
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                this.subinfoTextView.setText(((TLRPC$TL_authorization) obj3).app_name);
                this.subinfoTextView.setVisibility(0);
                this.infoTextView.setTextSize(1, 14.0f);
                this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.undoTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
                this.undoImageView.setVisibility(8);
                this.undoButton.setVisibility(0);
                this.leftImageView.setVisibility(0);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else if (i8 == 15) {
                this.timeLeft = 10000;
                this.undoTextView.setText(LocaleController.getString("Open", NUM).toUpperCase());
                this.infoTextView.setText(LocaleController.getString("FilterAvailableTitle", NUM));
                this.leftImageView.setAnimation(NUM, 36, 36);
                int ceil = ((int) Math.ceil((double) this.undoTextView.getPaint().measureText(this.undoTextView.getText().toString()))) + AndroidUtilities.dp(26.0f);
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.rightMargin = ceil;
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                ((FrameLayout.LayoutParams) this.subinfoTextView.getLayoutParams()).rightMargin = ceil;
                String string3 = LocaleController.getString("FilterAvailableText", NUM);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string3);
                int indexOf = string3.indexOf(42);
                int lastIndexOf = string3.lastIndexOf(42);
                if (indexOf >= 0 && lastIndexOf >= 0 && indexOf != lastIndexOf) {
                    spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, str);
                    spannableStringBuilder.replace(indexOf, indexOf + 1, str);
                    spannableStringBuilder.setSpan(new URLSpanNoUnderline("tg://settings/folders"), indexOf, lastIndexOf - 1, 33);
                }
                this.subinfoTextView.setText(spannableStringBuilder);
                this.subinfoTextView.setVisibility(0);
                this.subinfoTextView.setSingleLine(false);
                this.subinfoTextView.setMaxLines(2);
                this.undoButton.setVisibility(0);
                this.undoImageView.setVisibility(8);
                this.leftImageView.setVisibility(0);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else if (i8 == 16 || i8 == 17) {
                this.timeLeft = 4000;
                this.infoTextView.setTextSize(1, 14.0f);
                this.infoTextView.setGravity(16);
                this.infoTextView.setMinHeight(AndroidUtilities.dp(30.0f));
                String str6 = (String) obj3;
                if ("🎲".equals(str6)) {
                    this.infoTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DiceInfo2", NUM)));
                    this.leftImageView.setImageResource(NUM);
                } else {
                    if ("🎯".equals(str6)) {
                        this.infoTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DartInfo", NUM)));
                    } else {
                        String serverString = LocaleController.getServerString("DiceEmojiInfo_" + str6);
                        if (!TextUtils.isEmpty(serverString)) {
                            TextView textView = this.infoTextView;
                            textView.setText(Emoji.replaceEmoji(serverString, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                        } else {
                            this.infoTextView.setText(Emoji.replaceEmoji(LocaleController.formatString("DiceEmojiInfo", NUM, str6), this.infoTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                        }
                    }
                    this.leftImageView.setImageDrawable(Emoji.getEmojiDrawable(str6));
                    this.leftImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    layoutParams.topMargin = AndroidUtilities.dp(14.0f);
                    layoutParams.bottomMargin = AndroidUtilities.dp(14.0f);
                    layoutParams2.leftMargin = AndroidUtilities.dp(14.0f);
                    layoutParams2.width = AndroidUtilities.dp(26.0f);
                    layoutParams2.height = AndroidUtilities.dp(26.0f);
                }
                this.undoTextView.setText(LocaleController.getString("SendDice", NUM));
                if (this.currentAction == 16) {
                    i2 = ((int) Math.ceil((double) this.undoTextView.getPaint().measureText(this.undoTextView.getText().toString()))) + AndroidUtilities.dp(26.0f);
                    this.undoTextView.setVisibility(0);
                    this.undoTextView.setTextColor(Theme.getColor("undo_cancelColor"));
                    this.undoImageView.setVisibility(8);
                    this.undoButton.setVisibility(0);
                } else {
                    i2 = AndroidUtilities.dp(8.0f);
                    this.undoTextView.setVisibility(8);
                    this.undoButton.setVisibility(8);
                }
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.rightMargin = i2;
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(7.0f);
                layoutParams.height = -1;
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(0);
            } else if (i8 == 18) {
                CharSequence charSequence4 = (CharSequence) obj3;
                this.timeLeft = (long) Math.max(4000, Math.min((charSequence4.length() / 50) * 1600, 10000));
                this.infoTextView.setTextSize(1, 14.0f);
                this.infoTextView.setGravity(16);
                this.infoTextView.setText(charSequence4);
                this.undoTextView.setVisibility(8);
                this.undoButton.setVisibility(8);
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(8.0f);
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(7.0f);
                layoutParams.height = -1;
                layoutParams2.gravity = 51;
                int dp = AndroidUtilities.dp(8.0f);
                layoutParams2.bottomMargin = dp;
                layoutParams2.topMargin = dp;
                this.leftImageView.setVisibility(0);
                this.leftImageView.setAnimation(NUM, 36, 36);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else if (i8 == 12) {
                this.infoTextView.setText(LocaleController.getString("ColorThemeChanged", NUM));
                this.leftImageView.setImageResource(NUM);
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(48.0f);
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                ((FrameLayout.LayoutParams) this.subinfoTextView.getLayoutParams()).rightMargin = AndroidUtilities.dp(48.0f);
                String string4 = LocaleController.getString("ColorThemeChangedInfo", NUM);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(string4);
                int indexOf2 = string4.indexOf(42);
                int lastIndexOf2 = string4.lastIndexOf(42);
                if (indexOf2 >= 0 && lastIndexOf2 >= 0 && indexOf2 != lastIndexOf2) {
                    spannableStringBuilder2.replace(lastIndexOf2, lastIndexOf2 + 1, str);
                    spannableStringBuilder2.replace(indexOf2, indexOf2 + 1, str);
                    spannableStringBuilder2.setSpan(new URLSpanNoUnderline("tg://settings/themes"), indexOf2, lastIndexOf2 - 1, 33);
                }
                this.subinfoTextView.setText(spannableStringBuilder2);
                this.subinfoTextView.setVisibility(0);
                this.subinfoTextView.setSingleLine(false);
                this.subinfoTextView.setMaxLines(2);
                this.undoTextView.setVisibility(8);
                this.undoButton.setVisibility(0);
                this.leftImageView.setVisibility(0);
            } else if (i8 == 2 || i8 == 4) {
                if (i5 == 2) {
                    this.infoTextView.setText(LocaleController.getString("ChatArchived", NUM));
                } else {
                    this.infoTextView.setText(LocaleController.getString("ChatsArchived", NUM));
                }
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.topMargin = AndroidUtilities.dp(13.0f);
                layoutParams.rightMargin = 0;
                this.infoTextView.setTextSize(1, 15.0f);
                this.undoButton.setVisibility(0);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(0);
                this.leftImageView.setAnimation(NUM, 36, 36);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else {
                layoutParams.leftMargin = AndroidUtilities.dp(45.0f);
                layoutParams.topMargin = AndroidUtilities.dp(13.0f);
                layoutParams.rightMargin = 0;
                this.infoTextView.setTextSize(1, 15.0f);
                this.undoButton.setVisibility(0);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(8);
                if (this.currentAction == 0) {
                    this.infoTextView.setText(LocaleController.getString("HistoryClearedUndo", NUM));
                } else {
                    int i9 = (int) j2;
                    if (i9 < 0) {
                        TLRPC$Chat chat3 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i9));
                        if (!ChatObject.isChannel(chat3) || chat3.megagroup) {
                            this.infoTextView.setText(LocaleController.getString("GroupDeletedUndo", NUM));
                        } else {
                            this.infoTextView.setText(LocaleController.getString("ChannelDeletedUndo", NUM));
                        }
                    } else {
                        this.infoTextView.setText(LocaleController.getString("ChatDeletedUndo", NUM));
                    }
                }
                MessagesController.getInstance(this.currentAccount).addDialogAction(j2, this.currentAction == 0);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.infoTextView.getText());
        if (this.subinfoTextView.getVisibility() == 0) {
            str2 = ". " + this.subinfoTextView.getText();
        } else {
            str2 = str;
        }
        sb.append(str2);
        AndroidUtilities.makeAccessibilityAnnouncement(sb.toString());
        if (isMultilineSubInfo()) {
            int measuredWidth = ((ViewGroup) getParent()).getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            measureChildWithMargins(this.subinfoTextView, View.MeasureSpec.makeMeasureSpec(measuredWidth - AndroidUtilities.dp(16.0f), NUM), 0, View.MeasureSpec.makeMeasureSpec(0, 0), 0);
            this.undoViewHeight = this.subinfoTextView.getMeasuredHeight() + AndroidUtilities.dp(37.0f);
        } else if (hasSubInfo()) {
            this.undoViewHeight = AndroidUtilities.dp(52.0f);
        } else if (getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            int measuredWidth2 = (viewGroup.getMeasuredWidth() - viewGroup.getPaddingLeft()) - viewGroup.getPaddingRight();
            if (measuredWidth2 <= 0) {
                measuredWidth2 = AndroidUtilities.displaySize.x;
            }
            measureChildWithMargins(this.infoTextView, View.MeasureSpec.makeMeasureSpec(measuredWidth2 - AndroidUtilities.dp(16.0f), NUM), 0, View.MeasureSpec.makeMeasureSpec(0, 0), 0);
            int measuredHeight = this.infoTextView.getMeasuredHeight();
            int i10 = this.currentAction;
            if (!(i10 == 16 || i10 == 17 || i10 == 18)) {
                f = 28.0f;
            }
            int dp2 = measuredHeight + AndroidUtilities.dp(f);
            this.undoViewHeight = dp2;
            int i11 = this.currentAction;
            if (i11 == 18) {
                this.undoViewHeight = Math.max(dp2, AndroidUtilities.dp(52.0f));
            } else if (i11 == 25) {
                this.undoViewHeight = Math.max(dp2, AndroidUtilities.dp(50.0f));
            }
        }
        if (getVisibility() != 0) {
            setVisibility(0);
            setTranslationY((this.fromTop ? -1.0f : 1.0f) * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[2];
            fArr[0] = (this.fromTop ? -1.0f : 1.0f) * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
            fArr[1] = (this.fromTop ? 1.0f : -1.0f) * this.additionalTranslationY;
            animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = this.currentAction;
        if (i == 1 || i == 0) {
            long j = this.timeLeft;
            int ceil = j > 0 ? (int) Math.ceil((double) (((float) j) / 1000.0f)) : 0;
            if (this.prevSeconds != ceil) {
                this.prevSeconds = ceil;
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ceil))});
                this.timeLeftString = format;
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(format));
            }
            canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = this.timeLeft - (elapsedRealtime - this.lastUpdateTime);
        this.timeLeft = j2;
        this.lastUpdateTime = elapsedRealtime;
        if (j2 <= 0) {
            hide(true, 1);
        }
        invalidate();
    }

    public void invalidate() {
        super.invalidate();
        this.infoTextView.invalidate();
        this.leftImageView.invalidate();
    }

    public void setInfoText(CharSequence charSequence) {
        this.infoText = charSequence;
    }
}
