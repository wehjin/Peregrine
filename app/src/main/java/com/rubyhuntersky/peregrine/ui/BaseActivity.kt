package com.rubyhuntersky.peregrine.ui

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Pair
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.exception.NotStoredException
import com.rubyhuntersky.peregrine.exception.ProductionStorage
import com.rubyhuntersky.peregrine.lib.oauth.model.OauthToken
import com.rubyhuntersky.peregrine.lib.oauth.ui.OauthUi.promptForVerifier
import com.rubyhuntersky.peregrine.model.*
import org.json.JSONException
import org.json.JSONObject
import rx.Observable
import rx.Subscription
import rx.functions.Action1
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity() {
    var refreshSubscription: Subscription? = null
    lateinit var etradeApi: EtradeApi
    lateinit var storage: Storage
    lateinit var partitionListStream: BehaviorSubject<PartitionList>
    val tag: String = this.javaClass.simpleName
    val errorAction: Action1<Throwable> = Action1 { throwable ->
        val title = "ERROR"
        Log.e(tag, title, throwable)
        alertError(title, throwable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        etradeApi = EtradeApi(this)
        storage = ProductionStorage(this, etradeApi.oauthAppToken)
        try {
            val inputStream = resources.openRawResource(R.raw.starting_partitions)
            partitionListStream = BehaviorSubject.create(PartitionList(JSONObject(getString(inputStream))))
        } catch (e: JSONException) {
            Log.e(tag, "onCreate", e)
            throw RuntimeException(e)
        } catch (e: IOException) {
            Log.e(tag, "onCreate", e)
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class)
    private fun getString(inputStream: InputStream): String {
        val stringBuilder = StringBuilder()
        val br = BufferedReader(InputStreamReader(inputStream))
        var s: String? = br.readLine()
        while (s != null) {
            stringBuilder.append(s)
            s = br.readLine()
        }
        var c = br.read()
        while (c != -1) {
            stringBuilder.append(c.toChar())
            c = br.read()
        }
        return stringBuilder.toString()
    }

    fun getPartitionListStream(): Observable<PartitionList> {
        return partitionListStream
    }

    val allAccountsStream: Observable<AllAccounts> get() = storage.streamAccountsList()
    val accountAssetsListStream: Observable<List<AccountAssets>> get() = storage.streamAccountAssetsList()
    val portfolioAssetsStream: Observable<PortfolioAssets> get() = storage.streamAccountAssetsList().map(::PortfolioAssets)

    fun refresh() {
        refreshSubscription?.unsubscribe()
        refreshSubscription = fetchAndStoreAccountsList().flatMap { allAccounts ->
            fetchAndStoreAccountAssetsList(allAccounts)
        }.subscribe({ logDebug("Refresh completed") }, { alertError("Refresh error", it) })
    }

    override fun onBackPressed() {
        val fragmentManager = fragmentManager
        if (fragmentManager.backStackEntryCount != 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun fetchAndStoreAccountAssetsList(allAccounts: AllAccounts): Observable<List<AccountAssets>> = fetchAccountAssets(allAccounts).toList().doOnNext { storage.writeAccountAssetsList(it) }

    private fun fetchAccountAssets(allAccounts: AllAccounts): Observable<AccountAssets> {
        return Observable.from(allAccounts.accounts).flatMap(object : Func1<EtradeAccount, Observable<Pair<JSONObject, JSONObject>>> {
            internal var count = 0

            override fun call(etradeAccount: EtradeAccount): Observable<Pair<JSONObject, JSONObject>> {
                return fetchAccountDetails(etradeAccount).delaySubscription((200 * count++).toLong(), TimeUnit.MILLISECONDS)
            }
        }).map(Func1<Pair<JSONObject, JSONObject>, com.rubyhuntersky.peregrine.model.AccountAssets> { jsonPair ->
            try {
                return@Func1 AccountAssets(EtradeApi.addBalanceToPositions(jsonPair.first, jsonPair.second))
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        })
    }

    protected fun fetchAndStoreAccountsList(): Observable<AllAccounts> {
        return oauthAccessToken.flatMap({ it.toEtradeAccounts })
                .onErrorResumeNext { throwable ->
                    when (throwable) {
                        is EtradeApi.NotAuthorizedException -> renewOauthAccessToken().flatMap({ it.toEtradeAccounts })
                        else -> Observable.error(throwable)
                    }
                }
                .onErrorResumeNext { throwable ->
                    when (throwable) {
                        is EtradeApi.NotAuthorizedException -> {
                            storage.eraseOauthAccessToken()
                            fetchOauthAccessToken().flatMap({ it.toEtradeAccounts })
                        }
                        else -> Observable.error(throwable)
                    }
                }
                .map { AllAccounts(it, Date()) }
                .doOnNext { storage.writeAccountList(it) }
    }

    private fun fetchAccountDetails(etradeAccount: EtradeAccount): Observable<Pair<JSONObject, JSONObject>> {
        return storage.readOauthAccessToken().flatMap { oauthToken ->
            fetchAccountPositionsAndBalanceResponses(oauthToken, etradeAccount)
        }
    }

    private fun fetchAccountPositionsAndBalanceResponses(oauthToken: OauthToken, etradeAccount: EtradeAccount): Observable<Pair<JSONObject, JSONObject>> {
        return Observable.zip(
                fetchAccountPositionsResponse(oauthToken, etradeAccount),
                fetchAccountBalanceResponse(oauthToken, etradeAccount),
                { positions, balance -> Pair(positions, balance) }
        )
    }

    private fun fetchAccountPositionsResponse(oauthToken: OauthToken, etradeAccount: EtradeAccount): Observable<JSONObject> {
        return etradeApi.fetchAccountPositionsResponse(etradeAccount.accountId, oauthToken).retry(1).map { jsonObject ->
            try {
                jsonObject.putOpt("accountDescription", etradeAccount.description)
                jsonObject.putOpt("requestAccountId", etradeAccount.accountId)
                jsonObject.putOpt("responseArrivalTime", DateFormat.getInstance().format(Date()))
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }

            jsonObject
        }
    }

    private fun fetchAccountBalanceResponse(oauthToken: OauthToken, etradeAccount: EtradeAccount): Observable<JSONObject> {
        return etradeApi.fetchAccountBalanceResponse(etradeAccount.accountId, oauthToken).retry(1).map { jsonObject ->
            try {
                jsonObject.putOpt("accountDescription", etradeAccount.description)
                jsonObject.putOpt("requestAccountId", etradeAccount.accountId)
                jsonObject.putOpt("responseArrivalTime", DateFormat.getInstance().format(Date()))
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            jsonObject
        }.doOnError { Log.e(tag, " v error, account: " + etradeAccount) }
    }


    private val oauthAccessToken: Observable<OauthToken>
        get() = storage.readOauthAccessToken().onErrorResumeNext { throwable ->
            when (throwable) {
                is NotStoredException -> fetchOauthAccessToken()
                else -> Observable.error(throwable)
            }
        }

    private fun renewOauthAccessToken(): Observable<OauthToken> = oauthAccessToken
            .flatMap { etradeApi.renewOauthAccessToken(it) }

    private fun fetchOauthAccessToken(): Observable<OauthToken> = etradeApi.fetchOauthRequestToken()
            .flatMap { requestToken -> promptForVerifier(this@BaseActivity, requestToken) }
            .flatMap { verifier -> etradeApi.fetchOauthAccessToken(verifier) }
            .doOnNext { oauthToken -> storage.writeOauthAccessToken(oauthToken) }

    private val OauthToken.toEtradeAccounts: Observable<List<EtradeAccount>> get() = etradeApi.fetchAccountList(this)

    protected fun alertError(title: String, throwable: Throwable) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(throwable.toString()).setPositiveButton("Close") { dialog, which -> dialog.dismiss() }.show()
        logError(title, throwable)
    }

    protected fun logDebug(message: String) {
        Log.d(tag, message)
    }

    protected fun logError(message: String, throwable: Throwable) {
        Log.e(tag, "$message\n${throwable.message}")
    }
}
