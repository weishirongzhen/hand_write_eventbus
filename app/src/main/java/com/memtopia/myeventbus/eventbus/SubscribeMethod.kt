package com.memtopia.myeventbus.eventbus

import java.lang.reflect.Method

class SubscribeMethod(val method: Method, val threadModel: ThreadModel, val parameterType: Class<*>)

