package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;
import teammates.ui.output.HasResponsesData;

/**
 * Checks whether a course or question has responses for instructor.
 * Checks whether a student has responded a feedback session.
 */
class GetHasResponsesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {

        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(entityType.equals(Const.EntityType.STUDENT) || entityType.equals(Const.EntityType.INSTRUCTOR))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            //An instructor of the feedback session can check responses for questions within it.
            String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            if (questionId != null) {
                FeedbackQuestionAttributes feedbackQuestionAttributes = feedbackQuestionsLogic.getFeedbackQuestion(questionId);
                FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(
                        feedbackQuestionAttributes.getFeedbackSessionName(),
                        feedbackQuestionAttributes.getCourseId());

                gateKeeper.verifyAccessible(
                        instructorsLogic.getInstructorForGoogleId(feedbackQuestionAttributes.getCourseId(), userInfo.getId()),
                        feedbackSession);

                //prefer question check over course checks
                return;
            }

            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            gateKeeper.verifyAccessible(
                    instructorsLogic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    coursesLogic.getCourse(courseId));
            return;
        }

        //An student can check whether he has submitted responses for a feedback session in his course.
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (feedbackSessionName != null) {
            gateKeeper.verifyAccessible(
                    studentsLogic.getStudentForGoogleId(courseId, userInfo.getId()),
                    getNonNullFeedbackSession(feedbackSessionName, courseId));
        }

        List<FeedbackSessionAttributes> feedbackSessions = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
        if (feedbackSessions.isEmpty()) {
            // Course has no sessions and therefore no response; access to responses is safe for all.
            return;
        }

        // Verify that all sessions are accessible to the user.
        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            if (!feedbackSession.isVisible()) {
                // Skip invisible sessions.
                continue;
            }

            gateKeeper.verifyAccessible(
                    studentsLogic.getStudentForGoogleId(courseId, userInfo.getId()),
                    feedbackSession);
        }
    }

    @Override
    public JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            return handleInstructorReq();
        }

        // Default path for student and admin
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (feedbackSessionName == null) {
            // check all sessions in the course
            List<FeedbackSessionAttributes> feedbackSessions = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
            StudentAttributes student = studentsLogic.getStudentForGoogleId(courseId, userInfo.getId());

            Map<String, Boolean> sessionsHasResponses = new HashMap<>();
            for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
                if (!feedbackSession.isVisible()) {
                    // Skip invisible sessions.
                    continue;
                }
                boolean hasResponses = feedbackSessionsLogic.isFeedbackSessionAttemptedByStudent(
                        feedbackSession, student.getEmail(), student.getTeam());
                sessionsHasResponses.put(feedbackSession.getFeedbackSessionName(), hasResponses);
            }
            return new JsonResult(new HasResponsesData(sessionsHasResponses));
        }

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        StudentAttributes student = studentsLogic.getStudentForGoogleId(courseId, userInfo.getId());
        return new JsonResult(new HasResponsesData(
                feedbackSessionsLogic.isFeedbackSessionAttemptedByStudent(feedbackSession, student.getEmail(), student.getTeam())));
    }

    private JsonResult handleInstructorReq() {
        String feedbackQuestionID = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (feedbackQuestionID != null) {
            if (feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionID) == null) {
                throw new EntityNotFoundException("No feedback question with id: " + feedbackQuestionID);
            }

            boolean hasResponses = feedbackResponsesLogic.areThereResponsesForQuestion(feedbackQuestionID);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (coursesLogic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        boolean hasResponses = feedbackResponsesLogic.hasResponsesForCourse(courseId);
        return new JsonResult(new HasResponsesData(hasResponses));
    }
}
