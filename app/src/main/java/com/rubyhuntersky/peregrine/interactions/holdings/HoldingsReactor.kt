package com.rubyhuntersky.peregrine.interactions.holdings

import com.rubyhuntersky.peregrine.data.Databook
import com.rubyhuntersky.peregrine.data.OfflineInventory
import com.rubyhuntersky.peregrine.interactions.newholding.NewHoldingCatalyst
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.BehaviorSubject

class HoldingsReactor(private val databook: Databook, private val newHoldingCatalyst: NewHoldingCatalyst) {

    sealed class State {
        object Loading : State()
        object Empty : State()
        data class Loaded(val offlineInventory: OfflineInventory) : State()
    }

    sealed class Action {
        object Load : Action()
        object AddHolding : Action()
    }

    private val stateSubject = BehaviorSubject.create(State.Loading as State)

    val states: Observable<State>
        get() = stateSubject.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())

    fun start() {
        databook.portfolioSubject
                .subscribe { portfolio ->
                    val newState = if (portfolio.isEmpty()) {
                        State.Empty
                    } else {
                        State.Loaded(portfolio.offlineInventory)
                    }
                    stateSubject.onNext(newState)
                }
    }

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