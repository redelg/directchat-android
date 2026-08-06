package com.codergang.chatdirecto.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

object MensajiSplashMotion {

    const val LINE_DURATION_MS = 500
    const val LINE_DELAY_MS = 0
    const val PULSE_DURATION_MS = 800
    const val PULSE_DELAY_MS = 500
    const val PULSE_PEAK_FRACTION = 0.4f
    const val PULSE_SCALE_PEAK = 1.18f

    val LineEasing: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val PulseEasing: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

    val Sender = Color(0xFFFFFFFF)
    val Receiver = Color(0xFFFF6B68)
    val Connector = Color(0xB3FFFFFF)

    @Composable
    fun rememberLineProgress(play: Boolean): State<Float> {
        val anim = remember { Animatable(if (play) 0f else 1f) }
        LaunchedEffect(play) {
            if (play) {
                anim.snapTo(0f)
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = LINE_DURATION_MS,
                        delayMillis = LINE_DELAY_MS,
                        easing = LineEasing
                    )
                )
            }
        }
        return anim.asState()
    }

    @Composable
    fun rememberReceiverScale(play: Boolean): State<Float> {
        val anim = remember { Animatable(1f) }
        LaunchedEffect(play) {
            if (play) {
                delay(PULSE_DELAY_MS.toLong())
                anim.animateTo(
                    targetValue = PULSE_SCALE_PEAK,
                    animationSpec = tween(
                        durationMillis = (PULSE_DURATION_MS * PULSE_PEAK_FRACTION).toInt(),
                        easing = PulseEasing
                    )
                )
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = (PULSE_DURATION_MS * (1f - PULSE_PEAK_FRACTION)).toInt(),
                        easing = PulseEasing
                    )
                )
            }
        }
        return anim.asState()
    }
}
