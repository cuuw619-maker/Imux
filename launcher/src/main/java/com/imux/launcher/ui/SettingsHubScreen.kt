package com.imux.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imux.launcher.performance.MemoryManager
import com.imux.launcher.settings.*

private enum class SettingsCategory(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    LAUNCHER("Launcher", Icons.Default.Settings), GAME("Game", Icons.Default.Gamepad), CONTROLS("Controls", Icons.Default.TouchApp),
    GRAPHICS("Graphics", Icons.Default.Build), RUNTIME("Runtime", Icons.Default.Memory), DOWNLOADS("Downloads", Icons.Default.Download),
    LOGS("Logs & Diagnostics", Icons.Default.List), APPEARANCE("Appearance", Icons.Default.Palette)
}

@Composable
fun SettingsHubScreen(settings: LauncherSettings, vm: LauncherSettingsViewModel, navController: NavHostController, onControls: () -> Unit) {
    var category by rememberSaveable { mutableStateOf(SettingsCategory.LAUNCHER) }
    Row(Modifier.fillMaxSize()) {
        NavigationRail { SettingsCategory.entries.forEach { item -> NavigationRailItem(selected = category == item, onClick = { category = item }, icon = { Icon(item.icon, null) }, label = { Text(item.label, maxLines = 1) }) } }
        VerticalDivider()
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text(category.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(12.dp))
            when (category) {
                SettingsCategory.LAUNCHER -> LauncherSettingsPage(settings, vm)
                SettingsCategory.GAME -> GameSettingsPage()
                SettingsCategory.CONTROLS -> ControlsSettingsPage(onControls)
                SettingsCategory.GRAPHICS -> GraphicsSettingsPage()
                SettingsCategory.RUNTIME -> RuntimeSettingsPage(settings)
                SettingsCategory.DOWNLOADS -> PlaceholderSettingsPage("Загрузки и управление игровыми ресурсами")
                SettingsCategory.LOGS -> PlaceholderSettingsPage("Ограниченный буфер логов, фильтры и диагностика Renderer/Runtime")
                SettingsCategory.APPEARANCE -> AppearanceSettingsPage(settings, vm)
            }
        }
    }
}

@Composable private fun LauncherSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) { SettingsList {
    SettingSwitch("Dynamic Color", s.dynamicColors) { value -> vm.update { state -> state.copy(dynamicColors = value) } }
    SettingSwitch("Confirm launch", s.confirmLaunch) { value -> vm.update { state -> state.copy(confirmLaunch = value) } }
    SettingSlider("Card corner radius", s.cardCornerRadius.toFloat(), 8f..36f) { v -> vm.update { state -> state.copy(cardCornerRadius = v.toInt()) } }
    SettingEnum("Theme", s.theme.name, LauncherTheme.entries.map { it.name }) { v -> vm.update { state -> state.copy(theme = LauncherTheme.valueOf(v)) } }
    SettingEnum("Animations", s.animations.profile.name, AnimationProfile.entries.map { it.name }) { v -> vm.update { state -> state.copy(animations = state.animations.copy(profile = AnimationProfile.valueOf(v), animationEnabled = v != "OFF")) } }
    SettingSlider("Animation speed", s.animations.animationSpeed, .5f..2f, .1f) { v -> vm.update { state -> state.copy(animations = state.animations.copy(animationSpeed = v)) } }
    SettingSwitch("Automatic update checks", true) { }
    SettingSwitch("Load resources on startup", true) { }
} }

@Composable private fun AppearanceSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) { SettingsList {
    SettingEnum("Style", s.theme.name, LauncherTheme.entries.map { it.name }) { v -> vm.update { state -> state.copy(theme = LauncherTheme.valueOf(v)) } }
    SettingSwitch("Visual effects", s.visualEffects) { value -> vm.update { state -> state.copy(visualEffects = value) } }
    SettingSwitch("Compact density", s.compactDensity) { value -> vm.update { state -> state.copy(compactDensity = value) } }
    SettingSlider("Transparency", s.interfaceTransparency, 0f..0.35f, .01f) { v -> vm.update { state -> state.copy(interfaceTransparency = v) } }
} }

@Composable private fun GameSettingsPage() { SettingsList {
    SettingEnum("Launch mode", "NORMAL", listOf("NORMAL", "SAFE_MODE", "DEBUG")) { }; SettingSlider("FPS limit", 60f, 30f..120f, 10f) { }; SettingSlider("Render distance", 8f, 4f..16f, 1f) { }; SettingSlider("Simulation distance", 6f, 4f..12f, 1f) { }
    SettingEnum("Particles", "DECREASED", listOf("MINIMAL", "DECREASED", "ALL")) { }; SettingSwitch("Shadows", false) { }; SettingEnum("Lighting", "MEDIUM", listOf("LOW", "MEDIUM", "HIGH")) { }; SettingEnum("Textures", "MEDIUM", listOf("LOW", "MEDIUM", "HIGH")) { }; SettingSwitch("VSync / frame pacing", true) { }; SettingEnum("Anti-aliasing", "NONE", listOf("NONE", "FXAA")) { }
} }

