package com.hussein.jana

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hussein.jana.core.common.initAppBaseUrl
import com.hussein.jana.core.database.initDatabaseContext
import com.hussein.jana.core.datastore.initDataStoreContext
import com.hussein.jana.core.datastore.initSecureStorageContext
import com.hussein.jana.core.designsystem.initAppContext
import com.hussein.jana.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class JanaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppContext(this)
        initDataStoreContext(this)
        initSecureStorageContext(this)
        initDatabaseContext(this)
        initKoin {
            androidLogger(Level.ERROR)
            androidContext(this@JanaApp)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
