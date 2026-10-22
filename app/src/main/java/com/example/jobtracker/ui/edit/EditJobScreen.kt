package com.example.jobtracker.ui.edit

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jobtracker.ui.components.StatusSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(
    onDone: () -> Unit, // used for both "back" and "after save"
    viewModel: EditJobViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Edit job" else "Add job") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.company,
                onValueChange = { viewModel.company = it },
                label = { Text("Company *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.role,
                onValueChange = { viewModel.role = it },
                label = { Text("Role *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.link,
                onValueChange = { viewModel.link = it },
                label = { Text("Job link") },
                singleLine = true,
                isError = !viewModel.isLinkValid,                       // red outline when invalid
                supportingText = {
                    if (!viewModel.isLinkValid) Text("Enter a valid link, e.g. company.com/jobs/123")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), // URL-friendly keyboard
                modifier = Modifier.fillMaxWidth()
            )

            Text("Status", style = MaterialTheme.typography.labelLarge)
            StatusSelector(
                selected = viewModel.status,
                onSelected = { viewModel.status = it }
            )

            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = { viewModel.notes = it },
                label = { Text("Notes") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Save is disabled until company and role are filled
            Button(
                onClick = { viewModel.save(onDone) },
                enabled = viewModel.canSave,
//                enabled = viewModel.company.isNotBlank() && viewModel.role.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}