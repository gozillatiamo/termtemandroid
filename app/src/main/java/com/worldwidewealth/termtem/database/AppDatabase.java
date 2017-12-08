package com.worldwidewealth.termtem.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.worldwidewealth.termtem.database.query.UserPinDao;
import com.worldwidewealth.termtem.database.table.UserPin;

/**
 * Created by gozillatiamo on 11/23/17.
 */

@Database(entities = {
        UserPin.class
}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase INSTANE;

    public abstract UserPinDao userPinDao();

    public static AppDatabase getAppDatabase(Context context){
        if (INSTANE == null){
            INSTANE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "termtem_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANE;
    }
}
