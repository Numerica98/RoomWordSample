package com.example.roomwordsample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.roomwordsample.data.WordRepository
import com.example.roomwordsample.data.WordRoomDatabase
import com.example.roomwordsample.data.models.Word
import kotlinx.coroutines.Dispatchers

class WordViewModel(application: Application) : AndroidViewModel(application){

    private val repository: WordRepository
    val allWords: LiveData<List<Word>>

    init {
        val wordsDao = WordRoomDatabase.getDatabase(application, viewModelScope).wordDao()
        repository = WordRepository(wordsDao)
        allWords = repository.allWords
    }

    fun insert(word: Word) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(word)
    }
}