package com.example.flashcard.datastore

import kotlinx.coroutines.flow.Flow

interface Storage<T> {
    fun insert(data: T): Flow<Int>
    fun insertAll(data: List<T>): Flow<Int>
    fun getAll(): Flow<List<T>>
    fun edit(identifier: Int, data: T): Flow<Int>
    fun get(where: (T) -> Boolean): Flow<T>
    fun delete(identifier: Int): Flow<Int>

}