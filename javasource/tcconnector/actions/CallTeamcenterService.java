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
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.webui.CustomJavaAction;
import tcconnector.foundation.TcConnection;
import tcconnector.proxies.ServiceResponse;

/**
 * Tc Version:
 * Teamcenter 10.1.5
 * 
 * Description:
 * The CallTeamcenterService Java Action is the entry point for calling Teamcenter service operations from a Microflow. This Java Action allows the developer to create a custom mapping between the Teamcenter service operation and the Mendix Domain Model Entities.
 * 
 * Returns:
 * Service response of type TcConnector.ServiceResponse. The returned list of model objects can be retrieved using appropriate association. Partial errors can be retrieved using TcConnector.ResponseData/TcConnector.PartialErrors
 * 
 * For more information on the usage of this Java Action kindly refer to connector documentation.
 */
public class CallTeamcenterService extends CustomJavaAction<IMendixObject>
{
	private java.lang.String ServiceName;
	private IMendixObject InputArgument;
	private IMendixObject __ResponseObject;
	private tcconnector.proxies.ServiceResponse ResponseObject;
	private java.lang.String OperationMapping;
	private java.lang.String BusinessObjectMappings;
	private java.lang.String ConfigurationName;

	public CallTeamcenterService(IContext context, java.lang.String ServiceName, IMendixObject InputArgument, IMendixObject ResponseObject, java.lang.String OperationMapping, java.lang.String BusinessObjectMappings, java.lang.String ConfigurationName)
	{
		super(context);
		this.ServiceName = ServiceName;
		this.InputArgument = InputArgument;
		this.__ResponseObject = ResponseObject;
		this.OperationMapping = OperationMapping;
		this.BusinessObjectMappings = BusinessObjectMappings;
		this.ConfigurationName = ConfigurationName;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		this.ResponseObject = this.__ResponseObject == null ? null : tcconnector.proxies.ServiceResponse.initialize(getContext(), __ResponseObject);

		// BEGIN USER CODE
		// TcSession tcSession = null;
		ServiceResponse response = TcConnection.callTeamcenterService(getContext(), ServiceName, InputArgument,
				ResponseObject, OperationMapping, BusinessObjectMappings, ConfigurationName, true);
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
		return "CallTeamcenterService";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
