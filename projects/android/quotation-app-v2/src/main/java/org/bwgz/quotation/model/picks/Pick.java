/*
 * Copyright (C) 2014 bwgz.org
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
package org.bwgz.quotation.model.picks;

import com.google.api.client.util.Key;

public class Pick {
	@Key
	private String id;
	
	public Pick(String id) {
		this.id = id;
	}

	public Pick() {}
	
	public String getId() {
		return id;
	}

	public void setId(String mid) {
		this.id = mid;
	}
	
	@Override
	public String toString() {
		return Pick.class.getSimpleName() + " { " + "id: " + getId() + " } ";
	}
	
	@Override
	public boolean equals(Object object) {
		boolean result = false;
		
		if (this == object) {
			result = true;
		}
		else if (object instanceof Pick) {
			result = getId().equals(((Pick) object).getId());
		}
		else {
			result = super.equals(object);
		}
		
		return result;
	}
}
