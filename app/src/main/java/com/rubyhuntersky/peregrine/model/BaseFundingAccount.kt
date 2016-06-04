package com.rubyhuntersky.peregrine.model

/**
 * @author Jeffrey Yu
 * @since 6/4/16.
 */

abstract class BaseFundingAccount : FundingAccount {

    override fun hasFundsForBuyIntention(buyIntention: BuyIntention): Boolean = cashAvailable.compareTo(buyIntention.amount) >= 0

    override fun getStatusForBuy(buyIntention: BuyIntention): FundingStatus {
        return FundingStatus(buyIntention, cashAvailable)
    }
}