package daocacao.myprecious.realm

import android.content.Context
import daocacao.myprecious.data.Day
import daocacao.myprecious.data.Record
import io.reactivex.processors.PublishProcessor
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class RealmManager(context: Context) {

    companion object {
        private const val NAME = "realm_db"
        private const val VERSION = 0L
    }

    private val realm by lazy {
        Realm.init(context)
        Realm.getInstance(configuration)
    }
    private val migration by lazy { Migration(VERSION) }
    private val configuration by lazy {
        RealmConfiguration.Builder()
                .name(NAME)
                .schemaVersion(VERSION)
                .migration(migration)
                .build()
    }

    val observable: PublishProcessor<Day> = PublishProcessor.create()

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
            observable.onNext(day)
        }
    }

    fun editRecord(day: Day, record: Record, transaction: (Record) -> Unit) {
        realm.executeTransaction {
            transaction.invoke(record)
            it.copyToRealmOrUpdate(day)
            observable.onNext(day)
        }
    }

    fun deleteRecord(day: Day, record: Record) {
        realm.executeTransaction {
            record.deleteFromRealm()
            observable.onNext(day)
        }
    }
}