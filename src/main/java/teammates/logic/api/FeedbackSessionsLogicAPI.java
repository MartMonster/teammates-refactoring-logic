package teammates.logic.api;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackSessionsLogic;

import java.time.Instant;
import java.util.List;

public class FeedbackSessionsLogicAPI {
    private static final FeedbackSessionsLogicAPI instance = new FeedbackSessionsLogicAPI();
    final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();

    FeedbackSessionsLogicAPI() {
        // prevent initialization
    }

    public static FeedbackSessionsLogicAPI inst() {
        return instance;
    }

    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {

        return feedbackSessionsLogic.getAllOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Checks whether an instructor has attempted a feedback session.
     *
     * <p>If there is no question for instructors, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByInstructor(FeedbackSessionAttributes fsa, String userEmail) {
        assert fsa != null;
        assert userEmail != null;
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByInstructor(fsa, userEmail);
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>If there is no question for students, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSessionAttributes fsa, String userEmail, String userTeam) {
        assert fsa != null;
        assert userEmail != null;
        assert userTeam != null;
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByStudent(fsa, userEmail, userTeam);
    }

    /**
     * Creates a feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSessionAttributes createFeedbackSession(FeedbackSessionAttributes feedbackSession)
            throws EntityAlreadyExistsException, InvalidParametersException {
        assert feedbackSession != null;

        return feedbackSessionsLogic.createFeedbackSession(feedbackSession);
    }

    /**
     * Gets a feedback session from the data storage.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found or in recycle bin.
     */
    public FeedbackSessionAttributes getFeedbackSession(String feedbackSessionName, String courseId) {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found.
     */
    public FeedbackSessionAttributes getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        assert courseId != null;
        return feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Gets the expected number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getExpectedTotalSubmission(FeedbackSessionAttributes fsa) {
        assert fsa != null;
        return feedbackSessionsLogic.getExpectedTotalSubmission(fsa);
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getActualTotalSubmission(FeedbackSessionAttributes fsa) {
        assert fsa != null;
        return feedbackSessionsLogic.getActualTotalSubmission(fsa);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {
        assert instructorList != null;
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorList);
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructors(
            List<InstructorAttributes> instructorList) {
        assert instructorList != null;
        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsListForInstructors(instructorList);
    }

    /**
     * Updates the details of a feedback session by {@link FeedbackSessionAttributes.UpdateOptions}.
     *
     * <p>Adjust email sending status if necessary.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback session
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes updateFeedbackSession(FeedbackSessionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return feedbackSessionsLogic.updateFeedbackSession(updateOptions);
    }

    /**
     * Publishes a feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the published feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException if session is already published
     */
    public FeedbackSessionAttributes publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.publishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Unpublishes a feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the unpublished feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException
     *             if the feedback session is not ready to be unpublished.
     */
    public FeedbackSessionAttributes unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.unpublishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);
    }

    /**
     * Soft-deletes a specific session to Recycle Bin.
     */
    public void moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Restores a specific session from Recycle Bin to feedback sessions table.
     */
    public void restoreFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Returns returns a list of sessions that were closed within past hour.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionsClosedWithinThePastHour()
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsClosedWithinThePastHour() {
        return feedbackSessionsLogic.getFeedbackSessionsClosedWithinThePastHour();
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
        return feedbackSessionsLogic.getFeedbackSessionsClosingWithinTimeLimit();
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsOpeningWithinTimeLimit() {
        return feedbackSessionsLogic.getFeedbackSessionsOpeningWithinTimeLimit();
    }

    /**
     * Returns a list of sessions that require automated emails to be sent as they are published.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent() {
        return feedbackSessionsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent() {
        return feedbackSessionsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
    }
}
