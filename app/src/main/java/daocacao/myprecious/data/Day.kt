package daocacao.myprecious.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Day() : RealmObject() {

    @PrimaryKey
    var date: String = "" //d.M.yyyy
    var year = 0
    var month = 0
    var records = RealmList<Record>()

    constructor(date: String) : this() {
        this.date = date
        month = date.split(".")[1].toInt()
        year = date.split(".")[2].toInt()
    }
}
