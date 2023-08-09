package tcconnector.internal.servicehelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.thirdparty.org.json.JSONArray;
import com.mendix.thirdparty.org.json.JSONObject;

import tcconnector.actions.CallTeamcenterService;
import tcconnector.foundation.JModelObject;
import tcconnector.foundation.TcConnection;
import tcconnector.internal.foundation.ClientMetaModel;
import tcconnector.internal.foundation.Constants;
import tcconnector.proxies.FindSavedQueryInput;

public class AdvancedSearchHelper {
	private static final String KEY_SAVED_QUERIES = "TcConnector.savedQueries";
	private static final String KEY_QUERY_UID = "queryUID";
	private static final String KEY_SEARCH_CRITERIA = "TcConnector.searchCriteria";
	private static final String KEY_OPERATION_MAPPING = "OperationMapping/Query/2010-04/SavedQuery/findSavedQueries.json";
	private static final String KEY_SEARCH_MAPPING = "OperationMapping/Query/2014-11/Finder/performSearch.json";
	private static final String KEY_SAVED_QUERY_OBJECT_MAPPING = "ImanQuery=TcConnector.ImanQuery";
	private static final String KEY_QUERY_NAME_PROP = "query_name";
	private static final String KEY_QUERY = "query";
	private static final String KEY_DESCRIBE_SAVED_QUERY_DEF_REQ_SUBSTITUTE_KEYWORD = "substituteKeyword";
	private static final String KEY_DESCRIBE_SAVED_QUERY_DEF_REQ_REQUESTED_QUERIES = "requestedQueries";
	private static final String KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITIONS = "definitions";
	private static final String KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITION_CLAUSES = "clauses";
	private static final String KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITION_CLAUSE_ENTRY_L10NKEY = "entryL10NKey";
	private static final String KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITION_CLAUSE_ENTRY_NAMEDISPLAY = "entryNameDisplay";

	/**
	 * performs the advance search operation based on query input reagrding
	 * 
	 * @param targetObject The service operation response, may be a ServiceData or
	 *                     custom data structure.
	 * @throws Exception
	 */
	public static IMendixObject performAdvanceSearch(IContext context, String queryName,
			IMendixObject specialisedSearchInput, String businessObjectMapping, String configurationName)
			throws Exception {
		// Find Saved Query SOA call
		String newQueryUidVal = getSavedQueryUID(context, queryName, configurationName);
		if (newQueryUidVal == null)
			return Core.instantiate(context, "TcConnector.FindSavedQueryResponse");

		// Set queryUid value from findSavedQuery SOA to 'queryUID' attribute of Saved
		// Query
		IMendixObject searchCriteriaInput = Core.retrieveByPath(context, specialisedSearchInput, KEY_SEARCH_CRITERIA)
				.get(0);
		searchCriteriaInput.setValue(context, KEY_QUERY_UID, newQueryUidVal);

		// Perform Search SOA call
		IMendixObject searchResponse = Core.instantiate(context, "TcConnector.SearchResponse");
		CallTeamcenterService performSearch = new CallTeamcenterService(context, Constants.OPERATION_PERFORMSEARCH,
				specialisedSearchInput, searchResponse, KEY_SEARCH_MAPPING, businessObjectMapping, configurationName);
		return performSearch.executeAction();
	}

	/**
	 * Retrieves the Saved Query UID using saved query name
	 * 
	 * @param context           Mendix request context
	 * @param queryName         Name of the saved query whose UID is to be
	 *                          retrieved.
	 * @param configurationName Teamcenter Configuration to be used.
	 * @throws Exception
	 */
	public static String getSavedQueryUID(IContext context, String queryName, String configurationName)
			throws Exception {
		FindSavedQueryInput findSavedQueryInput = new FindSavedQueryInput(context);
		findSavedQueryInput.setqueryNames(queryName);
		IMendixObject queryResponse = Core.instantiate(context, "TcConnector.FindSavedQueryResponse");
		CallTeamcenterService findSavedQuery = new CallTeamcenterService(context, Constants.OPERATION_FINDSAVEDQUERIES,
				findSavedQueryInput.getMendixObject(), queryResponse, KEY_OPERATION_MAPPING,
				KEY_SAVED_QUERY_OBJECT_MAPPING, configurationName);
		findSavedQuery.executeAction();
		List<IMendixObject> savedQueries = Core.retrieveByPath(context, queryResponse, KEY_SAVED_QUERIES);
		if (savedQueries.isEmpty()) {
			return null;
		}

		IMendixObject imanQuery = savedQueries.get(0);
		if (queryName != null)
			for (IMendixObject savedQuery : savedQueries)
				if (queryName.equals(savedQuery.getValue(context, KEY_QUERY_NAME_PROP)))
					imanQuery = savedQuery;

		JModelObject imanQueryObj = new JModelObject(context, imanQuery);
		return imanQueryObj.getUID();
	}

