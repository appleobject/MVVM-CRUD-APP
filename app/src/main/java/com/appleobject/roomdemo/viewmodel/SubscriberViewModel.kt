package com.appleobject.roomdemo.viewmodel

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appleobject.roomdemo.ERROR_HANDLING
import com.appleobject.roomdemo.Event
import com.appleobject.roomdemo.db.Subscriber
import com.appleobject.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository): ViewModel(), Observable {

    val subscribers = repository.subscribers

    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val deleteOrClearAllButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        deleteOrClearAllButtonText.value = "Clear All"
    }

    fun saveOrUpdate() {
        if (inputName.value == null) {
            statusMessage.value = Event("Please enter the subscriber's name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter the subscriber's email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter a correct email address")
        } else {

            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))

                inputName.value = null
                inputEmail.value = null
            }

        }


    }

    fun deleteOrClearAll() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }


    }


    private fun insert(subscriber: Subscriber): Job = viewModelScope.launch {
        val rowId = repository.insert(subscriber)
        if (rowId > -1) {
            statusMessage.value = Event("$rowId Subscriber Inserted Successfully")
        } else {
            statusMessage.value = Event(ERROR_HANDLING)
        }
    }

    private fun delete(subscriber: Subscriber): Job = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)
        if (noOfRowsDeleted > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false

            saveOrUpdateButtonText.value = "Save"
            deleteOrClearAllButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRowsDeleted Subscriber Deleted Successfully")
        } else {
            statusMessage.value = Event(ERROR_HANDLING)
        }


    }

    private fun update(subscriber: Subscriber): Job = viewModelScope.launch {
        val noOfRows = repository.update(subscriber)
        if (noOfRows > 0) {
            statusMessage.value = Event(" $noOfRows Subscriber Updated Successfully")
        } else {
            statusMessage.value = Event(ERROR_HANDLING)
        }

    }

    private fun clearAll(): Job = viewModelScope.launch {
        val noOfRows = repository.deleteAll()
        if (noOfRows > 0) {
            statusMessage.value = Event("$noOfRows Subscribers Deleted Successfully")
        } else {
            statusMessage.value = Event(ERROR_HANDLING)
        }

    }


    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber

        saveOrUpdateButtonText.value = "Update"
        deleteOrClearAllButtonText.value = "Delete"


    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }/*Observable*/

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }/*Observable*/

}