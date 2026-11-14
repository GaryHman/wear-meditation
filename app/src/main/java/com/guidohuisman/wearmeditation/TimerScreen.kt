package com.guidohuisman.wearmeditation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText

private val sessionLengthOptions = listOf(1, 5, 10, 15, 20, 30, 45)
private val intervalOptions = listOf(0, 1, 2, 3, 5, 10)

@Composable
fun MeditationApp(settings: SessionSettings) {
    val state by SessionEngine.state.collectAsState()
    MaterialTheme {
        if (state.active) {
            SessionScreen(state)
        } else {
            SetupScreen(settings)
        }
    }
}

@Composable
private fun SetupScreen(settings: SessionSettings) {
    var sessionMinutes by remember { mutableStateOf(settings.sessionMinutes) }
    var intervalMinutes by remember { mutableStateOf(settings.intervalMinutes) }
    var soundEnabled by remember { mutableStateOf(settings.soundEnabled) }

    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                StepperRow(
                    label = "Length",
                    value = "$sessionMinutes min",
                    onDecrease = {
                        sessionMinutes = neighbour(sessionLengthOptions, sessionMinutes, -1)
                    },
                    onIncrease = {
                        sessionMinutes = neighbour(sessionLengthOptions, sessionMinutes, 1)
                    },
                )
            }
            item {
                StepperRow(
                    label = "Interval bell",
                    value = if (intervalMinutes == 0) "off" else "$intervalMinutes min",
                    onDecrease = {
                        intervalMinutes = neighbour(intervalOptions, intervalMinutes, -1)
                    },
                    onIncrease = {
                        intervalMinutes = neighbour(intervalOptions, intervalMinutes, 1)
                    },
                )
            }
            item {
                Chip(
                    onClick = { soundEnabled = !soundEnabled },
                    label = { Text(if (soundEnabled) "Sound: on" else "Sound: off") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Button(
                    onClick = {
                        settings.sessionMinutes = sessionMinutes
                        settings.intervalMinutes = intervalMinutes
                        settings.soundEnabled = soundEnabled
                        SessionEngine.start(sessionMinutes, intervalMinutes, soundEnabled)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Start")
                }
            }
        }
    }
}

@Composable
private fun SessionScreen(state: SessionState) {
    Scaffold(timeText = { TimeText() }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatRemaining(state.remainingMillis),
                style = MaterialTheme.typography.display2,
            )
            Text(
                text = if (state.paused) "Paused" else "Meditating",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        if (state.paused) SessionEngine.resume() else SessionEngine.pause()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (state.paused) "Resume" else "Pause")
                }
                Button(
                    onClick = { SessionEngine.stop() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompactChip(onClick = onDecrease, label = { Text("-") })
            Text(
                text = value,
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            CompactChip(onClick = onIncrease, label = { Text("+") })
        }
    }
}

private fun neighbour(options: List<Int>, current: Int, step: Int): Int {
    val index = options.indexOf(current)
    if (index < 0) return current
    return options[(index + step).coerceIn(0, options.lastIndex)]
}

private fun formatRemaining(millis: Long): String {
    val totalSeconds = (millis + 999) / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
