package propertymanager.feature.tenant.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.PriorityLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceRequestScreen(
    selectedCategory: String,
    onSubmit: (MaintenanceRequest) -> Unit
) {
    val viewModel: MaintenanceRequestViewModel = hiltViewModel()
    val priorities by viewModel.priorityLevels.collectAsState(initial = listOf())
    val statuses by viewModel.requestStatuses.collectAsState(initial = listOf())

    var issueDescription by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(priorities.firstOrNull() ?: "") }
    var status by remember { mutableStateOf(statuses.firstOrNull() ?: "") }
    var isUrgent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Complaint") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = issueDescription,
                onValueChange = { issueDescription = it },
                label = { Text("Issue Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spinner(
                selectedValue = priority,
                options = priorities,
                onValueChange = { priority = it },
                label = "Category"
            )
            Spinner(
                selectedValue = status,
                options = statuses,
                onValueChange = { status = it },
                label = "Subcategory"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isUrgent,
                    onCheckedChange = { isUrgent = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Is it Urgent?")
            }
            Button(
                onClick = {
                    val request = MaintenanceRequest(
                        issueDescription = issueDescription,
                        priority = priority,
                        status = status,
                        isUrgent = isUrgent,
                        issueCategory = selectedCategory
                    )
                    onSubmit(request)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Complaint")
            }
        }
    }
}

@Composable
fun Spinner(
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val icon: ImageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $selectedValue")
            Icon(imageVector = icon, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    text = {
                        Text(option)
                    }
                )
            }
        }
    }
}
