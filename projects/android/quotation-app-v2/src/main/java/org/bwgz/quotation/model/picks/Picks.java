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

import java.util.List;

import com.google.api.client.util.Key;

public class Picks {
	@Key
	private long created;

	@Key
	private List<Pick> picks;

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
	
	public List<Pick> getPicks() {
		return picks;
	}

	public void setPicks(List<Pick> picks) {
		this.picks = picks;
	}
}
