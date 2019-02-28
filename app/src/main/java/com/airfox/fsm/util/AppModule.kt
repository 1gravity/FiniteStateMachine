package com.airfox.fsm.util

import com.airfox.fsm.Closed
import com.airfox.fsm.Door
import com.airfox.fsm.pacman.ghost.Ghost
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @Provides
    fun provideDoor(): Door {
        return Door(Closed)
    }

    @Provides
    fun provideGhost(): Ghost {
        return Ghost()
    }

}
