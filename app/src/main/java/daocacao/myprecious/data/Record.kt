package daocacao.myprecious.data

import io.realm.RealmObject

open class Record() : RealmObject() {

    var spended: Float = 0f
    var comment: String = ""

    constructor(price: Float, comment: String) : this() {
        this.spended = price
        this.comment = comment
    }
}