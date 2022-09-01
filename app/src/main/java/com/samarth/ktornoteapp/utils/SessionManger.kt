package com.samarth.ktornoteapp.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.samarth.ktornoteapp.utils.Constants.EMAIL_KEY
import com.samarth.ktornoteapp.utils.Constants.JWT_TOKEN_KEY
import com.samarth.ktornoteapp.utils.Constants.NAME_KEY
import kotlinx.coroutines.flow.first

/**
 * Created By Eslam Ghazy on 8/6/2022
 */
class SessionManger(val context: Context) {

    /*
    *  dataStore -> exention function
    * */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session_manager")
    private val TAG = "SessionManger"
    suspend fun updateSession(token: String, name: String, email: String) {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN_KEY)
        val nameKey = stringPreferencesKey(NAME_KEY)
        val emailKey = stringPreferencesKey(EMAIL_KEY)
        /*
        * to save name , email , token in local phone by preferences
        * */

        /*updateSession: token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOb3RlQXV0aCIsImlzcyI6Im5vdGVTZXJ2ZXIiLCJlbWFpbCI6ImVzbGFtZ2hhenk1NTVAZ21haWwuY29tIn0.pcy21quPrOSmzJ_RcbdP55e_gaTGANgH8SeZBt7kRlO4wO3zPP5GBRp671LcugASvzUbzu6eQrNuflRfva1jWA name: Eslam Ghazy email: eslamghazy555@gmail.com*/
        Log.e(TAG, "updateSession: " + "token: " + token + " name: " + name + " email: " + email);
        context.dataStore.edit { preferences ->
            preferences[jwtTokenKey] = token
            preferences[nameKey] = name
            preferences[emailKey] = email
        }
    }


    /*
    * to get token stored in preferences by (key name)
    * */
    suspend fun getJwtToken(): String? {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN_KEY)
        val preferences = context.dataStore.data.first()
        return preferences[jwtTokenKey]
    }


    /*
    * to get name stored in preferences by (key name)
    * */
    suspend fun getCurrentUserName(): String? {
        val nameKey = stringPreferencesKey(NAME_KEY)
        val preferences = context.dataStore.data.first()
        return preferences[nameKey]
    }

    /*
    * to get email stored in preferences by (key name)
    * */
    suspend fun getCurrentUserEmail(): String? {
        val emailKey = stringPreferencesKey(EMAIL_KEY)
        val preferences = context.dataStore.data.first()
        return preferences[emailKey]
    }


    /*
    * delete all data by logout
    * */
    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }


}