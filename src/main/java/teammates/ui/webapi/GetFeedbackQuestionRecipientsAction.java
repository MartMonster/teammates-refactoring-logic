package teammates.ui.webapi;

import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackQuestionRecipientsData;
import teammates.ui.request.Intent;

/**
 * Get the recipients of a feedback question.
 *
 * @see FeedbackQuestionRecipientsData for output format
 */
class GetFeedbackQuestionRecipientsAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);
        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackSessionAttributes feedbackSession =
                getNonNullFeedbackSession(feedbackQuestion.getFeedbackSessionName(), feedbackQuestion.getCourseId());
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackQuestionAttributes question = feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);

        Map<String, FeedbackQuestionRecipient> recipient;
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(question.getCourseId());

            recipient = feedbackQuestionsLogic.getRecipientsOfQuestion(question, null, studentAttributes);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(question.getCourseId());

            recipient = feedbackQuestionsLogic.getRecipientsOfQuestion(question, instructorAttributes, null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
        return new JsonResult(new FeedbackQuestionRecipientsData(recipient));
    }

}
