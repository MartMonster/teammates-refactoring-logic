package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.logic.api.NotificationsLogicAPI;

/**
 * Action: Deletes a notification by its ID.
 */
public class DeleteNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String notificationId = getNonNullRequestParamValue(Const.ParamsNames.NOTIFICATION_ID);
        notificationsLogic.deleteNotification(notificationId);
        return new JsonResult("Notification has been deleted.");
    }
}
