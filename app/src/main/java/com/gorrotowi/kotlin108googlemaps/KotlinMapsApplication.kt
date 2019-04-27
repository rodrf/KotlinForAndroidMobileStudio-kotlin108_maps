package com.gorrotowi.kotlin108googlemaps

import android.app.Application
import com.google.android.libraries.places.api.Places

class KotlinMapsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this@KotlinMapsApplication, "AIzaSyA645cz5C7m8zNgmvM_HtRmOFw0hWi3alA")

    }

}