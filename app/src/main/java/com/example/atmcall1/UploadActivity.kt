package com.example.atmcall1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UploadActivity : AppCompatActivity() {

    private val REQUEST_PICK_FILE = 1
    private lateinit var selectFileButton: Button
    private lateinit var fileNameTextView: TextView
    private lateinit var uploadButton: Button
    private lateinit var HomeButton: Button
    private lateinit var uploadProgressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        selectFileButton = findViewById(R.id.selectFileButton)
        fileNameTextView = findViewById(R.id.fileNameTextView)
        uploadButton = findViewById(R.id.uploadButton)

        selectFileButton.setOnClickListener {
            // 파일 선택 다이얼로그 열기
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // 모든 파일 타입 허용
            startActivityForResult(intent, REQUEST_PICK_FILE)
        }
        uploadProgressBar = findViewById(R.id.uploadProgressBar)

        uploadButton.setOnClickListener {
            // 파일 업로드 로직을 구현

            // 프로그래스 바 보이기
            uploadProgressBar.visibility = View.VISIBLE

            Handler().postDelayed({

            // 업로드한 파일 이름 가져오기
            val uploadedFileName = fileNameTextView.text.toString()
            val fileNameWithoutExtension = uploadedFileName.replaceFirst("[.][^.]+$".toRegex(), "")

            Handler().postDelayed({
                startActivity(intent) // 화면 전환 코드
            }, 4000) // 4초 딜레이

            // 파일 이름이 "보이스피싱"인 경우
            if (fileNameWithoutExtension.equals("보이스피싱", ignoreCase = true)) {
                val intent = Intent(this, UnknownCallerActivity::class.java)
                startActivity(intent)
            }
            // 파일 이름이 "안전"인 경우
            else if (fileNameWithoutExtension.equals("안전", ignoreCase = true)) {
                val intent = Intent(this, SafeActivity::class.java)
                startActivity(intent)
            }
            // 그 외의 경우
            else {
                Toast.makeText(this, "지원하지 않는 파일 입니다.", Toast.LENGTH_SHORT).show()
            }
                uploadProgressBar.visibility = View.INVISIBLE
            }, 4000) // 4초후 숨김
        }
        //홈버튼
        val homeButton = findViewById<Button>(R.id.HomeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null) {
            // 선택한 파일의 URI 가져오기
            val selectedFileUri: Uri? = data.data

            // 파일 이름만 추출

            val fileNameArray = selectedFileUri?.path?.split("/")
            val fileName = fileNameArray?.lastOrNull() ?: ""

            // 파일 이름을 TextView에 표시
            fileNameTextView.text = "$fileName"
        }
    }
}
