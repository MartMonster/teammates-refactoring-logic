package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;

/**
 * Get a set of givers that has given at least one response in the feedback session.
 */
class GetFeedbackSessionSubmittedGiverSetAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = instructorsLogic.getInstructorForGoogleId(courseId, userInfo.id);

        gateKeeper.verifyAccessible(instructor, feedbackSession);
    }

    @Override
    public JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionSubmittedGiverSet output =
                new FeedbackSessionSubmittedGiverSet(
                        feedbackResponsesLogic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName));

        return new JsonResult(output);
    }

}
