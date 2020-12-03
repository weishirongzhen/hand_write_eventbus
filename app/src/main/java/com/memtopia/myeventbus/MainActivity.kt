package com.memtopia.myeventbus

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.memtopia.myeventbus.eventbus.EventBus
import com.memtopia.myeventbus.eventbus.Subscribe
import com.memtopia.myeventbus.eventbus.ThreadModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    EventBus.getDefault().register(this)


    findViewById<Button>(R.id.main).setOnClickListener {
      Log.e("mylog", "send from = ${Thread.currentThread().name}")
      EventBus.getDefault().post(A(nameA = "AAAAAA"))

    }
    findViewById<Button>(R.id.bg).setOnClickListener {

      thread(start = true) {
        Log.e("mylog", "send from = ${Thread.currentThread().name}")
        EventBus.getDefault().post(A(nameA = "AAAAAA"))
      }


    }


  }


  @Subscribe(threadModel = ThreadModel.BACKGROUND)
  fun onCallBg(obj: Any) {
    when (obj) {
      is A -> {
        Log.e("mylog", "onCallBg ${obj.nameA}  receive from = ${Thread.currentThread().name}")
      }

    }
  }

  @Subscribe
  fun onCallMain(obj: Any) {

    when (obj) {
      is A -> {
        Log.e("mylog", "onCallMain ${obj.nameA}  receive from = ${Thread.currentThread().name}")
      }

    }
  }
}
