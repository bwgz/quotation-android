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

import java.net.URI;
import java.net.URISyntaxException;

import org.bwgz.freebase.model.Topic;
import org.bwgz.freebase.query.MQLSingleResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class Response extends MQLSingleResultResponse<Topic> {
}

public class RestTemplateTest {

	public static void main(String[] args) {
		String query = "query={\"name\":null,\"id\":\"/en/bob_dylan\"}";

		try {
			URI uri = new URI("https", "www.googleapis.com", "/freebase/v1/mqlread/", query, null);
	
			RestTemplate restTemplate = new RestTemplate();
	
			ResponseEntity<Response> entity = restTemplate.getForEntity(uri, Response.class);
			System.out.printf("entity: %s\n", entity);
			Response response = entity.getBody();
			System.out.printf("result: %s\n", response.getResult());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
