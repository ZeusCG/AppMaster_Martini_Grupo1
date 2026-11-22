package com.example.appmaster_martini_grupo1.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.data.model.Video
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme

@Composable
fun ContentCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = content.typeIcon(),
                contentDescription = content.typeDescription(),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = content.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = content.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FavoriteButton(
                isFavorite = isFavorite,
                onToggle = onToggleFavorite
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentCardPreview() {
    AppMaster_Martini_Grupo1Theme {
        ContentCard(
            content = Recipe(
                id = "recipe-001",
                title = "Ganache de chocolate semiamargo",
                description = "Base cremosa para rellenos y baños, ideal para vitrina.",
                category = "Chocolatería",
                portions = 12,
                ingredients = emptyList(),
                steps = emptyList()
            ),
            onClick = {},
            isFavorite = true
        )
    }
}

/** H-043: un ícono distinto por tipo, en vez del mismo logo para todos. */
private fun Content.typeIcon(): ImageVector = when (this) {
    is Recipe -> Icons.Filled.List
    is Video -> Icons.Filled.PlayArrow
    is Course -> Icons.Filled.Star
    else -> Icons.Filled.List
}

private fun Content.typeDescription(): String = when (this) {
    is Recipe -> "Receta"
    is Video -> "Video"
    is Course -> "Curso"
    else -> "Contenido"
}
