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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imux.launcher.diagnostics.LogLevel
import com.imux.launcher.diagnostics.LogStore
import com.imux.launcher.performance.MemoryManager
import com.imux.launcher.settings.*
import kotlin.math.roundToInt

private enum class SettingsCategory(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    LAUNCHER("Launcher", Icons.Default.Settings), GAME("Game", Icons.Default.Gamepad), CONTROLS("Controls", Icons.Default.TouchApp),
    GRAPHICS("Graphics", Icons.Default.Build), RUNTIME("Runtime", Icons.Default.Memory), DOWNLOADS("Downloads", Icons.Default.Download),
    LOGS("Logs & Diagnostics", Icons.Default.List), APPEARANCE("Appearance", Icons.Default.Palette), ABOUT("About", Icons.Default.Info)
}

@Composable
fun SettingsHubScreen(settings: LauncherSettings, vm: LauncherSettingsViewModel, navController: NavHostController, onControls: () -> Unit) {
    var category by rememberSaveable { mutableStateOf(SettingsCategory.LAUNCHER) }
    Row(Modifier.fillMaxSize()) {
        NavigationRail { SettingsCategory.entries.forEach { item -> NavigationRailItem(selected = category == item, onClick = { category = item }, icon = { Icon(item.icon, null) }, label = { Text(item.label, maxLines = 1) }) } }
        VerticalDivider()
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text(category.label, style = MaterialTheme.typography.headlineSmall) }
            Spacer(Modifier.height(12.dp))
            when (category) {
                SettingsCategory.LAUNCHER -> LauncherSettingsPage(settings, vm)
                SettingsCategory.GAME -> GameSettingsPage(settings, vm)
                SettingsCategory.CONTROLS -> ControlsSettingsPage(settings, onControls)
                SettingsCategory.GRAPHICS -> GraphicsSettingsPage(settings, vm)
                SettingsCategory.RUNTIME -> RuntimeSettingsPage(settings, vm)
                SettingsCategory.DOWNLOADS -> DownloadsSettingsPage()
                SettingsCategory.LOGS -> LogsSettingsPage()
                SettingsCategory.APPEARANCE -> AppearanceSettingsPage(settings, vm)
                SettingsCategory.ABOUT -> AboutSettingsPage()
            }
        }
    }
}

@Composable private fun LauncherSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) = SettingsList {
    SettingEnum("Theme", s.theme.name, LauncherTheme.entries.map { it.name }) { v -> vm.update { it.copy(theme = LauncherTheme.valueOf(v)) } }
    SettingSwitch("Material You / Dynamic Color", s.dynamicColors) { v -> vm.update { it.copy(dynamicColors = v) } }
    SettingEnum("Language", s.language, listOf("system", "en", "uk", "ru")) { v -> vm.update { it.copy(language = v) } }
    SettingEnum("Animations", s.animations.profile.name, AnimationProfile.entries.map { it.name }) { v -> vm.update { it.copy(animations = it.animations.copy(profile = AnimationProfile.valueOf(v), animationEnabled = v != "OFF")) } }
    SettingSlider("Animation speed", s.animations.animationSpeed, .5f..2f, .1f) { v -> vm.update { it.copy(animations = it.animations.copy(animationSpeed = v)) } }
    SettingEnum("Transition", s.animations.transitionStyle.name, TransitionStyle.entries.map { it.name }) { v -> vm.update { it.copy(animations = it.animations.copy(transitionStyle = TransitionStyle.valueOf(v))) } }
    SettingSwitch("Confirm launch", s.confirmLaunch) { v -> vm.update { it.copy(confirmLaunch = v) } }
    SettingSwitch("Automatic update checks", true) { }
    SettingSwitch("Load resources on startup", true) { }
    SettingSwitch("Notifications", true) { }
    OutlinedButton(onClick = { LogStore.repository.log(LogLevel.INFO, "Launcher", "Launcher cache cleanup requested") }) { Text("Clear launcher cache") }
}

