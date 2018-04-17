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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
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

    public void setData(TLObject object, EncryptedChat ec, CharSequence n, CharSequence s, boolean needCount, boolean saved) {
        this.currentName = n;
        if (object instanceof User) {
            this.user = (User) object;
            this.chat = null;
        } else if (object instanceof Chat) {
            this.chat = (Chat) object;
            this.user = null;
        }
        this.encryptedChat = ec;
        this.subLabel = s;
        this.drawCount = needCount;
        this.savedMessages = saved;
        update(0);
    }

    public void setPaddingRight(int padding) {
        this.paddingRight = padding;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(72.0f));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!(this.user == null && this.chat == null && this.encryptedChat == null) && changed) {
            buildLayout();
        }
    }

    public void buildLayout() {
        CharSequence nameString;
        TextPaint currentNamePaint;
        int onlineWidth;
        float f;
        int avatarLeft;
        double widthpx;
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
            nameString = r0.currentName;
        } else {
            String nameString2 = TtmlNode.ANONYMOUS_REGION_ID;
            if (r0.chat != null) {
                nameString2 = r0.chat.title;
            } else if (r0.user != null) {
                nameString2 = UserObject.getUserName(r0.user);
            }
            nameString = nameString2.replace('\n', ' ');
        }
        if (nameString.length() == 0) {
            if (r0.user == null || r0.user.phone == null || r0.user.phone.length() == 0) {
                nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
            } else {
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(r0.user.phone);
                nameString = instance.format(stringBuilder.toString());
            }
        }
        if (r0.encryptedChat != null) {
            currentNamePaint = Theme.dialogs_nameEncryptedPaint;
        } else {
            currentNamePaint = Theme.dialogs_namePaint;
        }
        if (LocaleController.isRTL) {
            onlineWidth = (getMeasuredWidth() - r0.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            r0.nameWidth = onlineWidth;
        } else {
            onlineWidth = (getMeasuredWidth() - r0.nameLeft) - AndroidUtilities.dp(14.0f);
            r0.nameWidth = onlineWidth;
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
        onlineWidth -= r0.paddingRight;
        if (r0.drawCount) {
            TL_dialog dialog = (TL_dialog) MessagesController.getInstance(r0.currentAccount).dialogs_dict.get(r0.dialog_id);
            if (dialog == null || dialog.unread_count == 0) {
                r0.lastUnreadCount = 0;
                r0.countLayout = null;
            } else {
                r0.lastUnreadCount = dialog.unread_count;
                String countString = String.format("%d", new Object[]{Integer.valueOf(dialog.unread_count)});
                r0.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(countString)));
                r0.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, r0.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int w = r0.countWidth + AndroidUtilities.dp(18.0f);
                r0.nameWidth -= w;
                if (LocaleController.isRTL) {
                    r0.countLeft = AndroidUtilities.dp(19.0f);
                    r0.nameLeft += w;
                } else {
                    r0.countLeft = (getMeasuredWidth() - r0.countWidth) - AndroidUtilities.dp(19.0f);
                }
            }
        } else {
            r0.lastUnreadCount = 0;
            r0.countLayout = null;
        }
        StaticLayout staticLayout = r7;
        StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(nameString, currentNamePaint, (float) (r0.nameWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, r0.nameWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        r0.nameLayout = staticLayout;
        if (r0.chat != null) {
            if (r0.subLabel == null) {
                r0.onlineLayout = null;
                r0.nameTop = AndroidUtilities.dp(25.0f);
                f = 13.0f;
                if (LocaleController.isRTL) {
                    if (AndroidUtilities.isTablet()) {
                        f = 9.0f;
                    }
                    avatarLeft = AndroidUtilities.dp(f);
                } else {
                    avatarLeft = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
                }
                r0.avatarImage.setImageCoords(avatarLeft, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                if (LocaleController.isRTL) {
                    if (r0.nameLayout.getLineCount() > 0 && r0.nameLayout.getLineRight(0) == ((float) r0.nameWidth)) {
                        widthpx = Math.ceil((double) r0.nameLayout.getLineWidth(0));
                        if (widthpx < ((double) r0.nameWidth)) {
                            r0.nameLeft = (int) (((double) r0.nameLeft) - (((double) r0.nameWidth) - widthpx));
                        }
                    }
                    if (r0.onlineLayout != null && r0.onlineLayout.getLineCount() > 0 && r0.onlineLayout.getLineRight(0) == ((float) onlineWidth)) {
                        widthpx = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
                        if (widthpx < ((double) onlineWidth)) {
                            r0.onlineLeft = (int) (((double) r0.onlineLeft) - (((double) onlineWidth) - widthpx));
                        }
                    }
                } else {
                    if (r0.nameLayout.getLineCount() > 0 && r0.nameLayout.getLineLeft(0) == 0.0f) {
                        widthpx = Math.ceil((double) r0.nameLayout.getLineWidth(0));
                        if (widthpx < ((double) r0.nameWidth)) {
                            r0.nameLeft = (int) (((double) r0.nameLeft) + (((double) r0.nameWidth) - widthpx));
                        }
                    }
                    if (r0.onlineLayout != null && r0.onlineLayout.getLineCount() > 0 && r0.onlineLayout.getLineLeft(0) == 0.0f) {
                        widthpx = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
                        if (widthpx < ((double) onlineWidth)) {
                            r0.onlineLeft = (int) (((double) r0.onlineLeft) + (((double) onlineWidth) - widthpx));
                        }
                    }
                }
                if (LocaleController.isRTL) {
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
        CharSequence onlineString = TtmlNode.ANONYMOUS_REGION_ID;
        TextPaint currentOnlinePaint = Theme.dialogs_offlinePaint;
        if (r0.subLabel != null) {
            onlineString = r0.subLabel;
        } else if (r0.user != null) {
            if (r0.user.bot) {
                onlineString = LocaleController.getString("Bot", R.string.Bot);
            } else {
                if (r0.user.id != 333000) {
                    if (r0.user.id != 777000) {
                        onlineString = LocaleController.formatUserStatus(r0.currentAccount, r0.user);
                        if (r0.user != null && (r0.user.id == UserConfig.getInstance(r0.currentAccount).getClientUserId() || (r0.user.status != null && r0.user.status.expires > ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime()))) {
                            currentOnlinePaint = Theme.dialogs_onlinePaint;
                            onlineString = LocaleController.getString("Online", R.string.Online);
                        }
                    }
                }
                onlineString = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
            }
        }
        if (r0.savedMessages) {
            r0.onlineLayout = null;
            r0.nameTop = AndroidUtilities.dp(25.0f);
            f = 13.0f;
        } else {
            staticLayout2 = r8;
            float f2 = 12.0f;
            StaticLayout staticLayout3 = new StaticLayout(TextUtils.ellipsize(onlineString, currentOnlinePaint, (float) (onlineWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentOnlinePaint, onlineWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            r0.onlineLayout = staticLayout2;
            f = 13.0f;
            r0.nameTop = AndroidUtilities.dp(13.0f);
            if (!(r0.subLabel == null || r0.chat == null)) {
                r0.nameLockTop -= AndroidUtilities.dp(f2);
            }
        }
        if (LocaleController.isRTL) {
            if (AndroidUtilities.isTablet()) {
            }
            avatarLeft = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            if (AndroidUtilities.isTablet()) {
                f = 9.0f;
            }
            avatarLeft = AndroidUtilities.dp(f);
        }
        r0.avatarImage.setImageCoords(avatarLeft, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        if (LocaleController.isRTL) {
            widthpx = Math.ceil((double) r0.nameLayout.getLineWidth(0));
            if (widthpx < ((double) r0.nameWidth)) {
                r0.nameLeft = (int) (((double) r0.nameLeft) - (((double) r0.nameWidth) - widthpx));
            }
            widthpx = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
            if (widthpx < ((double) onlineWidth)) {
                r0.onlineLeft = (int) (((double) r0.onlineLeft) - (((double) onlineWidth) - widthpx));
            }
        } else {
            widthpx = Math.ceil((double) r0.nameLayout.getLineWidth(0));
            if (widthpx < ((double) r0.nameWidth)) {
                r0.nameLeft = (int) (((double) r0.nameLeft) + (((double) r0.nameWidth) - widthpx));
            }
            widthpx = Math.ceil((double) r0.onlineLayout.getLineWidth(0));
            if (widthpx < ((double) onlineWidth)) {
                r0.onlineLeft = (int) (((double) r0.onlineLeft) + (((double) onlineWidth) - widthpx));
            }
        }
        if (LocaleController.isRTL) {
            r0.nameLeft += r0.paddingRight;
            r0.onlineLeft += r0.paddingRight;
        }
    }

    public void update(int mask) {
        TLObject photo = null;
        if (this.user != null) {
            this.avatarDrawable.setInfo(this.user);
            if (this.savedMessages) {
                this.avatarDrawable.setSavedMessages(1);
            } else if (this.user.photo != null) {
                photo = this.user.photo.photo_small;
            }
        } else if (this.chat != null) {
            if (this.chat.photo != null) {
                photo = this.chat.photo.photo_small;
            }
            this.avatarDrawable.setInfo(this.chat);
        } else {
            this.avatarDrawable.setInfo(0, null, null, false);
        }
        if (mask != 0) {
            boolean continueUpdate = false;
            if (!(((mask & 2) == 0 || this.user == null) && ((mask & 8) == 0 || this.chat == null)) && ((this.lastAvatar != null && photo == null) || !(this.lastAvatar != null || photo == null || this.lastAvatar == null || photo == null || (this.lastAvatar.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id)))) {
                continueUpdate = true;
            }
            if (!(continueUpdate || (mask & 4) == 0 || this.user == null)) {
                int newStatus = 0;
                if (this.user.status != null) {
                    newStatus = this.user.status.expires;
                }
                if (newStatus != this.lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!((continueUpdate || (mask & 1) == 0 || this.user == null) && ((mask & 16) == 0 || this.chat == null))) {
                String newName;
                if (this.user != null) {
                    newName = new StringBuilder();
                    newName.append(this.user.first_name);
                    newName.append(this.user.last_name);
                    newName = newName.toString();
                } else {
                    newName = this.chat.title;
                }
                if (!newName.equals(this.lastName)) {
                    continueUpdate = true;
                }
            }
            if (!(continueUpdate || !this.drawCount || (mask & 256) == 0)) {
                TL_dialog dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
                if (!(dialog == null || dialog.unread_count == this.lastUnreadCount)) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate) {
                return;
            }
        }
        if (this.user != null) {
            if (this.user.status != null) {
                this.lastStatus = this.user.status.expires;
            } else {
                this.lastStatus = 0;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.user.first_name);
            stringBuilder.append(this.user.last_name);
            this.lastName = stringBuilder.toString();
        } else if (this.chat != null) {
            this.lastName = this.chat.title;
        }
        this.lastAvatar = photo;
        this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, null, 0);
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
            int x;
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
                        x = (int) ((((float) this.nameLeft) + this.nameLayout.getLineRight(0)) + ((float) AndroidUtilities.dp(6.0f)));
                    } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        x = (this.nameLeft - AndroidUtilities.dp(6.0f)) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    } else {
                        x = (int) (((((double) (this.nameLeft + this.nameWidth)) - Math.ceil((double) this.nameLayout.getLineWidth(0))) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                    }
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, x, this.nameLockTop);
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, x, this.nameLockTop);
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
                x = this.countLeft - AndroidUtilities.dp(5.5f);
                this.rect.set((float) x, (float) this.countTop, (float) ((this.countWidth + x) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
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
