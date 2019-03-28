package com.airfox.fsm.util

import com.airfox.fsm.door.Door
import com.airfox.fsm.door.Opened
import com.airfox.fsm.pacman.ghost.Ghost
import com.airfox.fsm.pacman.pacman.Pacman
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return LoggerImpl()
    }

    @Provides
    fun provideDoor(logger: Logger): Door {
        return Door(logger, Opened)
    }

    @Provides
    fun provideGhost(logger: Logger): Ghost {
        return Ghost(logger)
    }

    @Provides
    fun providePacman(logger: Logger): Pacman {
        return Pacman(logger)
    }

}
