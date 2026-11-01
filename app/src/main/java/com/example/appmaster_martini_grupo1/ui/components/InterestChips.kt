package com.example.appmaster_martini_grupo1.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmaster_martini_grupo1.data.model.Interest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestChips(
    interests: List<Interest>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        interests.forEach { interest ->
            val selected = interest.id in selectedIds
            FilterChip(
                selected = selected,
                onClick = { onToggle(interest.id) },
                label = { Text(interest.label) },
                leadingIcon = if (selected) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else null
            )
        }
    }
}