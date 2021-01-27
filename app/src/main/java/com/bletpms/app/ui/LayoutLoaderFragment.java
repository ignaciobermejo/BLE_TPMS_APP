package com.bletpms.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bletpms.app.ui.home.HomeViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class LayoutLoaderFragment extends Fragment {

    private AndroidViewModel model;
    private View root;
    private ArrayList<MaterialCardView> cards;
    private boolean layoutLoaded = false;

    public LayoutLoaderFragment(AndroidViewModel model, View root) {
        this.model = model;
        this.root = root;
        this.cards = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        model = new ViewModelProvider(this).get(model.getClass());
        return root;
    }
}
