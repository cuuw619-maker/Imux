package com.imux.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imux.gamecore.VersionInfo
import com.imux.gamecore.loadVersionCatalog
import com.imux.launcher.settings.AnimationProfile
import com.imux.launcher.settings.LauncherSettings
import com.imux.launcher.settings.LauncherSettingsViewModel
import com.imux.launcher.startup.DelayedStartupTask
import com.imux.launcher.startup.StartupCoordinator
import com.imux.launcher.ui.FullscreenControlEditor
import com.imux.launcher.ui.ImuxTheme
import com.imux.launcher.ui.SettingsHubScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val settingsViewModel: LauncherSettingsViewModel by viewModels()
    private var versions by mutableStateOf<List<VersionInfo>?>(null)
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); lifecycleScope.launch { versions = loadVersionCatalog() }; setContent { ImuxRoot(versions, settingsViewModel) } }
}

@Composable private fun ImuxRoot(versions: List<VersionInfo>?, vm: LauncherSettingsViewModel) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    ImuxTheme(settings.theme, settings.dynamicColors) { StartupGate(settings) { LauncherShell(versions, settings, vm) } }
}

@Composable private fun StartupGate(settings: LauncherSettings, content: @Composable () -> Unit) {
    var ready by rememberSaveable { mutableStateOf(false) }; var progress by remember { mutableFloatStateOf(0f) }; var status by remember { mutableStateOf("Checking configuration") }
    val tasks = remember { listOf(DelayedStartupTask("configuration", "Checking configuration", 100), DelayedStartupTask("updates", "Checking updates", 140), DelayedStartupTask("resources", "Loading resources", 120), DelayedStartupTask("runtime", "Preparing runtime", 140), DelayedStartupTask("renderer", "Initializing renderer", 100)) }
    LaunchedEffect(Unit) { StartupCoordinator(tasks).run { index, total, task -> status = task.label; progress = index.toFloat() / total }; progress = 1f; ready = true }
    AnimatedContent(targetState = ready, label = "startup") { isReady -> if (isReady) content() else LoadingScreen(settings, status, progress) }
}

@Composable private fun LoadingScreen(settings: LauncherSettings, status: String, progress: Float) {
    val animatedProgress by animateFloatAsState(progress, if (settings.animations.profile == AnimationProfile.OFF) tween(0) else tween(220), label = "startup-progress")
    Surface(Modifier.fillMaxSize()) { Column(Modifier.fillMaxSize().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Imux", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold); Spacer(Modifier.height(28.dp)); LinearProgressIndicator(progress = { animatedProgress }, modifier = Modifier.fillMaxWidth(.55f)); Spacer(Modifier.height(16.dp)); Text(status, style = MaterialTheme.typography.titleMedium); Text("${(animatedProgress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    } }
}

@Composable private fun LauncherShell(versions: List<VersionInfo>?, settings: LauncherSettings, vm: LauncherSettingsViewModel) {
    val nav = rememberNavController(); val wide = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600
    if (wide) Row(Modifier.fillMaxSize()) { NavigationRail { Spacer(Modifier.height(12.dp)); NavigationRailItem(selected = false, onClick = { nav.navigate("home") }, icon = { Icon(Icons.Default.Gamepad, null) }, label = { Text("Game") }); NavigationRailItem(selected = false, onClick = { nav.navigate("settings") }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }) }; NavHost(nav, "home", Modifier.weight(1f)) { routes(versions, settings, vm, nav) } }
    else NavHost(nav, "home", Modifier.fillMaxSize()) { routes(versions, settings, vm, nav) }
}

private fun androidx.navigation.NavGraphBuilder.routes(versions: List<VersionInfo>?, settings: LauncherSettings, vm: LauncherSettingsViewModel, nav: androidx.navigation.NavHostController) {
    composable("home") { HomeScreen(versions, settings, nav) }; composable("settings") { SettingsHubScreen(settings, vm, nav) { nav.navigate("controls") } }; composable("controls") { FullscreenControlEditor(nav) }
}

@Composable private fun HomeScreen(versions: List<VersionInfo>?, settings: LauncherSettings, nav: androidx.navigation.NavHostController) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }; var launching by rememberSaveable { mutableStateOf(false) }; val selected = versions?.firstOrNull { it.id == selectedId } ?: versions?.firstOrNull()
    Row(Modifier.fillMaxSize().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        Column(Modifier.weight(1.2f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text("Imux", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); IconButton(onClick = { nav.navigate("settings") }) { Icon(Icons.Default.Settings, "Settings") } }
            Card(Modifier.fillMaxWidth().animateContentSize()) { Column(Modifier.padding(24.dp)) { Text("Selected build", style = MaterialTheme.typography.labelLarge); Text(selected?.name ?: "No build available", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold); Text(selected?.repository ?: "Checking catalog…", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(20.dp)); Button(onClick = { launching = true }, enabled = selected != null && !launching, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text(if (launching) "Preparing…" else "Launch") }; if (launching) Text("Runtime integration point is ready; GameRuntime will be connected in the next stage.", Modifier.padding(top = 10.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            Text("RAM ${settings.performance.memoryMb} MB · ${settings.performance.fpsLimit} FPS", style = MaterialTheme.typography.labelLarge)
        }
        Card(Modifier.weight(1f).fillMaxHeight()) { Column(Modifier.padding(16.dp)) { Text("Game builds", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); if (versions == null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(versions, key = { it.id }) { version -> ListItem(headlineContent = { Text(version.name) }, supportingContent = { Text(version.repository) }, leadingContent = { Icon(Icons.Default.Gamepad, null) }, modifier = Modifier.fillMaxWidth(), tonalElevation = if (version.id == selected?.id) 2.dp else 0.dp) } } } }
    }
}
