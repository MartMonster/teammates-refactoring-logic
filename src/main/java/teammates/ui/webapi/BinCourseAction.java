package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;
import teammates.ui.output.CourseData;

/**
 * Move a course to the recycle bin.
 */
class BinCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(instructorsLogic.getInstructorForGoogleId(idOfCourseToBin, userInfo.id),
                coursesLogic.getCourse(idOfCourseToBin), Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            CourseAttributes courseAttributes = coursesLogic.getCourse(idOfCourseToBin);
            courseAttributes.setDeletedAt(coursesLogic.moveCourseToRecycleBin(idOfCourseToBin));

            return new JsonResult(new CourseData(courseAttributes));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
