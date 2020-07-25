package app.sen.musics.source

import app.sen.musics.utils.GlobalContext
import app.sen.musics.utils.Pref


/**
 */
object AccountSettings {

    var account: String by Pref(
        GlobalContext.instance,
        key = "account",
        default = ""
    )

    var uid: String by Pref(
        GlobalContext.instance,
        key = "account_uid",
        default = ""
    )

    var token: String by Pref(
        GlobalContext.instance,
        key = "token",
        default = ""
    )

    var license: String by Pref(
        GlobalContext.instance,
        key = "license",
        default = ""
    )




}