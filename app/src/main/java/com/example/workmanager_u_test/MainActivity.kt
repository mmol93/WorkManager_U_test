package com.example.workmanager_u_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.workmanager_u_test.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object{
        const val KEY_COUNT_VALUE = "key"
    }
    private lateinit var binder : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binder.button.setOnClickListener {
//            setOnTimeWorkRequest()
            setPeriodicWorkRequest()
        }
    }
    // 해당 조건(Constraints 또는 다른 사용자 지정 조건)에 따라 한 번만 발동한다
    private fun setOnTimeWorkRequest(){
        val workManager = WorkManager.getInstance(applicationContext)

        // input data 설정 - 키, 값 구성으로 데이터를 넣을 수 있다
        val data = Data.Builder().putInt(KEY_COUNT_VALUE, 125).build()

        // WorkManager에서 사용가능한 Constraints
        // Constraints: WorkManager에서 사용가능한 제약조건
        // setRequiresCharging: 기기가 현재 충전중일 때 WorkRequest를 할 수 있게 한다
        // https://developer.android.com/reference/kotlin/androidx/work/Constraints.Builder
        val constraints = Constraints.Builder().setRequiresCharging(true)
            // Constraints는 2개 이상도 설정할 수 있다
                // setRequiredNetworkType: 네트워크에 대한 제약조건
                // 여기선 인터넷에 연결되었을 때 실시함을 알린다
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // WorkManager의 실행은 WorkManager 서비스로 할 수 있다
        // OneTimeWorkRequest: 한 번만 실행한다
        val uploadRequest = OneTimeWorkRequest.Builder(UploadingWorker::class.java)
            // 설정한 Constraints를 넣는다
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        // 다른 workRequest를 추가하고 싶을 때도 똑같이 workRequest를 정의해주며된다
        val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()
        val compressingRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
            .build()
        val downloadingRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .build()

        // 병렬 작업용 workRequest 정의
        // 병렬 작업용 workRequest는 mutableListOf를 이용한다
        val paralleWorks = mutableListOf<OneTimeWorkRequest>()
        // 어자피 병렬 작업은 동시에 같이 진행되기 때문에 add 하는 순서가 상관이 없다
        paralleWorks.add(downloadingRequest)
        paralleWorks.add(filteringRequest)

        // enqueue를 사용하여 workRequest를 workManager에 제출한다
        WorkManager.getInstance(applicationContext)
            // begin -> then 입력 "순서대로" 일이 진행된다
            // 병렬(paralle) 작업 또한 workManager에 등록하는 방법은 동일하다
            // biginWith뿐 아니라 then에 병렬 작업을 넣는거도 물론 가능하다
            .beginWith(paralleWorks).then(compressingRequest).then(uploadRequest)
            // begin으로 시작해서 then으로 끝났다면 enqueue에 공백이 들어간다
            .enqueue()

        // WorkManager 인스턴스의 id를 이용하여 LiveData로 observe를 할 수 있다
        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            // workManager의 상태를 observe 한다
            .observe(this, Observer {
                // state: Blocked -> Enqueued -> Running -> Succeeded
                binder.textView.text = it.state.name
                // Worker에서 보낸 값을 받는다
                if (it.state.isFinished){
                    // put된 데이터를 가져올 준비(객체 생성)
                    val data = it.outputData
                    // UploadingWorker에서 보낸 putString 데이터를 가져온다
                    val message = data.getString(UploadingWorker.KEY_WORKER)
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setPeriodicWorkRequest(){
        // PeriodicWorkRequest의 경우 Builder에 Class 이외에 추가적인 매개변수가 필요하다
        // repeatInterval: 얼마만큼의 시간으로 반복할 것인가(최소 15분 이상 간격 필요)
        // TimeUnit: 시간의 단위를 결정한다
        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(DownloadWorker::class.java, 16, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
    }
}