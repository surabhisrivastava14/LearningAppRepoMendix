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
import tcconnector.internal.foundation.Constants;
import tcconnector.proxies.GetLOVValuesResponse;

/**
 * SOA URL:
 * Core-2013-05-LOV/getInitialLOVValues
 * 
 * Desciption:
 * This activity is invoked to query the data for a property having an LOV attachment. The results returned from the server also take into consideration any filter string to be applied on the LOV values to be retrieved. 
 * 
 * Returns:
 * This activity returns both LOV meta data as necessary for the client to render the LOV and partial LOV values list as specified. Maximum number of results to be returned are specified in the LOVFilterDataInputEntity entity. If there are more results, the moreValuesExist flag in the GetLOVValuesResponse Entity will be set to true. If the flag is true, more values can be retrieved with a call to the GetNextLOVValues activity.
 * 
 * For sample usage, kindly download and refer to latest version of 'Teamcenter Connector Sample Application'
 */
public class GetInitialLOVValues extends CustomJavaAction<IMendixObject>
{
	private IMendixObject __inputEntity;
	private tcconnector.proxies.GetInitialLOVValuesInputEntity inputEntity;
	private java.lang.String configurationName;

	public GetInitialLOVValues(IContext context, IMendixObject inputEntity, java.lang.String configurationName)
	{
		super(context);
		this.__inputEntity = inputEntity;
		this.configurationName = configurationName;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		this.inputEntity = this.__inputEntity == null ? null : tcconnector.proxies.GetInitialLOVValuesInputEntity.initialize(getContext(), __inputEntity);

		// BEGIN USER CODE
		GetLOVValuesResponse response = new GetLOVValuesResponse(getContext());
		response = (GetLOVValuesResponse) TcConnection.callTeamcenterService(getContext(),
				Constants.OPERATION_GET_INITIAL_LOV_VALUES, inputEntity.getMendixObject(), response,
				SERVICE_OPERATION_MAP, null, configurationName, true);
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
		return "GetInitialLOVValues";
	}

	// BEGIN EXTRA CODE
	private static final String SERVICE_OPERATION_MAP = "OperationMapping/Core/2013-05/LOV/getInitialLOVValues.json";
	// END EXTRA CODE
}
