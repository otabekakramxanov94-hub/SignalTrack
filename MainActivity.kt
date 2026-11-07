package uz.signaltrack

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.print.PrintAttributes
import android.print.PrintManager
import android.telephony.SubscriptionManager
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var map: org.osmdroid.views.MapView
    private lateinit var tvStatus: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val PERMS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.POST_NOTIFICATIONS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        tvStatus = findViewById(R.id.tvStatus)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(13.0)
        map.controller.setCenter(GeoPoint(41.2995, 69.2401)) // Toshkent

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMS, 1)
        }

        fillSimSpinner()

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            val sp = findViewById<Spinner>(R.id.spSim)
            val op = sp.selectedItem?.toString() ?: ""
            val i = Intent(this, SignalService::class.java).putExtra("op", op)
            ContextCompat.startForegroundService(this, i)
            toast("O'lchash boshlandi. Ekranni o'chirishingiz mumkin.")
        }
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            startService(Intent(this, SignalService::class.java).setAction(SignalService.ACTION_STOP))
            toast("To'xtatildi. Endi Hisobot tugmasini bosing.")
        }
        findViewById<Button>(R.id.btnCall).setOnClickListener {
            val num = findViewById<EditText>(R.id.etPhone).text.toString().trim()
            if (num.isEmpty()) { toast("Raqam kiriting (operator bepul raqami)"); return@setOnClickListener }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$num")))
            } else toast("CALL_PHONE ruxsati kerak")
        }
        findViewById<Button>(R.id.btnHtml).setOnClickListener {
            tvStatus.text = "Hisobot tayyorlanmoqda..."
            Thread {
                try {
                    val f = Report.buildHtml(this)
                    runOnUiThread {
                        tvStatus.text = "Tayyor: " + f.name
                        shareFile(f, "text/html")
                    }
                } catch (e: Exception) { runOnUiThread { toast("Xato: " + e.message) } }
            }.start()
        }
        findViewById<Button>(R.id.btnPdf).setOnClickListener {
            Thread {
                try {
                    val f = Report.buildHtml(this)
                    runOnUiThread { printHtml(f) }
                } catch (e: Exception) { runOnUiThread { toast("Xato: " + e.message) } }
            }.start()
        }
        handler.post(statusLoop)
    }

    private fun fillSimSpinner() {
        val sp = findViewById<Spinner>(R.id.spSim)
        val names = mutableListOf<String>()
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
                val sm = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                for (s in sm.activeSubscriptionInfoList) names.add(s.displayName.toString())
            }
        } catch (_: Exception) {}
        if (names.isEmpty()) names.add("SIM (standart)")
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
    }

    private val statusLoop = object : Runnable {
        override fun run() {
            val d = File(filesDir, "meas")
            var n = 0
            d.listFiles()?.forEach { n += it.readLines().size }
            tvStatus.text = if (SignalService.running)
                "O'lchash davomida... nuqtalar: $n. Boshqa operator SIMini qo'yib, qayta Boshlang." else "Nuqtalar: $n"
            handler.postDelayed(this, 2000)
        }
    }

    private fun printHtml(f: File) {
        val wv = WebView(this)
        wv.loadUrl("file://" + f.absolutePath)
        wv.setInitialScale(100)
        wv.visibility = View.GONE
        val root = (findViewById<View>(android.R.id.content) as ViewGroup)
        root.addView(wv)
        val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val attrs = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()
        pm.print("SignalTrack hisobot", wv.createPrintDocumentAdapter("hisobot"), attrs)
    }

    private fun shareFile(f: File, mime: String) {
        val uri = FileProvider.getUriForFile(this, packageName + ".fileprovider", f)
        val i = Intent(Intent.ACTION_SEND).apply {
            type = mime; putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(i, "Hisobotni yuborish"))
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()

    override fun onResume() { super.onResume(); map.onResume() }
    override fun onPause() { super.onPause(); map.onPause() }
}
