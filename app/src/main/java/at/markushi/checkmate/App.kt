package at.markushi.checkmate

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.sentry.android.core.SentryAndroid

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        context = this
        super.onCreate()

        SentryAndroid.init(this) { options ->
            options.apply {
                isDebug = true
                dsn =
                    "https://d7f82dceeead5759af46e8b1c59169d7@o4503964729475072.ingest.us.sentry.io/4504123188838400"
            }
        }
    }
}