package com.tuncayavci.workmanagerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    //constraints for Work -> only wi_fi etc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //update database with Shared Preferences
        //store int in shared preferences and increase the value by one

        val data = Data.Builder().putInt("intKey",1).build()

        val constraints = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            //.setRequiresBatteryNotLow(true)
            .build()
        //OneTimeWorkRequestBuilder() -> can be used in two context
        //1-chaining process -- binding process
        //2- After opening the app for the first time, do the strain after 25 minutes etc. in the background
        // 2. part is also done with PeriodicWorkRequestBuilder

        /*
        val myWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            //.setInitialDelay(5,TimeUnit.SECONDS)
            //.addTag("firstRequest") // Parsing more than one workrequest
            // (they have their own id, but we can also separate them with a tag)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

         */

        // lower limit 15 min
        val myWorkRequest : WorkRequest = PeriodicWorkRequestBuilder<RefreshDatabase>(15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

        // WorkManager -- control WorkManager succeed,failed orblocked etc.
        // Because getWorkInfoByIdLiveData is used, observe function is added at the end.
        // Because LiveData is used.
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id).observe(this,
            Observer {
                if(it.state == WorkInfo.State.RUNNING){
                    println("running")
                }else if (it.state == WorkInfo.State.FAILED){
                    println("failed")
                }else if (it.state == WorkInfo.State.SUCCEEDED){
                    println("succeeded")
                }else if (it.state == WorkInfo.State.ENQUEUED){
                    println("enqueued")
                }else if (it.state == WorkInfo.State.BLOCKED){
                    println("blocked")
                }else if (it.state == WorkInfo.State.CANCELLED){
                    println("cancelled")
                }
            })

        // cancel process
        //WorkManager.getInstance(this).cancelAllWork()

        /*
        //Chaining -> i.e. chaining back to back
        //Don't use with PeriodicWorkManager
        //with OneTimeRequest orderly

        val oneTimeWorkRequest : OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            //.setInitialDelay(5,TimeUnit.DAYS)
            //.addTag("myWorkTag")
            .build()

        // use beginWith instead of enqueue

        WorkManager.getInstance(this).beginWith(oneTimeWorkRequest)
            .then(oneTimeWorkRequest)
            .then(oneTimeWorkRequest)
            .enqueue()

         */


    }
}