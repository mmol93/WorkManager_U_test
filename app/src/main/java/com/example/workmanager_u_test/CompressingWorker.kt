package com.example.workmanager_u_test

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

// WorkManager: 앱이 종료되거나 기기가 다시 시작되더라도 실행될 것으로 예상되는 신뢰할 수 있는 비동기 작업을 쉽게 예약할 수 있게 해주는 API
// Work를 상속받기 위해선 Context와 WorkerParameters가 필요하다
class CompressingWorker(context: Context, workPara:WorkerParameters):Worker(context, workPara) {
    // 실시할 내용을 doWork 메서드 안에 정의
    // doWork()는 백그라라운드 스레드에서 비동기적으로 실행됨
    override fun doWork(): Result {
        try{
            for (i in 0 until 300){
                Log.d("compressing", "compressing: $i")
            }
            return Result.success()
        }catch (e:Exception){
            return Result.failure()
        }
    }
}