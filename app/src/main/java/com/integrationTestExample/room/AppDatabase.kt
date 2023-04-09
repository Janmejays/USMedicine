package com.integrationTestExample.room

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction
import com.integrationTestExample.utils.Constants.Companion.ALGORITHM_AES
import com.integrationTestExample.utils.Constants.Companion.DATABASE_NAME
import com.integrationTestExample.utils.Constants.Companion.KEY_SIZE
import com.integrationTestExample.utils.Constants.Companion.PREFS_KEY_PASSPHRASE
import com.integrationTestExample.utils.Constants.Companion.SHARED_PREFS_NAME
import net.sqlcipher.database.SupportFactory
import javax.crypto.KeyGenerator
@Database(entities = [ConceptProperty::class, Interaction::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun DrugDao(): DrugDao

    companion object {

        @Volatile
        var INSTANCE: AppDatabase? = null


        /**
         * Returns the database instance. If the database does not exist yet, it will be created.
         */
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = createDB(context)
                    INSTANCE = instance
                }
                return instance
            }
        }

        /**
         * Creates a database instance and returns it.
         */
        private fun createDB(context: Context): AppDatabase {
            val passphrase = getPassphrase(context) ?: initializePassphrase(context)

            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * Generates a passphrase and stores it in the encrypted shared preferences.
         * Returns the newly generated passphrase.
         */
        private fun initializePassphrase(context: Context): ByteArray {
            val passphrase = generatePassphrase()

            getSharedPrefs(context).edit(commit = true) {
                putString(PREFS_KEY_PASSPHRASE, passphrase.toString(Charsets.ISO_8859_1))
            }

            return passphrase
        }


        /**
         * Retrieves the passphrase for encryption from the encrypted shared preferences.
         * Returns null if there is no stored passphrase.
         */
        private fun getPassphrase(context: Context): ByteArray? {
            val passphraseString = getSharedPrefs(context)
                .getString(PREFS_KEY_PASSPHRASE, null)
            return passphraseString?.toByteArray(Charsets.ISO_8859_1)
        }

        /**
         * Returns a reference to the encrypted shared preferences.
         */
        private fun getSharedPrefs(context: Context): SharedPreferences {
            val masterKey =
                MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            return EncryptedSharedPreferences.create(
                context,
                SHARED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        /**
         * Generates and returns a passphrase.
         */
        private fun generatePassphrase(): ByteArray {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES)
            keyGenerator.init(KEY_SIZE)
            return keyGenerator.generateKey().encoded
        }
    }
}
