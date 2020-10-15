package com.uc4.ara.feature.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Properties;

import com.uc4.ara.common.exception.FileLockedException;
import com.uc4.ara.common.file.RandomAccessFileUtils;
import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.FeatureUtil.MsgTypes;
import com.uc4.ara.feature.globalcodes.ErrorCodes;

public class RetrieveProperty extends AbstractPropertiesFeature {

	@Override
	public int run(String[] args) throws Exception {
		super.run(args);
		int returnCode = ErrorCodes.ERROR;

		RandomAccessFile raf = null;
		try {

			validateArguments();

			raf = new RandomAccessFile(propFile, "rw");

			RandomAccessFileUtils.lock(raf, 10, 500);

			Properties props = new Properties();

			loadProps(raf, props);

			if (propName.isEmpty()) {
				Iterator<Object> itr = props.keySet().iterator();
				String str;
				while (itr.hasNext()) {
					str = (String) itr.next();
					FeatureUtil.logMsg("Properties-Result: " + str + "="
							+ props.getProperty(str));
				}
				returnCode = ErrorCodes.OK;
			} else {

				String propValue = (String) props.get(propName);
				if (propValue != null) {
					FeatureUtil.logMsg("Properties-Result: " + propValue);
					returnCode = ErrorCodes.OK;
				} else {
					returnCode = ErrorCodes.ERROR;
					FeatureUtil.logMsg("The property '" + propName
							+ "' does not exist!");
				}

			}
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

	@Override
	public void initialize() {
		super.initialize();

		StringBuilder examples = new StringBuilder();
		StringBuilder description = new StringBuilder();

		description
				.append("Search the value of the specific variable in a properties-file.");
		examples.append("java -jar java-properties-tool.jar properties RetrieveProperty -prop C:\\properties.txt -var myname \r\n");
		examples.append("java -jar java-properties-tool.jar properties RetrieveProperty -prop C:\\properties.txt -enc UTF-16LE \r\n");

		parser.setDescription(description.toString());
		parser.setExamples(examples.toString());

	}

}
