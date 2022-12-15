package com.example.qrcodescanner

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*


class MainActivity : AppCompatActivity() {
    companion object{
        private  const val CAMERA_REQ = 101
    }
//    private lateinit var cardRescan: CardView
//    private lateinit var cardCopy: CardView
//    private lateinit var cardShare: CardView
    private lateinit var codeScanner: CodeScanner
//    private lateinit var textQR : TextView
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    val cardRescan = findViewById<CardView>(R.id.cardRescan)
    val cardCopy = findViewById<CardView>(R.id.cardCopy)
    val cardShare = findViewById<CardView>(R.id.cardShare)

        getPermission()
        QRScanner()
        reScan()

        /*Read Menus Status*/
        cardRescan.setOnClickListener {
            reScan()
        }
        cardCopy.setOnClickListener {
            copyQR()
        }
        cardShare.setOnClickListener {
            shareQR()
        }
    }

    /*Main Functions*/
    private fun getPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQ)
        }
    }
    private fun QRScanner(){
        val textQR = findViewById<TextView>(R.id.textQR)
        val scanView = findViewById<CodeScannerView>(R.id.scanView)
        codeScanner  = CodeScanner(this, scanView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                runOnUiThread{
                    textQR.text = it.text
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                }
            }
            scanView.setOnClickListener {
                reScan()
            }
        }
    }

    /*Menu Handler Functions*/
    private fun reScan(){
        val textQR = findViewById<TextView>(R.id.textQR)
        codeScanner.startPreview()
        textQR.text="scanning..."
    }
    private fun copyQR(){
        val textQR = findViewById<TextView>(R.id.textQR)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", textQR.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, " - ${textQR.text} - is copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    private fun shareQR(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            val textQR = findViewById<TextView>(R.id.textQR)
            putExtra(Intent.EXTRA_TEXT, textQR.text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /*Release Resource*/
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}