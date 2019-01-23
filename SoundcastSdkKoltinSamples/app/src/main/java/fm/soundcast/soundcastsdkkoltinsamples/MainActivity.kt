package fm.soundcast.soundcastsdkkoltinsamples;

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson

import java.util.ArrayList

import fm.soundcast.soundcastsdk.interfaceplayer.MediaListener
import fm.soundcast.soundcastsdk.model.modeldata.DataParserXMLModel
import fm.soundcast.soundcastsdk.model.modeldata.ModelResponse
import fm.soundcast.soundcastsdk.model.modelview.RequestModel
import fm.soundcast.soundcastsdk.presenter.ISoundCastPresenter
import fm.soundcast.soundcastsdk.presenter.SoundCastPresenterImpl
import fm.soundcast.soundcastsdk.until.SoundCastManager
import fm.soundcast.soundcastsdk.view.ISoundCastViewListener

import fm.soundcast.soundcastsdk.until.Contanst.CHECKAUDIO
import fm.soundcast.soundcastsdk.until.Contanst.SECONDS
import fm.soundcast.soundcastsdk.until.Contanst.URLADVERTISEMENT
import fm.soundcast.soundcastsdk.until.Utils.TimeFormat
import java.lang.Exception

class MainActivity : Activity(), View.OnClickListener, ISoundCastViewListener, MediaListener {
    private var presenter: ISoundCastPresenter? = null
    private var startPlayerBtn: Button? = null
    private var stopPlayerBtn: Button? = null
    private var edtNet: EditText? = null
    private var edtSite: EditText? = null
    private var edTag: EditText? = null
    private var timeStart: TextView? = null
    private var timefinish: TextView? = null
    private var seekbarSong: SeekBar? = null
    private var arrayList: ArrayList<String>? = null
    private var adapter: ArrayAdapter<String>? = null
    private var listView: ListView? = null
    private var checkbox: CheckBox? = null
    private var checkboxMidscroll: CheckBox? = null
    private var manager: SoundCastManager? = null
    private val linkMP3PlayAudido = "https://demo-stg.soundcast.fm/assets/audio/going-blind-court_1.mp3"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        addControl()
        setOnClickListener()

    }

    private fun init() {
        if (presenter == null) {
            presenter = SoundCastPresenterImpl(this)
        }
    }

    private fun setOnClickListener() {
        startPlayerBtn!!.setOnClickListener(this)
        stopPlayerBtn!!.setOnClickListener(this)
        seekbarSong!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (manager!!.mediaPlayeAdvertisement == null && manager!!.mediaPlayAudio != null) {
                    manager!!.mediaPlayAudio.seekTo(seekBar.progress)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        manager!!.pause()
    }

    override fun onResume() {
        super.onResume()
        manager!!.start()
        hideKeyBoard()
    }

    private fun addControl() {
        startPlayerBtn = findViewById(R.id.startPlayerBtn) as Button
        stopPlayerBtn = findViewById(R.id.stopPlayerBtn) as Button
        edtNet = findViewById(R.id.edtNet) as EditText
        edtSite = findViewById(R.id.edtSite) as EditText
        edTag = findViewById(R.id.edTag) as EditText
        timefinish = findViewById(R.id.timefinish) as TextView
        timeStart = findViewById(R.id.timeStart) as TextView
        seekbarSong = findViewById(R.id.seekbarSong) as SeekBar
        listView = findViewById(R.id.listView) as ListView
        checkbox = findViewById(R.id.checkbox) as CheckBox
        checkboxMidscroll = findViewById(R.id.checkboxMidscroll) as CheckBox
        arrayList = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList!!)
        listView!!.adapter = adapter

        //Play Audio
        manager = SoundCastManager(this, linkMP3PlayAudido)
    }

    override fun midCroll(check: Boolean, second: Int) {
        CHECKAUDIO = check
        SECONDS = second
    }

    override fun onSuccessParserXML(data: DataParserXMLModel?) {
        runOnUiThread {
            if (data != null) {
                updateData(data)
            }
        }
    }

    override fun onErrorParserXML(data: String?) {
        runOnUiThread {
            setEnable()
            setBackGroundButtonPause()
            if (data != null) {
                Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
            }
        }

    }

   /* fun responeData(data: String?) {
        try {
            if (data != null) {
                val resultObject = Gson().fromJson<ModelResponse>(data, ModelResponse::class.java)
                TOKEN = resultObject.token
                presenter!!.sendGetVastXML(resultObject.creative.creativeView.url)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onSuccessSendGetURL(data: String) {
        runOnUiThread { responeData(data) }
    }
    */
    override fun onErrorSendGetURL(data: String?) {
        runOnUiThread {
            setEnable()
            setBackGroundButtonPause()
            if (data != null) {
                Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.startPlayerBtn -> if (manager!!.mediaPlayeAdvertisement != null && manager!!.mediaPlayeAdvertisement.isPlaying == true) {
                manager!!.skipAuio()
            } else if (manager!!.mediaPlayAudio != null && manager!!.mediaPlayAudio.isPlaying == true) {
                manager!!.pause()
            } else if (manager!!.mediaPlayAudio != null && manager!!.mediaPlayAudio.isPlaying == false) {
                manager!!.start()
            } else if (edtNet!!.text.toString().length > 0 && edtSite!!.text.toString().length > 0 && edTag!!.text.toString().length > 0) {
                if (checkbox!!.isChecked && checkboxMidscroll!!.isChecked == false) {
                    resetTime()
                    setBackGroundButtonPlay()
                    sendPresenter()
                } else if (checkbox!!.isChecked && checkboxMidscroll!!.isChecked) {
                    resetTime()
                    setBackGroundButtonPlay()
                    midCroll(true, 15)
                    sendPresenter()
                } else if (checkbox!!.isChecked == false && checkboxMidscroll!!.isChecked == false) {
                    resetTime()
                    setBackGroundButtonPlay()
                    sendPresenter()
                }
                else {
                    setBackGroundButtonPlay()
                    if (arrayList != null) {
                        arrayList!!.clear()
                    }
                    arrayList!!.add(getString(R.string.adcall))
                    arrayList!!.add(getString(R.string.Error))
                    arrayList!!.add(getString(R.string.audio))
                    adapter!!.notifyDataSetChanged()
                    manager!!.playmedias()
                }
            }
            R.id.stopPlayerBtn -> {
                cleanData()
                if (manager!!.mediaPlayeAdvertisement != null && manager!!.mediaPlayeAdvertisement.isPlaying == true) {
                    manager!!.skipAuio()
                } else if (manager!!.mediaPlayAudio != null && manager!!.mediaPlayAudio.isPlaying == true) {
                    resetTime()
                    manager!!.mediaPlayAudioRelease()
                    setBackGroundButtonPause()
                } else if (manager!!.mediaPlayAudio != null && manager!!.mediaPlayAudio.isPlaying == false) {
                    resetTime()
                    manager!!.mediaPlayAudioRelease()
                    setBackGroundButtonPause()
                }
            }
        }
    }

    override fun resetTime() {
        timeStart!!.text = getString(R.string.timeStart)
        seekbarSong!!.progress = 0
    }

    override fun setEnable() {
        startPlayerBtn!!.isEnabled = true
    }

    override fun setDisable() {
        startPlayerBtn!!.isEnabled = false
    }

    private fun sendPresenter() {
        val requestModel = RequestModel()
        requestModel.networkID = edtNet!!.text.toString()
        requestModel.siteID = edtSite!!.text.toString()
        requestModel.tagID = edTag!!.text.toString()
        requestModel.pageTitle = "NRJ"
        requestModel.pageDescription = "null"
        requestModel.keywords = "null"
        requestModel.pageUrl = "https%3A%2F%2Fdemo.soundcast.fm%2F"
        requestModel.tags = "null"
        requestModel.test = "true"
        setDisable()
        presenter!!.loadAd(requestModel)
    }


    public override fun onDestroy() {
        presenter!!.onDestroy()
        super.onDestroy()
    }

    fun updateData(model: DataParserXMLModel?) {
        try {
            if (model!!.link[0].toString() != null) {
                URLADVERTISEMENT = model.link[0]
                manager!!.playAdvertisement(model.link[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun setBackGroundButtonPlay() {
        startPlayerBtn!!.setBackgroundResource(R.drawable.play) /**/
    }

    override fun setBackGroundButtonPause() {
        startPlayerBtn!!.setBackgroundResource(R.drawable.pause)
    }

    override fun hideKeyBoard() {
        try {
            val imm = baseContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val focusedView = currentFocus
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun cleanData() {
        if (arrayList != null) {
            arrayList!!.clear()
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun callAd(start: Int, finish: Int) {
        if (arrayList != null) {
            arrayList!!.clear()
        }
        arrayList!!.add(getString(R.string.callAd))
        adapter!!.notifyDataSetChanged()
        seekbarSong!!.max = finish
        timeStart!!.text = TimeFormat(start)
        timefinish!!.text = TimeFormat(finish)
        seekbarSong!!.progress = start
    }

    override fun addStart() {
        arrayList!!.add(getString(R.string.start))
        adapter!!.notifyDataSetChanged()
    }

    override fun addFirstQuartile() {
        arrayList!!.add(getString(R.string.firstquartile))
        adapter!!.notifyDataSetChanged()
    }

    override fun addMidPoint() {
        arrayList!!.add(getString(R.string.midpoint))
        adapter!!.notifyDataSetChanged()
    }

    override fun addThirdQuartile() {
        arrayList!!.add(getString(R.string.thirdquartile))
        adapter!!.notifyDataSetChanged()
    }

    override fun addComplete() {
        arrayList!!.add(getString(R.string.complete))
        adapter!!.notifyDataSetChanged()
        listView!!.setSelection(arrayList!!.size)
    }

    override fun updateTimeAdvertisement(Start: Int) {
        timeStart!!.text = TimeFormat(Start)
        seekbarSong!!.progress = Start
    }

    override fun setTimePlayAudio(start: Int, finish: Int) {
        seekbarSong!!.max = finish
        timeStart!!.text = TimeFormat(start)
        timefinish!!.text = TimeFormat(finish)
        seekbarSong!!.progress = start
    }

    override fun updateTimePlayAudio(start: Int) {
        timeStart!!.text = TimeFormat(start)
        seekbarSong!!.progress = start
    }

    override fun hideProgress() {
    }

    override fun showProgress() {
    }
}
