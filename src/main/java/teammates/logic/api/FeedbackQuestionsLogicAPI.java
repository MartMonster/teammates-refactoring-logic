package teammates.logic.api;

import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackQuestionsLogic;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FeedbackQuestionsLogicAPI {
    private static final FeedbackQuestionsLogicAPI instance = new FeedbackQuestionsLogicAPI();
    final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();

    FeedbackQuestionsLogicAPI() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogicAPI inst() {
        return instance;
    }

    /**
     * Populates fields that need dynamic generation in a question.
     *
     * <p>Currently, only MCQ/MSQ needs to generate choices dynamically.</p>
     *
     * <br/> Preconditions: <br/>
     * * All parameters except <code>teamOfEntityDoingQuestion</code> are non-null.
     *
     * @param feedbackQuestionAttributes the question to populate
     * @param emailOfEntityDoingQuestion the email of the entity doing the question
     * @param teamOfEntityDoingQuestion the team of the entity doing the question. If the entity is an instructor,
     *                                  it can be {@code null}.
     */
    public void populateFieldsToGenerateInQuestion(FeedbackQuestionAttributes feedbackQuestionAttributes,
                                                   String emailOfEntityDoingQuestion, String teamOfEntityDoingQuestion) {
        assert feedbackQuestionAttributes != null;
        assert emailOfEntityDoingQuestion != null;

        feedbackQuestionsLogic.populateFieldsToGenerateInQuestion(
                feedbackQuestionAttributes, emailOfEntityDoingQuestion, teamOfEntityDoingQuestion);
    }

    /**
     * Gets the recipients of a feedback question for student.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestion
     */
    public Map<String, FeedbackQuestionRecipient> getRecipientsOfQuestion(
            FeedbackQuestionAttributes question,
            @Nullable InstructorAttributes instructorGiver, @Nullable StudentAttributes studentGiver) {
        assert question != null;

        // we do not supply course roster here
        return feedbackQuestionsLogic.getRecipientsOfQuestion(question, instructorGiver, studentGiver, null);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     *
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        assert feedbackQuestionId != null;
        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Gets a list of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * instructor can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructors(
            String feedbackSessionName, String courseId, String instructorEmail) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForInstructors(feedbackSessionName, courseId, instructorEmail);
    }

    /**
     * Creates a new feedback question.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     */
    public FeedbackQuestionAttributes createFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion)
            throws InvalidParametersException {
        assert feedbackQuestion != null;

        return feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestion);
    }

    /**
     * Updates a feedback question by {@code FeedbackQuestionAttributes.UpdateOptions}.
     *
     * <p>Cascade adjust the question number of questions in the same session.
     *
     * <p>Cascade adjust the existing response of the question.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback question
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestionAttributes updateFeedbackQuestionCascade(FeedbackQuestionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return feedbackQuestionsLogic.updateFeedbackQuestionCascade(updateOptions);
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>Silently fail if question does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackQuestionCascade(String questionId) {
        assert questionId != null;
        feedbackQuestionsLogic.deleteFeedbackQuestionCascade(questionId);
    }

    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
    }
}