@Composable private fun GraphicsSettingsPage() { SettingsList {
    SettingEnum("Renderer", "AUTO", listOf("AUTO", "OPENGL_ES", "VULKAN")) { }; SettingEnum("Quality", "LOW", listOf("LOW", "MEDIUM", "HIGH", "CUSTOM")) { }; SettingSlider("Resolution scale", .85f, .5f..1f, .05f) { }; SettingSlider("Texture memory", 384f, 128f..768f, 64f) { }; SettingEnum("Shader complexity", "LOW", listOf("LOW", "MEDIUM", "HIGH")) { }; SettingSwitch("Experimental features", false) { }
    Text("Для Snapdragon 685 профиль Low является базовым: ограниченная сложность shader, texture memory и overdraw.", color = MaterialTheme.colorScheme.onSurfaceVariant)
} }

@Composable private fun RuntimeSettingsPage(s: LauncherSettings) {
    val context = androidx.compose.ui.platform.LocalContext.current; val manager = remember(context) { MemoryManager(context) }; val range = remember(manager) { manager.safeRangeMb() }; var memory by remember(s.performance.memoryMb) { mutableFloatStateOf(manager.sanitize(s.performance.memoryMb).toFloat()) }
    SettingsList { Text("Runtime budget", style = MaterialTheme.typography.titleMedium); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Memory"); Text(manager.formatMb(memory.toInt()), fontWeight = FontWeight.SemiBold) }; Slider(value = memory, onValueChange = { memory = manager.sanitize(it.toInt()).toFloat() }, valueRange = range.first.toFloat()..range.last.toFloat()); Text("Safe range: ${manager.formatMb(range.first)} – ${manager.formatMb(range.last)}", style = MaterialTheme.typography.labelSmall); if (memory.toInt() > manager.recommendedMemoryMb()) Text("Значение выше рекомендуемого; runtime может испытывать memory pressure.", color = MaterialTheme.colorScheme.error); Text("RuntimeConfiguration передаст этот бюджет будущему GameRuntime.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
}

@Composable private fun ControlsSettingsPage(onControls: () -> Unit) { SettingsList { Text("Редактор открывается на весь экран и работает как preview игрового viewport."); Button(onClick = onControls, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Edit, null); Spacer(Modifier.width(8.dp)); Text("Открыть редактор") }; SettingEnum("Preset", "DEFAULT", listOf("DEFAULT", "COMPACT", "GAMING")) { }; SettingSlider("Button size", 1f, .6f..1.5f, .1f) { }; SettingSlider("Opacity", 1f, .25f..1f, .05f) { } } }
@Composable private fun PlaceholderSettingsPage(text: String) { SettingsList { Text(text); Text("Раздел подготовлен для подключения соответствующего repository/service слоя.", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun SettingsList(content: @Composable ColumnScope.() -> Unit) { LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) { item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) } } }
@Composable private fun SettingSwitch(label: String, value: Boolean, onChange: (Boolean) -> Unit) { Card { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(label, Modifier.weight(1f)); Switch(value, onChange) } } }
@Composable private fun SettingSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, step: Float = 1f, onChange: (Float) -> Unit) { Card { Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text("%.2f".format(value)) }; Slider(value, onChange, valueRange = range, steps = (((range.endInclusive-range.start)/step).toInt()-1).coerceAtLeast(0)) } } }
@Composable private fun SettingEnum(label: String, value: String, options: List<String>, onChange: (String) -> Unit) { var expanded by remember { mutableStateOf(false) }; Card { Box(Modifier.fillMaxWidth().padding(12.dp)) { FilledTonalButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("$label: ${value.replace('_', ' ')}") }; DropdownMenu(expanded, { expanded = false }) { options.forEach { option -> DropdownMenuItem(text = { Text(option.replace('_', ' ')) }, onClick = { expanded = false; onChange(option) }) } } } } }

@Composable
fun FullscreenControlEditor(navController: NavHostController) {
    var layout by remember { mutableStateOf(ControlLayout()) }; var selectedId by remember { mutableStateOf<String?>(null) }; var zoom by remember { mutableFloatStateOf(1f) }; val selected = layout.elements.firstOrNull { it.id == selectedId }
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.weight(1f).fillMaxWidth().padding(12.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))) {
            layout.elements.filter { it.visible }.forEach { element -> val selectedElement = element.id == selectedId; Box(Modifier.offset((element.x * 1000).dp, (element.y * 520).dp).size((element.width * 260 * zoom).dp, (element.height * 180 * zoom).dp).background(if (selectedElement) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(18.dp)).pointerInput(element.id) { detectDragGestures(onDragStart = { selectedId = element.id }) { change, drag -> change.consume(); layout = layout.copy(elements = layout.elements.map { current -> if (current.id == element.id) current.copy(x = (current.x + drag.x / 1000f).coerceIn(0f, .88f), y = (current.y + drag.y / 520f).coerceIn(0f, .88f)) else current }) } }, contentAlignment = Alignment.Center) { Text(element.type.name, style = MaterialTheme.typography.labelSmall) } }
        }
        Surface(tonalElevation = 3.dp) { Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { Button(onClick = { layout = ControlLayout() }) { Text("Reset") }; FilledTonalButton(onClick = { layout = layout.copy(elements = layout.elements + ControlElement("custom-${layout.elements.size}", ControlElementType.CUSTOM, .45f, .45f)) }) { Text("Add element") }; if (selected != null) { Text("${selected.id}: ${selected.type.name}", Modifier.weight(1f)); Slider(value = selected.alpha, onValueChange = { a -> layout = layout.copy(elements = layout.elements.map { if (it.id == selected.id) it.copy(alpha = a) else it }) }, valueRange = .2f..1f, modifier = Modifier.width(160.dp)) } else Spacer(Modifier.weight(1f)); OutlinedButton(onClick = { navController.popBackStack() }) { Text("Done") } } }
    }
}
