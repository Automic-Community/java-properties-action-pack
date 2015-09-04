package com.uc4.ara.feature.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Properties;

import com.uc4.ara.common.file.RandomAccessFileUtils;
import com.uc4.ara.common.unicode.UnicodeInputStreamReader;
import com.uc4.ara.common.unicode.UnicodeOutputStreamWriter;
import com.uc4.ara.feature.AbstractPublicFeature;
import com.uc4.ara.feature.globalcodes.ErrorCodes;
import com.uc4.ara.feature.utils.CmdLineParser;

public abstract class AbstractPropertiesFeature extends AbstractPublicFeature {

	private CmdLineParser.Option<String> propFileArg;
	private CmdLineParser.Option<String> propNameArg;
	private CmdLineParser.Option<String> overwriteEncArg;

	protected String propFile;
	protected String propName;
	protected String overwriteEnc;

	@Override
	public void initialize() {
		super.initialize();
		propFileArg = parser.addHelp(
				parser.addStringOption("prop", "property", true),
				"Path of the properties-file");
		propNameArg = parser.addHelp(
				parser.addStringOption("var", "variable", false),
				"The name of the variable to create/update/delete/search");
		overwriteEncArg = parser.addHelp(
				parser.addStringOption("enc", "encoding", false),
				"The the properties file type encoding");

	}

	@Override
	public int run(String[] args) throws Exception {
		super.run(args);
		propFile = parser.getOptionValue(propFileArg);
		propName = parser.getOptionValue(propNameArg);
		overwriteEnc = parser.getOptionValue(overwriteEncArg);

		if (overwriteEnc != null) {
			Charset.forName(overwriteEnc);
		} else {
			overwriteEnc = "";
		}
		overwriteEnc = (overwriteEnc == null) ? "" : overwriteEnc;

		propName = (propName == null) ? "" : propName;

		return ErrorCodes.OK;

	}

	protected void validateArguments() throws Exception {
		
		File f  = new File(propFile);
		if (!f.isFile() ) {
			throw new FileNotFoundException("Couldn't find the file specified: " + f.getCanonicalPath() );
		}
		
		if (!overwriteEnc.isEmpty() ) {
			Charset.forName(overwriteEnc);
		}

	}
	public void loadProps(RandomAccessFile raf, Properties props)
			throws IOException {

		raf.seek(0L);
		props.load(new UnicodeInputStreamReader(raf.getFD(), overwriteEnc));

	}

	public void saveProps(RandomAccessFile raf, Properties props, String comment)
			throws IOException {

		String actualEncoding = RandomAccessFileUtils.detectFileEncoding(raf);
		boolean bom = !actualEncoding.isEmpty();

		actualEncoding = !actualEncoding.isEmpty() ? actualEncoding
				: overwriteEnc;
		// reset file length to zero
		raf.setLength(0);
		Writer writer = new UnicodeOutputStreamWriter(raf.getFD(),
				actualEncoding, bom);

		props.store(writer, comment);
		writer.flush();
	}
}
