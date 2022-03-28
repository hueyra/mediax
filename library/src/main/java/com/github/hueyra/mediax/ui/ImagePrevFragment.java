package com.github.hueyra.mediax.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.photoview.PhotoView;

public class ImagePrevFragment extends Fragment {

    private final String mUrl;

    public ImagePrevFragment(String url) {
        this.mUrl = url;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_prev, container, false);
        PhotoView mPhotoView = view.findViewById(R.id.pv_photo);

        Glide.with(requireContext()).load(mUrl).error(R.mipmap.ic_image_load_error).into(mPhotoView);

        mPhotoView.setOnPhotoTapListener((view1, x, y) -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

}
