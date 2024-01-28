package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;

/**
 * Action: Restores a course from Recycle Bin.
 */
class RestoreCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(instructorsLogic.getInstructorForGoogleId(idOfCourseToRestore, userInfo.id),
                coursesLogic.getCourse(idOfCourseToRestore),
                Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {

        String idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String statusMessage;

        try {
            coursesLogic.restoreCourseFromRecycleBin(idOfCourseToRestore);

            statusMessage = "The course " + idOfCourseToRestore + " has been restored.";
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(statusMessage);
    }
}
