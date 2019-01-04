package com.rubyhuntersky.peregrine.interactions.holdings

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class HoldingsReactor {

    sealed class State {
        object Loading : State()
        object Empty : State()
    }

    sealed class Action {
        object Load : Action()
        object AddHolding : Action()
    }

    private val stateSubject = BehaviorSubject.create(State.Loading as State)

    init {
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    stateSubject.onNext(State.Empty)
                }
    }

    val states: Observable<State>
        get() = stateSubject.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())
}