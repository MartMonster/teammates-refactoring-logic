package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;

/**
 * Cron job: schedules feedback session opening soon emails to be sent.
 */
class FeedbackSessionOpeningSoonRemindersAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        List<FeedbackSessionAttributes> sessions = feedbackSessionsLogic.getFeedbackSessionsOpeningWithinTimeLimit();
        for (FeedbackSessionAttributes session : sessions) {
            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionOpeningSoonEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);

                feedbackSessionsLogic.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                                .withSentOpeningSoonEmail(true)
                                .build());
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }
        return new JsonResult("Successful");
    }
}
