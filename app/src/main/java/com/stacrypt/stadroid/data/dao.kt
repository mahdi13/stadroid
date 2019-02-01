package com.stacrypt.stadroid.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface AssetDao {
    @Insert(onConflict = REPLACE)
    fun save(asset: Asset)

    @Query("SELECT * FROM Asset")
    fun loadAll(): LiveData<List<Asset>>
}

@Dao
interface BalanceDao {
    @Insert(onConflict = REPLACE)
    fun save(balance: Balance)

    @Query("SELECT * FROM Balance")
    fun loadAll(): LiveData<List<Balance>>
}
