package com.imux.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imux.gamecore.VersionInfo
import com.imux.gamecore.loadVersionCatalog
import com.imux.launcher.performance.MemoryManager
import com.imux.launcher.settings.AnimationSettings
import com.imux.launcher.settings.CardAnimationStyle
import com.imux.launcher.settings.ControlElement
import com.imux.launcher.settings.ControlElementType
import com.imux.launcher.settings.ControlLayout
import com.imux.launcher.settings.LauncherOrientation
import com.imux.launcher.settings.LauncherSettings
import com.imux.launcher.settings.LauncherSettingsViewModel
import com.imux.launcher.settings.LauncherTheme
import com.imux.launcher.settings.LoadingAnimationStyle
import com.imux.launcher.settings.TransitionStyle
import com.imux.launcher.startup.DelayedStartupTask
import com.imux.launcher.startup.StartupCoordinator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val settingsViewModel: LauncherSettingsViewModel by viewModels()
    private var versions by mutableStateOf<List<VersionInfo>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { versions = loadVersionCatalog() }
        setContent { ImuxApp(versions, settingsViewModel) }
    }
}

@Composable
private fun ImuxApp(versions: List<VersionInfo>?, settingsViewModel: LauncherSettingsViewModel) {
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val dark = androidx.compose.foundation.isSystemInDarkTheme()
    val colors = when {
        settings.dynamicColors && android.os.Build.VERSION.SDK_INT >= 31 && dark -> dynamicDarkColorScheme(context)
        settings.dynamicColors && android.os.Build.VERSION.SDK_INT >= 31 -> dynamicLightColorScheme(context)
        dark -> androidx.compose.material3.darkColorScheme()
        else -> androidx.compose.material3.lightColorScheme()
    }
    MaterialTheme(colorScheme = colors) {
        StartupGate(settings) {
            LauncherNavigation(versions, settings, settingsViewModel)
        }
    }
}

