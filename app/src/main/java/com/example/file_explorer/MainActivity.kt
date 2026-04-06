package com.example.file_explorer

import android.R.attr.onClick
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import kotlin.contracts.contract

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFileExplorerScreen() {

    val context = LocalContext.current

    var selectedTreeRoot by remember { mutableStateOf<DocumentFile?>(null) }
    var currentDir by remember { mutableStateOf<DocumentFile?>(null) }
    var filesInDir by remember { mutableStateOf(emptyList<DocumentFile>()) }

    var folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri : Uri? ->
        if (uri != null) {
            val documentTree = DocumentFile.fromTreeUri(context, uri)
            selectedTreeRoot = documentTree
            currentDir = documentTree
        }
    }

    fun refreshFileList() {
        if(currentDir != null && currentDir!!.isDirectory) {
            val allFiles = currentDir!!.listFiles().toList()
            filesInDir = allFiles.sortedWith(
                compareBy({!it.isDirectory},
                    { it.name?.lowercase() ?: "" }
                )
            )
        } else {
            filesInDir = emptyList()
        }
    }

    LaunchedEffect(currentDir) {
        refreshFileList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    "System Explorer",
                    style = MaterialTheme.typography.titleMedium
                ) },
                navigationIcon = {
                    if(currentDir != null && currentDir?.uri != selectedTreeRoot?.uri) {
                        IconButton(onClick = {
                            currentDir = currentDir?.parentFile ?: selectedTreeRoot
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = { folderPickerLauncher.launch(null) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Open Folder")
                        Spacer( modifier = Modifier.width(4.dp) )
                        Text("Select Folder")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray)
            )
        },
        bottomBar = {
            if(currentDir != null) {
                BottomAppBar(containerColor = Color.LightGray) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                currentDir?.createDirectory("Folder_${UUID.randomUUID().toString().take(4)}")
                                refreshFileList()
                            }
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = "New Folder")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Folder")
                        }

                        Button(
                            onClick = {
                                val newFile = currentDir?.createFile(
                                    "text/plain",
                                    "File_${UUID.randomUUID().toString().take(4)}"
                                )
                                newFile?.uri?.let { uri ->
                                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                        outputStream.write("Hello 546".toByteArray())
                                    }
                                }
                                refreshFileList()
                            }
                        ) {
                            Icon(Icons.Default.Create, contentDescription = "New File")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New File")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentDir == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Click 'Select Folder' to browse your phone", color = Color.Gray)
                }
            }
            else {
                Text(
                    text = "Current: ${currentDir?.name ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )

                HorizontalDivider()

                if(filesInDir.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("This folder is empty", color = Color.Gray)
                    }
                }
                else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filesInDir) { file ->
                            FileListItem(
                                file = file,
                                onClick = {
                                    if (file.isDirectory) {
                                        currentDir = file
                                    }
                                    else {
                                        println("This is a file...")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
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