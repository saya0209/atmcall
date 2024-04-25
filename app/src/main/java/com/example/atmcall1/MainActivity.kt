package com.example.atmcall1

// MainActivity.kt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var infoText: TextView
    private lateinit var button: Button
    private lateinit var mainImage: ImageView
    private lateinit var activationButton: Button
    private lateinit var UPButton: Button

    private var isSafeImageVisible = true
    private var isDetectionEnabled = true

    private var originalInfoText: String = ""
    private var originalBTNText: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activationButton = findViewById(R.id.BTN1)
        UPButton = findViewById(R.id.UPpageButton)

        activationButton.setOnClickListener {
            if (UPButton.visibility == View.INVISIBLE) {
                // 활성화 버튼을 클릭하면 UPButton을 보이도록 설정
                UPButton.visibility = View.VISIBLE
            } else {
                // 활성화 버튼을 다시 클릭하면 UPButton을 숨기도록 설정
                UPButton.visibility = View.INVISIBLE
            }
        }

        sharedPreferences = getPreferences(MODE_PRIVATE)

        infoText = findViewById(R.id.Infotext)
        button = findViewById(R.id.BTN1)
        mainImage = findViewById(R.id.MainImage)

        originalInfoText = infoText.text.toString()
        originalBTNText = button.text.toString()

        // 저장된 상태를 복원
        isDetectionEnabled = sharedPreferences.getBoolean("isDetectionEnabled", true)
        isSafeImageVisible = sharedPreferences.getBoolean("isSafeImageVisible", true)

        updateUI()

        button.setOnClickListener {
            // 상태 변경
            isDetectionEnabled = !isDetectionEnabled
            isSafeImageVisible = !isSafeImageVisible

            // UI 업데이트
            updateUI()

            // 상태 저장
            with(sharedPreferences.edit()) {
                putBoolean("isDetectionEnabled", isDetectionEnabled)
                putBoolean("isSafeImageVisible", isSafeImageVisible)
                apply()
            }
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // UPButton 클릭 시 UploadActivity로 이동
        UPButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI() {
        if (isDetectionEnabled) {
            infoText.text = "보이스피싱 감지중입니다."
            button.text = "보이스피싱 감지 비활성화"
        } else {
            infoText.text = originalInfoText
            button.text = originalBTNText
        }

        if (isSafeImageVisible) {
            mainImage.setImageResource(R.drawable.unactive)
        } else {
            mainImage.setImageResource(R.drawable.active)
        }

        UPButton.visibility = if (isDetectionEnabled) View.VISIBLE else View.INVISIBLE
    }
}


