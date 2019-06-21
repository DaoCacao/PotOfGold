package daocacao.myprecious

import android.content.Context
import daocacao.myprecious.data.Day
import daocacao.myprecious.data.Record
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class RealmManager(context: Context) {

    private val realm by lazy {
        Realm.init(context)
        Realm.getInstance(configuration)
    }
    private val configuration by lazy {
        RealmConfiguration.Builder()
                .name("realm_db")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()
    }

    fun getMonth(month: Int, year: Int): RealmResults<Day> {
        return realm.where(Day::class.java)
                .equalTo("year", year)
                .equalTo("month", month)
                .findAll()
    }

    fun getDay(date: String): Day {
        return realm.where(Day::class.java)
                .equalTo("date", date)
                .let {
                    if (it.count() == 0L) Day(date)
                    else it.findFirst()
                }
    }

    fun addRecord(day: Day, price: Float, comment: String) {
        realm.executeTransaction {
            day.records.add(Record(price, comment))
            it.copyToRealmOrUpdate(day)
        }

    }

    fun editRecord(record: Record, transaction: (Record) -> Unit) {
        realm.executeTransaction {
            transaction.invoke(record)
            it.copyToRealmOrUpdate(record)
        }
    }

    fun deleteRecord(record: Record) {
        realm.executeTransaction { record.deleteFromRealm() }
    }
}