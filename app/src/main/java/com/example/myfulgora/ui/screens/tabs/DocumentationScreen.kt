package com.example.myfulgora.ui.screens.tabs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator // 👈 Adicionado para a rodinha de loading
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider // 👈 Adicionado para a segurança dos ficheiros
import androidx.lifecycle.viewmodel.compose.viewModel // 👈 Adicionado para injetar o ViewModel
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.ui.viewmodel.MotaViewModel
import kotlinx.coroutines.launch // 👈 Adicionado para as coroutines

@Composable
fun DocumentationScreen(
    viewModel: MotaViewModel = viewModel(), // 👈 O ViewModel foi injetado aqui
    state: BikeState,
    onSaveDocument: (String, String) -> Unit,
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
){
    val context = LocalContext.current
    val currentUser = UserManager.currentUser
    val scope = rememberCoroutineScope() // 👈 Inicializamos as coroutines

    var docToUpload by remember { mutableStateOf<String?>(null) }

    // Variável para saber qual documento está a fazer download e mostrar o loading
    var downloadingDoc by remember { mutableStateOf<String?>(null) }

    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null && docToUpload != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onSaveDocument(docToUpload!!, uri.toString())
                docToUpload = null
            }
        }
    )

    val documentTypes = listOf(
        // DUA Removido!
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
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                documentTypes.forEach { docName ->
                    val savedUriString = state.documents[docName]
                    val hasDocument = savedUriString != null
                    val isDownloadingThis = downloadingDoc == docName

                    FulgoraInfoCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Previne que o utilizador clique várias vezes no mesmo
                                    if (downloadingDoc != null) return@clickable

                                    if (hasDocument) {
                                        // 🌟 LÓGICA DE DOWNLOAD E CACHE 🌟
                                        scope.launch {
                                            downloadingDoc = docName // Começa a rodar o loading

                                            // Vai ao ViewModel pedir o ficheiro
                                            val ficheiroPdf = viewModel.obterDocumento(context, docName, savedUriString!!)

                                            downloadingDoc = null // Pára o loading

                                            if (ficheiroPdf != null) {
                                                try {
                                                    // Cria o endereço seguro via FileProvider
                                                    val uriSegura = FileProvider.getUriForFile(
                                                        context,
                                                        "${context.packageName}.provider",
                                                        ficheiroPdf
                                                    )

                                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                                        setDataAndType(uriSegura, "application/pdf")
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Nenhuma aplicação instalada para abrir PDFs.", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(context, "Erro ao transferir documento. Verifica a internet.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        docToUpload = docName
                                        docPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                                    }
                                }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = docName,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )

                            // Se estiver a transferir este documento, mostra a rodinha, senão mostra o texto
                            if (isDownloadingThis) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = GreenFresh,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (hasDocument) "Open" else "Upload",
                                    color = if (hasDocument) GreenFresh else Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                }
            }
        }
    }
}