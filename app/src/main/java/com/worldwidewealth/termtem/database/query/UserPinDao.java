package com.worldwidewealth.termtem.database.query;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.worldwidewealth.termtem.database.table.UserPin;

import java.util.List;

/**
 * Created by gozillatiamo on 11/23/17.
 */

@Dao
public interface UserPinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserPin userPin);

    @Query("SELECT * FROM user_pin")
    List<UserPin> selectAll();

    @Query("SELECT * FROM user_pin WHERE user_id LIKE :userid")
    UserPin getUserPinById(String userid);

    @Query("SELECT * FROM user_pin WHERE pin_id = :pinId")
    UserPin getuserPinByPinId(String pinId);

    @Query("SELECT * FROM user_pin WHERE use_fingerprint = 1")
    UserPin getUserUseFingerprint();

    @Update
    void updateUserPin(UserPin userPin);
}
