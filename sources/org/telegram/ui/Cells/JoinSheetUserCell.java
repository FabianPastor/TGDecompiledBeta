package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class JoinSheetUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView imageView;
    private TextView nameTextView;
    private int[] result = new int[1];

    public JoinSheetUserCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), NUM));
    }

    public void setUser(User user) {
        this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.avatarDrawable.setInfo(user);
        TLObject photo = null;
        if (!(user == null || user.photo == null)) {
            photo = user.photo.photo_small;
        }
        this.imageView.setImage(photo, "50_50", this.avatarDrawable);
    }

    public void setCount(int count) {
        this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
        AvatarDrawable avatarDrawable = this.avatarDrawable;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("+");
        stringBuilder.append(LocaleController.formatShortNumber(count, this.result));
        avatarDrawable.setInfo(0, null, null, false, stringBuilder.toString());
        this.imageView.setImage((FileLocation) null, "50_50", this.avatarDrawable);
    }
}
