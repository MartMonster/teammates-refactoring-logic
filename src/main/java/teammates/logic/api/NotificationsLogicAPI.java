package teammates.logic.api;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.NotificationsLogic;

import java.util.List;

public class NotificationsLogicAPI {
    private static final NotificationsLogicAPI instance = new NotificationsLogicAPI();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();

    NotificationsLogicAPI() {
        // prevent initialization
    }

    public static NotificationsLogicAPI inst() {
        return instance;
    }

    /**
     * Returns active notification for general users and the specified {@code targetUser}.
     */
    public List<NotificationAttributes> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsLogic.getActiveNotificationsByTargetUser(targetUser);
    }

    public List<NotificationAttributes> getAllNotifications() {
        return notificationsLogic.getAllNotifications();
    }

    /**
     * Gets a notification by ID.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public NotificationAttributes getNotification(String notificationId) {
        return notificationsLogic.getNotification(notificationId);
    }

    /**
     * Creates a notification.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return created notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification exists in the database
     */
    public NotificationAttributes createNotification(NotificationAttributes notification) throws
            InvalidParametersException, EntityAlreadyExistsException {
        return notificationsLogic.createNotification(notification);
    }

    /**
     * Updates a notification.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     * @return updated notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityDoesNotExistException if the notification does not exist in the database
     */
    public NotificationAttributes updateNotification(NotificationAttributes.UpdateOptions updateOptions) throws
            InvalidParametersException, EntityDoesNotExistException {
        return notificationsLogic.updateNotification(updateOptions);
    }

    /**
     * Deletes notification by ID.
     *
     * <ul>
     * <li>Fails silently if no such notification.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteNotification(String notificationId) {
        assert notificationId != null;

        notificationsLogic.deleteNotification(notificationId);
    }
}
