package com.imux.game.launcher.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imux.game.R

@Composable
fun LauncherTopBar(
    onDirectory: () -> Unit,
    onAccounts: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.icon),
                contentDescription = "Imux",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "Imux Launcher",
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDirectory, modifier = Modifier.size(46.dp)) {
                Icon(Icons.Default.FolderOpen, contentDescription = "Imux Directory")
            }
            IconButton(onClick = onAccounts, modifier = Modifier.size(46.dp)) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
            }
            IconButton(onClick = onSettings, modifier = Modifier.size(46.dp)) {
                Icon(Icons.Default.Settings, contentDescription = "Launcher Settings")
            }
        }
    }
}
