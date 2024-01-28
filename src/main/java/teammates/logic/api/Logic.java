package teammates.logic.api;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.logic.core.*;

import java.time.Instant;
import java.util.List;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {

    private static final Logic instance = new Logic();
    final AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();
    final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    final FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = FeedbackResponseCommentsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    /**
     * Persists the given data bundle to the database.
     *
     * @see DataBundleLogic#persistDataBundle(DataBundle)
     */
    public DataBundle persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        return dataBundleLogic.persistDataBundle(dataBundle);
    }

    /**
     * Removes the given data bundle from the database.
     *
     * @see DataBundleLogic#removeDataBundle(DataBundle)
     */
    public void removeDataBundle(DataBundle dataBundle) {
        dataBundleLogic.removeDataBundle(dataBundle);
    }

    /**
     * Puts searchable documents from the data bundle to the database.
     *
     * @see DataBundleLogic#putDocuments(DataBundle)
     */
    public void putDocuments(DataBundle dataBundle) throws SearchServiceException {
        dataBundleLogic.putDocuments(dataBundle);
    }

    /**
     * Creates an account request.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityAlreadyExistsException if the account request already exists
     */
    public AccountRequestAttributes createAccountRequest(AccountRequestAttributes accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        return accountRequestsLogic.createAccountRequest(accountRequest);
    }

    /**
     * Updates an account request.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityDoesNotExistException if the account request to update does not exist
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return accountRequestsLogic.updateAccountRequest(updateOptions);
    }

    /**
     * Deletes an account request.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteAccountRequest(String email, String institute) {
        assert email != null;

        accountRequestsLogic.deleteAccountRequest(email, institute);
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        return accountRequestsLogic.getAccountRequestForRegistrationKey(registrationKey);
    }

    /**
     * Gets an account request by email address and institute.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        return accountRequestsLogic.getAccountRequest(email, institute);
    }

    /**
     * This is used by admin to search account requests in the whole system.
     *
     * @return A list of {@link AccountRequestAttributes} or {@code null} if no match found.
     */
    public List<AccountRequestAttributes> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return accountRequestsLogic.searchAccountRequestsInWholeSystem(queryString);
    }

    /**
     * Creates or updates search document for the given account request.
     *
     * @see AccountRequestsLogic#putDocument(AccountRequestAttributes)
     */
    public void putAccountRequestDocument(AccountRequestAttributes accountRequest) throws SearchServiceException {
        accountRequestsLogic.putDocument(accountRequest);
    }

    public List<UsageStatisticsAttributes> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.toEpochMilli() < endTime.toEpochMilli();

        return usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);
    }

    public UsageStatisticsAttributes calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.toEpochMilli() < endTime.toEpochMilli();
        return usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);
    }

    public void createUsageStatistics(UsageStatisticsAttributes attributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        usageStatisticsLogic.createUsageStatistics(attributes);
    }

    /**
     * Updates a deadline extension.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the updated deadline extension
     * @throws InvalidParametersException if the updated deadline extension is not valid
     * @throws EntityDoesNotExistException if the deadline extension to update does not exist
     */
    public DeadlineExtensionAttributes updateDeadlineExtension(DeadlineExtensionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return deadlineExtensionsLogic.updateDeadlineExtension(updateOptions);
    }

    /**
     * Creates a deadline extension.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created deadline extension
     * @throws InvalidParametersException if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension to create already exists
     */
    public DeadlineExtensionAttributes createDeadlineExtension(DeadlineExtensionAttributes deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert deadlineExtension != null;

        return deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert userEmail != null;

        deadlineExtensionsLogic.deleteDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Gets a deadline extension by {@code courseId}, {@code feedbackSessionName},
     * {@code userEmail} and {@code isInstructor}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the deadline extension if it exists, null otherwise
     */
    public DeadlineExtensionAttributes getDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert userEmail != null;

        return deadlineExtensionsLogic.getDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Gets a list of deadline extensions with end time within the next 24 hours
     * and possibly need a closing email to be sent.
     */
    public List<DeadlineExtensionAttributes> getDeadlineExtensionsPossiblyNeedingClosingEmail() {
        return deadlineExtensionsLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail();
    }

}
