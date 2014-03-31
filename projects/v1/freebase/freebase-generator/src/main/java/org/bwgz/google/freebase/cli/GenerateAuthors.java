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
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import org.bwgz.google.freebase.client.FreebaseHelper;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Base64;
import com.google.api.services.freebase.model.TopicLookup;

public class GenerateAuthors {
	private static final String TAG = GenerateAuthors.class.getName();
	
	private static final String COMMON_TYPE_DESCRIPTION		= "/common/topic/description";
	private static final String PEOPLE_PERSON_QUOTATIONS	= "/people/person/quotations";
	private static final String TYPE_OBJECT_NAME			= "/type/object/name";
	
	private static final String[] personFilters = { TYPE_OBJECT_NAME, COMMON_TYPE_DESCRIPTION, PEOPLE_PERSON_QUOTATIONS };
	
	private static final String[] ids = {
		"/m/0jcx",		// Albert Einstein
		//"/m/081k8",		// William Shakespeare
		"/m/01bpn",		// Bertrand Russell
		"/m/082xp",		// Winston Churchill
		//"/m/0btr9",		// Yogi Berra
		//"/m/03xlm",		// Indira Gandhi
		//"/m/0h9wp",		// Laozi 
		//"/m/07g2b",		// T.S. Eliot
		"/m/014635",	// Mark Twain
		"/m/05g7q"		// Nelson Mandela
	};

	private static final String[] testIds = {
		"/m/02yy8",		// Franklin D. Roosevelt
		"/m/01rll",		// Confucius
		"/m/01t7pgk",	// Will Rogers
		"/m/04xzm",		// Mao Zedong
		"/m/02kz_",		// Ernest Hemingway
		"/m/082mw",		// W. Somerset Maugham
		"/m/05np2",		// Oscar Wilde
		"/m/0q5bb",		// Charles Spurgeon
		"/m/0431z",		// James Thurber
		"/m/01q9b9"		// Maya Angelou
	};

	private static void verbose(boolean verbose, String string) {
		if (verbose) {
			System.out.println(string);
		}
	}
	
	private static void generateAuthors(FreebaseHelper helper, String[] ids, boolean verbose, String key, String file) throws IOException {
        verbose(verbose, String.format("using key: %s", key));
		
		List<TopicLookup> persons = new ArrayList<TopicLookup>();
		for (String id : ids) {
	        verbose(verbose, String.format("topic lookup: %s", id));
			TopicLookup topic = helper.fetchTopic(id, personFilters);
			persons.add(topic);
		}
		
		JsonFactory jsonFactory = new JacksonFactory();
        verbose(verbose, jsonFactory.toPrettyString(persons));
		Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF8");
		out.write(persons.toString());
		out.close();
	}
	
	private static void generateImages(FreebaseHelper helper, String[] ids, boolean verbose, String key, String file) throws IOException {
        verbose(verbose, String.format("using key: %s", key));
	
		ArrayMap<String, String> images = new ArrayMap<String, String>();
		for (String id : ids) {
			byte[] image = helper.fetchImage(id, 200, 200);

			images.put(id, new String(Base64.encodeBase64(image)));
		}
		
		JsonFactory jsonFactory = new JacksonFactory();
        verbose(verbose, jsonFactory.toPrettyString(images));
		Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF8");
		out.write(jsonFactory.toPrettyString(images));
		out.close();
	}
	
	public static void main(String[] args) {
	    CommandLineParser parser = new BasicParser();
	    
	    try {
	    	Options options = new Options();
	    	options.addOption(new Option("help", "print this message"));
	    	options.addOption(new Option("test", "generate test file"));
	    	options.addOption(new Option("verbose", "output real-time status"));
	    	options.addOption(OptionBuilder.withArgName("key").hasArg().withDescription("google client api key").create("key"));
	        CommandLine line = parser.parse(options, args);
	        
	        boolean test = false;
	        boolean verbose = false;
	        String key = null;
	        
	        if( line.hasOption("help") ) {
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp(TAG + " [OPTIONS] authors-file images-file", options);
	        }
	        else {
		        if( line.hasOption("test") ) {
		        	test = true;
		        }
		        if( line.hasOption("verbose") ) {
		        	verbose = true;
		        }
		        if( line.hasOption("key") ) {
		            key = line.getOptionValue("key");
		        }
		        
		        args = line.getArgs();
		        
		        if (args.length < 2) {
			        System.err.println("usage error: two output files not specified");
		        }
		        else if (args.length > 2) {
			        System.err.println("usage error: only two output files may be specified");
		        }
		        else {
		    		FreebaseHelper helper = new FreebaseHelper(TAG, key);
		    		try {
						generateAuthors(helper, test ? testIds: ids, verbose, key, args[0]);
						generateImages(helper, test ? testIds: ids, verbose, key, args[1]);
					} catch (IOException e) {
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
