package com.rubyhuntersky.peregrine.interactions.holdings

import com.rubyhuntersky.peregrine.interactions.newholding.NewHoldingCatalyst
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class HoldingsReactor(private val newHoldingCatalyst: NewHoldingCatalyst) {

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
                    perform(Action.Load)
                }
    }

    val states: Observable<State>
        get() = stateSubject.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())

    fun perform(action: Action) {
        when (action) {
            is Action.Load -> {
                stateSubject.onNext(State.Empty)
            }
            is Action.AddHolding -> {
                newHoldingCatalyst.startInteraction()
            }
        }
    }
}