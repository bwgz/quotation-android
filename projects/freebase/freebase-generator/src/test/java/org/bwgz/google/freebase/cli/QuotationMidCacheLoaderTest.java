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

import java.io.FileInputStream;
import java.util.Random;

import org.bwgz.google.freebase.cache.QuotationMidCacheLoader;
import org.bwgz.google.freebase.client.FreebaseHelper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class QuotationMidCacheLoaderTest {
	private static final String TAG = QuotationMidCacheLoaderTest.class.getName();

	public static void main(String[] args) {
		FileInputStream in;
		try {
			in = new FileInputStream("quotation-mid-cursors.json");
    		FreebaseHelper helper = new FreebaseHelper(TAG, args.length != 0 ? args[0] : null);
			QuotationMidCacheLoader loader = new QuotationMidCacheLoader(in, helper);
			System.out.printf("cursors size: %d\n", loader.getMaxResults());
			Random random = new Random();
			for (int i = 0; i < 10; i++) {
				System.out.println(loader.load(random.nextInt(loader.getMaxResults())));
			}
			
	    	LoadingCache<Integer, String> cache = CacheBuilder.newBuilder().build(loader);
			for (int i = 0; i < 10; i++) {
				System.out.println(cache.get(random.nextInt(loader.getMaxResults())));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
