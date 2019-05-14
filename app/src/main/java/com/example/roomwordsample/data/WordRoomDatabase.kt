package com.example.roomwordsample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomwordsample.data.dao.WordDao
import com.example.roomwordsample.data.models.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 1)
public abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): WordRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return  tempInstance
            }
            synchronized(this){
                //Sitio donde se crea la base de datos
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WordRoomDatabase::class.java,
                        "Word_database"
                ).addCallback(WordDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class WordDatabaseCallback(
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback(){

        override fun onOPen(db: SupportSQLiteDatabase){
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO){
                    populateDatabase(database.wordDao())
                }
            }
        }

        fun populateDatabase(wordDao: WordDao){
            wordDao.deleteAll()

            var word = Word("Hello")
            wordDao.insert(word)
            word = Word("Word!")
            wordDao.insert(word)
        }
    }
}