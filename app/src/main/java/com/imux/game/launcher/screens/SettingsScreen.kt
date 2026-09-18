package com.imux.game.launcher.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imux.game.launcher.localization.LocaleRegistry
import com.imux.game.launcher.model.LauncherSettings
import com.imux.game.launcher.model.UiScale

@Composable
fun SettingsScreen(
    settings: LauncherSettings,
    onSettingsChanged: (LauncherSettings) -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
                Text(
                    text = "Launcher Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsSection("Launcher") {
                    SettingSwitch(
                        title = "Fullscreen",
                        description = "Immersive fullscreen без системных панелей.",
                        checked = settings.fullscreen,
                        onCheckedChange = { onSettingsChanged(settings.copy(fullscreen = it)) }
                    )
                    SettingSwitch(
                        title = "Animations",
                        description = "Плавные переходы launcher UI.",
                        checked = settings.animations,
                        onCheckedChange = { onSettingsChanged(settings.copy(animations = it)) }
                    )
                    SettingSwitch(
                        title = "Notifications",
                        description = "Backend уведомлений пока не подключён.",
                        checked = false,
                        enabled = false,
                        onCheckedChange = {}
                    )
                    PlaceholderSetting(
                        "Performance",
                        "Профили энергопотребления и фоновых задач будут подключены к Launcher Services."
                    )
                    PlaceholderSetting(
                        "Storage",
                        "Storage service будет управлять cache, resources и runtime."
                    )
                    PlaceholderSetting(
                        "Diagnostics",
                        "Техническая информация будет доступна через Diagnostics service."
                    )
                }

                SettingsSection("Appearance") {
                    PlaceholderSetting("Theme", "Активна собственная Imux dark theme.")
                    PlaceholderSetting("Navigation style", "Текущий режим: adaptive vertical sidebar.")
                    PlaceholderSetting("UI effects", "Blur и тяжёлые эффекты намеренно не используются.")
                    LanguageSelector(settings.languageTag) { tag ->
                        onSettingsChanged(settings.copy(languageTag = tag))
                    }
                }

                SettingsSection("Interface") {
                    SettingSwitch(
                        title = "Reduce motion",
                        description = "Минимизировать необязательные переходы.",
                        checked = settings.reduceMotion,
                        onCheckedChange = { onSettingsChanged(settings.copy(reduceMotion = it)) }
                    )
                    SettingSwitch(
                        title = "Touch feedback",
                        description = "Визуальная обратная связь элементов.",
                        checked = settings.touchFeedback,
                        onCheckedChange = { onSettingsChanged(settings.copy(touchFeedback = it)) }
                    )
                    Text(
                        "UI scale",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UiScale.values().forEach { scale ->
                            Row(
                                modifier = Modifier.width(116.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.uiScale == scale,
                                    onClick = { onSettingsChanged(settings.copy(uiScale = scale)) }
                                )
                                Text(scale.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }

                SettingsSection("Runtime") {
                    PlaceholderSetting("Runtime directory", "Будет подключён к RuntimeManager.")
                    PlaceholderSetting("Runtime status", "Источник: FileRuntimeManager.")
                    PlaceholderSetting("Memory configuration", "Будет передаваться через LaunchConfiguration.")
                    PlaceholderSetting("Launch parameters", "Будут принадлежать Imux Runtime.")
                    PlaceholderSetting("Renderer configuration", "Будет подключена к Imux Renderer API.")
                }

                SettingsSection("Advanced") {
                    PlaceholderSetting("Developer options", "Backend пока не подключён.")
                    PlaceholderSetting("Logs", "Доступ к launcher logs будет добавлен через Diagnostics.")
                    PlaceholderSetting("Debug information", "Будет отображаться в Diagnostics.")
                    PlaceholderSetting("Engine diagnostics", "Источник: native diagnostics через отдельный service boundary.")
                }
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    selectedTag: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = LocaleRegistry.resolve(selectedTag)

    Column(modifier = Modifier.padding(vertical = 7.dp)) {
        Text("Language", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Fallback: selected language → English → safe fallback",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { expanded = true }) {
            Text(selected.displayName)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LocaleRegistry.supported.forEach { locale ->
                DropdownMenuItem(
                    text = { Text(locale.displayName) },
                    onClick = {
                        onSelected(locale.tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PlaceholderSetting(
    title: String,
    description: String
) {
    Column(modifier = Modifier.padding(vertical = 7.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(
            "Not implemented · " + description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
