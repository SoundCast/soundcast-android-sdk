package fm.soundcast.soundcastsdkjava;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import fm.soundcast.soundcastsdk.interfaceplayer.MediaListener;
import fm.soundcast.soundcastsdk.model.modeldata.DataParserXMLModel;
import fm.soundcast.soundcastsdk.model.modeldata.ModelResponse;
import fm.soundcast.soundcastsdk.model.modelview.RequestModel;
import fm.soundcast.soundcastsdk.presenter.ISoundCastPresenter;
import fm.soundcast.soundcastsdk.presenter.SoundCastPresenterImpl;
import fm.soundcast.soundcastsdk.until.SoundCastManager;
import fm.soundcast.soundcastsdk.view.ISoundCastViewListener;
import fms.soundcast.soundcastsdkjava.R;

import static fm.soundcast.soundcastsdk.until.Contanst.CHECKAUDIO;
import static fm.soundcast.soundcastsdk.until.Contanst.SECONDS;
import static fm.soundcast.soundcastsdk.until.Contanst.URLADVERTISEMENT;
import static fm.soundcast.soundcastsdk.until.Utils.TimeFormat;

public class MainActivity extends Activity implements View.OnClickListener, ISoundCastViewListener, MediaListener {
    private ISoundCastPresenter presenter;
    private Button startPlayerBtn, stopPlayerBtn;
    private EditText edtNet, edtSite, edTag;
    private TextView timeStart, timefinish;
    private SeekBar seekbarSong;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private CheckBox checkbox, checkboxMidscroll;
    private SoundCastManager manager;
    private String linkMP3PlayAudido = "https://demo-stg.soundcast.fm/assets/audio/going-blind-court_1.mp3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        addControl();
        setOnClickListener();

    }

    private void init() {
        if (presenter == null) {
            presenter = new SoundCastPresenterImpl(this);
        }
    }

    private void setOnClickListener() {
        startPlayerBtn.setOnClickListener(this);
        stopPlayerBtn.setOnClickListener(this);
        seekbarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (manager.mediaPlayeAdvertisement == null && manager.mediaPlayAudio != null) {
                    manager.mediaPlayAudio.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.start();
        hideKeyBoard();
    }

    private void addControl() {
        startPlayerBtn = (Button) findViewById(R.id.startPlayerBtn);
        stopPlayerBtn = (Button) findViewById(R.id.stopPlayerBtn);
        edtNet = (EditText) findViewById(R.id.edtNet);
        edtSite = (EditText) findViewById(R.id.edtSite);
        edTag = (EditText) findViewById(R.id.edTag);
        timefinish = (TextView) findViewById(R.id.timefinish);
        timeStart = (TextView) findViewById(R.id.timeStart);
        seekbarSong = (SeekBar) findViewById(R.id.seekbarSong);
        listView = (ListView) findViewById(R.id.listView);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkboxMidscroll = (CheckBox) findViewById(R.id.checkboxMidscroll);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        //Play Audio
        manager = new SoundCastManager(this, linkMP3PlayAudido);
    }

    @Override
    public void midCroll(boolean check, int second) {
        CHECKAUDIO = check;
        SECONDS = second;
    }

    @Override
    public void onSuccessParserXML(final DataParserXMLModel data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data != null) {
                    updateData(data);
                }
            }
        });
    }

    @Override
    public void onErrorParserXML(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setEnable();
                setBackGroundButtonPause();
                if (data != null) {
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }


    @Override
    public void onErrorSendGetURL(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setEnable();
                setBackGroundButtonPause();
                if (data != null) {
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startPlayerBtn:
                if (manager.mediaPlayeAdvertisement != null && manager.mediaPlayeAdvertisement.isPlaying() == true) {
                    manager.skipAuio();
                } else if (manager.mediaPlayAudio != null && manager.mediaPlayAudio.isPlaying() == true) {
                    manager.pause();
                } else if (manager.mediaPlayAudio != null && manager.mediaPlayAudio.isPlaying() == false) {
                    manager.start();
                } else if (edtNet.getText().toString().length() > 0 && edtSite.getText().toString().length() > 0 && edTag.getText().toString().length() > 0) {
                    if (checkbox.isChecked() && checkboxMidscroll.isChecked() == false) {
                        resetTime();
                        setBackGroundButtonPlay();
                        sendPresenter();
                    } else if (checkbox.isChecked() && checkboxMidscroll.isChecked()) {
                        resetTime();
                        setBackGroundButtonPlay();
                        midCroll(true, 15);
                        sendPresenter();
                    } else if (checkbox.isChecked() == false && checkboxMidscroll.isChecked() == false) {
                        resetTime();
                        setBackGroundButtonPlay();
                        sendPresenterAD();
                    }
                    else {
                        setBackGroundButtonPlay();
                        if (arrayList != null) {
                            arrayList.clear();
                        }
                        arrayList.add(getString(R.string.adcall));
                        arrayList.add(getString(R.string.Error));
                        arrayList.add(getString(R.string.audio));
                        adapter.notifyDataSetChanged();
                        manager.playmedias();
                    }
                }
                break;
            case R.id.stopPlayerBtn:
                cleanData();
                if (manager.mediaPlayeAdvertisement != null && manager.mediaPlayeAdvertisement.isPlaying() == true) {
                    manager.skipAuio();
                } else if (manager.mediaPlayAudio != null && manager.mediaPlayAudio.isPlaying() == true) {
                    resetTime();
                    manager.mediaPlayAudioRelease();
                    setBackGroundButtonPause();
                } else if (manager.mediaPlayAudio != null && manager.mediaPlayAudio.isPlaying() == false) {
                    resetTime();
                    manager.mediaPlayAudioRelease();
                    setBackGroundButtonPause();
                }
                break;
        }
    }

    @Override
    public void resetTime() {
        timeStart.setText(getString(R.string.timeStart));
        seekbarSong.setProgress(0);
    }

    @Override
    public void setEnable() {
        startPlayerBtn.setEnabled(true);
    }

    @Override
    public void setDisable() {
        startPlayerBtn.setEnabled(false);
    }

    private void sendPresenter() {
        RequestModel requestModel = new RequestModel();
        requestModel.networkID = edtNet.getText().toString();
        requestModel.siteID = edtSite.getText().toString();
        requestModel.tagID = edTag.getText().toString();
        requestModel.pageTitle = "NRJ";
        requestModel.pageDescription = "null";
        requestModel.keywords = "null";
        requestModel.pageUrl = "https%3A%2F%2Fdemo-stg.soundcast.fm%2F";
        requestModel.tags = "null";
        requestModel.test = "true";
        setDisable();
        presenter.loadAd(requestModel);
    }

    private void sendPresenterAD() {
        RequestModel requestModel = new RequestModel();
        requestModel.networkID = edtNet.getText().toString();
        requestModel.siteID = edtSite.getText().toString();
        requestModel.tagID = edTag.getText().toString();
        requestModel.pageTitle = "NRJ";
        requestModel.pageDescription = "null";
        requestModel.keywords = "null";
        requestModel.pageUrl = "https%3A%2F%2Fdelivery-stg.api.soundcast.fm%2F";
        requestModel.tags = "null";
        setDisable();
        presenter.loadAd(requestModel);
    }
    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    public void updateData(final DataParserXMLModel model) {
        try {
            if (model.link.get(0).toString() != null) {
                URLADVERTISEMENT = model.link.get(0);
                manager.playAdvertisement(model.link.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBackGroundButtonPlay() {
        startPlayerBtn.setBackgroundResource(R.drawable.play); /**/
    }

    @Override
    public void setBackGroundButtonPause() {
        startPlayerBtn.setBackgroundResource(R.drawable.pause);
    }

    @Override
    public void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focusedView = getCurrentFocus();
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanData() {
        if (arrayList != null) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void callAd(int start, int finish) {
        if (arrayList != null) {
            arrayList.clear();
        }
        arrayList.add(getString(R.string.callAd));
        adapter.notifyDataSetChanged();
        seekbarSong.setMax(finish);
        timeStart.setText(TimeFormat(start));
        timefinish.setText(TimeFormat(finish));
        seekbarSong.setProgress(start);
    }

    @Override
    public void addStart() {
        arrayList.add(getString(R.string.start));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addFirstQuartile() {
        arrayList.add(getString(R.string.firstquartile));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addMidPoint() {
        arrayList.add(getString(R.string.midpoint));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addThirdQuartile() {
        arrayList.add(getString(R.string.thirdquartile));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addComplete() {
        arrayList.add(getString(R.string.complete));
        adapter.notifyDataSetChanged();
        listView.setSelection(arrayList.size());
    }

    @Override
    public void updateTimeAdvertisement(int Start) {
        timeStart.setText(TimeFormat(Start));
        seekbarSong.setProgress(Start);
    }

    @Override
    public void setTimePlayAudio(int start, int finish) {
        seekbarSong.setMax(finish);
        timeStart.setText(TimeFormat(start));
        timefinish.setText(TimeFormat(finish));
        seekbarSong.setProgress(start);
    }

    @Override
    public void updateTimePlayAudio(int start) {
        timeStart.setText(TimeFormat(start));
        seekbarSong.setProgress(start);
    }
}
