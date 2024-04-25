package com.example.atmcall1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.atmcall1.R

class UnknownCallerActivity : AppCompatActivity() {

    private val SEND_SMS_REQUEST_CODE = 1
    private val APP_SETTINGS_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unknown_caller)

        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)

        // 전화번호를 화면에 표시
        phoneNumber?.let {
            val phoneNumberTextView: TextView = findViewById(R.id.phoneNumberTextView)
            phoneNumberTextView.text = phoneNumber
        }

        // 신고 버튼 클릭 이벤트 처리
        val reportButton: Button = findViewById(R.id.ReportButton)
        reportButton.setOnClickListener {
            // SMS 전송 권한 확인
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    SEND_SMS_REQUEST_CODE
                )
            } else {
                // 권한이 있는 경우 SMS 전송
                sendReportSMS()
            }
        }

        // 홈 버튼 클릭 이벤트 처리
        val homeButton: Button = findViewById(R.id.HomeButton)
        homeButton.setOnClickListener {
            onHomeButtonClick()
        }
    }

    private fun sendReportSMS() {
        val phoneNumber = "112" // 기본 메시지 전송 번호를 112로 설정
        val message = "보이스피싱이 의심갑니다." // 보낼 메시지 내용을 변경 가능

        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
        }

        try {
            startActivity(smsIntent)
            finish()
        } catch (e: Exception) {
            // 메세지 전송 실패 시 메세지 홈 화면으로 이동
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeIntent)
            finish()
        }
    }

    // 홈 버튼 클릭 이벤트 핸들러
    private fun onHomeButtonClick() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SEND_SMS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SMS 전송 권한이 허용된 경우 SMS 전송
                sendReportSMS()
            } else {
                // SMS 전송 권한이 거부된 경우 애플리케이션 종료
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == APP_SETTINGS_REQUEST_CODE) {
            // 애플리케이션 설정 화면에서 돌아온 경우
            // SMS 전송 권한 다시 확인
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // SMS 전송 권한이 거부된 경우 애플리케이션 종료
                finish()
            } else {
                // SMS 전송 권한이 허용된 경우 SMS 전송
                sendReportSMS()
            }
        }
    }

    companion object {
        const val EXTRA_PHONE_NUMBER = "phone_number"
    }
}

