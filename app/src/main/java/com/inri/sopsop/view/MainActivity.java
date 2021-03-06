package com.inri.sopsop.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.inri.sopsop.App;
import com.inri.sopsop.BluetoothClient;
import com.inri.sopsop.BluetoothEventListener;
import com.inri.sopsop.R;
import com.inri.sopsop.Utils;
import com.inri.sopsop.model.StatisticsResult;
import com.inri.sopsop.presenter.MainPresenter;
import com.inri.sopsop.view.adapter.BluetoothDeviceAdapter;
import com.inri.sopsop.view.results.AttentionDistributionResultsFragment;
import com.inri.sopsop.view.results.ComplexMotorReactionResultsFragment;
import com.inri.sopsop.view.results.FocusingResultsFragment;
import com.inri.sopsop.view.results.ReactionResultsFragment;
import com.inri.sopsop.view.results.ResultScreen;
import com.inri.sopsop.view.tests.AttentionDistributionTestFragment;
import com.inri.sopsop.view.tests.ComplexMotorReactionTestFragment;
import com.inri.sopsop.view.tests.FocusingTestFragment;
import com.inri.sopsop.view.tests.ReactionTestFragment;
import com.wang.avi.AVLoadingIndicatorView;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter> implements BluetoothClient {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final String TAG_MAIN = "mainTag";

    private final static int PACKAGE_SIZE = 5;

    private final static int REQUEST_ENABLE_BT = 101;

    private final static int DEGREES_MIN = 1;
    private final static int DEGREES_MAX = 30;

    private final static String BLUETOOTH_TAG = "Bluetooth";
    public final static double THRESHOLD_ACCELEROMETER_MAX = 7.0;
    private final static double THRESHOLD_ACCELEROMETER_MIN = 1.0;

    private Handler handler = new Handler();
    private final static int INTERVAL = 10;

    private int counter = 0;
    private ArrayList<Double> x = new ArrayList<>(), y = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;

    private AcceptThread acceptThread;
    private ConnectedThread connectedThread;

    private DisplayMetrics displayMetrics;

    private boolean isX = true;

    private boolean connected;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.loading)
    AVLoadingIndicatorView loading;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

    private FirebaseAnalytics analytics;

    private Fragment currentFragment;

    private List<BluetoothEventListener> bluetoothListeners = new ArrayList<>();

    private double thresholdMin;
    private double thresholdMax;

    private boolean bluetoothLock;

    @Override
    public void setTitle(CharSequence title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    public void setStatusBarColor(int color) {
        Window window = getWindow();

        window.setStatusBarColor(getResources().getColor(color));
    }

    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }

    public void toggleArrow(boolean visible) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(visible);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "Discovered device: " + deviceName + " with MAC: " + deviceHardwareAddress);
            }
        }
    };

    BluetoothDeviceAdapter.OnDeviceClickListener bluetoothListener = device -> {
        toLanding();

        // start bluetooth host
        acceptThread = new AcceptThread(device);
        acceptThread.start();
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        App.setMainActivity(this);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        analytics = FirebaseAnalytics.getInstance(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        initBluetooth();

        toSplash();

        hideToolbar();

        checkForUpdates();

        prepareBluetoothData();
    }

    private void prepareBluetoothData() {
        thresholdMin = calculateThreshold(DEGREES_MAX);
        thresholdMax = calculateThreshold(DEGREES_MAX);
    }

    private static double calculateThreshold(int degrees) { // 3 degrees - ?
        // 90 degrees - MAX_THRESHOLD (7.0)
        double thresholdValue = degrees * THRESHOLD_ACCELEROMETER_MAX / 90.0;
        Log.i(BLUETOOTH_TAG, "Threshold value: " + thresholdValue);
        return thresholdValue;
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.e(TAG, "Bluetooth is not supported!");
            stopLoading();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            stopLoading();
            return;
        }
    }

    public List<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "Paired device: " + deviceName + " with MAC: " + deviceHardwareAddress);
            }
        }

        return new ArrayList<>(pairedDevices);
    }

    public BluetoothDeviceAdapter.OnDeviceClickListener getBluetoothListener() {
        return bluetoothListener;
    }

    private void clear() {
        x.clear();
        y.clear();

        counter = 0;
    }

    @Override
    public void onBackPressed() {

        if (currentFragment instanceof ResultScreen) {
            toLanding();
            return;
        }

        if (currentFragment instanceof LandingFragment || currentFragment instanceof BluetoothSetupFragment) {
            finish();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        stopBluetooth();
    }

    private DecimalFormat df = new DecimalFormat("#.00");

    private void update() {
        if (counter >= PACKAGE_SIZE * 2) {
            int realWidth = displayMetrics.widthPixels; // 1920
            int realHeight = displayMetrics.heightPixels; // 1080

            double[] xValues = new double[this.x.size()];
            for (int i = 0; i < this.x.size(); i++) {
                xValues[i] = this.x.get(i);//(double) adjust(this.x.get(i).floatValue(), realWidth, true);
            }

            double[] yValues = new double[this.y.size()];
            for (int i = 0; i < this.y.size(); i++) {
                yValues[i] = this.y.get(i);//(double) adjust(this.y.get(i), realHeight, false);
            }

            sendToBluetoothListeners(xValues, yValues);

            clear();
        }
    }

    private void sendToBluetoothListeners(double[] xValues, double[] yValues) {
        if (bluetoothListeners != null && bluetoothListeners.size() > 0) {
            for (int i = 0; i < xValues.length; i++) {
                for (BluetoothEventListener listener : bluetoothListeners) {
                    double currentX = xValues[i];
                    double currentY = yValues[i];

                    if (currentX < thresholdMin && currentX > -thresholdMin && currentY < thresholdMin && currentY > -thresholdMin) {
                        Log.i(BLUETOOTH_TAG, "onCenter");
                        bluetoothLock = false;
                        listener.onCenter();
                    }

                    if (bluetoothLock) {
                        return;
                    }

                    if (bluetoothLock) {
                        return;
                    }

                    if (currentX > thresholdMax && currentY < thresholdMax && currentY > -thresholdMax) { // left
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onLeft");
                        listener.onLeft();
                    } else if (currentX > thresholdMax && currentY > thresholdMax) { // bottom left
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onBottomLeft");
                        listener.onBottomLeft();
                    } else if (currentX > thresholdMax && currentY < -thresholdMax) { // top left
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onTopLeft");
                        listener.onTopLeft();
                    } else if (currentX < -thresholdMax && currentY < thresholdMax && currentY > -thresholdMax) { // right
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onRight");
                        listener.onRight();
                    } else if (currentX < -thresholdMax && currentY > thresholdMax) { // bottom right
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onBottomRight");
                        listener.onBottomRight();
                    } else if (currentX < -thresholdMax && currentY < -thresholdMax) { // top right
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onTopRight");
                        listener.onTopRight();
                    } else if (currentY > thresholdMax && currentX < thresholdMax && currentX > -thresholdMax) { // bottom
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onBottom");
                        listener.onBottom();
                    } else if (currentY < -thresholdMax && currentX < thresholdMax && currentX > -thresholdMax) { // top
                        bluetoothLock = true;
                        Log.i(BLUETOOTH_TAG, "onTop");
                        listener.onTop();
                    }


                }
            }
        }
    }

    public void registerBluetoothListener(BluetoothEventListener listener) {
        bluetoothListeners.add(listener);
    }

    public void unregisterBluetoothListener(BluetoothEventListener listener) {
        bluetoothListeners.remove(listener);
    }

    private void addValue(double value) {
        if (isX) {
            this.x.add(Double.valueOf(df.format(value)));
        } else {
            this.y.add(Double.valueOf(df.format(value)));
        }

        counter++;
    }

    private float adjust(double paramToAdjust, double maxValue, boolean inverse) { // 7 - xResolutionMax (e.g. 1920), 3 - y?
        //    7-1920
        //    paramToAdjust-x
        double value = (maxValue / 2) + (float) (paramToAdjust * maxValue / (Utils.THRESHOLD_ACCELEROMETER_MAX * 2));
        return inverse ? (float) Math.abs(maxValue - value) : (float) value;
    }


    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                initBluetooth();
            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothSocket mmSocket;

        public AcceptThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("0cbb85aa-7951-41a6-b891-b2ee53960860"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }

            mmSocket = tmp;
        }

        public void run() {
            while (true) {
                try {
                    mmSocket.connect();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    stopBluetooth();
                    break;
                }

                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(mmSocket);
                break;
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        runOnUiThread(() -> {
            stopLoading();
            connected = true;
        });

        connectedThread = new ConnectedThread(socket);
        handler.post(updateRunnable);
    }

    @Override
    public void read() {
        if (connectedThread != null) {
            connectedThread.read();
            update();
        }
    }

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!connected) {
                return;
            }

            read();
            if (handler != null) {
                handler.postDelayed(updateRunnable, INTERVAL);
            }
        }
    };

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }

            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data");

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);

                stopBluetooth();
            }
        }

        public void read() {
            try {
                DataInputStream dataInputStream = new DataInputStream(mmInStream);
                for (int i = 0; i < PACKAGE_SIZE * 2; i++) {
                    double value = dataInputStream.readDouble();
                    Log.i(TAG, isX ? "X: " : "Y: " + value);

                    addValue(value);
                    if (i == PACKAGE_SIZE - 1) {
                        isX = false;
                    }
                }

                isX = true;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error occurred when sending data");

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);

                stopBluetooth();
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void stopBluetooth() {
        if (acceptThread != null) {
            acceptThread.cancel();
        }

        if (connectedThread != null) {
            connectedThread.cancel();
        }

        connected = false;

        if (handler != null) {
            handler.removeCallbacks(updateRunnable);
        }

        Log.i(TAG, "Bluetooth connection stopped");
    }


    public void startLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        loading.setVisibility(View.GONE);
    }

    private <T extends Fragment> void setFragment(final T fragment) {
        runOnUiThread(() -> {
            stopLoading();

            currentFragment = fragment;

            final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            // TODO custom transaction animations
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            fragmentTransaction.replace(R.id.mainFragment, fragment, TAG_MAIN);
            fragmentTransaction.commitAllowingStateLoss();

        });
    }

    public void toSplash() {
        setFragment(SplashFragment.newInstance());
    }

    public void toLogin() {
        setFragment(LoginFragment.newInstance());
    }

    public void toNameScreen() {
        setFragment(NameFragment.newInstance());
    }

    public void toAgeScreen() {
        setFragment(AgeFragment.newInstance());
    }

    public void toTimeScreen() {
        setFragment(TimeFragment.newInstance());
    }

    public void toLanding() {
        setFragment(LandingFragment.newInstance());
    }

    public void toInfo() {
        setFragment(InfoFragment.newInstance());
    }

    public void toStatistics() {
        setFragment(StatisticsFragment.newInstance());
    }


    public void toSettings() {
        setFragment(SettingsFragment.newInstance());
    }

    public void toInstructionFragment(InstructionFragment.Test test) {
        setFragment(InstructionFragment.newInstance(test));
    }

    public void toAttentionDistributionTest() {
        setFragment(AttentionDistributionTestFragment.newInstance());
    }

    public void toReactionTest() {
        setFragment(ReactionTestFragment.newInstance());
    }

    public void toStart() {
        setFragment(StartFragment.newInstance());
    }

    public void toComplexMotorReactionTest() {
        setFragment(ComplexMotorReactionTestFragment.newInstance());
    }

    public void toFocusingTest() {
        setFragment(FocusingTestFragment.newInstance());
    }

    public void toReactionResults(Bundle args) {
        setFragment(ReactionResultsFragment.newInstance(args));
    }

    public void toAttentionDistributionResults(Bundle args) {
        setFragment(AttentionDistributionResultsFragment.newInstance(args));
    }

    public void toFocusingResults(Bundle args) {
        setFragment(FocusingResultsFragment.newInstance(args));
    }

    public void toComplexMotorReactionResults(Bundle args) {
        setFragment(ComplexMotorReactionResultsFragment.newInstance(args));
    }

    public void toStatisticsReaction(StatisticsResult results) {
        setFragment(StatisticsReactionFragment.newInstance(results));
    }

    public void toStatisticsComplex(StatisticsResult results) {
        setFragment(StatisticsComplexFragment.newInstance(results));
    }

    public void toStatisticsFocusing(StatisticsResult results) {
        setFragment(StatisticsFocusingFragment.newInstance(results));
    }

    public void toStatisticsDistribution(StatisticsResult results) {
        setFragment(StatisticsDistributionFragment.newInstance(results));
    }

    public void toBluetoothFragment() {
        setFragment(BluetoothSetupFragment.newInstance());
    }
}