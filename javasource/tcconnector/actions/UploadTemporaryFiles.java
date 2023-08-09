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
import com.mendix.thirdparty.org.json.JSONArray;
import com.mendix.thirdparty.org.json.JSONObject;
import com.mendix.webui.CustomJavaAction;
import tcconnector.foundation.TcConnection;
import tcconnector.internal.foundation.Constants;
import tcconnector.internal.foundation.FMSUtils;
import tcconnector.internal.foundation.Messages;

/**
 * SOA URL: 
 * Core-2007-01-FileManagement/getTransientFileTicketsForUpload
 * 
 * Uploads one or more to Teamcenter Transient Volume using teamcenter FMS service*. 
 * 
 * Input -
 * - List of Files to be uploaded
 * 
 * Output -
 * - List of StringObject. Each StringObject holds File-Ticket information corresponding to input File.
 * 
 * * - By default "deleteFlag" on uploaded transient files is set to true. It indicates the file would be deleted from temporary storage after it is read.
 */
public class UploadTemporaryFiles extends CustomJavaAction<java.util.List<IMendixObject>>
{
	private java.lang.String ConfigurationName;
	private java.util.List<IMendixObject> __Files;
	private java.util.List<tcconnector.proxies.FileDocument> Files;

	public UploadTemporaryFiles(IContext context, java.lang.String ConfigurationName, java.util.List<IMendixObject> Files)
	{
		super(context);
		this.ConfigurationName = ConfigurationName;
		this.__Files = Files;
	}

	@java.lang.Override
	public java.util.List<IMendixObject> executeAction() throws Exception
	{
		this.Files = java.util.Optional.ofNullable(this.__Files)
			.orElse(java.util.Collections.emptyList())
			.stream()
			.map(__FilesElement -> tcconnector.proxies.FileDocument.initialize(getContext(), __FilesElement))
			.collect(java.util.stream.Collectors.toList());

		// BEGIN USER CODE
		java.util.List<IMendixObject> fileTickets = null;
		try {

			if (Files.size() > 0) {
				JSONObject createTransientFileTicketsResponse = createTransientFileTickets();
				if (areTicketsCreated(createTransientFileTicketsResponse) == true) {
					// Upload the files.
					// Method would throw exception in case of error. Hence no return value
					fileTickets = FMSUtils.uploadFilestoFMS(getContext(), ConfigurationName,
							createTransientFileTicketsResponse, Files);
				}
			} else {
				Constants.LOGGER.info(Messages.Dataset.NoFilesAvailableToUpload);
			}
		} catch (Exception e) {
			Constants.LOGGER.error(Messages.Dataset.UploadFilesError + e.getMessage());
			fileTickets.clear();
			throw e;
		}
		return fileTickets;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "UploadTemporaryFiles";
	}

	// BEGIN EXTRA CODE

	/*
	 * createDataset service would return Dataset UID in datasetOutput
	 */
	private boolean areTicketsCreated(JSONObject createTransientFileTicketsResponse) {
		boolean success = Boolean.FALSE;
		if (createTransientFileTicketsResponse.getJSONArray("transientFileTicketInfos").length() > 0) {
			for (int i = 0; i < Files.size(); i++) {
				JSONObject transientFileTicketInfo = (JSONObject) createTransientFileTicketsResponse
						.getJSONArray("transientFileTicketInfos").get(i);
				int length = transientFileTicketInfo.getString("ticket").length();
				if (length > 0) {
					success = Boolean.TRUE;
				} else {
					success = Boolean.FALSE;
					break;
				}
			}
		} else {
			success = Boolean.FALSE;
		}
		return success;
	}

	private JSONObject createTransientFileTickets() throws Exception {

		JSONObject createTransientFileTicketsInput = new JSONObject();
		JSONArray transientFileInfos = new JSONArray();
		for (int i = 0; i < Files.size(); i++) {
			JSONObject transientFileInfo = new JSONObject();
			transientFileInfo.put("fileName", Files.get(i).getName());
			transientFileInfo.put("isBinary", true);
			transientFileInfo.put("deleteFlag", true);
			transientFileInfos.put(transientFileInfo);
		}
		createTransientFileTicketsInput.put("transientFileInfos", transientFileInfos);

		return TcConnection.callTeamcenterService(getContext(), Constants.OPERATION_GETTRANSIENTFILETICKETSFORUPLOAD,
				createTransientFileTicketsInput, new JSONObject(), ConfigurationName);
	}

	// END EXTRA CODE
}