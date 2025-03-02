package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.logic.api.AccountsLogicAPI;

/**
 * Action: deletes an existing account (either student or instructor).
 */
class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        accountsLogic.deleteAccountCascade(googleId);
        return new JsonResult("Account is successfully deleted.");
    }

}
