package com.imux.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.imux.gamecore.VersionInfo
import com.imux.gamecore.VersionManager
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ImuxTheme { ImuxApp() } }
    }
}

@Composable
private fun ImuxApp() {
    var showSplash by remember { mutableStateOf(true) }
    if (showSplash) SplashScreen { showSplash = false } else MainScreen()
}

@Composable
private fun SplashScreen(onFinished: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "splash")
    val rotation by transition.animateFloat(
        0f,
        360f,
        infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Imux", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(28.dp))
            Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(56.dp).rotate(rotation)) {
                    drawArc(
                        color = MaterialTheme.colorScheme.primary,
                        startAngle = -55f,
                        sweepAngle = 275f,
                        useCenter = false,
                        style = Stroke(6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Проверка обновлений…", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Подготавливаем лаунчер",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private suspend fun loadVersions(): List<VersionInfo> {
    val manager = VersionManager()
    return try {
        runCatching {
            val latest = manager.fetchLatest()
            listOf(latest) + manager.fetchArchives()
        }.getOrDefault(
            listOf(
                VersionInfo("main", "Imux • main", "main", "cuuw619-maker/Imux", true)
            )
        )
    } finally {
        manager.close()
    }
}

@Composable
private fun MainScreen() {
    var versions by remember {
        mutableStateOf(
            listOf(
                VersionInfo("main", "Imux • main", "main", "cuuw619-maker/Imux", true)
            )
        )
    }

    LaunchedEffect(Unit) {
        versions = loadVersions()
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Выберите версию",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
            items(versions, key = { it.id }) { VersionCard(it) }
        }
    }
}

@Composable
private fun VersionCard(version: VersionInfo) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (version.isCurrent) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(version.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (version.isCurrent) "Актуальная сборка" else "Архивная версия",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    version.repository,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (version.isCurrent) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
        }
    }
}

@Composable
private fun ImuxTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
