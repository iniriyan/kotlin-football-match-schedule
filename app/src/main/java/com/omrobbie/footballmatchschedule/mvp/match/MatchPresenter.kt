package com.omrobbie.footballmatchschedule.mvp.match

import android.content.Context
import com.google.gson.Gson
import com.omrobbie.footballmatchschedule.helper.database
import com.omrobbie.footballmatchschedule.model.EventResponse
import com.omrobbie.footballmatchschedule.model.EventsItem
import com.omrobbie.footballmatchschedule.model.LeagueResponse
import com.omrobbie.footballmatchschedule.network.ApiRepository
import com.omrobbie.footballmatchschedule.network.TheSportsDbApi
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MatchPresenter(val view: MatchView) {

    val apiRepository = ApiRepository()
    val gson = Gson()

    var menu = 1

    fun getLeagueAll() {
        view.showLoading()

        doAsync {
            val data = gson.fromJson(apiRepository
                    .doRequest(TheSportsDbApi.getLeagueAll()),
                    LeagueResponse::class.java
            )

            uiThread {
                view.hideLoading()
                view.showLeagueList(data)
            }
        }
    }

    fun getEventsPrev(id: String) {
        menu = 1
        view.showLoading()

        doAsync {
            val data = gson.fromJson(apiRepository
                    .doRequest(TheSportsDbApi.getLeaguePrev(id)),
                    EventResponse::class.java
            )

            uiThread {
                view.hideLoading()

                try {
                    view.showEventList(data.events!!)
                } catch (e: NullPointerException) {
                    view.showEmptyData()
                }
            }
        }
    }

    fun getEventsNext(id: String) {
        menu = 2
        view.showLoading()

        doAsync {
            val data = gson.fromJson(apiRepository
                    .doRequest(TheSportsDbApi.getLeagueNext(id)),
                    EventResponse::class.java
            )

            uiThread {
                view.hideLoading()

                try {
                    view.showEventList(data.events!!)
                } catch (e: NullPointerException) {
                    view.showEmptyData()
                }
            }
        }
    }

    fun getFavoritesAll(context: Context) {
        menu = 3
        view.showLoading()

        val data: MutableList<EventsItem> = mutableListOf()

        context.database.use {
            val favorites = select(EventsItem.TABLE_FAVORITES)
                    .parseList(classParser<EventsItem>())

            data.addAll(favorites)
        }

        view.hideLoading()

        if (data.size > 0) {
            view.showEventList(data)
        } else {
            view.showEmptyData()
        }
    }
}
