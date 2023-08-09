// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package tcconnector.actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;
import com.mendix.thirdparty.org.json.JSONArray;
import com.mendix.thirdparty.org.json.JSONObject;
import com.mendix.webui.CustomJavaAction;
import tcconnector.foundation.BusinessObjectMappings;
import tcconnector.foundation.JPolicy;
import tcconnector.foundation.JServiceData;
import tcconnector.foundation.TcConnection;
import tcconnector.foundation.TcDefaultMappings;
import tcconnector.internal.foundation.Constants;
import tcconnector.internal.servicehelper.AdvancedSearchHelper;
import tcconnector.proxies.ServiceResponse;

/**
 * SOA URL:
 * Query-2010-04-SavedQuery/findSavedQueries
 * Query-2008-06-SavedQuery/executeSavedQueries
 * 
 * This is generic action to perform the search for the saved queries. It takes query name and corresponding search criteria entity for the given query.
 * 
 * Returns:
 * An entity of type ServiceResponse. Search Results can be retrieved using association TcConnector.ResponseData/TcConnector.plain. Partial errors can be retrieved using association TcConnector.ResponseData/TcConnector.PartialErrors.
 */
public class ExecuteSavedQueries extends CustomJavaAction<IMendixObject>
{
	private java.lang.String QueryName;
	private IMendixObject InputData;
	private java.lang.String BusinessObjectMappings;
	private java.lang.String ConfigurationName;

	public ExecuteSavedQueries(IContext context, java.lang.String QueryName, IMendixObject InputData, java.lang.String BusinessObjectMappings, java.lang.String ConfigurationName)
	{
		super(context);
		this.QueryName = QueryName;
		this.InputData = InputData;
		this.BusinessObjectMappings = BusinessObjectMappings;
		this.ConfigurationName = ConfigurationName;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		// BEGIN USER CODE

		IContext context = getContext();

		// Find Saved Query SOA call
		String newQueryUidVal = AdvancedSearchHelper.getSavedQueryUID(context, QueryName, ConfigurationName);
		if (newQueryUidVal == null) {
			return Core.instantiate(context, "TcConnector.FindSavedQueryResponse");
		}

		// executeSavedQueries SOA Call
		JSONObject imanQueryObj = new JSONObject();
		imanQueryObj.put(KEY_UID, newQueryUidVal);

		JSONObject input = new JSONObject();
		input.put(KEY_QUERY, imanQueryObj);

		ArrayList<String> enteries = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();

		if (InputData != null) {
			Map<String, ? extends IMendixObjectMember<?>> allMembers = InputData.getMembers(context);
			for (String l10nKeyAttr : allMembers.keySet()) {
				if (InputData.getMetaObject().getMetaPrimitive(l10nKeyAttr) != null) {
					Object attrValue = InputData.getValue(context, l10nKeyAttr);

					if (attrValue != null) {
						if (attrValue.getClass().getName().contains("Date")) {
							SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
							attrValue = format.format(attrValue);
						}
						enteries.add(TcDefaultMappings.trimSuffix(TcDefaultMappings.trimPrefix(l10nKeyAttr)));
						values.add(attrValue.toString());
					}
				}
			}
		}

		BusinessObjectMappings boMappings = new BusinessObjectMappings(BusinessObjectMappings, ConfigurationName);
		JPolicy policy = new JPolicy(boMappings);

		// Retrieve entries to localized entries map from Teamcenter.
		Map<String, String> localizedEntriesMap = AdvancedSearchHelper.getSavedQueryLocalizedEntries(context,
				newQueryUidVal, enteries, policy, ConfigurationName);

		// Send the localized entries and it's corresponding values for executing the
		// saved queries.
		ArrayList<String> localizedEnteries = new ArrayList<>();
		Iterator<String> entryIterator = enteries.iterator();
		while (entryIterator.hasNext()) {
			localizedEnteries.add(localizedEntriesMap.get(entryIterator.next()));
		}

		input.put(KEY_ENTERIES, localizedEnteries);
		input.put(KEY_VALUES, values);

		input.put(KEY_MAX_TO_RETURN, 0);
		input.put(KEY_RESULT_TYPE, 0);
		input.put(KEY_REQUEST_ID, "");
		input.put(KEY_CLIENT_ID, "");

		JSONArray inputData = new JSONArray();
		inputData.put(input);

		JSONObject inputJson = new JSONObject();
		inputJson.put(KEY_INPUT, inputData);

		// Call the executeSavedQueries service
		JSONObject queryResult = TcConnection.callTeamcenterService(context, Constants.OPERATION_EXECUTESAVEDQUERIES,
				inputJson, policy, ConfigurationName);

		// Load Objects SOA
		JSONObject arrayOfResults = queryResult.getJSONArray(KEY_ARRAY_OF_RESULTS).getJSONObject(0);

		ServiceResponse responseObj = new ServiceResponse(getContext());
		if (arrayOfResults.length() == 0)
			return responseObj.getMendixObject();

		JSONObject loadObjInputJSON = new JSONObject();
		loadObjInputJSON.put(KEY_UIDS, arrayOfResults.getJSONArray(KEY_OBJECT_UIDS));

		// Call loadObjects SOA
		JSONObject searchResult = TcConnection.callTeamcenterService(context, Constants.OPERATION_LOAD_OBJECTS,
				loadObjInputJSON, policy, ConfigurationName);

		JServiceData svcData = new JServiceData(searchResult);
		responseObj.setResponseData(svcData.instantiateServiceData(getContext(), boMappings, ConfigurationName, true));

		return responseObj.getMendixObject();

		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "ExecuteSavedQueries";
	}

	// BEGIN EXTRA CODE
	private static final String KEY_INPUT = "input";
	private static final String KEY_QUERY = "query";
	private static final String KEY_ENTERIES = "entries";
	private static final String KEY_VALUES = "values";
	private static final String KEY_MAX_TO_RETURN = "maxNumToReturn";
	private static final String KEY_RESULT_TYPE = "resultsType";
	private static final String KEY_REQUEST_ID = "requestId";
	private static final String KEY_CLIENT_ID = "clientId";
	private static final String KEY_UIDS = "uids";
	private static final String KEY_ARRAY_OF_RESULTS = "arrayOfResults";
	private static final String KEY_OBJECT_UIDS = "objectUIDS";
	private static final String KEY_UID = "uid";
	// END EXTRA CODE
}