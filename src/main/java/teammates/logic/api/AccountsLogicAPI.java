package teammates.logic.api;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.AccountsLogic;

import java.time.Instant;
import java.util.List;

public class AccountsLogicAPI {
    private static final AccountsLogicAPI instance = new AccountsLogicAPI();
    final AccountsLogic accountsLogic = AccountsLogic.inst();

    AccountsLogicAPI() {
        // prevent initialization
    }

    public static AccountsLogicAPI inst() {
        return instance;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public AccountAttributes getAccount(String googleId) {
        assert googleId != null;

        return accountsLogic.getAccount(googleId);
    }

    /**
     * Returns a list of accounts with email matching {@code email}.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     */
    public List<AccountAttributes> getAccountsForEmail(String email) {
        assert email != null;

        return accountsLogic.getAccountsForEmail(email);
    }

    public List<String> getReadNotificationsId(String googleId) {
        return accountsLogic.getReadNotificationsId(googleId);
    }

    /**
     * Updates user read status for notification with ID {@code notificationId} and expiry time {@code endTime}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null. {@code endTime} must be after current moment.
     */
    public List<String> updateReadNotifications(String googleId, String notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert googleId != null;
        return accountsLogic.updateReadNotifications(googleId, notificationId, endTime);
    }

    /**
     * Deletes both instructor and student privileges, as well as the account.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteAccountCascade(String googleId) {

        assert googleId != null;

        accountsLogic.deleteAccountCascade(googleId);
    }

    public InstructorAttributes joinCourseForInstructor(String regkey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert regkey != null;

        return accountsLogic.joinCourseForInstructor(regkey, googleId);
    }

    /**
     * Make the student join the course, i.e. associate the Google ID to the student.<br>
     * Create an account for the student if no existing account is found.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param key the registration key
     */
    public StudentAttributes joinCourseForStudent(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert key != null;

        return accountsLogic.joinCourseForStudent(key, googleId);

    }
}
