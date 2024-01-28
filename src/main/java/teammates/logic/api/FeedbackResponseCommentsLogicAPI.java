package teammates.logic.api;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackResponseCommentsLogic;

public class FeedbackResponseCommentsLogicAPI {
    private static final FeedbackResponseCommentsLogicAPI instance = new FeedbackResponseCommentsLogicAPI();
    final FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = FeedbackResponseCommentsLogic.inst();

    FeedbackResponseCommentsLogicAPI() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsLogicAPI inst() {
        return instance;
    }

    /**
     * Create a feedback response comment, and return the created comment.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public FeedbackResponseCommentAttributes createFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        assert feedbackResponseComment != null;

        return feedbackResponseCommentsLogic.createFeedbackResponseComment(feedbackResponseComment);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        assert feedbackResponseCommentId != null;
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(feedbackResponseCommentId);
    }

    /**
     * Gets comment associated with the response.
     *
     * <p>The comment is given by a feedback participant to explain the response</p>
     *
     * @param feedbackResponseId the response id
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseCommentForResponseFromParticipant(
            String feedbackResponseId) {
        assert feedbackResponseId != null;

        return feedbackResponseCommentsLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseCommentAttributes.UpdateOptions}.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes.UpdateOptions updateOptions)
            throws EntityDoesNotExistException, InvalidParametersException {
        assert updateOptions != null;

        return feedbackResponseCommentsLogic.updateFeedbackResponseComment(updateOptions);
    }

    /**
     * Deletes a comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        feedbackResponseCommentsLogic.deleteFeedbackResponseComment(commentId);
    }
}
