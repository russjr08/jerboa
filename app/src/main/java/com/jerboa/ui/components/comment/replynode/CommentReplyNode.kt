package com.jerboa.ui.components.comment.replynode

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.VoteType
import com.jerboa.datatypes.CommentReplyView
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.CommentBody
import com.jerboa.ui.components.comment.PostAndCommunityContextHeader
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING
import com.jerboa.ui.theme.muted

@Composable
fun CommentReplyNodeHeader(
    commentReplyView: CommentReplyView,
    onPersonClick: (personId: Int) -> Unit,
    score: Int,
    myVote: Int?,
    onLongClick: () -> Unit = {}
) {
    CommentOrPostNodeHeader(
        creator = commentReplyView.creator,
        score = score,
        myVote = myVote,
        published = commentReplyView.comment.published,
        updated = commentReplyView.comment.updated,
        deleted = commentReplyView.comment.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = false,
        isModerator = false,
        isCommunityBanned = commentReplyView.creator_banned_from_community,
        onLongClick = onLongClick
    )
}

@Preview
@Composable
fun CommentReplyNodeHeaderPreview() {
    CommentReplyNodeHeader(
        commentReplyView = sampleCommentReplyView,
        score = 23,
        myVote = 26,
        onPersonClick = {}
    )
}

@Composable
fun CommentReplyNodeFooterLine(
    commentReplyView: CommentReplyView,
    onUpvoteClick: (commentReplyView: CommentReplyView) -> Unit,
    onDownvoteClick: (commentReplyView: CommentReplyView) -> Unit,
    onReplyClick: (commentReplyView: CommentReplyView) -> Unit,
    onSaveClick: (commentReplyView: CommentReplyView) -> Unit,
    onMarkAsReadClick: (commentReplyView: CommentReplyView) -> Unit,
    onViewSourceClick: () -> Unit,
    onReportClick: (commentReplyView: CommentReplyView) -> Unit,
    onCommentLinkClick: (commentReplyView: CommentReplyView) -> Unit,
    onBlockCreatorClick: (creator: PersonSafe) -> Unit,
    myVote: Int?,
    upvotes: Int,
    downvotes: Int,
    account: Account?
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showMoreOptions) {
        CommentReplyNodeOptionsDialog(
            commentReplyView = commentReplyView,
            onDismissRequest = { showMoreOptions = false },
            onViewSourceClick = {
                showMoreOptions = false
                onViewSourceClick()
            },
            onReportClick = {
                showMoreOptions = false
                onReportClick(commentReplyView)
            },
            onBlockCreatorClick = {
                showMoreOptions = false
                onBlockCreatorClick(commentReplyView.creator)
            },
            isCreator = account?.id == commentReplyView.creator.id
        )
    }

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LARGE_PADDING, bottom = SMALL_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING)
        ) {
            VoteGeneric(
                myVote = myVote,
                votes = upvotes,
                item = commentReplyView,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                showNumber = (downvotes != 0),
                account = account
            )
            VoteGeneric(
                myVote = myVote,
                votes = downvotes,
                item = commentReplyView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
                account = account
            )
            ActionBarButton(
                icon = Icons.Outlined.Link,
                onClick = { onCommentLinkClick(commentReplyView) },
                account = account
            )
            ActionBarButton(
                icon = if (commentReplyView.comment_reply.read) {
                    Icons.Outlined.MarkChatRead
                } else {
                    Icons.Outlined.MarkChatUnread
                },
                onClick = { onMarkAsReadClick(commentReplyView) },
                contentColor = if (commentReplyView.comment_reply.read) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.muted
                },
                account = account
            )
            ActionBarButton(
                icon = if (commentReplyView.saved) { Icons.Filled.Bookmark } else {
                    Icons.Outlined.BookmarkBorder
                },
                onClick = { onSaveClick(commentReplyView) },
                contentColor = if (commentReplyView.saved) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.muted
                },
                account = account
            )
            // Don't let you respond to your own comment.
            if (commentReplyView.creator.id != account?.id) {
                ActionBarButton(
                    icon = Icons.Outlined.Textsms,
                    onClick = { onReplyClick(commentReplyView) },
                    account = account
                )
            }
            ActionBarButton(
                icon = Icons.Outlined.MoreVert,
                account = account,
                onClick = { showMoreOptions = !showMoreOptions }
            )
        }
    }
}

@Composable
fun CommentReplyNodeOptionsDialog(
    commentReplyView: CommentReplyView,
    onDismissRequest: () -> Unit,
    onViewSourceClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockCreatorClick: () -> Unit,
    isCreator: Boolean
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "View Source",
                    icon = Icons.Outlined.Description,
                    onClick = onViewSourceClick
                )
                IconAndTextDrawerItem(
                    text = "Copy Permalink",
                    icon = Icons.Outlined.Link,
                    onClick = {
                        val permalink = "${commentReplyView.comment.ap_id}"
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(ctx, "Permalink Copied", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    }
                )
                if (!isCreator) {
                    IconAndTextDrawerItem(
                        text = "Report Comment",
                        icon = Icons.Outlined.Flag,
                        onClick = onReportClick
                    )
                    IconAndTextDrawerItem(
                        text = "Block ${commentReplyView.creator.name}",
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCreatorClick
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun CommentReplyNode(
    commentReplyView: CommentReplyView,
    onUpvoteClick: (commentReplyView: CommentReplyView) -> Unit,
    onDownvoteClick: (commentReplyView: CommentReplyView) -> Unit,
    onReplyClick: (commentReplyView: CommentReplyView) -> Unit,
    onSaveClick: (commentReplyView: CommentReplyView) -> Unit,
    onMarkAsReadClick: (commentReplyView: CommentReplyView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    onReportClick: (commentReplyView: CommentReplyView) -> Unit,
    onCommentLinkClick: (commentReplyView: CommentReplyView) -> Unit,
    onBlockCreatorClick: (creator: PersonSafe) -> Unit,
    account: Account?
) {
    // These are necessary for instant comment voting
    val score = commentReplyView.counts.score
    val myVote = commentReplyView.my_vote
    val upvotes = commentReplyView.counts.upvotes
    val downvotes = commentReplyView.counts.downvotes

    var viewSource by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(horizontal = LARGE_PADDING)
    ) {
        Divider()
        PostAndCommunityContextHeader(
            post = commentReplyView.post,
            community = commentReplyView.community,
            onCommunityClick = onCommunityClick,
            onPostClick = onPostClick
        )
        CommentReplyNodeHeader(
            commentReplyView = commentReplyView,
            onPersonClick = onPersonClick,
            score = score,
            myVote = myVote,
            onLongClick = {
                isExpanded = !isExpanded
            }
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                CommentBody(
                    comment = commentReplyView.comment,
                    viewSource = viewSource
                )
                CommentReplyNodeFooterLine(
                    commentReplyView = commentReplyView,
                    onUpvoteClick = {
                        onUpvoteClick(it)
                    },
                    onDownvoteClick = {
                        onDownvoteClick(it)
                    },
                    onViewSourceClick = {
                        viewSource = !viewSource
                    },
                    onReplyClick = onReplyClick,
                    onSaveClick = onSaveClick,
                    onMarkAsReadClick = onMarkAsReadClick,
                    onReportClick = onReportClick,
                    onCommentLinkClick = onCommentLinkClick,
                    onBlockCreatorClick = onBlockCreatorClick,
                    myVote = myVote,
                    upvotes = upvotes,
                    downvotes = downvotes,
                    account = account
                )
            }
        }
    }
}
