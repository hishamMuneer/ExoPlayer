package com.novo.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.novo.R;
import com.novo.models.CategoryContentModel;
import com.novo.models.CategoryItemModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ayushgarg on 02/09/17.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private Context context;
    private int layoutResource;
    private List<CategoryContentModel> categoryContentModels;
    private LayoutInflater layoutInflater;

    public ContentAdapter(Context context, int layoutResource, List<CategoryContentModel> categoryContentModels) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.categoryContentModels = categoryContentModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutResource, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CategoryContentModel categoryContentModel = categoryContentModels.get(position);
        holder.tvCatTitle.setText(categoryContentModel.getTitle());
        holder.tvCatSubTitle.setText(categoryContentModel.getSubTitle());

        holder.tvSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "See more: " + categoryContentModel.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.linearLayoutContainer.removeAllViews();
        for (final CategoryItemModel categoryItemModel : categoryContentModel.getItemModels()) {
            View rowHorizontalView = layoutInflater.inflate(R.layout.row_video_horizontal_home, null);
            ImageView ivThumbnail = (ImageView) rowHorizontalView.findViewById(R.id.ivThumbnail);
            TextView tvVideoTitle = (TextView) rowHorizontalView.findViewById(R.id.tvVideoTitle);
            CardView cv = (CardView) rowHorizontalView.findViewById(R.id.cv);

            Glide.with(context).load(categoryItemModel.getVideoUrl()).into(ivThumbnail);
            tvVideoTitle.setText(categoryItemModel.getVideoTitle());

            rowHorizontalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, categoryItemModel.getVideoTitle() + " clicked", Toast.LENGTH_SHORT).show();
                }
            });
            LinearLayout.LayoutParams layoutParams = getlayoutParams(holder.linearLayoutContainer);
            holder.linearLayoutContainer.addView(rowHorizontalView, layoutParams);
        }
    }

    private LinearLayout.LayoutParams getlayoutParams(ViewGroup viewGroup) {

        LinearLayout ll = (LinearLayout) viewGroup;
        float density = context.getResources().getDisplayMetrics().density;
        ll.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) (4 * density),(int) (4 * density),(int) (4 * density),(int) (4 * density));
        return layoutParams;
    }

    @Override
    public int getItemCount() {
        return categoryContentModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCatTitle;
        TextView tvCatSubTitle;
        TextView tvSeeMore;
        ViewGroup linearLayoutContainer;

        ViewHolder(View itemView) {
            super(itemView);
            tvCatTitle = (TextView) itemView.findViewById(R.id.tvCatTitle);
            tvCatSubTitle = (TextView) itemView.findViewById(R.id.tvCatSubTitle);
            tvSeeMore = (TextView) itemView.findViewById(R.id.tvSeeMore);
            linearLayoutContainer = (ViewGroup) itemView.findViewById(R.id.linearLayoutContainer);
        }
    }

}
