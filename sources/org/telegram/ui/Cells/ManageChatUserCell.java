package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ManageChatUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currrntStatus;
    private ImageView customImageView;
    private ManageChatUserCellDelegate delegate;
    private String dividerColor;
    private boolean isAdmin;
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int namePadding;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private ImageView optionsButton;
    private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
    private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
    private SimpleTextView statusTextView;

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ManageChatUserCell(Context context, int i, int i2, boolean z) {
        super(context);
        this.namePadding = i2;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z2 = LocaleController.isRTL;
        int i3 = 5;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : (float) (i + 7), 8.0f, z2 ? (float) (i + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 46.0f : (float) (this.namePadding + 68), 11.5f, z3 ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z4 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z4 ? 5 : 3) | 48, z4 ? 28.0f : (float) (this.namePadding + 68), 34.5f, z4 ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (z) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(60, 64, (LocaleController.isRTL ? 3 : i3) | 48));
            this.optionsButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ManageChatUserCell.this.lambda$new$0$ManageChatUserCell(view);
                }
            });
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ManageChatUserCell(View view) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setCustomRightImage(int i) {
        ImageView imageView = new ImageView(getContext());
        this.customImageView = imageView;
        imageView.setImageResource(i);
        this.customImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.customImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_mutedIconUnscrolled"), PorterDuff.Mode.MULTIPLY));
        addView(this.customImageView, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : 5) | 48));
    }

    public void setCustomImageVisible(boolean z) {
        ImageView imageView = this.customImageView;
        if (imageView != null) {
            imageView.setVisibility(z ? 0 : 8);
        }
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        float f;
        float f2;
        Object obj2 = obj;
        CharSequence charSequence3 = charSequence2;
        boolean z2 = z;
        if (obj2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = charSequence3;
        this.currentName = charSequence;
        this.currentObject = obj2;
        float f3 = 20.5f;
        int i = 5;
        int i2 = 28;
        if (this.optionsButton != null) {
            boolean onOptionsButtonCheck = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(onOptionsButtonCheck ? 0 : 4);
            SimpleTextView simpleTextView = this.nameTextView;
            boolean z3 = LocaleController.isRTL;
            int i3 = (z3 ? 5 : 3) | 48;
            float f4 = (float) (z3 ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68);
            if (charSequence3 == null || charSequence2.length() > 0) {
                f3 = 11.5f;
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i3, f4, f3, (float) (LocaleController.isRTL ? this.namePadding + 68 : onOptionsButtonCheck ? 46 : 28), 0.0f));
            SimpleTextView simpleTextView2 = this.statusTextView;
            boolean z4 = LocaleController.isRTL;
            if (!z4) {
                i = 3;
            }
            int i4 = i | 48;
            float f5 = (float) (z4 ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68);
            if (z4) {
                f2 = (float) (this.namePadding + 68);
            } else {
                if (onOptionsButtonCheck) {
                    i2 = 46;
                }
                f2 = (float) i2;
            }
            simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i4, f5, 34.5f, f2, 0.0f));
        } else {
            ImageView imageView = this.customImageView;
            if (imageView != null) {
                boolean z5 = imageView.getVisibility() == 0;
                SimpleTextView simpleTextView3 = this.nameTextView;
                boolean z6 = LocaleController.isRTL;
                int i5 = (z6 ? 5 : 3) | 48;
                float f6 = (float) (z6 ? z5 ? 54 : 28 : this.namePadding + 68);
                if (charSequence3 == null || charSequence2.length() > 0) {
                    f3 = 11.5f;
                }
                simpleTextView3.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i5, f6, f3, (float) (LocaleController.isRTL ? this.namePadding + 68 : z5 ? 54 : 28), 0.0f));
                SimpleTextView simpleTextView4 = this.statusTextView;
                boolean z7 = LocaleController.isRTL;
                if (!z7) {
                    i = 3;
                }
                int i6 = i | 48;
                float f7 = (float) (z7 ? z5 ? 54 : 28 : this.namePadding + 68);
                if (z7) {
                    f = (float) (this.namePadding + 68);
                } else {
                    if (z5) {
                        i2 = 54;
                    }
                    f = (float) i2;
                }
                simpleTextView4.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i6, f7, 34.5f, f, 0.0f));
            }
        }
        this.needDivider = z2;
        setWillNotDraw(!z2);
        update(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public int getUserId() {
        Object obj = this.currentObject;
        if (obj instanceof TLRPC$User) {
            return ((TLRPC$User) obj).id;
        }
        return 0;
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setNameColor(int i) {
        this.nameTextView.setTextColor(i);
    }

    public void setDividerColor(String str) {
        this.dividerColor = str;
    }

    public void update(int i) {
        String str;
        String str2;
        TLRPC$FileLocation tLRPC$FileLocation;
        String str3;
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$FileLocation tLRPC$FileLocation2;
        Object obj = this.currentObject;
        if (obj != null) {
            boolean z = false;
            if (obj instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) obj;
                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
                TLRPC$FileLocation tLRPC$FileLocation3 = tLRPC$UserProfilePhoto != null ? tLRPC$UserProfilePhoto.photo_small : null;
                if (i != 0) {
                    boolean z2 = (i & 2) != 0 && (((tLRPC$FileLocation2 = this.lastAvatar) != null && tLRPC$FileLocation3 == null) || ((tLRPC$FileLocation2 == null && tLRPC$FileLocation3 != null) || !(tLRPC$FileLocation2 == null || tLRPC$FileLocation3 == null || (tLRPC$FileLocation2.volume_id == tLRPC$FileLocation3.volume_id && tLRPC$FileLocation2.local_id == tLRPC$FileLocation3.local_id))));
                    if (!(tLRPC$User == null || z2 || (i & 4) == 0)) {
                        TLRPC$UserStatus tLRPC$UserStatus2 = tLRPC$User.status;
                        if ((tLRPC$UserStatus2 != null ? tLRPC$UserStatus2.expires : 0) != this.lastStatus) {
                            z2 = true;
                        }
                    }
                    if (z2 || this.currentName != null || this.lastName == null || (i & 1) == 0) {
                        str3 = null;
                    } else {
                        str3 = UserObject.getUserName(tLRPC$User);
                        if (!str3.equals(this.lastName)) {
                            z2 = true;
                        }
                    }
                    if (!z2) {
                        return;
                    }
                } else {
                    str3 = null;
                }
                this.avatarDrawable.setInfo(tLRPC$User);
                TLRPC$UserStatus tLRPC$UserStatus3 = tLRPC$User.status;
                if (tLRPC$UserStatus3 != null) {
                    this.lastStatus = tLRPC$UserStatus3.expires;
                } else {
                    this.lastStatus = 0;
                }
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence);
                } else {
                    if (str3 == null) {
                        str3 = UserObject.getUserName(tLRPC$User);
                    }
                    this.lastName = str3;
                    this.nameTextView.setText(str3);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else if (tLRPC$User.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (tLRPC$User.bot_chat_history || this.isAdmin) {
                        this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
                    }
                } else if (tLRPC$User.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (((tLRPC$UserStatus = tLRPC$User.status) != null && tLRPC$UserStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(tLRPC$User.id)))) {
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", NUM));
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, tLRPC$User));
                }
                this.lastAvatar = tLRPC$FileLocation3;
                this.avatarImageView.setImage(ImageLocation.getForUserOrChat(tLRPC$User, 1), "50_50", ImageLocation.getForUserOrChat(tLRPC$User, 2), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
            } else if (obj instanceof TLRPC$Chat) {
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) obj;
                TLRPC$ChatPhoto tLRPC$ChatPhoto = tLRPC$Chat.photo;
                TLRPC$FileLocation tLRPC$FileLocation4 = tLRPC$ChatPhoto != null ? tLRPC$ChatPhoto.photo_small : null;
                if (i != 0) {
                    if ((i & 2) != 0 && (((tLRPC$FileLocation = this.lastAvatar) != null && tLRPC$FileLocation4 == null) || ((tLRPC$FileLocation == null && tLRPC$FileLocation4 != null) || !(tLRPC$FileLocation == null || tLRPC$FileLocation4 == null || (tLRPC$FileLocation.volume_id == tLRPC$FileLocation4.volume_id && tLRPC$FileLocation.local_id == tLRPC$FileLocation4.local_id))))) {
                        z = true;
                    }
                    if (z || this.currentName != null || (str2 = this.lastName) == null || (i & 1) == 0) {
                        str = null;
                    } else {
                        str = tLRPC$Chat.title;
                        if (!str.equals(str2)) {
                            z = true;
                        }
                    }
                    if (!z) {
                        return;
                    }
                } else {
                    str = null;
                }
                this.avatarDrawable.setInfo(tLRPC$Chat);
                CharSequence charSequence2 = this.currentName;
                if (charSequence2 != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence2);
                } else {
                    if (str == null) {
                        str = tLRPC$Chat.title;
                    }
                    this.lastName = str;
                    this.nameTextView.setText(str);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (tLRPC$Chat.participants_count != 0) {
                        if (!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup) {
                            this.statusTextView.setText(LocaleController.formatPluralString("Members", tLRPC$Chat.participants_count));
                        } else {
                            this.statusTextView.setText(LocaleController.formatPluralString("Subscribers", tLRPC$Chat.participants_count));
                        }
                    } else if (tLRPC$Chat.has_geo) {
                        this.statusTextView.setText(LocaleController.getString("MegaLocation", NUM));
                    } else if (TextUtils.isEmpty(tLRPC$Chat.username)) {
                        this.statusTextView.setText(LocaleController.getString("MegaPrivate", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("MegaPublic", NUM));
                    }
                }
                this.lastAvatar = tLRPC$FileLocation4;
                this.avatarImageView.setImage(ImageLocation.getForUserOrChat(tLRPC$Chat, 1), "50_50", ImageLocation.getForUserOrChat(tLRPC$Chat, 2), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$Chat);
            } else if (obj instanceof Integer) {
                this.nameTextView.setText(this.currentName);
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
                this.avatarDrawable.setAvatarType(3);
                this.avatarImageView.setImage((String) null, "50_50", this.avatarDrawable);
            }
        }
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            String str = this.dividerColor;
            if (str != null) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(str));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerColor != null ? Theme.dividerExtraPaint : Theme.dividerPaint);
        }
    }
}