	private static JSONObject retrieveSavedQueryDefinition(IContext context, String queryUid, JSONObject policy,
			String configurationName) throws Exception {
		JSONObject describeSavedQueryRequestedQueryObj = new JSONObject();
		describeSavedQueryRequestedQueryObj.put(KEY_QUERY, queryUid);
		describeSavedQueryRequestedQueryObj.put(KEY_DESCRIBE_SAVED_QUERY_DEF_REQ_SUBSTITUTE_KEYWORD, false);

		JSONArray describeSavedQueryRequestedQueryObjArray = new JSONArray();
		describeSavedQueryRequestedQueryObjArray.put(describeSavedQueryRequestedQueryObj);

		JSONObject describeSavedQueryDefinitionRequest = new JSONObject();
		describeSavedQueryDefinitionRequest.put(KEY_DESCRIBE_SAVED_QUERY_DEF_REQ_REQUESTED_QUERIES,
				describeSavedQueryRequestedQueryObjArray);

		// Check the available service to retrieve Saved Query definition
		String describeSavedQueryDefinitionServiceName = Constants.OPERATION_DESCRIBESAVEDQUERYDEFINITION3;
		if (!ClientMetaModel.isServiceAvailable(context, Constants.OPERATION_DESCRIBESAVEDQUERYDEFINITION3,
				configurationName)) {
			describeSavedQueryDefinitionServiceName = Constants.OPERATION_DESCRIBESAVEDQUERYDEFINITION2;
			if (!ClientMetaModel.isServiceAvailable(context, Constants.OPERATION_DESCRIBESAVEDQUERYDEFINITION2,
					configurationName)) {
				describeSavedQueryDefinitionServiceName = Constants.OPERATION_DESCRIBESAVEDQUERYDEFINITION;
				if (!ClientMetaModel.isServiceAvailable(context, Constants.OPERATION_DESCRIBESAVEDQUERYDEFINITION,
						configurationName)) {
					describeSavedQueryDefinitionServiceName = null;
				}
			}
		}

		// Call the DescribeSavedQueryDefinitions service
		return TcConnection.callTeamcenterService(context, describeSavedQueryDefinitionServiceName,
				describeSavedQueryDefinitionRequest, policy, configurationName);
	}

	private static Map<String, String> getCriteriaInfoFromClauses(List<String> l10nKeys,
			JSONArray savedQueryDefinitionClauses) {
		Map<String, String> l10nKeyToLocalizedEntryMap = new HashMap<>();
		for (int l10nKeyIndex = 0; l10nKeyIndex < l10nKeys.size(); ++l10nKeyIndex) {
			String l10nKey = l10nKeys.get(l10nKeyIndex);
			boolean foundClause = false;
			for (int clauseIndex = 0; clauseIndex < savedQueryDefinitionClauses.length()
					&& !foundClause; ++clauseIndex) {
				JSONObject savedQueryDefinitionClause = (JSONObject) savedQueryDefinitionClauses.get(clauseIndex);
				String entryL10NKey = (String) savedQueryDefinitionClause
						.get(KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITION_CLAUSE_ENTRY_L10NKEY);
				if (entryL10NKey.contentEquals(l10nKey)) {
					l10nKeyToLocalizedEntryMap.put(l10nKey, (String) savedQueryDefinitionClause
							.get(KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITION_CLAUSE_ENTRY_NAMEDISPLAY));
					foundClause = true;
				}
			}
		}
		return l10nKeyToLocalizedEntryMap;
	}

	/**
	 * Retrieves the localized entries for a given saved query.
	 * 
	 * @param context           Mendix request context
	 * @param queryUid          Teamcenter Saved Query UID
	 * @param l10nKeys          List of l10n keys corresponding to an entry on the
	 *                          saved query. Saved query criteria entries with ' '
	 *                          character are represented with Entity attributes
	 *                          with '_' character(As Mendix does not allow ' '
	 *                          character for Entity/Attribute names). Consumer of
	 *                          this API prepares the L10N keys from attribute names
	 *                          as given below. e.g. L10N Key for Entity attribute
	 *                          Item_ID is ItemID L10N Key for Entity attribute
	 *                          Alternate_Revision is AlternateRevision i.e. '_' are
	 *                          removed from the attribute names and joined together
	 *                          to form L10N key
	 * @param policy            Policy to be applied for retrieving Teamcenter data
	 * @param configurationName Teamcenter Configuration to be used.
	 * @throws Exception
	 */
	public static Map<String, String> getSavedQueryLocalizedEntries(IContext context, String queryUid,
			List<String> l10nKeys, JSONObject policy, String configurationName) throws Exception {
		JSONObject describeSavedQueryRequestedResponse = retrieveSavedQueryDefinition(context, queryUid, policy,
				configurationName);
		if (describeSavedQueryRequestedResponse != null) {
			JSONArray savedQueryDefinitions = describeSavedQueryRequestedResponse
					.getJSONArray(KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITIONS);
			if (savedQueryDefinitions != null && savedQueryDefinitions.length() > 0) {
				JSONObject savedQueryDefinition = (JSONObject) savedQueryDefinitions.get(0);
				if (savedQueryDefinition != null) {
					JSONArray savedQueryDefinitionClauses = savedQueryDefinition
							.getJSONArray(KEY_DESCRIBE_SAVED_QUERY_DEF_RES_DEFINITION_CLAUSES);
					return getCriteriaInfoFromClauses(l10nKeys, savedQueryDefinitionClauses);
				}
			}
		}
		return new HashMap<>();
	}
}
