package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.Intent;

/**
 * Get all responses given by the user for a question.
 */
class GetFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

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
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackQuestion.getFeedbackSessionName(),
                feedbackQuestion.getCourseId());

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(
                    feedbackSession.getCourseId());
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
        FeedbackQuestionAttributes questionAttributes = feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);

        List<FeedbackResponseAttributes> responses;
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(questionAttributes.getCourseId());
            responses = feedbackResponsesLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(questionAttributes,
                    studentAttributes);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(
                    questionAttributes.getCourseId());
            responses = feedbackResponsesLogic.getFeedbackResponsesFromInstructorForQuestion(questionAttributes,
                    instructorAttributes);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        List<FeedbackResponseData> responsesData = new LinkedList<>();

        responses.forEach(response -> {
            FeedbackResponseData data = new FeedbackResponseData(response);
            FeedbackResponseCommentAttributes comment = feedbackResponseCommentsLogic
                    .getFeedbackResponseCommentForResponseFromParticipant(response.getId());
            if (comment != null) {
                data.setGiverComment(new FeedbackResponseCommentData(comment));
            }
            responsesData.add(data);
        });
        FeedbackResponsesData result = new FeedbackResponsesData();
        if (!responsesData.isEmpty()) {
            result.setResponses(responsesData);
        }

        return new JsonResult(result);
    }

}
