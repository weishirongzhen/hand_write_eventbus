package com.memtopia.myeventbus.eventbus


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Subscribe(val threadModel: ThreadModel = ThreadModel.MAIN)
