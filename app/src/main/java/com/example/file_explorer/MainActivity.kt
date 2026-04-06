package com.example.file_explorer

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.file_explorer.ui.theme.File_ExplorerTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            File_ExplorerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimpleFileExplorerScreen()
                }
            }
        }
    }
}

@Composable
fun SimpleFileExplorerScreen() {
    
}

@Composable
fun FileListItem(file: DocumentFile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
            contentDescription = null,
            tint = if (file.isDirectory) Color(0xFFF6C85F) else Color.Gray,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = file.name ?: "Unknown", fontWeight = FontWeight.Bold)
            Text(
                text = if (file.isDirectory) "Directory" else "${file.length()} bytes",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
}