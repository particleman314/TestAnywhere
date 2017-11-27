package com.testanywhere.core.machines.utilities;

import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.machines.actions.ActionConstants;
import com.testanywhere.core.machines.actions.ActionSequence;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.classes.managers.ErrorMsgDBManager;
import com.testanywhere.core.classes.managers.Registrar;
import com.testanywhere.core.classes.utilities.ErrorUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionUtils 
{
	public static Logger logger;

	static
	{
		ActionUtils.logger = Logger.getLogger("ActionUtils");
		LogConfiguration.configure();
	}

	public static URL convertToUrl( String fileInput, boolean local ) throws MalformedURLException
	{
		if ( local ) 
		{
			if ( ! fileInput.startsWith(ActionConstants.FILE_URL) )
				fileInput = new File(fileInput).toURI().toString();
		}
		else
		{
			Pattern p = Pattern.compile("^(\\w*\\://)");
			Matcher m = p.matcher(fileInput);
			if ( m.find(0) )
			{
				if ( ActionConstants.FILE_URL.equals(m.group(1)) )
				{
					return ActionUtils.convertToUrl(fileInput, true);
				}
				else
				{
					fileInput = fileInput.replace(m.group(1), "");
					if ( local ) return ActionUtils.convertToUrl(fileInput, true);
					fileInput = ActionConstants.HTTP_URL + fileInput;
				}
			}
		}
		return new URL(fileInput);
	}

	public static boolean setSourceConnectivity( URL src ) throws MalformedURLException
	{
		if ( src != null )
		{
			if ( ActionConstants.LOCAL_ENTITY.equals( src.getProtocol()) )
				return ActionConstants.LOCAL;
			else
				return ActionConstants.REMOTE;
		}
		else
		{
			throw new MalformedURLException();
		}
	}
	
	public static boolean setDestinationConnectivity( URL dest ) throws MalformedURLException
	{
		if ( dest != null )
		{
			if ( ActionConstants.LOCAL_ENTITY.equals( dest.getProtocol()) )
				return ActionConstants.LOCAL;
			else
				return ActionConstants.REMOTE;
		}
		else
		{
			throw new MalformedURLException();
		}
	}
	
	public static ActionSequence combineActionResults( Action... actions )
	{
		ActionSequence combinedActions = new ActionSequence();
		if ( actions.length == 0 ) return null;
		else
		{
			for (Action action : actions) {
				try {
					combinedActions.add((Action) action.copy());
				} catch (CloneNotSupportedException e) {
					action.error("Unable to clone/copy actions : " + action.toString());
					return null;
				}
			}
			return combinedActions;
		}
	}

	public static void handleActionError( Action producer, Action consumer, String errorText )
	{
		if (producer == null || consumer == null)
		{
			ActionUtils.logger.error("No actions provided which can transfer...");
			return;
		}

		ErrorMsgDBManager eDB = Registrar.getDefaultManager("ErrorMsgDBManager");

		int actionErrorCode = producer.getLastErrorValue();
		Action consumerCopy;

		try {
			consumerCopy = consumer.copy();
		} catch ( CloneNotSupportedException e ) {
			ActionUtils.logger.error("Unable to provide atomic operation for transfer of action results");
			return;
		}

		if ( actionErrorCode != ErrorUtils.SUCCESS_ID )
			producer.setFailure(actionErrorCode, "Error incurred : " + eDB.getErrorMsgFromID(actionErrorCode) + TextManager.EOL + errorText);

		if ( !producer.equals(consumer) )
		{
			try {
				producer.assignResults(consumer);
			} catch (CloneNotSupportedException e) {
				producer.error("Unable to submit results to action receiver");
				consumer.error("Unable to receiver results from action initiator");
				consumer = consumerCopy;
			}
		}
	}
}
