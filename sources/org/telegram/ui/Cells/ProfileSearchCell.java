package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private Chat chat;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop = AndroidUtilities.dp(25.0f);
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private long dialog_id;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private EncryptedChat encryptedChat;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameTop;
    private int nameWidth;
    private StaticLayout onlineLayout;
    private int onlineLeft;
    private int paddingRight;
    private RectF rect = new RectF();
    private boolean savedMessages;
    private CharSequence subLabel;
    public boolean useSeparator;
    private User user;

    public ProfileSearchCell(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setData(TLObject tLObject, EncryptedChat encryptedChat, CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        this.currentName = charSequence;
        if ((tLObject instanceof User) != null) {
            this.user = (User) tLObject;
            this.chat = null;
        } else if ((tLObject instanceof Chat) != null) {
            this.chat = (Chat) tLObject;
            this.user = null;
        }
        this.encryptedChat = encryptedChat;
        this.subLabel = charSequence2;
        this.drawCount = z;
        this.savedMessages = z2;
        update(null);
    }

    public void setPaddingRight(int i) {
        this.paddingRight = i;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!(this.user == 0 && this.chat == 0 && this.encryptedChat == 0) && z) {
            buildLayout();
        }
    }

    public void buildLayout() {
        CharSequence charSequence;
        TextPaint textPaint;
        int measuredWidth;
        int measuredWidth2;
        double ceil;
        double ceil2;
        double d;
        TextPaint textPaint2;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameGroup = false;
        this.drawCheck = false;
        this.drawNameBot = false;
        if (this.encryptedChat != null) {
            r0.drawNameLock = true;
            r0.dialog_id = ((long) r0.encryptedChat.id) << 32;
            if (LocaleController.isRTL) {
                r0.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                r0.nameLeft = AndroidUtilities.dp(11.0f);
            } else {
                r0.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                r0.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            }
            r0.nameLockTop = AndroidUtilities.dp(16.5f);
        } else if (r0.chat != null) {
            if (r0.chat.id < 0) {
                r0.dialog_id = AndroidUtilities.makeBroadcastId(r0.chat.id);
                r0.drawNameBroadcast = true;
                r0.nameLockTop = AndroidUtilities.dp(28.5f);
            } else {
                r0.dialog_id = (long) (-r0.chat.id);
                if (!ChatObject.isChannel(r0.chat) || r0.chat.megagroup) {
                    r0.drawNameGroup = true;
                    r0.nameLockTop = AndroidUtilities.dp(30.0f);
                } else {
                    r0.drawNameBroadcast = true;
                    r0.nameLockTop = AndroidUtilities.dp(28.5f);
                }
            }
            r0.drawCheck = r0.chat.verified;
            if (LocaleController.isRTL) {
                r0.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - (r0.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                r0.nameLeft = AndroidUtilities.dp(11.0f);
            } else {
                r0.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                r0.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r0.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
            }
        } else if (r0.user != null) {
            r0.dialog_id = (long) r0.user.id;
            if (LocaleController.isRTL) {
                r0.nameLeft = AndroidUtilities.dp(11.0f);
            } else {
                r0.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            if (r0.user.bot) {
                r0.drawNameBot = true;
                if (LocaleController.isRTL) {
                    r0.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - Theme.dialogs_botDrawable.getIntrinsicWidth();
                    r0.nameLeft = AndroidUtilities.dp(11.0f);
                } else {
                    r0.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    r0.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_botDrawable.getIntrinsicWidth();
                }
                r0.nameLockTop = AndroidUtilities.dp(16.5f);
            } else {
                r0.nameLockTop = AndroidUtilities.dp(17.0f);
            }
            r0.drawCheck = r0.user.verified;
        }
        if (r0.currentName != null) {
            charSequence = r0.currentName;
        } else {
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            if (r0.chat != null) {
                str = r0.chat.title;
            } else if (r0.user != null) {
                str = UserObject.getUserName(r0.user);
            }
            charSequence = str.replace('\n', ' ');
        }
        if (charSequence.length() == 0) {
            if (r0.user == null || r0.user.phone == null || r0.user.phone.length() == 0) {
                charSequence = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
            } else {
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(r0.user.phone);
                charSequence = instance.format(stringBuilder.toString());
            }
        }
        if (r0.encryptedChat != null) {
            textPaint = Theme.dialogs_nameEncryptedPaint;
        } else {
            textPaint = Theme.dialogs_namePaint;
        }
        TextPaint textPaint3 = textPaint;
        if (LocaleController.isRTL) {
            measuredWidth = (getMeasuredWidth() - r0.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            r0.nameWidth = measuredWidth;
        } else {
            measuredWidth = (getMeasuredWidth() - r0.nameLeft) - AndroidUtilities.dp(14.0f);
            r0.nameWidth = measuredWidth;
        }
        if (r0.drawNameLock) {
            r0.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (r0.drawNameBroadcast) {
            r0.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (r0.drawNameGroup) {
            r0.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (r0.drawNameBot) {
            r0.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        r0.nameWidth -= r0.paddingRight;
        measuredWidth -= r0.paddingRight;
        if (r0.drawCount) {
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(r0.currentAccount).dialogs_dict.get(r0.dialog_id);
            if (tL_dialog == null || tL_dialog.unread_count == 0) {
                r0.lastUnreadCount = 0;
                r0.countLayout = null;
            } else {
                r0.lastUnreadCount = tL_dialog.unread_count;
                String format = String.format("%d", new Object[]{Integer.valueOf(tL_dialog.unread_count)});
                r0.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                r0.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, r0.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int dp = r0.countWidth + AndroidUtilities.dp(18.0f);
                r0.nameWidth -= dp;
                if (LocaleController.isRTL) {
                    r0.countLeft = AndroidUtilities.dp(19.0f);
                    r0.nameLeft += dp;
                } else {
                    r0.countLeft = (getMeasuredWidth() - r0.countWidth) - AndroidUtilities.dp(19.0f);
                }
            }
        } else {
            r0.lastUnreadCount = 0;
            r0.countLayout = null;
        }
        float f = 12.0f;
        r0.nameLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint3, (float) (r0.nameWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint3, r0.nameWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        float f2 = 13.0f;
        if (r0.chat != null) {
            if (r0.subLabel == null) {
                r0.onlineLayout = null;
                r0.nameTop = AndroidUtilities.dp(25.0f);
                if (LocaleController.isRTL) {
                    measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
                } else {
                    if (AndroidUtilities.isTablet()) {
                        f2 = 9.0f;
                    }
                    measuredWidth2 = AndroidUtilities.dp(f2);
                }
                r0.avatarImage.setImageCoords(measuredWidth2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                if (LocaleController.isRTL) {
                    if (r0.nameLayout.getLineCount() > 0 && r0.nameLayout.getLineLeft(0) == 0.0f) {
                        ceil = Math.ceil((double) r0.nameLayout.getLineWidth(0));
                        if (ceil < ((double) r0.nameWidth)) {
                            r0.nameLeft = (int) (((double) r0.nameLeft) + (((double) r0.nameWidth) - ceil));
                        }
                    }
                    if (r0.onlineLayout != null && r0.onlineLayout.getLineCount() > 0 && r0.onlineLayout.getLineLeft(0) == 0.0f) {
                        ceil2 = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
                        d = (double) measuredWidth;
                        if (ceil2 < d) {
                            r0.onlineLeft = (int) (((double) r0.onlineLeft) + (d - ceil2));
                        }
                    }
                } else {
                    if (r0.nameLayout.getLineCount() > 0 && r0.nameLayout.getLineRight(0) == ((float) r0.nameWidth)) {
                        ceil = Math.ceil((double) r0.nameLayout.getLineWidth(0));
                        if (ceil < ((double) r0.nameWidth)) {
                            r0.nameLeft = (int) (((double) r0.nameLeft) - (((double) r0.nameWidth) - ceil));
                        }
                    }
                    if (r0.onlineLayout != null && r0.onlineLayout.getLineCount() > 0 && r0.onlineLayout.getLineRight(0) == ((float) measuredWidth)) {
                        ceil2 = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
                        d = (double) measuredWidth;
                        if (ceil2 < d) {
                            r0.onlineLeft = (int) (((double) r0.onlineLeft) - (d - ceil2));
                        }
                    }
                }
                if (!LocaleController.isRTL) {
                    r0.nameLeft += r0.paddingRight;
                    r0.onlineLeft += r0.paddingRight;
                }
            }
        }
        if (LocaleController.isRTL) {
            r0.onlineLeft = AndroidUtilities.dp(11.0f);
        } else {
            r0.onlineLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        }
        charSequence = TtmlNode.ANONYMOUS_REGION_ID;
        TextPaint textPaint4 = Theme.dialogs_offlinePaint;
        if (r0.subLabel != null) {
            charSequence = r0.subLabel;
        } else if (r0.user != null) {
            if (r0.user.bot) {
                charSequence = LocaleController.getString("Bot", C0446R.string.Bot);
            } else {
                if (r0.user.id != 333000) {
                    if (r0.user.id != 777000) {
                        charSequence = LocaleController.formatUserStatus(r0.currentAccount, r0.user);
                        if (r0.user != null && (r0.user.id == UserConfig.getInstance(r0.currentAccount).getClientUserId() || (r0.user.status != null && r0.user.status.expires > ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime()))) {
                            textPaint2 = Theme.dialogs_onlinePaint;
                            charSequence = LocaleController.getString("Online", C0446R.string.Online);
                            if (r0.savedMessages) {
                                r0.onlineLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint2, (float) (measuredWidth - AndroidUtilities.dp(f)), TruncateAt.END), textPaint2, measuredWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                r0.nameTop = AndroidUtilities.dp(13.0f);
                                if (!(r0.subLabel == null || r0.chat == null)) {
                                    r0.nameLockTop -= AndroidUtilities.dp(f);
                                }
                            } else {
                                r0.onlineLayout = null;
                                r0.nameTop = AndroidUtilities.dp(25.0f);
                            }
                            if (LocaleController.isRTL) {
                                if (AndroidUtilities.isTablet()) {
                                }
                                measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
                            } else {
                                if (AndroidUtilities.isTablet()) {
                                    f2 = 9.0f;
                                }
                                measuredWidth2 = AndroidUtilities.dp(f2);
                            }
                            r0.avatarImage.setImageCoords(measuredWidth2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                            if (LocaleController.isRTL) {
                                ceil = Math.ceil((double) r0.nameLayout.getLineWidth(0));
                                if (ceil < ((double) r0.nameWidth)) {
                                    r0.nameLeft = (int) (((double) r0.nameLeft) - (((double) r0.nameWidth) - ceil));
                                }
                                ceil2 = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
                                d = (double) measuredWidth;
                                if (ceil2 < d) {
                                    r0.onlineLeft = (int) (((double) r0.onlineLeft) - (d - ceil2));
                                }
                            } else {
                                ceil = Math.ceil((double) r0.nameLayout.getLineWidth(0));
                                if (ceil < ((double) r0.nameWidth)) {
                                    r0.nameLeft = (int) (((double) r0.nameLeft) + (((double) r0.nameWidth) - ceil));
                                }
                                ceil2 = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
                                d = (double) measuredWidth;
                                if (ceil2 < d) {
                                    r0.onlineLeft = (int) (((double) r0.onlineLeft) + (d - ceil2));
                                }
                            }
                            if (!LocaleController.isRTL) {
                                r0.nameLeft += r0.paddingRight;
                                r0.onlineLeft += r0.paddingRight;
                            }
                        }
                    }
                }
                charSequence = LocaleController.getString("ServiceNotifications", C0446R.string.ServiceNotifications);
            }
        }
        textPaint2 = textPaint4;
        if (r0.savedMessages) {
            r0.onlineLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint2, (float) (measuredWidth - AndroidUtilities.dp(f)), TruncateAt.END), textPaint2, measuredWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            r0.nameTop = AndroidUtilities.dp(13.0f);
            r0.nameLockTop -= AndroidUtilities.dp(f);
        } else {
            r0.onlineLayout = null;
            r0.nameTop = AndroidUtilities.dp(25.0f);
        }
        if (LocaleController.isRTL) {
            if (AndroidUtilities.isTablet()) {
                f2 = 9.0f;
            }
            measuredWidth2 = AndroidUtilities.dp(f2);
        } else {
            if (AndroidUtilities.isTablet()) {
            }
            measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        }
        r0.avatarImage.setImageCoords(measuredWidth2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        if (LocaleController.isRTL) {
            ceil = Math.ceil((double) r0.nameLayout.getLineWidth(0));
            if (ceil < ((double) r0.nameWidth)) {
                r0.nameLeft = (int) (((double) r0.nameLeft) + (((double) r0.nameWidth) - ceil));
            }
            ceil2 = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
            d = (double) measuredWidth;
            if (ceil2 < d) {
                r0.onlineLeft = (int) (((double) r0.onlineLeft) + (d - ceil2));
            }
        } else {
            ceil = Math.ceil((double) r0.nameLayout.getLineWidth(0));
            if (ceil < ((double) r0.nameWidth)) {
                r0.nameLeft = (int) (((double) r0.nameLeft) - (((double) r0.nameWidth) - ceil));
            }
            ceil2 = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
            d = (double) measuredWidth;
            if (ceil2 < d) {
                r0.onlineLeft = (int) (((double) r0.onlineLeft) - (d - ceil2));
            }
        }
        if (!LocaleController.isRTL) {
            r0.nameLeft += r0.paddingRight;
            r0.onlineLeft += r0.paddingRight;
        }
    }

    public void update(int i) {
        String str = null;
        if (this.user != null) {
            this.avatarDrawable.setInfo(this.user);
            if (this.savedMessages) {
                this.avatarDrawable.setSavedMessages(1);
            } else if (this.user.photo != null) {
                str = this.user.photo.photo_small;
            }
        } else if (this.chat != null) {
            if (this.chat.photo != null) {
                str = this.chat.photo.photo_small;
            }
            this.avatarDrawable.setInfo(this.chat);
        } else {
            this.avatarDrawable.setInfo(0, null, null, false);
        }
        TLObject tLObject = str;
        if (i != 0) {
            int i2 = ((((i & 2) == 0 || this.user == null) && ((i & 8) == 0 || this.chat == null)) || ((this.lastAvatar == null || tLObject != null) && (this.lastAvatar != null || tLObject == null || this.lastAvatar == null || tLObject == null || (this.lastAvatar.volume_id == tLObject.volume_id && this.lastAvatar.local_id == tLObject.local_id)))) ? false : 1;
            if (!(i2 != 0 || (i & 4) == 0 || this.user == null)) {
                if ((this.user.status != null ? this.user.status.expires : 0) != this.lastStatus) {
                    i2 = 1;
                }
            }
            if (!((i2 != 0 || (i & 1) == 0 || this.user == null) && ((i & 16) == 0 || this.chat == null))) {
                if (this.user != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(this.user.first_name);
                    stringBuilder.append(this.user.last_name);
                    str = stringBuilder.toString();
                } else {
                    str = this.chat.title;
                }
                if (!str.equals(this.lastName)) {
                    i2 = 1;
                }
            }
            if (i2 == 0 && this.drawCount && (i & 256) != 0) {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
                if (!(tL_dialog == null || tL_dialog.unread_count == this.lastUnreadCount)) {
                    i2 = 1;
                }
            }
            if (i2 == 0) {
                return;
            }
        }
        if (this.user != 0) {
            if (this.user.status != 0) {
                this.lastStatus = this.user.status.expires;
            } else {
                this.lastStatus = 0;
            }
            i = new StringBuilder();
            i.append(this.user.first_name);
            i.append(this.user.last_name);
            this.lastName = i.toString();
        } else if (this.chat != 0) {
            this.lastName = this.chat.title;
        }
        this.lastAvatar = tLObject;
        this.avatarImage.setImage(tLObject, "50_50", this.avatarDrawable, null, 0);
        if (getMeasuredWidth() == 0) {
            if (getMeasuredHeight() == 0) {
                requestLayout();
                postInvalidate();
            }
        }
        buildLayout();
        postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            int lineRight;
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            }
            if (this.drawNameLock) {
                BaseCell.setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                BaseCell.setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_groupDrawable.draw(canvas);
            } else if (this.drawNameBroadcast) {
                BaseCell.setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                BaseCell.setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) this.nameTop);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (this.drawCheck) {
                    if (!LocaleController.isRTL) {
                        lineRight = (int) ((((float) this.nameLeft) + this.nameLayout.getLineRight(0)) + ((float) AndroidUtilities.dp(6.0f)));
                    } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        lineRight = (this.nameLeft - AndroidUtilities.dp(6.0f)) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    } else {
                        lineRight = (int) (((((double) (this.nameLeft + this.nameWidth)) - Math.ceil((double) this.nameLayout.getLineWidth(0))) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                    }
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, lineRight, this.nameLockTop);
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, lineRight, this.nameLockTop);
                    Theme.dialogs_verifiedDrawable.draw(canvas);
                    Theme.dialogs_verifiedCheckDrawable.draw(canvas);
                }
            }
            if (this.onlineLayout != null) {
                canvas.save();
                canvas.translate((float) this.onlineLeft, (float) AndroidUtilities.dp(40.0f));
                this.onlineLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countLayout != null) {
                lineRight = this.countLeft - AndroidUtilities.dp(5.5f);
                this.rect.set((float) lineRight, (float) this.countTop, (float) ((lineRight + this.countWidth) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, 11.5f * AndroidUtilities.density, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            this.avatarImage.draw(canvas);
        }
    }
}
