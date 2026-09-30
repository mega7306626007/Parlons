package com.francofun

/**
 * Phase 4 seed of the centralized learning engine.
 *
 * A [LearnerSnapshot] is a pure, read-only derivation of [Store] state:
 * no screen may scatter its own copy of "what does the learner need".
 * Every current and future consumer (Up Next card, daily plan, adaptive
 * difficulty, mistake engine) reads from here, so learner intelligence
 * has exactly one home. Nothing here persists anything.
 */
enum class NextAction { FIRST_STEPS, REVIEW_DUE, FIX_MISTAKES, CRUISING, DAILY_GOAL, KEEP_STREAK }

/** The single most-repeated mistake, for "you keep mixing up X" messaging. */
data class TopMistake(val fr: String, val meaning: String, val count: Int)

data class LearnerSnapshot(
    val dueN: Int,
    val mistakesN: Int,
    val topMistake: TopMistake?,
    val goalLeft: Int,
    val hearts: Int,
    val streak: Int,
    val todayXp: Int,
    val dailyGoalXp: Int,
    val lessonsDone: Int,
    val strain: Float
) {
    /** Single top priority: onboarding first, then memory (due words),
     * then errors, then cruising detection (sustained ≥95% over 5+ lessons),
     * then the daily goal, then streak-keeping. */
    fun nextAction(): NextAction = when {
        lessonsDone == 0 -> NextAction.FIRST_STEPS
        dueN > 0 -> NextAction.REVIEW_DUE
        mistakesN > 0 -> NextAction.FIX_MISTAKES
        strain >= 0.95f && lessonsDone >= 5 -> NextAction.CRUISING
        goalLeft > 0 -> NextAction.DAILY_GOAL
        else -> NextAction.KEEP_STREAK
    }
}

fun Store.snapshot(): LearnerSnapshot {
    val top = mistakes.maxByOrNull { it.value.count }?.value
    return LearnerSnapshot(
        dueN = allDueCount(srs),
        mistakesN = mistakes.size,
        topMistake = top?.let { TopMistake(fr = it.fr, meaning = it.meaning, count = it.count) },
        goalLeft = (dailyGoalXp - todayXp).coerceAtLeast(0),
        hearts = hearts,
        streak = streak,
        todayXp = todayXp,
        dailyGoalXp = dailyGoalXp,
        lessonsDone = lessonsDone,
        strain = strain()
    )
}
