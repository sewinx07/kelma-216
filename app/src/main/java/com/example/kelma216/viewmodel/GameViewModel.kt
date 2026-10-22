package com.example.kelma216.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelma216.data.content.PuzzleCatalog
import com.example.kelma216.data.model.GameState
import com.example.kelma216.data.model.LetterTile
import com.example.kelma216.data.model.Puzzle
import com.example.kelma216.data.repository.ProgressRepository
import com.example.kelma216.domain.GameRules
import com.example.kelma216.domain.TileGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** What the player is currently doing with a puzzle. */
enum class PuzzlePhase { INPUT, CORRECT, INCORRECT }

/**
 * In-progress state of a single puzzle screen (transient, survives rotation but not
 * required to survive process death — completed progress lives in the repository).
 */
data class PuzzleSession(
    val puzzle: Puzzle,
    val tiles: List<LetterTile>,
    val slots: List<Char?>,
    /** Which tile id currently fills each slot (null if empty or filled by a hint). */
    val slotSource: List<String?>,
    /** Slots revealed by a paid hint; these are locked and cannot be cleared. */
    val lockedSlots: Set<Int>,
    val phase: PuzzlePhase = PuzzlePhase.INPUT,
)

/**
 * Single app-level ViewModel: exposes persisted [GameState] plus the transient puzzle
 * session, and applies [GameRules] for every mutation so rules stay in one place.
 */
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProgressRepository(application)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _session = MutableStateFlow<PuzzleSession?>(null)
    val session: StateFlow<PuzzleSession?> = _session.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        viewModelScope.launch {
            repository.gameState.collect { saved ->
                _gameState.value = saved
                _isLoaded.value = true
            }
        }
    }

    private fun persist(newState: GameState) {
        _gameState.value = newState
        viewModelScope.launch { repository.save(newState) }
    }

    // --- puzzle lifecycle -------------------------------------------------

    /** Loads a puzzle and resets its interaction state. Safe to call on every entry. */
    fun startPuzzle(puzzleId: String) {
        val puzzle = PuzzleCatalog.byId(puzzleId)
        if (puzzle == null || !puzzle.playable) {
            _session.value = null
            _message.value = "Contenu à venir pour cette énigme."
            return
        }
        if (_session.value?.puzzle?.id == puzzle.id) return // already loaded, keep progress

        val tiles = TileGenerator.build(puzzle)
        if (tiles.isEmpty()) {
            // Answer too long for the fixed tray: content bug, never truncate silently.
            _session.value = null
            _message.value = "Cette énigme doit être vérifiée (mot trop long)."
            return
        }
        val answerChars = puzzle.answer.toList()
        _session.value = PuzzleSession(
            puzzle = puzzle,
            tiles = tiles,
            slots = List(answerChars.size) { null },
            slotSource = List(answerChars.size) { null },
            lockedSlots = emptySet(),
            phase = PuzzlePhase.INPUT,
        )
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun updateSession(transform: (PuzzleSession) -> PuzzleSession) {
        val current = _session.value ?: return
        _session.value = transform(current)
    }

    /** Fills the leftmost empty slot with the tapped tile. Ignores already-used tiles. */
    fun selectTile(tileId: String) {
        updateSession { s ->
            if (s.phase != PuzzlePhase.INPUT) return@updateSession s
            val tile = s.tiles.firstOrNull { it.id == tileId } ?: return@updateSession s
            if (s.slotSource.any { it == tileId }) return@updateSession s // never select a tile twice
            val target = s.slots.indexOfFirst { it == null }
            if (target == -1) return@updateSession s
            val slots = s.slots.toMutableList().also { it[target] = tile.char }
            val source = s.slotSource.toMutableList().also { it[target] = tileId }
            s.copy(slots = slots, slotSource = source)
        }
    }

    /** Clears a filled slot, returning its tile to the tray. Hint-locked slots are immune. */
    fun deselectSlot(slotIndex: Int) {
        updateSession { s ->
            if (s.phase != PuzzlePhase.INPUT) return@updateSession s
            if (slotIndex in s.lockedSlots) return@updateSession s
            if (slotIndex !in s.slots.indices || s.slots[slotIndex] == null) return@updateSession s
            val slots = s.slots.toMutableList().also { it[slotIndex] = null }
            val source = s.slotSource.toMutableList().also { it[slotIndex] = null }
            s.copy(slots = slots, slotSource = source)
        }
    }

    /** Validates the assembled word. Empty answers cannot be submitted (requirement 5). */
    fun submitAnswer() {
        val s = _session.value ?: return
        if (s.phase != PuzzlePhase.INPUT) return
        if (s.slots.any { it == null }) {
            _message.value = "Remplis toutes les cases avant de valider."
            return
        }
        val candidate = s.slots.joinToString("") { it.toString() }
        if (GameRules.isCorrect(s.puzzle, candidate)) {
            _session.value = s.copy(phase = PuzzlePhase.CORRECT)
            // Award exactly once; GameRules ignores already-completed puzzles.
            persist(GameRules.completePuzzle(_gameState.value, s.puzzle))
        } else {
            _session.value = s.copy(phase = PuzzlePhase.INCORRECT)
        }
    }

    /**
     * Back from an incorrect attempt: clears the wrong letters so the player can rebuild
     * the word cleanly. Hint-locked slots stay revealed.
     */
    fun retryAfterMistake() {
        updateSession { s ->
            if (s.phase != PuzzlePhase.INCORRECT) return@updateSession s
            val slots = s.slots.toMutableList()
            val source = s.slotSource.toMutableList()
            slots.indices.forEach { i ->
                if (i !in s.lockedSlots) {
                    slots[i] = null
                    source[i] = null
                }
            }
            s.copy(slots = slots, slotSource = source, phase = PuzzlePhase.INPUT)
        }
    }

    /**
     * Buys a hint. Coins are only deducted when [GameRules.purchaseHint] returns a new
     * state (i.e. the hint is new AND affordable) — requirements 5.5, 5.6, 5.7.
     */
    fun buyHint() {
        val s = _session.value ?: return
        if (s.phase != PuzzlePhase.INPUT) return
        if (_gameState.value.isPuzzleHinted(s.puzzle.id)) {
            _message.value = "Indice déjà obtenu pour cette énigme."
            return
        }
        val next = GameRules.purchaseHint(_gameState.value, s.puzzle)
        if (next == null) {
            _message.value = "Pièces insuffisantes pour acheter un indice."
            return
        }
        persist(next)
        revealOneLetter(s)
    }

    /** Reveals the leftmost slot that is empty or holds a wrong letter; then locks it. */
    private fun revealOneLetter(s: PuzzleSession) {
        val answerChars = s.puzzle.answer.toList()
        val target = s.slots.indices.firstOrNull { i ->
            i !in s.lockedSlots && (s.slots[i] == null || s.slots[i] != answerChars[i])
        } ?: return
        val slots = s.slots.toMutableList().also { it[target] = answerChars[target] }
        val source = s.slotSource.toMutableList().also { it[target] = null }
        _session.value = s.copy(
            slots = slots,
            slotSource = source,
            lockedSlots = s.lockedSlots + target,
        )
    }

    /** Called when leaving the puzzle screen so a new puzzle starts clean. */
    fun closeSession() {
        _session.value = null
    }

    // --- settings ---------------------------------------------------------

    fun setSoundEnabled(enabled: Boolean) {
        persist(_gameState.value.copy(settings = _gameState.value.settings.copy(soundEnabled = enabled)))
    }

    fun setMusicEnabled(enabled: Boolean) {
        persist(_gameState.value.copy(settings = _gameState.value.settings.copy(musicEnabled = enabled)))
    }

    /** Dev/testing helper: wipes local progress. */
    fun resetProgress() {
        persist(GameState())
        _session.value = null
        _message.value = "Progression réinitialisée."
    }
}
