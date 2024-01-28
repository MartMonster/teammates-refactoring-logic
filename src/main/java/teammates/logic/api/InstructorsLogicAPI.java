package teammates.logic.api;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.*;
import teammates.logic.core.InstructorsLogic;

import java.util.Collection;
import java.util.List;

public class InstructorsLogicAPI {
    private static final InstructorsLogicAPI instance = new InstructorsLogicAPI();
    final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    InstructorsLogicAPI() {
        // prevent initialization
    }

    public static InstructorsLogicAPI inst() {
        return instance;
    }

    /**
     * Verifies that all the given instructors exist in the given course.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If some instructor does not exist in the course.
     */
    public void verifyAllInstructorsExistInCourse(String courseId, Collection<String> instructorEmailAddresses)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert instructorEmailAddresses != null;

        instructorsLogic.verifyAllInstructorsExistInCourse(courseId, instructorEmailAddresses);
    }

    /**
     * Creates an instructor.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database
     */
    public InstructorAttributes createInstructor(InstructorAttributes instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert instructor != null;

        return instructorsLogic.createInstructor(instructor);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @return Null if no match found.
     */
    public List<InstructorAttributes> searchInstructorsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return instructorsLogic.searchInstructorsInWholeSystem(queryString);
    }

    /**
     * Creates or updates search document for the given instructor.
     *
     * @see InstructorsLogic#putDocument(InstructorAttributes)
     */
    public void putInstructorDocument(InstructorAttributes instructor) throws SearchServiceException {
        instructorsLogic.putDocument(instructor);
    }

    /**
     * Update instructor being edited to ensure validity of instructors for the course.
     *
     * @see InstructorsLogic#updateToEnsureValidityOfInstructorsForTheCourse(String, InstructorAttributes)
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {

        assert courseId != null;
        assert instructorToEdit != null;

        instructorsLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.getInstructorForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.getInstructorById(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {

        assert googleId != null;
        assert courseId != null;

        return instructorsLogic.getInstructorForGoogleId(courseId, googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {

        assert registrationKey != null;

        return instructorsLogic.getInstructorForRegistrationKey(registrationKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {

        assert googleId != null;

        return instructorsLogic.getInstructorsForGoogleId(googleId);
    }

    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {

        assert googleId != null;

        return instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {

        assert courseId != null;

        return instructorsLogic.getInstructorsForCourse(courseId);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithGoogleId}.
     *
     * <p>Cascade update the comments, responses and deadline extensions associated with the instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorCascade(InstructorAttributes.UpdateOptionsWithGoogleId updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return instructorsLogic.updateInstructorByGoogleIdCascade(updateOptions);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructor(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return instructorsLogic.updateInstructorByEmail(updateOptions);
    }

    /**
     * Deletes an instructor cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteInstructorCascade(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        instructorsLogic.deleteInstructorCascade(courseId, email);
    }

    /**
     * Changes the archive status of a course for an instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @param courseId The course of which the archive status is to be changed
     * @param archiveStatus The archive status to be set
     */
    public void setArchiveStatusOfInstructor(String googleId, String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {

        assert googleId != null;
        assert courseId != null;

        instructorsLogic.setArchiveStatusOfInstructor(googleId, courseId, archiveStatus);
    }

    /**
     * Regenerates the registration key for the instructor with email address {@code email} in course {@code courseId}.
     *
     * @return the instructor attributes with the new registration key.
     * @throws EntityAlreadyExistsException if the newly generated instructor has the same registration key as the
     *          original one.
     * @throws EntityDoesNotExistException if the instructor does not exist.
     */
    public InstructorAttributes regenerateInstructorRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.regenerateInstructorRegistrationKey(courseId, email);
    }

    /**
     * Resets the associated googleId of an instructor.
     */
    public void resetInstructorGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        assert originalEmail != null;
        assert courseId != null;

        instructorsLogic.resetInstructorGoogleId(originalEmail, courseId);
    }
}
