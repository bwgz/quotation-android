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
package org.bwgz.quotation.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.jakewharton.disklrucache.DiskLruCache;

public class VolleySingleton {
	static public final String TAG = VolleySingleton.class.getSimpleName();

	static private final int CACHE_SIZE = 1 * 1024 * 1024;

	private static VolleySingleton instance = null;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private class DiskLruImageCache implements ImageLoader.ImageCache {
		public static final int IO_BUFFER_SIZE = 8 * 1024;
		private CompressFormat compressFormat = CompressFormat.JPEG;
		private int compressQuality = 70;
		private DiskLruCache cache;

		public DiskLruImageCache(Context context) throws IOException {
			cache = DiskLruCache.open(new File(context.getCacheDir().getPath() + context.getPackageName()), 0, 1, CACHE_SIZE);
		}

		private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException, FileNotFoundException {
			OutputStream out = null;
			try {
				out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
				return bitmap.compress(compressFormat, compressQuality, out);
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}

		@Override
		public Bitmap getBitmap(String key) {
			Log.d(TAG, String.format("getBitmap - key: %s", key));
	        Bitmap bitmap = null;
	        DiskLruCache.Snapshot snapshot = null;
	        
	        try {
	            snapshot = cache.get(key);
	            
	            if (snapshot == null) {
	                return null;
	            }
	            InputStream in = snapshot.getInputStream(0);
	            if (in != null) {
	                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(in, IO_BUFFER_SIZE));              
	            }   
	        } catch (IOException e) {
	            Log.e(TAG, e.getLocalizedMessage());
	        } finally {
	            if (snapshot != null) {
	                snapshot.close();
	            }
	        }
	 
	        return bitmap;
		}

		@Override
		public void putBitmap(String key, Bitmap bitmap) {
			Log.d(TAG, String.format("putBitmap - key: %s  bitmap: %s (width: %d  height: %d)", key, bitmap, bitmap.getWidth(), bitmap.getHeight()));
			DiskLruCache.Editor editor = null;
			try {
				editor = cache.edit(key);
				if (editor == null) {
					return;
				}

				if (writeBitmapToFile(bitmap, editor)) {
					cache.flush();
					editor.commit();
				} else {
					editor.abort();
				}
			} catch (IOException e) {
				try {
					if (editor != null) {
						editor.abort();
					}
				} catch (IOException ignored) {
				}
			}
		}
	}

	private class LruImageCache implements ImageLoader.ImageCache {
		private LruCache<String, Bitmap> cache;

		public LruImageCache() {
			cache = new LruCache<String, Bitmap>(CACHE_SIZE);
		}
		
		@Override
		public Bitmap getBitmap(String url) {
			Log.d(TAG, String.format("getBitmap - url: %s", url));
			Bitmap bitmap = cache.get(url);
			Log.d(TAG, String.format("getBitmap - bitmap: %s", bitmap));
			return bitmap;
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			Log.d(TAG, String.format("putBitmap - url: %s  bitmap: %s (width: %d  height: %d)", url, bitmap, bitmap.getWidth(), bitmap.getHeight()));
			cache.put(url, bitmap);
		}
	}

	private class DiskTwoLevelImageCache implements ImageLoader.ImageCache {
		private LruImageCache memoryLru;
		private DiskLruImageCache diskLru;

		private String uriToKey(Uri uri) {
			return uri.getLastPathSegment() + uri.getScheme().replace("#W", "-").replace("#H", "x").replace("https", "");
		}

		private String urlToKey(String string) {
			return uriToKey(Uri.parse(string));
		}
		
		public DiskTwoLevelImageCache(Context context) {
			try {
				diskLru = new DiskLruImageCache(context);
			} catch (IOException e) {
				Log.w(TAG, String.format("Cannot create disk LRU image cache  - %s", e.getLocalizedMessage()));
			}
			
			memoryLru = new LruImageCache();
		}
		
		@Override
		public Bitmap getBitmap(String url) {
			Log.d(TAG, String.format("getBitmap - url: %s", url));
			String key = urlToKey(url);
			
			Bitmap bitmap = memoryLru.getBitmap(key);
			
			if (bitmap == null && diskLru != null) {
				bitmap = diskLru.getBitmap(key);
				
				if (bitmap != null) {
					memoryLru.putBitmap(key, bitmap);
				}
			}
			
			return bitmap;
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			Log.d(TAG, String.format("putBitmap - url: %s  bitmap: %s (width: %d  height: %d)", url, bitmap, bitmap.getWidth(), bitmap.getHeight()));
			String key = urlToKey(url);
			
			memoryLru.putBitmap(key, bitmap);
			if (diskLru != null) {
				diskLru.putBitmap(key, bitmap);
			}
		}
	}

	private VolleySingleton(final Context context) {
		requestQueue = Volley.newRequestQueue(context);
	
		imageLoader = new ImageLoader(requestQueue, new DiskTwoLevelImageCache(context));
	}

	public static VolleySingleton getInstance(Context context) {
		if (instance == null) {
			instance = new VolleySingleton(context);
		}
		return instance;
	}

	public RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}
}