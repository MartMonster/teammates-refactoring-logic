package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;
import teammates.ui.output.CourseSectionNamesData;

/**
 * Gets the section names of a course.
 */
class GetCourseSectionNamesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = coursesLogic.getCourse(courseId);
        InstructorAttributes instructor = instructorsLogic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, course);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            List<String> sectionNames = coursesLogic.getSectionNamesForCourse(courseId);
            return new JsonResult(new CourseSectionNamesData(sectionNames));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
