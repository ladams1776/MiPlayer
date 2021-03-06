package com.example.larry.miplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class MainActivityAlbumListFragmentAlbumAdapter extends
		ArrayAdapter<SongHolder> implements SectionIndexer {
	
	
	private final String[] alphabet = {"#", "A", "B", "C", 
			"D","E","F","G","H",
			"I","J","K","L","M","N","O","P","Q","R","S",
			"T","U","V","W","X","Y","Z"};
	
	
	HashMap<String, Integer> sectionsMap = new HashMap<String, Integer>();
    ArrayList<String> sectionsList = new ArrayList<String>();

    ArrayList<Integer> sectionForPosition = new ArrayList<Integer>();
    ArrayList<Integer> positionForSection = new ArrayList<Integer>();
	
	
	private int mTextViewResourceId;
	private Context mContext;
	private List<SongHolder> mList;
	private LayoutInflater mInflater;
	private ArrayList<taskConvertImage> taskPool;
	
	
	public MainActivityAlbumListFragmentAlbumAdapter(Context context,
			int textViewResourceId, List<SongHolder> objects,
			ArrayList<taskConvertImage> taskPool) {
		super(context, textViewResourceId, objects);
		this.mTextViewResourceId = textViewResourceId;
		this.mContext = context;
		this.mList = objects;
		this.taskPool = taskPool;
		
		
		
		for (int i = 0; i < objects.size(); i++) {
	            String objectString = objects.get(i).toString();
	            if (objectString.length() > 0) {
	                String firstLetter = objectString.substring(0, 1).toUpperCase();
	                if (!sectionsMap.containsKey(firstLetter)) {
	                    sectionsMap.put(firstLetter, sectionsMap.size());
	                    sectionsList.add(firstLetter);
	                }
	            }
	        }

	        // Calculate the section for each position in the list.
	        for (int i = 0; i < objects.size(); i++) {
	            String objectString = objects.get(i).toString();
	            if (objectString.length() > 0) {
	                String firstLetter = objectString.substring(0, 1).toUpperCase();
	                if (sectionsMap.containsKey(firstLetter)) {
	                    sectionForPosition.add(sectionsMap.get(firstLetter));
	                } else
	                    sectionForPosition.add(0);
	            } else
	                sectionForPosition.add(0);
	        }

	        // Calculate the first position where each section begins.
	        for (int i = 0; i < sectionsMap.size(); i++)
	            positionForSection.add(0);
	        for (int i = 0; i < sectionsMap.size(); i++) {
	            for (int j = 0; j < objects.size(); j++) {
	                Integer section = sectionForPosition.get(j);
	                if (section == i) {
	                    positionForSection.set(i, j);
	                    break;
	                }
	            }
	        }
	}




	@Override
	public SongHolder getItem(int position) {
		return  mList.get(position);

	}
	 


    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

        
		
		if (convertView == null) {
			mInflater = LayoutInflater.from(mContext);
			convertView = mInflater.inflate(mTextViewResourceId, parent, false);
			holder = new ViewHolder();
			holder.artist = (TextView) convertView.findViewById(R.id.tvArtist);
			holder.albumArt = (ImageView) convertView
					.findViewById(R.id.imageView1);
			holder.album = (TextView) convertView.findViewById(R.id.tvAlbum);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.artist.setText(getItem(position).getArtist());
		holder.album.setText(getItem(position).getAlbum());

		taskConvertImage tci = new taskConvertImage(getItem(position)
				.getAlbumImage(), holder.albumArt, holder, position, taskPool);

		taskPool.add(tci);

		tci.execute();
		return convertView;
		
		
	}

	class taskConvertImage extends AsyncTask<String, Integer, Boolean> {
		private String urlString;
		private ImageView imageView;
		private Drawable drawable;
		private int position;
		private ViewHolder holder;
		private Drawable placeHolder;
		private DownloadManager downloadManager;

		public taskConvertImage(String urlString, ImageView imageView,
				ViewHolder holder, int position,
				ArrayList<taskConvertImage> taskPool) {
			this.urlString = urlString;
			this.imageView = imageView;
			this.downloadManager = new DownloadManager();
			this.position = position;
			this.holder = holder;
			placeHolder = mContext.getResources().getDrawable(
					R.mipmap.capture);

		}

		public void viewRecycled(View v) {
			if (holder == v.getTag()) {
				cancel(true);
			}

		}

		@Override
		protected Boolean doInBackground(String... params) {
			while (true) {
				if (isCancelled())
					break;
				drawable = downloadManager.fetchDrawable(urlString);
				break;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.holder.albumArt.setImageDrawable(drawable);

		}

	}

	class DownloadManager {
		private Map<String, Drawable> drawableMap;
		private Drawable placeHolder;

		DownloadManager() {
			drawableMap = new HashMap<String, Drawable>();
			placeHolder = mContext.getResources().getDrawable(
					R.mipmap.capture);
		}

		public Drawable fetchDrawable(String urlString) {

			if (drawableMap.containsKey(urlString))
				return drawableMap.get(urlString);

			if (urlString == null)
				return placeHolder;

			Bitmap Bitmap = BitmapFactory.decodeFile(urlString);
			if(Bitmap != null){
				Bitmap bitmapResized = android.graphics.Bitmap.createScaledBitmap(Bitmap, 92, 92, false);
			
			BitmapDrawable bd = new BitmapDrawable(mContext
					.getResources(), bitmapResized);

			if (bd != null) {
				drawableMap.put(urlString, bd);
			}
			return bd;
			}
			
			return placeHolder;
		}

	}


	


	@Override
	public int getPositionForSection(int sectionIndex) {
		//return indexer.getPositionForSection(sectionIndex);
		 return positionForSection.get(sectionIndex);
	}
	

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		//return indexer.getSectionForPosition(position);
		return sectionForPosition.get(position);
	}



	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		//return indexer.getSections();
		return sectionsList.toArray();
	}


	public int evaluateWhereWeAre(int position){
		String letter = alphabet[position];
		if(sectionsMap.containsKey(letter)){
			return positionForSection.get(sectionsMap.get(letter));
		}else {
			return -2;
		}
		
	}
}
