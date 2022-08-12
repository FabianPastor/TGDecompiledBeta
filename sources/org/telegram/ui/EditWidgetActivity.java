package org.telegram.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatsWidgetProvider;
import org.telegram.messenger.ContactsWidgetProvider;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RecyclerListView;

public class EditWidgetActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int chatsEndRow;
    /* access modifiers changed from: private */
    public int chatsStartRow;
    /* access modifiers changed from: private */
    public int currentWidgetId;
    /* access modifiers changed from: private */
    public EditWidgetActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int infoRow;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ImageView previewImageView;
    /* access modifiers changed from: private */
    public int previewRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int selectChatsRow;
    /* access modifiers changed from: private */
    public ArrayList<Long> selectedDialogs = new ArrayList<>();
    /* access modifiers changed from: private */
    public WidgetPreviewCell widgetPreviewCell;
    /* access modifiers changed from: private */
    public int widgetType;

    public interface EditWidgetActivityDelegate {
        void didSelectDialogs(ArrayList<Long> arrayList);
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        private boolean moved;

        public boolean isLongPressDragEnabled() {
            return false;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 3) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            boolean z = false;
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            int adapterPosition2 = viewHolder2.getAdapterPosition();
            if (EditWidgetActivity.this.listAdapter.swapElements(adapterPosition, adapterPosition2)) {
                ((GroupCreateUserCell) viewHolder.itemView).setDrawDivider(adapterPosition2 != EditWidgetActivity.this.chatsEndRow - 1);
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder2.itemView;
                if (adapterPosition != EditWidgetActivity.this.chatsEndRow - 1) {
                    z = true;
                }
                groupCreateUserCell.setDrawDivider(z);
                this.moved = true;
            }
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                EditWidgetActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            } else if (this.moved) {
                if (EditWidgetActivity.this.widgetPreviewCell != null) {
                    EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                }
                this.moved = false;
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public class WidgetPreviewCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        private RectF bitmapRect = new RectF();
        private ViewGroup[] cells = new ViewGroup[2];
        private Drawable oldBackgroundDrawable;
        private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
        private Paint roundPaint = new Paint(1);
        private Drawable shadowDrawable;
        final /* synthetic */ EditWidgetActivity this$0;

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean z) {
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public WidgetPreviewCell(org.telegram.ui.EditWidgetActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                android.graphics.Paint r3 = new android.graphics.Paint
                r4 = 1
                r3.<init>(r4)
                r0.roundPaint = r3
                android.graphics.RectF r3 = new android.graphics.RectF
                r3.<init>()
                r0.bitmapRect = r3
                r3 = 2
                android.view.ViewGroup[] r5 = new android.view.ViewGroup[r3]
                r0.cells = r5
                r5 = 0
                r0.setWillNotDraw(r5)
                r6 = 1103101952(0x41CLASSNAME, float:24.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r0.setPadding(r5, r7, r5, r6)
                android.widget.LinearLayout r6 = new android.widget.LinearLayout
                r6.<init>(r2)
                r6.setOrientation(r4)
                r7 = -2
                r8 = 17
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r8)
                r0.addView(r6, r9)
                org.telegram.ui.Cells.ChatActionCell r9 = new org.telegram.ui.Cells.ChatActionCell
                r9.<init>(r2)
                int r10 = org.telegram.messenger.R.string.WidgetPreview
                java.lang.String r11 = "WidgetPreview"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
                r9.setCustomText(r10)
                r11 = -2
                r12 = -2
                r13 = 17
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 4
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r6.addView(r9, r10)
                android.widget.LinearLayout r9 = new android.widget.LinearLayout
                r9.<init>(r2)
                r9.setOrientation(r4)
                int r10 = org.telegram.messenger.R.drawable.widget_bg
                r9.setBackgroundResource(r10)
                r14 = 10
                r16 = 10
                r17 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r6.addView(r9, r10)
                android.widget.ImageView r6 = new android.widget.ImageView
                r6.<init>(r2)
                android.widget.ImageView unused = r1.previewImageView = r6
                int r6 = r19.widgetType
                r10 = 0
                r11 = 160(0xa0, float:2.24E-43)
                if (r6 != 0) goto L_0x00ca
            L_0x008e:
                if (r5 >= r3) goto L_0x00b3
                android.view.ViewGroup[] r4 = r0.cells
                android.app.Activity r6 = r19.getParentActivity()
                android.view.LayoutInflater r6 = r6.getLayoutInflater()
                int r12 = org.telegram.messenger.R.layout.shortcut_widget_item
                android.view.View r6 = r6.inflate(r12, r10)
                android.view.ViewGroup r6 = (android.view.ViewGroup) r6
                r4[r5] = r6
                android.view.ViewGroup[] r4 = r0.cells
                r4 = r4[r5]
                r6 = -1
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7)
                r9.addView(r4, r6)
                int r5 = r5 + 1
                goto L_0x008e
            L_0x00b3:
                android.widget.ImageView r3 = r19.previewImageView
                r4 = 218(0xda, float:3.05E-43)
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r11, (int) r8)
                r9.addView(r3, r4)
                android.widget.ImageView r1 = r19.previewImageView
                int r3 = org.telegram.messenger.R.drawable.chats_widget_preview
                r1.setImageResource(r3)
                goto L_0x0108
            L_0x00ca:
                int r6 = r19.widgetType
                if (r6 != r4) goto L_0x0108
            L_0x00d0:
                if (r5 >= r3) goto L_0x00f4
                android.view.ViewGroup[] r4 = r0.cells
                android.app.Activity r6 = r19.getParentActivity()
                android.view.LayoutInflater r6 = r6.getLayoutInflater()
                int r12 = org.telegram.messenger.R.layout.contacts_widget_item
                android.view.View r6 = r6.inflate(r12, r10)
                android.view.ViewGroup r6 = (android.view.ViewGroup) r6
                r4[r5] = r6
                android.view.ViewGroup[] r4 = r0.cells
                r4 = r4[r5]
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r7)
                r9.addView(r4, r6)
                int r5 = r5 + 1
                goto L_0x00d0
            L_0x00f4:
                android.widget.ImageView r3 = r19.previewImageView
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r8)
                r9.addView(r3, r4)
                android.widget.ImageView r1 = r19.previewImageView
                int r3 = org.telegram.messenger.R.drawable.contacts_widget_preview
                r1.setImageResource(r3)
            L_0x0108:
                r18.updateDialogs()
                int r1 = org.telegram.messenger.R.drawable.greydivider_bottom
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r1, (java.lang.String) r3)
                r0.shadowDrawable = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.<init>(org.telegram.ui.EditWidgetActivity, android.content.Context):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:103:0x028d, code lost:
            if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0290;
         */
        /* JADX WARNING: Removed duplicated region for block: B:268:0x071e  */
        /* JADX WARNING: Removed duplicated region for block: B:278:0x074d  */
        /* JADX WARNING: Removed duplicated region for block: B:376:0x0900  */
        /* JADX WARNING: Removed duplicated region for block: B:389:0x094b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateDialogs() {
            /*
                r28 = this;
                r1 = r28
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                java.lang.String r3 = "%d"
                java.lang.String r4 = "HiddenName"
                java.lang.String r5 = "RepliesTitle"
                java.lang.String r7 = "SavedMessages"
                r8 = 0
                r9 = 0
                r11 = 8
                r12 = 2
                r15 = 0
                if (r0 != 0) goto L_0x067c
                r13 = 0
            L_0x001a:
                if (r13 >= r12) goto L_0x0656
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x004a
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x0045
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                java.lang.Object r0 = r0.get(r13)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                goto L_0x0046
            L_0x0045:
                r0 = 0
            L_0x0046:
                r2 = r0
                r18 = r3
                goto L_0x0094
            L_0x004a:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x0091
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                org.telegram.ui.EditWidgetActivity r12 = r1.this$0
                java.util.ArrayList r12 = r12.selectedDialogs
                java.lang.Object r12 = r12.get(r13)
                java.lang.Long r12 = (java.lang.Long) r12
                r18 = r3
                long r2 = r12.longValue()
                java.lang.Object r0 = r0.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                if (r0 != 0) goto L_0x008f
                org.telegram.tgnet.TLRPC$TL_dialog r0 = new org.telegram.tgnet.TLRPC$TL_dialog
                r0.<init>()
                org.telegram.ui.EditWidgetActivity r2 = r1.this$0
                java.util.ArrayList r2 = r2.selectedDialogs
                java.lang.Object r2 = r2.get(r13)
                java.lang.Long r2 = (java.lang.Long) r2
                long r2 = r2.longValue()
                r0.id = r2
            L_0x008f:
                r2 = r0
                goto L_0x0094
            L_0x0091:
                r18 = r3
                r2 = 0
            L_0x0094:
                if (r2 != 0) goto L_0x00a4
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r11)
                r20 = r4
                r15 = r7
                r4 = r18
                goto L_0x0647
            L_0x00a4:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r15)
                long r11 = r2.id
                boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r11)
                java.lang.String r11 = ""
                if (r0 == 0) goto L_0x0125
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r14 = r2.id
                java.lang.Long r14 = java.lang.Long.valueOf(r14)
                org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r14)
                if (r0 == 0) goto L_0x011e
                boolean r14 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r14 == 0) goto L_0x00d4
                int r14 = org.telegram.messenger.R.string.SavedMessages
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r7, r14)
                goto L_0x00f6
            L_0x00d4:
                boolean r14 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r14 == 0) goto L_0x00e1
                int r14 = org.telegram.messenger.R.string.RepliesTitle
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r5, r14)
                goto L_0x00f6
            L_0x00e1:
                boolean r14 = org.telegram.messenger.UserObject.isDeleted(r0)
                if (r14 == 0) goto L_0x00ee
                int r14 = org.telegram.messenger.R.string.HiddenName
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r4, r14)
                goto L_0x00f6
            L_0x00ee:
                java.lang.String r14 = r0.first_name
                java.lang.String r15 = r0.last_name
                java.lang.String r14 = org.telegram.messenger.ContactsController.formatName(r14, r15)
            L_0x00f6:
                boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r15 != 0) goto L_0x011a
                boolean r15 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r15 != 0) goto L_0x011a
                org.telegram.tgnet.TLRPC$UserProfilePhoto r15 = r0.photo
                if (r15 == 0) goto L_0x011a
                org.telegram.tgnet.TLRPC$FileLocation r15 = r15.photo_small
                if (r15 == 0) goto L_0x011a
                r20 = r4
                long r3 = r15.volume_id
                int r22 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r22 == 0) goto L_0x011c
                int r3 = r15.local_id
                if (r3 == 0) goto L_0x011c
                r3 = r15
                r6 = 0
                r15 = r7
                goto L_0x015a
            L_0x011a:
                r20 = r4
            L_0x011c:
                r15 = r7
                goto L_0x0122
            L_0x011e:
                r20 = r4
                r15 = r7
                r14 = r11
            L_0x0122:
                r3 = 0
                r6 = 0
                goto L_0x015a
            L_0x0125:
                r20 = r4
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r3 = r2.id
                long r3 = -r3
                java.lang.Long r3 = java.lang.Long.valueOf(r3)
                org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
                if (r0 == 0) goto L_0x0155
                java.lang.String r14 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r3 = r0.photo
                if (r3 == 0) goto L_0x0152
                org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_small
                if (r3 == 0) goto L_0x0152
                r15 = r7
                long r6 = r3.volume_id
                int r22 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r22 == 0) goto L_0x0153
                int r6 = r3.local_id
                if (r6 == 0) goto L_0x0153
                r6 = r0
                r0 = 0
                goto L_0x015a
            L_0x0152:
                r15 = r7
            L_0x0153:
                r6 = r0
                goto L_0x0158
            L_0x0155:
                r15 = r7
                r6 = r0
                r14 = r11
            L_0x0158:
                r0 = 0
                r3 = 0
            L_0x015a:
                android.view.ViewGroup[] r7 = r1.cells
                r7 = r7[r13]
                int r4 = org.telegram.messenger.R.id.shortcut_widget_item_text
                android.view.View r4 = r7.findViewById(r4)
                android.widget.TextView r4 = (android.widget.TextView) r4
                r4.setText(r14)
                if (r3 == 0) goto L_0x017f
                org.telegram.ui.EditWidgetActivity r4 = r1.this$0     // Catch:{ all -> 0x0227 }
                org.telegram.messenger.FileLoader r4 = r4.getFileLoader()     // Catch:{ all -> 0x0227 }
                r7 = 1
                java.io.File r3 = r4.getPathToAttach(r3, r7)     // Catch:{ all -> 0x0227 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0227 }
                android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r3)     // Catch:{ all -> 0x0227 }
                goto L_0x0180
            L_0x017f:
                r3 = 0
            L_0x0180:
                r4 = 1111490560(0x42400000, float:48.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x0227 }
                android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0227 }
                android.graphics.Bitmap r14 = android.graphics.Bitmap.createBitmap(r7, r7, r14)     // Catch:{ all -> 0x0227 }
                r4 = 0
                r14.eraseColor(r4)     // Catch:{ all -> 0x0227 }
                android.graphics.Canvas r4 = new android.graphics.Canvas     // Catch:{ all -> 0x0227 }
                r4.<init>(r14)     // Catch:{ all -> 0x0227 }
                if (r3 != 0) goto L_0x01c3
                if (r0 == 0) goto L_0x01b5
                org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0227 }
                r3.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0227 }
                boolean r23 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0227 }
                if (r23 == 0) goto L_0x01aa
                r12 = 12
                r3.setAvatarType(r12)     // Catch:{ all -> 0x0227 }
                goto L_0x01ba
            L_0x01aa:
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x0227 }
                if (r0 == 0) goto L_0x01ba
                r12 = 1
                r3.setAvatarType(r12)     // Catch:{ all -> 0x0227 }
                goto L_0x01ba
            L_0x01b5:
                org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0227 }
                r3.<init>((org.telegram.tgnet.TLRPC$Chat) r6)     // Catch:{ all -> 0x0227 }
            L_0x01ba:
                r12 = 0
                r3.setBounds(r12, r12, r7, r7)     // Catch:{ all -> 0x0227 }
                r3.draw(r4)     // Catch:{ all -> 0x0227 }
            L_0x01c1:
                r3 = 0
                goto L_0x0214
            L_0x01c3:
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0227 }
                android.graphics.Shader$TileMode r12 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0227 }
                r0.<init>(r3, r12, r12)     // Catch:{ all -> 0x0227 }
                android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x0227 }
                if (r12 != 0) goto L_0x01de
                android.graphics.Paint r12 = new android.graphics.Paint     // Catch:{ all -> 0x0227 }
                r9 = 1
                r12.<init>(r9)     // Catch:{ all -> 0x0227 }
                r9 = r12
                r1.roundPaint = r9     // Catch:{ all -> 0x0227 }
                android.graphics.RectF r9 = new android.graphics.RectF     // Catch:{ all -> 0x0227 }
                r9.<init>()     // Catch:{ all -> 0x0227 }
                r1.bitmapRect = r9     // Catch:{ all -> 0x0227 }
            L_0x01de:
                float r7 = (float) r7     // Catch:{ all -> 0x0227 }
                int r9 = r3.getWidth()     // Catch:{ all -> 0x0227 }
                float r9 = (float) r9     // Catch:{ all -> 0x0227 }
                float r7 = r7 / r9
                r4.save()     // Catch:{ all -> 0x0227 }
                r4.scale(r7, r7)     // Catch:{ all -> 0x0227 }
                android.graphics.Paint r7 = r1.roundPaint     // Catch:{ all -> 0x0227 }
                r7.setShader(r0)     // Catch:{ all -> 0x0227 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0227 }
                int r7 = r3.getWidth()     // Catch:{ all -> 0x0227 }
                float r7 = (float) r7     // Catch:{ all -> 0x0227 }
                int r9 = r3.getHeight()     // Catch:{ all -> 0x0227 }
                float r9 = (float) r9     // Catch:{ all -> 0x0227 }
                r0.set(r8, r8, r7, r9)     // Catch:{ all -> 0x0227 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0227 }
                int r7 = r3.getWidth()     // Catch:{ all -> 0x0227 }
                float r7 = (float) r7     // Catch:{ all -> 0x0227 }
                int r3 = r3.getHeight()     // Catch:{ all -> 0x0227 }
                float r3 = (float) r3     // Catch:{ all -> 0x0227 }
                android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x0227 }
                r4.drawRoundRect(r0, r7, r3, r9)     // Catch:{ all -> 0x0227 }
                r4.restore()     // Catch:{ all -> 0x0227 }
                goto L_0x01c1
            L_0x0214:
                r4.setBitmap(r3)     // Catch:{ all -> 0x0227 }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x0227 }
                r0 = r0[r13]     // Catch:{ all -> 0x0227 }
                int r3 = org.telegram.messenger.R.id.shortcut_widget_item_avatar     // Catch:{ all -> 0x0227 }
                android.view.View r0 = r0.findViewById(r3)     // Catch:{ all -> 0x0227 }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x0227 }
                r0.setImageBitmap(r14)     // Catch:{ all -> 0x0227 }
                goto L_0x022b
            L_0x0227:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x022b:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r0.dialogMessage
                long r3 = r2.id
                java.lang.Object r0 = r0.get(r3)
                r3 = r0
                org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
                if (r3 == 0) goto L_0x05a3
                long r9 = r3.getFromChatId()
                r23 = 0
                int r0 = (r9 > r23 ? 1 : (r9 == r23 ? 0 : -1))
                if (r0 <= 0) goto L_0x0259
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.lang.Long r4 = java.lang.Long.valueOf(r9)
                org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
                r4 = r0
                r0 = 0
                goto L_0x0269
            L_0x0259:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r9 = -r9
                java.lang.Long r4 = java.lang.Long.valueOf(r9)
                org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
                r4 = 0
            L_0x0269:
                android.content.Context r7 = r28.getContext()
                android.content.res.Resources r7 = r7.getResources()
                int r9 = org.telegram.messenger.R.color.widget_text
                int r7 = r7.getColor(r9)
                org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner
                boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageService
                if (r9 == 0) goto L_0x02a2
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
                if (r0 == 0) goto L_0x0290
                org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
                boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
                if (r4 != 0) goto L_0x0292
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
                if (r0 == 0) goto L_0x0290
                goto L_0x0292
            L_0x0290:
                java.lang.CharSequence r11 = r3.messageText
            L_0x0292:
                android.content.Context r0 = r28.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r4 = org.telegram.messenger.R.color.widget_action_text
                int r7 = r0.getColor(r4)
                goto L_0x056a
            L_0x02a2:
                java.lang.String r9 = "🎤 "
                r10 = 14
                java.lang.String r14 = "📹 "
                r26 = r9
                if (r6 == 0) goto L_0x0480
                long r8 = r6.id
                r24 = 0
                int r27 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
                if (r27 <= 0) goto L_0x0480
                if (r0 != 0) goto L_0x0480
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
                if (r0 == 0) goto L_0x02c2
                boolean r0 = org.telegram.messenger.ChatObject.isMegagroup(r6)
                if (r0 == 0) goto L_0x0480
            L_0x02c2:
                boolean r0 = r3.isOutOwner()
                if (r0 == 0) goto L_0x02d2
                int r0 = org.telegram.messenger.R.string.FromYou
                java.lang.String r4 = "FromYou"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            L_0x02d0:
                r4 = r0
                goto L_0x02e2
            L_0x02d2:
                if (r4 == 0) goto L_0x02df
                java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r4)
                java.lang.String r4 = "\n"
                java.lang.String r0 = r0.replace(r4, r11)
                goto L_0x02d0
            L_0x02df:
                java.lang.String r0 = "DELETED"
                goto L_0x02d0
            L_0x02e2:
                java.lang.String r0 = "%2$s: ⁨%1$s⁩"
                java.lang.CharSequence r6 = r3.caption
                r8 = 32
                r9 = 10
                r12 = 150(0x96, float:2.1E-43)
                if (r6 == 0) goto L_0x0349
                java.lang.String r6 = r6.toString()
                int r10 = r6.length()
                if (r10 <= r12) goto L_0x02fd
                r10 = 0
                java.lang.String r6 = r6.substring(r10, r12)
            L_0x02fd:
                boolean r10 = r3.isVideo()
                if (r10 == 0) goto L_0x0306
                r10 = r14
            L_0x0304:
                r11 = 2
                goto L_0x0324
            L_0x0306:
                boolean r10 = r3.isVoice()
                if (r10 == 0) goto L_0x030f
                r10 = r26
                goto L_0x0304
            L_0x030f:
                boolean r10 = r3.isMusic()
                if (r10 == 0) goto L_0x0318
                java.lang.String r10 = "🎧 "
                goto L_0x0304
            L_0x0318:
                boolean r10 = r3.isPhoto()
                if (r10 == 0) goto L_0x0321
                java.lang.String r10 = "🖼 "
                goto L_0x0304
            L_0x0321:
                java.lang.String r10 = "📎 "
                goto L_0x0304
            L_0x0324:
                java.lang.Object[] r12 = new java.lang.Object[r11]
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r10)
                java.lang.String r6 = r6.replace(r9, r8)
                r11.append(r6)
                java.lang.String r6 = r11.toString()
                r8 = 0
                r12[r8] = r6
                r6 = 1
                r12[r6] = r4
                java.lang.String r0 = java.lang.String.format(r0, r12)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0464
            L_0x0349:
                org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r14 = r14.media
                if (r14 == 0) goto L_0x0434
                boolean r14 = r3.isMediaEmpty()
                if (r14 != 0) goto L_0x0434
                android.content.Context r7 = r28.getContext()
                android.content.res.Resources r7 = r7.getResources()
                int r11 = org.telegram.messenger.R.color.widget_action_text
                int r7 = r7.getColor(r11)
                org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                r14 = 18
                if (r12 == 0) goto L_0x0398
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r11 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r11
                int r10 = android.os.Build.VERSION.SDK_INT
                if (r10 < r14) goto L_0x0384
                r6 = 1
                java.lang.Object[] r10 = new java.lang.Object[r6]
                org.telegram.tgnet.TLRPC$Poll r11 = r11.poll
                java.lang.String r11 = r11.question
                r14 = 0
                r10[r14] = r11
                java.lang.String r11 = "📊 ⁨%s⁩"
                java.lang.String r10 = java.lang.String.format(r11, r10)
                goto L_0x0394
            L_0x0384:
                r6 = 1
                r14 = 0
                java.lang.Object[] r10 = new java.lang.Object[r6]
                org.telegram.tgnet.TLRPC$Poll r6 = r11.poll
                java.lang.String r6 = r6.question
                r10[r14] = r6
                java.lang.String r6 = "📊 %s"
                java.lang.String r10 = java.lang.String.format(r6, r10)
            L_0x0394:
                r12 = 1
                r19 = 0
                goto L_0x0404
            L_0x0398:
                boolean r6 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r6 == 0) goto L_0x03c5
                int r6 = android.os.Build.VERSION.SDK_INT
                if (r6 < r14) goto L_0x03b2
                r6 = 1
                java.lang.Object[] r10 = new java.lang.Object[r6]
                org.telegram.tgnet.TLRPC$TL_game r11 = r11.game
                java.lang.String r11 = r11.title
                r19 = 0
                r10[r19] = r11
                java.lang.String r11 = "🎮 ⁨%s⁩"
                java.lang.String r10 = java.lang.String.format(r11, r10)
                goto L_0x03c3
            L_0x03b2:
                r6 = 1
                r19 = 0
                java.lang.Object[] r10 = new java.lang.Object[r6]
                org.telegram.tgnet.TLRPC$TL_game r6 = r11.game
                java.lang.String r6 = r6.title
                r10[r19] = r6
                java.lang.String r6 = "🎮 %s"
                java.lang.String r10 = java.lang.String.format(r6, r10)
            L_0x03c3:
                r12 = 1
                goto L_0x0404
            L_0x03c5:
                r19 = 0
                int r6 = r3.type
                if (r6 != r10) goto L_0x03fd
                int r6 = android.os.Build.VERSION.SDK_INT
                if (r6 < r14) goto L_0x03e6
                r6 = 2
                java.lang.Object[] r10 = new java.lang.Object[r6]
                java.lang.String r11 = r3.getMusicAuthor()
                r10[r19] = r11
                java.lang.String r11 = r3.getMusicTitle()
                r12 = 1
                r10[r12] = r11
                java.lang.String r11 = "🎧 ⁨%s - %s⁩"
                java.lang.String r10 = java.lang.String.format(r11, r10)
                goto L_0x0404
            L_0x03e6:
                r6 = 2
                r12 = 1
                java.lang.Object[] r10 = new java.lang.Object[r6]
                java.lang.String r6 = r3.getMusicAuthor()
                r10[r19] = r6
                java.lang.String r6 = r3.getMusicTitle()
                r10[r12] = r6
                java.lang.String r6 = "🎧 %s - %s"
                java.lang.String r10 = java.lang.String.format(r6, r10)
                goto L_0x0404
            L_0x03fd:
                r12 = 1
                java.lang.CharSequence r6 = r3.messageText
                java.lang.String r10 = r6.toString()
            L_0x0404:
                java.lang.String r6 = r10.replace(r9, r8)
                r8 = 2
                java.lang.Object[] r9 = new java.lang.Object[r8]
                r9[r19] = r6
                r9[r12] = r4
                java.lang.String r0 = java.lang.String.format(r0, r9)
                android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r0)
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x042e }
                java.lang.String r9 = "chats_attachMessage"
                r0.<init>(r9)     // Catch:{ Exception -> 0x042e }
                int r9 = r4.length()     // Catch:{ Exception -> 0x042e }
                r10 = 2
                int r9 = r9 + r10
                int r10 = r8.length()     // Catch:{ Exception -> 0x042e }
                r11 = 33
                r8.setSpan(r0, r9, r10, r11)     // Catch:{ Exception -> 0x042e }
                goto L_0x0432
            L_0x042e:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0432:
                r11 = r8
                goto L_0x0465
            L_0x0434:
                org.telegram.tgnet.TLRPC$Message r10 = r3.messageOwner
                java.lang.String r10 = r10.message
                if (r10 == 0) goto L_0x0460
                int r11 = r10.length()
                if (r11 <= r12) goto L_0x0446
                r11 = 0
                java.lang.String r10 = r10.substring(r11, r12)
                goto L_0x0447
            L_0x0446:
                r11 = 0
            L_0x0447:
                java.lang.String r8 = r10.replace(r9, r8)
                java.lang.String r8 = r8.trim()
                r9 = 2
                java.lang.Object[] r10 = new java.lang.Object[r9]
                r10[r11] = r8
                r6 = 1
                r10[r6] = r4
                java.lang.String r0 = java.lang.String.format(r0, r10)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0464
            L_0x0460:
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r11)
            L_0x0464:
                r11 = r0
            L_0x0465:
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x047a }
                java.lang.String r6 = "chats_nameMessage"
                r0.<init>(r6)     // Catch:{ Exception -> 0x047a }
                int r4 = r4.length()     // Catch:{ Exception -> 0x047a }
                r6 = 1
                int r4 = r4 + r6
                r6 = 33
                r8 = 0
                r11.setSpan(r0, r8, r4, r6)     // Catch:{ Exception -> 0x047a }
                goto L_0x056a
            L_0x047a:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x056a
            L_0x0480:
                org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
                if (r4 == 0) goto L_0x049c
                org.telegram.tgnet.TLRPC$Photo r4 = r0.photo
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
                if (r4 == 0) goto L_0x049c
                int r4 = r0.ttl_seconds
                if (r4 == 0) goto L_0x049c
                int r0 = org.telegram.messenger.R.string.AttachPhotoExpired
                java.lang.String r4 = "AttachPhotoExpired"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r4, r0)
                goto L_0x056a
            L_0x049c:
                boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
                if (r4 == 0) goto L_0x04b4
                org.telegram.tgnet.TLRPC$Document r4 = r0.document
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
                if (r4 == 0) goto L_0x04b4
                int r4 = r0.ttl_seconds
                if (r4 == 0) goto L_0x04b4
                int r0 = org.telegram.messenger.R.string.AttachVideoExpired
                java.lang.String r4 = "AttachVideoExpired"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r4, r0)
                goto L_0x056a
            L_0x04b4:
                java.lang.CharSequence r4 = r3.caption
                if (r4 == 0) goto L_0x04f0
                boolean r0 = r3.isVideo()
                if (r0 == 0) goto L_0x04c0
                r9 = r14
                goto L_0x04dd
            L_0x04c0:
                boolean r0 = r3.isVoice()
                if (r0 == 0) goto L_0x04c9
                r9 = r26
                goto L_0x04dd
            L_0x04c9:
                boolean r0 = r3.isMusic()
                if (r0 == 0) goto L_0x04d2
                java.lang.String r9 = "🎧 "
                goto L_0x04dd
            L_0x04d2:
                boolean r0 = r3.isPhoto()
                if (r0 == 0) goto L_0x04db
                java.lang.String r9 = "🖼 "
                goto L_0x04dd
            L_0x04db:
                java.lang.String r9 = "📎 "
            L_0x04dd:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r9)
                java.lang.CharSequence r4 = r3.caption
                r0.append(r4)
                java.lang.String r11 = r0.toString()
                goto L_0x056a
            L_0x04f0:
                boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                if (r4 == 0) goto L_0x050d
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r6 = "📊 "
                r4.append(r6)
                org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
                java.lang.String r0 = r0.question
                r4.append(r0)
                java.lang.String r0 = r4.toString()
            L_0x050b:
                r11 = r0
                goto L_0x0550
            L_0x050d:
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r0 == 0) goto L_0x052b
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r4 = "🎮 "
                r0.append(r4)
                org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
                org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
                java.lang.String r4 = r4.title
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                goto L_0x050b
            L_0x052b:
                int r0 = r3.type
                if (r0 != r10) goto L_0x0547
                r4 = 2
                java.lang.Object[] r0 = new java.lang.Object[r4]
                java.lang.String r4 = r3.getMusicAuthor()
                r6 = 0
                r0[r6] = r4
                java.lang.String r4 = r3.getMusicTitle()
                r6 = 1
                r0[r6] = r4
                java.lang.String r4 = "🎧 %s - %s"
                java.lang.String r0 = java.lang.String.format(r4, r0)
                goto L_0x050b
            L_0x0547:
                java.lang.CharSequence r0 = r3.messageText
                java.util.ArrayList<java.lang.String> r4 = r3.highlightedWords
                r6 = 0
                org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
                goto L_0x050b
            L_0x0550:
                org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                if (r0 == 0) goto L_0x056a
                boolean r0 = r3.isMediaEmpty()
                if (r0 != 0) goto L_0x056a
                android.content.Context r0 = r28.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r4 = org.telegram.messenger.R.color.widget_action_text
                int r7 = r0.getColor(r4)
            L_0x056a:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r4 = org.telegram.messenger.R.id.shortcut_widget_item_time
                android.view.View r0 = r0.findViewById(r4)
                android.widget.TextView r0 = (android.widget.TextView) r0
                org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
                int r3 = r3.date
                long r3 = (long) r3
                java.lang.String r3 = org.telegram.messenger.LocaleController.stringForMessageListDate(r3)
                r0.setText(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r3 = org.telegram.messenger.R.id.shortcut_widget_item_message
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                java.lang.String r4 = r11.toString()
                r0.setText(r4)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setTextColor(r7)
                goto L_0x05dc
            L_0x05a3:
                int r0 = r2.last_message_date
                if (r0 == 0) goto L_0x05be
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r3 = org.telegram.messenger.R.id.shortcut_widget_item_time
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                int r3 = r2.last_message_date
                long r3 = (long) r3
                java.lang.String r3 = org.telegram.messenger.LocaleController.stringForMessageListDate(r3)
                r0.setText(r3)
                goto L_0x05cd
            L_0x05be:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r3 = org.telegram.messenger.R.id.shortcut_widget_item_time
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r11)
            L_0x05cd:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r3 = org.telegram.messenger.R.id.shortcut_widget_item_message
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r11)
            L_0x05dc:
                int r0 = r2.unread_count
                if (r0 <= 0) goto L_0x0636
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r3 = org.telegram.messenger.R.id.shortcut_widget_item_badge
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r4 = 1
                java.lang.Object[] r6 = new java.lang.Object[r4]
                int r4 = r2.unread_count
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r7 = 0
                r6[r7] = r4
                r4 = r18
                java.lang.String r6 = java.lang.String.format(r4, r6)
                r0.setText(r6)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r3)
                r0.setVisibility(r7)
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r6 = r2.id
                boolean r0 = r0.isDialogMuted(r6)
                if (r0 == 0) goto L_0x0628
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r3)
                int r2 = org.telegram.messenger.R.drawable.widget_counter_muted
                r0.setBackgroundResource(r2)
                goto L_0x0647
            L_0x0628:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r3)
                int r2 = org.telegram.messenger.R.drawable.widget_counter
                r0.setBackgroundResource(r2)
                goto L_0x0647
            L_0x0636:
                r4 = r18
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                int r2 = org.telegram.messenger.R.id.shortcut_widget_item_badge
                android.view.View r0 = r0.findViewById(r2)
                r2 = 8
                r0.setVisibility(r2)
            L_0x0647:
                int r13 = r13 + 1
                r3 = r4
                r7 = r15
                r4 = r20
                r8 = 0
                r9 = 0
                r11 = 8
                r12 = 2
                r15 = 0
                goto L_0x001a
            L_0x0656:
                android.view.ViewGroup[] r0 = r1.cells
                r2 = 0
                r0 = r0[r2]
                int r2 = org.telegram.messenger.R.id.shortcut_widget_item_divider
                android.view.View r0 = r0.findViewById(r2)
                android.view.ViewGroup[] r4 = r1.cells
                r6 = 1
                r4 = r4[r6]
                int r4 = r4.getVisibility()
                r0.setVisibility(r4)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r6]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x097a
            L_0x067c:
                r20 = r4
                r15 = r7
                r6 = 1
                r4 = r3
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                if (r0 != r6) goto L_0x097a
                r2 = 0
            L_0x068a:
                r6 = 2
                if (r2 >= r6) goto L_0x097a
                r7 = 0
            L_0x068e:
                if (r7 >= r6) goto L_0x0968
                int r0 = r2 * 2
                int r0 = r0 + r7
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                java.util.ArrayList r6 = r6.selectedDialogs
                boolean r6 = r6.isEmpty()
                if (r6 == 0) goto L_0x06d7
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MediaDataController r6 = r6.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r6 = r6.hints
                int r6 = r6.size()
                if (r0 >= r6) goto L_0x071b
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MediaDataController r6 = r6.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r6 = r6.hints
                java.lang.Object r6 = r6.get(r0)
                org.telegram.tgnet.TLRPC$TL_topPeer r6 = (org.telegram.tgnet.TLRPC$TL_topPeer) r6
                org.telegram.tgnet.TLRPC$Peer r6 = r6.peer
                long r8 = r6.user_id
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
                java.lang.Object r6 = r6.get(r8)
                org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
                if (r6 != 0) goto L_0x071c
                org.telegram.tgnet.TLRPC$TL_dialog r6 = new org.telegram.tgnet.TLRPC$TL_dialog
                r6.<init>()
                r6.id = r8
                goto L_0x071c
            L_0x06d7:
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                java.util.ArrayList r6 = r6.selectedDialogs
                int r6 = r6.size()
                if (r0 >= r6) goto L_0x071b
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
                org.telegram.ui.EditWidgetActivity r8 = r1.this$0
                java.util.ArrayList r8 = r8.selectedDialogs
                java.lang.Object r8 = r8.get(r0)
                java.lang.Long r8 = (java.lang.Long) r8
                long r8 = r8.longValue()
                java.lang.Object r6 = r6.get(r8)
                org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
                if (r6 != 0) goto L_0x071c
                org.telegram.tgnet.TLRPC$TL_dialog r6 = new org.telegram.tgnet.TLRPC$TL_dialog
                r6.<init>()
                org.telegram.ui.EditWidgetActivity r8 = r1.this$0
                java.util.ArrayList r8 = r8.selectedDialogs
                java.lang.Object r8 = r8.get(r0)
                java.lang.Long r8 = (java.lang.Long) r8
                long r8 = r8.longValue()
                r6.id = r8
                goto L_0x071c
            L_0x071b:
                r6 = 0
            L_0x071c:
                if (r6 != 0) goto L_0x074d
                android.view.ViewGroup[] r6 = r1.cells
                r6 = r6[r2]
                if (r7 != 0) goto L_0x0727
                int r8 = org.telegram.messenger.R.id.contacts_widget_item1
                goto L_0x0729
            L_0x0727:
                int r8 = org.telegram.messenger.R.id.contacts_widget_item2
            L_0x0729:
                android.view.View r6 = r6.findViewById(r8)
                r8 = 4
                r6.setVisibility(r8)
                if (r0 == 0) goto L_0x0736
                r6 = 2
                if (r0 != r6) goto L_0x073f
            L_0x0736:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r3 = 8
                r0.setVisibility(r3)
            L_0x073f:
                r10 = r15
                r11 = r20
                r6 = 1
                r9 = 0
                r12 = 0
                r13 = 1111490560(0x42400000, float:48.0)
                r17 = 12
                r20 = 0
                goto L_0x0960
            L_0x074d:
                android.view.ViewGroup[] r8 = r1.cells
                r8 = r8[r2]
                if (r7 != 0) goto L_0x0756
                int r9 = org.telegram.messenger.R.id.contacts_widget_item1
                goto L_0x0758
            L_0x0756:
                int r9 = org.telegram.messenger.R.id.contacts_widget_item2
            L_0x0758:
                android.view.View r8 = r8.findViewById(r9)
                r9 = 0
                r8.setVisibility(r9)
                r8 = 2
                if (r0 == 0) goto L_0x0765
                if (r0 != r8) goto L_0x076c
            L_0x0765:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r0.setVisibility(r9)
            L_0x076c:
                long r9 = r6.id
                boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r9)
                if (r0 == 0) goto L_0x07e2
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r9 = r6.id
                java.lang.Long r9 = java.lang.Long.valueOf(r9)
                org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r9)
                boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r9 == 0) goto L_0x0794
                int r9 = org.telegram.messenger.R.string.SavedMessages
                r10 = r15
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            L_0x0791:
                r11 = r20
                goto L_0x07b7
            L_0x0794:
                r10 = r15
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r9 == 0) goto L_0x07a2
                int r9 = org.telegram.messenger.R.string.RepliesTitle
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r5, r9)
                goto L_0x0791
            L_0x07a2:
                boolean r9 = org.telegram.messenger.UserObject.isDeleted(r0)
                if (r9 == 0) goto L_0x07b1
                int r9 = org.telegram.messenger.R.string.HiddenName
                r11 = r20
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                goto L_0x07b7
            L_0x07b1:
                r11 = r20
                java.lang.String r9 = org.telegram.messenger.UserObject.getFirstName(r0)
            L_0x07b7:
                boolean r13 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r13 != 0) goto L_0x07dc
                boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r13 != 0) goto L_0x07dc
                if (r0 == 0) goto L_0x07dc
                org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r0.photo
                if (r13 == 0) goto L_0x07dc
                org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small
                if (r13 == 0) goto L_0x07dc
                long r14 = r13.volume_id
                r20 = 0
                int r16 = (r14 > r20 ? 1 : (r14 == r20 ? 0 : -1))
                if (r16 == 0) goto L_0x07dc
                int r14 = r13.local_id
                if (r14 == 0) goto L_0x07dc
                r14 = r9
                r9 = 0
                goto L_0x07df
            L_0x07dc:
                r14 = r9
                r9 = 0
                r13 = 0
            L_0x07df:
                r20 = 0
                goto L_0x0814
            L_0x07e2:
                r10 = r15
                r11 = r20
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r13 = r6.id
                long r13 = -r13
                java.lang.Long r9 = java.lang.Long.valueOf(r13)
                org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r9)
                java.lang.String r9 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r13 = r0.photo
                if (r13 == 0) goto L_0x080e
                org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small
                if (r13 == 0) goto L_0x080e
                long r14 = r13.volume_id
                r20 = 0
                int r16 = (r14 > r20 ? 1 : (r14 == r20 ? 0 : -1))
                if (r16 == 0) goto L_0x0810
                int r14 = r13.local_id
                if (r14 == 0) goto L_0x0810
                r14 = r9
                goto L_0x0812
            L_0x080e:
                r20 = 0
            L_0x0810:
                r14 = r9
                r13 = 0
            L_0x0812:
                r9 = r0
                r0 = 0
            L_0x0814:
                android.view.ViewGroup[] r15 = r1.cells
                r15 = r15[r2]
                if (r7 != 0) goto L_0x081d
                int r16 = org.telegram.messenger.R.id.contacts_widget_item_text1
                goto L_0x081f
            L_0x081d:
                int r16 = org.telegram.messenger.R.id.contacts_widget_item_text2
            L_0x081f:
                r3 = r16
                android.view.View r3 = r15.findViewById(r3)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r3.setText(r14)
                if (r13 == 0) goto L_0x0847
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0     // Catch:{ all -> 0x0840 }
                org.telegram.messenger.FileLoader r3 = r3.getFileLoader()     // Catch:{ all -> 0x0840 }
                r12 = 1
                java.io.File r3 = r3.getPathToAttach(r13, r12)     // Catch:{ all -> 0x0840 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0840 }
                android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r3)     // Catch:{ all -> 0x0840 }
                goto L_0x0848
            L_0x0840:
                r0 = move-exception
                r9 = 0
                r12 = 0
                r13 = 1111490560(0x42400000, float:48.0)
                goto L_0x08f7
            L_0x0847:
                r3 = 0
            L_0x0848:
                r13 = 1111490560(0x42400000, float:48.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x08f4 }
                android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x08f4 }
                android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r14, r14, r15)     // Catch:{ all -> 0x08f4 }
                r8 = 0
                r15.eraseColor(r8)     // Catch:{ all -> 0x08f4 }
                android.graphics.Canvas r8 = new android.graphics.Canvas     // Catch:{ all -> 0x08f4 }
                r8.<init>(r15)     // Catch:{ all -> 0x08f4 }
                if (r3 != 0) goto L_0x0893
                if (r0 == 0) goto L_0x0882
                org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x08f4 }
                r3.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x08f4 }
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x08f4 }
                if (r9 == 0) goto L_0x0872
                r9 = 12
                r3.setAvatarType(r9)     // Catch:{ all -> 0x08f4 }
                goto L_0x087e
            L_0x0872:
                r9 = 12
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x08f4 }
                if (r0 == 0) goto L_0x087e
                r12 = 1
                r3.setAvatarType(r12)     // Catch:{ all -> 0x08f4 }
            L_0x087e:
                r9 = 0
                r17 = 12
                goto L_0x088a
            L_0x0882:
                r17 = 12
                org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x08f0 }
                r3.<init>((org.telegram.tgnet.TLRPC$Chat) r9)     // Catch:{ all -> 0x08f0 }
                r9 = 0
            L_0x088a:
                r3.setBounds(r9, r9, r14, r14)     // Catch:{ all -> 0x08f0 }
                r3.draw(r8)     // Catch:{ all -> 0x08f0 }
                r9 = 0
                r12 = 0
                goto L_0x08d3
            L_0x0893:
                r17 = 12
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x08f0 }
                android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x08f0 }
                r0.<init>(r3, r9, r9)     // Catch:{ all -> 0x08f0 }
                float r9 = (float) r14     // Catch:{ all -> 0x08f0 }
                int r14 = r3.getWidth()     // Catch:{ all -> 0x08f0 }
                float r14 = (float) r14     // Catch:{ all -> 0x08f0 }
                float r9 = r9 / r14
                r8.save()     // Catch:{ all -> 0x08f0 }
                r8.scale(r9, r9)     // Catch:{ all -> 0x08f0 }
                android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x08f0 }
                r9.setShader(r0)     // Catch:{ all -> 0x08f0 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x08f0 }
                int r9 = r3.getWidth()     // Catch:{ all -> 0x08f0 }
                float r9 = (float) r9     // Catch:{ all -> 0x08f0 }
                int r14 = r3.getHeight()     // Catch:{ all -> 0x08f0 }
                float r14 = (float) r14
                r12 = 0
                r0.set(r12, r12, r9, r14)     // Catch:{ all -> 0x08ed }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x08ed }
                int r9 = r3.getWidth()     // Catch:{ all -> 0x08ed }
                float r9 = (float) r9     // Catch:{ all -> 0x08ed }
                int r3 = r3.getHeight()     // Catch:{ all -> 0x08ed }
                float r3 = (float) r3     // Catch:{ all -> 0x08ed }
                android.graphics.Paint r14 = r1.roundPaint     // Catch:{ all -> 0x08ed }
                r8.drawRoundRect(r0, r9, r3, r14)     // Catch:{ all -> 0x08ed }
                r8.restore()     // Catch:{ all -> 0x08ed }
                r9 = 0
            L_0x08d3:
                r8.setBitmap(r9)     // Catch:{ all -> 0x08eb }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x08eb }
                r0 = r0[r2]     // Catch:{ all -> 0x08eb }
                if (r7 != 0) goto L_0x08df
                int r3 = org.telegram.messenger.R.id.contacts_widget_item_avatar1     // Catch:{ all -> 0x08eb }
                goto L_0x08e1
            L_0x08df:
                int r3 = org.telegram.messenger.R.id.contacts_widget_item_avatar2     // Catch:{ all -> 0x08eb }
            L_0x08e1:
                android.view.View r0 = r0.findViewById(r3)     // Catch:{ all -> 0x08eb }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x08eb }
                r0.setImageBitmap(r15)     // Catch:{ all -> 0x08eb }
                goto L_0x08fc
            L_0x08eb:
                r0 = move-exception
                goto L_0x08f9
            L_0x08ed:
                r0 = move-exception
                r9 = 0
                goto L_0x08f9
            L_0x08f0:
                r0 = move-exception
                r9 = 0
                r12 = 0
                goto L_0x08f9
            L_0x08f4:
                r0 = move-exception
                r9 = 0
                r12 = 0
            L_0x08f7:
                r17 = 12
            L_0x08f9:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x08fc:
                int r0 = r6.unread_count
                if (r0 <= 0) goto L_0x094b
                r3 = 99
                if (r0 <= r3) goto L_0x0915
                r6 = 1
                java.lang.Object[] r0 = new java.lang.Object[r6]
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r8 = 0
                r0[r8] = r3
                java.lang.String r3 = "%d+"
                java.lang.String r0 = java.lang.String.format(r3, r0)
                goto L_0x0923
            L_0x0915:
                r6 = 1
                r8 = 0
                java.lang.Object[] r3 = new java.lang.Object[r6]
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r3[r8] = r0
                java.lang.String r0 = java.lang.String.format(r4, r3)
            L_0x0923:
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r2]
                if (r7 != 0) goto L_0x092c
                int r8 = org.telegram.messenger.R.id.contacts_widget_item_badge1
                goto L_0x092e
            L_0x092c:
                int r8 = org.telegram.messenger.R.id.contacts_widget_item_badge2
            L_0x092e:
                android.view.View r3 = r3.findViewById(r8)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r3.setText(r0)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r7 != 0) goto L_0x0940
                int r3 = org.telegram.messenger.R.id.contacts_widget_item_badge_bg1
                goto L_0x0942
            L_0x0940:
                int r3 = org.telegram.messenger.R.id.contacts_widget_item_badge_bg2
            L_0x0942:
                android.view.View r0 = r0.findViewById(r3)
                r3 = 0
                r0.setVisibility(r3)
                goto L_0x0960
            L_0x094b:
                r6 = 1
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r7 != 0) goto L_0x0955
                int r3 = org.telegram.messenger.R.id.contacts_widget_item_badge_bg1
                goto L_0x0957
            L_0x0955:
                int r3 = org.telegram.messenger.R.id.contacts_widget_item_badge_bg2
            L_0x0957:
                android.view.View r0 = r0.findViewById(r3)
                r3 = 8
                r0.setVisibility(r3)
            L_0x0960:
                int r7 = r7 + 1
                r15 = r10
                r20 = r11
                r6 = 2
                goto L_0x068e
            L_0x0968:
                r10 = r15
                r11 = r20
                r6 = 1
                r9 = 0
                r12 = 0
                r13 = 1111490560(0x42400000, float:48.0)
                r17 = 12
                r20 = 0
                int r2 = r2 + 1
                r20 = r11
                goto L_0x068a
            L_0x097a:
                android.view.ViewGroup[] r0 = r1.cells
                r2 = 0
                r0 = r0[r2]
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x0991
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x099a
            L_0x0991:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r0.setVisibility(r2)
            L_0x099a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.updateDialogs():void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(264.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
            if (!(cachedWallpaperNonBlocking == this.backgroundDrawable || cachedWallpaperNonBlocking == null)) {
                if (Theme.isAnimatingColor()) {
                    this.oldBackgroundDrawable = this.backgroundDrawable;
                    this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
                } else {
                    BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                    if (disposable != null) {
                        disposable.dispose();
                        this.backgroundGradientDisposable = null;
                    }
                }
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            float themeAnimationValue = this.this$0.parentLayout.getThemeAnimationValue();
            int i = 0;
            while (i < 2) {
                Drawable drawable = i == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
                if (drawable != null) {
                    if (i != 1 || this.oldBackgroundDrawable == null || this.this$0.parentLayout == null) {
                        drawable.setAlpha(255);
                    } else {
                        drawable.setAlpha((int) (255.0f * themeAnimationValue));
                    }
                    if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        if (drawable instanceof BackgroundGradientDrawable) {
                            this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                        } else {
                            drawable.draw(canvas);
                        }
                    } else if (drawable instanceof BitmapDrawable) {
                        if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                            canvas.save();
                            float f = 2.0f / AndroidUtilities.density;
                            canvas.scale(f, f);
                            drawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                        } else {
                            int measuredHeight = getMeasuredHeight();
                            float max = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) measuredHeight) / ((float) drawable.getIntrinsicHeight()));
                            int ceil = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * max));
                            int ceil2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * max));
                            int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                            int i2 = (measuredHeight - ceil2) / 2;
                            canvas.save();
                            canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                            drawable.setBounds(measuredWidth, i2, ceil + measuredWidth, ceil2 + i2);
                        }
                        drawable.draw(canvas);
                        canvas.restore();
                    }
                    if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                        if (disposable2 != null) {
                            disposable2.dispose();
                            this.oldBackgroundGradientDisposable = null;
                        }
                        this.oldBackgroundDrawable = null;
                        invalidate();
                    }
                }
                i++;
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
            BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
            if (disposable2 != null) {
                disposable2.dispose();
                this.oldBackgroundGradientDisposable = null;
            }
        }
    }

    public EditWidgetActivity(int i, int i2) {
        this.widgetType = i;
        this.currentWidgetId = i2;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        getMessagesStorage().getWidgetDialogIds(this.currentWidgetId, this.widgetType, this.selectedDialogs, arrayList, arrayList2, true);
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        updateRows();
    }

    public boolean onFragmentCreate() {
        DialogsActivity.loadDialogs(AccountInstance.getInstance(this.currentAccount));
        getMediaDataController().loadHints(true);
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.previewRow = 0;
        this.rowCount = i + 1;
        this.selectChatsRow = i;
        if (this.selectedDialogs.isEmpty()) {
            this.chatsStartRow = -1;
            this.chatsEndRow = -1;
        } else {
            int i2 = this.rowCount;
            this.chatsStartRow = i2;
            int size = i2 + this.selectedDialogs.size();
            this.rowCount = size;
            this.chatsEndRow = size;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.infoRow = i3;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void setDelegate(EditWidgetActivityDelegate editWidgetActivityDelegate) {
        this.delegate = editWidgetActivityDelegate;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.widgetType == 0) {
            this.actionBar.setTitle(LocaleController.getString("WidgetChats", R.string.WidgetChats));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WidgetShortcuts", R.string.WidgetShortcuts));
        }
        this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (EditWidgetActivity.this.delegate == null) {
                        EditWidgetActivity.this.finishActivity();
                    } else {
                        EditWidgetActivity.this.finishFragment();
                    }
                } else if (i == 1 && EditWidgetActivity.this.getParentActivity() != null) {
                    EditWidgetActivity.this.getMessagesStorage().putWidgetDialogs(EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.selectedDialogs);
                    SharedPreferences.Editor edit = EditWidgetActivity.this.getParentActivity().getSharedPreferences("shortcut_widget", 0).edit();
                    edit.putInt("account" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.currentAccount);
                    edit.putInt("type" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.widgetType);
                    edit.commit();
                    AppWidgetManager instance = AppWidgetManager.getInstance(EditWidgetActivity.this.getParentActivity());
                    if (EditWidgetActivity.this.widgetType == 0) {
                        ChatsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId);
                    } else {
                        ContactsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId);
                    }
                    if (EditWidgetActivity.this.delegate != null) {
                        EditWidgetActivity.this.delegate.didSelectDialogs(EditWidgetActivity.this.selectedDialogs);
                    } else {
                        EditWidgetActivity.this.finishActivity();
                    }
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new EditWidgetActivity$$ExternalSyntheticLambda2(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            private Rect rect = new Rect();

            public void onLongClickRelease() {
            }

            public void onMove(float f, float f2) {
            }

            public boolean onItemClick(View view, int i, float f, float f2) {
                if (EditWidgetActivity.this.getParentActivity() != null && (view instanceof GroupCreateUserCell)) {
                    ((ImageView) view.getTag(R.id.object_tag)).getHitRect(this.rect);
                    if (!this.rect.contains((int) f, (int) f2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) EditWidgetActivity.this.getParentActivity());
                        builder.setItems(new CharSequence[]{LocaleController.getString("Delete", R.string.Delete)}, new EditWidgetActivity$2$$ExternalSyntheticLambda0(this, i));
                        EditWidgetActivity.this.showDialog(builder.create());
                        return true;
                    }
                }
                return false;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0(int i, DialogInterface dialogInterface, int i2) {
                if (i2 == 0) {
                    EditWidgetActivity.this.selectedDialogs.remove(i - EditWidgetActivity.this.chatsStartRow);
                    EditWidgetActivity.this.updateRows();
                    if (EditWidgetActivity.this.widgetPreviewCell != null) {
                        EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                    }
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(Context context, View view, int i) {
        if (i == this.selectChatsRow) {
            InviteMembersBottomSheet inviteMembersBottomSheet = new InviteMembersBottomSheet(context, this.currentAccount, (LongSparseArray<TLObject>) null, 0, this, (Theme.ResourcesProvider) null);
            inviteMembersBottomSheet.setDelegate(new EditWidgetActivity$$ExternalSyntheticLambda1(this), this.selectedDialogs);
            inviteMembersBottomSheet.setSelectedContacts(this.selectedDialogs);
            showDialog(inviteMembersBottomSheet);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(ArrayList arrayList) {
        this.selectedDialogs.clear();
        this.selectedDialogs.addAll(arrayList);
        updateRows();
        WidgetPreviewCell widgetPreviewCell2 = this.widgetPreviewCell;
        if (widgetPreviewCell2 != null) {
            widgetPreviewCell2.updateDialogs();
        }
    }

    /* access modifiers changed from: private */
    public void finishActivity() {
        if (getParentActivity() != null) {
            getParentActivity().finish();
            AndroidUtilities.runOnUIThread(new EditWidgetActivity$$ExternalSyntheticLambda0(this), 1000);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return EditWidgetActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 1 || itemViewType == 3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GroupCreateUserCell groupCreateUserCell;
            if (i == 0) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                groupCreateUserCell = textInfoPrivacyCell;
            } else if (i == 1) {
                TextCell textCell = new TextCell(this.mContext);
                textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                groupCreateUserCell = textCell;
            } else if (i != 2) {
                GroupCreateUserCell groupCreateUserCell2 = new GroupCreateUserCell(this.mContext, 0, 0, false);
                ImageView imageView = new ImageView(this.mContext);
                imageView.setImageResource(R.drawable.list_reorder);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                groupCreateUserCell2.setTag(R.id.object_tag, imageView);
                groupCreateUserCell2.addView(imageView, LayoutHelper.createFrame(40, -1.0f, (LocaleController.isRTL ? 3 : 5) | 16, 10.0f, 0.0f, 10.0f, 0.0f));
                imageView.setOnTouchListener(new EditWidgetActivity$ListAdapter$$ExternalSyntheticLambda0(this, groupCreateUserCell2));
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_pinnedIcon"), PorterDuff.Mode.MULTIPLY));
                groupCreateUserCell = groupCreateUserCell2;
            } else {
                groupCreateUserCell = EditWidgetActivity.this.widgetPreviewCell = new WidgetPreviewCell(EditWidgetActivity.this, this.mContext);
            }
            return new RecyclerListView.Holder(groupCreateUserCell);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0(GroupCreateUserCell groupCreateUserCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            EditWidgetActivity.this.itemTouchHelper.startDrag(EditWidgetActivity.this.listView.getChildViewHolder(groupCreateUserCell));
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == EditWidgetActivity.this.infoRow) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    if (EditWidgetActivity.this.widgetType == 0) {
                        spannableStringBuilder.append(LocaleController.getString("EditWidgetChatsInfo", R.string.EditWidgetChatsInfo));
                    } else if (EditWidgetActivity.this.widgetType == 1) {
                        spannableStringBuilder.append(LocaleController.getString("EditWidgetContactsInfo", R.string.EditWidgetContactsInfo));
                    }
                    if (SharedConfig.passcodeHash.length() > 0) {
                        spannableStringBuilder.append("\n\n").append(AndroidUtilities.replaceTags(LocaleController.getString("WidgetPasscode2", R.string.WidgetPasscode2)));
                    }
                    textInfoPrivacyCell.setText(spannableStringBuilder);
                }
            } else if (itemViewType == 1) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                textCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
                Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
                Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(drawable, drawable2);
                String string = LocaleController.getString("SelectChats", R.string.SelectChats);
                if (EditWidgetActivity.this.chatsStartRow == -1) {
                    z = false;
                }
                textCell.setTextAndIcon(string, (Drawable) combinedDrawable, z);
                textCell.getImageView().setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
            } else if (itemViewType == 3) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                long longValue = ((Long) EditWidgetActivity.this.selectedDialogs.get(i - EditWidgetActivity.this.chatsStartRow)).longValue();
                if (DialogObject.isUserDialog(longValue)) {
                    TLRPC$User user = EditWidgetActivity.this.getMessagesController().getUser(Long.valueOf(longValue));
                    if (i == EditWidgetActivity.this.chatsEndRow - 1) {
                        z = false;
                    }
                    groupCreateUserCell.setObject(user, (CharSequence) null, (CharSequence) null, z);
                    return;
                }
                TLRPC$Chat chat = EditWidgetActivity.this.getMessagesController().getChat(Long.valueOf(-longValue));
                if (i == EditWidgetActivity.this.chatsEndRow - 1) {
                    z = false;
                }
                groupCreateUserCell.setObject(chat, (CharSequence) null, (CharSequence) null, z);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 3 || itemViewType == 1) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int i) {
            if (i == EditWidgetActivity.this.previewRow) {
                return 2;
            }
            if (i == EditWidgetActivity.this.selectChatsRow) {
                return 1;
            }
            return i == EditWidgetActivity.this.infoRow ? 0 : 3;
        }

        public boolean swapElements(int i, int i2) {
            int access$1300 = i - EditWidgetActivity.this.chatsStartRow;
            int access$13002 = i2 - EditWidgetActivity.this.chatsStartRow;
            int access$100 = EditWidgetActivity.this.chatsEndRow - EditWidgetActivity.this.chatsStartRow;
            if (access$1300 < 0 || access$13002 < 0 || access$1300 >= access$100 || access$13002 >= access$100) {
                return false;
            }
            EditWidgetActivity.this.selectedDialogs.set(access$1300, (Long) EditWidgetActivity.this.selectedDialogs.get(access$13002));
            EditWidgetActivity.this.selectedDialogs.set(access$13002, (Long) EditWidgetActivity.this.selectedDialogs.get(access$1300));
            notifyItemMoved(i, i2);
            return true;
        }
    }

    public boolean onBackPressed() {
        if (this.delegate != null) {
            return super.onBackPressed();
        }
        finishActivity();
        return false;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        return arrayList;
    }
}
