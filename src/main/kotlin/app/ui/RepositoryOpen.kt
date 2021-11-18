package app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.DialogManager
import app.credentials.CredentialsState
import app.git.DiffEntryType
import app.git.GitManager
import app.ui.dialogs.NewBranchDialog
import app.ui.dialogs.PasswordDialog
import app.ui.dialogs.UserPasswordDialog
import openRepositoryDialog
import org.eclipse.jgit.revwalk.RevCommit


@Composable
fun RepositoryOpenPage(gitManager: GitManager, dialogManager: DialogManager) {
    var selectedRevCommit by remember {
        mutableStateOf<RevCommit?>(null)
    }

    var diffSelected by remember {
        mutableStateOf<DiffEntryType?>(null)
    }
    var uncommitedChangesSelected by remember {
        mutableStateOf(false)
    }

    val selectedIndexCommitLog = remember { mutableStateOf(-1) }

    val credentialsState by gitManager.credentialsState.collectAsState()

    if (credentialsState == CredentialsState.HttpCredentialsRequested) {
        dialogManager.show {
            UserPasswordDialog(
                onReject = {
                    gitManager.credentialsDenied()
                    dialogManager.dismiss()
                },
                onAccept = { user, password ->
                    gitManager.httpCredentialsAccepted(user, password)
                    dialogManager.dismiss()
                }
            )
        }
    } else if (credentialsState == CredentialsState.SshCredentialsRequested) {
        dialogManager.show {
            PasswordDialog(
                onReject = {
                    gitManager.credentialsDenied()
                    dialogManager.dismiss()
                },
                onAccept = { password ->
                    gitManager.sshCredentialsAccepted(password)
                    dialogManager.dismiss()
                }
            )
        }
    }

    Column {
        GMenu(
            onRepositoryOpen = {
                openRepositoryDialog(gitManager = gitManager)
            },
            onPull = { gitManager.pull() },
            onPush = { gitManager.push() },
            onStash = { gitManager.stash() },
            onPopStash = { gitManager.popStash() },
            onCreateBranch = {
                dialogManager.show {
                    NewBranchDialog(
                        onReject = {
                            dialogManager.dismiss()
                        },
                        onAccept = { branchName ->
                            gitManager.createBranch(branchName)
                            dialogManager.dismiss()
                        }
                    )
                }
            }
        )

        Row {
            Column(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .weight(0.15f)
                    .fillMaxHeight()
            ) {
                Branches(gitManager = gitManager)
                Tags(gitManager = gitManager)
                Stashes(gitManager = gitManager)
            }
            Box(
                modifier = Modifier
                    .weight(0.60f)
                    .fillMaxHeight()
            ) {
                Crossfade(targetState = diffSelected) { diffEntry ->
                    when (diffEntry) {
                        null -> {
                            Log(
                                gitManager = gitManager,
                                dialogManager = dialogManager,
                                selectedIndex = selectedIndexCommitLog,
                                onRevCommitSelected = { commit ->
                                    selectedRevCommit = commit
                                    uncommitedChangesSelected = false
                                },
                                onUncommitedChangesSelected = {
                                    gitManager.statusShouldBeUpdated()
                                    uncommitedChangesSelected = true
                                },
                            )
                        }
                        else -> {
                            Diff(
                                gitManager = gitManager,
                                diffEntryType = diffEntry,
                                onCloseDiffView = { diffSelected = null })
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()
            ) {
                if (uncommitedChangesSelected) {
                    UncommitedChanges(
                        gitManager = gitManager,
                        onStagedDiffEntrySelected = { diffEntry ->
                            diffSelected = if (diffEntry != null)
                                DiffEntryType.StagedDiff(diffEntry)
                            else
                                null
                        },
                        onUnstagedDiffEntrySelected = { diffEntry ->
                            diffSelected = DiffEntryType.UnstagedDiff(diffEntry)
                        }
                    )
                } else {
                    selectedRevCommit?.let {
                        CommitChanges(
                            gitManager = gitManager,
                            commit = it,
                            onDiffSelected = { diffEntry ->
                                diffSelected = DiffEntryType.CommitDiff(diffEntry)
                            }
                        )
                    }
                }
            }
        }
    }
}