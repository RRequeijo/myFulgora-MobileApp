package com.example.myfulgora.ui.screens.tabs.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenDeep
import com.example.myfulgora.ui.theme.GreenFresh

@Composable
fun DocumentationScreen(
    viewModel: DocumentationViewModel = viewModel(), // 👈 O nosso novo ViewModel
    onMenuClick: () -> Unit = {}
){
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estado para saber que documento o utilizador clicou para fazer Upload
    var docToUpload by remember { mutableStateOf<String?>(null) }

    // 1. O "Abridor" de ficheiros do Android (File Picker)
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null && docToUpload != null) {
                // Diz ao Android para não esquecer a permissão deste ficheiro mesmo que a app feche
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                // Guarda no ViewModel
                viewModel.guardarDocumento(docToUpload!!, uri.toString())
                docToUpload = null
            }
        }
    )

    // A tua lista de documentos
    val documentTypes = listOf(
        "Documento Único Automóvel (DUA)",
        "Certificado de Seguro",
        "Ficha de Inspeção Periódica",
        "Manual da Mota",
        "Histórico de Manutenção",
        "Contratos/Orçamentos de Reparações"
    )

    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio
            val paddingSide = screenW * Dimens.SideMarginRatio

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = paddingSide)
                    .padding(top = Dimens.TopPadding)
            ){
                FulgoraTopBar(
                    iconSize = iconSize,
                    onMenuClick = onMenuClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // Em vez de repetirmos código, usamos um forEach!
                documentTypes.forEach { docName ->

                    // Verifica se já existe um ficheiro guardado para este documento
                    val savedUriString = state.savedDocuments[docName]
                    val hasDocument = savedUriString != null

                    FulgoraInfoCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (hasDocument) {
                                        // 2. SE TEM DOCUMENTO: Abre o PDF/Imagem
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(Uri.parse(savedUriString), "*/*")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        } catch (e: ActivityNotFoundException) {
                                            Toast.makeText(context, "Nenhuma app para abrir este ficheiro", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        // 3. SE NÃO TEM DOCUMENTO: Abre os Downloads para fazer Upload
                                        docToUpload = docName
                                        // Filtra para PDFs ou Imagens
                                        docPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                                    }
                                }
                                .padding(vertical = 4.dp), // Área de clique ligeiramente maior
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = docName,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )

                            // Muda a cor e o texto dinamicamente!
                            Text(
                                text = if (hasDocument) "Open" else "Upload",
                                color = if (hasDocument) GreenFresh else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                }
            }
        }
    }
}