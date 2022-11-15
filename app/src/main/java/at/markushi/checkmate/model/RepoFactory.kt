package at.markushi.checkmate.model

import at.markushi.checkmate.App

object RepoFactory {
    fun getRepo(): Repo = RepoImpl(App.context)
}