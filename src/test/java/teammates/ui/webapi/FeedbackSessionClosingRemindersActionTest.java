package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.logic.api.CoursesLogicAPI;
import teammates.logic.api.FeedbackSessionsLogicAPI;
import teammates.test.ThreadHelper;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionClosingRemindersAction}.
 */
public class FeedbackSessionClosingRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosingRemindersAction> {
    private final FeedbackSessionsLogicAPI feedbackSessionsLogic = FeedbackSessionsLogicAPI.inst();
    private final CoursesLogicAPI coursesLogic = CoursesLogicAPI.inst();

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        ______TS("default state of typical data bundle: 0 sessions/deadline extensions closing soon");

        FeedbackSessionClosingRemindersAction action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("1 session closing soon, 1 session closing soon with disabled closing reminder, "
                + "1 session closing soon but not yet opened");

        // Modify session to close in 24 hours

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1.setTimeZone("UTC");
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withTimeZone(session1.getTimeZone())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());
        session1.setSentOpeningSoonEmail(true); // fsLogic will set the flag to true
        session1.setSentOpenEmail(true); // fsLogic will set the flag to true
        verifyPresentInDatabase(session1);

        // Ditto, but disable the closing reminder

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session1InCourse2");
        session2.setTimeZone("UTC");
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        session2.setClosingEmailEnabled(false);
        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withTimeZone(session2.getTimeZone())
                        .withStartTime(session2.getStartTime())
                        .withEndTime(session2.getEndTime())
                        .withIsClosingEmailEnabled(session2.isClosingEmailEnabled())
                        .build());
        session2.setSentOpeningSoonEmail(true); // fsLogic will set the flag to true
        session2.setSentOpenEmail(true); // fsLogic will set the flag to true
        verifyPresentInDatabase(session2);

        // 1 session not yet opened; do not send the closing reminder

        FeedbackSessionAttributes session3 = typicalBundle.feedbackSessions.get("gracePeriodSession");
        session3.setTimeZone("UTC");
        session3.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1));
        session3.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session3.getFeedbackSessionName(), session3.getCourseId())
                        .withTimeZone(session3.getTimeZone())
                        .withStartTime(session3.getStartTime())
                        .withEndTime(session3.getEndTime())
                        .build());
        session3.setSentOpeningSoonEmail(true); // fsLogic will set the flag to true
        session3.setSentOpenEmail(false); // fsLogic will set the flag to true
        verifyPresentInDatabase(session3);

        // wait for very briefly so that the above session will be within the time limit
        ThreadHelper.waitFor(5);

        action = getAction();
        action.execute();

        // 5 students, 5 instructors, and 3 co-owner instructors in course1
        // 3 students and 2 instructors in session have deadline extensions and should not receive email
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 8);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                    courseName, session1.getFeedbackSessionName());
            assertEquals(expectedSubject, email.getSubject());
        }

        ______TS("1 session closing soon with emails sent;"
                + "deadline extensions closing within next 24 hours have emails sent");

        session1.setSentClosingEmail(true);
        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session3.getFeedbackSessionName(), session3.getCourseId())
                        .withSentClosingEmail(session3.isSentClosingEmail())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("2 students and 1 instructor with valid deadline extensions,"
                + "1 student in session with reminders disabled,"
                + "2 students with invalid deadline extensions");

        // update deadline extensions to have end time within the next 24 hours
        var studentDe = typicalBundle.deadlineExtensions.get("student3InCourse1Session1");
        var studentOutdatedEndTimeDe = typicalBundle.deadlineExtensions.get("student4InCourse1Session1");
        var studentDeletedDeadlineDe = typicalBundle.deadlineExtensions.get("student5InCourse1Session1");
        var studentRemindersDisabledDe = typicalBundle.deadlineExtensions.get("student4InCourse1Session2");
        var studentDifferentCourseDe = typicalBundle.deadlineExtensions.get("student1InCourse1GracePeriodSession");
        var instructorDe = typicalBundle.deadlineExtensions.get("instructor2InCourse1Session1");

        List<DeadlineExtensionAttributes> deadlineExtensions = List.of(
                studentDe,
                studentRemindersDisabledDe,
                studentDifferentCourseDe,
                studentOutdatedEndTimeDe,
                studentDeletedDeadlineDe,
                instructorDe);

        Instant extendedDeadlineTime = TimeHelperExtension.getInstantHoursOffsetFromNow(16);
        Instant sessionClosingTime = TimeHelperExtension.getInstantHoursOffsetFromNow(2);

        for (var deadlineExtension : deadlineExtensions) {
            logic.updateDeadlineExtension(
                    DeadlineExtensionAttributes
                            .updateOptionsBuilder(
                                deadlineExtension.getCourseId(),
                                deadlineExtension.getFeedbackSessionName(),
                                deadlineExtension.getUserEmail(),
                                deadlineExtension.getIsInstructor())
                            .withEndTime(extendedDeadlineTime)
                            .build());
        }

        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withEndTime(sessionClosingTime)
                        .withSentClosingEmail(true)
                        .withStudentDeadlines(Map.of(studentDe.getUserEmail(), extendedDeadlineTime,
                                studentOutdatedEndTimeDe.getUserEmail(), extendedDeadlineTime.minusSeconds(60 * 60)))
                        .withInstructorDeadlines(Map.of(instructorDe.getUserEmail(), extendedDeadlineTime))
                        .build());
        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withEndTime(sessionClosingTime)
                        .withSentClosingEmail(true)
                        .withStudentDeadlines(Map.of(studentRemindersDisabledDe.getUserEmail(), extendedDeadlineTime))
                        .build());
        feedbackSessionsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session3.getFeedbackSessionName(), session3.getCourseId())
                        .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1))
                        .withEndTime(sessionClosingTime)
                        .withSentClosingEmail(true)
                        .withIsClosingEmailEnabled(true)
                        .withStudentDeadlines(Map.of(studentDifferentCourseDe.getUserEmail(), extendedDeadlineTime))
                        .build());

        // wait for very briefly so that the above session will be within the time limit
        ThreadHelper.waitFor(5);

        action = getAction();
        action.execute();

        // sentClosingEmail is true for all sessions, should only send emails to those with extended deadlines
        // 2 students, 1 instructor with valid deadline extensions within time period
        // 1 student in session with reminders disabled
        // 1 student with outdated deadline, 1 student with deleted deadline
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        tasksAdded = mockTaskQueuer.getTasksAdded();
        for (var task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubjectSession1 = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                    courseName, session1.getFeedbackSessionName());
            String expectedSubjectSession3 = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                    courseName, session3.getFeedbackSessionName());
            assertTrue(expectedSubjectSession1.equals(email.getSubject())
                    || expectedSubjectSession3.equals(email.getSubject()));
        }

    }

}
