package com.rubyhuntersky.peregrine.data

import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class Databook {

    val portfolioSubject = BehaviorSubject.create<Portfolio>().toSerialized()!!

    init {
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe {
                    portfolioSubject.onNext(Portfolio())
                }
    }

    var portfolio: Portfolio
        get() = portfolioSubject.toBlocking().first()
        set(value) = portfolioSubject.onNext(value)
}