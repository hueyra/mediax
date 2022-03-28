package com.github.hueyra.mediax.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.config.PictureMimeType;
import com.github.hueyra.mediax.config.PictureSelectionConfig;
import com.github.hueyra.mediax.entity.LocalMediaFolder;
import com.github.hueyra.mediax.listener.OnAlbumItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PictureAlbumDirectoryAdapter extends RecyclerView.Adapter<PictureAlbumDirectoryAdapter.ViewHolder> {
    private List<LocalMediaFolder> folders = new ArrayList<>();
    private int chooseMode;

    public PictureAlbumDirectoryAdapter(PictureSelectionConfig config) {
        super();
        this.chooseMode = config.chooseMode;
    }

    public void bindFolderData(List<LocalMediaFolder> folders) {
        this.folders = folders == null ? new ArrayList<>() : folders;
        notifyDataSetChanged();
    }

    public void setChooseMode(int chooseMode) {
        this.chooseMode = chooseMode;
    }

    public List<LocalMediaFolder> getFolderData() {
        return folders == null ? new ArrayList<>() : folders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picture_album_folder_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LocalMediaFolder folder = folders.get(position);
        holder.tvSign.setVisibility(folder.getCheckedNum() > 0 ? View.VISIBLE : View.INVISIBLE);
        holder.ivSelect.setVisibility(folder.isChecked() ? View.VISIBLE : View.GONE);
        if (chooseMode == PictureMimeType.ofAudio()) {
            holder.ivFirstImage.setImageResource(R.drawable.picture_audio_placeholder);
        } else {
            if (PictureSelectionConfig.imageEngine != null) {
                PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(),
                        folder.getFirstImagePath(), holder.ivFirstImage);
            }
        }
        Context context = holder.itemView.getContext();
        String firstTitle = folder.getOfAllType() != -1 ? folder.getOfAllType() == PictureMimeType.ofAudio() ? context.getString(R.string.picture_all_audio) : context.getString(R.string.picture_camera_roll) : folder.getName();
        holder.tvFolderName.setText(firstTitle);
        holder.tvFolderNum.setText(("(" + folder.getImageNum() + ")"));
        holder.itemView.setOnClickListener(view -> {
            if (onAlbumItemClickListener != null) {
                int size = folders.size();
                for (int i = 0; i < size; i++) {
                    LocalMediaFolder mediaFolder = folders.get(i);
                    mediaFolder.setChecked(false);
                }
                folder.setChecked(true);
                notifyDataSetChanged();
                onAlbumItemClickListener.onItemClick(position, folder.isCameraFolder(), folder.getBucketId(), folder.getName(), folder.getData());
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFirstImage, ivSelect;
        TextView tvFolderName, tvFolderNum, tvSign;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFirstImage = itemView.findViewById(R.id.first_image);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            tvFolderNum = itemView.findViewById(R.id.tv_folder_num);
            tvSign = itemView.findViewById(R.id.tv_sign);
            ivSelect = itemView.findViewById(R.id.iv_folder_selector);
//            if (PictureSelectionConfig.uiStyle != null) {
//                if (PictureSelectionConfig.uiStyle.picture_album_checkDotStyle != 0) {
//                    tvSign.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_album_checkDotStyle);
//                }
//                if (PictureSelectionConfig.uiStyle.picture_album_textColor != 0) {
//                    tvFolderName.setTextColor(PictureSelectionConfig.uiStyle.picture_album_textColor);
//                }
//                if (PictureSelectionConfig.uiStyle.picture_album_textSize > 0) {
//                    tvFolderName.setTextSize(PictureSelectionConfig.uiStyle.picture_album_textSize);
//                }
//            } else if (PictureSelectionConfig.style != null) {
//                if (PictureSelectionConfig.style.pictureFolderCheckedDotStyle != 0) {
//                    tvSign.setBackgroundResource(PictureSelectionConfig.style.pictureFolderCheckedDotStyle);
//                }
//                if (PictureSelectionConfig.style.folderTextColor != 0) {
//                    tvFolderName.setTextColor(PictureSelectionConfig.style.folderTextColor);
//                }
//                if (PictureSelectionConfig.style.folderTextSize > 0) {
//                    tvFolderName.setTextSize(PictureSelectionConfig.style.folderTextSize);
//                }
//            } else {
//                Drawable folderCheckedDotDrawable = AttrsUtils.getTypeValueDrawable(itemView.getContext(), R.attr.picture_folder_checked_dot, R.drawable.picture_orange_oval);
//                tvSign.setBackground(folderCheckedDotDrawable);
//                int folderTextColor = AttrsUtils.getTypeValueColor(itemView.getContext(), R.attr.picture_folder_textColor);
//                if (folderTextColor != 0) {
//                    tvFolderName.setTextColor(folderTextColor);
//                }
//                float folderTextSize = AttrsUtils.getTypeValueSize(itemView.getContext(), R.attr.picture_folder_textSize);
//                if (folderTextSize > 0) {
//                    tvFolderName.setTextSize(TypedValue.COMPLEX_UNIT_PX, folderTextSize);
//                }
//            }
        }
    }

    private OnAlbumItemClickListener onAlbumItemClickListener;

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener) {
        this.onAlbumItemClickListener = listener;
    }
}
