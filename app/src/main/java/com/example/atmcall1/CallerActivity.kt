package com.example.atmcall1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.example.atmcall1.R
import com.example.atmcall1.UnknownCallerActivity

class CallerActivity : AppCompatActivity() {
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callStateListener: CallStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        callStateListener = CallStateListener()

        // 전화 상태 변화를 감지하기 위해 리스너 등록
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 앱 종료 시 리스너 해제
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }

    private inner class CallStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 전화가 왔을 때의 동작을 여기에 구현합니다.
                if (!isNumberKnown(phoneNumber)) {
                    // 모르는 번호로 전화가 왔을 때의 동작을 처리합니다.
                    // 예를 들어 다른 화면으로 전환하는 코드를 추가할 수 있습니다.
                    val intent = Intent(this@CallerActivity, UnknownCallerActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        private fun isNumberKnown(phoneNumber: String?): Boolean {
            // 여기에 모르는 번호인지 확인하는 로직을 구현합니다.
            // 필요한 경우 통화 기록 등을 사용하여 번호를 확인할 수 있습니다.
            // 이 예제에서는 모르는 번호로 전화가 왔다고 가정하고 항상 false를 반환합니다.
            return false
        }
    }
}
