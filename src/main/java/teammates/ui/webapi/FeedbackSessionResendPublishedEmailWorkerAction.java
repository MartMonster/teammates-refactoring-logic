package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.InstructorsLogicAPI;
import teammates.ui.request.FeedbackSessionRemindRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Task queue worker action: sends feedback session reminder email to particular students of a course.
 */
class FeedbackSessionResendPublishedEmailWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        FeedbackSessionRemindRequest remindRequest = getAndValidateRequestBody(FeedbackSessionRemindRequest.class);
        String googleIdOfInstructorToNotify = remindRequest.getRequestingInstructorId();
        if (googleIdOfInstructorToNotify == null) {
            throw new InvalidHttpRequestBodyException("Instructor to notify cannot be null.");
        }
        String feedbackSessionName = remindRequest.getFeedbackSessionName();
        String courseId = remindRequest.getCourseId();
        String[] usersToRemind = remindRequest.getUsersToRemind();

        try {
            FeedbackSessionAttributes session = feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentsToEmailList = new ArrayList<>();
            List<InstructorAttributes> instructorsToEmailList = new ArrayList<>();
            InstructorAttributes instructorToNotify =
                    instructorsLogic.getInstructorForGoogleId(courseId, googleIdOfInstructorToNotify);

            for (String userEmail : usersToRemind) {
                StudentAttributes student = studentsLogic.getStudentForEmail(courseId, userEmail);
                if (student != null) {
                    studentsToEmailList.add(student);
                }

                InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
                if (instructor != null) {
                    instructorsToEmailList.add(instructor);
                }
            }

            List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionPublishedEmails(
                    session, studentsToEmailList, instructorsToEmailList, Collections.singletonList(instructorToNotify));
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails", e);
        }
        return new JsonResult("Successful");
    }

}
