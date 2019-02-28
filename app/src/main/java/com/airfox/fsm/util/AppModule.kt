package com.airfox.fsm.util

import com.airfox.fsm.pacman.ghost.Ghost
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @Provides
    fun provideGhost(): Ghost {
        return Ghost()
    }

//    @Provides
//    @Singleton
//    fun providePacman(): Pacman {
//        return Ghost()
//    }

}
