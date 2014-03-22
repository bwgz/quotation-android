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
package org.bwgz.android.environment;

import java.util.ArrayList;
import java.util.List;

import org.bwgz.android.common.TwoLineItem;
import org.bwgz.android.common.TwoLineListActivity;

import android.os.Bundle;

public class NetworkInfoActivity extends TwoLineListActivity {
	private List<TwoLineItem> list = new ArrayList<TwoLineItem>();
	
	@Override
	public List<TwoLineItem> getList() {
		return list;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		list = new NetworkInfoTwoLineList(this, getIntent());

		super.onCreate(savedInstanceState);
    }
}