@Composable private fun AppearanceSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) = SettingsList {
    SettingEnum("Interface style", s.theme.name, LauncherTheme.entries.map { it.name }) { v -> vm.update { it.copy(theme = LauncherTheme.valueOf(v)) } }
    SettingSwitch("Visual effects", s.visualEffects) { v -> vm.update { it.copy(visualEffects = v) } }
    SettingSwitch("Compact density", s.compactDensity) { v -> vm.update { it.copy(compactDensity = v) } }
    SettingSlider("Transparency", s.interfaceTransparency, 0f..0.35f, .01f) { v -> vm.update { it.copy(interfaceTransparency = v) } }
    SettingSlider("Card corner radius", s.cardCornerRadius.toFloat(), 8f..36f, 1f) { v -> vm.update { it.copy(cardCornerRadius = v.roundToInt()) } }
}

@Composable private fun GameSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) = SettingsList {
    val g = s.game
    SettingEnum("Launch mode", g.launchMode.name, LaunchMode.entries.map { it.name }) { v -> vm.update { it.copy(game = it.game.copy(launchMode = LaunchMode.valueOf(v))) } }
    SettingSlider("FPS limit", g.fpsLimit.toFloat(), 30f..120f, 10f) { v -> vm.update { it.copy(game = it.game.copy(fpsLimit = v.roundToInt())) } }
    SettingSlider("Render distance", g.renderDistance.toFloat(), 4f..16f, 1f) { v -> vm.update { it.copy(game = it.game.copy(renderDistance = v.roundToInt())) } }
    SettingSlider("Simulation distance", g.simulationDistance.toFloat(), 4f..12f, 1f) { v -> vm.update { it.copy(game = it.game.copy(simulationDistance = v.roundToInt())) } }
    SettingEnum("Particles", g.particles.name, ParticleQuality.entries.map { it.name }) { v -> vm.update { it.copy(game = it.game.copy(particles = ParticleQuality.valueOf(v))) } }
    SettingSwitch("Shadows", g.shadows) { v -> vm.update { it.copy(game = it.game.copy(shadows = v)) } }
    SettingEnum("Lighting", g.lighting.name, LightingQuality.entries.map { it.name }) { v -> vm.update { it.copy(game = it.game.copy(lighting = LightingQuality.valueOf(v))) } }
    SettingEnum("Textures", g.textureQuality.name, TextureQuality.entries.map { it.name }) { v -> vm.update { it.copy(game = it.game.copy(textureQuality = TextureQuality.valueOf(v))) } }
    SettingSwitch("VSync / frame pacing", g.vsync) { v -> vm.update { it.copy(game = it.game.copy(vsync = v)) } }
    SettingEnum("Anti-aliasing", g.antiAliasing.name, AntiAliasing.entries.map { it.name }) { v -> vm.update { it.copy(game = it.game.copy(antiAliasing = AntiAliasing.valueOf(v))) } }
}

