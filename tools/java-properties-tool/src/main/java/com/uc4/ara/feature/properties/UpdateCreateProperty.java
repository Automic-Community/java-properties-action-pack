package com.uc4.ara.feature.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Properties;

import com.uc4.ara.common.exception.FileLockedException;
import com.uc4.ara.common.file.RandomAccessFileUtils;
import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.FeatureUtil.MsgTypes;
import com.uc4.ara.feature.globalcodes.ErrorCodes;
import com.uc4.ara.feature.utils.CmdLineParser;

public class UpdateCreateProperty extends AbstractPropertiesFeature {

	private CmdLineParser.Option<String> propValueArg;

	@Override
	public void initialize() {
		super.initialize();
		propValueArg = parser.addHelp(
				parser.addStringOption("v", "value", false),
				"The new value of the variable to set.");
		StringBuilder examples = new StringBuilder();
		StringBuilder description = new StringBuilder();

		description
				.append("Update/Add a variable in a properties-file.");
		examples.append("java -jar java-properties-tool.jar properties RetrieveProperty -prop C:\\properties.txt -var myname -v Test \r\n");
		examples.append("java -jar java-properties-tool.jar properties RetrieveProperty -prop C:\\properties.txt -var myname -v Test -enc UTF-16LE \r\n");

		parser.setDescription(description.toString());
		parser.setExamples(examples.toString());		
	}

	@Override
	public int run(String[] args) throws Exception {
		super.run(args);
		int returnCode = ErrorCodes.ERROR;
		String propValue = parser.getOptionValue(propValueArg);

		propValue = (propValue == null) ? "" : propValue;
		RandomAccessFile raf = null;
		try {

			validateArguments();
			
			raf = new RandomAccessFile(propFile, "rw");			

			RandomAccessFileUtils.lock(raf, 10, 500);

			Properties props = new Properties();

			loadProps(raf, props);

			props.put(propName, propValue);

			saveProps(raf, props,propName + " updated/added");

			returnCode = ErrorCodes.OK;

		} catch (FileNotFoundException e) {
			FeatureUtil.logMsg(e.getMessage(), MsgTypes.ERROR);

		} catch (FileLockedException e) {
			FeatureUtil.logMsg("Couldn't get lock for file '" + propFile + "':"
					+ e.getMessage(), MsgTypes.ERROR);
		} catch (IOException e) {
			FeatureUtil.logMsg("IO Error: " + e.getMessage(), MsgTypes.ERROR);
		} catch (UnsupportedCharsetException e) {
			FeatureUtil.logMsg(
					"No support is available for the requested charset: "
							+ e.getMessage(), MsgTypes.ERROR);
		} catch (Exception e) {
			FeatureUtil.logMsg(e.getMessage(), MsgTypes.ERROR);
		} finally {

			if (raf != null) {
				raf.close();

			}
		}

		return returnCode;
	}


}
