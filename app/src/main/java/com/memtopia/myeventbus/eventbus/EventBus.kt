package com.memtopia.myeventbus.eventbus

import android.os.Handler
import android.os.Looper
import java.lang.reflect.Method
import kotlin.concurrent.thread

class EventBus private constructor() {


  private val cacheMap: HashMap<Any, List<SubscribeMethod>> = HashMap()
  private var mainHandler: Handler = Handler(Looper.getMainLooper())

  companion object {
    @Volatile
    private var instance: EventBus? = null
    fun getDefault(): EventBus {
      if (instance == null) {
        synchronized(this) {
          instance = EventBus()
        }
      }
      return instance!!
    }
  }

  //循环obj中带有 Subscribe注解的方法，保存到subscribeMap中
  fun register(obj: Any) {

    var list = cacheMap[obj]

    if (list == null) {
      list = findSubscribeMethods(obj)
      cacheMap[obj] = list
    }


  }

  fun post(type: Any) {
    //循环 cachedMap 找到对应的方法
    cacheMap.entries.forEach {
      it.value.forEach { method ->
        if (method.parameterType.isAssignableFrom(type::class.java)) {

          when (method.threadModel) {
            ThreadModel.MAIN -> {

              //if主到主 else 子到主
              if (Looper.myLooper() == Looper.getMainLooper()) {
                invoke(method, it.key, type)
              } else {
                mainHandler.post {
                  invoke(method, it.key, type)
                }
              }
            }
            ThreadModel.BACKGROUND -> {
              //if主到子 else 子到子
              if (Looper.myLooper() == Looper.getMainLooper()) {
                thread(start = true) { invoke(method, it.key, type) }
              } else {
                invoke(method, it.key, type)
              }
            }
          }

        }
      }
    }

  }

  private fun invoke(subscribeMethod: SubscribeMethod, it: Any, type: Any) {
    val method = subscribeMethod.method
    method.invoke(it, type)
  }

  private fun findSubscribeMethods(obj: Any): List<SubscribeMethod> {
    val list = arrayListOf<SubscribeMethod>()

    var clazz: Class<*>? = obj.javaClass


    //取出所有父类中的带有Subscribe 注解的方法
    while (clazz != null) {


      //如果是系统级别的父类则忽略
      val className = clazz.name
      if (className.contains("java.") || className.contains("javax.") || className.contains("android.") || className.contains("androidx.")) {
        break
      }

      //获取obj类中的所有方法
      val methods: Array<Method> = clazz.declaredMethods

      for (method in methods) {
        //判断obj类中的方法是否带有Subscribe注解
        if (method.isAnnotationPresent(Subscribe::class.java)) {

          //获取subscribe
          val subscribe = method.getAnnotation(Subscribe::class.java)
          val parameterType = method.parameterTypes
          //只能有一个参数对应 post方法中只能传一个参数
          if (parameterType.size != 1) throw  IllegalArgumentException("EventBus only support one parameter")


          val threadModel = subscribe!!.threadModel
          val subscribeMethod = SubscribeMethod(method, threadModel, parameterType[0])

          list.add(subscribeMethod)
        }
      }

      clazz = clazz.superclass
    }
    return list

  }
}
