package app.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.extensions.simpleVisibleName
import app.git.RemoteInfo
import app.ui.components.SideMenuPanel
import app.ui.components.SideMenuSubentry
import app.ui.components.VerticalExpandable
import app.ui.context_menu.remoteBranchesContextMenu
import app.viewmodels.RemotesViewModel
import org.eclipse.jgit.lib.Ref

@Composable
fun Remotes(
    remotesViewModel: RemotesViewModel,
    onBranchClicked: (Ref) -> Unit,
) {
    val remotes by remotesViewModel.remotes.collectAsState()

    val itemsCount = remember(remotes) {
        val allBranches = remotes.map { it.branchesList }.flatten()
        allBranches.count() + remotes.count()
    }

    SideMenuPanel(
        title = "Remotes",
        icon = painterResource("cloud.svg"),
        items = remotes,
        itemsCountForMaxHeight = itemsCount,
        itemContent = { remoteInfo ->
            RemoteRow(
                remote = remoteInfo,
                onBranchClicked = { branch -> onBranchClicked(branch) },
                onDeleteBranch = { branch -> remotesViewModel.deleteBranch(branch) }
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RemoteRow(
    remote: RemoteInfo,
    onBranchClicked: (Ref) -> Unit,
    onDeleteBranch: (Ref) -> Unit,
) {
    VerticalExpandable(
        header = {
            SideMenuSubentry(
                text = remote.remoteConfig.name,
                iconResourcePath = "cloud.svg",
            )
        }
    ) {
        val branches = remote.branchesList
        Column {
            branches.forEach { branch ->
                ContextMenuArea(
                    items = {
                        remoteBranchesContextMenu(
                            onDeleteBranch = { onDeleteBranch(branch) }
                        )
                    }
                ) {
                    SideMenuSubentry(
                        text = branch.simpleVisibleName,
                        extraPadding = 8.dp,
                        iconResourcePath = "branch.svg",
                        onClick = { onBranchClicked(branch) }
                    )
                }
            }
        }
    }
}