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
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatsWidgetProvider;
import org.telegram.messenger.ContactsWidgetProvider;
import org.telegram.messenger.LocaleController;
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
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.EditWidgetActivity;

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
    public boolean isEdit;
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
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r8)
                r0.addView(r6, r9)
                org.telegram.ui.Cells.ChatActionCell r9 = new org.telegram.ui.Cells.ChatActionCell
                r9.<init>(r2)
                java.lang.String r10 = "WidgetPreview"
                r11 = 2131628054(0x7f0e1016, float:1.888339E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
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
                r10 = 2131166109(0x7var_d, float:1.7946454E38)
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
                if (r6 != 0) goto L_0x00ce
            L_0x0090:
                if (r5 >= r3) goto L_0x00b6
                android.view.ViewGroup[] r4 = r0.cells
                android.app.Activity r6 = r19.getParentActivity()
                android.view.LayoutInflater r6 = r6.getLayoutInflater()
                r12 = 2131427346(0x7f0b0012, float:1.8476306E38)
                android.view.View r6 = r6.inflate(r12, r10)
                android.view.ViewGroup r6 = (android.view.ViewGroup) r6
                r4[r5] = r6
                android.view.ViewGroup[] r4 = r0.cells
                r4 = r4[r5]
                r6 = -1
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7)
                r9.addView(r4, r6)
                int r5 = r5 + 1
                goto L_0x0090
            L_0x00b6:
                android.widget.ImageView r3 = r19.previewImageView
                r4 = 218(0xda, float:3.05E-43)
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r11, (int) r8)
                r9.addView(r3, r4)
                android.widget.ImageView r1 = r19.previewImageView
                r3 = 2131165356(0x7var_ac, float:1.7944927E38)
                r1.setImageResource(r3)
                goto L_0x010e
            L_0x00ce:
                int r6 = r19.widgetType
                if (r6 != r4) goto L_0x010e
            L_0x00d4:
                if (r5 >= r3) goto L_0x00f9
                android.view.ViewGroup[] r4 = r0.cells
                android.app.Activity r6 = r19.getParentActivity()
                android.view.LayoutInflater r6 = r6.getLayoutInflater()
                r12 = 2131427330(0x7f0b0002, float:1.8476273E38)
                android.view.View r6 = r6.inflate(r12, r10)
                android.view.ViewGroup r6 = (android.view.ViewGroup) r6
                r4[r5] = r6
                android.view.ViewGroup[] r4 = r0.cells
                r4 = r4[r5]
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r7)
                r9.addView(r4, r6)
                int r5 = r5 + 1
                goto L_0x00d4
            L_0x00f9:
                android.widget.ImageView r3 = r19.previewImageView
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r8)
                r9.addView(r3, r4)
                android.widget.ImageView r1 = r19.previewImageView
                r3 = 2131165384(0x7var_c8, float:1.7944984E38)
                r1.setImageResource(r3)
            L_0x010e:
                r18.updateDialogs()
                r1 = 2131165449(0x7var_, float:1.7945115E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r1, (java.lang.String) r3)
                r0.shadowDrawable = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.<init>(org.telegram.ui.EditWidgetActivity, android.content.Context):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:97:0x024b, code lost:
            if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x024e;
         */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x0078  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x0083  */
        /* JADX WARNING: Removed duplicated region for block: B:256:0x0693  */
        /* JADX WARNING: Removed duplicated region for block: B:265:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:352:0x084c  */
        /* JADX WARNING: Removed duplicated region for block: B:361:0x0888  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateDialogs() {
            /*
                r19 = this;
                r1 = r19
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                r2 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
                java.lang.String r3 = "RepliesTitle"
                r4 = 1111490560(0x42400000, float:48.0)
                r5 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
                java.lang.String r6 = "SavedMessages"
                r7 = 0
                r8 = 0
                r10 = 8
                r11 = 2
                r14 = 0
                if (r0 != 0) goto L_0x05ec
                r15 = 0
            L_0x001e:
                if (r15 >= r11) goto L_0x05c8
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0049
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                int r0 = r0.size()
                if (r15 >= r0) goto L_0x0074
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                java.lang.Object r0 = r0.get(r15)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                goto L_0x0075
            L_0x0049:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                int r0 = r0.size()
                if (r15 >= r0) goto L_0x0074
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                org.telegram.ui.EditWidgetActivity r11 = r1.this$0
                java.util.ArrayList r11 = r11.selectedDialogs
                java.lang.Object r11 = r11.get(r15)
                java.lang.Long r11 = (java.lang.Long) r11
                long r12 = r11.longValue()
                java.lang.Object r0 = r0.get(r12)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                goto L_0x0075
            L_0x0074:
                r0 = 0
            L_0x0075:
                r11 = r0
                if (r11 != 0) goto L_0x0083
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r15]
                r0.setVisibility(r10)
                r16 = r6
                goto L_0x05b6
            L_0x0083:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r15]
                r0.setVisibility(r14)
                long r12 = r11.id
                int r0 = (int) r12
                java.lang.String r12 = ""
                if (r0 <= 0) goto L_0x00ed
                org.telegram.ui.EditWidgetActivity r13 = r1.this$0
                org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r13.getUser(r0)
                if (r0 == 0) goto L_0x00e6
                boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r13 == 0) goto L_0x00ac
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r6, r5)
                goto L_0x00bf
            L_0x00ac:
                boolean r13 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r13 == 0) goto L_0x00b7
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x00bf
            L_0x00b7:
                java.lang.String r13 = r0.first_name
                java.lang.String r2 = r0.last_name
                java.lang.String r13 = org.telegram.messenger.ContactsController.formatName(r13, r2)
            L_0x00bf:
                boolean r2 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r2 != 0) goto L_0x00e2
                boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r2 != 0) goto L_0x00e2
                org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r0.photo
                if (r2 == 0) goto L_0x00e2
                org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
                if (r2 == 0) goto L_0x00e2
                r16 = r6
                long r5 = r2.volume_id
                int r17 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r17 == 0) goto L_0x00e4
                int r5 = r2.local_id
                if (r5 == 0) goto L_0x00e4
                r5 = r0
                r0 = r2
                goto L_0x00eb
            L_0x00e2:
                r16 = r6
            L_0x00e4:
                r5 = r0
                goto L_0x00ea
            L_0x00e6:
                r16 = r6
                r5 = r0
                r13 = r12
            L_0x00ea:
                r0 = 0
            L_0x00eb:
                r2 = 0
                goto L_0x0121
            L_0x00ed:
                r16 = r6
                org.telegram.ui.EditWidgetActivity r2 = r1.this$0
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                int r0 = -r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
                if (r0 == 0) goto L_0x011d
                java.lang.String r13 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r2 = r0.photo
                if (r2 == 0) goto L_0x011b
                org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
                if (r2 == 0) goto L_0x011b
                long r5 = r2.volume_id
                int r17 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r17 == 0) goto L_0x011b
                int r5 = r2.local_id
                if (r5 == 0) goto L_0x011b
                r5 = 0
                r18 = r2
                r2 = r0
                r0 = r18
                goto L_0x0121
            L_0x011b:
                r2 = r0
                goto L_0x011f
            L_0x011d:
                r2 = r0
                r13 = r12
            L_0x011f:
                r0 = 0
                r5 = 0
            L_0x0121:
                android.view.ViewGroup[] r6 = r1.cells
                r6 = r6[r15]
                r8 = 2131230907(0x7var_bb, float:1.807788E38)
                android.view.View r6 = r6.findViewById(r8)
                android.widget.TextView r6 = (android.widget.TextView) r6
                r6.setText(r13)
                if (r0 == 0) goto L_0x0141
                r6 = 1
                java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r6)     // Catch:{ all -> 0x01e5 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e5 }
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)     // Catch:{ all -> 0x01e5 }
                goto L_0x0142
            L_0x0141:
                r0 = 0
            L_0x0142:
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x01e5 }
                android.graphics.Bitmap$Config r8 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01e5 }
                android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r6, r6, r8)     // Catch:{ all -> 0x01e5 }
                r8.eraseColor(r14)     // Catch:{ all -> 0x01e5 }
                android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ all -> 0x01e5 }
                r9.<init>(r8)     // Catch:{ all -> 0x01e5 }
                if (r0 != 0) goto L_0x0181
                if (r5 == 0) goto L_0x0174
                org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01e5 }
                r0.<init>((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ all -> 0x01e5 }
                boolean r13 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ all -> 0x01e5 }
                if (r13 == 0) goto L_0x0169
                r5 = 12
                r0.setAvatarType(r5)     // Catch:{ all -> 0x01e5 }
                goto L_0x0179
            L_0x0169:
                boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r5)     // Catch:{ all -> 0x01e5 }
                if (r5 == 0) goto L_0x0179
                r5 = 1
                r0.setAvatarType(r5)     // Catch:{ all -> 0x01e5 }
                goto L_0x0179
            L_0x0174:
                org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01e5 }
                r0.<init>((org.telegram.tgnet.TLRPC$Chat) r2)     // Catch:{ all -> 0x01e5 }
            L_0x0179:
                r0.setBounds(r14, r14, r6, r6)     // Catch:{ all -> 0x01e5 }
                r0.draw(r9)     // Catch:{ all -> 0x01e5 }
            L_0x017f:
                r4 = 0
                goto L_0x01d1
            L_0x0181:
                android.graphics.BitmapShader r5 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01e5 }
                android.graphics.Shader$TileMode r13 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01e5 }
                r5.<init>(r0, r13, r13)     // Catch:{ all -> 0x01e5 }
                android.graphics.Paint r13 = r1.roundPaint     // Catch:{ all -> 0x01e5 }
                if (r13 != 0) goto L_0x019b
                android.graphics.Paint r13 = new android.graphics.Paint     // Catch:{ all -> 0x01e5 }
                r4 = 1
                r13.<init>(r4)     // Catch:{ all -> 0x01e5 }
                r1.roundPaint = r13     // Catch:{ all -> 0x01e5 }
                android.graphics.RectF r4 = new android.graphics.RectF     // Catch:{ all -> 0x01e5 }
                r4.<init>()     // Catch:{ all -> 0x01e5 }
                r1.bitmapRect = r4     // Catch:{ all -> 0x01e5 }
            L_0x019b:
                float r4 = (float) r6     // Catch:{ all -> 0x01e5 }
                int r6 = r0.getWidth()     // Catch:{ all -> 0x01e5 }
                float r6 = (float) r6     // Catch:{ all -> 0x01e5 }
                float r4 = r4 / r6
                r9.save()     // Catch:{ all -> 0x01e5 }
                r9.scale(r4, r4)     // Catch:{ all -> 0x01e5 }
                android.graphics.Paint r4 = r1.roundPaint     // Catch:{ all -> 0x01e5 }
                r4.setShader(r5)     // Catch:{ all -> 0x01e5 }
                android.graphics.RectF r4 = r1.bitmapRect     // Catch:{ all -> 0x01e5 }
                int r5 = r0.getWidth()     // Catch:{ all -> 0x01e5 }
                float r5 = (float) r5     // Catch:{ all -> 0x01e5 }
                int r6 = r0.getHeight()     // Catch:{ all -> 0x01e5 }
                float r6 = (float) r6     // Catch:{ all -> 0x01e5 }
                r4.set(r7, r7, r5, r6)     // Catch:{ all -> 0x01e5 }
                android.graphics.RectF r4 = r1.bitmapRect     // Catch:{ all -> 0x01e5 }
                int r5 = r0.getWidth()     // Catch:{ all -> 0x01e5 }
                float r5 = (float) r5     // Catch:{ all -> 0x01e5 }
                int r0 = r0.getHeight()     // Catch:{ all -> 0x01e5 }
                float r0 = (float) r0     // Catch:{ all -> 0x01e5 }
                android.graphics.Paint r6 = r1.roundPaint     // Catch:{ all -> 0x01e5 }
                r9.drawRoundRect(r4, r5, r0, r6)     // Catch:{ all -> 0x01e5 }
                r9.restore()     // Catch:{ all -> 0x01e5 }
                goto L_0x017f
            L_0x01d1:
                r9.setBitmap(r4)     // Catch:{ all -> 0x01e5 }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x01e5 }
                r0 = r0[r15]     // Catch:{ all -> 0x01e5 }
                r4 = 2131230903(0x7var_b7, float:1.8077872E38)
                android.view.View r0 = r0.findViewById(r4)     // Catch:{ all -> 0x01e5 }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x01e5 }
                r0.setImageBitmap(r8)     // Catch:{ all -> 0x01e5 }
                goto L_0x01e9
            L_0x01e5:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x01e9:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                android.util.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r0.dialogMessage
                long r4 = r11.id
                java.lang.Object r0 = r0.get(r4)
                r4 = r0
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                if (r4 == 0) goto L_0x055f
                int r0 = r4.getFromChatId()
                if (r0 <= 0) goto L_0x0213
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)
                r5 = r0
                r0 = 0
                goto L_0x0223
            L_0x0213:
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                int r0 = -r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r0 = r5.getChat(r0)
                r5 = 0
            L_0x0223:
                android.content.Context r6 = r19.getContext()
                android.content.res.Resources r6 = r6.getResources()
                r8 = 2131034146(0x7var_, float:1.7678801E38)
                int r6 = r6.getColor(r8)
                org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
                boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageService
                r9 = 2131034141(0x7var_d, float:1.7678791E38)
                if (r8 == 0) goto L_0x025e
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r0 == 0) goto L_0x024e
                org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
                if (r2 != 0) goto L_0x0250
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
                if (r0 == 0) goto L_0x024e
                goto L_0x0250
            L_0x024e:
                java.lang.CharSequence r12 = r4.messageText
            L_0x0250:
                android.content.Context r0 = r19.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r6 = r0.getColor(r9)
                goto L_0x0521
            L_0x025e:
                if (r2 == 0) goto L_0x042e
                int r8 = r2.id
                if (r8 <= 0) goto L_0x042e
                if (r0 != 0) goto L_0x042e
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r0 == 0) goto L_0x0272
                boolean r0 = org.telegram.messenger.ChatObject.isMegagroup(r2)
                if (r0 == 0) goto L_0x042e
            L_0x0272:
                boolean r0 = r4.isOutOwner()
                if (r0 == 0) goto L_0x0283
                r0 = 2131625635(0x7f0e06a3, float:1.8878484E38)
                java.lang.String r2 = "FromYou"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            L_0x0281:
                r2 = r0
                goto L_0x0293
            L_0x0283:
                if (r5 == 0) goto L_0x0290
                java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r5)
                java.lang.String r2 = "\n"
                java.lang.String r0 = r0.replace(r2, r12)
                goto L_0x0281
            L_0x0290:
                java.lang.String r0 = "DELETED"
                goto L_0x0281
            L_0x0293:
                java.lang.String r0 = "%2$s: ⁨%1$s⁩"
                java.lang.CharSequence r5 = r4.caption
                r8 = 32
                r13 = 10
                r7 = 150(0x96, float:2.1E-43)
                if (r5 == 0) goto L_0x02fe
                java.lang.String r5 = r5.toString()
                int r9 = r5.length()
                if (r9 <= r7) goto L_0x02ad
                java.lang.String r5 = r5.substring(r14, r7)
            L_0x02ad:
                boolean r7 = r4.isVideo()
                if (r7 == 0) goto L_0x02b8
                java.lang.String r7 = "📹 "
            L_0x02b6:
                r9 = 2
                goto L_0x02da
            L_0x02b8:
                boolean r7 = r4.isVoice()
                if (r7 == 0) goto L_0x02c2
                java.lang.String r7 = "🎤 "
                goto L_0x02b6
            L_0x02c2:
                boolean r7 = r4.isMusic()
                if (r7 == 0) goto L_0x02cc
                java.lang.String r7 = "🎧 "
                goto L_0x02b6
            L_0x02cc:
                boolean r7 = r4.isPhoto()
                if (r7 == 0) goto L_0x02d6
                java.lang.String r7 = "🖼 "
                goto L_0x02b6
            L_0x02d6:
                java.lang.String r7 = "📎 "
                goto L_0x02b6
            L_0x02da:
                java.lang.Object[] r12 = new java.lang.Object[r9]
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                r9.append(r7)
                java.lang.String r5 = r5.replace(r13, r8)
                r9.append(r5)
                java.lang.String r5 = r9.toString()
                r12[r14] = r5
                r5 = 1
                r12[r5] = r2
                java.lang.String r0 = java.lang.String.format(r0, r12)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0413
            L_0x02fe:
                org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
                if (r5 == 0) goto L_0x03e6
                boolean r5 = r4.isMediaEmpty()
                if (r5 != 0) goto L_0x03e6
                android.content.Context r5 = r19.getContext()
                android.content.res.Resources r5 = r5.getResources()
                int r5 = r5.getColor(r9)
                org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
                boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                r9 = 18
                if (r7 == 0) goto L_0x0348
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6
                int r7 = android.os.Build.VERSION.SDK_INT
                if (r7 < r9) goto L_0x0337
                r7 = 1
                java.lang.Object[] r9 = new java.lang.Object[r7]
                org.telegram.tgnet.TLRPC$Poll r6 = r6.poll
                java.lang.String r6 = r6.question
                r9[r14] = r6
                java.lang.String r6 = "📊 ⁨%s⁩"
                java.lang.String r6 = java.lang.String.format(r6, r9)
                goto L_0x0371
            L_0x0337:
                r7 = 1
                java.lang.Object[] r9 = new java.lang.Object[r7]
                org.telegram.tgnet.TLRPC$Poll r6 = r6.poll
                java.lang.String r6 = r6.question
                r9[r14] = r6
                java.lang.String r6 = "📊 %s"
                java.lang.String r6 = java.lang.String.format(r6, r9)
                goto L_0x0371
            L_0x0348:
                boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r7 == 0) goto L_0x0373
                int r7 = android.os.Build.VERSION.SDK_INT
                if (r7 < r9) goto L_0x0361
                r7 = 1
                java.lang.Object[] r9 = new java.lang.Object[r7]
                org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
                java.lang.String r6 = r6.title
                r9[r14] = r6
                java.lang.String r6 = "🎮 ⁨%s⁩"
                java.lang.String r6 = java.lang.String.format(r6, r9)
                goto L_0x0371
            L_0x0361:
                r7 = 1
                java.lang.Object[] r9 = new java.lang.Object[r7]
                org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
                java.lang.String r6 = r6.title
                r9[r14] = r6
                java.lang.String r6 = "🎮 %s"
                java.lang.String r6 = java.lang.String.format(r6, r9)
            L_0x0371:
                r12 = 1
                goto L_0x03b5
            L_0x0373:
                int r6 = r4.type
                r7 = 14
                if (r6 != r7) goto L_0x03ae
                int r6 = android.os.Build.VERSION.SDK_INT
                if (r6 < r9) goto L_0x0396
                r6 = 2
                java.lang.Object[] r7 = new java.lang.Object[r6]
                java.lang.String r9 = r4.getMusicAuthor()
                r7[r14] = r9
                java.lang.String r9 = r4.getMusicTitle()
                r12 = 1
                r7[r12] = r9
                java.lang.String r9 = "🎧 ⁨%s - %s⁩"
                java.lang.String r7 = java.lang.String.format(r9, r7)
                r6 = r7
                goto L_0x03b5
            L_0x0396:
                r6 = 2
                r12 = 1
                java.lang.Object[] r7 = new java.lang.Object[r6]
                java.lang.String r6 = r4.getMusicAuthor()
                r7[r14] = r6
                java.lang.String r6 = r4.getMusicTitle()
                r7[r12] = r6
                java.lang.String r6 = "🎧 %s - %s"
                java.lang.String r6 = java.lang.String.format(r6, r7)
                goto L_0x03b5
            L_0x03ae:
                r12 = 1
                java.lang.CharSequence r6 = r4.messageText
                java.lang.String r6 = r6.toString()
            L_0x03b5:
                java.lang.String r6 = r6.replace(r13, r8)
                r7 = 2
                java.lang.Object[] r8 = new java.lang.Object[r7]
                r8[r14] = r6
                r8[r12] = r2
                java.lang.String r0 = java.lang.String.format(r0, r8)
                android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r0)
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x03df }
                java.lang.String r7 = "chats_attachMessage"
                r0.<init>(r7)     // Catch:{ Exception -> 0x03df }
                int r7 = r2.length()     // Catch:{ Exception -> 0x03df }
                r8 = 2
                int r7 = r7 + r8
                int r8 = r6.length()     // Catch:{ Exception -> 0x03df }
                r9 = 33
                r6.setSpan(r0, r7, r8, r9)     // Catch:{ Exception -> 0x03df }
                goto L_0x03e3
            L_0x03df:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x03e3:
                r12 = r6
                r6 = r5
                goto L_0x0414
            L_0x03e6:
                org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
                java.lang.String r5 = r5.message
                if (r5 == 0) goto L_0x040f
                int r9 = r5.length()
                if (r9 <= r7) goto L_0x03f6
                java.lang.String r5 = r5.substring(r14, r7)
            L_0x03f6:
                java.lang.String r5 = r5.replace(r13, r8)
                java.lang.String r5 = r5.trim()
                r7 = 2
                java.lang.Object[] r8 = new java.lang.Object[r7]
                r8[r14] = r5
                r5 = 1
                r8[r5] = r2
                java.lang.String r0 = java.lang.String.format(r0, r8)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0413
            L_0x040f:
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r12)
            L_0x0413:
                r12 = r0
            L_0x0414:
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0428 }
                java.lang.String r5 = "chats_nameMessage"
                r0.<init>(r5)     // Catch:{ Exception -> 0x0428 }
                int r2 = r2.length()     // Catch:{ Exception -> 0x0428 }
                r5 = 1
                int r2 = r2 + r5
                r5 = 33
                r12.setSpan(r0, r14, r2, r5)     // Catch:{ Exception -> 0x0428 }
                goto L_0x0521
            L_0x0428:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0521
            L_0x042e:
                org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
                if (r2 == 0) goto L_0x044b
                org.telegram.tgnet.TLRPC$Photo r2 = r0.photo
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
                if (r2 == 0) goto L_0x044b
                int r2 = r0.ttl_seconds
                if (r2 == 0) goto L_0x044b
                r0 = 2131624389(0x7f0e01c5, float:1.8875956E38)
                java.lang.String r2 = "AttachPhotoExpired"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r2, r0)
                goto L_0x0521
            L_0x044b:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
                if (r2 == 0) goto L_0x0464
                org.telegram.tgnet.TLRPC$Document r2 = r0.document
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
                if (r2 == 0) goto L_0x0464
                int r2 = r0.ttl_seconds
                if (r2 == 0) goto L_0x0464
                r0 = 2131624395(0x7f0e01cb, float:1.8875969E38)
                java.lang.String r2 = "AttachVideoExpired"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r2, r0)
                goto L_0x0521
            L_0x0464:
                java.lang.CharSequence r2 = r4.caption
                if (r2 == 0) goto L_0x04a6
                boolean r0 = r4.isVideo()
                if (r0 == 0) goto L_0x0472
                java.lang.String r0 = "📹 "
                goto L_0x0493
            L_0x0472:
                boolean r0 = r4.isVoice()
                if (r0 == 0) goto L_0x047c
                java.lang.String r0 = "🎤 "
                goto L_0x0493
            L_0x047c:
                boolean r0 = r4.isMusic()
                if (r0 == 0) goto L_0x0486
                java.lang.String r0 = "🎧 "
                goto L_0x0493
            L_0x0486:
                boolean r0 = r4.isPhoto()
                if (r0 == 0) goto L_0x0490
                java.lang.String r0 = "🖼 "
                goto L_0x0493
            L_0x0490:
                java.lang.String r0 = "📎 "
            L_0x0493:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r0)
                java.lang.CharSequence r0 = r4.caption
                r2.append(r0)
                java.lang.String r12 = r2.toString()
                goto L_0x0521
            L_0x04a6:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                if (r2 == 0) goto L_0x04c4
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r5 = "📊 "
                r2.append(r5)
                org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
                java.lang.String r0 = r0.question
                r2.append(r0)
                java.lang.String r0 = r2.toString()
            L_0x04c2:
                r12 = r0
                goto L_0x0509
            L_0x04c4:
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r0 == 0) goto L_0x04e3
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "🎮 "
                r0.append(r2)
                org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
                org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
                java.lang.String r2 = r2.title
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                goto L_0x04c2
            L_0x04e3:
                int r0 = r4.type
                r2 = 14
                if (r0 != r2) goto L_0x0501
                r2 = 2
                java.lang.Object[] r0 = new java.lang.Object[r2]
                java.lang.String r2 = r4.getMusicAuthor()
                r0[r14] = r2
                java.lang.String r2 = r4.getMusicTitle()
                r5 = 1
                r0[r5] = r2
                java.lang.String r2 = "🎧 %s - %s"
                java.lang.String r0 = java.lang.String.format(r2, r0)
                goto L_0x04c2
            L_0x0501:
                java.lang.CharSequence r0 = r4.messageText
                java.util.ArrayList<java.lang.String> r2 = r4.highlightedWords
                org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2)
                goto L_0x04c2
            L_0x0509:
                org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                if (r0 == 0) goto L_0x0521
                boolean r0 = r4.isMediaEmpty()
                if (r0 != 0) goto L_0x0521
                android.content.Context r0 = r19.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r6 = r0.getColor(r9)
            L_0x0521:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r15]
                r2 = 2131230908(0x7var_bc, float:1.8077882E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
                int r2 = r2.date
                long r4 = (long) r2
                java.lang.String r2 = org.telegram.messenger.LocaleController.stringForMessageListDate(r4)
                r0.setText(r2)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r15]
                r2 = 2131230906(0x7var_ba, float:1.8077878E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                java.lang.String r2 = r12.toString()
                r0.setText(r2)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r15]
                r2 = 2131230906(0x7var_ba, float:1.8077878E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setTextColor(r6)
                goto L_0x0578
            L_0x055f:
                if (r11 == 0) goto L_0x0578
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r15]
                r2 = 2131230908(0x7var_bc, float:1.8077882E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                int r2 = r11.last_message_date
                long r4 = (long) r2
                java.lang.String r2 = org.telegram.messenger.LocaleController.stringForMessageListDate(r4)
                r0.setText(r2)
            L_0x0578:
                r0 = 2131230904(0x7var_b8, float:1.8077874E38)
                if (r11 == 0) goto L_0x05ab
                int r2 = r11.unread_count
                if (r2 <= 0) goto L_0x05ab
                android.view.ViewGroup[] r2 = r1.cells
                r2 = r2[r15]
                android.view.View r2 = r2.findViewById(r0)
                android.widget.TextView r2 = (android.widget.TextView) r2
                r4 = 1
                java.lang.Object[] r5 = new java.lang.Object[r4]
                int r4 = r11.unread_count
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r5[r14] = r4
                java.lang.String r4 = "%d"
                java.lang.String r4 = java.lang.String.format(r4, r5)
                r2.setText(r4)
                android.view.ViewGroup[] r2 = r1.cells
                r2 = r2[r15]
                android.view.View r0 = r2.findViewById(r0)
                r0.setVisibility(r14)
                goto L_0x05b6
            L_0x05ab:
                android.view.ViewGroup[] r2 = r1.cells
                r2 = r2[r15]
                android.view.View r0 = r2.findViewById(r0)
                r0.setVisibility(r10)
            L_0x05b6:
                int r15 = r15 + 1
                r6 = r16
                r2 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
                r4 = 1111490560(0x42400000, float:48.0)
                r5 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
                r7 = 0
                r8 = 0
                r11 = 2
                goto L_0x001e
            L_0x05c8:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r14]
                r2 = 2131230905(0x7var_b9, float:1.8077876E38)
                android.view.View r0 = r0.findViewById(r2)
                android.view.ViewGroup[] r3 = r1.cells
                r4 = 1
                r3 = r3[r4]
                int r3 = r3.getVisibility()
                r0.setVisibility(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r4]
                android.view.View r0 = r0.findViewById(r2)
                r0.setVisibility(r10)
                goto L_0x08b3
            L_0x05ec:
                r16 = r6
                r4 = 1
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                if (r0 != r4) goto L_0x08b3
                r2 = 0
            L_0x05f8:
                r4 = 2
                if (r2 >= r4) goto L_0x08b3
                r5 = 0
            L_0x05fc:
                if (r5 >= r4) goto L_0x08a4
                int r0 = r2 * 2
                int r0 = r0 + r5
                org.telegram.ui.EditWidgetActivity r4 = r1.this$0
                java.util.ArrayList r4 = r4.selectedDialogs
                boolean r4 = r4.isEmpty()
                if (r4 == 0) goto L_0x0646
                org.telegram.ui.EditWidgetActivity r4 = r1.this$0
                org.telegram.messenger.MediaDataController r4 = r4.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r4 = r4.hints
                int r4 = r4.size()
                if (r0 >= r4) goto L_0x068a
                org.telegram.ui.EditWidgetActivity r4 = r1.this$0
                org.telegram.messenger.MediaDataController r4 = r4.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r4 = r4.hints
                java.lang.Object r4 = r4.get(r0)
                org.telegram.tgnet.TLRPC$TL_topPeer r4 = (org.telegram.tgnet.TLRPC$TL_topPeer) r4
                org.telegram.tgnet.TLRPC$Peer r4 = r4.peer
                int r4 = r4.user_id
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
                long r7 = (long) r4
                java.lang.Object r4 = r6.get(r7)
                org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC$Dialog) r4
                if (r4 != 0) goto L_0x068b
                org.telegram.tgnet.TLRPC$TL_dialog r4 = new org.telegram.tgnet.TLRPC$TL_dialog
                r4.<init>()
                r4.id = r7
                goto L_0x068b
            L_0x0646:
                org.telegram.ui.EditWidgetActivity r4 = r1.this$0
                java.util.ArrayList r4 = r4.selectedDialogs
                int r4 = r4.size()
                if (r0 >= r4) goto L_0x068a
                org.telegram.ui.EditWidgetActivity r4 = r1.this$0
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r4 = r4.dialogs_dict
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                java.util.ArrayList r6 = r6.selectedDialogs
                java.lang.Object r6 = r6.get(r0)
                java.lang.Long r6 = (java.lang.Long) r6
                long r6 = r6.longValue()
                java.lang.Object r4 = r4.get(r6)
                org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC$Dialog) r4
                if (r4 != 0) goto L_0x068b
                org.telegram.tgnet.TLRPC$TL_dialog r4 = new org.telegram.tgnet.TLRPC$TL_dialog
                r4.<init>()
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                java.util.ArrayList r6 = r6.selectedDialogs
                java.lang.Object r6 = r6.get(r0)
                java.lang.Long r6 = (java.lang.Long) r6
                long r6 = r6.longValue()
                r4.id = r6
                goto L_0x068b
            L_0x068a:
                r4 = 0
            L_0x068b:
                r6 = 2131230789(0x7var_, float:1.807764E38)
                r7 = 2131230790(0x7var_, float:1.8077643E38)
                if (r4 != 0) goto L_0x06bc
                android.view.ViewGroup[] r4 = r1.cells
                r4 = r4[r2]
                if (r5 != 0) goto L_0x069a
                goto L_0x069d
            L_0x069a:
                r6 = 2131230790(0x7var_, float:1.8077643E38)
            L_0x069d:
                android.view.View r4 = r4.findViewById(r6)
                r6 = 4
                r4.setVisibility(r6)
                if (r0 == 0) goto L_0x06aa
                r4 = 2
                if (r0 != r4) goto L_0x06b1
            L_0x06aa:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r0.setVisibility(r10)
            L_0x06b1:
                r8 = r16
                r6 = 0
                r7 = 1
                r9 = 0
                r11 = 1111490560(0x42400000, float:48.0)
                r16 = 0
                goto L_0x089d
            L_0x06bc:
                android.view.ViewGroup[] r8 = r1.cells
                r8 = r8[r2]
                if (r5 != 0) goto L_0x06c3
                goto L_0x06c6
            L_0x06c3:
                r6 = 2131230790(0x7var_, float:1.8077643E38)
            L_0x06c6:
                android.view.View r6 = r8.findViewById(r6)
                r6.setVisibility(r14)
                r6 = 2
                if (r0 == 0) goto L_0x06d2
                if (r0 != r6) goto L_0x06d9
            L_0x06d2:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r0.setVisibility(r14)
            L_0x06d9:
                long r7 = r4.id
                int r0 = (int) r7
                if (r0 <= 0) goto L_0x0741
                org.telegram.ui.EditWidgetActivity r7 = r1.this$0
                org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r7.getUser(r0)
                boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r7 == 0) goto L_0x0700
                r8 = r16
                r7 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r8, r7)
                r11 = r9
                r9 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
                goto L_0x071a
            L_0x0700:
                r8 = r16
                r7 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r9 == 0) goto L_0x0713
                r9 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r9)
                goto L_0x071a
            L_0x0713:
                r9 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
                java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r0)
            L_0x071a:
                boolean r12 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r12 != 0) goto L_0x073c
                boolean r12 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r12 != 0) goto L_0x073c
                org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
                if (r12 == 0) goto L_0x073c
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                if (r12 == 0) goto L_0x073c
                long r6 = r12.volume_id
                r16 = 0
                int r13 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
                if (r13 == 0) goto L_0x073c
                int r6 = r12.local_id
                if (r6 == 0) goto L_0x073c
                r6 = r12
                goto L_0x073d
            L_0x073c:
                r6 = 0
            L_0x073d:
                r7 = 0
                r16 = 0
                goto L_0x0773
            L_0x0741:
                r8 = r16
                r9 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                int r0 = -r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0)
                java.lang.String r11 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
                if (r6 == 0) goto L_0x076e
                org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
                if (r6 == 0) goto L_0x076e
                long r12 = r6.volume_id
                r16 = 0
                int r7 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
                if (r7 == 0) goto L_0x0770
                int r7 = r6.local_id
                if (r7 == 0) goto L_0x0770
                r7 = r0
                r0 = 0
                goto L_0x0773
            L_0x076e:
                r16 = 0
            L_0x0770:
                r7 = r0
                r0 = 0
                r6 = 0
            L_0x0773:
                android.view.ViewGroup[] r12 = r1.cells
                r12 = r12[r2]
                if (r5 != 0) goto L_0x077d
                r13 = 2131230797(0x7var_d, float:1.8077657E38)
                goto L_0x0780
            L_0x077d:
                r13 = 2131230798(0x7var_e, float:1.8077659E38)
            L_0x0780:
                android.view.View r12 = r12.findViewById(r13)
                android.widget.TextView r12 = (android.widget.TextView) r12
                r12.setText(r11)
                if (r6 == 0) goto L_0x07a0
                r11 = 1
                java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r11)     // Catch:{ all -> 0x0799 }
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0799 }
                android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeFile(r6)     // Catch:{ all -> 0x0799 }
                goto L_0x07a1
            L_0x0799:
                r0 = move-exception
                r6 = 0
                r9 = 0
                r11 = 1111490560(0x42400000, float:48.0)
                goto L_0x0843
            L_0x07a0:
                r6 = 0
            L_0x07a1:
                r11 = 1111490560(0x42400000, float:48.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x0840 }
                android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0840 }
                android.graphics.Bitmap r13 = android.graphics.Bitmap.createBitmap(r12, r12, r13)     // Catch:{ all -> 0x0840 }
                r13.eraseColor(r14)     // Catch:{ all -> 0x0840 }
                android.graphics.Canvas r15 = new android.graphics.Canvas     // Catch:{ all -> 0x0840 }
                r15.<init>(r13)     // Catch:{ all -> 0x0840 }
                if (r6 != 0) goto L_0x07e3
                if (r0 == 0) goto L_0x07d5
                org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0840 }
                r6.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0840 }
                boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0840 }
                if (r7 == 0) goto L_0x07ca
                r0 = 12
                r6.setAvatarType(r0)     // Catch:{ all -> 0x0840 }
                goto L_0x07da
            L_0x07ca:
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x0840 }
                if (r0 == 0) goto L_0x07da
                r7 = 1
                r6.setAvatarType(r7)     // Catch:{ all -> 0x0840 }
                goto L_0x07da
            L_0x07d5:
                org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0840 }
                r6.<init>((org.telegram.tgnet.TLRPC$Chat) r7)     // Catch:{ all -> 0x0840 }
            L_0x07da:
                r6.setBounds(r14, r14, r12, r12)     // Catch:{ all -> 0x0840 }
                r6.draw(r15)     // Catch:{ all -> 0x0840 }
                r6 = 0
                r9 = 0
                goto L_0x0821
            L_0x07e3:
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0840 }
                android.graphics.Shader$TileMode r7 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0840 }
                r0.<init>(r6, r7, r7)     // Catch:{ all -> 0x0840 }
                float r7 = (float) r12     // Catch:{ all -> 0x0840 }
                int r12 = r6.getWidth()     // Catch:{ all -> 0x0840 }
                float r12 = (float) r12     // Catch:{ all -> 0x0840 }
                float r7 = r7 / r12
                r15.save()     // Catch:{ all -> 0x0840 }
                r15.scale(r7, r7)     // Catch:{ all -> 0x0840 }
                android.graphics.Paint r7 = r1.roundPaint     // Catch:{ all -> 0x0840 }
                r7.setShader(r0)     // Catch:{ all -> 0x0840 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0840 }
                int r7 = r6.getWidth()     // Catch:{ all -> 0x0840 }
                float r7 = (float) r7     // Catch:{ all -> 0x0840 }
                int r12 = r6.getHeight()     // Catch:{ all -> 0x0840 }
                float r12 = (float) r12
                r9 = 0
                r0.set(r9, r9, r7, r12)     // Catch:{ all -> 0x083d }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x083d }
                int r7 = r6.getWidth()     // Catch:{ all -> 0x083d }
                float r7 = (float) r7     // Catch:{ all -> 0x083d }
                int r6 = r6.getHeight()     // Catch:{ all -> 0x083d }
                float r6 = (float) r6     // Catch:{ all -> 0x083d }
                android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x083d }
                r15.drawRoundRect(r0, r7, r6, r12)     // Catch:{ all -> 0x083d }
                r15.restore()     // Catch:{ all -> 0x083d }
                r6 = 0
            L_0x0821:
                r15.setBitmap(r6)     // Catch:{ all -> 0x083b }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x083b }
                r0 = r0[r2]     // Catch:{ all -> 0x083b }
                if (r5 != 0) goto L_0x082e
                r7 = 2131230791(0x7var_, float:1.8077645E38)
                goto L_0x0831
            L_0x082e:
                r7 = 2131230792(0x7var_, float:1.8077647E38)
            L_0x0831:
                android.view.View r0 = r0.findViewById(r7)     // Catch:{ all -> 0x083b }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x083b }
                r0.setImageBitmap(r13)     // Catch:{ all -> 0x083b }
                goto L_0x0846
            L_0x083b:
                r0 = move-exception
                goto L_0x0843
            L_0x083d:
                r0 = move-exception
                r6 = 0
                goto L_0x0843
            L_0x0840:
                r0 = move-exception
                r6 = 0
                r9 = 0
            L_0x0843:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0846:
                if (r4 == 0) goto L_0x0888
                int r0 = r4.unread_count
                if (r0 <= 0) goto L_0x0888
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r5 != 0) goto L_0x0856
                r7 = 2131230793(0x7var_, float:1.8077649E38)
                goto L_0x0859
            L_0x0856:
                r7 = 2131230794(0x7var_a, float:1.807765E38)
            L_0x0859:
                android.view.View r0 = r0.findViewById(r7)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r7 = 1
                java.lang.Object[] r12 = new java.lang.Object[r7]
                int r4 = r4.unread_count
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r12[r14] = r4
                java.lang.String r4 = "%d"
                java.lang.String r4 = java.lang.String.format(r4, r12)
                r0.setText(r4)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r5 != 0) goto L_0x087d
                r4 = 2131230795(0x7var_b, float:1.8077653E38)
                goto L_0x0880
            L_0x087d:
                r4 = 2131230796(0x7var_c, float:1.8077655E38)
            L_0x0880:
                android.view.View r0 = r0.findViewById(r4)
                r0.setVisibility(r14)
                goto L_0x089d
            L_0x0888:
                r7 = 1
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r5 != 0) goto L_0x0893
                r4 = 2131230795(0x7var_b, float:1.8077653E38)
                goto L_0x0896
            L_0x0893:
                r4 = 2131230796(0x7var_c, float:1.8077655E38)
            L_0x0896:
                android.view.View r0 = r0.findViewById(r4)
                r0.setVisibility(r10)
            L_0x089d:
                int r5 = r5 + 1
                r16 = r8
                r4 = 2
                goto L_0x05fc
            L_0x08a4:
                r8 = r16
                r6 = 0
                r7 = 1
                r9 = 0
                r11 = 1111490560(0x42400000, float:48.0)
                r16 = 0
                int r2 = r2 + 1
                r16 = r8
                goto L_0x05f8
            L_0x08b3:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r14]
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x08c7
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r0.setVisibility(r10)
                goto L_0x08d0
            L_0x08c7:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r0.setVisibility(r14)
            L_0x08d0:
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
                    if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
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

    public EditWidgetActivity(int i, int i2, boolean z) {
        this.widgetType = i;
        this.currentWidgetId = i2;
        this.isEdit = z;
        if (z) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            getMessagesStorage().getWidgetDialogIds(this.currentWidgetId, this.widgetType, this.selectedDialogs, arrayList, arrayList2, true);
            getMessagesController().putUsers(arrayList, true);
            getMessagesController().putChats(arrayList2, true);
        }
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
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.widgetType == 0) {
            this.actionBar.setTitle(LocaleController.getString("WidgetChats", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WidgetShortcuts", NUM));
        }
        this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
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
                    SharedPreferences sharedPreferences = EditWidgetActivity.this.getParentActivity().getSharedPreferences("shortcut_widget", 0);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("account" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.currentAccount).commit();
                    SharedPreferences.Editor edit2 = sharedPreferences.edit();
                    edit2.putInt("type" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.widgetType).commit();
                    AppWidgetManager instance = AppWidgetManager.getInstance(EditWidgetActivity.this.getParentActivity());
                    if (EditWidgetActivity.this.widgetType == 0) {
                        ChatsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.isEdit);
                    } else {
                        ContactsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.isEdit);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                EditWidgetActivity.this.lambda$createView$1$EditWidgetActivity(this.f$1, view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            private Rect rect = new Rect();

            public void onLongClickRelease() {
            }

            public void onMove(float f, float f2) {
            }

            public boolean onItemClick(View view, int i, float f, float f2) {
                if (EditWidgetActivity.this.getParentActivity() != null && (view instanceof GroupCreateUserCell)) {
                    ((ImageView) view.getTag(NUM)).getHitRect(this.rect);
                    if (!this.rect.contains((int) f, (int) f2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) EditWidgetActivity.this.getParentActivity());
                        builder.setItems(new CharSequence[]{LocaleController.getString("Delete", NUM)}, new DialogInterface.OnClickListener(i) {
                            public final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                EditWidgetActivity.AnonymousClass2.this.lambda$onItemClick$0$EditWidgetActivity$2(this.f$1, dialogInterface, i);
                            }
                        });
                        EditWidgetActivity.this.showDialog(builder.create());
                        return true;
                    }
                }
                return false;
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$0 */
            public /* synthetic */ void lambda$onItemClick$0$EditWidgetActivity$2(int i, DialogInterface dialogInterface, int i2) {
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
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$EditWidgetActivity(Context context, View view, int i) {
        if (i == this.selectChatsRow) {
            InviteMembersBottomSheet inviteMembersBottomSheet = new InviteMembersBottomSheet(context, this.currentAccount, (SparseArray<TLObject>) null, 0, this);
            inviteMembersBottomSheet.setDelegate(new InviteMembersBottomSheet.InviteMembersBottomSheetDelegate() {
                public final void didSelectDialogs(ArrayList arrayList) {
                    EditWidgetActivity.this.lambda$null$0$EditWidgetActivity(arrayList);
                }
            }, this.selectedDialogs);
            inviteMembersBottomSheet.setSelectedContacts(this.selectedDialogs);
            showDialog(inviteMembersBottomSheet);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$EditWidgetActivity(ArrayList arrayList) {
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    EditWidgetActivity.this.removeSelfFromStack();
                }
            }, 1000);
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: org.telegram.ui.EditWidgetActivity$WidgetPreviewCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                if (r10 == 0) goto L_0x007d
                r9 = 1
                if (r10 == r9) goto L_0x006b
                r9 = 2
                if (r10 == r9) goto L_0x005b
                org.telegram.ui.Cells.GroupCreateUserCell r9 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r10 = r8.mContext
                r0 = 0
                r9.<init>(r10, r0, r0, r0)
                android.widget.ImageView r10 = new android.widget.ImageView
                android.content.Context r0 = r8.mContext
                r10.<init>(r0)
                r0 = 2131165586(0x7var_, float:1.7945393E38)
                r10.setImageResource(r0)
                android.widget.ImageView$ScaleType r0 = android.widget.ImageView.ScaleType.CENTER
                r10.setScaleType(r0)
                r0 = 2131230866(0x7var_, float:1.8077797E38)
                r9.setTag(r0, r10)
                r1 = 40
                r2 = -1082130432(0xffffffffbvar_, float:-1.0)
                boolean r0 = org.telegram.messenger.LocaleController.isRTL
                if (r0 == 0) goto L_0x0032
                r0 = 3
                goto L_0x0033
            L_0x0032:
                r0 = 5
            L_0x0033:
                r3 = r0 | 16
                r4 = 1092616192(0x41200000, float:10.0)
                r5 = 0
                r6 = 1092616192(0x41200000, float:10.0)
                r7 = 0
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
                r9.addView(r10, r0)
                org.telegram.ui.-$$Lambda$EditWidgetActivity$ListAdapter$DSsKbuKfWT3I3cbgW4RlMG2dRdM r0 = new org.telegram.ui.-$$Lambda$EditWidgetActivity$ListAdapter$DSsKbuKfWT3I3cbgW4RlMG2dRdM
                r0.<init>(r9)
                r10.setOnTouchListener(r0)
                android.graphics.PorterDuffColorFilter r0 = new android.graphics.PorterDuffColorFilter
                java.lang.String r1 = "chats_pinnedIcon"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
                r0.<init>(r1, r2)
                r10.setColorFilter(r0)
                goto L_0x0093
            L_0x005b:
                org.telegram.ui.EditWidgetActivity r9 = org.telegram.ui.EditWidgetActivity.this
                org.telegram.ui.EditWidgetActivity$WidgetPreviewCell r10 = new org.telegram.ui.EditWidgetActivity$WidgetPreviewCell
                org.telegram.ui.EditWidgetActivity r0 = org.telegram.ui.EditWidgetActivity.this
                android.content.Context r1 = r8.mContext
                r10.<init>(r0, r1)
                org.telegram.ui.EditWidgetActivity.WidgetPreviewCell unused = r9.widgetPreviewCell = r10
                r9 = r10
                goto L_0x0093
            L_0x006b:
                org.telegram.ui.Cells.TextCell r9 = new org.telegram.ui.Cells.TextCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                java.lang.String r10 = "windowBackgroundWhite"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r9.setBackgroundColor(r10)
                goto L_0x0093
            L_0x007d:
                org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                android.content.Context r10 = r8.mContext
                r0 = 2131165448(0x7var_, float:1.7945113E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r10, (int) r0, (java.lang.String) r1)
                r9.setBackgroundDrawable(r10)
            L_0x0093:
                org.telegram.ui.Components.RecyclerListView$Holder r10 = new org.telegram.ui.Components.RecyclerListView$Holder
                r10.<init>(r9)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$0 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0$EditWidgetActivity$ListAdapter(GroupCreateUserCell groupCreateUserCell, View view, MotionEvent motionEvent) {
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
                if (i != EditWidgetActivity.this.infoRow) {
                    return;
                }
                if (EditWidgetActivity.this.widgetType == 0) {
                    textInfoPrivacyCell.setText(LocaleController.getString("EditWidgetChatsInfo", NUM));
                } else if (EditWidgetActivity.this.widgetType == 1) {
                    textInfoPrivacyCell.setText(LocaleController.getString("EditWidgetContactsInfo", NUM));
                }
            } else if (itemViewType == 1) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                textCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
                Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(drawable, drawable2);
                String string = LocaleController.getString("SelectChats", NUM);
                if (EditWidgetActivity.this.chatsStartRow == -1) {
                    z = false;
                }
                textCell.setTextAndIcon(string, (Drawable) combinedDrawable, z);
                textCell.getImageView().setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
            } else if (itemViewType == 3) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                int longValue = (int) ((Long) EditWidgetActivity.this.selectedDialogs.get(i - EditWidgetActivity.this.chatsStartRow)).longValue();
                if (longValue > 0) {
                    TLRPC$User user = EditWidgetActivity.this.getMessagesController().getUser(Integer.valueOf(longValue));
                    if (i == EditWidgetActivity.this.chatsEndRow - 1) {
                        z = false;
                    }
                    groupCreateUserCell.setObject(user, (CharSequence) null, (CharSequence) null, z);
                    return;
                }
                TLRPC$Chat chat = EditWidgetActivity.this.getMessagesController().getChat(Integer.valueOf(-longValue));
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
            int access$1400 = i - EditWidgetActivity.this.chatsStartRow;
            int access$14002 = i2 - EditWidgetActivity.this.chatsStartRow;
            int access$100 = EditWidgetActivity.this.chatsEndRow - EditWidgetActivity.this.chatsStartRow;
            if (access$1400 < 0 || access$14002 < 0 || access$1400 >= access$100 || access$14002 >= access$100) {
                return false;
            }
            EditWidgetActivity.this.selectedDialogs.set(access$1400, (Long) EditWidgetActivity.this.selectedDialogs.get(access$14002));
            EditWidgetActivity.this.selectedDialogs.set(access$14002, (Long) EditWidgetActivity.this.selectedDialogs.get(access$1400));
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
