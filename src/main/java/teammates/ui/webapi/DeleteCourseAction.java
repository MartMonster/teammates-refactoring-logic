package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;
import teammates.ui.output.MessageOutput;

/**
 * Delete a course.
 */
class DeleteCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(instructorsLogic.getInstructorForGoogleId(idOfCourseToDelete, userInfo.id),
                coursesLogic.getCourse(idOfCourseToDelete),
                Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        coursesLogic.deleteCourseCascade(idOfCourseToDelete);

        return new JsonResult(new MessageOutput("OK"));
    }
}
