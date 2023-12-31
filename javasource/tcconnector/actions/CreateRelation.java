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
import tcconnector.proxies.CreateRelationResponse;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * SOA URL:
 * Core-2006-03-DataManagement/createRelations
 * 
 * Description:
 * Creates the specified relation between the input objects (primary and secondary objects). If the primary object has a relation property by the specified relation name, then the secondary object is associated with the primary object through the relation property.
 * 
 * Returns:
 * The created relations. The partial error is returned for any request relation types that are not valid relation type name.
 */
public class CreateRelation extends CustomJavaAction<IMendixObject>
{
	private IMendixObject __InputData;
	private tcconnector.proxies.CreateRelationInput InputData;
	private java.lang.String BusinessObjectMappings;
	private java.lang.String ConfigurationName;

	public CreateRelation(IContext context, IMendixObject InputData, java.lang.String BusinessObjectMappings, java.lang.String ConfigurationName)
	{
		super(context);
		this.__InputData = InputData;
		this.BusinessObjectMappings = BusinessObjectMappings;
		this.ConfigurationName = ConfigurationName;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		this.InputData = this.__InputData == null ? null : tcconnector.proxies.CreateRelationInput.initialize(getContext(), __InputData);

		// BEGIN USER CODE
		CreateRelationResponse response = new CreateRelationResponse(getContext());
		response = (CreateRelationResponse) TcConnection.callTeamcenterService(getContext(),
				Constants.OPERATION_CREATERELATIONS, InputData.getMendixObject(), response, SERVICE_OPERATION_MAP,
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
		return "CreateRelation";
	}

	// BEGIN EXTRA CODE

	private static final String SERVICE_OPERATION_MAP = "OperationMapping/Core/2006-03/DataManagement/createRelations.json";
	// END EXTRA CODE
}
