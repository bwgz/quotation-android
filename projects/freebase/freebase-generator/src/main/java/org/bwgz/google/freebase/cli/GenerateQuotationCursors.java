/*
 * Copyright (C) 2013 bwgz.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bwgz.google.freebase.cli;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bwgz.google.api.services.freebase.model.MqlReadResponse;
import org.bwgz.google.freebase.cache.FreebaseCursorCache.FreebaseCursor;
import org.bwgz.google.freebase.cache.QuotationMidCacheLoader.Quotation;
import org.bwgz.google.freebase.client.FreebaseHelper;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GenerateQuotationCursors {
	private static final String TAG = GenerateAuthors.class.getName();
	static private final String query = 
			"[{\"type\":\"/media_common/quotation\",\"mid\":null}]";

	private static void verbose(boolean verbose, String string) {
		if (verbose) {
			System.out.println(string);
		}
	}
	
	private static void generate(boolean verbose, String key, String file) throws IOException {
        verbose(verbose, String.format("using key: %s", key));
	    FreebaseHelper freebaseHelper = new FreebaseHelper(TAG, key);
		
    	Type type = ParameterizedTypeImpl.make(MqlReadResponse.class, new Type[] { Quotation[].class }, null);
		
        String cursor = "";
        int offset = 0;
        
        List<FreebaseCursor> cursors = new ArrayList<FreebaseCursor>();
        while (!cursor.equalsIgnoreCase("false")) {
        	MqlReadResponse<Quotation[]> response = freebaseHelper.mqlRead(query, MqlReadResponse.class, type, cursor);
	        
	        FreebaseCursor fbc = new FreebaseCursor();
	        fbc.setOffset(offset);
	        fbc.setLength(response.getResult().length);
	        fbc.setCursor(cursor);
	        cursors.add(fbc);
	        
	        offset += response.getResult().length;
	        
	        Object object = response.getCursor();
	        if (object instanceof String) {
	        	cursor = (String) object;
	        }
	        else {
	        	cursor = "false";
	        }
        }
        
		JsonFactory jsonFactory = new JacksonFactory();
		verbose(verbose, jsonFactory.toPrettyString(cursors));

		JsonGenerator generator = jsonFactory.createJsonGenerator(new FileOutputStream(file), Charset.forName("UTF-8")); 
        generator.serialize(cursors);
        generator.close();
	}
	
	public static void main(String[] args) {
	    CommandLineParser parser = new BasicParser();
	    
	    try {
	    	Options options = new Options();
	    	options.addOption(new Option("help", "print this message"));
	    	options.addOption(new Option("verbose", "output real-time status"));
	    	options.addOption(OptionBuilder.withArgName("key").hasArg().withDescription("google client api key").create("key"));
	        CommandLine line = parser.parse(options, args);
	        
	        boolean verbose = false;
	        String key = null;
	        
	        if( line.hasOption("help") ) {
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp(TAG + " [OPTIONS] file", options);
	        }
	        else {
		        if( line.hasOption("verbose") ) {
		        	verbose = true;
		        }
		        if( line.hasOption("key") ) {
		            key = line.getOptionValue("key");
		        }
		        
		        args = line.getArgs();
		        
		        if (args.length == 0) {
			        System.err.println("usage error: output file not specified");
		        }
		        else if (args.length > 1) {
			        System.err.println("usage error: only one output file may be specified");
		        }
		        else {
		        	try {
		        		generate(verbose, key, args[0]);
					}
		        	catch (IOException e) {
						e.printStackTrace();
					}
		        }
	        }
	    }
	    catch(ParseException exp) {
	        System.err.println("usage error: " + exp.getMessage());
	    }
	}
}
