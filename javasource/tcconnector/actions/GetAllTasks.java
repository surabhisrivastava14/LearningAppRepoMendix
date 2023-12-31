// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package tcconnector.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import tcconnector.foundation.TcConnection;
import tcconnector.internal.foundation.Constants;
import tcconnector.proxies.GetAllTasksResponse;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * SOA URL: 
 * Workflow-2008-06-Workflow/getAllTasks
 * 
 * Description:
 * This actions gets all the tasks in a process
 * Returns:
 * An entity of type GetAllTasksResponse. AllTask can be retrieved using association TcConnector.allTasks.Partial errors can be retrieved using association TcConnector.ResponseData/TcConnector.PartialErrors.
 */
public class GetAllTasks extends CustomJavaAction<IMendixObject>
{
	private IMendixObject __InputData;
	private tcconnector.proxies.GetAllTasksInput InputData;
	private java.lang.String BusinessObjectMappings;
	private java.lang.String ConfigurationName;

	public GetAllTasks(IContext context, IMendixObject InputData, java.lang.String BusinessObjectMappings, java.lang.String ConfigurationName)
	{
		super(context);
		this.__InputData = InputData;
		this.BusinessObjectMappings = BusinessObjectMappings;
		this.ConfigurationName = ConfigurationName;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		this.InputData = this.__InputData == null ? null : tcconnector.proxies.GetAllTasksInput.initialize(getContext(), __InputData);

		// BEGIN USER CODE
		GetAllTasksResponse response = new GetAllTasksResponse(getContext());
		response = (GetAllTasksResponse) TcConnection.callTeamcenterService(getContext(),
				Constants.OPERATION_GETALLTASK, InputData.getMendixObject(), response, SERVICE_OPERATION_MAP,
				BusinessObjectMappings, ConfigurationName, true);
		return response.getMendixObject();
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "GetAllTasks";
	}

	// BEGIN EXTRA CODE
	private static final String SERVICE_OPERATION_MAP = "OperationMapping/Workflow/2008-06/Workflow/getAllTasks.json";
	// END EXTRA CODE
}
