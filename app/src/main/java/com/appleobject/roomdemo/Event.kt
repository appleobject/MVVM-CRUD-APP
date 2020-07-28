package com.appleobject.roomdemo

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /*
    * Return the content and prevent its use again
    *
    * */


    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }


    /*
    * Return the content, even if its already been handled
    * */
    fun peekContent(): T = content
}