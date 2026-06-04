package game.domain.model

import kotlinx.serialization.Serializable

/**
 * Seems to only contain integers type, and is only 0 or 1.
 * I think this is a "tutorial state context", basically to show
 * the player's tutorial progress.
 */
@Serializable
data class TutorialData(
    val battleArena: Int = 0,
    val kshatriya: Int = 0,
    val formation: Int = 0,
    val mainMenu: Int = 0,
    val store: Int = 0,
    val deitySelect: Int = 0,
)
