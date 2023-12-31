package com.akkobana.coroutines.di

import android.app.Application
import com.akkobana.coroutines.ui.MainPresenter
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }

    fun provideMainPresenter(): MainPresenter
}