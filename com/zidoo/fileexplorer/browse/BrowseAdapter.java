package com.zidoo.fileexplorer.browse;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import java.util.ArrayList;
import java.util.List;

public class BrowseAdapter extends Adapter<BrowseViewHolder> {
    private int count = 0;
    private List<BrowseItem> items = new ArrayList(0);
    private OnFileListener onFileListener;

    private class ItemListener implements OnClickListener, OnKeyListener, OnFocusChangeListener {
        private BrowseViewHolder holder;

        ItemListener(BrowseViewHolder holder) {
            this.holder = holder;
        }

        public void onClick(View v) {
            if (BrowseAdapter.this.onFileListener == null) {
                return;
            }
            if (BrowseAdapter.this.items.size() == 0) {
                BrowseAdapter.this.onFileListener.onBack();
                return;
            }
            int position = this.holder.getAdapterPosition();
            if (position == BrowseAdapter.this.items.size()) {
                BrowseAdapter.this.onFileListener.onTop();
            } else {
                BrowseAdapter.this.onFileListener.onFileClick((BrowseItem) BrowseAdapter.this.items.get(position));
            }
        }

        public void onFocusChange(View v, boolean hasFocus) {
            this.holder.name.setSelected(hasFocus);
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (BrowseAdapter.this.onFileListener == null) {
                return false;
            }
            return BrowseAdapter.this.onFileListener.onFileItemKey(v, this.holder.getAdapterPosition(), keyCode, event);
        }
    }

    interface OnFileListener {
        void onBack();

        void onFileClick(BrowseItem browseItem);

        boolean onFileItemKey(View view, int i, int i2, KeyEvent keyEvent);

        void onTop();
    }

    class BrowseViewHolder extends ViewHolder {
        ImageView icon;
        TextView name;
        ImageView select;

        BrowseViewHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.select = (ImageView) itemView.findViewById(R.id.select);
        }
    }

    public void setOnFileListener(OnFileListener onFileListener) {
        this.onFileListener = onFileListener;
    }

    public void setItems(List<BrowseItem> items) {
        this.items = items;
        int size = items.size() == 0 ? 1 : items.size() > 8 ? items.size() + 1 : items.size();
        this.count = size;
        notifyDataSetChanged();
    }

    public void onAddedOne() {
        int newCount = this.items.size() == 0 ? 1 : this.items.size() > 8 ? this.items.size() + 1 : this.items.size();
        for (int position = this.items.size() - 1; position < this.count; position++) {
            notifyItemChanged(position);
        }
        notifyItemRangeInserted(this.count, newCount - this.count);
        this.count = newCount;
    }

    public BrowseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BrowseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false));
    }

    public void onBindViewHolder(BrowseViewHolder holder, int position) {
        if (this.items.size() == 0) {
            holder.icon.setImageResource(R.drawable.ic_browse_back);
            holder.name.setText(R.string.back);
        } else if (position == this.items.size()) {
            holder.icon.setImageResource(R.drawable.ic_browse_top);
            holder.name.setText(R.string.back_top);
        } else {
            BrowseItem item = (BrowseItem) this.items.get(position);
            holder.icon.setImageResource(item.getIcon());
            holder.name.setText(item.getName());
        }
        ItemListener listener = new ItemListener(holder);
        holder.itemView.setOnClickListener(listener);
        holder.itemView.setOnKeyListener(listener);
        holder.itemView.setOnFocusChangeListener(listener);
    }

    public int getItemCount() {
        return this.count;
    }
}
