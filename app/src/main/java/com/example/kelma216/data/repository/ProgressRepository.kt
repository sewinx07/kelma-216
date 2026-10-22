package com.example.kelma216.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kelma216.data.model.GameSettings
import com.example.kelma216.data.model.GameState
import com.example.kelma216.domain.GameConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "kelma216_progress")

/**
 * Local persistence for player progress (requirement 5.8 / 5.9: stored locally and
 * survives app restarts).
 *
 * DataStore + Android's built-in org.json is the simplest option that fits a single
 * structured blob of game state; it avoids a database and avoids adding a serialization
 * dependency.
 */
class ProgressRepository(private val context: Context) {

    private val stateKey = stringPreferencesKey("game_state_v1")

    val gameState: Flow<GameState> = context.gameDataStore.data.map { prefs ->
        prefs[stateKey]?.let(::decode) ?: GameState()
    }

    suspend fun update(transform: (GameState) -> GameState) {
        context.gameDataStore.edit { prefs ->
            val current = prefs[stateKey]?.let(::decode) ?: GameState()
            prefs[stateKey] = encode(transform(current))
        }
    }

    suspend fun save(state: GameState) {
        context.gameDataStore.edit { prefs ->
            prefs[stateKey] = encode(state)
        }
    }

    // --- JSON (org.json, framework built-in) -----------------------------

    private fun encode(state: GameState): String {
        val json = JSONObject()
        json.put(KEY_COMPLETED, JSONArray(state.completedPuzzleIds.toList()))
        json.put(KEY_HINTED, JSONArray(state.hintedPuzzleIds.toList()))
        json.put(KEY_COINS, state.coins)
        json.put(KEY_UNLOCKED, JSONArray(state.unlockedGovernorateIds.toList()))
        json.put(KEY_SOUND, state.settings.soundEnabled)
        json.put(KEY_MUSIC, state.settings.musicEnabled)
        return json.toString()
    }

    private fun decode(raw: String): GameState = try {
        val json = JSONObject(raw)
        GameState(
            completedPuzzleIds = json.stringSet(KEY_COMPLETED),
            hintedPuzzleIds = json.stringSet(KEY_HINTED),
            coins = json.optInt(KEY_COINS, GameConfig.STARTING_COINS).coerceAtLeast(0),
            unlockedGovernorateIds = json.stringSet(KEY_UNLOCKED)
                .ifEmpty { setOf(GameConfig.FIRST_GOVERNORATE_ID) },
            settings = GameSettings(
                soundEnabled = json.optBoolean(KEY_SOUND, true),
                musicEnabled = json.optBoolean(KEY_MUSIC, false),
            ),
        )
    } catch (t: Throwable) {
        // Corrupt or unreadable save: fall back to a safe default rather than crashing.
        GameState()
    }

    private fun JSONObject.stringSet(key: String): Set<String> {
        val arr = optJSONArray(key) ?: return emptySet()
        return (0 until arr.length()).mapNotNull { arr.optString(it, null) }.toSet()
    }

    private companion object {
        const val KEY_COMPLETED = "completed"
        const val KEY_HINTED = "hinted"
        const val KEY_COINS = "coins"
        const val KEY_UNLOCKED = "unlocked"
        const val KEY_SOUND = "sound"
        const val KEY_MUSIC = "music"
    }
}
