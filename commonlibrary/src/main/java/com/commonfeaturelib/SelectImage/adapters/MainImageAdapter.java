package com.commonfeaturelib.SelectImage.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonfeaturelib.R;
import com.commonfeaturelib.SelectImage.PhotoSelectPojo;
import com.commonfeaturelib.SelectImage.interfaces.OnSelectionListner;
import com.commonfeaturelib.SelectImage.interfaces.SectionIndexer;
import com.commonfeaturelib.SelectImage.utility.HeaderItemDecoration;
import com.commonfeaturelib.SelectImage.utility.Utility;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

/**
 * Created by janarthananr on 16/5/18.
 */

public class MainImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements HeaderItemDecoration.StickyHeaderInterface, SectionIndexer {
    public static final int HEADER = 1;
    public static final int ITEM = 2;
    public static int spanCount = 3;
    Context context;
    ArrayList<PhotoSelectPojo> list;
    OnSelectionListner onSelectionListner;

    int margin = 2;
    float size = ((Utility.WIDTH / spanCount));

    FrameLayout.LayoutParams layoutParams;

    public MainImageAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        layoutParams = new FrameLayout.LayoutParams((int) size, (int) size);
        layoutParams.setMargins(margin, margin, margin, margin);

    }

    public ArrayList<PhotoSelectPojo> getItemList() {
        return list;
    }

    public MainImageAdapter addImage(PhotoSelectPojo image) {
        list.add(image);
        notifyDataSetChanged();
        return this;
    }

    public void AddOnSelectionListner(OnSelectionListner onSelectionListner) {
        this.onSelectionListner = onSelectionListner;
    }

    public MainImageAdapter addImageList(ArrayList<PhotoSelectPojo> imagelist) {
        list.addAll(imagelist);
        notifyDataSetChanged();
        return this;
    }

    public void ClearList() {
        list.clear();
    }

    public void Select(boolean selection, int pos) {
        list.get(pos).isSelected=selection;
        notifyItemChanged(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.header_row, parent, false));
        } else {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.main_image, parent, false);
            return new Holder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoSelectPojo i = list.get(position);
        if (holder instanceof Holder) {
            Holder h = (Holder) holder;

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(i.contentUrl))
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(130, 130))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
            h.sdv.setController(controller);
            h.selection.setVisibility(i.isSelected ? View.VISIBLE : View.GONE);
        } else if (holder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(i.headerDate);
        }
    }

    @Override
    public int getItemViewType(int position) {
        PhotoSelectPojo i = list.get(position);
        return (i.contentUrl.equalsIgnoreCase("")) ?
                HEADER : ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        // Log.e("itemPosition", " ---- " + itemPosition);
        return headerPosition;
       /*
        pos = () ? itemPosition : pos;
        Log.e("itemPosition", " ---- " + itemPosition + "  pos  - " + pos);
        return pos;*/
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        //  Log.e("headerPosition", " ---- " + headerPosition);
        return R.layout.header_row;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        PhotoSelectPojo i = list.get(headerPosition);
        ((TextView) header.findViewById(R.id.header)).setText(i.headerDate);
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItemViewType(itemPosition) == 1;
    }

    @Override
    public String getSectionText(int position) {
        PhotoSelectPojo i = list.get(position);
        return "" + i.headerDate;
    }

    public String getSectionMonthYearText(int position) {
        PhotoSelectPojo i = list.get(position);
        return "" + i.scrollerDate;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        SimpleDraweeView sdv;
        ImageView selection;

        public Holder(View itemView) {
            super(itemView);
            sdv = itemView.findViewById(R.id.sdv);
            selection = itemView.findViewById(R.id.selection);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            sdv.setLayoutParams(layoutParams);
        }

        @Override
        public void onClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListner.OnClick(list.get(id), view, id);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListner.OnLongClick(list.get(id), view, id);
            return true;
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
        }
    }
}
