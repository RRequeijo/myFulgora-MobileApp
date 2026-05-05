package com.example.myfulgora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.ui.theme.GreenFresh

// Cor do Fundo para Inputs
val DarkInputBackground = Color(0xFF222222)

@Composable
fun FulgoraTextField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        cursorBrush = SolidColor(GreenFresh),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(text = placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun FulgoraPasswordField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
        cursorBrush = SolidColor(GreenFresh),
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        Text(text = placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                    }
                    innerTextField()
                }
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

// 1. INPUT DE TEXTO NORMAL (Adaptativo)
@Composable
fun CustomDarkInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = if (icon != null) {
            { Icon(imageVector = icon, contentDescription = null, tint = GreenFresh) }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = GreenFresh,
            focusedBorderColor = GreenFresh,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            focusedLabelColor = GreenFresh,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onAction() },
            onDone = { onAction() },
            onGo = { onAction() },
            onSearch = { onAction() }
        ),
        singleLine = true
    )
}

// 2. INPUT DE PASSWORD (Adaptativo)
@Composable
fun CustomDarkPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction = ImeAction.Done,
    onAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = GreenFresh,
            focusedBorderColor = GreenFresh,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            focusedLabelColor = GreenFresh,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { onAction() },
            onNext = { onAction() },
            onGo = { onAction() }
        ),
        singleLine = true
    )
}
