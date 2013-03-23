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

package org.bwgz.freebase.model;

import org.bwgz.freebase.persistence.Entity;
import org.bwgz.freebase.persistence.Id;
import org.bwgz.freebase.persistence.Property;
import org.bwgz.freebase.persistence.Type;

@Entity
public class Image extends Test {

	@Type("/common/image")
	private String type;
	@Id
	private String id;
	@Property
	private String name;

	@Property(name="image_caption")
	private String image_caption;
	@Property(name="image_location")
	private String image_location;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 

	public String getImage_caption() {
		return image_caption;
	}

	public void setImage_caption(String image_caption) {
		this.image_caption = image_caption;
	}

	public String getImage_location() {
		return image_location;
	}

	public void setImage_location(String image_location) {
		this.image_location = image_location;
	}
}
