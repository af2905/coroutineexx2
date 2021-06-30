package com.github.af2905.coroutineexx2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.bthRun).setOnClickListener { onRun() }
        findViewById<View>(R.id.bthRun2).setOnClickListener { onRun2() }
        findViewById<View>(R.id.bthRun3).setOnClickListener { onRun3() }

        launchScope3coroutine()
    }

    private fun launchScope2coroutine() {
        val scope2 = CoroutineScope(Dispatchers.Main)

        log("scope2, ${contextToString(scope2.coroutineContext)}")

        scope2.launch {
            log("scope2 coroutine, ${contextToString(coroutineContext)}")
        }
    }

    private fun launchScope3coroutine() {
        val scope3 = CoroutineScope(Job() + Dispatchers.Main + UserData())
        log("scope3, ${contextToString(scope.coroutineContext)}")

        scope3.launch {
            log("scope3 coroutine, level1, ${contextToString(coroutineContext)}")

            launch(Dispatchers.Default) {
                log("scope3 coroutine, level2, ${contextToString(coroutineContext)}")

                launch {
                    log("scope3 coroutine, level3, ${contextToString(coroutineContext)}")
                    log("scope3 coroutine, level3, userData, ${coroutineContext[UserData]}")
                }
            }
        }
    }

    private fun onRun() {

        scope.launch {

            log("parent coroutine, start")

            job = launch(start = CoroutineStart.LAZY) {
                log("child coroutine, start")
                delay(1000)
                log("child coroutine, end")
            }

            val job2 = launch {
                log("child coroutine 2, start")
                delay(1500)
                log("child coroutine 2, end")
            }

            log("parent coroutine, wait until child completes")
            job2.join()

            val deferred = async {
                log("child coroutine async, start")
                delay(1500)
                log("child coroutine async, end")
                "async result"
            }

            log("parent coroutine, wait until child returns result")
            val result = deferred.await()
            log("parent coroutine, child returns: $result")

            log("parent coroutine, end")
        }
    }

    private fun onRun2() {
        log("onRun2, start")
        job.start()
        log("onRun2, end")
    }

    private fun onRun3() {
        scope.launch {
            log("parent coroutine, start")
            val data = async { getData() }
            val data2 = async { getData2() }

            log("parent coroutine, wait until children return result")
            val result = "${data.await()}, ${data2.await()}"
            log("parent coroutine, children returned: $result")

            log("parent coroutine, end")
        }
    }

    private suspend fun getData(): String {
        delay(1000)
        return "data"
    }

    private suspend fun getData2(): String {
        delay(1500)
        return "data2"
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun contextToString(context: CoroutineContext): String =
        "Job = ${context[Job]}, Dispatcher = ${context[ContinuationInterceptor]}"

    private fun log(text: String) {
        Log.d("TAG", "${formatter.format(Date())} $text [${Thread.currentThread().name}]")
    }
}