@Composable
private fun StartupGate(settings: LauncherSettings, content: @Composable () -> Unit) {
    var ready by rememberSaveable { mutableStateOf(false) }
    var stage by rememberSaveable { mutableStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    val tasks = remember {
        listOf(
            DelayedStartupTask("configuration", "Checking configuration", 180),
            DelayedStartupTask("updates", "Checking updates", 220),
            DelayedStartupTask("resources", "Loading resources", 180),
            DelayedStartupTask("runtime", "Preparing runtime", 220),
            DelayedStartupTask("ready", "Ready", 120)
        )
    }
    LaunchedEffect(Unit) {
        if (!ready) {
            StartupCoordinator(tasks).run { index, total, _ ->
                stage = index
                progress = index.toFloat() / total
            }
            delay(if (settings.animations.animationEnabled) settings.loading.minDurationMs else 0)
            progress = 1f
            ready = true
        }
    }
    Crossfade(targetState = ready, label = "startup") { done ->
        if (done) content() else LoadingScreen(settings, tasks.getOrElse(stage) { tasks.last() }.label, progress)
    }
}

@Composable
private fun LoadingScreen(settings: LauncherSettings, status: String, progress: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition(label = "loading")
    val rotation by transition.animateFloat(0f, 360f, infiniteRepeatable(tween(1100), RepeatMode.Restart), label = "rotation")
    val pulse by transition.animateFloat(0.65f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "pulse")
    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Imux", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            when (settings.animations.loadingAnimationStyle) {
                LoadingAnimationStyle.LINEAR -> androidx.compose.material3.LinearProgressIndicator(progress = { progress }, Modifier.fillMaxWidth(0.55f))
                LoadingAnimationStyle.PULSING -> Box(Modifier.size(64.dp).background(primaryColor.copy(alpha = pulse), CircleShape))
                LoadingAnimationStyle.DOTS -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { repeat(3) { Box(Modifier.size(10.dp).background(primaryColor.copy(alpha = if (it == (rotation / 120).toInt() % 3) 1f else .35f), CircleShape)) } }
                LoadingAnimationStyle.WAVE, LoadingAnimationStyle.SHIMMER -> androidx.compose.material3.LinearProgressIndicator(progress = { (progress + (rotation / 360f) * .15f).coerceAtMost(1f) }, Modifier.fillMaxWidth(0.55f))
                LoadingAnimationStyle.CIRCULAR -> Canvas(Modifier.size(58.dp).rotate(rotation)) { drawArc(primaryColor, -60f, 280f, false, Stroke(6.dp.toPx(), cap = StrokeCap.Round)) }
            }
            Spacer(Modifier.height(20.dp))
            Text(status, style = MaterialTheme.typography.titleMedium)
            if (settings.loading.showStatus) Text("Подготавливаем платформу", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (settings.loading.showProgress) Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun LauncherNavigation(versions: List<VersionInfo>?, settings: LauncherSettings, vm: LauncherSettingsViewModel) {
    val navController = rememberNavController()
    val wide = LocalConfiguration.current.screenWidthDp >= 600
    val start = "home"
    if (wide) {
        Row(Modifier.fillMaxSize()) {
            NavigationRail(Modifier.fillMaxHeight()) {
                Spacer(Modifier.height(12.dp))
                NavigationRailItem(selected = false, onClick = { navController.navigate("home") }, icon = { Icon(Icons.Default.Gamepad, null) }, label = { Text("Game") })
                NavigationRailItem(selected = false, onClick = { navController.navigate("settings") }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
            }
            NavHost(navController, startDestination = start, Modifier.weight(1f)) {
                composable("home") { HomeScreen(versions, settings, navController) }
                composable("settings") { SettingsScreen(settings, vm, navController) }
                composable("controls") { ControlEditorScreen(settings, navController) }
            }
        }
    } else {
        NavHost(navController, startDestination = start, Modifier.fillMaxSize()) {
            composable("home") { HomeScreen(versions, settings, navController) }
            composable("settings") { SettingsScreen(settings, vm, navController) }
            composable("controls") { ControlEditorScreen(settings, navController) }
        }
    }
}

@Composable
private fun HomeScreen(versions: List<VersionInfo>?, settings: LauncherSettings, navController: androidx.navigation.NavHostController) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var launching by rememberSaveable { mutableStateOf(false) }
    val selected = versions?.firstOrNull { it.id == selectedId } ?: versions?.firstOrNull()
    val corner = settings.cardCornerRadius.dp
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Imux", fontWeight = FontWeight.Bold) },
            actions = { IconButton(onClick = { navController.navigate("settings") }) { Icon(Icons.Default.Settings, "Settings") } }
        )
    }) { padding ->
        Row(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(Modifier.weight(1.2f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(shape = RoundedCornerShape(corner), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth().animateContentSize()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Выбранная сборка", style = MaterialTheme.typography.labelLarge)
                        Text(selected?.name ?: "Нет доступных сборок", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text(selected?.repository ?: "Проверка каталога…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Button(enabled = selected != null && !launching, onClick = { launching = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.PlayArrow, null)
                            Spacer(Modifier.size(8.dp))
                            Text(if (launching) "Подготовка…" else "Запустить")
                        }
                        AnimatedVisibility(launching, enter = fadeIn(), exit = fadeOut()) {
                            Text("GameRuntime пока не подключён. Конфигурация готова для следующего этапа.", Modifier.padding(top = 10.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text("RAM ${settings.performance.memoryMb} MB") }, leadingIcon = { Icon(Icons.Default.Memory, null) })
                    AssistChip(onClick = {}, label = { Text("FPS ${settings.performance.fpsLimit}") })
                }
            }
            Card(Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(corner)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Версии", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    if (versions == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
                            items(versions, key = { it.id }) { version ->
                                VersionCard(version, version.id == (selected?.id), settings, onClick = { selectedId = version.id })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VersionCard(version: VersionInfo, selected: Boolean, settings: LauncherSettings, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = {
        Column { Text(version.name, fontWeight = FontWeight.Medium); Text(if (version.isCurrent) "Текущая сборка" else "Архив", style = MaterialTheme.typography.labelSmall) }
    }, leadingIcon = if (selected) ({ Icon(Icons.Default.Check, null) }) else null, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun SettingsScreen(settings: LauncherSettings, vm: LauncherSettingsViewModel, navController: androidx.navigation.NavHostController) {
    val scroll = rememberScrollState()
    Scaffold(topBar = { TopAppBar(title = { Text("Настройки") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(scroll).padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsSection("General") {
                SettingRow("Тема") { EnumMenu(settings.theme.name) { name -> vm.update { it.copy(theme = LauncherTheme.valueOf(name)) } } }
                SettingRow("Динамические цвета") { Switch(checked = settings.dynamicColors, onCheckedChange = { vm.update { s -> s.copy(dynamicColors = it) } }) }
                SettingRow("Подтверждать запуск") { Switch(checked = settings.confirmLaunch, onCheckedChange = { vm.update { s -> s.copy(confirmLaunch = it) } }) }
                SettingRow("Ориентация") { EnumMenu(settings.orientation.name) { name -> vm.update { it.copy(orientation = LauncherOrientation.valueOf(name)) } } }
            }
            SettingsSection("Appearance") {
                SettingRow("Компактная плотность") { Switch(checked = settings.compactDensity, onCheckedChange = { vm.update { s -> s.copy(compactDensity = it) } }) }
                SettingRow("Визуальные эффекты") { Switch(checked = settings.visualEffects, onCheckedChange = { vm.update { s -> s.copy(visualEffects = it) } }) }
                SliderSetting("Скругление карточек", settings.cardCornerRadius.toFloat(), 8f..36f) { vm.update { s -> s.copy(cardCornerRadius = it.toInt()) } }
            }
            SettingsSection("Animations") {
                SettingRow("Анимации") { Switch(checked = settings.animations.animationEnabled, onCheckedChange = { vm.update { s -> s.copy(animations = s.animations.copy(animationEnabled = it)) } }) }
                SliderSetting("Скорость", settings.animations.animationSpeed, .5f..2f, 0.5f) { vm.update { s -> s.copy(animations = s.animations.copy(animationSpeed = it)) } }
                SettingRow("Переходы") { EnumMenu(settings.animations.transitionStyle.name) { name -> vm.update { it.copy(animations = it.animations.copy(transitionStyle = TransitionStyle.valueOf(name))) } } }
                SettingRow("Карточки") { EnumMenu(settings.animations.cardAnimationStyle.name) { name -> vm.update { it.copy(animations = it.animations.copy(cardAnimationStyle = CardAnimationStyle.valueOf(name))) } } }
                SettingRow("Загрузка") { EnumMenu(settings.animations.loadingAnimationStyle.name) { name -> vm.update { it.copy(animations = it.animations.copy(loadingAnimationStyle = LoadingAnimationStyle.valueOf(name))) } } }
            }
            SettingsSection("Loading Screen") {
                SettingRow("Показывать прогресс") { Switch(checked = settings.loading.showProgress, onCheckedChange = { vm.update { s -> s.copy(loading = s.loading.copy(showProgress = it)) } }) }
                SettingRow("Показывать статус") { Switch(checked = settings.loading.showStatus, onCheckedChange = { vm.update { s -> s.copy(loading = s.loading.copy(showStatus = it)) } }) }
                SliderSetting("Минимальная длительность", settings.loading.minDurationMs.toFloat(), 0f..3000f) { vm.update { s -> s.copy(loading = s.loading.copy(minDurationMs = it.toLong())) } }
            }
            SettingsSection("Performance") {
                MemorySetting(settings, vm)
                SliderSetting("Лимит FPS", settings.performance.fpsLimit.toFloat(), 30f..120f, 2f) { vm.update { s -> s.copy(performance = s.performance.copy(fpsLimit = it.toInt())) } }
                SettingRow("Экономия энергии") { Switch(checked = settings.performance.powerSaving, onCheckedChange = { vm.update { s -> s.copy(performance = s.performance.copy(powerSaving = it)) } }) }
            }
            SettingsSection("Controls") {
                OutlinedButton(onClick = { navController.navigate("controls") }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Tune, null); Spacer(Modifier.size(8.dp)); Text("Открыть редактор раскладки") }
            }
            SettingsSection("Runtime") {
                Text("Настройки не зависят от Minecraft/Mojang. Runtime будет подключён через RuntimeConfiguration и GameRuntime.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MemorySetting(settings: LauncherSettings, vm: LauncherSettingsViewModel) {
    val context = LocalContext.current
    val manager = remember(context) { MemoryManager(context) }
    val range = remember { manager.safeRangeMb() }
    val current = settings.performance.memoryMb.coerceIn(range)
    val steps = ((range.last - range.first) / 256 - 1).coerceAtLeast(0)
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Память для runtime"); Text(manager.formatMb(current), fontWeight = FontWeight.SemiBold) }
        Slider(value = current.toFloat(), onValueChange = { value -> vm.update { s -> s.copy(performance = s.performance.copy(memoryMb = manager.sanitize(value.toInt()))) } }, valueRange = range.first.toFloat()..range.last.toFloat(), steps = steps)
        Text("Безопасный диапазон: ${manager.formatMb(range.first)} — ${manager.formatMb(range.last)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); content() }
    }
}

@Composable
private fun SettingRow(label: String, control: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Text(label, Modifier.weight(1f)); control() }
}

@Composable
private fun SliderSetting(label: String, value: Float, range: ClosedFloatingPointRange<Float>, step: Float = 1f, onChange: (Float) -> Unit) {
    Column { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text(if (step < 1f) "%.1f".format(value) else value.toInt().toString()) }; Slider(value, onValueChange = onChange, valueRange = range, steps = ((range.endInclusive - range.start) / step).toInt().coerceAtLeast(0) - 1) }
}

@Composable
private fun EnumMenu(value: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilledTonalButton(onClick = { expanded = true }) { Text(value.replace('_', ' ')) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(value).plus(
                when (value) {
                    "MATERIAL_YOU" -> LauncherTheme.entries.map { it.name }
                    "LANDSCAPE" -> LauncherOrientation.entries.map { it.name }
                    "FADE_SCALE" -> TransitionStyle.entries.map { it.name }
                    "CIRCULAR" -> LoadingAnimationStyle.entries.map { it.name }
                    "FADE" -> CardAnimationStyle.entries.map { it.name }
                    else -> emptyList()
                }
            ).distinct().forEach { option -> DropdownMenuItem(text = { Text(option.replace('_', ' ')) }, onClick = { expanded = false; onSelect(option) }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlEditorScreen(settings: LauncherSettings, navController: androidx.navigation.NavHostController) {
    var layout by remember { mutableStateOf(ControlLayout()) }
    var selected by remember { mutableStateOf<ControlElement?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("Редактор управления") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }, actions = { IconButton(onClick = { layout = ControlLayout() }) { Icon(Icons.Default.Build, "Reset") } }) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(16.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))) {
            layout.elements.forEach { element ->
                if (element.visible) {
                    Box(Modifier.align(Alignment.TopStart).padding(start = (element.x * 80).dp, top = (element.y * 40).dp).size((element.width * 260).dp, (element.height * 180).dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = element.alpha), RoundedCornerShape(20.dp)).pointerInput(element.id) { detectDragGestures(onDragStart = { selected = element }, onDrag = { _, _ -> }) }) {
                        Text(element.type.name, Modifier.align(Alignment.Center), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Column(Modifier.align(Alignment.BottomCenter).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Перемещение/размер/прозрачность подготовлены моделью ControlElement")
                OutlinedButton(onClick = { showSheet = true }) { Text("Настроить элемент") }
            }
        }
    }
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            Column(Modifier.padding(24.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ControlLayout", style = MaterialTheme.typography.headlineSmall)
                Text("Элементы: ${layout.elements.size}")
                if (selected != null) Text("Выбран: ${selected!!.id}") else Text("Выберите элемент на поле")
                Button(onClick = { showSheet = false }) { Text("Готово") }
            }
        }
    }
}
