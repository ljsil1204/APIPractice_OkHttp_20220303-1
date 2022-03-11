package com.neppplus.apipractice_okhttp_20220303

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.apipractice_okhttp_20220303.databinding.ActivityEditReplyBinding
import com.neppplus.apipractice_okhttp_20220303.datas.TopicData

class EditReplyActivity : BaseActivity() {

    lateinit var binding : ActivityEditReplyBinding

//    어느 토론 주제에서 온건지. + 무조건 선택한 진영도 있다!
    lateinit var mTopicData : TopicData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_reply)
        mTopicData = intent.getSerializableExtra("topic") as TopicData
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.btnPostReply.setOnClickListener {

//            앱에서 검사 > 입력문구가 5자 미만이면 토스트, 함수 강제종료

            val inputContent = binding.edtReplyContent.text.toString()

            if (inputContent.length < 5) {
                Toast.makeText(mContext, "5자 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//          입력한 내용을  > 서버  API 호출

        }

    }

    override fun setValues() {

        binding.txtTopicTitle.text =  mTopicData.title
        binding.txtSideTitle.text = mTopicData.mySelectedSide!!.title

    }

}