@Composable private fun GraphicsSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) = SettingsList {
    val g = s.graphics
    SettingEnum("Renderer", g.renderer.name, listOf("AUTO", "OPENGL_ES", "VULKAN")) { v -> vm.update { it.copy(graphics = it.graphics.copy(renderer = com.imux.gamecore.runtime.GraphicsBackendType.valueOf(v))) } }
    SettingEnum("Quality", g.quality.name, GraphicsQuality.entries.map { it.name }) { v -> vm.update { it.copy(graphics = it.graphics.copy(quality = GraphicsQuality.valueOf(v))) } }
    SettingSlider("Resolution scale", g.resolutionScale, .5f..1f, .05f) { v -> vm.update { it.copy(graphics = it.graphics.copy(resolutionScale = v)) } }
    SettingSlider("Texture memory", g.maxTextureMemoryMb.toFloat(), 128f..768f, 64f) { v -> vm.update { it.copy(graphics = it.graphics.copy(maxTextureMemoryMb = v.roundToInt())) } }
    SettingEnum("Shader complexity", g.shaderComplexity.name, ShaderComplexity.entries.map { it.name }) { v -> vm.update { it.copy(graphics = it.graphics.copy(shaderComplexity = ShaderComplexity.valueOf(v))) } }
    SettingSwitch("Experimental renderer features", g.experimental) { v -> vm.update { it.copy(graphics = it.graphics.copy(experimental = v)) } }
    Text("Snapdragon 685 baseline: Low quality, limited shader complexity, texture memory and overdraw.", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable private fun RuntimeSettingsPage(s: LauncherSettings, vm: LauncherSettingsViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val manager = remember(context) { MemoryManager(context) }
    val range = remember(manager) { manager.safeRangeMb() }
    var memory by remember(s.performance.memoryMb) { mutableFloatStateOf(manager.sanitize(s.performance.memoryMb).toFloat()) }
    SettingsList {
        Text("Runtime budget", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Memory"); Text(manager.formatMb(memory.toInt())) }
        Slider(value = memory, onValueChange = { memory = manager.sanitize(it.toInt()).toFloat() }, valueRange = range.first.toFloat()..range.last.toFloat())
        Text("Safe range: ${manager.formatMb(range.first)} – ${manager.formatMb(range.last)}", style = MaterialTheme.typography.labelSmall)
        if (memory.toInt() > manager.recommendedMemoryMb()) Text("Above recommended budget: memory pressure may increase under load.", color = MaterialTheme.colorScheme.error)
        Button(onClick = { vm.update { it.copy(performance = it.performance.copy(memoryMb = manager.sanitize(memory.toInt()))) } }) { Text("Apply memory budget") }
        Text("RuntimeConfiguration receives this validated budget before GameRuntime starts.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun ControlsSettingsPage(s: LauncherSettings, onControls: () -> Unit) = SettingsList {
    Text("The editor is a full-screen game viewport preview. Changes are serialized into the launcher settings repository.")
    Button(onClick = onControls, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Edit, null); Spacer(Modifier.width(8.dp)); Text("Open full-screen editor") }
    Text("Preset: ${s.controlLayout.name}", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable private fun DownloadsSettingsPage() = SettingsList {
    Text("Game resource management")
    Text("Download service boundary is prepared for version manifests, resource packs and integrity checks.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    SettingSwitch("Wi-Fi only", true) { }
    SettingSwitch("Verify downloaded resources", true) { }
}

@Composable private fun LogsSettingsPage() {
    val entries by LogStore.repository.entries.collectAsState()
    var filter by rememberSaveable { mutableStateOf("ALL") }
    SettingsList {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ALL", "INFO", "WARNING", "ERROR").forEach { value -> FilterChip(selected = filter == value, onClick = { filter = value }, label = { Text(value) }) }
            Spacer(Modifier.weight(1f)); OutlinedButton(onClick = { LogStore.repository.clear() }) { Text("Clear") }
        }
        entries.asReversed().filter { filter == "ALL" || it.level.name == filter }.forEach { entry -> ListItem(headlineContent = { Text("${entry.level.name} · ${entry.tag}") }, supportingContent = { Text(entry.message) }) }
        if (entries.isEmpty()) Text("No buffered entries. Capacity is limited to 500 entries.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun AboutSettingsPage() = SettingsList {
    Text("Imux", style = MaterialTheme.typography.headlineMedium)
    Text("Launcher and runtime foundation for the future Imux game. No Minecraft API or runtime dependency is used.")
    Text("Renderer: OpenGL ES foundation. Vulkan remains an explicit extension point.", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable private fun SettingsList(content: @Composable ColumnScope.() -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) { item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) } }
}

@Composable private fun SettingSwitch(label: String, value: Boolean, onChange: (Boolean) -> Unit) = Card {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(label, Modifier.weight(1f)); Switch(checked = value, onCheckedChange = onChange) }
}

@Composable private fun SettingSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, step: Float = 1f, onChange: (Float) -> Unit) = Card {
    Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text("%.2f".format(value)) }; Slider(value = value, onValueChange = onChange, valueRange = range, steps = (((range.endInclusive - range.start) / step).roundToInt() - 1).coerceAtLeast(0)) }
}

@Composable private fun SettingEnum(label: String, value: String, options: List<String>, onChange: (String) -> Unit) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    Card { Box(Modifier.fillMaxWidth().padding(12.dp)) {
        FilledTonalButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("$label: ${value.replace('_', ' ')}") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { options.forEach { option -> DropdownMenuItem(text = { Text(option.replace('_', ' ')) }, onClick = { expanded = false; onChange(option) }) } }
    } }
}

@Composable
fun FullscreenControlEditor(settings: LauncherSettings, vm: LauncherSettingsViewModel, navController: NavHostController) {
    var layout by remember { mutableStateOf(settings.controlLayout) }
    var selectedId by remember { mutableStateOf<String?>(layout.elements.firstOrNull()?.id) }
    val selected = layout.elements.firstOrNull { it.id == selectedId }
    val density = LocalDensity.current
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(12.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))) {
            val previewWidthPx = with(density) { maxWidth.toPx() }
            val previewHeightPx = with(density) { maxHeight.toPx() }
            layout.elements.filter { it.visible }.forEach { element ->
                val selectedElement = element.id == selectedId
                Box(
                    Modifier.offset { IntOffset((previewWidthPx * element.x).roundToInt(), (previewHeightPx * element.y).roundToInt()) }
                        .size(maxWidth * element.width, maxHeight * element.height)
                        .background(if (selectedElement) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(18.dp))
                        .pointerInput(element.id) {
                            detectDragGestures(onDragStart = { selectedId = element.id }) { change, drag ->
                                change.consume()
                                layout = layout.copy(elements = layout.elements.map { current ->
                                    if (current.id == element.id) current.copy(
                                        x = (current.x + drag.x / previewWidthPx).coerceIn(0f, (1f - current.width).coerceAtLeast(0f)),
                                        y = (current.y + drag.y / previewHeightPx).coerceIn(0f, (1f - current.height).coerceAtLeast(0f))
                                    ) else current
                                })
                            }
                        },
                    contentAlignment = Alignment.Center
                ) { Text(element.type.name, style = MaterialTheme.typography.labelSmall) }
            }
        }
        Surface(tonalElevation = 3.dp) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selected != null) {
                    Text("${selected.type.name} · ${selected.action ?: "default action"}", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingSlider("X", selected.x, 0f..(1f - selected.width).coerceAtLeast(0f), .01f) { v -> layout = changeElement(layout, selected.id) { it.copy(x = v) } }
                        SettingSlider("Y", selected.y, 0f..(1f - selected.height).coerceAtLeast(0f), .01f) { v -> layout = changeElement(layout, selected.id) { it.copy(y = v) } }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingSlider("Width", selected.width, .04f..(1f - selected.x).coerceAtLeast(.04f), .01f) { v -> layout = changeElement(layout, selected.id) { it.copy(width = v) } }
                        SettingSlider("Height", selected.height, .04f..(1f - selected.y).coerceAtLeast(.04f), .01f) { v -> layout = changeElement(layout, selected.id) { it.copy(height = v) } }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Opacity", Modifier.weight(1f)); Slider(value = selected.alpha, onValueChange = { a -> layout = changeElement(layout, selected.id) { it.copy(alpha = a) } }, valueRange = .2f..1f, modifier = Modifier.width(180.dp))
                        OutlinedButton(onClick = { layout = layout.copy(elements = layout.elements.filterNot { it.id == selected.id }); selectedId = layout.elements.firstOrNull()?.id }) { Text("Delete") }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { layout = ControlLayout(); selectedId = layout.elements.firstOrNull()?.id }) { Text("Reset") }
                    FilledTonalButton(onClick = { val id = "custom-${layout.elements.size}"; layout = layout.copy(elements = layout.elements + ControlElement(id, ControlElementType.CUSTOM, .45f, .42f)); selectedId = id }) { Text("Add element") }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = { vm.update { it.copy(controlLayout = layout, controlLayoutId = layout.id) }; navController.popBackStack() }) { Text("Save & Done") }
                }
            }
        }
    }
}

private fun changeElement(layout: ControlLayout, id: String, transform: (ControlElement) -> ControlElement): ControlLayout =
    layout.copy(elements = layout.elements.map { if (it.id == id) transform(it) else it })
