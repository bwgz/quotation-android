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
package org.bwgz.quotation.search;

import java.math.BigDecimal;
import java.util.List;

import org.bwgz.quotation.model.picks.Pick;

public class SearchResults {
	private List<Pick> picks;
	private BigDecimal hits;
	
	public SearchResults(BigDecimal hits, List<Pick> picks) {
		this.hits = hits;
		this.picks = picks;
	}
	public List<Pick> getPicks() {
		return picks;
	}
	public void setPicks(List<Pick> picks) {
		this.picks = picks;
	}
	public BigDecimal getHits() {
		return hits;
	}
	public void setHits(BigDecimal hits) {
		this.hits = hits;
	}
}
