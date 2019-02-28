package com.airfox.fsm.util

object DaggerFactory {

    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule)
                .build()
    }

}