package teammates.logic.api;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackResponsesLogic;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class FeedbackResponsesLogicAPI {
    private static final FeedbackResponsesLogicAPI instance = new FeedbackResponsesLogicAPI();
    final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();

    FeedbackResponsesLogicAPI() {
        // prevent initialization
    }

    public static FeedbackResponsesLogicAPI inst() {
        return instance;
    }

    /**
     * Checks whether there are responses for a question.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public boolean areThereResponsesForQuestion(String feedbackQuestionId) {
        return feedbackResponsesLogic.areThereResponsesForQuestion(feedbackQuestionId);
    }

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnswerFeedbackSession(String courseId, String feedbackSessionName) {
        assert courseId != null;
        assert feedbackSessionName != null;

        return feedbackResponsesLogic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @see FeedbackResponsesLogic#getSessionResultsForCourse(
     * String, String, String, String, String, FeedbackResultFetchType)
     */
    public SessionResultsBundle getSessionResultsForCourse(
            String feedbackSessionName, String courseId, String userEmail,
            @Nullable String questionId, @Nullable String section, @Nullable FeedbackResultFetchType fetchType) {
        assert feedbackSessionName != null;
        assert courseId != null;
        assert userEmail != null;

        return feedbackResponsesLogic.getSessionResultsForCourse(
                feedbackSessionName, courseId, userEmail, questionId, section, fetchType);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @see FeedbackResponsesLogic#getSessionResultsForUser(String, String, String, boolean, String, boolean)
     */
    public SessionResultsBundle getSessionResultsForUser(
            String feedbackSessionName, String courseId, String userEmail, boolean isInstructor,
            @Nullable String questionId, boolean isPreviewResults) {
        assert feedbackSessionName != null;
        assert courseId != null;
        assert userEmail != null;

        return feedbackResponsesLogic.getSessionResultsForUser(
                feedbackSessionName, courseId, userEmail, isInstructor, questionId, isPreviewResults);
    }

    /**
     * Get existing feedback responses from student or his team for the given question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestionAttributes question, StudentAttributes student) {
        assert question != null;
        assert student != null;

        return feedbackResponsesLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestionAttributes question, InstructorAttributes instructorAttributes) {
        assert question != null;
        assert instructorAttributes != null;

        return feedbackResponsesLogic.getFeedbackResponsesFromGiverForQuestion(
                question.getFeedbackQuestionId(), instructorAttributes.getEmail());
    }

    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        assert feedbackResponseId != null;
        return feedbackResponsesLogic.getFeedbackResponse(feedbackResponseId);
    }

    /**
     * Creates a feedback response.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return created feedback response
     * @throws InvalidParametersException if the response is not valid
     * @throws EntityAlreadyExistsException if the response already exist
     */
    public FeedbackResponseAttributes createFeedbackResponse(FeedbackResponseAttributes feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponse != null;

        return feedbackResponsesLogic.createFeedbackResponse(feedbackResponse);
    }

    public boolean hasResponsesForCourse(String courseId) {
        return feedbackResponsesLogic.hasResponsesForCourse(courseId);
    }

    /**
     * Updates a feedback response by {@link FeedbackResponseAttributes.UpdateOptions}.
     *
     * <p>Cascade updates its associated feedback response comment
     * (e.g. associated response ID, giverSection and recipientSection).
     *
     * <p>If the giver/recipient field is changed, the response is updated by recreating the response
     * as question-giver-recipient is the primary key.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback response
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     * @throws EntityAlreadyExistsException if the response cannot be updated
     *         by recreation because of an existent response
     */
    public FeedbackResponseAttributes updateFeedbackResponseCascade(FeedbackResponseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        assert updateOptions != null;

        return feedbackResponsesLogic.updateFeedbackResponseCascade(updateOptions);
    }

    /**
     * Deletes a feedback response cascade its associated comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackResponseCascade(String responseId) {
        assert responseId != null;
        feedbackResponsesLogic.deleteFeedbackResponseCascade(responseId);
    }
}
