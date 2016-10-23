package com.abdallaadelessa.demo.presentation.listAirlines.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdallaadelessa.core.utils.ImageLoaderManager;
import com.abdallaadelessa.core.view.BaseCoreRecyclerAdapter;
import com.abdallaadelessa.core.view.custom.TextViewWithMovingMarquee;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.util.AirlinesUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdullah on 01/10/16.
 */
public class AirLinesRVAdapter extends BaseCoreRecyclerAdapter<AirLinesRVAdapter.AirLineViewHolder, AirlineModel> {
    private IAirLinesRVAdapter iAirLinesRVAdapter;

    public void setIAirLinesRVAdapter(IAirLinesRVAdapter iAirLinesRVAdapter) {
        this.iAirLinesRVAdapter = iAirLinesRVAdapter;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_air_line, parent, false);
    }

    @Override
    public void onBindViewHolder(final AirLineViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        AirlineModel airLineModel = getData().get(position);
        holder.itemView.setTag(position);
        holder.likeBtn.setTag(position);
        holder.tvAirLineName.setText(airLineModel.getName());
        boolean fav = iAirLinesRVAdapter != null && iAirLinesRVAdapter.isFav(airLineModel);
        holder.likeBtn.setLiked(fav);
        int iconSize = (int) context.getResources().getDimension(R.dimen.listing_air_line_icon_size);
        ImageLoaderManager.getDefaultBuilder().path(context, AirlinesUtils.getFullLogoUrl(airLineModel)).imageView(holder.ivAirLineIcon).widthAndHeight(iconSize).load();
        holder.likeBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (iAirLinesRVAdapter != null) {
                    int pos = (int) likeButton.getTag();
                    AirlineModel airLineModel = getData().get(pos);
                    iAirLinesRVAdapter.onFavouriteAirLine(airLineModel, true);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (iAirLinesRVAdapter != null) {
                    int pos = (int) likeButton.getTag();
                    AirlineModel airLineModel = getData().get(pos);
                    iAirLinesRVAdapter.onFavouriteAirLine(airLineModel, false);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iAirLinesRVAdapter != null) {
                    int pos = (int) v.getTag();
                    AirlineModel airLineModel = getData().get(pos);
                    iAirLinesRVAdapter.onAirLineClicked(holder.tvAirLineName, holder.ivAirLineIcon, airLineModel);
                }
            }
        });
    }

    // --------------------->

    public static class AirLineViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAirLineIcon)
        ImageView ivAirLineIcon;
        @BindView(R.id.tvAirLineName)
        TextViewWithMovingMarquee tvAirLineName;
        @BindView(R.id.likeBtn)
        LikeButton likeBtn;

        public AirLineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface IAirLinesRVAdapter {
        boolean isFav(AirlineModel airLineModel);

        void onFavouriteAirLine(AirlineModel airLineModel, boolean isFav);

        void onAirLineClicked(View tvTitle, View ivIcon, AirlineModel airLineModel);
    }
}
