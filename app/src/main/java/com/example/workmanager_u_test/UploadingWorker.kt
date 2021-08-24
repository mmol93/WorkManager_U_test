package com.example.workmanager_u_test

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

// WorkManager: 앱이 종료되거나 기기가 다시 시작되더라도 실행될 것으로 예상되는 신뢰할 수 있는 비동기 작업을 쉽게 예약할 수 있게 해주는 API
// Work를 상속받기 위해선 Context와 WorkerParameters가 필요하다
class UploadingWorker(context: Context, workPara:WorkerParameters):Worker(context, workPara) {
    companion object{
        const val KEY_WORKER = "key_worker"
    }

    // 실시할 내용을 doWork 메서드 안에 정의
    // doWork()는 백그라라운드 스레드에서 비동기적으로 실행됨
    override fun doWork(): Result {
        try{
            val count = inputData.getInt(MainActivity.KEY_COUNT_VALUE, 0)

            for (i in 0 until count){
                Log.d("test", "uploading: $i")
            }
            // Work가 끝난 시점을 같이 inputData로 반환해준다
            val time = SimpleDateFormat("dd/M/yyyy hh:mm")
            val currentData = time.format(Date())
            val outputData = Data.Builder().putString(KEY_WORKER, currentData).build()

            // 성공 or 실패를 확인할 수 있다
            // success 안에 putString을 통해 데이터를 같이 반환할 수 있다 -> MainActivity에서 활용
            return Result.success(outputData)
        }catch (e:Exception){
            return Result.failure()
        }
    }
}