package com.inri.sopsop.view;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inri.sopsop.R;
import com.inri.sopsop.view.adapter.BluetoothDeviceAdapter;
import com.inri.sopsop.view.base.SimpleFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BluetoothSetupFragment extends SimpleFragment {

    private BluetoothDeviceAdapter adapter;

    @BindView(R.id.list)
    RecyclerView list;

    public static BluetoothSetupFragment newInstance() {

        Bundle args = new Bundle();

        BluetoothSetupFragment fragment = new BluetoothSetupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.bluetoothSetupTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_bluetooth_setup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new BluetoothDeviceAdapter();

        List<BluetoothDevice> pairedDevices = getMainActivity().getPairedDevices();
        BluetoothDeviceAdapter.OnDeviceClickListener listener = getMainActivity().getBluetoothListener();

        adapter.setDevices(new ArrayList<>(pairedDevices), listener);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);

        list.setAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}