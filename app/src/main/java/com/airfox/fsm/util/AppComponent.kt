package com.airfox.fsm.util

import com.airfox.fsm.MainActivity
import com.airfox.fsm.android.AndroidActivity1
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: AndroidActivity1)

}
