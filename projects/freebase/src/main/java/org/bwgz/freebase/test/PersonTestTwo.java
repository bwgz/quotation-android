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

package org.bwgz.freebase.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bwgz.freebase.model.Person;
import org.bwgz.freebase.query.MQLProperty;
import org.bwgz.freebase.query.MQLQueryBuilder;

public class PersonTestTwo {

	public static void main(String[] args) {
		Map<Class<?>, MQLProperty[]> map = new HashMap<Class<?>, MQLProperty[]>();

		MQLProperty[] directives = new MQLProperty[] {
				new MQLProperty("limit", new Integer(3))
		};
		map.put(Person.class, directives);

		MQLQueryBuilder builder = new MQLQueryBuilder(
				MQLQueryBuilder.PROPERTY_PRETTY);
		String mql = builder.createQuery(Person[].class, map);
		System.out.println(mql);
		
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("query", mql);

		HttpClient httpclient = new DefaultHttpClient();
        try {
			URI uri = new URI("https", "www.googleapis.com", "/freebase/v1/mqlread/", Util.createQuery(variables), null);
            HttpGet httpget = new HttpGet(uri);

            System.out.println("executing request " + httpget.getURI());

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        } catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
            httpclient.getConnectionManager().shutdown();
        }
	}
}